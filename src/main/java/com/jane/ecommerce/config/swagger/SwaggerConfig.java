package com.jane.ecommerce.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("E-Commerce API")
                .description("API specification for E-Commerce Application")
                .version("1.0.0")
            );
    }
}
