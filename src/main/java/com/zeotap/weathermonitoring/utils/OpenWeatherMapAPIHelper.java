package com.zeotap.weathermonitoring.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeotap.weathermonitoring.bean.City;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OpenWeatherMapAPIHelper {

    private static final String endPoint = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";

    @Autowired
    private Environment environment;;

    public Map<String, Object> getWeatherInfo(City city) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        String apiEndpoint = String.format(endPoint, city.getLatitude(), city.getLongitude(), environment.getProperty("open.weather.map.api.key"));
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiEndpoint, String.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseBody, new TypeReference<HashMap<String, Object>>() {});
            } else {
                throw new Exception("Call to weather api is failed : " +  response.getBody());
            }
        } catch (Exception e) {
            log.error("Error calling API: {}", e.getMessage());
            throw e;
        }
    }

}
