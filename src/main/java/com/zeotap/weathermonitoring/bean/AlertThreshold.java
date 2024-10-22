package com.zeotap.weathermonitoring.bean;

import lombok.Data;

@Data
public class AlertThreshold {
    private Long id;
    private String field;
    private String agg;
    private String operator;
    private String unitOfTemp;
    private Double value;
    private String name;
}
