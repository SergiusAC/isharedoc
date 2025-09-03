package io.github.isharedoc.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isharedoc.api.config.AppProps;
import io.github.isharedoc.api.event.DeleteFileMetadataEvent;
import io.github.isharedoc.api.event.DynamoStreamEventRecord;
import io.github.isharedoc.api.event.EventSourceRecord;
import io.github.isharedoc.api.event.SqsEventRecord;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHandler {

    private final ObjectMapper objectMapper;
    private final DynamoDbAsyncClient dynamoClient;
    private final S3AsyncClient s3Client;
    private final AppProps appProps;
    private final FileMetadataService fileMetadataService;

    public Mono<Void> handle(List<JsonNode> records) {
        return Flux.fromStream(records.stream())
                .flatMap(jsonNode -> {
                    try {
                        EventSourceRecord eventSourceRecord = objectMapper.treeToValue(
                                jsonNode, EventSourceRecord.class
                        );
                        if ("aws:sqs".equals(eventSourceRecord.eventSource())) {
                            SqsEventRecord sqsEventRecord = objectMapper.treeToValue(
                                    jsonNode, SqsEventRecord.class
                            );
                            return this.handleSqsRecord(sqsEventRecord);
                        }
                        if ("aws:dynamodb".equals(eventSourceRecord.eventSource())) {
                            DynamoStreamEventRecord dynamoStreamEventRecord = objectMapper.treeToValue(
                                    jsonNode, DynamoStreamEventRecord.class
                            );
                            return this.handleDynamoStreamRecord(dynamoStreamEventRecord);
                        }
                        return Mono.empty();
                    } catch (JsonProcessingException ex) {
                        throw new IllegalStateException(
                                "failed to deserialize record=%s".formatted(jsonNode.toString()), ex
                        );
                    }
                })
                .then();
    }

    private Mono<Void> handleSqsRecord(SqsEventRecord eventRecord) {
        try {
            String messageBody = eventRecord.body();
            DeleteFileMetadataEvent deleteFileMetadataEvent = objectMapper.readValue(
                    messageBody, DeleteFileMetadataEvent.class
            );
            String fileId = deleteFileMetadataEvent.fileId();
            return fileMetadataService.getById(fileId)
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
                    }).then();
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "failed to deserialize sqs message body=%s".formatted(eventRecord.body()), ex
            );
        }
    }

    private Mono<Void> handleDynamoStreamRecord(DynamoStreamEventRecord eventRecord) {
        if (!"REMOVE".equals(eventRecord.eventName())) {
            return Mono.empty();
        }
        Optional<DeleteObjectRequest> deleteObjectRequestOptional = Optional.of(eventRecord)
                .map(DynamoStreamEventRecord::dynamodb)
                .map(DynamoStreamEventRecord.EventData::oldImage)
                .filter(oldImageAttrs ->
                        oldImageAttrs.containsKey("file_key") && oldImageAttrs.containsKey("bucket_name"))
                .flatMap(oldImageAttrs -> {
                    JsonNode fileKeyNode = oldImageAttrs.get("file_key");
                    JsonNode bucketNameNode = oldImageAttrs.get("bucket_name");
                    String fileKey = fileKeyNode.get("S").asText("");
                    String bucketName = bucketNameNode.get("S").asText("");
                    if (fileKey.isBlank() || bucketName.isBlank()) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            DeleteObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(fileKey)
                                    .build()
                    );
                });
        if (deleteObjectRequestOptional.isPresent()) {
            CompletableFuture<DeleteObjectResponse> deleteFuture = s3Client.deleteObject(
                    deleteObjectRequestOptional.get()
            );
            return Mono.fromFuture(deleteFuture).then();
        } else {
            return Mono.empty();
        }
    }

}
