CREATE TABLE IF NOT EXISTS actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE,
    UNIQUE (user_id, event_id, action_type)
);

CREATE TABLE IF NOT EXISTS similarities (
    event_a_id BIGINT NOT NULL,
    event_b_id BIGINT NOT NULL,
    rating FLOAT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (event_a_id, event_b_id)
);