package com.baeldung.metrics.servo;


import com.netflix.servo.monitor.BasicInformational;
import com.netflix.servo.monitor.BasicTimer;
import com.netflix.servo.monitor.BucketConfig;
import com.netflix.servo.monitor.BucketTimer;
import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.Gauge;
import com.netflix.servo.monitor.MaxGauge;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.monitor.StatsTimer;
import com.netflix.servo.monitor.Stopwatch;
import com.netflix.servo.stats.StatsConfig;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricTypeManualTest {
    @Test
    public void givenDefaultCounter_whenManipulate_thenCountValid() {
        Counter counter = Monitors.newCounter("test");
        Assert.assertEquals("counter should start with 0", 0, counter.getValue().intValue());
        counter.increment();
        Assert.assertEquals("counter should have increased by 1", 1, counter.getValue().intValue());
        counter.increment((-1));
        Assert.assertEquals("counter should have decreased by 1", 0, counter.getValue().intValue());
    }

    @Test
    public void givenBasicCounter_whenManipulate_thenCountValid() {
        Counter counter = new com.netflix.servo.monitor.BasicCounter(MonitorConfig.builder("test").build());
        Assert.assertEquals("counter should start with 0", 0, counter.getValue().intValue());
        counter.increment();
        Assert.assertEquals("counter should have increased by 1", 1, counter.getValue().intValue());
        counter.increment((-1));
        Assert.assertEquals("counter should have decreased by 1", 0, counter.getValue().intValue());
    }

    @Test
    public void givenPeakRateCounter_whenManipulate_thenPeakRateReturn() throws Exception {
        Counter counter = new com.netflix.servo.monitor.PeakRateCounter(MonitorConfig.builder("test").build());
        Assert.assertEquals("counter should start with 0", 0, counter.getValue().intValue());
        counter.increment();
        TimeUnit.SECONDS.sleep(1);
        counter.increment();
        counter.increment();
        Assert.assertEquals("peak rate should have be 2", 2, counter.getValue().intValue());
    }

    @Test
    public void givenTimer_whenExecuteTask_thenTimerUpdated() throws Exception {
        BasicTimer timer = new BasicTimer(MonitorConfig.builder("test").build(), TimeUnit.MILLISECONDS);
        Stopwatch stopwatch = timer.start();
        TimeUnit.SECONDS.sleep(1);
        timer.record(2, TimeUnit.SECONDS);
        stopwatch.stop();
        Assert.assertEquals("timer should count 1 second", 1000, timer.getValue().intValue(), 1000);
        Assert.assertEquals("timer should count 3 second in total", 3000, timer.getTotalTime().intValue(), 1000);
        Assert.assertEquals("timer should record 2 updates", 2, timer.getCount().intValue());
        Assert.assertEquals("timer should have max 2", 2000, timer.getMax(), 0.01);
    }

    @Test
    public void givenBucketTimer_whenRecord_thenStatsCalculated() throws Exception {
        BucketTimer timer = new BucketTimer(MonitorConfig.builder("test").build(), new BucketConfig.Builder().withBuckets(new long[]{ 2L, 5L }).withTimeUnit(TimeUnit.SECONDS).build(), TimeUnit.SECONDS);
        timer.record(3);
        timer.record(6);
        Assert.assertEquals("timer should count 9 seconds in total", 9, timer.getTotalTime().intValue());
        final Map<String, Long> metricMap = timer.getMonitors().stream().filter(( monitor) -> monitor.getConfig().getTags().containsKey("servo.bucket")).collect(Collectors.toMap(( monior) -> getMonitorTagValue(monior, "servo.bucket"), ( monitor) -> ((Long) (monitor.getValue()))));
        Assert.assertThat(metricMap, Matchers.allOf(Matchers.hasEntry("bucket=2s", 0L), Matchers.hasEntry("bucket=5s", 1L), Matchers.hasEntry("bucket=overflow", 1L)));
    }

    @Test
    public void givenStatsTimer_whenExecuteTask_thenStatsCalculated() throws Exception {
        System.setProperty("netflix.servo", "1000");
        StatsTimer timer = new StatsTimer(MonitorConfig.builder("test").build(), new StatsConfig.Builder().withComputeFrequencyMillis(2000).withPercentiles(new double[]{ 99.0, 95.0, 90.0 }).withPublishMax(true).withPublishMin(true).withPublishCount(true).withPublishMean(true).withPublishStdDev(true).withPublishVariance(true).build(), TimeUnit.MILLISECONDS);
        Stopwatch stopwatch = timer.start();
        TimeUnit.SECONDS.sleep(1);
        timer.record(3, TimeUnit.SECONDS);
        stopwatch.stop();
        stopwatch = timer.start();
        timer.record(6, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(2);
        stopwatch.stop();
        Assert.assertEquals("timer should count 12 seconds in total", 12000, timer.getTotalTime(), 500);
        Assert.assertEquals("timer should count 12 seconds in total", 12000, timer.getTotalMeasurement(), 500);
        Assert.assertEquals("timer should record 4 updates", 4, timer.getCount());
        Assert.assertEquals("stats timer value time-cost/update should be 2", 3000, timer.getValue().intValue(), 500);
        final Map<String, Number> metricMap = timer.getMonitors().stream().collect(Collectors.toMap(( monitor) -> getMonitorTagValue(monitor, "statistic"), ( monitor) -> ((Number) (monitor.getValue()))));
        Assert.assertThat(metricMap.keySet(), Matchers.containsInAnyOrder("count", "totalTime", "max", "min", "variance", "stdDev", "avg", "percentile_99", "percentile_95", "percentile_90"));
    }

    @Test
    public void givenGauge_whenCall_thenValueReturned() {
        Gauge<Double> gauge = new com.netflix.servo.monitor.BasicGauge(MonitorConfig.builder("test").build(), () -> 2.32);
        Assert.assertEquals(2.32, gauge.getValue(), 0.01);
    }

    @Test
    public void givenMaxGauge_whenUpdateMultipleTimes_thenMaxReturned() {
        MaxGauge gauge = new MaxGauge(MonitorConfig.builder("test").build());
        Assert.assertEquals(0, gauge.getValue().intValue());
        gauge.update(4);
        Assert.assertEquals(4, gauge.getCurrentValue(0));
        gauge.update(1);
        Assert.assertEquals(4, gauge.getCurrentValue(0));
    }

    @Test
    public void givenInformationalMonitor_whenRecord_thenInformationCollected() throws Exception {
        BasicInformational informational = new BasicInformational(MonitorConfig.builder("test").build());
        informational.setValue("information collected");
        Assert.assertEquals("information collected", informational.getValue());
    }
}

