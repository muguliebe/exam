drop table if exists act_stg_mst;
create table act_stg_mst
(
    user_id        integer constraint act_stg_mst_pk primary key,
    acct_stg_cd    varchar(2),
    etc_ctn        varchar(4),
    create_user_id integer,
    update_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    update_dt      timestamp with time zone
);

comment on table act_stg_mst is '계좌 단계 내역';
comment on column act_stg_mst.user_id is '사용자 ID';
comment on column act_stg_mst.acct_stg_cd is '계좌 단계 코드';
comment on column act_stg_mst.etc_ctn is '적요 내용 (인증단어)';

