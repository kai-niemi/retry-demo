drop table if exists customer;
drop table if exists purchase_order;

create sequence if not exists seq_customer;
create sequence if not exists seq_order;

create table customer
(
    id         integer     not null,
    address1   varchar(255),
    address2   varchar(255),
    city       varchar(255),
    country    varchar(16),
    postcode   varchar(16),
    created    timestamptz not null,
    email      varchar(128),
    user_name  varchar(15) not null unique,
    first_name varchar(128),
    last_name  varchar(128),
    telephone  varchar(128),

    primary key (id)
);

create table purchase_order
(
    id                  integer        not null,
    bill_address1       varchar(255),
    bill_address2       varchar(255),
    bill_city           varchar(255),
    bill_country        varchar(16),
    bill_postcode       varchar(16),
    bill_to_first_name  varchar(255),
    bill_to_last_name   varchar(255),
    date_placed         timestamptz    not null,
    deliv_to_first_name varchar(255),
    deliv_to_last_name  varchar(255),
    deliv_address1      varchar(255),
    deliv_address2      varchar(255),
    deliv_city          varchar(255),
    deliv_country       varchar(16),
    deliv_postcode      varchar(16),
    order_status        varchar(64),
    total_price         decimal(18, 2) not null,
    customer_id         integer        null,

    primary key (id)
);

