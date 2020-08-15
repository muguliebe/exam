drop table if exists com_push_mst;
create table com_push_mst
(
    seq            serial primary key,
    push_id        varchar(2),
    user_id        integer,
    dlv_yn         varchar(1) default 'N',
    create_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null
);

create index idx_com_push_mst on com_push_mst (push_id, seq);

comment on table com_push_mst is '대외 거래 마스터';
comment on column com_push_mst.seq is '순번';
comment on column com_push_mst.push_id is '푸쉬 ID';
comment on column com_push_mst.user_id is '사용자 ID';
comment on column com_push_mst.dlv_yn is '전달 여부';
comment on column com_push_mst.create_user_id is '생성자 ID';
comment on column com_push_mst.create_dt is '생성일시';


