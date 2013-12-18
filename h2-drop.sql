SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists authentication;

drop table if exists folder;

drop table if exists history;

drop table if exists pii;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists authentication_seq;

drop sequence if exists folder_seq;

drop sequence if exists history_seq;

drop sequence if exists pii_seq;

