--liquibase formatted sql

--changeset reybos:1
CREATE TABLE IF NOT EXISTS merge_request
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES users (id) NOT NULL,
    owner_id   BIGINT REFERENCES users (id) NOT NULL,
    approved   BOOLEAN                      NOT NULL,
    expired    BOOLEAN                      NOT NULL,
    created_at TIMESTAMPTZ                  NOT NULL DEFAULT NOW()
);

--changeset reybos:merge_request-add_rejected_column
ALTER TABLE merge_request
    ADD COLUMN IF NOT EXISTS rejected BOOLEAN,
    ADD COLUMN IF NOT EXISTS message_id INT;
UPDATE merge_request SET rejected = false WHERE rejected IS NULL;
UPDATE merge_request SET message_id = -1 WHERE message_id IS NULL;
ALTER TABLE merge_request
    ALTER COLUMN rejected SET NOT NULL,
    ALTER COLUMN message_id SET NOT NULL;

--changeset reybos:merge_request-rename_table
ALTER TABLE merge_request RENAME TO join_request;

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE join_request IS 'Request from one user to another to merge lists';
COMMENT ON COLUMN join_request.user_id IS 'The user who wants to join another list';
COMMENT ON COLUMN join_request.owner_id IS 'The owner of the list they want to join';
COMMENT ON COLUMN join_request.approved IS 'Has the application been approved';
COMMENT ON COLUMN join_request.expired IS 'Has the application been expired';
COMMENT ON COLUMN join_request.rejected IS 'Has the application been rejected';