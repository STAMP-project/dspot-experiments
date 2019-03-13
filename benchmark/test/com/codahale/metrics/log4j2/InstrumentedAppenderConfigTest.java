package com.codahale.metrics.log4j2;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.junit.Test;


public class InstrumentedAppenderConfigTest {
    public static final String METRIC_NAME_PREFIX = "metrics";

    public static final String REGISTRY_NAME = "shared-metrics-registry";

    private final MetricRegistry registry = SharedMetricRegistries.getOrCreate(InstrumentedAppenderConfigTest.REGISTRY_NAME);

    private ConfigurationSource source;

    private LoggerContext context;

    // The biggest test is that we can initialize the log4j2 config at all.
    @Test
    public void canRecordAll() {
        Logger logger = context.getLogger(this.getClass().getName());
        long initialAllCount = registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".all")).getCount();
        logger.error("an error message");
        assertThat(registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo((initialAllCount + 1));
    }

    @Test
    public void canRecordError() {
        Logger logger = context.getLogger(this.getClass().getName());
        long initialErrorCount = registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".error")).getCount();
        logger.error("an error message");
        assertThat(registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo((initialErrorCount + 1));
    }

    @Test
    public void noInvalidRecording() {
        Logger logger = context.getLogger(this.getClass().getName());
        long initialInfoCount = registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".info")).getCount();
        logger.error("an error message");
        assertThat(registry.meter(((InstrumentedAppenderConfigTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(initialInfoCount);
    }
}

