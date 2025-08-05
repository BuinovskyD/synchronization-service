package com.intech.rkn.ksim.synchronization_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.TopicBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Import(Configuration.KafkaConfig.class)
@TestConfiguration(proxyBeanMethods = false)
public class Configuration {

	@Bean
	@ServiceConnection
	public KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
	}

	@Bean
	@ServiceConnection
	public PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
				.withInitScripts("sql/schema.sql", "sql/data.sql");
	}

	@TestConfiguration
	public static class KafkaConfig {

		@Bean
		public NewTopic topics() {
			return TopicBuilder.name("sim-cards-data")
					.partitions(1)
					.replicas(1)
					.build();
		}
	}
}