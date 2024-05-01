package ru.learning.searchengine.infrastructure.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPISecurityConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components())
                .info(new Info().title("API Поискового движка")
                        .description("Если увидел это, придумай что тут написать")
                        .version("0.0.1"));
    }
}
