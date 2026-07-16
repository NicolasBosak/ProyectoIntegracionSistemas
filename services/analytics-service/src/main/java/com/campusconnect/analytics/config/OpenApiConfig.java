package com.campusconnect.analytics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI analyticsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusConnect 360 - Servicio de Analitica")
                .version("1.0.0")
                .description("Read model (CQRS) alimentado por todos los eventos del ecosistema. "
                        + "Expone los indicadores consolidados del dashboard directivo."));
    }
}
