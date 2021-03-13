--liquibase formatted sql

--changeset sergey.lobin:2021-01-11-002-authors
DROP TABLE IF EXISTS authors;
CREATE TABLE authors(
 id bigserial  NOT NULL PRIMARY KEY,
 name VARCHAR(255)
);