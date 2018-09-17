package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostMapping("/workout")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveWorkout(@RequestBody Workout workout) {
        System.out.println(workout.toString());
        workoutRepository.addWorkout(workout);
    }
}
