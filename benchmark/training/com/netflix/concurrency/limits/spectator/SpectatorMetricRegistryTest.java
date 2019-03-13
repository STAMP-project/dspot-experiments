package com.netflix.concurrency.limits.spectator;


import com.netflix.spectator.api.DefaultRegistry;
import com.netflix.spectator.api.patterns.PolledMeter;
import org.junit.Assert;
import org.junit.Test;


public class SpectatorMetricRegistryTest {
    @Test
    public void testGuage() {
        DefaultRegistry registry = new DefaultRegistry();
        SpectatorMetricRegistry metricRegistry = new SpectatorMetricRegistry(registry, registry.createId("foo"));
        metricRegistry.registerGauge("bar", () -> 10);
        PolledMeter.update(registry);
        Assert.assertEquals(10.0, registry.gauge(registry.createId("foo.bar")).value(), 0);
    }

    @Test
    public void testUnregister() {
        DefaultRegistry registry = new DefaultRegistry();
        SpectatorMetricRegistry metricRegistry = new SpectatorMetricRegistry(registry, registry.createId("foo"));
        metricRegistry.registerGauge("bar", () -> 10);
        metricRegistry.registerGauge("bar", () -> 20);
        PolledMeter.update(registry);
        Assert.assertEquals(20.0, registry.gauge(registry.createId("foo.bar")).value(), 0);
    }
}

