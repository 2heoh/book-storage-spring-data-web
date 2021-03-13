--liquibase formatted sql

--changeset sergey.lobin:2021-01-11-001-books
DROP TABLE IF EXISTS books;
create table books (
 id serial NOT NULL PRIMARY KEY,
 title varchar(255),
 description VARCHAR(255)
)