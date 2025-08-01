package com.intech.rkn.ksim.synchronization_service.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table(name = "msisdn_data_snapshot", schema = "sync")
public record MsisdnData(
        @Id
        Long id,
        Long msisdn,
        LocalDateTime parsedAt,
        Integer operatorId,
        Integer fileId,
        Integer rowNum,
        Boolean isValid,
        Boolean isProcessed,
        Boolean syncDone
) {
        public MsisdnData markProcessed(MsisdnData data, boolean syncDone) {
                return MsisdnData.builder()
                        .id(data.id)
                        .msisdn(data.msisdn)
                        .parsedAt(data.parsedAt)
                        .operatorId(data.operatorId)
                        .fileId(data.fileId)
                        .rowNum(data.rowNum)
                        .isValid(data.isValid)
                        .isProcessed(true)
                        .syncDone(syncDone)
                        .build();
        }
}