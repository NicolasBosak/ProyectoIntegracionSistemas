package com.campusconnect.analytics.service;

import com.campusconnect.analytics.domain.EventRecord;
import com.campusconnect.analytics.domain.MetricCounter;
import com.campusconnect.analytics.dto.DashboardResponse;
import com.campusconnect.analytics.dto.EventRecordResponse;
import com.campusconnect.analytics.event.IncomingEvent;
import com.campusconnect.analytics.repository.EventRecordRepository;
import com.campusconnect.analytics.repository.MetricCounterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Construye el read model (CQRS) a partir de los eventos: mantiene contadores y un registro
 * de eventos para la trazabilidad. Es la fuente de datos del dashboard directivo.
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    // Nombres de los contadores
    private static final String ENROLLED = "ENROLLED";
    private static final String PAYMENTS_CONFIRMED = "PAYMENTS_CONFIRMED";
    private static final String ATTENDANCE_RECORDS = "ATTENDANCE_RECORDS";
    private static final String INCIDENTS_REPORTED = "INCIDENTS_REPORTED";
    private static final String EVENTS_PROCESSED = "EVENTS_PROCESSED";
    private static final String FAILED_MESSAGES = "FAILED_MESSAGES";

    private final MetricCounterRepository counters;
    private final EventRecordRepository events;

    public AnalyticsService(MetricCounterRepository counters, EventRecordRepository events) {
        this.counters = counters;
        this.events = events;
    }

    @Transactional
    public void handleEvent(IncomingEvent event) {
        // Guardia de idempotencia ligera: evita recontar en caso de redelivery
        if (event.eventId() != null && events.existsByEventId(event.eventId())) {
            log.info("Evento ya procesado, se ignora eventId={}", event.eventId());
            return;
        }

        increment(EVENTS_PROCESSED);
        switch (event.eventType()) {
            case "StudentEnrolled" -> increment(ENROLLED);
            case "PaymentConfirmed" -> increment(PAYMENTS_CONFIRMED);
            case "AttendanceRecorded" -> increment(ATTENDANCE_RECORDS);
            case "IncidentReported" -> increment(INCIDENTS_REPORTED);
            case "NotificationFailed" -> increment(FAILED_MESSAGES);
            default -> { /* NotificationSent, StudentStatusUpdated: solo cuentan como procesados */ }
        }

        events.save(new EventRecord(event.eventId(), event.eventType(),
                event.correlationId(), event.source(), event.occurredAt()));

        log.info("Evento consolidado type={} correlationId={}", event.eventType(), event.correlationId());
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long enrolled = value(ENROLLED);
        long confirmed = value(PAYMENTS_CONFIRMED);
        long pending = Math.max(0, enrolled - confirmed);
        long failed = value(FAILED_MESSAGES);
        String status = failed > 0 ? "DEGRADED" : "HEALTHY";

        return new DashboardResponse(enrolled, confirmed, pending,
                value(ATTENDANCE_RECORDS), value(INCIDENTS_REPORTED),
                value(EVENTS_PROCESSED), failed, status);
    }

    @Transactional(readOnly = true)
    public List<EventRecordResponse> recentEvents() {
        return events.findTop100ByOrderByReceivedAtDesc().stream()
                .map(EventRecordResponse::from).toList();
    }

    // ----------------- helpers -----------------

    private void increment(String name) {
        MetricCounter counter = counters.findById(name).orElseGet(() -> new MetricCounter(name, 0));
        counter.increment();
        counters.save(counter);
    }

    private long value(String name) {
        return counters.findById(name).map(MetricCounter::getValue).orElse(0L);
    }
}
