package io.github.isharedoc.api.util;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;

public class DynamoUtils {

    public static Instant instantFromAttribute(Map<String, AttributeValue> item, String attributeName) {
        return Instant.ofEpochSecond(
                Long.parseLong(
                        item.get(attributeName).n()
                )
        );
    }

    public static AttributeValue instantToAttribute(Instant date) {
        return AttributeValue.builder()
                .n(String.valueOf(date.getEpochSecond()))
                .build();
    }

}
