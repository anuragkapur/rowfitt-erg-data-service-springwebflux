package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.Workout;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface WorkoutRepository extends ReactiveCrudRepository<Workout, String> {

     Flux<Workout> getAllWorkoutsForUser(String userId);
}
