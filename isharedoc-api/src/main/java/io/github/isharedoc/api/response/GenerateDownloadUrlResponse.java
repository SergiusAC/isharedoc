package io.github.isharedoc.api.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record GenerateDownloadUrlResponse(
        String fileId,
        String downloadUrl,
        Map<String, String> sseHeaders
) {
}
