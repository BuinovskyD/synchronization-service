package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.model.MnpComparisonEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MnpComparisonRepository extends CrudRepository<MnpComparisonEntity, Long>, CustomMnpComparisonRepository {

    @Query("""
            SELECT * FROM sync.mnp_full_comparison WHERE comparison_date IS NULL
            FOR UPDATE SKIP LOCKED LIMIT :limit
            """)
    List<MnpComparisonEntity> findUnprocessed(int limit);
}