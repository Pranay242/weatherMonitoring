package com.zeotap.weathermonitoring.utils;

import com.zeotap.weathermonitoring.bean.AlertThreshold;

import java.util.List;
import java.util.stream.Collectors;

public class ThresholdUtils {


    public static String generateCityWiseQuery(AlertThreshold threshold) {
        // if unit type is celcius then +273
        if("celsius".equals(threshold.getUnitOfTemp())) {
            threshold.setValue(threshold.getValue() + 273.15);
        }
        String operator = getOperator(threshold.getOperator());

        String aggregateFunction = getAggregateFunction(threshold);

        if("last 2 records".equals(threshold.getAgg())) {
            return "WITH RankedWeather AS (" +
                    "   SELECT city, " + getField(threshold) + ", " +
                    "          ROW_NUMBER() OVER (PARTITION BY city ORDER BY date_time DESC) AS rn " +
                    "   FROM weather_metadata " +
                    "   WHERE DATE(date_time) = CURDATE() " +
                    ") " +
                    "SELECT city, COUNT(*) AS c " +
                    "FROM RankedWeather " +
                    "WHERE rn <= 2 AND " + getField(threshold) + " " + operator + " " + threshold.getValue() + " " +
                    "GROUP BY city " +
                    "HAVING COUNT(*) >= 2;";
        }
        
        return String.format(
            "SELECT city, %s FROM weather_metadata WHERE DATE(date_time) = CURDATE() GROUP BY city " +
            "HAVING %s %s %s",
            aggregateFunction,
            aggregateFunction.split(" AS ")[0], // Get the aggregate field
            operator,
            threshold.getValue()
        );
    }

    private static String getAggregateFunction(AlertThreshold threshold) {
        String field = getField(threshold);
        switch (threshold.getAgg()) {
            case "avg":
                return "AVG(" + field + ") AS avg_" + field;
            case "max":
                return "MAX(" + field + ") AS max_" + field;
            case "min":
                return "MIN(" + field + ") AS min_" + field;
            default:
                return ""; // Skip unknown aggregations
        }
    }

    private static String getField(AlertThreshold threshold) {
        switch (threshold.getField()) {
            case "temp":
                return "temperature";
            case "humidity":
                return "humidity";
            case "windspeed":
                return "windSpeed";
            default:
                return "";
        }
    }

    private static String getOperator(String operator) {
        switch (operator) {
            case ">":
                return ">";
            case "<":
                return "<";
            case ">=":
                return ">=";
            case "<=":
                return "<=";
            case "=":
                return "=";
            default:
                return ""; // Handle unknown operators
        }
    }
}
