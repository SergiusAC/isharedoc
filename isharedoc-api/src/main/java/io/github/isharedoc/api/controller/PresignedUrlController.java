package io.github.isharedoc.api.controller;

import io.github.isharedoc.api.request.GenerateDownloadUrlRequest;
import io.github.isharedoc.api.request.GenerateUploadUrlRequest;
import io.github.isharedoc.api.response.FileMetadataResponse;
import io.github.isharedoc.api.response.GeneralResponse;
import io.github.isharedoc.api.response.GenerateDownloadUrlResponse;
import io.github.isharedoc.api.response.GenerateUploadUrlResponse;
import io.github.isharedoc.api.service.PresignedUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/presigned-urls")
public class PresignedUrlController {

    private final PresignedUrlService service;

    @PostMapping("/upload-url")
    public Mono<ResponseEntity<GeneralResponse<GenerateUploadUrlResponse>>> generateUploadUrl(@RequestBody @Valid GenerateUploadUrlRequest request) {
        return service.generateUploadUrl(request).flatMap(GeneralResponse::ok);
    }

    @PostMapping("/download-url")
    public Mono<ResponseEntity<GeneralResponse<GenerateDownloadUrlResponse>>> generateDownloadUrl(@RequestBody @Valid GenerateDownloadUrlRequest request) {
        return service.generateDownloadUrl(request).flatMap(GeneralResponse::ok);
    }

}
