package io.github.isharedoc.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @PostMapping
    public Mono<ResponseEntity<GeneralResponse<String>>> processEvents(@RequestBody String body) throws JsonProcessingException {
        log.info("processEvents body={}", body);
        SqsEventRequest sqsEventRequest = objectMapper.readValue(body, SqsEventRequest.class);
        return sqsEventHandler.handle(sqsEventRequest.records())
                .flatMap(unused -> GeneralResponse.ok("OK"));
    }

}
