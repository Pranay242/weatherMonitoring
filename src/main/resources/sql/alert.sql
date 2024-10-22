CREATE TABLE IF NOT EXISTS alert_thresholds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    field VARCHAR(50) NOT NULL,
    agg VARCHAR(50) NOT NULL,
    operator VARCHAR(5) NOT NULL,
    name VARCHAR(250) NOT NULL,
    unit_of_temp VARCHAR(10),
    value DECIMAL(10, 2) NOT NULL

);