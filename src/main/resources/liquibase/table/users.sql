--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    telegram_id   BIGINT UNIQUE NOT NULL,
    user_name     VARCHAR(50)   NOT NULL,
    first_name    TEXT          NOT NULL,
    language_code VARCHAR(5)    NOT NULL,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

--changeset reybos:users_add_blocked_column
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS blocked BOOLEAN DEFAULT false NOT NULL;

--changeset reybos:users_add_join_request_disabled_column
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS join_request_disabled BOOLEAN DEFAULT false NOT NULL;

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE users IS 'Users who have used the bot at least once';
COMMENT ON COLUMN users.telegram_id IS 'User id in telegram';
COMMENT ON COLUMN users.user_name IS 'User name in telegram';
COMMENT ON COLUMN users.first_name IS 'First name in telegram';
COMMENT ON COLUMN users.language_code IS 'User language';
COMMENT ON COLUMN users.blocked IS 'User has blocked the bot';