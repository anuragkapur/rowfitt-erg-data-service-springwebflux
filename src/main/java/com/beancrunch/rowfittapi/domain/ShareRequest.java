package com.beancrunch.rowfittapi.domain;

import lombok.Data;

@Data
public class ShareRequest {

    private String shareId;
    private ShareStatus shareStatus;
}
