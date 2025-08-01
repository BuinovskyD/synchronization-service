package com.intech.rkn.ksim.synchronization_service.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record SimCardDto(
        Long msisdn,
        LocalDateTime parsedAt
) {
}