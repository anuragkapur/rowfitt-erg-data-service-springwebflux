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
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@RestController
@CrossOrigin(origins = "${corsOrigins}")
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity> saveWorkout(@RequestBody Workout workout, @RequestAttribute String userId) {
        workout.setUserId(userId);
        return workoutRepository
                .save(workout)
                .map(WorkoutController::responseEntityFromWorkout);
    }

    @GetMapping
    public Flux<Workout> getWorkoutsForUser(@RequestAttribute(name = "userId") String allowedUserId,
            @RequestParam(name = "userId") String requestedUserId, @RequestParam(required = false) String minDate,
            @RequestParam(required = false) String maxDate, @RequestParam(required = false) String minDistance,
            @RequestParam(required = false) String maxDistance, @RequestParam(required = false) String minTimeHh,
            @RequestParam(required = false) String maxTimeHh, @RequestParam(required = false) String minTimeMm,
            @RequestParam(required = false) String maxTimeMm, @RequestParam(required = false) String sortBy) {

        if (!requestedUserId.equals(allowedUserId)) {
            throw new NotAuthorisedException("Access token not authorised to get workouts for user="+requestedUserId);
        }


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
                .getAllWorkoutsFor(requestedUserId, builder.build())
                .sort(WorkoutController::compareDates);
    }

    @GetMapping("/leaderboard")
    public Flux<Workout> getLeaderboardWorkoutsForUser(@RequestAttribute(name = "userId") String allowedUserId,
            @RequestParam(name = "userId") String requestedUserId, @RequestParam(required = false) String minDate,
            @RequestParam(required = false) String maxDate, @RequestParam(required = false) String minDistance,
            @RequestParam(required = false) String maxDistance, @RequestParam(required = false) String minTimeHh,
            @RequestParam(required = false) String maxTimeHh, @RequestParam(required = false) String minTimeMm,
            @RequestParam(required = false) String maxTimeMm) {

        if (!requestedUserId.equals(allowedUserId)) {
            throw new NotAuthorisedException("Access token not authorised to get workouts for user="+requestedUserId);
        }

        FilterCriteria.FilterCriteriaBuilder criteria = FilterCriteria.builder();
        ofNullable(minDate).flatMap(this::getDate).map(criteria::minDate);
        ofNullable(maxDate).flatMap(this::getDate).map(criteria::maxDate);
        ofNullable(minDistance).map(Integer::parseInt).map(criteria::minDistance);
        ofNullable(maxDistance).map(Integer::parseInt).map(criteria::maxDistance);
        ofNullable(minTimeHh).map(Integer::parseInt).map(criteria::minTimeHh);
        ofNullable(maxTimeHh).map(Integer::parseInt).map(criteria::maxTimeHh);
        ofNullable(minTimeMm).map(Integer::parseInt).map(criteria::minTimeMm);
        ofNullable(maxTimeMm).map(Integer::parseInt).map(criteria::maxTimeMm);

        Flux<Workout> userWorkout = workoutRepository
                .getAllWorkoutsFor(requestedUserId, criteria.build())
                .sort(WorkoutController::compareSplits)
                .take(1);

        Flux<Workout> networkWorkouts = getUsersNetwork(requestedUserId)
                    .collectList()
                    .map(userIds -> workoutRepository.getAllWorkoutsFor(userIds, criteria.build()))
                    .flux()
                    .flatMap(Function.identity())
                    .sort(WorkoutController::compareSplits)
                    .take(3);

        return Flux.merge(networkWorkouts, userWorkout).sort(WorkoutController::compareSplits).take(3);
    }

    private Flux<String> getUsersNetwork(String requestedUserId) {
        return Mono.just(requestedUserId).flux();
    }

    private static ResponseEntity responseEntityFromWorkout(Workout w) {
        String workoutUriFormat = "/api/workouts/%s";
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

    private static int compareSplits(Workout w1, Workout w2) {
        float w1SplitInSeconds = w1.getSplitMm()*60+w1.getSplitSss();
        float w2SplitInSeconds = w2.getSplitMm()*60+w2.getSplitSss();

        if (w1SplitInSeconds < w2SplitInSeconds) {
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
