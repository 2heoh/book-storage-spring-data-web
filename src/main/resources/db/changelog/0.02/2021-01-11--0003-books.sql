--liquibase formatted sql

--changeset sergey.lobin:2021-01-11-003-books
alter table books
add column author_id bigint DEFAULT 0 NOT NULL references authors(id)