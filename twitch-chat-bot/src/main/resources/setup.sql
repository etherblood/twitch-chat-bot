CREATE TABLE command
(
    id text PRIMARY KEY,
    code text NOT NULL,
    author text NOT NULL,
    usecount int NOT NULL,
    lastused timestamp,
    lastmodified timestamp NOT NULL,
    UNIQUE (lower(id))
)
WITH (
  OIDS=FALSE
);

CREATE TABLE whitelist
(
    sender text PRIMARY KEY
)
WITH (
  OIDS=FALSE
);