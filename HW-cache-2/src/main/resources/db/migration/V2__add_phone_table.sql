create table phone
(
    id   bigint not null primary key,
    number varchar(50),
    client_id bigint references client(id)
);

