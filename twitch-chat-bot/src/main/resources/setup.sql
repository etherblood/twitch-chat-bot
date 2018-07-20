CREATE SEQUENCE id_seq;

CREATE TABLE command
(
    id bigint PRIMARY KEY DEFAULT nextval('id_seq'),
    code text NOT NULL,
    author text NOT NULL,
    usecount int NOT NULL,
    lastused timestamp,
    lastmodified timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);

CREATE TABLE commandalias
(
    id bigint PRIMARY KEY DEFAULT nextval('id_seq'),
    alias text,
    command_id bigint NOT NULL REFERENCES command (id) ON DELETE CASCADE,
    author text NOT NULL,
    usecount int NOT NULL,
    lastused timestamp,
    lastmodified timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);
CREATE UNIQUE INDEX commandalias_lower_alias_unique_idx on commandalias (LOWER(alias));

CREATE SEQUENCE commandtag_id_seq;

CREATE TABLE commandtag
(
    id bigint PRIMARY KEY DEFAULT nextval('id_seq'),
    tag text,
    command_id bigint NOT NULL REFERENCES command (id) ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
CREATE UNIQUE INDEX commandtag_lower_tag_command_id_unique_idx on commandtag (LOWER(tag), command_id);

CREATE TABLE whitelist
(
    sender text PRIMARY KEY
)
WITH (
  OIDS=FALSE
);




CREATE TABLE tmp
(
    id bigint DEFAULT nextval('id_seq'),
    tag text
);

insert into tmp (tag) values ('set'),('permit'),('unpermit'),('alias'),('tag'),('untag'),('list'),('show'),('tags');
insert into command (code, author, usecount, lastused, lastmodified, id) select '[' || tag || '][regex].*[/regex][/' || tag || ']', 'Etherblood', 0, null, now(), id from tmp;
insert into commandalias (alias, author, usecount, lastused, lastmodified, command_id) select tag, 'Etherblood', 0, null, now(), id from tmp;
insert into commandtag (tag, command_id) select 'core', id from tmp;
drop table tmp;
