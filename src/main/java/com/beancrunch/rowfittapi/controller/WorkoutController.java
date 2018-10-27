package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.filter.NotAuthorisedException;
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
    public Mono<ResponseEntity> saveWorkout(@RequestBody Workout workout, @RequestAttribute String userId) {
        workout.setUserId(userId);
        return workoutRepository
                .save(workout)
                .map(WorkoutController::responseEntityFromWorkout);
    }

    @GetMapping("/workouts")
    public Flux<Workout> getWorkoutsForUser(@RequestParam(name = "userId") String requestedUserId,
                                            @RequestAttribute(name = "userId") String allowedUserId) {
        if (requestedUserId.equals(allowedUserId)) {
            return workoutRepository
                    .getAllWorkoutsForUser(requestedUserId)
                    .sort(WorkoutController::compareDates);
        } else {
            throw new NotAuthorisedException("Access token not authorised to get workouts for user="+requestedUserId);
        }
    }

    private static ResponseEntity responseEntityFromWorkout(Workout w) {
        String workoutUriFormat = "/api/workout/%s";
        return ResponseEntity
                .created(URI.create(String.format(workoutUriFormat, w.getWorkoutId())))
                .body(w);
    }

    private static int compareDates(Workout w1, Workout w2) {
        if (w1.getDate().before(w2.getDate())) {
            return -1;
        } else {
            return 1;
        }
    }
}
