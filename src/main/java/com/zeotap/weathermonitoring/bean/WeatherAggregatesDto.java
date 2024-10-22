package com.zeotap.weathermonitoring.bean;

import lombok.Data;

@Data
public class WeatherAggregatesDto {
    private String city;
    private String day;
    private Double averageTemperatureKelvin;
    private Double maximumTemperatureKelvin;
    private Double minimumTemperatureKelvin;
    private Double averageTemperatureCelsius;
    private Double maximumTemperatureCelsius;
    private Double minimumTemperatureCelsius;
    private Double averageFeelsLikeTemperatureKelvin;
    private Double maximumFeelsLikeTemperatureKelvin;
    private Double minimumFeelsLikeTemperatureKelvin;
    private Double averageFeelsLikeTemperatureCelsius;
    private Double maximumFeelsLikeTemperatureCelsius;
    private Double minimumFeelsLikeTemperatureCelsius;
    private Double averageHumidity;
    private Double maximumHumidity;
    private Double minimumHumidity;
    private Double averageWindspeed;
    private Double maximumWindspeed;
    private Double minimumWindspeed;
    private String dominantWeatherCondition;
}
