package com.intech.rkn.ksim.synchronization_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public ExecutorService syncExecutors() {
        return Executors.newFixedThreadPool(2);
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