package com.intech.rkn.ksim.synchronization_service.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Valid
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Min(1)
    private int syncRequestExecutorsPool;

    @NotBlank
    private String zniisConnectorUrl;

    @Valid
    private Kafka kafka = new Kafka();

    @Valid
    private Workers workers = new Workers();

    @Data
    public static class Kafka {

        @Min(1)
        private int topicReplicas;

        @Valid
        private Topic msisdnDataSyncTopic = new Topic();
    }

    @Data
    public static class Topic {

        @NotBlank
        private String name;

        @Min(1)
        private int partitions;
    }

    @Data
    public static class Workers {

        @Min(1)
        private int msisdnDataSyncPool;

        @Min(100)
        private int msisdnDataSyncBatch;

        @Min(12)
        private int msisdnDataSyncLagHours;

        @Min(1)
        private int mnpDataComparisonPool;

        @Min(100)
        private int mnpDataComparisonBatch;
    }
}
