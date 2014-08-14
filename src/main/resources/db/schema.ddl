
    create table mydb.price (
        id varchar(45) not null unique,
        ask decimal(15,4),
        bid decimal(15,4),
        symbol varchar(20),
        time datetime,
        vol decimal(15,4),
        primary key (id),
        unique (time, symbol)
    );
