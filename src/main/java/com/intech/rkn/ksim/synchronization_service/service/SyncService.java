package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.model.SyncLog;
import com.intech.rkn.ksim.synchronization_service.repository.SyncLogRepository;
import com.intech.rkn.ksim.synchronization_service.service.synchronizer.Synchronizer;
import com.intech.rkn.ksim.synchronization_service.utils.SyncAlreadyRunningException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.intech.rkn.ksim.synchronization_service.enums.SyncStatus.STARTED;

@Slf4j
@Service
public class SyncService {

    private final ExecutorService syncRequestExecutors;
    private final SyncLogRepository logRepository;
    private final Map<SyncType, Synchronizer> synchronizers;

    public SyncService(ExecutorService syncRequestExecutors,
                       SyncLogRepository logRepository,
                       List<Synchronizer> synchronizers) {
        this.syncRequestExecutors = syncRequestExecutors;
        this.logRepository = logRepository;
        this.synchronizers = synchronizers.stream()
                .collect(Collectors.toMap(Synchronizer::type, Function.identity()));
    }


    public long runSynchronization(SyncType type) {
        Optional<SyncLog> optLastLog = logRepository.findLastByType(type);

        if (optLastLog.isPresent()) {
            SyncLog syncLog = optLastLog.get();

            if (syncLog.status().equals(STARTED)) {
                log.error("{} sync already running", type);
                throw new SyncAlreadyRunningException(String.format("%s sync already running", type));
            }
        }

        return run(type);
    }

    private long run(SyncType type) {
        SyncLog logEntity = logRepository.save(SyncLog.create(type));

        CompletableFuture.runAsync(synchronizers.get(type)::synchronize, syncRequestExecutors)
                .whenComplete((result, error) -> {
                    SyncLog logEntityResult;

                    if (error == null) {
                        log.info("{} synchronization completed", type);
                        logEntityResult = logEntity.completed(logEntity);
                    } else {
                        log.error("{} synchronization failed: {}", type, error.getMessage());
                        logEntityResult = logEntity.error(logEntity, error.getMessage());
                    }

                    logRepository.save(logEntityResult);
                });

        return logEntity.id();
    }
}