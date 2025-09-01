package io.github.isharedoc.api.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record EventRequest(
        @JsonProperty("Records")
        List<String> records
) implements Serializable {
}
