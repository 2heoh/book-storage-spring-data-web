--liquibase formatted sql

--changeset sergey.lobin:2021-01-17-006-books
alter table books drop column author_id;