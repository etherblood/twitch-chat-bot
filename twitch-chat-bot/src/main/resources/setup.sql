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