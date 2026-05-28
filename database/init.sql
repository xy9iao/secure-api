CREATE DATABASE IF NOT EXISTS secure_api_db;

USE secure_api_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT IGNORE INTO users (username, password, role)
VALUES
('alice', 'password123', 'USER'),
('admin', 'admin123', 'ADMIN');