package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("rawtypes")
public class BufferPoolMetricSetTest {
    private final MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);

    private final BufferPoolMetricSet buffers = new BufferPoolMetricSet(mBeanServer);

    private ObjectName mapped;

    private ObjectName direct;

    @Test
    public void includesGaugesForDirectAndMappedPools() {
        assertThat(buffers.getMetrics().keySet()).containsOnly("direct.count", "mapped.used", "mapped.capacity", "direct.capacity", "mapped.count", "direct.used");
    }

    @Test
    public void ignoresGaugesForObjectsWhichCannotBeFound() throws Exception {
        Mockito.when(mBeanServer.getMBeanInfo(mapped)).thenThrow(new InstanceNotFoundException());
        assertThat(buffers.getMetrics().keySet()).containsOnly("direct.count", "direct.capacity", "direct.used");
    }

    @Test
    public void includesAGaugeForDirectCount() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("direct.count")));
        Mockito.when(mBeanServer.getAttribute(direct, "Count")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }

    @Test
    public void includesAGaugeForDirectMemoryUsed() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("direct.used")));
        Mockito.when(mBeanServer.getAttribute(direct, "MemoryUsed")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }

    @Test
    public void includesAGaugeForDirectCapacity() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("direct.capacity")));
        Mockito.when(mBeanServer.getAttribute(direct, "TotalCapacity")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedCount() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("mapped.count")));
        Mockito.when(mBeanServer.getAttribute(mapped, "Count")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedMemoryUsed() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("mapped.used")));
        Mockito.when(mBeanServer.getAttribute(mapped, "MemoryUsed")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }

    @Test
    public void includesAGaugeForMappedCapacity() throws Exception {
        final Gauge gauge = ((Gauge) (buffers.getMetrics().get("mapped.capacity")));
        Mockito.when(mBeanServer.getAttribute(mapped, "TotalCapacity")).thenReturn(100);
        assertThat(gauge.getValue()).isEqualTo(100);
    }
}

