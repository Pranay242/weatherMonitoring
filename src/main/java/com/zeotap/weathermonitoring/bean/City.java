package com.zeotap.weathermonitoring.bean;

import lombok.Data;

@Data
public class City {
    private String cityName;
    private Double longitude;
    private Double latitude;
    private String country;
}
