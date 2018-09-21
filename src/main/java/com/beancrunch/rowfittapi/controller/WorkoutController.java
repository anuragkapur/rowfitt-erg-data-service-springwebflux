package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostMapping("/workout")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity> saveWorkout(@RequestBody Workout workout) {
        System.out.println(workout.toString());
        return workoutRepository
                .save(workout)
                .map(WorkoutController::responseEntityFromWorkout);
    }

    private static ResponseEntity responseEntityFromWorkout(Workout w) {
        String workoutUriFormat = "/api/workout/%s";
        return ResponseEntity
                .created(URI.create(String.format(workoutUriFormat, w.getWorkoutId())))
                .body(w);
    }
}
