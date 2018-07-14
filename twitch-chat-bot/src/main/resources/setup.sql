CREATE SEQUENCE command_id_seq;

CREATE TABLE command
(
    id bigint PRIMARY KEY DEFAULT nextval('command_id_seq'),
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
    alias text PRIMARY KEY,
    command_id bigint NOT NULL REFERENCES command (id) ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
CREATE UNIQUE INDEX commandalias_lower_alias_unique_idx on commandalias (LOWER(alias));

CREATE TABLE clip
(
    id text PRIMARY KEY,
    code text NOT NULL,
    author text NOT NULL,
    usecount int NOT NULL,
    lastused timestamp,
    lastmodified timestamp NOT NULL
)
WITH (
  OIDS=FALSE
);
CREATE UNIQUE INDEX clip_lower_id_unique_idx on clip (LOWER(id));

CREATE TABLE whitelist
(
    sender text PRIMARY KEY
)
WITH (
  OIDS=FALSE
);