WITH invalid_snapshot AS (
    SELECT DISTINCT ON (msisdn) msisdn, parsed_at, operator_id, file_id, row_num, false AS is_valid
    FROM data.invalid_rows_data
    WHERE msisdn IS NOT NULL
    ORDER BY msisdn, parsed_at DESC
)
INSERT INTO sync.msisdn_data_snapshot (msisdn, parsed_at, operator_id, file_id, row_num, is_valid)
SELECT * FROM invalid_snapshot
ON CONFLICT (msisdn) DO UPDATE
    SET parsed_at =   excluded.parsed_at,
        operator_id = excluded.operator_id,
        file_id =     excluded.file_id,
        row_num =     excluded.row_num,
        is_valid =    excluded.is_valid
WHERE msisdn_data_snapshot.parsed_at < excluded.parsed_at;