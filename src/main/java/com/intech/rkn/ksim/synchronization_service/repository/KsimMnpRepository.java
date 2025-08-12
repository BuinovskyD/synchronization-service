package com.intech.rkn.ksim.synchronization_service.repository;

import com.intech.rkn.ksim.synchronization_service.model.MnpMsisdnData;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface KsimMnpRepository extends CrudRepository<MnpMsisdnData, Long> {

    MnpMsisdnData findByMsisdn(Long msisdn);
}
