--liquibase formatted sql

--changeset sergey.lobin:2021-01-17-006-author_books
CREATE TABLE author_books(
 author_id bigint,
 book_id bigint
);