package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.config.Configuration;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.repository.impl.CustomSnapshotRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.thymeleaf.ITemplateEngine;

import static com.intech.rkn.ksim.synchronization_service.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

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
        snapshotRepository.deleteAll();
    }

    @Test
    void test() {
        repositoryUnderTests.create();
        Iterable<MsisdnData> all = snapshotRepository.findAll();
        assertThat(all).isNotEmpty();
    }
}