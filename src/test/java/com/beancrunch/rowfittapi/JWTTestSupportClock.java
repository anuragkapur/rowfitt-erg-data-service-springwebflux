package com.beancrunch.rowfittapi;

import io.jsonwebtoken.Clock;

import java.util.Date;

public class JWTTestSupportClock implements Clock {

    private Date date;

    public JWTTestSupportClock(Date date) {
        this.date = date;
    }

    @Override
    public Date now() {
        return date;
    }
}
