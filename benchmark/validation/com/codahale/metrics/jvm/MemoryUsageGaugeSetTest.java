package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("rawtypes")
public class MemoryUsageGaugeSetTest {
    private final MemoryUsage heap = Mockito.mock(MemoryUsage.class);

    private final MemoryUsage nonHeap = Mockito.mock(MemoryUsage.class);

    private final MemoryUsage pool = Mockito.mock(MemoryUsage.class);

    private final MemoryUsage weirdPool = Mockito.mock(MemoryUsage.class);

    private final MemoryUsage weirdCollection = Mockito.mock(MemoryUsage.class);

    private final MemoryMXBean mxBean = Mockito.mock(MemoryMXBean.class);

    private final MemoryPoolMXBean memoryPool = Mockito.mock(MemoryPoolMXBean.class);

    private final MemoryPoolMXBean weirdMemoryPool = Mockito.mock(MemoryPoolMXBean.class);

    private final MemoryUsageGaugeSet gauges = new MemoryUsageGaugeSet(mxBean, Arrays.asList(memoryPool, weirdMemoryPool));

    @Test
    public void hasASetOfGauges() {
        // skip in non-collected pools - "pools.Big-Pool.used-after-gc",
        assertThat(gauges.getMetrics().keySet()).containsOnly("heap.init", "heap.committed", "heap.used", "heap.usage", "heap.max", "non-heap.init", "non-heap.committed", "non-heap.used", "non-heap.usage", "non-heap.max", "total.init", "total.committed", "total.used", "total.max", "pools.Big-Pool.init", "pools.Big-Pool.committed", "pools.Big-Pool.used", "pools.Big-Pool.usage", "pools.Big-Pool.max", "pools.Weird-Pool.init", "pools.Weird-Pool.committed", "pools.Weird-Pool.used", "pools.Weird-Pool.used-after-gc", "pools.Weird-Pool.usage", "pools.Weird-Pool.max");
    }

    @Test
    public void hasAGaugeForTotalCommitted() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("total.committed")));
        assertThat(gauge.getValue()).isEqualTo(11L);
    }

    @Test
    public void hasAGaugeForTotalInit() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("total.init")));
        assertThat(gauge.getValue()).isEqualTo(22L);
    }

    @Test
    public void hasAGaugeForTotalUsed() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("total.used")));
        assertThat(gauge.getValue()).isEqualTo(33L);
    }

    @Test
    public void hasAGaugeForTotalMax() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("total.max")));
        assertThat(gauge.getValue()).isEqualTo(44L);
    }

    @Test
    public void hasAGaugeForHeapCommitted() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("heap.committed")));
        assertThat(gauge.getValue()).isEqualTo(10L);
    }

    @Test
    public void hasAGaugeForHeapInit() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("heap.init")));
        assertThat(gauge.getValue()).isEqualTo(20L);
    }

    @Test
    public void hasAGaugeForHeapUsed() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("heap.used")));
        assertThat(gauge.getValue()).isEqualTo(30L);
    }

    @Test
    public void hasAGaugeForHeapMax() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("heap.max")));
        assertThat(gauge.getValue()).isEqualTo(40L);
    }

    @Test
    public void hasAGaugeForHeapUsage() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("heap.usage")));
        assertThat(gauge.getValue()).isEqualTo(0.75);
    }

    @Test
    public void hasAGaugeForNonHeapCommitted() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("non-heap.committed")));
        assertThat(gauge.getValue()).isEqualTo(1L);
    }

    @Test
    public void hasAGaugeForNonHeapInit() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("non-heap.init")));
        assertThat(gauge.getValue()).isEqualTo(2L);
    }

    @Test
    public void hasAGaugeForNonHeapUsed() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("non-heap.used")));
        assertThat(gauge.getValue()).isEqualTo(3L);
    }

    @Test
    public void hasAGaugeForNonHeapMax() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("non-heap.max")));
        assertThat(gauge.getValue()).isEqualTo(4L);
    }

    @Test
    public void hasAGaugeForNonHeapUsage() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("non-heap.usage")));
        assertThat(gauge.getValue()).isEqualTo(0.75);
    }

    @Test
    public void hasAGaugeForMemoryPoolUsage() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Big-Pool.usage")));
        assertThat(gauge.getValue()).isEqualTo(0.75);
    }

    @Test
    public void hasAGaugeForWeirdMemoryPoolInit() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.init")));
        assertThat(gauge.getValue()).isEqualTo(200L);
    }

    @Test
    public void hasAGaugeForWeirdMemoryPoolCommitted() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.committed")));
        assertThat(gauge.getValue()).isEqualTo(100L);
    }

    @Test
    public void hasAGaugeForWeirdMemoryPoolUsed() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.used")));
        assertThat(gauge.getValue()).isEqualTo(300L);
    }

    @Test
    public void hasAGaugeForWeirdMemoryPoolUsage() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.usage")));
        assertThat(gauge.getValue()).isEqualTo(3.0);
    }

    @Test
    public void hasAGaugeForWeirdMemoryPoolMax() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.max")));
        assertThat(gauge.getValue()).isEqualTo((-1L));
    }

    @Test
    public void hasAGaugeForWeirdCollectionPoolUsed() {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get("pools.Weird-Pool.used-after-gc")));
        assertThat(gauge.getValue()).isEqualTo(290L);
    }

    @Test
    public void autoDetectsMemoryUsageBeanAndMemoryPools() {
        assertThat(new MemoryUsageGaugeSet().getMetrics().keySet()).isNotEmpty();
    }
}

