package com.indium.ipl.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Swagger using OpenAPI.
 *
 * This class provides the configuration for the OpenAPI documentation
 * for the IPL API. It sets up the API information including title, version,
 * and description.
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("IPL API")
                        .version("1.0")
                        .description("IPL API for fetching details about the match"));
    }
}