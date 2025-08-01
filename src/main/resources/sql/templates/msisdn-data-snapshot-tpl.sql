WITH invalid_snapshot AS (
    SELECT DISTINCT ON (msisdn) msisdn, parsed_at, operator_id, file_id, row_num, false AS is_valid
    FROM data.invalid_rows_data
    WHERE msisdn IS NOT NULL
    ORDER BY msisdn, parsed_at DESC
), valid_snapshot AS (
    SELECT msisdn, parsed_at, operator_id, file_id, row_num, true AS is_valid
    FROM data.msisdns_data
), combined AS (
    SELECT * FROM invalid_snapshot UNION
    SELECT * FROM valid_snapshot
)
SELECT DISTINCT ON (msisdn) msisdn, parsed_at, operator_id, file_id, row_num, is_valid
FROM combined
ORDER BY msisdn, parsed_at DESC;