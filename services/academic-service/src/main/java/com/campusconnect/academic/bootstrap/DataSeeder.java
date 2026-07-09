package com.campusconnect.academic.bootstrap;

import com.campusconnect.academic.domain.Enrollment;
import com.campusconnect.academic.domain.FinancialStatus;
import com.campusconnect.academic.domain.Student;
import com.campusconnect.academic.repository.EnrollmentRepository;
import com.campusconnect.academic.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga estudiantes de ejemplo la primera vez (datos semilla). No publica eventos:
 * representan informacion preexistente para que los portales no esten vacios en la demo.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final StudentRepository students;
    private final EnrollmentRepository enrollments;

    public DataSeeder(StudentRepository students, EnrollmentRepository enrollments) {
        this.students = students;
        this.enrollments = enrollments;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (students.count() > 0) {
            return;
        }
        seed("María", "González", "SCH-001", "7mo EGB", "rep.gonzalez@example.com", FinancialStatus.UP_TO_DATE);
        seed("Carlos", "Ramírez", "SCH-001", "8vo EGB", "rep.ramirez@example.com", FinancialStatus.PENDING);
        seed("Lucía", "Torres", "SCH-002", "9no EGB", "rep.torres@example.com", FinancialStatus.PENDING);
        log.info("Datos semilla cargados: {} estudiantes", students.count());
    }

    private void seed(String firstName, String lastName, String schoolId, String grade,
                      String guardianEmail, FinancialStatus status) {
        Student s = new Student(firstName, lastName, schoolId, grade, guardianEmail);
        s.setFinancialStatus(status);
        s = students.save(s);
        s.setCode("STU-" + String.format("%03d", s.getId()));

        Enrollment e = new Enrollment(s.getId(), schoolId, grade);
        e = enrollments.save(e);
        e.setCode("ENR-" + String.format("%03d", e.getId()));
    }
}
