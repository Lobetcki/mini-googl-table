-- liquibase formatted sql

-- changeset anton:1
create table balance
(
    row     int NOT NULL,
    column_number  int,
    content varchar(255) NOT NULL,

    primary key (row, column_number)
);

