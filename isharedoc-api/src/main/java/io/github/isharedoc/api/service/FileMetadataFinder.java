package io.github.isharedoc.api.service;

import io.github.isharedoc.api.config.AppProps;
import io.github.isharedoc.api.item.FileMetadataItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FileMetadataFinder {

    private final DynamoDbAsyncClient dynamoClient;
    private final AppProps appProps;

    public Mono<FileMetadataItem> findById(String fileId) {
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(appProps.awsTableName())
                .key(FileMetadataItem.newDynamoKey(fileId))
                .build();
        CompletableFuture<GetItemResponse> responseFuture = dynamoClient.getItem(getItemRequest);
        return Mono.fromFuture(responseFuture).map(getItemResponse -> {
            if (!getItemResponse.hasItem()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "file not found by id '%s' or expired".formatted(fileId)
                );
            }
            return FileMetadataItem.fromDynamo(getItemResponse.item());
        });
    }

}
