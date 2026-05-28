USE secure_api_db;

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    owner VARCHAR(50) NOT NULL,
    CONSTRAINT fk_products_owner
        FOREIGN KEY (owner)
        REFERENCES profiles(username)
);

INSERT IGNORE INTO products (id, name, category, description, owner)
VALUES
(1, 'CityGo Compact', 'Sedan', 'Fuel-efficient compact car for short city bookings.', 'renter1'),
(2, 'Harbor Family SUV', 'SUV', 'Roomy SUV for families, luggage, and weekend trips.', 'renter1'),
(3, 'Campus Cargo Van', 'Van', 'Practical van for moving boxes and event equipment.', 'renter2'),
(4, 'GreenSpark EV', 'Electric', 'Quiet electric vehicle for low-emission daily rentals.', 'renter2'),
(5, 'Roadster', 'Sedan', 'A sports car for the road.', 'admin');
