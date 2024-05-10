--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS list_item
(
    id         BIGSERIAL PRIMARY KEY,
    list_id    BIGINT REFERENCES list (id) NOT NULL,
    value      VARCHAR(30)                 NOT NULL,
    created_at TIMESTAMPTZ                 NOT NULL DEFAULT NOW()
);

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE list_item IS 'User records';
COMMENT ON COLUMN list_item.list_id IS 'The list that the record belongs to';
COMMENT ON COLUMN list_item.value IS 'The value of the list item';