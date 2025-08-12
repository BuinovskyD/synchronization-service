package com.intech.rkn.ksim.synchronization_service.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record MnpRecord(
        Long msisdn,
        String ownerId,
        int mnc,
        LocalDateTime portDate,
        String donorId,
        String rangeHolderId,
        int oldMnc,
        String processType
) {
}