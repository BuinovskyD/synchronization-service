package com.intech.rkn.ksim.synchronization_service.utils;

public class SyncAlreadyRunningException extends RuntimeException {
    public SyncAlreadyRunningException(String message) {
        super(message);
    }
}