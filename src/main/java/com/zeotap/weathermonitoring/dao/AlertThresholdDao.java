package com.zeotap.weathermonitoring.dao;

import com.zeotap.weathermonitoring.bean.AlertThreshold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

public interface AlertThresholdDao {
    AlertThreshold createThreshold(AlertThreshold threshold);
    List<AlertThreshold> getAllThresholds();

    boolean isDuplicate(AlertThreshold threshold);

    void createTable() throws IOException;
}
