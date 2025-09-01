package io.github.isharedoc.api.error_handler;

import io.github.isharedoc.api.response.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<GeneralResponse<String>>> handleException(Exception exception) {
        log.error("handleException message={}", exception.getMessage(), exception);
        return GeneralResponse.internalError(exception.getMessage())
                .map(stringGeneralResponse -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(stringGeneralResponse)
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<GeneralResponse<String>>> handleResponseStatusException(ResponseStatusException exception) {
        log.error("handleResponseStatusException message={}", exception.getMessage(), exception);
        return GeneralResponse.error(
                    HttpStatus.valueOf(exception.getStatusCode().value()),
                    exception.getMessage()
        ).map(generalResponse ->
                ResponseEntity
                        .status(exception.getStatusCode())
                        .body(generalResponse)
        );
    }

}
