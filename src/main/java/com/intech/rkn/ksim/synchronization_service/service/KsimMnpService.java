package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.dto.DataFile;
import com.intech.rkn.ksim.synchronization_service.dto.MnpRecord;
import com.intech.rkn.ksim.synchronization_service.repository.MnpComparisonRepository;
import com.intech.rkn.ksim.synchronization_service.repository.OperatorNameMappingRepository;
import com.intech.rkn.ksim.synchronization_service.integretion.ZniisConnectorClient;
import com.intech.rkn.ksim.synchronization_service.service.MnpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.mnp", name = "source", havingValue = "sftp")
public class KsimMnpService extends MnpService {

    private final ZniisConnectorClient client;

    public KsimMnpService(ZniisConnectorClient client,
                          MnpComparisonRepository mnpRepository,
                          OperatorNameMappingRepository opNameMappingRepository) {
        super(mnpRepository, opNameMappingRepository);
        this.client = client;
    }

    @Override
    protected Stream<MnpRecord> getMnpStream() throws Exception {
        Optional<DataFile> latestFile = client.getLatestFullMnpFile();

        if (latestFile.isEmpty())
            throw new RuntimeException("File not found");

        return client.streamFullMnpData(latestFile.get().filename());
    }
}