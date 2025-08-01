package com.intech.rkn.ksim.synchronization_service.service.impl;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.repository.MsisdnDataSnapshotRepository;
import com.intech.rkn.ksim.synchronization_service.service.Synchronizer;
import com.intech.rkn.ksim.synchronization_service.service.SyncWorkersFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.intech.rkn.ksim.synchronization_service.enums.SyncType.MSISDN_DATA;

@Slf4j
@Component
public class MsisdnDataSynchronizer implements Synchronizer {

    private final AppProperties properties;
    private final ExecutorService workersPool;
    private final SyncWorkersFactory workersFactory;
    private final MsisdnDataSnapshotRepository snapshotRepository;

    public MsisdnDataSynchronizer(AppProperties properties,
                                  SyncWorkersFactory workersFactory,
                                  MsisdnDataSnapshotRepository snapshotRepository) {
        this.properties = properties;
        this.workersFactory = workersFactory;
        this.snapshotRepository = snapshotRepository;
        this.workersPool = Executors.newFixedThreadPool(properties.getWorkers().getMsisdnDataSyncPool());
    }

    @Override
    public SyncType type() {
        return MSISDN_DATA;
    }

    @Override
    public void synchronize() {
        try {
            log.info("cleaning up msisdns data snapshot table...");
            snapshotRepository.cleanUp();

            log.info("creating new snapshot of msisdns data...");
            snapshotRepository.makeSnapshot();

            CountDownLatch latch = new CountDownLatch(properties.getWorkers().getMsisdnDataSyncPool());
            IntStream.rangeClosed(1, properties.getWorkers().getMsisdnDataSyncPool())
                    .boxed()
                    .map(n -> workersFactory.getMsisdnDataSyncWorker(latch))
                    .forEach(workersPool::submit);

            latch.await();

        } catch (InterruptedException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}