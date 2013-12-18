create table authentication (
  id                        bigint not null,
  role                      varchar(255),
  password                  varchar(255),
  constraint uq_authentication_password unique (password),
  constraint pk_authentication primary key (id))
;

create table folder (
  id                        bigint not null,
  usage                     varchar(255),
  path                      varchar(255),
  constraint pk_folder primary key (id))
;

create table history (
  id                        bigint not null,
  file_name                 varchar(255),
  constraint uq_history_file_name unique (file_name),
  constraint pk_history primary key (id))
;

create table pii (
  id                        bigint not null,
  編碼日期                      varchar(255),
  guid                      varchar(255),
  mrn                       varchar(255),
  身份證字號                     varchar(255),
  姓氏                        varchar(255),
  名字                        varchar(255),
  出生月                       varchar(255),
  出生日                       varchar(255),
  出生年                       varchar(255),
  性別                        varchar(255),
  聯絡電話                      varchar(255),
  地址                        varchar(255),
  收案醫師                      varchar(255),
  收案醫院名稱                    varchar(255),
  constraint pk_pii primary key (id))
;

create sequence authentication_seq;

create sequence folder_seq;

create sequence history_seq;

create sequence pii_seq;



