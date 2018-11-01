package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.ShareRequest;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ShareRequestsRepositoryImpl implements ShareRequestsRepository {

    @Override
    public <S extends ShareRequest> Mono<S> save(S entity) {
        return null;
    }

    @Override
    public <S extends ShareRequest> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends ShareRequest> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<ShareRequest> findById(String s) {
        return null;
    }

    @Override
    public Mono<ShareRequest> findById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(String s) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> id) {
        return null;
    }

    @Override
    public Flux<ShareRequest> findAll() {
        return null;
    }

    @Override
    public Flux<ShareRequest> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public Flux<ShareRequest> findAllById(Publisher<String> idStream) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return null;
    }

    @Override
    public Mono<Void> deleteById(String s) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Void> delete(ShareRequest entity) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends ShareRequest> entities) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends ShareRequest> entityStream) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }
}
