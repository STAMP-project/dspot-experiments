package com.codahale.metrics.jvm;


import java.lang.management.ManagementFactory;
import org.junit.Test;


public class CpuTimeClockTest {
    @Test
    public void cpuTimeClock() {
        final CpuTimeClock clock = new CpuTimeClock();
        assertThat(((double) (clock.getTime()))).isEqualTo(System.currentTimeMillis(), offset(200.0));
        assertThat(((double) (clock.getTick()))).isEqualTo(ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime(), offset(1000000.0));
    }
}

