package com.zeotap.weathermonitoring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeotap.weathermonitoring.bean.AlertThreshold;
import com.zeotap.weathermonitoring.bean.City;
import com.zeotap.weathermonitoring.bean.WeatherMetadata;
import com.zeotap.weathermonitoring.dao.AlertThresholdDao;
import com.zeotap.weathermonitoring.dao.WeatherMetadataDao;
import com.zeotap.weathermonitoring.utils.OpenWeatherMapAPIHelper;
import com.zeotap.weathermonitoring.utils.ThresholdUtils;
import jakarta.websocket.OnClose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WeatherScheduleService {

    @Autowired
    private OpenWeatherMapAPIHelper openWeatherMapAPIHelper;
    @Autowired
    private WeatherMetadataDao weatherMetadataDao;
    @Autowired
    private AlertThresholdDao alertThresholdDao;
    @Scheduled(cron = "${schedule.cron}")
    public void execute() throws IOException {
        List<City> cities;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            cities = objectMapper.readValue(this.getClass().getResourceAsStream("/cities/cities.json"), new TypeReference<List<City>>(){});
        } catch (Exception e) {
            log.error("Error while reading city file");
            return;
        }

        weatherMetadataDao.createTable(); // If table not exist, then we create
        alertThresholdDao.createTable(); // If table not exist, then we create
        weatherMetadataDao.createView(); // If MV not exist, then we create

        List<WeatherMetadata> weatherMetadataList = new ArrayList<>();

        for (City city : cities) {
            try {
                Map<String, Object> res = openWeatherMapAPIHelper.getWeatherInfo(city);
                if(!MapUtils.isEmpty(res)) {
                    WeatherMetadata weatherMetadata = new WeatherMetadata();
                    weatherMetadata.setCity(city.getCityName());
                    Integer dateTime = (Integer) res.get("dt");
                    weatherMetadata.setDateTime(new Date(dateTime * 1000L));
                    List<Map<String, Object>> weather = (List<Map<String, Object>>) res.get("weather");
                    weatherMetadata.setMain((String) weather.get(0).get("main"));
                    Map<String, Object> main = (Map<String, Object>) res.get("main");
                    weatherMetadata.setTemperature((Double) main.get("temp"));
                    weatherMetadata.setFeelsLikeTemperature((Double) main.get("feels_like"));
                    weatherMetadata.setHumidity((Integer) main.get("humidity"));
                    Map<String, Object> wind = (Map<String, Object>) res.get("wind");
                    weatherMetadata.setWindSpeed((Double) wind.get("speed"));

                    weatherMetadataList.add(weatherMetadata);

                }
            } catch (Exception e) {
                log.error("Error while call for city - {}", city.getCityName());
            }
        }

        try {
            weatherMetadataDao.insert(weatherMetadataList);
        }catch (Exception e) {
            log.error("Exception occurred while inserting to DB");
            throw e;
        }

        // Alert code
        List<AlertThreshold> allThresholds = alertThresholdDao.getAllThresholds();
        if(!CollectionUtils.isEmpty(allThresholds)) {
            allThresholds.forEach(threshold -> {
                List<String> citiesToBeAlerted = weatherMetadataDao.getAllCitiesForAlertQuery(threshold);
                if(!CollectionUtils.isEmpty(citiesToBeAlerted)) {
                    citiesToBeAlerted.forEach(c -> {
                        log.info("Alert for city - {} Alert details - {} | {} | {} | {} | {}", c, threshold.getName(), threshold.getField(), threshold.getAgg(), threshold.getOperator(), threshold.getValue());
                    });
                }
            });
        }

    }
}
