# --- First database schema

# --- !Ups

create table student (
  id                        bigint not null AUTO_INCREMENT,
  student_id           bigint ,
  name                      varchar(255),
  mac                       varchar(255),
  one                       varchar(255) DEFAULT 'absent',
  two                       varchar(255) DEFAULT 'absent',
  three                     varchar(255) DEFAULT 'absent',
  four                      varchar(255) DEFAULT 'absent',
  five                      varchar(255) DEFAULT 'absent',
  six                       varchar(255) DEFAULT 'absent',
  seven                     varchar(255) DEFAULT 'absent',
  eight                     varchar(255) DEFAULT 'absent',

  constraint pk_student primary key (id))
;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists student;

SET REFERENTIAL_INTEGRITY TRUE;
