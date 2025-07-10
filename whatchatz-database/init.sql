CREATE DATABASE IF NOT EXISTS whatchatz;

USE whatchatz;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS contacts;


CREATE TABLE IF NOT EXISTS users
(
    uid  VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    info TEXT
);

CREATE TABLE IF NOT EXISTS messages
(
    id          SERIAL PRIMARY KEY,
    chat_id     VARCHAR(255) NOT NULL,
    sender_id   VARCHAR(100) NOT NULL,
    receiver_id VARCHAR(100) NOT NULL,
    message     TEXT         NOT NULL,
    timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS contacts
(
    id                     SERIAL PRIMARY KEY,
    owner_id               VARCHAR(100) NOT NULL,
    contact_id             VARCHAR(100) NOT NULL,
    contact_name           VARCHAR(100) NOT NULL,
    last_message_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);