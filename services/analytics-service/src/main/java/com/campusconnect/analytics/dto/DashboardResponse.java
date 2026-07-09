package com.campusconnect.analytics.dto;

/** Indicadores consolidados del ecosistema para el dashboard directivo. */
public record DashboardResponse(
        long totalEnrolled,
        long paymentsConfirmed,
        long paymentsPending,
        long attendanceRecords,
        long incidentsReported,
        long eventsProcessed,
        long failedMessages,
        String ecosystemStatus
) {}
