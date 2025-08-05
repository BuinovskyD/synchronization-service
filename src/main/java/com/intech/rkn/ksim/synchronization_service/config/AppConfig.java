package com.intech.rkn.ksim.synchronization_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}