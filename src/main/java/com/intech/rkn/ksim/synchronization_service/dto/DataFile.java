package com.intech.rkn.ksim.synchronization_service.dto;

import com.intech.rkn.ksim.synchronization_service.enums.MnpFileType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DataFile(
        String filename,
        MnpFileType fileType,
        int id,
        LocalDateTime exportedAt
) {
}