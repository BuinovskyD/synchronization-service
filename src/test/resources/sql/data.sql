INSERT INTO data.msisdns_data(msisdn, parsed_at, operator_id, file_id, row_num)
VALUES
    (9121690789, '2025-08-01 14:00:00'::timestamp, 2, 14, 270),
    (9121720330, '2025-08-02 15:00:00'::timestamp, 3, 22, 340),
    (9121756199, '2025-08-03 16:00:00'::timestamp, 4, 17, 826);

INSERT INTO data.invalid_rows_data(msisdn, parsed_at, operator_id, file_id, row_num)
VALUES (9121720330, '2025-08-04 17:00:00'::timestamp, 5, 67, 92),
       (9121720330, '2025-08-04 18:00:00'::timestamp, 5, 70, 184),
       (9097862815, '2025-08-05 08:00:00'::timestamp, 6, 34, 48);