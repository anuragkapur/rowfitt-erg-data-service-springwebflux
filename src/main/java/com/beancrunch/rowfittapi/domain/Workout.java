package com.beancrunch.rowfittapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Workout {

    private String workoutId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private Date date;
    private int distance;
    private int timeHh;
    private int timeMm;
    private float timeSss;
    private float splitMm;
    private float splitSss;
    private int strokeRate;
    private int heartRate;
    private String userId;

    public Workout() {
        workoutId = UUID.randomUUID().toString();
    }
}
