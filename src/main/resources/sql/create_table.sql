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
