--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS message_list
(
    id         BIGSERIAL PRIMARY KEY,
    chat_id    BIGINT REFERENCES users (id) NOT NULL,
    list_id    BIGINT REFERENCES list (id)  NOT NULL,
    message_id INT                          NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL DEFAULT NOW()
);

--changeset reybos:message_list-change_references
ALTER TABLE message_list
    DROP CONSTRAINT message_list_chat_id_fkey,
    ADD FOREIGN KEY (chat_id) REFERENCES users (telegram_id);

--changeset reybos:message_list-add_user_id_column
ALTER TABLE message_list
    ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users (id);

UPDATE message_list
SET user_id = users.id
FROM users
WHERE users.telegram_id = message_list.chat_id;

ALTER TABLE message_list
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE message_list
    DROP COLUMN IF EXISTS chat_id;

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE message_list IS 'The messages in which the list was sent';
COMMENT ON COLUMN message_list.user_id IS 'The ID of the user in whom the list was sent, it is also the user id';
COMMENT ON COLUMN message_list.list_id IS 'The ID of list';
COMMENT ON COLUMN message_list.message_id IS 'The ID of the message in which the list was sent';