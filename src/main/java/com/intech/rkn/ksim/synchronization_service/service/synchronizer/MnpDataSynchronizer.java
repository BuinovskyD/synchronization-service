package com.intech.rkn.ksim.synchronization_service.service.synchronizer;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.service.MnpService;
import com.intech.rkn.ksim.synchronization_service.service.worker.SyncWorkersFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.intech.rkn.ksim.synchronization_service.enums.SyncType.MNP_DATA_FULL;

@Slf4j
@Component
public class MnpDataSynchronizer implements Synchronizer {

    private final AppProperties properties;
    private final MnpService mnpService;
    private final ExecutorService workersPool;
    private final SyncWorkersFactory workersFactory;

    public MnpDataSynchronizer(AppProperties properties,
                               MnpService mnpService,
                               SyncWorkersFactory workersFactory) {
        this.properties = properties;
        this.mnpService = mnpService;
        this.workersFactory = workersFactory;
        this.workersPool = Executors.newFixedThreadPool(properties.getWorkers().getMnpDataComparisonPool());
    }

    @Override
    public SyncType type() {
        return MNP_DATA_FULL;
    }

    @Override
    public void synchronize() {
        try {
            log.info("start mnp data comparison");
            mnpService.uploadFullUpdates();

            log.info("submit comparison task to {} workers", properties.getWorkers().getMnpDataComparisonPool());
            CountDownLatch latch = new CountDownLatch(properties.getWorkers().getMnpDataComparisonPool());

            IntStream.rangeClosed(1, properties.getWorkers().getMnpDataComparisonPool())
                    .boxed()
                    .map(n -> workersFactory.getMnpComparisonWorker(latch))
                    .forEach(workersPool::submit);

            latch.await();

        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}
