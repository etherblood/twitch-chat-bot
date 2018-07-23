CREATE SEQUENCE id_seq;

CREATE TABLE command
(
    id bigint PRIMARY KEY DEFAULT nextval('id_seq'),
    alias text NOT NULL,
    code text NOT NULL,
    author text NOT NULL,
    usecount int NOT NULL,
    lastused timestamp,
    lastmodified timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);
CREATE UNIQUE INDEX command_lower_alias_unique_idx on command (LOWER(alias));

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



CREATE TABLE defaultvalues
(
    id bigint DEFAULT nextval('id_seq'),
    tag text
);

insert into defaultvalues (tag) values ('set'),('permit'),('unpermit'),('tag'),('untag'),('list'),('show'),('tags');
insert into command (code, alias, author, usecount, lastused, lastmodified, id) select '[' || tag || '][regex].*[/regex][/' || tag || ']', tag, '<system>', 0, null, now(), id from defaultvalues;
insert into commandtag (tag, command_id) select 'core', id from defaultvalues;
drop table defaultvalues;
