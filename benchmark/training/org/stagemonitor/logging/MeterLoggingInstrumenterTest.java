package org.stagemonitor.logging;


import com.codahale.metrics.Meter;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class MeterLoggingInstrumenterTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testLogging() throws Exception {
        logger.error("test");
        final Map<MetricName, Meter> meters = Stagemonitor.getMetric2Registry().getMeters();
        Assert.assertNotNull(meters.get(name("logging").tag("log_level", "error").build()));
        Assert.assertEquals(1, meters.get(name("logging").tag("log_level", "error").build()).getCount());
    }
}

