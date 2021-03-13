--liquibase formatted sql

--changeset sergey.lobin:2021-01-13-005-books
alter table books
add column genre_id bigint DEFAULT 0 NOT NULL references genres(id)