package com.intech.rkn.ksim.synchronization_service.service.worker;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.model.MnpComparisonEntity;
import com.intech.rkn.ksim.synchronization_service.model.MnpMsisdnData;
import com.intech.rkn.ksim.synchronization_service.repository.KsimMnpRepository;
import com.intech.rkn.ksim.synchronization_service.repository.MnpComparisonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@Scope("prototype")
public class MnpComparisonWorker implements Runnable {

    @Autowired
    private AppProperties properties;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private KsimMnpRepository ksimMnpRepository;
    @Autowired
    private MnpComparisonRepository mnpComparisonRepository;

    private final CountDownLatch latch;
    private boolean isDone;

    public MnpComparisonWorker(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        log.info("start processing mnp data");

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
            List<MnpComparisonEntity> unprocessed = mnpComparisonRepository.findUnprocessed(properties.getWorkers().getMnpDataComparisonBatch());
            log.info("processing batch of size {} records", unprocessed.size());

            if (!unprocessed.isEmpty()) {
                List<MnpComparisonEntity> processed = unprocessed.stream().map(record -> {
                    MnpMsisdnData ksimMnpData = ksimMnpRepository.findByMsisdn(record.msisdn());
                    return record.markAsProcessed(ksimMnpData);
                }).toList();

                mnpComparisonRepository.saveAll(processed);
            } else {
                log.info("no records for processing, finish");
                isDone = true;
            }
            return true;
        });
    }
}