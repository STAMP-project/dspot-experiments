package com.zaxxer.hikari.metrics.prometheus;


import CollectorRegistry.defaultRegistry;
import io.prometheus.client.CollectorRegistry;
import org.junit.Test;


public class PrometheusMetricsTrackerFactoryTest {
    @Test
    public void registersToProvidedCollectorRegistry() {
        CollectorRegistry collectorRegistry = new CollectorRegistry();
        PrometheusMetricsTrackerFactory factory = new PrometheusMetricsTrackerFactory(collectorRegistry);
        factory.create("testpool-1", poolStats());
        assertHikariMetricsAreNotPresent(defaultRegistry);
        assertHikariMetricsArePresent(collectorRegistry);
    }

    @Test
    public void registersToDefaultCollectorRegistry() {
        PrometheusMetricsTrackerFactory factory = new PrometheusMetricsTrackerFactory();
        factory.create("testpool-2", poolStats());
        assertHikariMetricsArePresent(defaultRegistry);
    }
}

