package com.zeotap.weathermonitoring.bean;

import lombok.Data;

import java.util.Date;

@Data
public class WeatherMetadata {
    private String city;
    private String main;// Rain, snow, clear
    private Double temperature;
    private Double feelsLikeTemperature;
    private Integer humidity;
    private Double windSpeed;
    private Date dateTime;
}
