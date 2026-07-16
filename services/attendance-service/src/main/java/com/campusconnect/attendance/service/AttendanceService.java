package com.campusconnect.attendance.service;

import com.campusconnect.attendance.domain.AttendanceRecord;
import com.campusconnect.attendance.domain.Incident;
import com.campusconnect.attendance.domain.StudentRef;
import com.campusconnect.attendance.dto.AttendanceRequest;
import com.campusconnect.attendance.dto.AttendanceResponse;
import com.campusconnect.attendance.dto.HistoryResponse;
import com.campusconnect.attendance.dto.IncidentRequest;
import com.campusconnect.attendance.dto.IncidentResponse;
import com.campusconnect.attendance.dto.StudentRefResponse;
import com.campusconnect.attendance.event.AttendanceRecordedData;
import com.campusconnect.attendance.event.EventEnvelope;
import com.campusconnect.attendance.event.EventPublisher;
import com.campusconnect.attendance.event.IncidentReportedData;
import com.campusconnect.attendance.event.IncomingEvent;
import com.campusconnect.attendance.repository.AttendanceRecordRepository;
import com.campusconnect.attendance.repository.IncidentRepository;
import com.campusconnect.attendance.repository.StudentRefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final StudentRefRepository studentRefs;
    private final AttendanceRecordRepository attendance;
    private final IncidentRepository incidents;
    private final EventPublisher publisher;
    private final String attendanceRoutingKey;
    private final String incidentRoutingKey;

    public AttendanceService(StudentRefRepository studentRefs,
                             AttendanceRecordRepository attendance,
                             IncidentRepository incidents,
                             EventPublisher publisher,
                             @Value("${campus.messaging.routing-keys.attendance-recorded}") String attendanceRoutingKey,
                             @Value("${campus.messaging.routing-keys.incident-reported}") String incidentRoutingKey) {
        this.studentRefs = studentRefs;
        this.attendance = attendance;
        this.incidents = incidents;
        this.publisher = publisher;
        this.attendanceRoutingKey = attendanceRoutingKey;
        this.incidentRoutingKey = incidentRoutingKey;
    }

    /** Consume StudentEnrolled para mantener la proyeccion local de estudiantes. */
    @Transactional
    public void handleStudentEnrolled(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        if (studentId == null || studentRefs.existsById(studentId)) {
            return;
        }
        studentRefs.save(new StudentRef(studentId, event.dataString("firstName"),
                event.dataString("lastName"), event.dataString("schoolId"), event.dataString("grade")));
    }

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRequest req) {
        StudentRef student = studentRefs.findById(req.studentId()).orElse(null);
        String schoolId = student != null ? student.getSchoolId() : null;
        LocalDate date = req.date() != null ? LocalDate.parse(req.date()) : LocalDate.now();
        String correlationId = newCorrelationId();

        AttendanceRecord record = new AttendanceRecord(req.studentId(), schoolId, date,
                req.status(), req.recordedBy(), correlationId);
        record = attendance.save(record);
        record.setCode("ATT-" + String.format("%03d", record.getId()));

        AttendanceRecordedData data = new AttendanceRecordedData(record.getCode(), req.studentId(),
                schoolId, date.toString(), req.status(), req.recordedBy());
        publisher.publish(attendanceRoutingKey, EventEnvelope.of("AttendanceRecorded", correlationId, data));

        log.info("Asistencia registrada code={} status={} correlationId={}",
                record.getCode(), req.status(), correlationId);
        return AttendanceResponse.from(record);
    }

    @Transactional
    public IncidentResponse recordIncident(IncidentRequest req) {
        StudentRef student = studentRefs.findById(req.studentId()).orElse(null);
        String schoolId = student != null ? student.getSchoolId() : null;
        String correlationId = newCorrelationId();

        Incident incident = new Incident(req.studentId(), schoolId, req.category(), req.severity(),
                req.description(), req.reportedBy(), correlationId);
        incident = incidents.save(incident);
        incident.setCode("INC-" + String.format("%03d", incident.getId()));

        IncidentReportedData data = new IncidentReportedData(incident.getCode(), req.studentId(),
                schoolId, req.category(), req.severity(), req.description(), req.reportedBy());
        publisher.publish(incidentRoutingKey, EventEnvelope.of("IncidentReported", correlationId, data));

        log.info("Incidente registrado code={} severity={} correlationId={}",
                incident.getCode(), req.severity(), correlationId);
        return IncidentResponse.from(incident);
    }

    @Transactional(readOnly = true)
    public List<StudentRefResponse> listStudents() {
        return studentRefs.findAll().stream().map(StudentRefResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public HistoryResponse history(String studentId) {
        List<AttendanceResponse> att = attendance.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(AttendanceResponse::from).toList();
        List<IncidentResponse> inc = incidents.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(IncidentResponse::from).toList();
        return new HistoryResponse(studentId, att, inc);
    }

    private String newCorrelationId() {
        String day = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(Instant.now());
        return "corr-" + day + "-" + Long.toString(System.nanoTime()).substring(6);
    }
}
