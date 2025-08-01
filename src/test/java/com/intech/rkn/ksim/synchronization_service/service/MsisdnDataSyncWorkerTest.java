package com.intech.rkn.ksim.synchronization_service.service;

import com.intech.rkn.ksim.synchronization_service.config.Configuration;
import com.intech.rkn.ksim.synchronization_service.dto.MsisdnSyncMessage;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.model.SimCard;
import com.intech.rkn.ksim.synchronization_service.repository.MsisdnDataSnapshotRepository;
import com.intech.rkn.ksim.synchronization_service.repository.SimCardRepository;
import com.intech.rkn.ksim.synchronization_service.service.impl.MsisdnDataSyncWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@Import(Configuration.class)
class MsisdnDataSyncWorkerTest {

    @Autowired
    private SyncWorkersFactory workersFactory;
    @Autowired
    private MsisdnDataSnapshotRepository snapshotRepository;
    @Autowired
    private SimCardRepository simCardRepository;

    private MsisdnDataSyncWorker workerUnderTests;

    private final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();

    @BeforeEach
    void setUp() {
        this.workerUnderTests = workersFactory.getMsisdnDataSyncWorker(new CountDownLatch(1));
    }

    @Test
    void run() {
        workerUnderTests.run();
        Iterable<MsisdnData> all = snapshotRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    void run2() {
        MsisdnData initDataValue1 = MsisdnData.builder().msisdn(9121690789L).parsedAt(LocalDateTime.of(2025,7,5,14,11,23)).operatorId(2).fileId(14).rowNum(270).isValid(true).isProcessed(false).syncDone(false).build();
        MsisdnData initDataValue2 = MsisdnData.builder().msisdn(9121720330L).parsedAt(LocalDateTime.of(2025,7,17,8,34,11)).operatorId(4).fileId(22).rowNum(340).isValid(true).isProcessed(false).syncDone(false).build();
        MsisdnData initDataValue3 = MsisdnData.builder().msisdn(9121756199L).parsedAt(LocalDateTime.of(2025,7,29,15,18,55)).operatorId(7).fileId(17).rowNum(826).isValid(false).isProcessed(false).syncDone(false).build();

        SimCard simCardValue1 = SimCard.builder().msisdn(9121690789L).parsedAt(LocalDateTime.of(2025,7,5,14,11,23)).build();
        SimCard simCardValue2 = SimCard.builder().msisdn(9121720330L).parsedAt(LocalDateTime.of(2025,7,17,8,34,11)).build();
        SimCard simCardValue3 = SimCard.builder().msisdn(9121756199L).parsedAt(LocalDateTime.of(2025,7,29,15,18,55)).build();

        snapshotRepository.saveAll(List.of(initDataValue1, initDataValue2, initDataValue3));
        simCardRepository.saveAll(List.of(simCardValue1, simCardValue2, simCardValue3));

        workerUnderTests.run();
    }

    @KafkaListener(id = "test-listener", topics = "sim-cards-data")
    public void listen(ConsumerRecord<Long, MsisdnSyncMessage> record) {
        log.info(record.toString());
    }
}