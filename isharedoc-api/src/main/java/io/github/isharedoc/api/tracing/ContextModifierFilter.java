package io.github.isharedoc.api.tracing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@Order(-2)
public class ContextModifierFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Optional<String> requestMethod = Optional.of(exchange)
                .map(ServerWebExchange::getRequest)
                .map(HttpRequest::getMethod)
                .map(HttpMethod::toString);
        Optional<String> requestPath = Optional.of(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getPath)
                .map(Objects::toString);
        Optional<String> requestId = Optional.of(exchange)
                .map(ServerWebExchange::getRequest)
                .map(ServerHttpRequest::getId);
        return chain.filter(exchange).contextWrite(context -> {
            if (requestId.isPresent()) {
                context = context.put(CustomMdcFields.REQUEST_ID, requestId.get());
            }
            if (requestPath.isPresent()) {
                context = context.put(CustomMdcFields.REQUEST_PATH, requestPath.get());
            }
            if (requestMethod.isPresent()) {
                context = context.put(CustomMdcFields.REQUEST_METHOD, requestMethod.get());
            }
            return context;
        });
    }

}
