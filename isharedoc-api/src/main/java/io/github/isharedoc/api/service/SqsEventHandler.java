package io.github.isharedoc.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isharedoc.api.config.AppProps;
import io.github.isharedoc.api.event.DeleteFileMetadataEvent;
import io.github.isharedoc.api.request.SqsEventRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsEventHandler {

    private final ObjectMapper objectMapper;
    private final DynamoDbAsyncClient dynamoClient;
    private final S3AsyncClient s3Client;
    private final AppProps appProps;
    private final FileMetadataFinder fileMetadataFinder;

    public Mono<Void> handle(List<SqsEventRecord> records) {
        return Flux.fromStream(records.stream())
                .flatMap(record -> {
                    try {
                        String messageBody = record.body();
                        DeleteFileMetadataEvent deleteFileMetadataEvent = objectMapper.readValue(
                                messageBody, DeleteFileMetadataEvent.class
                        );
                        String fileId = deleteFileMetadataEvent.fileId();
                        return fileMetadataFinder.findById(fileId);
                    } catch (JsonProcessingException ex) {
                        log.error("failed to deserialize message body: {}", record.body(), ex);
                        throw new IllegalStateException(ex);
                    }
                })
                .flatMap(fileMetadataItem -> {
                    DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                            .tableName(appProps.awsTableName())
                            .key(fileMetadataItem.getDynamoKey())
                            .build();
                    CompletableFuture<DeleteItemResponse> deleteResponseFuture = dynamoClient.deleteItem(deleteItemRequest);
                    return Mono.fromFuture(deleteResponseFuture).map(unused -> fileMetadataItem);
                })
                .flatMap(fileMetadataItem -> {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(appProps.awsBucketName())
                            .key(fileMetadataItem.fileKey())
                            .build();
                    CompletableFuture<DeleteObjectResponse> deleteFuture = s3Client.deleteObject(deleteObjectRequest);
                    return Mono.fromFuture(deleteFuture);
                })
                .then();
    }

}
