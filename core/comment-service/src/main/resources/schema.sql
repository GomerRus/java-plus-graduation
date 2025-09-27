CREATE TABLE IF NOT EXISTS comments
(
    comment_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id       BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    text            VARCHAR(5000) NOT NULL,
    created_date    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT text_len CHECK (LENGTH(text) >= 1 AND LENGTH(text) <= 5000),
    CONSTRAINT created_date_not_null CHECK (created_date <> NULL)
);