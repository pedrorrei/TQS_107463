DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS meal;
DROP TABLE IF EXISTS restaurant;

CREATE TABLE restaurant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100)
);

CREATE TABLE meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    available_quantity INT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    CONSTRAINT fk_meal_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    menu VARCHAR(255),
    used BOOLEAN NOT NULL,
    quantity INT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    CONSTRAINT fk_reservation_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);