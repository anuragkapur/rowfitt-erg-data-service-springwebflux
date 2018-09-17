package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.repository.InMemoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@RunWith(SpringRunner.class)
@WebFluxTest(WorkoutController.class)
@Import(InMemoryRepository.class)
public class WorkoutControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void saveWorkout() {

        String requestBody = "{\"date\":\"01/02/2018\"}";

        this.webTestClient
                .post()
                .uri("/api/workout")
                .body(BodyInserters.fromObject(requestBody))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated();
    }
}