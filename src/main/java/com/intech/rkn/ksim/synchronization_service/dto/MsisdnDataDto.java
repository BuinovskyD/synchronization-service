package com.intech.rkn.ksim.synchronization_service.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record MsisdnDataDto(
        Long msisdn,
        LocalDateTime parsedAt,
        Integer operatorId,
        Integer fileId,
        Integer rowNum,
        Boolean isValid
) {
}