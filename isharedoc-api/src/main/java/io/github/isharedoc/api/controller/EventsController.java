package io.github.isharedoc.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isharedoc.api.event.EventRequest;
import io.github.isharedoc.api.response.GeneralResponse;
import io.github.isharedoc.api.service.EventHandler;
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

    private final EventHandler eventHandler;
    private final ObjectMapper objectMapper;

    @PostMapping
    public Mono<ResponseEntity<GeneralResponse<String>>> processEvents(@RequestBody String body) throws JsonProcessingException {
        log.info("start processEvents body={}", body);
        EventRequest eventRequest = objectMapper.readValue(body, EventRequest.class);
        return eventHandler.handle(eventRequest.records())
                .flatMap(unused -> GeneralResponse.ok("OK"));
    }

}
