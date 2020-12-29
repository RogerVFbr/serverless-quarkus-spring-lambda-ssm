package com.soundlab.utils;

import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.Instant;

import lombok.Data;

@Data
public class DurationMetric {
    private static final Logger LOG = Logger.getLogger(DurationMetric.class);
    private Instant startTime;

    public DurationMetric() {
        this.startTime = Instant.now();
    }

    public void measure(String msg) {
        double duration = Duration.between(startTime, Instant.now()).toMillis();
        LOG.info(String.format("%s: %.0fms", msg, duration));
    }
}
