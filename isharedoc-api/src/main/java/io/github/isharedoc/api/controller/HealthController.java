package io.github.isharedoc.api.controller;

import io.github.isharedoc.api.response.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Mono<ResponseEntity<GeneralResponse<String>>> health() {
        return GeneralResponse.ok("OK");
    }

}
