package com.campusconnect.analytics.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Contador clave-valor del read model (p. ej. ENROLLED, PAYMENTS_CONFIRMED). */
@Entity
@Table(name = "metric_counters")
public class MetricCounter {

    @Id
    private String name;
    private long value;

    protected MetricCounter() {
    }

    public MetricCounter(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }
    public void increment() { this.value++; }
}
