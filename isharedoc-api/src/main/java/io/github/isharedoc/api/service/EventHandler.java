package io.github.isharedoc.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
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
public class EventHandler {

    private final ObjectMapper objectMapper;
    private final DynamoDbAsyncClient dynamoClient;
    private final S3AsyncClient s3Client;
    private final AppProps appProps;
    private final FileMetadataFinder fileMetadataFinder;

    public Mono<Void> handle(List<String> records) {
        return Flux.fromStream(records.stream())
                .flatMap(record -> {
                    try {
                        EventSourceRecord eventSourceRecord = objectMapper.readValue(
                                record, EventSourceRecord.class
                        );
                        if ("aws.sqs".equals(eventSourceRecord.eventSource())) {
                            SqsEventRecord sqsEventRecord = objectMapper.readValue(
                                    record, SqsEventRecord.class
                            );
                            return this.handleSqsRecord(sqsEventRecord);
                        }
                        if ("aws:dynamodb".equals(eventSourceRecord.eventSource())) {
                            DynamoStreamEventRecord dynamoStreamEventRecord = objectMapper.readValue(
                                    record, DynamoStreamEventRecord.class
                            );
                            return this.handleDynamoStreamRecord(dynamoStreamEventRecord);
                        }
                        return Mono.empty();
                    } catch (JsonProcessingException ex) {
                        throw new IllegalStateException("failed to deserialize event: %s".formatted(record), ex);
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
            return fileMetadataFinder.findById(fileId)
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
        AttributeValue fileKey = eventRecord.dynamodb().oldImage().get("file_key");
        AttributeValue bucketName = eventRecord.dynamodb().oldImage().get("bucket_name");
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName.s())
                .key(fileKey.s())
                .build();
        CompletableFuture<DeleteObjectResponse> deleteFuture = s3Client.deleteObject(deleteObjectRequest);
        return Mono.fromFuture(deleteFuture).then();
    }

}
