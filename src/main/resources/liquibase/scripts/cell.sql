-- liquibase formatted sql

-- changeset anton:1
create table balance
(
    row     int NOT NULL,
    column_number  varchar(255) NOT NULL,
    content varchar(255),

    primary key (row, column_number)
);

