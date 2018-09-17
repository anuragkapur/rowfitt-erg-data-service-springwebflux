package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.Workout;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryRepository implements WorkoutRepository {

    private List<Workout> workoutList;

    public InMemoryRepository() {
        this.workoutList = new ArrayList<>();
    }

    public void addWorkout(Workout workout) {
        workoutList.add(workout);
    }
}
