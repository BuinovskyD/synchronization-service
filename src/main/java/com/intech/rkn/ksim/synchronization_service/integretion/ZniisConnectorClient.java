package com.intech.rkn.ksim.synchronization_service.integretion;

import com.intech.rkn.ksim.synchronization_service.dto.DataFile;
import com.intech.rkn.ksim.synchronization_service.dto.MnpRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZniisConnectorClient {

    private final RestClient zniisRestClient;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 200))
    public Optional<DataFile> getLatestFullMnpFile() {
        return Optional.of(
                zniisRestClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/sftp/PORT_ALL_FULL/latest").build())
                        .retrieve()
                        .body(DataFile.class)
        );
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 200))
    public Stream<MnpRecord> streamFullMnpData(String filename) {
        return zniisRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/sftp/stream/{filename}").build(filename))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}