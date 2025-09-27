CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name  VARCHAR(250) NOT NULL,
    user_email VARCHAR(254) NOT NULL UNIQUE,
    CONSTRAINT user_email_len CHECK (LENGTH(user_email) >= 6 AND LENGTH(user_email) <= 254),
    CONSTRAINT user_name_len CHECK (LENGTH(user_name) >= 2 AND LENGTH(user_name) <= 250)
);
