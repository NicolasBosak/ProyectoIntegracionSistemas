package com.campusconnect.academic.controller;

import com.campusconnect.academic.dto.CreateStudentRequest;
import com.campusconnect.academic.dto.EnrollmentRequest;
import com.campusconnect.academic.dto.EventLogResponse;
import com.campusconnect.academic.dto.StudentRegisteredResponse;
import com.campusconnect.academic.dto.StudentResponse;
import com.campusconnect.academic.dto.StudentStatusResponse;
import com.campusconnect.academic.service.AcademicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/academic")
@Tag(name = "Academico", description = "Estudiantes, matriculas y estado academico/financiero")
public class AcademicController {

    private final AcademicService service;

    public AcademicController(AcademicService service) {
        this.service = service;
    }

    @PostMapping("/students")
    @Operation(summary = "Registrar estudiante y crear matricula (publica StudentEnrolled)")
    public ResponseEntity<StudentRegisteredResponse> register(@Valid @RequestBody CreateStudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerStudent(req));
    }

    @PostMapping("/students/{studentId}/enrollment")
    @Operation(summary = "Actualizar o confirmar la matricula de un estudiante")
    public StudentResponse updateEnrollment(@PathVariable String studentId,
                                            @Valid @RequestBody EnrollmentRequest req) {
        return service.updateEnrollment(studentId, req);
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "Consultar ficha del estudiante")
    public StudentResponse getStudent(@PathVariable String studentId) {
        return service.getStudent(studentId);
    }

    @GetMapping("/students")
    @Operation(summary = "Listar estudiantes matriculados")
    public List<StudentResponse> listStudents() {
        return service.listStudents();
    }

    @GetMapping("/students/{studentId}/status")
    @Operation(summary = "Consultar estado academico y financiero")
    public StudentStatusResponse getStatus(@PathVariable String studentId) {
        return service.getStatus(studentId);
    }

    @GetMapping("/students/{studentId}/events")
    @Operation(summary = "Historial de eventos asociados al estudiante")
    public List<EventLogResponse> getEvents(@PathVariable String studentId) {
        return service.getStudentEvents(studentId);
    }
}
