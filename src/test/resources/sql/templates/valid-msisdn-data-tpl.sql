INSERT INTO sync.msisdn_data_snapshot (msisdn, parsed_at, operator_id, file_id, row_num, is_valid)
SELECT msisdn, parsed_at, operator_id, file_id, row_num, true AS is_valid
FROM data.msisdns_data;
