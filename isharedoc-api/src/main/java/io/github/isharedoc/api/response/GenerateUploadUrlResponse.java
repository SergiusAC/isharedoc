package io.github.isharedoc.api.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record GenerateUploadUrlResponse(
        String fileId,
        String uploadUrl,
        Map<String, String> sseHeaders
) {
}
