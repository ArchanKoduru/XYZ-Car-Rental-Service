package com.xyz.carrentalservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carRentalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XYZ Car Rental Service API")
                        .description("REST API for car booking, pricing, and license validation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("XYZ Tech Team")
                                .email("support@xyz.com")
                                .url("https://xyz.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("XYZ Car Rental Documentation")
                        .url("https://xyz.com/docs"));
    }
}

