package com.zeotap.weathermonitoring.dao;

import com.zeotap.weathermonitoring.bean.AlertThreshold;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
public class AlertThresholdDaoImpl implements AlertThresholdDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate template;

    public AlertThreshold createThreshold(AlertThreshold threshold) {

        if(isDuplicate(threshold)) {
            throw new RuntimeException("Duplicate alert");
        }

        String sql = "INSERT INTO alert_thresholds (field, agg, operator, unit_of_temp, value, name) VALUES (:field, :agg, :operator, :unitOfTemp, :value, :name)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("field", threshold.getField());
        parameters.addValue("agg", threshold.getAgg());
        parameters.addValue("operator", threshold.getOperator());
        parameters.addValue("unitOfTemp", threshold.getUnitOfTemp());
        parameters.addValue("value", threshold.getValue());
        parameters.addValue("name", threshold.getName());

        jdbcTemplate.update(sql, parameters);
        return threshold;
    }

    public List<AlertThreshold> getAllThresholds() {
        String sql = "SELECT * FROM alert_thresholds";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AlertThreshold threshold = new AlertThreshold();
            threshold.setId(rs.getLong("id"));
            threshold.setField(rs.getString("field"));
            threshold.setAgg(rs.getString("agg"));
            threshold.setOperator(rs.getString("operator"));
            threshold.setUnitOfTemp(rs.getString("unit_of_temp"));
            threshold.setValue(rs.getDouble("value"));
            threshold.setName(rs.getString("name"));
            return threshold;
        });
    }

    public boolean isDuplicate(AlertThreshold threshold) {
        String sql = "SELECT COUNT(*) FROM alert_thresholds WHERE (field = :field AND agg = :agg AND operator = :operator AND unit_of_temp = :unitOfTemp AND value=:value) OR name=:name";
        
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("field", threshold.getField());
        parameters.addValue("agg", threshold.getAgg());
        parameters.addValue("operator", threshold.getOperator());
        parameters.addValue("unitOfTemp", threshold.getUnitOfTemp());
        parameters.addValue("value", threshold.getValue());
        parameters.addValue("name", threshold.getName());

        Integer count = jdbcTemplate.queryForObject(sql, parameters, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public void createTable() throws IOException {
        InputStream io = this.getClass().getResourceAsStream("/sql/alert.sql");
        String sql = new String(IOUtils.toByteArray(io));
        template.execute(sql);
    }
}
