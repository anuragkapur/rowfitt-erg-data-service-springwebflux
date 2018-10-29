package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.JWTTestSupportClock;
import com.beancrunch.rowfittapi.domain.Workout;
import com.beancrunch.rowfittapi.filter.AuthorisationFilter;
import com.beancrunch.rowfittapi.repository.InMemoryWorkoutRepository;
import com.beancrunch.rowfittapi.repository.WorkoutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.junit.Assert;
import org.junit.Before;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest(WorkoutController.class)
@Import({InMemoryWorkoutRepository.class, AuthorisationFilter.class, GlobalExceptionHandler.class})
public class WorkoutControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private AuthorisationFilter authorisationFilter;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String expectedLocationHeaderRegex = "^.*/api/workouts/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
            "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    @Before
    public void setUp() throws ParseException {
        workoutRepository.deleteAll().block();
        for (int i = 0; i < 3; i++) {
            saveWorkoutForUser("anurag@beancrunch.com");
        }
        saveWorkoutForUser("user1@example.com");
        JWTTestSupportClock jwtTestSupportClock = new JWTTestSupportClock(getValidDateCorrespondingToTestAccessToken());
        authorisationFilter.setJwtParser(Jwts.parser().setClock(jwtTestSupportClock));
    }

    private void saveWorkoutForUser(String userId) throws ParseException {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setDate(new Date());
        workout.setDate(getDate("22/10/2018 01:00:00"));
        workoutRepository.save(workout).block();
    }

    @Test
    public void saveWorkoutEndpointWithValidAccessTokenShouldSaveWorkoutToRepo() throws IOException {
        EntityExchangeResult<Workout> savedResult = this.webTestClient
                .post()
                .uri("/api/workouts")
                .body(BodyInserters.fromObject(getSaveWorkoutRequestBody()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getValidAccessToken())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", expectedLocationHeaderRegex)
                .expectBody(Workout.class)
                .returnResult();

        String savedWorkoutId = savedResult.getResponseBody().getWorkoutId();
        Workout expectedSavedWorkout = objectMapper.readValue(getSaveWorkoutRequestBody(), Workout.class);
        expectedSavedWorkout.setWorkoutId(savedWorkoutId);
        expectedSavedWorkout.setUserId("anurag@beancrunch.com");

        Workout actualSavedWorkoutFromRepo = this.workoutRepository.findById(savedWorkoutId).block();
        Assert.assertEquals(expectedSavedWorkout, actualSavedWorkoutFromRepo);
    }

    @Test
    public void saveWorkoutEndpointWithoutAuthorizationHeaderShouldReturn401() {
        this.webTestClient
                .post()
                .uri("/api/workouts")
                .body(BodyInserters.fromObject(getSaveWorkoutRequestBody()))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized()
                .returnResult(String.class);
    }

    @Test
    public void getWorkoutsForUserWithValidAccessTokenShouldReturnAllWorkoutsForTheUser() {
        EntityExchangeResult<List<Workout>> workoutsResult = this.webTestClient
                .get()
                .uri("/api/workouts?userId=anurag@beancrunch.com")
                .header("Authorization", "Bearer " + getValidAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Workout.class)
                .returnResult();
        List<Workout> workouts = workoutsResult.getResponseBody();
        System.out.println(workouts);
        Assert.assertNotNull(workouts);
        Assert.assertEquals(3, workouts.size());
    }

    private Date getValidDateCorrespondingToTestAccessToken() throws ParseException {
        String validDate = "22/10/2018 01:00:00";
        return getDate(validDate);
    }

    private Date getDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.parse(dateString);
    }

    private String getValidAccessToken() {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1FUTRSa0k1TWpKQ05UWTROMFE0UlRWR1JUa3l" +
                "OMEl5TWtFelJFUTVOek01TVVNNU1qUkRSUSJ9.eyJodHRwczovL2JlYW5jcnVuY2guY29tL2VtYWlsIjoiYW51cmFnQGJlYW" +
                "5jcnVuY2guY29tIiwiaXNzIjoiaHR0cHM6Ly9iZWFuY3J1bmNoLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJnb29nbGUtb2F1dG" +
                "gyfDEwODAyOTUzMjAwOTcxNTU2NzE2MCIsImF1ZCI6WyJodHRwczovL3Jvd2ZpdHQtZXJnLWRhdGEtc2VydmljZS5oZXJva3" +
                "VhcHAuY29tL2FwaSIsImh0dHBzOi8vYmVhbmNydW5jaC5ldS5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNTQwMTYzMz" +
                "AxLCJleHAiOjE1NDAxNzA1MDEsImF6cCI6IkpDQTJkS3RNcWNrMkVwem9pT1o2alc5NUpUbXR5UndHIiwic2NvcGUiOiJvcG" +
                "VuaWQifQ.nv5YeU70X000KiF-mOSXB7QqmovIK30zrXt3WXspI-b1cTIsajIXvr8vkYVos06oZuJeE6_pfixA5_hVWRYfYKO" +
                "1HBYsodDNPg9XeS5zsaycpgHOEWkOmfeI6PVnbbgmI3Btn-igkFx_kkYhXK3117V6P1BwL1jHfS2inF2SWfQwyXsqKx7ajCm" +
                "SB59bWnFjqmaIhpt07DpjLFPNB25ZH8JDXyi82thaAqAgmBXm1nyAw66fMgYeqUa0CZ9qEDuCLIHPl9ilVH75Ll8X66hNXUu" +
                "7elh7JftjJJN37YfrgAwa_nPc1fv6UPWQRunWBH8-0dIdiQ9Idlm5f9RmpqYEXQ";
    }

    private String getSaveWorkoutRequestBody() {
        return "{\"date\":\"2018-07-29\", \"distance\": \"5000\",\"timeHh\":\"0\",\"timeMm\":\"19\"," +
                "\"timeSss\":\"30.0\", \"splitMm\":\"1\",\"splitSss\":\"57.0\",\"strokeRate\":\"23\"," +
                "\"heartRate\":\"163\"}";
    }
}