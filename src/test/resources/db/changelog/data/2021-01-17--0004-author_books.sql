--liquibase formatted sql

--changeset sergey.lobin:2021-01-17-004-author_books
insert into author_books (author_id, book_id)
values (1, 1),
       (2, 2);

