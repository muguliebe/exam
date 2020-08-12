drop table if exists fwk_transaction_hst;
create table fwk_transaction_hst
(
    tr_dy            date                     not null,
    gid              varchar(255)             not null,
    method           varchar(6),
    path             varchar(200),
    status_code      varchar(3),
    start_time       varchar(6),
    end_time         varchar(6),
    elapsed          varchar(11),
    remote_ip        varchar(20),
    query_string     varchar(4000),
    body             varchar(4000),
    referrer         varchar(200),
    error_message    varchar(2000),
    create_user_id   varchar(11),
    create_dt        timestamp with time zone not null
);

CREATE INDEX idx_tr_hst ON fwk_transaction_hst (tr_dy);
