package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.FilterCriteria;
import com.beancrunch.rowfittapi.domain.Workout;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface WorkoutRepository extends ReactiveCrudRepository<Workout, String> {

     Flux<Workout> getAllWorkoutsForUser(String userId);

     Flux<Workout> getAllWorkoutsForUsers(List<String> userIds);

     Flux<Workout> getAllWorkoutsForUser(String requestedUserId, FilterCriteria build);
}
