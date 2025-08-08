--liquibase formatted sql

--changeset bokov:1
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(64) NOT NULL UNIQUE
);

--changeset bokov:2
CREATE TABLE IF NOT EXISTS orders
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL REFERENCES users (id),
    product_name VARCHAR(64),
    quantity INT,
    order_date TIMESTAMP
);