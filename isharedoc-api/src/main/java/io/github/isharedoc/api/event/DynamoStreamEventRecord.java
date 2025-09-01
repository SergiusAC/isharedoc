package io.github.isharedoc.api.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.Serializable;
import java.util.Map;

public record DynamoStreamEventRecord(
        String eventID,
        String eventName,
        String eventSource,
        DynamoEventData dynamodb
) implements Serializable {

    public record DynamoEventData(
            @JsonProperty("Keys")
            Map<String, AttributeValue> keys,
            @JsonProperty("OldImage")
            Map<String, AttributeValue> oldImage
    ) implements Serializable {
    }

}
