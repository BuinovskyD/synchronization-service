package com.intech.rkn.ksim.synchronization_service.service.impl;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.dto.MsisdnSyncMessage;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.model.SimCard;
import com.intech.rkn.ksim.synchronization_service.repository.MsisdnDataSnapshotRepository;
import com.intech.rkn.ksim.synchronization_service.repository.SimCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@Scope("prototype")
public class MsisdnDataSyncWorker implements Runnable {

    @Autowired
    private AppProperties properties;
    @Autowired
    private SimCardRepository simCardRepository;
    @Autowired
    private MsisdnDataSnapshotRepository snapshotRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private KafkaTemplate<Long, Object> msisdnKafkaTemplate;

    private final CountDownLatch latch;
    private boolean isDone;

    public MsisdnDataSyncWorker(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        log.info("start processing snapshot data...");

        try {
            while (!isDone) {
                doWork();
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            latch.countDown();
        }
    }

    private void doWork() {
        transactionTemplate.execute(status -> {
            List<MsisdnData> unprocessed = snapshotRepository.findUnprocessed(properties.getWorkers().getMsisdnDataSyncBatch());
            log.info("processing batch of size {} records", unprocessed.size());

            if (!unprocessed.isEmpty()) {
                List<MsisdnData> result = unprocessed.stream().map(snapshotItem -> {
                    Optional<SimCard> optSimCard = simCardRepository.findByMsisdn(snapshotItem.msisdn());

                    if (optSimCard.isEmpty() || isTimeOffsetExceeded(optSimCard.get(), snapshotItem.parsedAt())) {
                        sendSyncMessage(snapshotItem);
                        return snapshotItem.markProcessed(snapshotItem, true);
                    } else {
                        return snapshotItem.markProcessed(snapshotItem, false);
                    }
                }).toList();

                snapshotRepository.saveAll(result);

            } else {
                log.info("no records for processing, finish");
                isDone = true;
            }

            return true;
        });
    }

    private boolean isTimeOffsetExceeded(SimCard simCard, LocalDateTime snapshotItemDate) {
        return simCard.parsedAt().plusHours(properties.getWorkers().getMsisdnDataSyncLagHours()).isBefore(snapshotItemDate);
    }

    private void sendSyncMessage(MsisdnData snapshotItem) {
        MsisdnSyncMessage syncMessage = MsisdnSyncMessage.builder()
                .eventDate(snapshotItem.parsedAt())
                .fileId(snapshotItem.fileId())
                .msisdn(snapshotItem.msisdn())
                .operatorId(snapshotItem.operatorId())
                .rowNum(snapshotItem.rowNum())
                .msisdnDataValid(snapshotItem.isValid())
                .build();

        msisdnKafkaTemplate.send(properties.getKafka().getMsisdnDataSyncTopic().getName(), snapshotItem.msisdn(), syncMessage);
    }
}