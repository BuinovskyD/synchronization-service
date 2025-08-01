package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.service.impl.MsisdnDataSyncWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

@Component
@RequiredArgsConstructor
public abstract class SyncWorkersFactory {

    @Lookup
    public abstract MsisdnDataSyncWorker getMsisdnDataSyncWorker(CountDownLatch latch);
}
