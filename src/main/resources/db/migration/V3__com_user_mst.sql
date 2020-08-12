drop table if exists com_user_mst;
create table com_user_mst
(
    user_id        serial constraint com_user_mst_pk primary key,
    email          varchar(30),
    create_user_id bigint,
    update_user_id integer,
    create_dt      timestamp with time zone default CURRENT_TIMESTAMP not null,
    update_dt      timestamp with time zone
);

comment on table com_user_mst is '사용자 마스터';
comment on column com_user_mst.user_id is '사용자 ID';
comment on column com_user_mst.email is '이메일';


insert into com_user_mst( email , create_user_id ) values ( '1@a.com' ,0);
insert into com_user_mst( email , create_user_id ) values ( '2@a.com' ,0);
insert into com_user_mst( email , create_user_id ) values ( '3@a.com' ,0);
insert into com_user_mst( email , create_user_id ) values ( '4@a.com' ,0);
insert into com_user_mst( email , create_user_id ) values ( '5@a.com' ,0);
