package com.codahale.metrics.jvm;


import com.codahale.metrics.Gauge;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;


public class ThreadStatesGaugeSetTest {
    private final ThreadMXBean threads = Mockito.mock(ThreadMXBean.class);

    private final ThreadDeadlockDetector detector = Mockito.mock(ThreadDeadlockDetector.class);

    private final ThreadStatesGaugeSet gauges = new ThreadStatesGaugeSet(threads, detector);

    private final long[] ids = new long[]{ 1, 2, 3 };

    private final ThreadInfo newThread = Mockito.mock(ThreadInfo.class);

    private final ThreadInfo runnableThread = Mockito.mock(ThreadInfo.class);

    private final ThreadInfo blockedThread = Mockito.mock(ThreadInfo.class);

    private final ThreadInfo waitingThread = Mockito.mock(ThreadInfo.class);

    private final ThreadInfo timedWaitingThread = Mockito.mock(ThreadInfo.class);

    private final ThreadInfo terminatedThread = Mockito.mock(ThreadInfo.class);

    private final Set<String> deadlocks = new HashSet<>();

    @Test
    public void hasASetOfGauges() {
        assertThat(gauges.getMetrics().keySet()).containsOnly("terminated.count", "new.count", "count", "timed_waiting.count", "deadlocks", "blocked.count", "waiting.count", "daemon.count", "runnable.count", "deadlock.count");
    }

    @Test
    public void hasAGaugeForEachThreadState() {
        assertThat(((Gauge<?>) (gauges.getMetrics().get("new.count"))).getValue()).isEqualTo(1);
        assertThat(((Gauge<?>) (gauges.getMetrics().get("runnable.count"))).getValue()).isEqualTo(1);
        assertThat(((Gauge<?>) (gauges.getMetrics().get("blocked.count"))).getValue()).isEqualTo(1);
        assertThat(((Gauge<?>) (gauges.getMetrics().get("waiting.count"))).getValue()).isEqualTo(1);
        assertThat(((Gauge<?>) (gauges.getMetrics().get("timed_waiting.count"))).getValue()).isEqualTo(1);
        assertThat(((Gauge<?>) (gauges.getMetrics().get("terminated.count"))).getValue()).isEqualTo(1);
    }

    @Test
    public void hasAGaugeForTheNumberOfThreads() {
        assertThat(((Gauge<?>) (gauges.getMetrics().get("count"))).getValue()).isEqualTo(12);
    }

    @Test
    public void hasAGaugeForTheNumberOfDaemonThreads() {
        assertThat(((Gauge<?>) (gauges.getMetrics().get("daemon.count"))).getValue()).isEqualTo(13);
    }

    @Test
    public void hasAGaugeForAnyDeadlocks() {
        assertThat(((Gauge<?>) (gauges.getMetrics().get("deadlocks"))).getValue()).isEqualTo(deadlocks);
    }

    @Test
    public void hasAGaugeForAnyDeadlockCount() {
        assertThat(((Gauge<?>) (gauges.getMetrics().get("deadlock.count"))).getValue()).isEqualTo(1);
    }

    @Test
    public void autoDiscoversTheMXBeans() {
        final ThreadStatesGaugeSet set = new ThreadStatesGaugeSet();
        assertThat(((Gauge<?>) (set.getMetrics().get("count"))).getValue()).isNotNull();
        assertThat(((Gauge<?>) (set.getMetrics().get("deadlocks"))).getValue()).isNotNull();
    }
}

