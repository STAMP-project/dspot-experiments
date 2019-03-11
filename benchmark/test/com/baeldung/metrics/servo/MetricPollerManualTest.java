package com.baeldung.metrics.servo;


import com.netflix.servo.Metric;
import com.netflix.servo.publish.BasicMetricFilter;
import com.netflix.servo.publish.JvmMetricPoller;
import com.netflix.servo.publish.MemoryMetricObserver;
import com.netflix.servo.publish.PollRunnable;
import com.netflix.servo.publish.PollScheduler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricPollerManualTest {
    @Test
    public void givenJvmPoller_whenMonitor_thenDataCollected() throws Exception {
        MemoryMetricObserver observer = new MemoryMetricObserver();
        PollRunnable pollRunnable = new PollRunnable(new JvmMetricPoller(), new BasicMetricFilter(true), observer);
        PollScheduler.getInstance().start();
        PollScheduler.getInstance().addPoller(pollRunnable, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(1);
        PollScheduler.getInstance().stop();
        List<List<Metric>> metrics = observer.getObservations();
        Assert.assertThat(metrics, Matchers.hasSize(Matchers.greaterThanOrEqualTo(1)));
        List<String> args = metrics.stream().filter(( m) -> !(m.isEmpty())).flatMap(( ms) -> ms.stream().map(( m) -> m.getConfig().getName())).collect(Collectors.toList());
        Assert.assertThat(args, Matchers.hasItems("loadedClassCount", "initUsage", "maxUsage", "threadCount"));
    }
}

