package com.intech.rkn.ksim.synchronization_service.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record MsisdnSyncMessage(
        LocalDateTime eventDate,
        Integer fileId,
        Long msisdn,
        Integer operatorId,
        Integer rowNum,
        Boolean msisdnDataValid
) {
}
