package com.zeotap.weathermonitoring.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.zeotap.*")
public class WeatherMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherMonitoringApplication.class, args);
	}

}
