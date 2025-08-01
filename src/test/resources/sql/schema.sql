CREATE SCHEMA IF NOT EXISTS sync;
CREATE SCHEMA IF NOT EXISTS data;

CREATE TABLE IF NOT EXISTS sync.msisdn_data_snapshot(
    id           bigserial PRIMARY KEY,
    msisdn       bigint    NOT NULL UNIQUE,
    parsed_at    timestamp NOT NULL,
    operator_id  integer   NOT NULL,
    file_id      integer   NOT NULL,
    row_num      integer   NOT NULL,
    is_valid     boolean   NOT NULL,
    is_processed boolean   NOT NULL DEFAULT false,
    sync_done    boolean   NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS sync.sync_log(
    id          bigserial PRIMARY KEY,
    sync_type   text      NOT NULL,
    started_at  timestamp NOT NULL,
    finished_at timestamp,
    status      text      NOT NULL ,
    error       text
);

CREATE TABLE IF NOT EXISTS data.sim_cards(
    id        bigserial PRIMARY KEY,
    msisdn    bigint    NOT NULL UNIQUE,
    parsed_at timestamp NOT NULL
);