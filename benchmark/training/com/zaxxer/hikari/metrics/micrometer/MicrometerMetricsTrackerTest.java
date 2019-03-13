package com.zaxxer.hikari.metrics.micrometer;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Assert;
import org.junit.Test;


public class MicrometerMetricsTrackerTest {
    private MeterRegistry mockMeterRegistry = new SimpleMeterRegistry();

    private MicrometerMetricsTracker testee;

    @Test
    public void close() throws Exception {
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.acquire").tag("pool", "mypool").timer());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.usage").tag("pool", "mypool").timer());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.creation").tag("pool", "mypool").timer());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.timeout").tag("pool", "mypool").counter());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections").tag("pool", "mypool").gauge());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.idle").tag("pool", "mypool").gauge());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.active").tag("pool", "mypool").gauge());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.pending").tag("pool", "mypool").gauge());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.max").tag("pool", "mypool").gauge());
        Assert.assertNotNull(mockMeterRegistry.find("hikaricp.connections.min").tag("pool", "mypool").gauge());
        testee.close();
    }
}

