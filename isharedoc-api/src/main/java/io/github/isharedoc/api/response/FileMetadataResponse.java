package io.github.isharedoc.api.response;

import io.github.isharedoc.api.item.FileMetadataItem;
import lombok.Builder;

@Builder
public record FileMetadataResponse(
        String fileId,
        String filename
) {

    public static FileMetadataResponse from(FileMetadataItem item) {
        return FileMetadataResponse.builder()
                .fileId(item.fileId())
                .filename(item.filename())
                .build();
    }

}
