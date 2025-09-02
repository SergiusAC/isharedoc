package io.github.isharedoc.api.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.io.Serializable;
import java.util.Map;

@RegisterReflectionForBinding(DynamoStreamEventRecord.class)
public record DynamoStreamEventRecord(
        String eventID,
        String eventName,
        String eventSource,
        EventData dynamodb
) implements Serializable {

    @RegisterReflectionForBinding(EventData.class)
    public record EventData(
            @JsonProperty("Keys")
            Map<String, JsonNode> keys,
            @JsonProperty("OldImage")
            Map<String, JsonNode> oldImage
    ) implements Serializable {
    }

}
