# Real-Time Data Processing System for Weather Monitoring

## Table of Contents
- [Objective](#objective)
- [Data Source](#data-source)
- [Database Schema](#database-schema)
- [Processing and Analysis](#processing-and-analysis)
- [Rollups and Aggregates](#rollups-and-aggregates)
- [Alerting Thresholds](#alerting-thresholds)
- [Visualizations](#visualizations)
- [Test Cases](#test-cases)
- [Bonus Features](#bonus-features)
- [Installation and Setup](#installation-and-setup)
- [Demo link](#demo)

## Objective
Develop a real-time data processing system to monitor weather conditions and provide summarized insights using rollups and aggregates. The system will utilize data from the [OpenWeatherMap API](https://openweathermap.org/). The system will provide UI to set alerts if said parameter threshold is met and alerts will be logged on the console.

## Data Source
The system will continuously retrieve weather data from the OpenWeatherMap API at the 5 minutes interval (configurable from application.properties). The system will extract following details from the API response:
- `main`: Main weather condition (e.g., Rain, Snow, Clear)
- `temp`: Current temperature in Kelvin (Stored as kelvin)
- `feels_like`: Perceived temperature in Centigrade
- `dt`: Time of the data update (Unix timestamp)
- `wind_speed` : Wind speed (integer)
- `humidity` : Humidity of the city (integer)

You can find the details of the data table in [Database Schema](#database-schema)

## Database Schema

### 1. Weather Metadata Table

The `weather_metadata` table stores real-time weather data for various cities. The schema is as follows:

```
CREATE TABLE IF NOT EXISTS weather_metadata (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    main VARCHAR(50) NOT NULL,
    temperature DECIMAL(10, 2) NOT NULL,
    feels_like_temperature DECIMAL(10, 2) NOT NULL,
    humidity INTEGER NOT NULL,
    windspeed DECIMAL(10, 2) NOT NULL,
    date_time TIMESTAMP NOT NULL
);
```
### 2. Alert Thresholds Table

The `alert_thresholds` table stores user-defined thresholds for weather alerts. The schema is as follows:

```sql
CREATE TABLE IF NOT EXISTS alert_thresholds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    field VARCHAR(50) NOT NULL,
    agg VARCHAR(50) NOT NULL,
    operator VARCHAR(5) NOT NULL,
    name VARCHAR(250) NOT NULL,
    unit_of_temp VARCHAR(10),
    value DECIMAL(10, 2) NOT NULL
);
```

### 3. Daily Weather Aggregates View

The `daily_weather_aggregates` view provides a summary of daily weather data aggregates. The schema is as follows:

```sql
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

```



## Processing and Analysis
- The system should continuously call the OpenWeatherMap API at a configurable interval to retrieve real-time weather data for major metros in India: Delhi, Mumbai, Chennai, Bangalore, Kolkata, Hyderabad, Pune, Ahmedabad.
- If you want to add more cities/towns, please add these cities with longitude and latitude in cities.json file in the project.
- The data mentioned in the [Data Source](#data-source) will be stored against each city in the main data table (schema can be found here [Database Schema](#database-schema)).
- The alerts which are created by user will be tested against data stored for today and if threshold is breached then log will be printed for that city (this can be modified to alert through email).
## Rollups and Aggregates
### Daily Weather Summary
- The view mentioned in the [Database Schema](#database-schema) is used to summarize the daily rollups and aggregates.
- Average, Maximum, Minimum aggregates functions are used to show the data in the UI for each field i.e. temperature, feel_like_temp, humidity, windspeed.
- Temperature will be either in Kelvin or celsius based on user input.
- You can select any past date to check the weather for that day, if data is not available not found image will be rendered.
- You can check the above data city-wise

### Alerting Thresholds
- **Customizable Alerts**: Users can easily set personalized thresholds for temperature and specific weather conditions, ensuring that alerts are relevant to their unique preferences.
    - Examples - Set alerts for conditions like "Notify me when the average exceeds 35°C/300K" or "Alert if the last 2 record temperature falls below 10°C/284K.", "Send an alert if maximum humidity rises above 80%.",  "Alert if average windspeed exceeds 20 km/h."
- **Real-Time Monitoring:** The system continuously monitors incoming weather data, providing user-defined thresholds to ensure timely notifications.

- **Instant Notifications:** When any threshold is breached, users receive immediate alerts displayed on the console, enabling quick action in response to changing weather conditions.

- **User-Friendly Interface:** The alert creation UI is designed for simplicity, allowing users to easily navigate and configure their alerts. Please check out the demo video to know about this feature more.
## Visualizations
- Daily weather summaries
- Existing alerts
- UI to add new alerts (Duplicates can not be allowed)

## Test Cases
1. **System Setup**: Verify the system starts successfully and connects to the OpenWeatherMap API using a valid API key.
2. **Data Retrieval**: Simulate API calls at configurable intervals. Ensure the system retrieves weather data for the specified location and parses the response correctly.
3. **Temperature Conversion**: View is used to convert the Kelvin to other units
4. **Daily Weather Summary**: Simulate a sequence of weather updates for several days. Verify that daily summaries are calculated correctly, including average, maximum, minimum temperatures, and dominant weather condition.
5. **Alerting Thresholds**: Define and configure user thresholds for temperature or weather conditions. Simulate weather data exceeding or breaching the thresholds. Verify that alerts are triggered only when a threshold is violated.

## Bonus Features
- Extended the system to support additional weather parameters from the OpenWeatherMap API (e.g., humidity, wind speed) and incorporate them into rollups/aggregates.


## Installation and Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/Pranay242/weatherMonitoring
   cd your-repo
   ## install npm if not available
   npm init -y
   npm install react react-dom babel-loader @babel/core @babel/preset-react
   
   ## Provide your mysql details in the application.properties
   ## zeotap.weather.url=jdbc:mysql://localhost:3306/zeotap_weather_db
   ## Create database/schema named zeotap_weather_db in your sql server
   ## Add your user name and password for the sql db in application.properties
   
   ## Start the server from this class com.zeotap.weathermonitoring.application.WeatherMonitoringApplication
   ## You can access the URL with your host ip (localhost:8080/ if running locally) on port 8080.
   
   ```
   
## Demo
[Watch the video ](https://drive.google.com/file/d/1Wg2RuU-6zFzzYjr-a57wEbsLle1qkq_W/view?usp=sharing)