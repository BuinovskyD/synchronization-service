package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.dto.MnpRecord;
import com.intech.rkn.ksim.synchronization_service.repository.MnpComparisonRepository;
import com.intech.rkn.ksim.synchronization_service.repository.OperatorNameMappingRepository;
import com.intech.rkn.ksim.synchronization_service.service.MnpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.mnp", name = "source", havingValue = "filesystem")
public class FileSystemMnpService extends MnpService {

    private static final String FILE_PATH = "D:\\Port_all\\";

    public FileSystemMnpService(MnpComparisonRepository mnpRepository,
                                OperatorNameMappingRepository opNameMappingRepository) {
        super(mnpRepository, opNameMappingRepository);
    }

    @Override
    protected Stream<MnpRecord> getMnpStream() throws Exception {
        Path filePath = Paths.get(FILE_PATH.concat(getFile()));

        return Files.lines(filePath).skip(1)
                .map(line -> line.split(","))
                .map(fields -> MnpRecord.builder()
                        .msisdn(Long.parseLong(fields[0]))
                        .ownerId(fields[1])
                        .mnc(Integer.parseInt(fields[2]))
                        .portDate(LocalDateTime.parse(fields[5], DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .donorId(fields[8])
                        .rangeHolderId(fields[9])
                        .oldMnc(Integer.parseInt(fields[11]))
                        .processType(fields[12])
                        .build());
    }

    private String getFile() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(FILE_PATH))) {
            return stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.toLowerCase().contains("Port_All_Full".toLowerCase()))
                    .findFirst().orElseThrow(() -> new RuntimeException("File not found"));
        }
    }
}