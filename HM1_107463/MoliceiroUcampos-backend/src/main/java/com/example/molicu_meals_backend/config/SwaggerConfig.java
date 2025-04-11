package com.example.molicu_meals_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI moliceiroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Moliceiro Meals API")
                        .description("API para reservas, menus e gestão de refeições no campus Moliceiro.")
                        .version("1.0"));
    }
}