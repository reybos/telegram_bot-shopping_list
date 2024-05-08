--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS users_list
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id),
    list_id    BIGINT REFERENCES list (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE users_list IS 'What is the main list of users now';
COMMENT ON COLUMN users_list.user_id IS 'The user who uses the list';
COMMENT ON COLUMN users_list.list_id IS 'The list used';