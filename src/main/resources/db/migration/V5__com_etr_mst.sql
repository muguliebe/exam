drop table if exists com_etr_mst;
create table com_etr_mst
(
    seq            serial primary key,
    tr_dy          date default now(),
    tr_id          varchar(2),
    tr_stat_cd     varchar(2) default '01',
    tr_res_cd      varchar(3) default '00',
    user_id        integer,
    body           json,
    create_user_id integer,
    update_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    update_dt      timestamp with time zone
);

create index idx_com_etr_mst_1 on com_etr_mst (tr_id, seq);
create index idx_com_etr_mst_2 on com_etr_mst (user_id);
create index idx_com_etr_mst_3 on com_etr_mst (tr_dy, user_id);

comment on table com_etr_mst is '대외 거래 마스터';
comment on column com_etr_mst.tr_id is '거래 ID';
comment on column com_etr_mst.seq is '순번';
comment on column com_etr_mst.tr_stat_cd is '거래상태코드 [01:요청, 02: 전송, 03:타임아웃, 04:취소, 05:수신]';
comment on column com_etr_mst.tr_res_cd is '거래결과코드 [00:진행중, 01:성공, 02:실패]';
comment on column com_etr_mst.user_id is '사용자 ID';


