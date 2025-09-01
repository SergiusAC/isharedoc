package io.github.isharedoc.api.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@Order(-1)
public class IncomingRequestLogger implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.deferContextual(contextView -> Mono.just(contextView)
                .doOnNext(ctx -> {
                    String requestMethod = ctx.get(CustomMdcFields.REQUEST_METHOD);
                    String requestPath = ctx.get(CustomMdcFields.REQUEST_PATH);
                    String requestHeaders = Optional.of(exchange)
                            .map(ServerWebExchange::getRequest)
                            .map(HttpMessage::getHeaders)
                            .map(HttpHeaders::toString)
                            .orElse("");
                    log.info(
                            "IncomingRequestLogger [method={}, path={}, headers={}]",
                            requestMethod, requestPath, requestHeaders
                    );
                })
                .then(chain.filter(exchange))
        );
    }

}
