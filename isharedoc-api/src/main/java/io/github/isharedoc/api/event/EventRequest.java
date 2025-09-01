package io.github.isharedoc.api.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.List;

public record EventRequest(
        @JsonProperty("Records")
        List<JsonNode> records
) implements Serializable {
}
