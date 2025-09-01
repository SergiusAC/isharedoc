package io.github.isharedoc.api.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EventRequest(
        @JsonProperty("Records")
        List<String> records
) {
}
