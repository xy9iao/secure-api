CREATE DATABASE IF NOT EXISTS secure_api_db;

USE secure_api_db;

CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT IGNORE INTO profiles (username, password, role)
VALUES
('renter1', 'renter123', 'USER'),
('renter2', 'renter456', 'USER'),
('admin', 'admin123', 'ADMIN');
