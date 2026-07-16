package com.campusconnect.notification.controller;

import com.campusconnect.notification.dto.NotificationResponse;
import com.campusconnect.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notificaciones", description = "Notificaciones simuladas generadas por eventos")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones")
    public List<NotificationResponse> list() {
        return service.listAll();
    }

    @GetMapping("/failed")
    @Operation(summary = "Listar notificaciones fallidas")
    public List<NotificationResponse> listFailed() {
        return service.listFailed();
    }
}
