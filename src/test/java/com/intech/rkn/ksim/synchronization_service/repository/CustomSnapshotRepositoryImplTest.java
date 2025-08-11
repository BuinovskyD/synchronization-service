package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.config.Configuration;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.repository.impl.CustomSnapshotRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.thymeleaf.ITemplateEngine;
import java.time.LocalDateTime;

import static com.intech.rkn.ksim.synchronization_service.TestUtils.TEST_MSISDN_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Slf4j
@SpringBootTest
@Import(Configuration.class)
class CustomSnapshotRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ITemplateEngine templateEngine;
    @Autowired
    private MsisdnDataSnapshotRepository snapshotRepository;

    private CustomSnapshotRepository repositoryUnderTests;

    @BeforeEach
    void setUp() {
        this.repositoryUnderTests = new CustomSnapshotRepositoryImpl(templateEngine, jdbcTemplate);
    }

    @AfterEach
    void cleanUp() {
        cleanUpTables();
    }

    @Test
    @Sql(scripts = "/sql/snapshot-test-data-1.sql", executionPhase = BEFORE_TEST_METHOD)
    void shouldReturnSnapshotWithCorrectInvalidMsisdn() {
        repositoryUnderTests.create();

        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().hasSize(3);

        assertThat(result).filteredOn(item -> item.msisdn().equals(TEST_MSISDN_2))
                .isNotEmpty().hasSize(1)
                .satisfiesExactly(item -> {
                    assertThat(item.isValid()).isFalse();
                    assertThat(item.parsedAt()).isEqualTo(LocalDateTime.of(2025,8,4,18,0,0));
                    assertThat(item.operatorId()).isEqualTo(5);
                    assertThat(item.fileId()).isEqualTo(70);
                });

        assertThat(result).filteredOn(item -> !item.msisdn().equals(TEST_MSISDN_2))
                .isNotEmpty().hasSize(2)
                .allSatisfy(item -> assertThat(item.isValid()).isTrue());
    }

    @Test
    @Sql(scripts = "/sql/snapshot-test-data-2.sql", executionPhase = BEFORE_TEST_METHOD)
    void shouldReturnSnapshotOnlyWithValidMsisdns() {
        repositoryUnderTests.create();

        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().hasSize(3)
                .allSatisfy(item -> assertThat(item.isValid()).isTrue());
    }

    @Test
    @Sql(scripts = "/sql/snapshot-test-data-3.sql", executionPhase = BEFORE_TEST_METHOD)
    void shouldReturnSnapshotOnlyWithInvalidMsisdns() {
        repositoryUnderTests.create();

        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().hasSize(2)
                .allSatisfy(item -> assertThat(item.isValid()).isFalse());
    }

    private void cleanUpTables() {
        snapshotRepository.deleteAll();
        jdbcTemplate.execute("truncate data.msisdns_data");
        jdbcTemplate.execute("truncate data.invalid_rows_data");
    }
}