INSERT INTO sync.msisdn_data_snapshot(msisdn, parsed_at, operator_id, file_id, row_num, is_valid)
VALUES
    (9121690789, '2025-07-05 14:11:23'::timestamp, 2, 14, 270, true),
    (9121720330, '2025-07-17 08:34:11'::timestamp, 4, 22, 340, true),
    (9121756199, '2025-07-29 15:18:55'::timestamp, 7, 17, 826, false);

-- 1) ОК
-- INSERT INTO data.sim_cards(msisdn, parsed_at)
-- VALUES
--     (9121690789, '2025-07-05 14:11:23'::timestamp),
--     (9121720330, '2025-07-17 08:34:11'::timestamp),
--     (9121756199, '2025-07-29 15:18:55'::timestamp);

-- 2-1) ОК
-- INSERT INTO data.sim_cards(msisdn, parsed_at)
-- VALUES
--     (9121690789, '2025-07-05 07:11:23'::timestamp),
--     (9121720330, '2025-07-17 08:00:11'::timestamp),
--     (9121756199, '2025-07-29 10:18:55'::timestamp);

-- 2-2) ОК
INSERT INTO data.sim_cards(msisdn, parsed_at)
VALUES
    (9121690789, '2025-07-01 14:11:23'::timestamp),
    (9121720330, '2025-07-11 08:34:11'::timestamp),
    (9121756199, '2025-07-21 15:18:55'::timestamp);

-- 2-3) ОК
-- INSERT INTO data.sim_cards(msisdn, parsed_at)
-- VALUES
--     (9121690789, '2025-07-05 14:11:23'::timestamp),
--     (9121720330, '2025-07-17 08:34:11'::timestamp),
--     (9121756199, '2025-07-28 15:18:54'::timestamp);

