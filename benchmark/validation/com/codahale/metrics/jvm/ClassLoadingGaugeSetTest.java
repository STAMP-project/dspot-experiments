package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import java.lang.management.ClassLoadingMXBean;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("rawtypes")
public class ClassLoadingGaugeSetTest {
    private final ClassLoadingMXBean cl = Mockito.mock(ClassLoadingMXBean.class);

    private final ClassLoadingGaugeSet gauges = new ClassLoadingGaugeSet(cl);

    @Test
    public void loadedGauge() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("loaded")));
        assertThat(gauge.getValue()).isEqualTo(2L);
    }

    @Test
    public void unLoadedGauge() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("unloaded")));
        assertThat(gauge.getValue()).isEqualTo(1L);
    }
}

