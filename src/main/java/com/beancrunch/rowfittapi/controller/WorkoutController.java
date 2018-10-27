package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@CrossOrigin(origins = "${corsOrigins}")
@RequestMapping("/api")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostMapping("/workout")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity> saveWorkout(@RequestBody Workout workout, @RequestAttribute(required = false) String userId) {
        workout.setUserId(userId);
        return workoutRepository
                .save(workout)
                .map(WorkoutController::responseEntityFromWorkout);
    }

    @GetMapping("/workouts")
    public Flux<Workout> getWorkoutsForUser(@RequestParam String userId) {
        System.out.println(userId);
        return workoutRepository.getAllWorkoutsForUser(userId);

    }

    private static ResponseEntity responseEntityFromWorkout(Workout w) {
        String workoutUriFormat = "/api/workout/%s";
        return ResponseEntity
                .created(URI.create(String.format(workoutUriFormat, w.getWorkoutId())))
                .body(w);
    }
}
