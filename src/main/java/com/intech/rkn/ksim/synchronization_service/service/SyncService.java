package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;

public interface SyncService {

    long runSynchronization(SyncType type);
}
