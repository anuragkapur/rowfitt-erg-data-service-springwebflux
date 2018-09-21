package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.repository.InMemoryRepository;
import com.beancrunch.rowfittapi.repository.WorkoutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;

@RunWith(SpringRunner.class)
@WebFluxTest(WorkoutController.class)
@Import(InMemoryRepository.class)
public class WorkoutControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WorkoutRepository workoutRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void saveWorkoutEndpointShouldSaveWorkoutToRepo() throws IOException {

        String requestBody = "{\"date\":\"29/07/2018\",\"timeHh\":\"0\",\"timeMm\":\"19\",\"timeSss\":\"30.0\"," +
                "\"splitMm\":\"1\",\"splitSss\":\"57.0\",\"strokeRate\":\"23\",\"heartRate\":\"163\"}";

        String expectedLocationHeaderRegex = "^.*/api/workout/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        EntityExchangeResult<Workout> savedResult = this.webTestClient
                .post()
                .uri("/api/workout")
                .body(BodyInserters.fromObject(requestBody))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", expectedLocationHeaderRegex)
                .expectBody(Workout.class)
                .returnResult();

        String savedWorkoutId = savedResult.getResponseBody().getWorkoutId();
        Workout expectedSavedWorkout = objectMapper.readValue(requestBody, Workout.class);
        expectedSavedWorkout.setWorkoutId(savedWorkoutId);

        Workout actualSavedWorkoutFromRepo = this.workoutRepository.findById(savedWorkoutId).block();
        Assert.assertEquals(expectedSavedWorkout, actualSavedWorkoutFromRepo);
    }
}