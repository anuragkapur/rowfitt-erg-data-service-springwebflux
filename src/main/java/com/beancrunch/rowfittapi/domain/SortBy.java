package com.beancrunch.rowfittapi.domain;

public enum SortBy {
    Date("date"), Distance("distance"), Time("time");

    private String value;

    SortBy(String value) {
        this.value = value;
    }
}
