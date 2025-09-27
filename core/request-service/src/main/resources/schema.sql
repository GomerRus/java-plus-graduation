CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    requester_id BIGINT  NOT NULL,
    event_id     BIGINT  NOT NULL,
    status       INTEGER NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT created_not_null CHECK (created <> NULL)
);