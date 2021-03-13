--liquibase formatted sql

--changeset sergey.lobin:2021-01-17-006-books
alter table books drop column author_id;

--changeset sergey.lobin:2021-03-08-007-books
alter sequence if exists books_id_seq restart with 2;