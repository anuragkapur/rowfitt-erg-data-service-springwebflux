package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.filter.NotAuthorisedException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        return Mono.create(s -> {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            if (throwable instanceof ExpiredJwtException || throwable instanceof NotAuthorisedException) {
                serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            }
            throwable.printStackTrace();
            s.success();
        });
    }
}
