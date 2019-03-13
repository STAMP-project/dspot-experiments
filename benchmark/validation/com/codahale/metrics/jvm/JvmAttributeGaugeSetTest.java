package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import java.lang.management.RuntimeMXBean;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class JvmAttributeGaugeSetTest {
    private final RuntimeMXBean runtime = Mockito.mock(RuntimeMXBean.class);

    private final JvmAttributeGaugeSet gauges = new JvmAttributeGaugeSet(runtime);

    @Test
    public void hasASetOfGauges() {
        assertThat(gauges.getMetrics().keySet()).containsOnly("vendor", "name", "uptime");
    }

    @Test
    public void hasAGaugeForTheJVMName() {
        final Gauge<String> gauge = ((Gauge<String>) (gauges.getMetrics().get("name")));
        assertThat(gauge.getValue()).isEqualTo("9928@example.com");
    }

    @Test
    public void hasAGaugeForTheJVMVendor() {
        final Gauge<String> gauge = ((Gauge<String>) (gauges.getMetrics().get("vendor")));
        assertThat(gauge.getValue()).isEqualTo("Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 23.7-b01 (1.7)");
    }

    @Test
    public void hasAGaugeForTheJVMUptime() {
        final Gauge<Long> gauge = ((Gauge<Long>) (gauges.getMetrics().get("uptime")));
        assertThat(gauge.getValue()).isEqualTo(100L);
    }

    @Test
    public void autoDiscoversTheRuntimeBean() {
        final Gauge<Long> gauge = ((Gauge<Long>) (new JvmAttributeGaugeSet().getMetrics().get("uptime")));
        assertThat(gauge.getValue()).isPositive();
    }
}

