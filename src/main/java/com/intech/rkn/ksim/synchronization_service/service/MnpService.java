package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.dto.MnpRecord;
import com.intech.rkn.ksim.synchronization_service.model.MnpComparisonEntity;
import com.intech.rkn.ksim.synchronization_service.model.OperatorNameMapping;
import com.intech.rkn.ksim.synchronization_service.repository.MnpComparisonRepository;
import com.intech.rkn.ksim.synchronization_service.repository.OperatorNameMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class MnpService {

    private final MnpComparisonRepository mnpRepository;
    private final OperatorNameMappingRepository opNameMappingRepository;

    public void uploadFullUpdates() throws Exception {
        Map<String, Integer> opMapping = getOperatorMappings();

        Stream<MnpComparisonEntity> mnpStream = getMnpStream()
                .map(record -> MnpComparisonEntity.builder()
                        .msisdn(record.msisdn())
                        .mnpId(opMapping.get(record.ownerId()))
                        .mnpCode(record.ownerId())
                        .mnpMnc(record.mnc())
                        .donorId(opMapping.get(record.donorId()))
                        .donorCode(record.donorId())
                        .donorMnc(record.oldMnc())
                        .rangeHolderId(opMapping.get(record.rangeHolderId()))
                        .rangeHolderCode(record.rangeHolderId())
                        .mnpPortDate(record.portDate())
                        .processType(record.processType())
                        .build());

        mnpRepository.uploadDataStream(mnpStream);
    }

    private Map<String, Integer> getOperatorMappings() {
        return StreamSupport.stream(opNameMappingRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(OperatorNameMapping::operatorName, OperatorNameMapping::operatorId));
    }

    protected abstract Stream<MnpRecord> getMnpStream() throws Exception;
}