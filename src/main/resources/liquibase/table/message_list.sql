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

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE message_list IS 'The messages in which the list was sent';
COMMENT ON COLUMN message_list.chat_id IS 'The ID of the chat in which the list was sent, it is also the user id';
COMMENT ON COLUMN message_list.list_id IS 'The ID of list';
COMMENT ON COLUMN message_list.message_id IS 'The ID of the message in which the list was sent';