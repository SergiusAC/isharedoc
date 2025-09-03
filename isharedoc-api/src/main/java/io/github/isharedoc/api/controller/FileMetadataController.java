package io.github.isharedoc.api.controller;

import io.github.isharedoc.api.response.FileMetadataResponse;
import io.github.isharedoc.api.response.GeneralResponse;
import io.github.isharedoc.api.service.FileMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file-metadata")
public class FileMetadataController {

    private final FileMetadataService service;

    @GetMapping
    public Mono<ResponseEntity<GeneralResponse<FileMetadataResponse>>> getFileMetadata(
            @RequestParam String fileId, @RequestParam String protectionPassword
    ) {
        return service.getByIdAndValidatePassword(fileId, protectionPassword)
                .map(FileMetadataResponse::from)
                .flatMap(GeneralResponse::ok);
    }

}
