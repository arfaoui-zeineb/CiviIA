package com.civia.reportservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CivicPulse — Report Service API")
                        .description("Microservice responsible for managing geolocated citizen reports, categories, status transitions, attachments and audit history.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CivicPulse Engineering Team")
                                .email("contact@civicpulse.org"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
