package com.campusconnect.academic.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Prueba unitaria ligera del envelope de eventos (no requiere infraestructura). */
class EventEnvelopeTest {

    @Test
    void of_buildsEnvelopeWithStandardFields() {
        StudentEnrolledData data = new StudentEnrolledData(
                "STU-001", "SCH-001", "Ana", "Pérez", "8vo EGB", "ENR-001", "rep@example.com");

        EventEnvelope<StudentEnrolledData> event =
                EventEnvelope.of("StudentEnrolled", "corr-123", data);

        assertThat(event.eventId()).startsWith("evt-");
        assertThat(event.eventType()).isEqualTo("StudentEnrolled");
        assertThat(event.correlationId()).isEqualTo("corr-123");
        assertThat(event.source()).isEqualTo("academic-service");
        assertThat(event.version()).isEqualTo("1.0");
        assertThat(event.occurredAt()).endsWith("Z");
        assertThat(event.data().studentId()).isEqualTo("STU-001");
    }
}
