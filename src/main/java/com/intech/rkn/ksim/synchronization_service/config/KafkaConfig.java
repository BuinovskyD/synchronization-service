package com.intech.rkn.ksim.synchronization_service.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final AppProperties properties;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(properties.getKafka().getMsisdnDataSyncTopic().getName())
                .partitions(properties.getKafka().getMsisdnDataSyncTopic().getPartitions())
                .replicas(properties.getKafka().getTopicReplicas())
                .build();
    }
}