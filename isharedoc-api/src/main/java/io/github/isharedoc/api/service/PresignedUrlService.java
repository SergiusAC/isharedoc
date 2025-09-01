package io.github.isharedoc.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isharedoc.api.config.AppProps;
import io.github.isharedoc.api.item.FileMetadataItem;
import io.github.isharedoc.api.event.DeleteFileMetadataEvent;
import io.github.isharedoc.api.request.GenerateDownloadUrlRequest;
import io.github.isharedoc.api.request.GenerateUploadUrlRequest;
import io.github.isharedoc.api.response.FileMetadataResponse;
import io.github.isharedoc.api.response.GenerateDownloadUrlResponse;
import io.github.isharedoc.api.response.GenerateUploadUrlResponse;
import io.github.isharedoc.api.util.CryptoUtils;
import io.github.isharedoc.api.util.CustomRandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private final S3Presigner s3Presigner;
    private final DynamoDbAsyncClient dynamoClient;
    private final SqsAsyncClient sqsClient;
    private final AppProps appProps;
    private final ObjectMapper objectMapper;

    private final FileMetadataFinder fileMetadataFinder;

    private static final String SSE_ALGORITHM = "AES256";
    private static final int AES256_ITERATIONS = 100_000;
    private static final int SALT_LENGTH = 16;
    private static final int FILE_ID_LENGTH = 12;
    private static final int SIGNATURE_DURATION_IN_MINUTES = 10;

    public Mono<GenerateUploadUrlResponse> generateUploadUrl(GenerateUploadUrlRequest request) {
        byte[] salt = CryptoUtils.generateSalt(SALT_LENGTH);
        byte[] sseKeyBytes = CryptoUtils.deriveAES256Key(request.secretKey(), salt, AES256_ITERATIONS).getEncoded();
        String sseCustomerKeyB64 = CryptoUtils.b64(sseKeyBytes);
        String sseCustomerKeyMd5B64 = CryptoUtils.md5b64(sseKeyBytes);

        String fileId = CustomRandomUtils.randomNumericString(FILE_ID_LENGTH);
        String fileKey = this.fileKey(request.filename());
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(appProps.awsBucketName())
                        .key(fileKey)
                        .sseCustomerAlgorithm(SSE_ALGORITHM)
                        .sseCustomerKey(sseCustomerKeyB64)
                        .sseCustomerKeyMD5(sseCustomerKeyMd5B64)
                        .build())
                .signatureDuration(Duration.of(SIGNATURE_DURATION_IN_MINUTES, ChronoUnit.MINUTES))
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        String uploadUrl = presignedPutObjectRequest.url().toString();

        FileMetadataItem fileMetadataItem = FileMetadataItem.builder()
                .fileId(fileId)
                .fileKey(fileKey)
                .bucketName(appProps.awsBucketName())
                .filename(request.filename())
                .salt(CryptoUtils.b64(salt))
                .sseCustomerKeyMD5(sseCustomerKeyMd5B64)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(request.expiresInSeconds()))
                .build();

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(appProps.awsTableName())
                .item(fileMetadataItem.toDynamo())
                .build();
        CompletableFuture<PutItemResponse> putFuture = dynamoClient.putItem(putItemRequest);

        return Mono.fromFuture(putFuture).thenReturn(
                GenerateUploadUrlResponse.builder()
                        .fileId(fileId)
                        .uploadUrl(uploadUrl)
                        .sseHeaders(this.sseHeaders(sseCustomerKeyB64, sseCustomerKeyMd5B64))
                        .build()
        );
    }

    public Mono<GenerateDownloadUrlResponse> generateDownloadUrl(GenerateDownloadUrlRequest request) {
        Mono<FileMetadataItem> fileMetadataItemMono = fileMetadataFinder.findById(request.fileId());
        return fileMetadataItemMono
                .flatMap(fileMetadataItem -> this.checkFileExpiration(fileMetadataItem).thenReturn(fileMetadataItem))
                .flatMap(fileMetadataItem -> {
                    String fileId = fileMetadataItem.fileId();
                    String fileKey = fileMetadataItem.fileKey();
                    String saltB64 = fileMetadataItem.salt();
                    byte[] salt = CryptoUtils.fromB64(saltB64);
                    byte[] sseKeyBytes = CryptoUtils.deriveAES256Key(request.secretKey(), salt, AES256_ITERATIONS).getEncoded();
                    String sseCustomerKeyB64 = CryptoUtils.b64(sseKeyBytes);
                    String sseCustomerKeyMd5B64 = CryptoUtils.md5b64(sseKeyBytes);

                    if (!fileMetadataItem.sseCustomerKeyMD5().equals(sseCustomerKeyMd5B64)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong secret key");
                    }

                    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                            .getObjectRequest(GetObjectRequest.builder()
                                    .bucket(appProps.awsBucketName())
                                    .key(fileKey)
                                    .sseCustomerAlgorithm(SSE_ALGORITHM)
                                    .sseCustomerKey(sseCustomerKeyB64)
                                    .sseCustomerKeyMD5(sseCustomerKeyMd5B64)
                                    .build())
                            .signatureDuration(Duration.of(SIGNATURE_DURATION_IN_MINUTES, ChronoUnit.MINUTES))
                            .build();
                    PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
                    return Mono.just(GenerateDownloadUrlResponse.builder()
                            .fileId(fileId)
                            .downloadUrl(presignedGetObjectRequest.url().toString())
                            .sseHeaders(this.sseHeaders(sseCustomerKeyB64, sseCustomerKeyMd5B64))
                            .build());
                });
    }

    public Mono<FileMetadataResponse> getFileMetadata(String fileId) {
        Mono<FileMetadataItem> fileMetadata = fileMetadataFinder.findById(fileId);
        return fileMetadata.map(FileMetadataResponse::from);
    }

    private String fileKey(String filename) {
        return "uploaded_files/%s/%s".formatted(UUID.randomUUID(), filename);
    }

    private Mono<Void> checkFileExpiration(FileMetadataItem item) {
        if (Instant.now().isBefore(item.expiresAt())) {
            return Mono.empty();
        }
        return Mono.defer(() -> this.sendExpirationFileMessage(item.fileId()))
                .then(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found or expired")
                ));
    }

    private Mono<Void> sendExpirationFileMessage(String fileId) {
        try {
            CompletableFuture<SendMessageResponse> future = sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(appProps.awsSqsUrl())
                            .messageBody(
                                    objectMapper.writeValueAsString(new DeleteFileMetadataEvent(fileId))
                            )
                            .build()
            );
            return Mono.fromFuture(future).then();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, String> sseHeaders(String sseCustomerKeyB64, String sseCustomerKeyMd5B64) {
        return Map.of(
                "x-amz-server-side-encryption-customer-algorithm", SSE_ALGORITHM,
                "x-amz-server-side-encryption-customer-key", sseCustomerKeyB64,
                "x-amz-server-side-encryption-customer-key-MD5", sseCustomerKeyMd5B64
        );
    }

}
