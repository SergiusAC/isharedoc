package io.github.isharedoc.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SqsEventRequest(
        @JsonProperty("Records")
        List<SqsEventRecord> records
) {
}
