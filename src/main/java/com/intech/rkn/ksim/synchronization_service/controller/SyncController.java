package com.intech.rkn.ksim.synchronization_service.controller;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import com.intech.rkn.ksim.synchronization_service.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SyncController {

    private final SyncService service;

    @PostMapping("/run/sync/{type}")
    @Operation(summary = "Запустить синхронизацию указанного типа")
    public ResponseEntity<Long> runSynchronization(@PathVariable SyncType type) {
        log.info("got {} synchronization request", type);
        return ResponseEntity.ok(service.runSynchronization(type));
    }
}
