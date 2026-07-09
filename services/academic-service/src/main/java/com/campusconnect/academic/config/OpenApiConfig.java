package com.campusconnect.academic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI academicOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusConnect 360 - Servicio Academico")
                .version("1.0.0")
                .description("Gestion de estudiantes, matriculas y estado academico/financiero. "
                        + "Publica el evento StudentEnrolled al matricular."));
    }
}
