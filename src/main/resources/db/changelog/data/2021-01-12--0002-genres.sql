--liquibase formatted sql

--changeset sergey.lobin:2021-01-12-002-genres
insert into genres (id,name) values (0, 'Unknown');
insert into genres (id,name) values (1, 'Action');
insert into genres (id,name) values (2, 'Classics');
insert into genres (id,name) values (3, 'Science Fiction (Sci-Fi)');
