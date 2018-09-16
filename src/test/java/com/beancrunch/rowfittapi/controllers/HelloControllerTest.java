package com.beancrunch.rowfittapi.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@WebFluxTest(HelloController.class)
public class HelloControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldReturnHelloWebFluxMessage() {

        this.webTestClient
                .get()
                .uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello Webflux!");
    }
}