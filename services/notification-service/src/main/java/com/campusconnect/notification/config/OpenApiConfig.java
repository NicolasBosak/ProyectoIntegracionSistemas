package com.campusconnect.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusConnect 360 - Servicio de Notificaciones")
                .version("1.0.0")
                .description("Consume eventos de negocio y genera notificaciones simuladas. "
                        + "Publica NotificationSent / NotificationFailed."));
    }
}
