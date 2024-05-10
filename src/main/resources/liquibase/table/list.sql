--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS list
(
    id         BIGSERIAL PRIMARY KEY,
    owner_id   BIGINT REFERENCES users (id) NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL DEFAULT NOW()
);

--changeset reybos:3
ALTER TABLE list
    DROP COLUMN IF EXISTS owner_id;

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE list IS 'Shopping list';