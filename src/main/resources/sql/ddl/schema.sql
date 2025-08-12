CREATE SCHEMA IF NOT EXISTS sync;

CREATE TABLE IF NOT EXISTS sync.msisdn_data_snapshot(
    id           bigserial PRIMARY KEY,
    msisdn       bigint    NOT NULL UNIQUE,
    parsed_at    timestamp NOT NULL,
    operator_id  integer   NOT NULL,
    file_id      integer   NOT NULL,
    row_num      integer,
    is_valid     boolean   NOT NULL,
    is_processed boolean   NOT NULL DEFAULT false,
    sync_done    boolean   NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS sync.mnp_full_comparison(
    msisdn            bigint    NOT NULL PRIMARY KEY,
    mnp_id            integer,
    mnp_code          text      NOT NULL,
    mnp_mnc           integer   NOT NULL,
    donor_id          integer,
    donor_code        text      NOT NULL,
    donor_mnc         integer   NOT NULL,
    range_holder_id   integer,
    range_holder_code text      NOT NULL,
    mnp_port_date     timestamp NOT NULL,
    process_type      text      NOT NULL,
    ksim_id           integer,
    ksim_code         text,
    ksim_mnc          integer,
    ksim_port_date    timestamp,
    is_synchronized   boolean   NOT NULL DEFAULT false,
    comparison_date   timestamp
);

CREATE TABLE IF NOT EXISTS sync.sync_log(
    id          bigserial PRIMARY KEY,
    sync_type   text      NOT NULL,
    started_at  timestamp NOT NULL,
    finished_at timestamp,
    status      text      NOT NULL ,
    error       text
);