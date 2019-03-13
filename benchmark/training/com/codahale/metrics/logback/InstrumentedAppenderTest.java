package com.codahale.metrics.logback;


import InstrumentedAppender.DEFAULT_REGISTRY;
import InstrumentedAppender.REGISTRY_PROPERTY_NAME;
import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.junit.Test;
import org.mockito.Mockito;


public class InstrumentedAppenderTest {
    public static final String METRIC_NAME_PREFIX = "ch.qos.logback.core.Appender";

    private final MetricRegistry registry = new MetricRegistry();

    private final InstrumentedAppender appender = new InstrumentedAppender(registry);

    private final ILoggingEvent event = Mockito.mock(ILoggingEvent.class);

    @Test
    public void metersTraceEvents() {
        Mockito.when(event.getLevel()).thenReturn(TRACE);
        appender.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".trace")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersDebugEvents() {
        Mockito.when(event.getLevel()).thenReturn(DEBUG);
        appender.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".debug")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersInfoEvents() {
        Mockito.when(event.getLevel()).thenReturn(INFO);
        appender.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersWarnEvents() {
        Mockito.when(event.getLevel()).thenReturn(WARN);
        appender.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".warn")).getCount()).isEqualTo(1);
    }

    @Test
    public void metersErrorEvents() {
        Mockito.when(event.getLevel()).thenReturn(ERROR);
        appender.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".all")).getCount()).isEqualTo(1);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".error")).getCount()).isEqualTo(1);
    }

    @Test
    public void usesSharedRegistries() {
        String registryName = "registry";
        SharedMetricRegistries.add(registryName, registry);
        final InstrumentedAppender shared = new InstrumentedAppender(registryName);
        shared.start();
        Mockito.when(event.getLevel()).thenReturn(INFO);
        shared.doAppend(event);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }

    @Test
    public void usesDefaultRegistry() {
        SharedMetricRegistries.add(DEFAULT_REGISTRY, registry);
        final InstrumentedAppender shared = new InstrumentedAppender();
        shared.start();
        Mockito.when(event.getLevel()).thenReturn(INFO);
        shared.doAppend(event);
        assertThat(SharedMetricRegistries.names()).contains(DEFAULT_REGISTRY);
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }

    @Test
    public void usesRegistryFromProperty() {
        SharedMetricRegistries.add("something_else", registry);
        System.setProperty(REGISTRY_PROPERTY_NAME, "something_else");
        final InstrumentedAppender shared = new InstrumentedAppender();
        shared.start();
        Mockito.when(event.getLevel()).thenReturn(INFO);
        shared.doAppend(event);
        assertThat(SharedMetricRegistries.names()).contains("something_else");
        assertThat(registry.meter(((InstrumentedAppenderTest.METRIC_NAME_PREFIX) + ".info")).getCount()).isEqualTo(1);
    }
}

