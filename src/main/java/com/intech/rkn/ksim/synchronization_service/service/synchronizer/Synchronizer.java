package com.intech.rkn.ksim.synchronization_service.service.synchronizer;

import com.intech.rkn.ksim.synchronization_service.enums.SyncType;
import java.time.LocalDateTime;

public interface Synchronizer {

    SyncType type();

    void synchronize();
}
