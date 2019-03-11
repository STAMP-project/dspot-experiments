package com.codahale.metrics;


import java.util.SortedMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MetricFilter.ALL;


public class ScheduledReporterTest {
    private final Gauge<String> gauge = () -> "";

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    private final ScheduledExecutorService mockExecutor = Mockito.mock(ScheduledExecutorService.class);

    private final ScheduledExecutorService customExecutor = Executors.newSingleThreadScheduledExecutor();

    private final ScheduledExecutorService externalExecutor = Executors.newSingleThreadScheduledExecutor();

    private final MetricRegistry registry = new MetricRegistry();

    private final ScheduledReporter reporter = Mockito.spy(new ScheduledReporterTest.DummyReporter(registry, "example", ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS));

    private final ScheduledReporter reporterWithNullExecutor = Mockito.spy(new ScheduledReporterTest.DummyReporter(registry, "example", ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, null));

    private final ScheduledReporter reporterWithCustomMockExecutor = new ScheduledReporterTest.DummyReporter(registry, "example", ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, mockExecutor);

    private final ScheduledReporter reporterWithCustomExecutor = new ScheduledReporterTest.DummyReporter(registry, "example", ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, customExecutor);

    private final ScheduledReporterTest.DummyReporter reporterWithExternallyManagedExecutor = new ScheduledReporterTest.DummyReporter(registry, "example", ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, externalExecutor, false);

    private final ScheduledReporter[] reporters = new ScheduledReporter[]{ reporter, reporterWithCustomExecutor, reporterWithExternallyManagedExecutor };

    @Test
    public void pollsPeriodically() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        reporter.start(100, 100, TimeUnit.MILLISECONDS, () -> {
            if ((latch.getCount()) > 0) {
                reporter.report();
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        Mockito.verify(reporter, Mockito.times(2)).report(map("gauge", gauge), map("counter", counter), map("histogram", histogram), map("meter", meter), map("timer", timer));
    }

    @Test
    public void shouldUsePeriodAsInitialDelayIfNotSpecifiedOtherwise() throws Exception {
        reporterWithCustomMockExecutor.start(200, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExecutor, Mockito.times(1)).scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(200L), ArgumentMatchers.eq(200L), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldStartWithSpecifiedInitialDelay() throws Exception {
        reporterWithCustomMockExecutor.start(350, 100, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExecutor).scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(350L), ArgumentMatchers.eq(100L), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldAutoCreateExecutorWhenItNull() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        reporterWithNullExecutor.start(100, 100, TimeUnit.MILLISECONDS, () -> {
            if ((latch.getCount()) > 0) {
                reporterWithNullExecutor.report();
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
        Mockito.verify(reporterWithNullExecutor, Mockito.times(2)).report(map("gauge", gauge), map("counter", counter), map("histogram", histogram), map("meter", meter), map("timer", timer));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDisallowToStartReportingMultiple() throws Exception {
        reporter.start(200, TimeUnit.MILLISECONDS);
        reporter.start(200, TimeUnit.MILLISECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDisallowToStartReportingMultipleTimesOnCustomExecutor() throws Exception {
        reporterWithCustomExecutor.start(200, TimeUnit.MILLISECONDS);
        reporterWithCustomExecutor.start(200, TimeUnit.MILLISECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDisallowToStartReportingMultipleTimesOnExternallyManagedExecutor() throws Exception {
        start(200, TimeUnit.MILLISECONDS);
        start(200, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldNotFailOnStopIfReporterWasNotStared() {
        for (ScheduledReporter reporter : reporters) {
            reporter.stop();
        }
    }

    @Test
    public void shouldNotFailWhenStoppingMultipleTimes() {
        for (ScheduledReporter reporter : reporters) {
            reporter.start(200, TimeUnit.MILLISECONDS);
            reporter.stop();
            reporter.stop();
            reporter.stop();
        }
    }

    @Test
    public void shouldShutdownExecutorOnStopByDefault() {
        reporterWithCustomExecutor.start(200, TimeUnit.MILLISECONDS);
        reporterWithCustomExecutor.stop();
        Assert.assertTrue(customExecutor.isTerminated());
    }

    @Test
    public void shouldNotShutdownExternallyManagedExecutorOnStop() {
        start(200, TimeUnit.MILLISECONDS);
        stop();
        Assert.assertFalse(mockExecutor.isTerminated());
        Assert.assertFalse(mockExecutor.isShutdown());
    }

    @Test
    public void shouldCancelScheduledFutureWhenStoppingWithExternallyManagedExecutor() throws InterruptedException, ExecutionException, TimeoutException {
        // configure very frequency rate of execution
        start(1, TimeUnit.MILLISECONDS);
        stop();
        Thread.sleep(100);
        // executionCount should not increase when scheduled future is canceled properly
        int executionCount = reporterWithExternallyManagedExecutor.executionCount.get();
        Thread.sleep(500);
        Assert.assertEquals(executionCount, reporterWithExternallyManagedExecutor.executionCount.get());
    }

    @Test
    public void shouldConvertDurationToMillisecondsPrecisely() {
        Assert.assertEquals(2.0E-5, reporter.convertDuration(20), 0.0);
    }

    private static class DummyReporter extends ScheduledReporter {
        private AtomicInteger executionCount = new AtomicInteger();

        DummyReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {
            super(registry, name, filter, rateUnit, durationUnit);
        }

        DummyReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, ScheduledExecutorService executor) {
            super(registry, name, filter, rateUnit, durationUnit, executor);
        }

        DummyReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, ScheduledExecutorService executor, boolean shutdownExecutorOnStop) {
            super(registry, name, filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
            executionCount.incrementAndGet();
            // nothing doing!
        }
    }
}

