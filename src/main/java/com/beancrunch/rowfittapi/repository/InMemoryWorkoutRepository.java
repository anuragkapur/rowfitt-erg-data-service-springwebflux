package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.FilterCriteria;
import com.beancrunch.rowfittapi.domain.Workout;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryWorkoutRepository implements WorkoutRepository {

    private Map<String, Workout> workoutsMap;

    public InMemoryWorkoutRepository() {
        this.workoutsMap = new HashMap<>();
    }

    @Override
    public <S extends Workout> Mono<S> save(S entity) {
        return Mono.fromCallable(() -> {
            workoutsMap.put(entity.getWorkoutId(), entity);
            return entity;
        });
    }

    @Override
    public Flux<Workout> getAllWorkoutsFor(String userId) {
        return Flux.fromStream(
                workoutsMap.values().stream().filter(w -> w.getUserId().equals(userId))
        );
    }

    @Override
    public Flux<Workout> getAllWorkoutsFor(List<String> userIds, FilterCriteria criteria) {
        return Flux.fromStream(
                workoutsMap.values().stream().filter(w -> userIds.contains(w.getUserId()))
        );
    }

    @Override
    public Flux<Workout> getAllWorkoutsFor(String userId, FilterCriteria criteria) {
        //todo: apply filter criteria
        return Flux.fromStream(
                workoutsMap.values().stream().filter(w -> w.getUserId().equals(userId))
        );
    }

    @Override
    public <S extends Workout> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends Workout> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<Workout> findById(String s) {
        return Mono.just(workoutsMap.get(s));
    }

    @Override
    public Mono<Workout> findById(Publisher<String> id) {
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
    public Flux<Workout> findAll() {
        return null;
    }

    @Override
    public Flux<Workout> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public Flux<Workout> findAllById(Publisher<String> idStream) {
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
    public Mono<Void> delete(Workout entity) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends Workout> entities) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends Workout> entityStream) {
        return null;
    }

    @Override
    public Mono<Void>deleteAll() {
        return Mono.fromCallable(() -> {
            workoutsMap.clear();
            return workoutsMap;
        }).then();
    }
}
