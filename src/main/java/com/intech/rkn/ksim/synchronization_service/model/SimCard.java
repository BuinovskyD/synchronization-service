package com.intech.rkn.ksim.synchronization_service.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table(name = "sim_cards", schema = "data")
public record SimCard(
        @Id
        Long id,
        Long msisdn,
        LocalDateTime parsedAt
) {
}