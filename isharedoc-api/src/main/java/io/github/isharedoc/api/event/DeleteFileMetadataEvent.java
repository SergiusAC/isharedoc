package io.github.isharedoc.api.event;

import java.io.Serializable;

public record DeleteFileMetadataEvent(String fileId) implements Serializable {
}
