package com.beancrunch.rowfittapi.domain;

import lombok.Builder;

import java.util.Date;

@Builder
public class FilterCriteria {

    private Date minDate;
    private Date maxDate;
    private int minDistance;
    private int maxDistance;
    private int minTimeHh;
    private int maxTimeHh;
    private int minTimeMm;
    private int maxTimeMm;
    private SortBy sortBy;
}
