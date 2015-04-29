#--- First database schema

#--- !Ups

create table student (
  id                        bigint not null AUTO_INCREMENT,
  name                      varchar(255),
  mac                       varchar(255) UNIQUE,
  one                       BOOL,
  two                       BOOL,
  three                     BOOL,
  four                      BOOL,
  five                      BOOL,
  six                       BOOL,
  seven                     BOOL,
  eight                     BOOL,

  constraint pk_student primary key (id))
;

#--- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists student;

SET REFERENTIAL_INTEGRITY TRUE;