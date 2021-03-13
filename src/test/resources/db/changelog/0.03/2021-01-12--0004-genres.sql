--liquibase formatted sql

--changeset sergey.lobin:2021-01-12-004-genres
DROP TABLE IF EXISTS genres;
CREATE TABLE genres(
 id bigserial NOT NULL PRIMARY KEY,
 name VARCHAR(255)
);