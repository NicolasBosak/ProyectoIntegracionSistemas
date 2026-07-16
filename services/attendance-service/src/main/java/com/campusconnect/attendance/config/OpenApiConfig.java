package com.campusconnect.attendance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI attendanceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusConnect 360 - Servicio de Asistencia / Bienestar")
                .version("1.0.0")
                .description("Registro de asistencia e incidentes. Publica AttendanceRecorded e IncidentReported."));
    }
}
