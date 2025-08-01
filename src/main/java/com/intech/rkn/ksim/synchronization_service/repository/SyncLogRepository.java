package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.model.SyncLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SyncLogRepository extends CrudRepository<SyncLog, Long> {

    @Query("""
            SELECT * FROM sync.sync_log WHERE sync_type = :syncType ORDER BY started_at DESC LIMIT 1
            """)
    Optional<SyncLog> findLastByType(@Param("syncType") SyncType syncType);
}
