package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.model.SimCard;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SimCardRepository extends CrudRepository<SimCard, Long> {

    Optional<SimCard> findByMsisdn(Long msisdn);
}
