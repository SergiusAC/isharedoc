package io.github.isharedoc.api.response;

import io.github.isharedoc.api.tracing.CustomMdcFields;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Builder
public record GeneralResponse<T>(
        Instant timestamp,
        int status,
        String requestId,
        ErrorInfo errorInfo,
        T data
) {

    @Builder
    public record ErrorInfo(
            String method,
            String path,
            String message
    ) {}

    public static <T> Mono<ResponseEntity<GeneralResponse<T>>> ok(T data) {
        return Mono.deferContextual(contextView ->
                Mono.just(
                        ResponseEntity.ok(
                                GeneralResponse.<T>builder()
                                        .timestamp(Instant.now())
                                        .status(200)
                                        .requestId(contextView.get(CustomMdcFields.REQUEST_ID))
                                        .data(data)
                                        .build()
                        )
                )
        );
    }

    public static Mono<GeneralResponse<String>> error(HttpStatus httpStatus, String errorMessage) {
        return Mono.deferContextual(contextView -> Mono.just(
                GeneralResponse.<String>builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .requestId(contextView.get(CustomMdcFields.REQUEST_ID))
                        .errorInfo(
                                ErrorInfo.builder()
                                        .method(contextView.get(CustomMdcFields.REQUEST_METHOD))
                                        .path(contextView.get(CustomMdcFields.REQUEST_PATH))
                                        .message(errorMessage)
                                        .build()
                        )
                        .data(httpStatus.getReasonPhrase())
                        .build()
        ));
    }

    public static Mono<GeneralResponse<String>> internalError(String errorMessage) {
        return GeneralResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    public static Mono<GeneralResponse<String>> badRequest(String errorMessage) {
        return GeneralResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    public static Mono<GeneralResponse<String>> notFound(String errorMessage) {
        return GeneralResponse.error(HttpStatus.NOT_FOUND, errorMessage);
    }

}
