CREATE table operation_log_record(
    id NUMBER primary key ,
    update_user varchar2(50),
    update_date timestamp ,
    op_content VARCHAR2(2000),
    op_user_ip varchar(50),
    op_type varchar(50),
    op_module varchar2(50),
    op_resule varchar2(50),
    ext_attr1 varchar2(50),
    ext_attr2 varchar2(50),
    ext_attr3 varchar2(50),
    ext_attr4 varchar2(50),
    ext_attr5 varchar2(50),
    ext_attr6 varchar2(1000),
    ext_attr7 varchar2(1000),
    ext_attr8 varchar2(1000)
);

create table mid_user_idd(
oa_id number primary key ,
idd varchar2(50)
);




