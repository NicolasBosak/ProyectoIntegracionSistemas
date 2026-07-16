package com.campusconnect.notification.controller;

import com.campusconnect.notification.domain.DeadLetter;
import com.campusconnect.notification.dto.NotificationResponse;
import com.campusconnect.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notificaciones", description = "Notificaciones simuladas y manejo de mensajes fallidos (DLQ)")
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

    @GetMapping("/dead-letters")
    @Operation(summary = "Listar mensajes en la Dead Letter Queue")
    public List<DeadLetter> deadLetters() {
        return service.listDeadLetters();
    }

    @PostMapping("/dead-letters/{id}/reprocess")
    @Operation(summary = "Reprocesar un mensaje fallido de la DLQ")
    public Map<String, Object> reprocess(@PathVariable Long id) {
        service.reprocess(id);
        return Map.of("deadLetterId", id, "status", "REPROCESSED");
    }
}
