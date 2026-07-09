package com.campusconnect.analytics.controller;

import com.campusconnect.analytics.dto.DashboardResponse;
import com.campusconnect.analytics.dto.EventRecordResponse;
import com.campusconnect.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analitica", description = "Indicadores consolidados del ecosistema (read model / CQRS)")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "KPIs consolidados del ecosistema")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }

    @GetMapping("/events")
    @Operation(summary = "Eventos procesados recientes (trazabilidad)")
    public List<EventRecordResponse> events() {
        return service.recentEvents();
    }
}
