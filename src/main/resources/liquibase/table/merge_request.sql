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

--changeset reybos:2 runOnChange:true
COMMENT ON TABLE merge_request IS 'Request from one user to another to merge lists';
COMMENT ON COLUMN merge_request.user_id IS 'The user who wants to join another list';
COMMENT ON COLUMN merge_request.owner_id IS 'The owner of the list they want to join';
COMMENT ON COLUMN merge_request.approved IS 'Has the application been approved';
COMMENT ON COLUMN merge_request.expired IS 'Has the application been expired';