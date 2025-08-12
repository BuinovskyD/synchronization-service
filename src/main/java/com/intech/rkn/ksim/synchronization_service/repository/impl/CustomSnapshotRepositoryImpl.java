package com.intech.rkn.ksim.synchronization_service.repository.impl;

import com.intech.rkn.ksim.synchronization_service.repository.AbstractRepository;
import com.intech.rkn.ksim.synchronization_service.repository.CustomSnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;

@Slf4j
@Repository
public class CustomSnapshotRepositoryImpl extends AbstractRepository implements CustomSnapshotRepository {

    private final String CLEAN_UP_SQL;
    private final String VALID_MSISDN_DATA_SQL;
    private final String INVALID_MSISDN_DATA_SQL;

    private final JdbcTemplate jdbcTemplate;

    public CustomSnapshotRepositoryImpl(ITemplateEngine templateEngine,
                                        JdbcTemplate jdbcTemplate) {
        super(templateEngine);
        this.jdbcTemplate = jdbcTemplate;
        this.CLEAN_UP_SQL = getSql("clean-msisdn-data-snapshot-table-tpl");
        this.VALID_MSISDN_DATA_SQL = getSql("valid-msisdn-data-tpl");
        this.INVALID_MSISDN_DATA_SQL = getSql("invalid-msisdn-data-tpl");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create() {
        log.info("cleaning up msisdns data snapshot table");
        jdbcTemplate.execute(CLEAN_UP_SQL);
        log.info("inserting valid msisdns data to snapshot table");
        jdbcTemplate.execute(VALID_MSISDN_DATA_SQL);
        log.info("inserting invalid msisdns data to snapshot table");
        jdbcTemplate.execute(INVALID_MSISDN_DATA_SQL);
    }
}