package io.github.isharedoc.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GenerateUploadUrlRequest(
        @NotBlank
        String filename,

        @NotBlank
        String protectionPassword,

        @Min(1)
        @Max(3600)
        @NotNull
        Integer expiresInSeconds
) {
}
