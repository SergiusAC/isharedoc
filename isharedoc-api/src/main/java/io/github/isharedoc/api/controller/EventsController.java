package io.github.isharedoc.api.controller;

import io.github.isharedoc.api.request.SqsEventRequest;
import io.github.isharedoc.api.response.GeneralResponse;
import io.github.isharedoc.api.service.SqsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {

    private final SqsEventHandler sqsEventHandler;

    @PostMapping
    public Mono<ResponseEntity<GeneralResponse<String>>> processEvents(@RequestBody SqsEventRequest body) {
        return sqsEventHandler.handle(body.records())
                .flatMap(unused -> GeneralResponse.ok("OK"));
    }

}
