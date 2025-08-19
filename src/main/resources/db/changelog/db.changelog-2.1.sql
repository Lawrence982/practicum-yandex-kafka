--liquibase formatted sql

--changeset bokov:1
CREATE TABLE IF NOT EXISTS revision
(
    id SERIAL PRIMARY KEY ,
    timestamp BIGINT NOT NULL
);

--changeset bokov:2
CREATE TABLE IF NOT EXISTS users_aud
(
    id BIGINT,
    rev INT REFERENCES revision (id),
    revtype SMALLINT ,
    name VARCHAR(64),
    email VARCHAR(64)
);

--changeset bokov:3
CREATE TABLE IF NOT EXISTS orders_aud
(
    id BIGINT,
    rev INT REFERENCES revision (id),
    revtype SMALLINT,
    user_id BIGSERIAL REFERENCES users (id),
    product_name VARCHAR(64),
    quantity INT,
    order_date TIMESTAMP
);