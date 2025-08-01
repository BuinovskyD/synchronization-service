package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Stream;

public interface MsisdnDataSnapshotRepository extends CrudRepository<MsisdnData, Long>, CustomSnapshotRepository {

    @Query("""
            SELECT * FROM sync.msisdn_data_snapshot WHERE NOT is_processed
            FOR UPDATE SKIP LOCKED LIMIT :limit
            """)
    List<MsisdnData> findUnprocessed(int limit);
}
