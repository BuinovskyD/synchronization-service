package com.intech.rkn.ksim.synchronization_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableRetry
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AppProperties properties;

    @Bean
    public ExecutorService syncRequestExecutors() {
        return Executors.newFixedThreadPool(properties.getSyncRequestExecutorsPool());
    }

    @Bean
    public OpenAPI openAPI(@Value("${info.app.name}") String title,
                           @Value("${info.app.description}") String description,
                           @Value("${info.app.version}") String version) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title(title)
                        .description(description)
                        .version(version));
    }

    @Bean
    public RestClient zniisRestClient() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(30000));
        factory.setReadTimeout(Duration.ofMillis(30000));

        return RestClient.builder()
                .baseUrl(properties.getZniisConnectorUrl())
                .requestFactory(factory)
                .build();
    }
}