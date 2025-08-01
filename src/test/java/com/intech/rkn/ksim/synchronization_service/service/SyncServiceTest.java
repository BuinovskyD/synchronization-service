package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.model.SyncLog;
import com.intech.rkn.ksim.synchronization_service.repository.MsisdnDataSnapshotRepository;
import com.intech.rkn.ksim.synchronization_service.repository.SyncLogRepository;
import com.intech.rkn.ksim.synchronization_service.service.impl.MsisdnDataSynchronizer;
import com.intech.rkn.ksim.synchronization_service.service.impl.SyncServiceImpl;
import com.intech.rkn.ksim.synchronization_service.utils.SyncAlreadyRunningException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.intech.rkn.ksim.synchronization_service.TestUtils.STARTED_SYNC_LOG_ENTITY;
import static com.intech.rkn.ksim.synchronization_service.TestUtils.getWorkerProperties;
import static com.intech.rkn.ksim.synchronization_service.enums.SyncStatus.STARTED;
import static com.intech.rkn.ksim.synchronization_service.enums.SyncType.MSISDN_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock
    private SyncLogRepository logRepository;
    private SyncService serviceUnderTests;

    @BeforeEach
    void setUp() {
        setupMockInfrastructure();
    }

    @Test
    void shouldThrowsSyncAlreadyRunningException() {
        SyncLog syncLog = SyncLog.builder().status(STARTED).build();

        when(logRepository.findLastByType(any())).thenReturn(Optional.of(syncLog));

        assertThatThrownBy(() -> serviceUnderTests.runSynchronization(MSISDN_DATA))
                .isInstanceOf(SyncAlreadyRunningException.class)
                .hasMessageContaining("MSISDN_DATA sync already running");
    }

    @Test
    void shouldReturnIdOfStartedSync() {
        when(logRepository.findLastByType(any(SyncType.class))).thenReturn(Optional.empty());
        when(logRepository.save(any(SyncLog.class))).thenReturn(STARTED_SYNC_LOG_ENTITY);

        long result = serviceUnderTests.runSynchronization(MSISDN_DATA);

        assertThat(result).isEqualTo(1L);
    }

    private void setupMockInfrastructure() {
        ExecutorService executorServiceMock = Mockito.mock(ExecutorService.class);
        SyncWorkersFactory workersFactoryMock = Mockito.mock(SyncWorkersFactory.class);
        MsisdnDataSnapshotRepository snapshotRepositoryMock = Mockito.mock(MsisdnDataSnapshotRepository.class);
        List<Synchronizer> synchronizers = List.of(
                new MsisdnDataSynchronizer(getWorkerProperties(), workersFactoryMock, snapshotRepositoryMock));

        this.serviceUnderTests = new SyncServiceImpl(executorServiceMock, logRepository, synchronizers);
    }
}