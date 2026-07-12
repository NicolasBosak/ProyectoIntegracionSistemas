package com.campusconnect.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusConnect 360 - Servicio de Pagos")
                .version("1.0.0")
                .description("Gestion de deudas y confirmacion de pagos. Publica PaymentConfirmed."));
    }
}
