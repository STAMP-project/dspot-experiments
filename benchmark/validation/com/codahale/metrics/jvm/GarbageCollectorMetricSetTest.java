package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import java.lang.management.GarbageCollectorMXBean;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class GarbageCollectorMetricSetTest {
    private final GarbageCollectorMXBean gc = Mockito.mock(GarbageCollectorMXBean.class);

    private final GarbageCollectorMetricSet metrics = new GarbageCollectorMetricSet(Collections.singletonList(gc));

    @Test
    public void hasGaugesForGcCountsAndElapsedTimes() {
        assertThat(metrics.getMetrics().keySet()).containsOnly("PS-OldGen.time", "PS-OldGen.count");
    }

    @Test
    public void hasAGaugeForGcCounts() {
        final Gauge<Long> gauge = ((Gauge<Long>) (metrics.getMetrics().get("PS-OldGen.count")));
        assertThat(gauge.getValue()).isEqualTo(1L);
    }

    @Test
    public void hasAGaugeForGcTimes() {
        final Gauge<Long> gauge = ((Gauge<Long>) (metrics.getMetrics().get("PS-OldGen.time")));
        assertThat(gauge.getValue()).isEqualTo(2L);
    }

    @Test
    public void autoDiscoversGCs() {
        assertThat(new GarbageCollectorMetricSet().getMetrics().keySet()).isNotEmpty();
    }
}

