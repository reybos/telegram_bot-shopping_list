--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS users_list
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id) NOT NULL,
    list_id    BIGINT REFERENCES list (id)  NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL DEFAULT NOW()
);

--changeset reybos:3
ALTER TABLE users_list
    ADD COLUMN IF NOT EXISTS owner  BOOLEAN DEFAULT false NOT NULL,
    ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT false NOT NULL;

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE users_list IS 'What is the main list of users now';
COMMENT ON COLUMN users_list.user_id IS 'The user who uses the list';
COMMENT ON COLUMN users_list.list_id IS 'The list used';
COMMENT ON COLUMN users_list.owner IS 'Is the user the owner of the list';
COMMENT ON COLUMN users_list.active IS 'Is the list currently in use';