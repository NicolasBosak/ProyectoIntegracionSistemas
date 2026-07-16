package com.campusconnect.attendance.controller;

import com.campusconnect.attendance.dto.AttendanceRequest;
import com.campusconnect.attendance.dto.AttendanceResponse;
import com.campusconnect.attendance.dto.HistoryResponse;
import com.campusconnect.attendance.dto.IncidentRequest;
import com.campusconnect.attendance.dto.IncidentResponse;
import com.campusconnect.attendance.dto.StudentRefResponse;
import com.campusconnect.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@Tag(name = "Asistencia/Bienestar", description = "Registro de asistencia e incidentes")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/students")
    @Operation(summary = "Consultar estudiantes")
    public List<StudentRefResponse> students() {
        return service.listStudents();
    }

    @PostMapping("/records")
    @Operation(summary = "Registrar asistencia (publica AttendanceRecorded)")
    public ResponseEntity<AttendanceResponse> record(@Valid @RequestBody AttendanceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.recordAttendance(req));
    }

    @PostMapping("/incidents")
    @Operation(summary = "Registrar incidente/novedad (publica IncidentReported)")
    public ResponseEntity<IncidentResponse> incident(@Valid @RequestBody IncidentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.recordIncident(req));
    }

    @GetMapping("/students/{studentId}/history")
    @Operation(summary = "Historial de asistencia e incidentes del estudiante")
    public HistoryResponse history(@PathVariable String studentId) {
        return service.history(studentId);
    }
}
