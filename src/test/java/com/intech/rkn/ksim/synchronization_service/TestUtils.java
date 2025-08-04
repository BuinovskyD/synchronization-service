package com.intech.rkn.ksim.synchronization_service;

import com.intech.rkn.ksim.synchronization_service.config.AppProperties;
import com.intech.rkn.ksim.synchronization_service.model.MsisdnData;
import com.intech.rkn.ksim.synchronization_service.model.SimCard;
import com.intech.rkn.ksim.synchronization_service.model.SyncLog;
import java.time.LocalDateTime;
import java.util.List;

import static com.intech.rkn.ksim.synchronization_service.enums.SyncStatus.STARTED;
import static com.intech.rkn.ksim.synchronization_service.enums.SyncType.MSISDN_DATA;

public interface TestUtils {

    SyncLog STARTED_SYNC_LOG_ENTITY = SyncLog.builder()
            .id(1L)
            .syncType(MSISDN_DATA)
            .status(STARTED)
            .startedAt(LocalDateTime.of(2025, 7, 30, 15, 15, 15))
            .build();

    Long TEST_MSISDN_1 = 9121690789L;
    Long TEST_MSISDN_2 = 9121720330L;
    Long TEST_MSISDN_3 = 9121756199L;

    LocalDateTime TEST_PARSING_DATE_1 = LocalDateTime.of(2025,7,5,14,11,23);
    LocalDateTime TEST_PARSING_DATE_2 = LocalDateTime.of(2025,7,17,8,34,11);
    LocalDateTime TEST_PARSING_DATE_3 = LocalDateTime.of(2025,7,29,15,18,55);

    static AppProperties getWorkerProperties() {
        AppProperties properties = new AppProperties();
        AppProperties.Workers workers = new AppProperties.Workers();
        workers.setMsisdnDataSyncPool(1);
        properties.setWorkers(workers);
        return properties;
    }

    static List<MsisdnData> getSnapshot() {
        return List.of(
                MsisdnData.builder().msisdn(TEST_MSISDN_1).parsedAt(TEST_PARSING_DATE_1)
                        .operatorId(2).fileId(14).rowNum(270)
                        .isValid(true).isProcessed(false).syncDone(false)
                        .build(),
                MsisdnData.builder().msisdn(TEST_MSISDN_2).parsedAt(TEST_PARSING_DATE_2)
                        .operatorId(4).fileId(22).rowNum(340)
                        .isValid(true).isProcessed(false).syncDone(false)
                        .build(),
                MsisdnData.builder().msisdn(TEST_MSISDN_3).parsedAt(TEST_PARSING_DATE_3)
                        .operatorId(7).fileId(17).rowNum(826)
                        .isValid(false).isProcessed(false).syncDone(false)
                        .build()
        );
    }

    static List<SimCard> getSimCardsWithEqualDates() {
        return List.of(
                SimCard.builder().msisdn(TEST_MSISDN_1).parsedAt(TEST_PARSING_DATE_1).build(),
                SimCard.builder().msisdn(TEST_MSISDN_2).parsedAt(TEST_PARSING_DATE_2).build(),
                SimCard.builder().msisdn(TEST_MSISDN_3).parsedAt(TEST_PARSING_DATE_3).build()
        );
    }

    static List<SimCard> getSimCardsWithLagDatesLessThenOneDay() {
        return List.of(
                SimCard.builder().msisdn(TEST_MSISDN_1).parsedAt(TEST_PARSING_DATE_1.minusHours(3)).build(),
                SimCard.builder().msisdn(TEST_MSISDN_2).parsedAt(TEST_PARSING_DATE_2.minusHours(24)).build(),
                SimCard.builder().msisdn(TEST_MSISDN_3).parsedAt(TEST_PARSING_DATE_3.minusHours(18)).build()
        );
    }

    static List<SimCard> getSimCardsWithLagDatesMoreThenOneDay() {
        return List.of(
                SimCard.builder().msisdn(TEST_MSISDN_1).parsedAt(TEST_PARSING_DATE_1.minusDays(3)).build(),
                SimCard.builder().msisdn(TEST_MSISDN_2).parsedAt(TEST_PARSING_DATE_2.minusMonths(2)).build(),
                SimCard.builder().msisdn(TEST_MSISDN_3).parsedAt(TEST_PARSING_DATE_3.minusYears(1)).build()
        );
    }

    static List<SimCard> getSimCardsWithLagDatesMoreThenOneDayForSingleSimCard() {
        return List.of(
                SimCard.builder().msisdn(TEST_MSISDN_1).parsedAt(TEST_PARSING_DATE_1).build(),
                SimCard.builder().msisdn(TEST_MSISDN_2).parsedAt(TEST_PARSING_DATE_2.minusDays(3)).build(),
                SimCard.builder().msisdn(TEST_MSISDN_3).parsedAt(TEST_PARSING_DATE_3).build()
        );
    }
}
