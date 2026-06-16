CREATE DATABASE IF NOT EXISTS secure_api_db;

USE secure_api_db;

CREATE TABLE IF NOT EXISTS profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    owner VARCHAR(20) NOT NULL,
    CONSTRAINT fk_products_owner
        FOREIGN KEY (owner)
        REFERENCES profiles(username)
);

-- Passwords are stored as SHA-512 hex hashes (no salt) of the original values:
INSERT IGNORE INTO profiles (username, password, role)
VALUES
('renter1', '5cddb1469a3dd126890ea69a5340914ece2230793fcce0d468a3db1f74e14c75f07f5104f60b398a77ca3fcc04b5eba3ae6f4455bc0d2301d76695eda123f508', 'USER'),
('renter2', 'f33fa83f98e7e4db960f8a0b14d825e4fe18cd77e30ac183ec214011fe92436a2be0b8db9ab949715e859c394af3a5f21932414c0d82911599cb811d17e0e1cd', 'USER'),
('admin', '7fcf4ba391c48784edde599889d6e3f1e47a27db36ecc050cc92f259bfac38afad2c68a1ae804d77075e8fb722503f3eca2b2c1006ee6f6c7b7628cb45fffd1d', 'ADMIN');

INSERT IGNORE INTO products (id, name, category, description, owner)
VALUES
(1, 'CityGo Compact', 'Sedan', 'Fuel-efficient compact car for short city bookings.', 'renter1'),
(2, 'Harbor Family SUV', 'SUV', 'Roomy SUV for families, luggage, and weekend trips.', 'renter1'),
(3, 'Campus Cargo Van', 'Van', 'Practical van for moving boxes and event equipment.', 'renter2'),
(4, 'GreenSpark EV', 'Electric', 'Quiet electric vehicle for low-emission daily rentals.', 'renter2'),
(5, 'Roadster', 'Sedan', 'A sports car for the road.', 'admin');
