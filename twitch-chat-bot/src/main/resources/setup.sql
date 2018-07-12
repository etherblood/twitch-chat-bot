CREATE TABLE command
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
CREATE UNIQUE INDEX command_lower_id_unique_idx on command (LOWER(id));

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