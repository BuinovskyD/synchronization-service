package com.intech.rkn.ksim.synchronization_service;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.model.SyncLog;
import java.time.LocalDateTime;

import static com.intech.rkn.ksim.synchronization_service.enums.SyncStatus.STARTED;
import static com.intech.rkn.ksim.synchronization_service.enums.SyncType.MSISDN_DATA;

public interface TestUtils {

    SyncLog STARTED_SYNC_LOG_ENTITY = SyncLog.builder()
            .id(1L)
            .syncType(MSISDN_DATA)
            .status(STARTED)
            .startedAt(LocalDateTime.of(2025, 7, 30, 15, 15, 15))
            .build();

    static AppProperties getWorkerProperties() {
        AppProperties properties = new AppProperties();
        AppProperties.Workers workers = new AppProperties.Workers();
        workers.setMsisdnDataSyncPool(1);
        properties.setWorkers(workers);
        return properties;
    }
}
