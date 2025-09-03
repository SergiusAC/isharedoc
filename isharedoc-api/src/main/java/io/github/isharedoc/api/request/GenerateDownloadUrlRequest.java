package io.github.isharedoc.api.request;

public record GenerateDownloadUrlRequest(
        String fileId,
        String protectionPassword
) {
}
