package com.baeldung.metrics.core;


import Timer.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricsIntegrationTest {
    @Test
    public void whenMarkMeter_thenCorrectRates() throws InterruptedException {
        Meter meter = new Meter();
        long initCount = meter.getCount();
        Assert.assertThat(initCount, CoreMatchers.equalTo(0L));
        meter.mark();
        Assert.assertThat(meter.getCount(), CoreMatchers.equalTo(1L));
        meter.mark(20);
        Assert.assertThat(meter.getCount(), CoreMatchers.equalTo(21L));
        // not use assert for these rate values because they change every time when this test is run
        double meanRate = meter.getMeanRate();
        double oneMinRate = meter.getOneMinuteRate();
        double fiveMinRate = meter.getFiveMinuteRate();
        double fifteenMinRate = meter.getFifteenMinuteRate();
        System.out.println(meanRate);
        System.out.println(oneMinRate);
        System.out.println(fiveMinRate);
        System.out.println(fifteenMinRate);
    }

    @Test
    public void whenInitRatioGauge_thenCorrectRatio() {
        Gauge<Double> ratioGauge = new AttendanceRatioGauge(15, 20);
        Assert.assertThat(ratioGauge.getValue(), CoreMatchers.equalTo(0.75));
    }

    @Test
    public void whenUseCacheGauge_thenCorrectGauge() {
        Gauge<List<Long>> activeUsersGauge = new ActiveUsersGauge(15, TimeUnit.MINUTES);
        List<Long> expected = new ArrayList<Long>();
        expected.add(12L);
        Assert.assertThat(activeUsersGauge.getValue(), CoreMatchers.equalTo(expected));
    }

    @Test
    public void whenUseDerivativeGauge_thenCorrectGaugeFromBase() {
        Gauge<List<Long>> activeUsersGauge = new ActiveUsersGauge(15, TimeUnit.MINUTES);
        Gauge<Integer> activeUserCountGauge = new ActiveUserCountGauge(activeUsersGauge);
        Assert.assertThat(activeUserCountGauge.getValue(), CoreMatchers.equalTo(1));
    }

    @Test
    public void whenIncDecCounter_thenCorrectCount() {
        Counter counter = new Counter();
        long initCount = counter.getCount();
        Assert.assertThat(initCount, CoreMatchers.equalTo(0L));
        counter.inc();
        Assert.assertThat(counter.getCount(), CoreMatchers.equalTo(1L));
        counter.inc(11);
        Assert.assertThat(counter.getCount(), CoreMatchers.equalTo(12L));
        counter.dec();
        Assert.assertThat(counter.getCount(), CoreMatchers.equalTo(11L));
        counter.dec(6);
        Assert.assertThat(counter.getCount(), CoreMatchers.equalTo(5L));
    }

    @Test
    public void whenUpdateHistogram_thenCorrectDistributionData() {
        Histogram histogram = new Histogram(new UniformReservoir());
        histogram.update(5);
        long count1 = histogram.getCount();
        Assert.assertThat(count1, CoreMatchers.equalTo(1L));
        Snapshot snapshot1 = histogram.getSnapshot();
        Assert.assertThat(snapshot1.getValues().length, CoreMatchers.equalTo(1));
        Assert.assertThat(snapshot1.getValues()[0], CoreMatchers.equalTo(5L));
        Assert.assertThat(snapshot1.getMax(), CoreMatchers.equalTo(5L));
        Assert.assertThat(snapshot1.getMin(), CoreMatchers.equalTo(5L));
        Assert.assertThat(snapshot1.getMean(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.getMedian(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.getStdDev(), CoreMatchers.equalTo(0.0));
        Assert.assertThat(snapshot1.get75thPercentile(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.get95thPercentile(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.get98thPercentile(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.get99thPercentile(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(snapshot1.get999thPercentile(), CoreMatchers.equalTo(5.0));
        histogram.update(20);
        long count2 = histogram.getCount();
        Assert.assertThat(count2, CoreMatchers.equalTo(2L));
        Snapshot snapshot2 = histogram.getSnapshot();
        Assert.assertThat(snapshot2.getValues().length, CoreMatchers.equalTo(2));
        Assert.assertThat(snapshot2.getValues()[0], CoreMatchers.equalTo(5L));
        Assert.assertThat(snapshot2.getValues()[1], CoreMatchers.equalTo(20L));
        Assert.assertThat(snapshot2.getMax(), CoreMatchers.equalTo(20L));
        Assert.assertThat(snapshot2.getMin(), CoreMatchers.equalTo(5L));
        Assert.assertThat(snapshot2.getMean(), CoreMatchers.equalTo(12.5));
        Assert.assertThat(snapshot2.getMedian(), CoreMatchers.equalTo(12.5));
        Assert.assertEquals(10.6, snapshot2.getStdDev(), 0.1);
        Assert.assertThat(snapshot2.get75thPercentile(), CoreMatchers.equalTo(20.0));
        Assert.assertThat(snapshot2.get95thPercentile(), CoreMatchers.equalTo(20.0));
        Assert.assertThat(snapshot2.get98thPercentile(), CoreMatchers.equalTo(20.0));
        Assert.assertThat(snapshot2.get99thPercentile(), CoreMatchers.equalTo(20.0));
        Assert.assertThat(snapshot2.get999thPercentile(), CoreMatchers.equalTo(20.0));
    }

    @Test
    public void whenUseTimer_thenCorrectTimerContexts() throws InterruptedException {
        Timer timer = new Timer();
        Timer.Context context1 = timer.time();
        TimeUnit.SECONDS.sleep(5);
        long elapsed1 = context1.stop();
        Assert.assertEquals(5000000000L, elapsed1, 1000000000);
        Assert.assertThat(timer.getCount(), CoreMatchers.equalTo(1L));
        Assert.assertEquals(0.2, timer.getMeanRate(), 0.2);
        Timer.Context context2 = timer.time();
        TimeUnit.SECONDS.sleep(2);
        context2.close();
        Assert.assertThat(timer.getCount(), CoreMatchers.equalTo(2L));
        Assert.assertEquals(0.3, timer.getMeanRate(), 0.2);
    }
}

