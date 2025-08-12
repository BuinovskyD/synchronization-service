package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.model.MnpComparisonEntity;
import java.util.stream.Stream;

public interface CustomMnpComparisonRepository {

    void uploadDataStream(Stream<MnpComparisonEntity> stream);
}
