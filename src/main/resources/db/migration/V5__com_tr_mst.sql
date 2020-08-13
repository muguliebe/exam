drop table if exists com_etr_mst;
create table com_etr_mst
(
    tr_cd          varchar(2),
    seq            serial,
    tr_stat_cd     varchar(2),
    tr_res_code    varchar(3),
    create_user_id integer,
    update_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    update_dt      timestamp with time zone,
        constraint com_etr_mst_pk
        primary key (tr_cd, seq)
);

comment on table com_etr_mst is '대외 거래 마스터';
comment on column com_etr_mst.tr_cd is '거래코드';
comment on column com_etr_mst.seq is '순번';
comment on column com_etr_mst.tr_stat_cd is '거래상태코드 [01:요청, 02: 완료]';
comment on column com_etr_mst.tr_res_code is '거래응답코드 (공통코드 아님) ';

