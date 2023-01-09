alter table client
    add column address_id bigint references address(id)

