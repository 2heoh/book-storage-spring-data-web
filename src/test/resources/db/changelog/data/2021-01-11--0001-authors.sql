--liquibase formatted sql

--changeset sergey.lobin:2021-01-11-001-authors
insert into authors (id,name) values (0, 'Unknown');
insert into authors (id,name) values (1, 'Лев Толстой');
insert into authors (id,name) values (2, 'Николай Гоголь');
