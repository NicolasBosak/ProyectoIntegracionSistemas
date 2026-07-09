package com.campusconnect.academic.service;

import com.campusconnect.academic.domain.Enrollment;
import com.campusconnect.academic.domain.Student;
import com.campusconnect.academic.domain.EventLog;
import com.campusconnect.academic.dto.CreateStudentRequest;
import com.campusconnect.academic.dto.EnrollmentRequest;
import com.campusconnect.academic.dto.EventLogResponse;
import com.campusconnect.academic.dto.StudentRegisteredResponse;
import com.campusconnect.academic.dto.StudentResponse;
import com.campusconnect.academic.dto.StudentStatusResponse;
import com.campusconnect.academic.event.EventEnvelope;
import com.campusconnect.academic.event.EventPublisher;
import com.campusconnect.academic.event.StudentEnrolledData;
import com.campusconnect.academic.exception.NotFoundException;
import com.campusconnect.academic.repository.EnrollmentRepository;
import com.campusconnect.academic.repository.EventLogRepository;
import com.campusconnect.academic.repository.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AcademicService {

    private static final Logger log = LoggerFactory.getLogger(AcademicService.class);

    private final StudentRepository students;
    private final EnrollmentRepository enrollments;
    private final EventLogRepository eventLogs;
    private final EventPublisher publisher;
    private final ObjectMapper objectMapper;
    private final String studentEnrolledRoutingKey;

    public AcademicService(StudentRepository students,
                           EnrollmentRepository enrollments,
                           EventLogRepository eventLogs,
                           EventPublisher publisher,
                           ObjectMapper objectMapper,
                           @Value("${campus.messaging.routing-keys.student-enrolled}") String studentEnrolledRoutingKey) {
        this.students = students;
        this.enrollments = enrollments;
        this.eventLogs = eventLogs;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.studentEnrolledRoutingKey = studentEnrolledRoutingKey;
    }

    /**
     * Registra un estudiante, crea su matricula y publica el evento StudentEnrolled.
     */
    @Transactional
    public StudentRegisteredResponse registerStudent(CreateStudentRequest req) {
        // 1. Guardar estudiante y asignar codigo de negocio (STU-xxx)
        Student student = new Student(req.firstName(), req.lastName(),
                req.schoolId(), req.grade(), req.guardianEmail());
        student = students.save(student);
        student.setCode(formatCode("STU", student.getId()));

        // 2. Crear matricula (ENR-xxx)
        Enrollment enrollment = new Enrollment(student.getId(), req.schoolId(), req.grade());
        enrollment = enrollments.save(enrollment);
        enrollment.setCode(formatCode("ENR", enrollment.getId()));

        // 3. Construir y publicar el evento de negocio
        String correlationId = newCorrelationId();
        StudentEnrolledData data = new StudentEnrolledData(
                student.getCode(), student.getSchoolId(), student.getFirstName(),
                student.getLastName(), student.getGrade(), enrollment.getCode(),
                student.getGuardianEmail());
        EventEnvelope<StudentEnrolledData> event =
                EventEnvelope.of("StudentEnrolled", correlationId, data);

        publisher.publish(studentEnrolledRoutingKey, event);

        // 4. Registrar el evento en el log local (trazabilidad / historial)
        eventLogs.save(new EventLog(event.eventId(), event.eventType(), correlationId,
                student.getId(), serialize(event), Instant.parse(event.occurredAt())));

        log.info("Estudiante registrado code={} correlationId={}", student.getCode(), correlationId);
        return new StudentRegisteredResponse(student.getCode(), enrollment.getCode(),
                student.getFinancialStatus().name(), correlationId);
    }

    /** Actualiza/confirma la matricula de un estudiante ya existente (sin republicar evento). */
    @Transactional
    public StudentResponse updateEnrollment(String studentCode, EnrollmentRequest req) {
        Student student = requireStudent(studentCode);
        Enrollment enrollment = enrollments.findByStudentId(student.getId())
                .orElseThrow(() -> new NotFoundException("Matricula no encontrada para " + studentCode));

        enrollment.setGrade(req.grade());
        student.setGrade(req.grade());
        if (req.schoolId() != null && !req.schoolId().isBlank()) {
            enrollment.setSchoolId(req.schoolId());
            student.setSchoolId(req.schoolId());
        }
        enrollment.setStatus("ACTIVE");
        return StudentResponse.from(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(String studentCode) {
        return StudentResponse.from(requireStudent(studentCode));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> listStudents() {
        return students.findAll().stream().map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentStatusResponse getStatus(String studentCode) {
        Student s = requireStudent(studentCode);
        return new StudentStatusResponse(s.getCode(), s.getAcademicStatus(), s.getFinancialStatus().name());
    }

    @Transactional(readOnly = true)
    public List<EventLogResponse> getStudentEvents(String studentCode) {
        Student s = requireStudent(studentCode);
        return eventLogs.findByStudentIdOrderByOccurredAtDesc(s.getId())
                .stream().map(EventLogResponse::from).toList();
    }

    // ----------------- helpers -----------------

    private Student requireStudent(String code) {
        return students.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + code));
    }

    private String formatCode(String prefix, Long id) {
        return prefix + "-" + String.format("%03d", id);
    }

    private String newCorrelationId() {
        String day = DateTimeFormatter.ofPattern("yyyyMMdd")
                .withZone(java.time.ZoneOffset.UTC).format(Instant.now());
        return "corr-" + day + "-" + Long.toString(System.nanoTime()).substring(6);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el evento", e);
        }
    }
}
