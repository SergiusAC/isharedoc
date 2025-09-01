package io.github.isharedoc.api.item;

import io.github.isharedoc.api.util.DynamoUtils;
import lombok.Builder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;

@Builder
public record FileMetadataItem(
        String fileId,
        String fileKey,
        String bucketName,
        String filename,
        String salt,
        String sseCustomerKeyMD5,
        Instant createdAt,
        Instant expiresAt
) {

    public static FileMetadataItem fromDynamo(Map<String, AttributeValue> dynamoItem) {
        return FileMetadataItem.builder()
                .fileId(dynamoItem.get("file_id").s())
                .fileKey(dynamoItem.get("file_key").s())
                .bucketName(dynamoItem.get("bucket_name").s())
                .filename(dynamoItem.get("filename").s())
                .salt(dynamoItem.get("salt").s())
                .sseCustomerKeyMD5(dynamoItem.get("sse_customer_key_md5").s())
                .createdAt(
                        DynamoUtils.instantFromAttribute(dynamoItem, "created_at")
                )
                .expiresAt(
                        DynamoUtils.instantFromAttribute(dynamoItem, "expires_at")
                )
                .build();
    }

    public Map<String, AttributeValue> toDynamo() {
        return Map.of(
                "file_id", AttributeValue.builder().s(this.fileId).build(),
                "file_key", AttributeValue.builder().s(this.fileKey).build(),
                "bucket_name", AttributeValue.builder().s(this.bucketName).build(),
                "filename", AttributeValue.builder().s(this.filename).build(),
                "salt", AttributeValue.builder().s(this.salt).build(),
                "sse_customer_key_md5", AttributeValue.builder().s(this.sseCustomerKeyMD5).build(),
                "created_at", DynamoUtils.instantToAttribute(this.createdAt),
                "expires_at", DynamoUtils.instantToAttribute(this.expiresAt)
        );
    }

    public static Map<String, AttributeValue> newDynamoKey(String fileId) {
        return Map.of("file_id", AttributeValue.builder().s(fileId).build());
    }

    public Map<String, AttributeValue> getDynamoKey() {
        return FileMetadataItem.newDynamoKey(this.fileId);
    }

}
