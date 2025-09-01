package io.github.isharedoc.api.event;

import java.util.Map;

public record SqsEventRecord(
        String messageId,
        String receiptHandle,
        String body,
        Map<String, String> attributes,
        Map<String, String> messageAttributes,
        String md5OfBody,
        String eventSource,
        String eventSourceARN,
        String awsRegion
) {
}
