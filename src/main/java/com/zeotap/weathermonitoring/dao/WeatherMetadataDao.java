package com.zeotap.weathermonitoring.dao;

import com.zeotap.weathermonitoring.bean.AlertThreshold;
import com.zeotap.weathermonitoring.bean.WeatherAggregatesDto;
import com.zeotap.weathermonitoring.bean.WeatherMetadata;

import java.io.IOException;
import java.util.List;

public interface WeatherMetadataDao {
    int insert(List<WeatherMetadata> weatherMetadataList);

    void createTable() throws IOException;

    void createView() throws IOException;

    List<WeatherAggregatesDto> getWeatherAggregates(String date, String unit, String city);

    List<String> getCities();

    List<String> getAllCitiesForAlertQuery(AlertThreshold alertThreshold);
}
