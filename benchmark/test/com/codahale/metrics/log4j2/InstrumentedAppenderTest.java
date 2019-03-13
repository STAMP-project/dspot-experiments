package com.codahale.metrics.log4j2;


import Level.DEBUG;
import Level.ERROR;
import Level.FATAL;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.apache.logging.log4j.core.LogEvent;
import org.junit.Test;
import org.mockito.Mockito;


public class InstrumentedAppenderTest {
    public static final String METRIC_NAME_PREFIX = "org.apache.logging.log4j.core.Appender";

    private final MetricRegistry registry = new MetricRegistry();

    private final InstrumentedAppender appender = new InstrumentedAppender(registry);

    private final LogEvent event = Mockito.mock(LogEvent.class);

    @Test
    public void metersTraceEvents() {
        Mockito.when(event.getLevel()).thenReturn(TRACE);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".trace")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersDebugEvents() {
        Mockito.when(event.getLevel()).thenReturn(DEBUG);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".debug")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersInfoEvents() {
        Mockito.when(event.getLevel()).thenReturn(INFO);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersWarnEvents() {
        Mockito.when(event.getLevel()).thenReturn(WARN);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".warn")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersErrorEvents() {
        Mockito.when(event.getLevel()).thenReturn(ERROR);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".error")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersFatalEvents() {
        Mockito.when(event.getLevel()).thenReturn(FATAL);
        appender.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".fatal")).getCount()).isEqualTo(1);
    }

    @Test
    public void usesSharedRegistries() {
        String registryName = "registry";
        SharedMetricRegistries.add(registryName, registry);
        final InstrumentedAppender shared = new InstrumentedAppender(registryName);
        shared.start();
        Mockito.when(event.getLevel()).thenReturn(INFO);
        shared.append(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }
}

