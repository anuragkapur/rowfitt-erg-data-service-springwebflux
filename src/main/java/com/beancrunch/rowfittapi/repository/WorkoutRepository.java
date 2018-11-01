package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.FilterCriteria;
import com.beancrunch.rowfittapi.domain.Workout;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface WorkoutRepository extends ReactiveCrudRepository<Workout, String> {

     Flux<Workout> getAllWorkoutsFor(String userId);

     Flux<Workout> getAllWorkoutsFor(List<String> userIds, FilterCriteria criteria);

     Flux<Workout> getAllWorkoutsFor(String requestedUserId, FilterCriteria criteria);
}
