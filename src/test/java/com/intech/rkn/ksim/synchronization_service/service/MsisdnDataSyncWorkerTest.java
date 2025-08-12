package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.config.Configuration;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.repository.MsisdnDataSnapshotRepository;
import com.intech.rkn.ksim.synchronization_service.repository.SimCardRepository;
import com.intech.rkn.ksim.synchronization_service.service.worker.MsisdnDataSyncWorker;
import com.intech.rkn.ksim.synchronization_service.service.worker.SyncWorkersFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import static com.intech.rkn.ksim.synchronization_service.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Import(Configuration.class)
class MsisdnDataSyncWorkerTest {

    @Autowired
    private SyncWorkersFactory workersFactory;
    @Autowired
    private MsisdnDataSnapshotRepository snapshotRepository;
    @Autowired
    private SimCardRepository simCardRepository;

    private MsisdnDataSyncWorker workerUnderTests;

    @BeforeEach
    void setUp() {
        this.workerUnderTests = workersFactory.getMsisdnDataSyncWorker(new CountDownLatch(1));
        clearTables();
        snapshotRepository.saveAll(getSnapshot());
    }

    @Test
    void shouldNotPerformSyncDueDatesEquality() {
        simCardRepository.saveAll(getSimCardsWithEqualDates());

        workerUnderTests.run();
        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().allSatisfy(item -> {
            assertThat(item.isProcessed()).isTrue();
            assertThat(item.syncDone()).isFalse();
        });
    }

    @Test
    void shouldNotPerformSyncDueLagOfDatesLessThenOneDay() {
        simCardRepository.saveAll(getSimCardsWithLagDatesLessThenOneDay());

        workerUnderTests.run();
        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().allSatisfy(item -> {
            assertThat(item.isProcessed()).isTrue();
            assertThat(item.syncDone()).isFalse();
        });
    }

    @Test
    void shouldPerformSyncDueLagOfDatesMoreThenOneDay() {
        simCardRepository.saveAll(getSimCardsWithLagDatesMoreThenOneDay());

        workerUnderTests.run();
        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().allSatisfy(item -> {
            assertThat(item.isProcessed()).isTrue();
            assertThat(item.syncDone()).isTrue();
        });
    }

    @Test
    void shouldPerformSyncDueSimCardsDoesNotExists() {
        simCardRepository.saveAll(Collections.emptyList());

        workerUnderTests.run();
        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty().allSatisfy(item -> {
            assertThat(item.isProcessed()).isTrue();
            assertThat(item.syncDone()).isTrue();
        });
    }

    @Test
    void shouldPerformSyncForSingleSimCardDueItsLagOfDateMoreThenOneDay() {
        simCardRepository.saveAll(getSimCardsWithLagDatesMoreThenOneDayForSingleSimCard());

        workerUnderTests.run();
        Iterable<MsisdnData> result = snapshotRepository.findAll();

        assertThat(result).isNotEmpty()
                .filteredOn(MsisdnData::syncDone)
                .satisfiesExactly(item -> {
                    assertThat(item.msisdn()).isEqualTo(TEST_MSISDN_2);
                    assertThat(item.isProcessed()).isTrue();
                });

        assertThat(result)
                .filteredOn(item -> !item.syncDone())
                .allSatisfy(item -> {
                    assertThat(item.isProcessed()).isTrue();
                    assertThat(item.syncDone()).isFalse();
                });
    }

    private void clearTables() {
        snapshotRepository.deleteAll();
        simCardRepository.deleteAll();
    }
}