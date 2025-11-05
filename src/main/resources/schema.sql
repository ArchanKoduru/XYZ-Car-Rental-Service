CREATE TABLE IF NOT EXISTS car (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    car_name VARCHAR(255),
    segment ENUM('EXTRA_LARGE','LARGE','MEDIUM','SMALL'),
    daily_rate FLOAT
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    driving_license_number VARCHAR(255),
    age INT,
    start_date DATE,
    end_date DATE,
    rental_price FLOAT,
    car_id BIGINT,
    CONSTRAINT FK_car FOREIGN KEY (car_id) REFERENCES car(id)
);
