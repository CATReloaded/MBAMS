# --- First database schema

# --- !Ups

create table student (
  id                        bigint not null AUTO_INCREMENT,
  student_id                VARCHAR(8) not null UNIQUE,
  name                      varchar(255) not null,
  mac                       varchar(17)  UNIQUE,
  one                       varchar(255) DEFAULT '-',
  two                       varchar(255) DEFAULT '-',
  three                     varchar(255) DEFAULT '-',
  four                      varchar(255) DEFAULT '-',
  five                      varchar(255) DEFAULT '-',
  six                       varchar(255) DEFAULT '-',
  seven                     varchar(255) DEFAULT '-',
  eight                     varchar(255) DEFAULT '-',

  constraint pk_student primary key (id))
;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists student;

SET REFERENTIAL_INTEGRITY TRUE;
