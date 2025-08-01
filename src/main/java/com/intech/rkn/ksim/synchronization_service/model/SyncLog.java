package com.intech.rkn.ksim.synchronization_service.model;

import com.intech.rkn.ksim.synchronization_service.enums.SyncStatus;
import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table(name = "sync_log", schema = "sync")
public record SyncLog(
        @Id
        Long id,
        SyncType syncType,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        SyncStatus status,
        String error
) {
        public static SyncLog create(SyncType type) {
                return SyncLog.builder()
                        .syncType(type)
                        .startedAt(LocalDateTime.now())
                        .status(SyncStatus.STARTED)
                        .build();
        }

        public SyncLog completed(SyncLog logEntity) {
                return base(logEntity)
                        .status(SyncStatus.COMPLETED)
                        .build();
        }

        public SyncLog error(SyncLog logEntity, String error) {
                return base(logEntity)
                        .status(SyncStatus.ERROR)
                        .error(error)
                        .build();
        }

        private SyncLogBuilder base(SyncLog logEntity) {
                return SyncLog.builder()
                        .id(logEntity.id)
                        .syncType(logEntity.syncType)
                        .startedAt(logEntity.startedAt)
                        .finishedAt(LocalDateTime.now());
        }
}
