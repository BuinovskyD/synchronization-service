package com.intech.rkn.ksim.synchronization_service.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table(name = "mnp", schema = "catalogs")
public record MnpMsisdnData(
        @Id
        Long msisdn,
        Integer operatorId,
        String operatorCode,
        Integer mnc,
        LocalDateTime portedAt
) {
}