package com.zeotap.weathermonitoring.dao;

import com.zeotap.weathermonitoring.bean.AlertThreshold;
import com.zeotap.weathermonitoring.bean.WeatherAggregatesDto;
import com.zeotap.weathermonitoring.bean.WeatherMetadata;
import com.zeotap.weathermonitoring.utils.ThresholdUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WeatherMetadataDaoImpl implements WeatherMetadataDao {

    private static final String tableName = "weather_metadata";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int insert(List<WeatherMetadata> weatherMetadataList) {

        String sql = "INSERT INTO weather_metadata (city, main, temperature, feels_like_temperature, date_time, humidity, windspeed) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    WeatherMetadata weatherData = weatherMetadataList.get(i);
                    ps.setString(1, weatherData.getCity());
                    ps.setString(2, weatherData.getMain());
                    ps.setDouble(3, weatherData.getTemperature());
                    ps.setDouble(4, weatherData. getFeelsLikeTemperature());
                    ps.setTimestamp(5, new java.sql.Timestamp(weatherData.getDateTime().getTime()));
                    ps.setInt(6, weatherData.getHumidity());
                    ps.setDouble(7, weatherData.getWindSpeed());
                }

                @Override
                public int getBatchSize() {
                    return weatherMetadataList.size();
                }
            });
        } catch (DataAccessException e) {
            throw new RuntimeException("Error saving weather data", e);
        }
        return weatherMetadataList.size();
    }

    @Override
    public void createTable() throws IOException {
        InputStream io = this.getClass().getResourceAsStream("/sql/create_table.sql");
        String sql = new String(IOUtils.toByteArray(io));
        jdbcTemplate.execute(sql);
    }

    @Override
    public void createView() throws IOException {

        String checkViewExists = "SELECT count(1) FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = 'daily_weather_aggregates'";

        int count = jdbcTemplate.queryForObject(checkViewExists, Integer.class);

        if(count == 0) {
            InputStream io = this.getClass().getResourceAsStream("/sql/view.sql");
            String sql = new String(IOUtils.toByteArray(io));
            jdbcTemplate.execute(sql);
        }
    }

    @Override
    public List<WeatherAggregatesDto> getWeatherAggregates(String date, String unit, String city) {
        String sql = "SELECT * FROM daily_weather_aggregates WHERE day = :day AND city = :city ";
        if (unit.equals("celsius")) {
            sql += "AND average_temperature_celsius IS NOT NULL";
        } else {
            sql += "AND average_temperature_kelvin IS NOT NULL";
        }

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("day", date);
        mapSqlParameterSource.addValue("city", city);

        return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> {
            WeatherAggregatesDto dto = new WeatherAggregatesDto();
            dto.setCity(rs.getString("city"));
            dto.setDay(rs.getString("day"));
            dto.setAverageTemperatureKelvin(rs.getDouble("average_temperature_kelvin"));
            dto.setMaximumTemperatureKelvin(rs.getDouble("maximum_temperature_kelvin"));
            dto.setMinimumTemperatureKelvin(rs.getDouble("minimum_temperature_kelvin"));
            dto.setAverageTemperatureCelsius(rs.getDouble("average_temperature_celsius"));
            dto.setMaximumTemperatureCelsius(rs.getDouble("maximum_temperature_celsius"));
            dto.setMinimumTemperatureCelsius(rs.getDouble("minimum_temperature_celsius"));
            dto.setAverageHumidity(rs.getDouble("average_humidity"));
            dto.setMaximumHumidity(rs.getDouble("maximum_humidity"));
            dto.setMinimumHumidity(rs.getDouble("minimum_humidity"));
            dto.setAverageWindspeed(rs.getDouble("average_windspeed"));
            dto.setMaximumWindspeed(rs.getDouble("maximum_windspeed"));
            dto.setMinimumWindspeed(rs.getDouble("minimum_windspeed"));
            dto.setDominantWeatherCondition(rs.getString("dominant_weather_condition"));
            return dto;
        });
    }

    public List<String> getCities() {
        String sql = "SELECT DISTINCT city FROM weather_metadata";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<String> getAllCitiesForAlertQuery(AlertThreshold alertThreshold) {
        String query = ThresholdUtils.generateCityWiseQuery(alertThreshold);
        return namedParameterJdbcTemplate.query(query, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("city");
            }
        });

    }

}
