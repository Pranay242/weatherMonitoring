CREATE VIEW daily_weather_aggregates AS
SELECT
    city,
    DATE(date_time) AS day,
    TRUNCATE(AVG(temperature), 2) AS average_temperature_kelvin,
    TRUNCATE(MAX(temperature), 2) AS maximum_temperature_kelvin,
    TRUNCATE(MIN(temperature), 2) AS minimum_temperature_kelvin,
    TRUNCATE(AVG(temperature - 273.15), 2) AS average_temperature_celsius,
    TRUNCATE(MAX(temperature - 273.15), 2) AS maximum_temperature_celsius,
    TRUNCATE(MIN(temperature - 273.15), 2) AS minimum_temperature_celsius,
    TRUNCATE(AVG(feels_like_temperature), 2) AS average_feels_like_temperature_kelvin,
    TRUNCATE(MAX(feels_like_temperature), 2) AS maximum_feels_like_temperature_kelvin,
    TRUNCATE(MIN(feels_like_temperature), 2) AS minimum_feels_like_temperature_kelvin,
    TRUNCATE(AVG(feels_like_temperature - 273.15), 2) AS average_feels_like_temperature_celsius,
    TRUNCATE(MAX(feels_like_temperature - 273.15), 2) AS maximum_feels_like_temperature_celsius,
    TRUNCATE(MIN(feels_like_temperature - 273.15), 2) AS minimum_feels_like_temperature_celsius,
    TRUNCATE(AVG(humidity), 2) AS average_humidity,
    TRUNCATE(MAX(humidity), 2) AS maximum_humidity,
    TRUNCATE(MIN(humidity), 2) AS minimum_humidity,
    TRUNCATE(AVG(windspeed), 2) AS average_windspeed,
    TRUNCATE(MAX(windspeed), 2) AS maximum_windspeed,
    TRUNCATE(MIN(windspeed), 2) AS minimum_windspeed,
    (SELECT main
     FROM weather_metadata wm2
     WHERE wm2.city = weather_metadata.city
       AND DATE(wm2.date_time) = DATE(weather_metadata.date_time)
     GROUP BY main
     ORDER BY COUNT(*) DESC
     LIMIT 1) AS dominant_weather_condition
FROM
    weather_metadata
GROUP BY
    city,
    DATE(date_time);
