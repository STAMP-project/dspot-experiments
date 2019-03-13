package org.stagemonitor.tracing;


import org.junit.Assert;
import org.junit.Test;


public class TracingPluginConfigurationTest {
    private TracingPlugin config;

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(1000000.0, config.getProfilerRateLimitPerMinute(), 0);
        Assert.assertEquals(false, config.isLogSpans());
    }
}

