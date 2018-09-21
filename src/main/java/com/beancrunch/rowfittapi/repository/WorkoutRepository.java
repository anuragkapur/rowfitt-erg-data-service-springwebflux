package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.Workout;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface WorkoutRepository extends ReactiveCrudRepository<Workout, String> {

}
