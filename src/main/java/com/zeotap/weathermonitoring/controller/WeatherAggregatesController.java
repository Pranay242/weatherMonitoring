package com.zeotap.weathermonitoring.controller;

import com.zeotap.weathermonitoring.bean.AlertThreshold;
import com.zeotap.weathermonitoring.bean.WeatherAggregatesDto;
import com.zeotap.weathermonitoring.dao.AlertThresholdDao;
import com.zeotap.weathermonitoring.dao.WeatherMetadataDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class WeatherAggregatesController {

    @Autowired
    private WeatherMetadataDao weatherMetadataDao;

    @Autowired
    private AlertThresholdDao alertThresholdDao;

    @GetMapping("/weather-aggregates")
    public List<WeatherAggregatesDto> getWeatherAggregates(@RequestParam String date, @RequestParam String unit, @RequestParam String city) {
        log.info("/weather-aggregates called");
        return weatherMetadataDao.getWeatherAggregates(date, unit, city);
    }

    @GetMapping("/cities")
    public List<String> getCities() {
        log.info("/cities called");
        return weatherMetadataDao.getCities();
    }

    @PostMapping("/thresholds")
    public ResponseEntity<?> createThreshold(@RequestBody AlertThreshold threshold) {
        if (alertThresholdDao.isDuplicate(threshold)) {
            return ResponseEntity.badRequest().body("Duplicate threshold entry.");
        }
        AlertThreshold createdThreshold = alertThresholdDao.createThreshold(threshold);
        return ResponseEntity.ok(createdThreshold);
    }

    @GetMapping("/thresholds")
    public ResponseEntity<List<AlertThreshold>> getAllThresholds() {
        List<AlertThreshold> thresholds = alertThresholdDao.getAllThresholds();
        return ResponseEntity.ok(thresholds);
    }

}