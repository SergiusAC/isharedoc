package io.github.isharedoc.api.event;

import java.io.Serializable;

public record EventSourceRecord(
        String eventSource
) implements Serializable {
}
