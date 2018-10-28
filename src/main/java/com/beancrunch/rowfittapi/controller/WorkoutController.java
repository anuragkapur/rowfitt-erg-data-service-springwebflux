package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.FilterCriteria;
import com.beancrunch.rowfittapi.domain.SortBy;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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

    @GetMapping("/workouts")
    public Flux<Workout> getWorkoutsForUser(@RequestAttribute(name = "userId") String allowedUserId,
            @RequestParam(name = "userId") String requestedUserId, @RequestParam(required = false) String minDate,
            @RequestParam(required = false) String maxDate, @RequestParam(required = false) String minDistance,
            @RequestParam(required = false) String maxDistance, @RequestParam(required = false) String minTimeHh,
            @RequestParam(required = false) String maxTimeHh, @RequestParam(required = false) String minTimeMm,
            @RequestParam(required = false) String maxTimeMm, @RequestParam(required = false) String sortBy) {

        if (requestedUserId.equals(allowedUserId)) {
            FilterCriteria.FilterCriteriaBuilder builder = FilterCriteria.builder();
            ofNullable(minDate).flatMap(this::getDate).map(builder::minDate);
            ofNullable(maxDate).flatMap(this::getDate).map(builder::maxDate);
            ofNullable(minDistance).map(Integer::parseInt).map(builder::minDistance);
            ofNullable(maxDistance).map(Integer::parseInt).map(builder::maxDistance);
            ofNullable(minTimeHh).map(Integer::parseInt).map(builder::minTimeHh);
            ofNullable(maxTimeHh).map(Integer::parseInt).map(builder::maxTimeHh);
            ofNullable(minTimeMm).map(Integer::parseInt).map(builder::minTimeMm);
            ofNullable(maxTimeMm).map(Integer::parseInt).map(builder::maxTimeMm);
            ofNullable(sortBy).map(SortBy::valueOf).map(builder::sortBy);
            return workoutRepository
                    .getAllWorkoutsForUser(requestedUserId, builder.build())
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

    private Optional<Date> getDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            return Optional.of(dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
