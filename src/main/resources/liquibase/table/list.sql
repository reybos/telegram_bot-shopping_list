--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS list
(
    id         BIGSERIAL PRIMARY KEY,
    owner_id   BIGINT REFERENCES users (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE list IS 'Shopping list';
COMMENT ON COLUMN list.owner_id IS 'The user who owns the list';