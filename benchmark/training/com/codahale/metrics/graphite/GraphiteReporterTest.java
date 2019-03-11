package com.codahale.metrics.graphite;


import MetricAttribute.M15_RATE;
import MetricAttribute.M5_RATE;
import MetricFilter.ALL;
import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class GraphiteReporterTest {
    private final long timestamp = 1000198;

    private final Clock clock = Mockito.mock(Clock.class);

    private final Graphite graphite = Mockito.mock(Graphite.class);

    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    private final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry).withClock(clock).prefixedWith("prefix").convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(Collections.emptySet()).build(graphite);

    private final GraphiteReporter minuteRateReporter = GraphiteReporter.forRegistry(registry).withClock(clock).prefixedWith("prefix").convertRatesTo(TimeUnit.MINUTES).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(Collections.emptySet()).build(graphite);

    @Test
    public void doesNotReportStringGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge("value")), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite, Mockito.never()).send("prefix.gauge", "value", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsByteGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(((byte) (1)))), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsShortGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(((short) (1)))), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsIntegerGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsLongGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1L)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsFloatGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1.1F)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1.10", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsDoubleGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1.1)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1.10", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsDoubleGaugeValuesWithCustomFormat() throws Exception {
        try (final GraphiteReporter graphiteReporter = getReporterWithCustomFormat()) {
            graphiteReporter.report(map("gauge", gauge(1.13574)), map(), map(), map(), map());
            final InOrder inOrder = Mockito.inOrder(graphite);
            inOrder.verify(graphite).connect();
            inOrder.verify(graphite).send("prefix.gauge", "1.1357", timestamp);
            inOrder.verify(graphite).flush();
            inOrder.verify(graphite).close();
            Mockito.verifyNoMoreInteractions(graphite);
        }
    }

    @Test
    public void reportsBooleanGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(true)), map(), map(), map(), map());
        reporter.report(map("gauge", gauge(false)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "1", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.gauge", "0", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsCounters() throws Exception {
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(100L);
        reporter.report(map(), map("counter", counter), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.counter.count", "100", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsHistograms() throws Exception {
        final Histogram histogram = Mockito.mock(Histogram.class);
        Mockito.when(histogram.getCount()).thenReturn(1L);
        final Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(snapshot.getMax()).thenReturn(2L);
        Mockito.when(snapshot.getMean()).thenReturn(3.0);
        Mockito.when(snapshot.getMin()).thenReturn(4L);
        Mockito.when(snapshot.getStdDev()).thenReturn(5.0);
        Mockito.when(snapshot.getMedian()).thenReturn(6.0);
        Mockito.when(snapshot.get75thPercentile()).thenReturn(7.0);
        Mockito.when(snapshot.get95thPercentile()).thenReturn(8.0);
        Mockito.when(snapshot.get98thPercentile()).thenReturn(9.0);
        Mockito.when(snapshot.get99thPercentile()).thenReturn(10.0);
        Mockito.when(snapshot.get999thPercentile()).thenReturn(11.0);
        Mockito.when(histogram.getSnapshot()).thenReturn(snapshot);
        reporter.report(map(), map(), map("histogram", histogram), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.histogram.count", "1", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.max", "2", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.mean", "3.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.min", "4", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.stddev", "5.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p50", "6.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p75", "7.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p95", "8.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p98", "9.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p99", "10.00", timestamp);
        inOrder.verify(graphite).send("prefix.histogram.p999", "11.00", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsMeters() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(2.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getMeanRate()).thenReturn(5.0);
        reporter.report(map(), map(), map(), map("meter", meter), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.meter.count", "1", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m1_rate", "2.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m5_rate", "3.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m15_rate", "4.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.mean_rate", "5.00", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsMetersInMinutes() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(2.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getMeanRate()).thenReturn(5.0);
        minuteRateReporter.report(this.map(), this.map(), this.map(), this.map("meter", meter), this.map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.meter.count", "1", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m1_rate", "120.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m5_rate", "180.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m15_rate", "240.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.mean_rate", "300.00", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void reportsTimers() throws Exception {
        final Timer timer = Mockito.mock(Timer.class);
        Mockito.when(timer.getCount()).thenReturn(1L);
        Mockito.when(timer.getMeanRate()).thenReturn(2.0);
        Mockito.when(timer.getOneMinuteRate()).thenReturn(3.0);
        Mockito.when(timer.getFiveMinuteRate()).thenReturn(4.0);
        Mockito.when(timer.getFifteenMinuteRate()).thenReturn(5.0);
        final Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(snapshot.getMax()).thenReturn(TimeUnit.MILLISECONDS.toNanos(100));
        Mockito.when(snapshot.getMean()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(200))));
        Mockito.when(snapshot.getMin()).thenReturn(TimeUnit.MILLISECONDS.toNanos(300));
        Mockito.when(snapshot.getStdDev()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(400))));
        Mockito.when(snapshot.getMedian()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(500))));
        Mockito.when(snapshot.get75thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(600))));
        Mockito.when(snapshot.get95thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(700))));
        Mockito.when(snapshot.get98thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(800))));
        Mockito.when(snapshot.get99thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(900))));
        Mockito.when(snapshot.get999thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(1000))));
        Mockito.when(timer.getSnapshot()).thenReturn(snapshot);
        reporter.report(map(), map(), map(), map(), map("timer", timer));
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.timer.max", "100.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.mean", "200.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.min", "300.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.stddev", "400.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p50", "500.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p75", "600.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p95", "700.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p98", "800.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p99", "900.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.p999", "1000.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.count", "1", timestamp);
        inOrder.verify(graphite).send("prefix.timer.m1_rate", "3.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.m5_rate", "4.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.m15_rate", "5.00", timestamp);
        inOrder.verify(graphite).send("prefix.timer.mean_rate", "2.00", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
        reporter.close();
    }

    @Test
    public void closesConnectionIfGraphiteIsUnavailable() throws Exception {
        Mockito.doThrow(new UnknownHostException("UNKNOWN-HOST")).when(graphite).connect();
        reporter.report(map("gauge", gauge(1)), map(), map(), map(), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void closesConnectionOnReporterStop() throws Exception {
        reporter.stop();
        Mockito.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }

    @Test
    public void disabledMetricsAttribute() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(2.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getMeanRate()).thenReturn(5.0);
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(11L);
        Set<MetricAttribute> disabledMetricAttributes = EnumSet.of(M15_RATE, M5_RATE);
        GraphiteReporter reporterWithdisabledMetricAttributes = GraphiteReporter.forRegistry(registry).withClock(clock).prefixedWith("prefix").convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(disabledMetricAttributes).build(graphite);
        reporterWithdisabledMetricAttributes.report(map(), map("counter", counter), map(), map("meter", meter), map());
        final InOrder inOrder = Mockito.inOrder(graphite);
        inOrder.verify(graphite).connect();
        inOrder.verify(graphite).send("prefix.counter.count", "11", timestamp);
        inOrder.verify(graphite).send("prefix.meter.count", "1", timestamp);
        inOrder.verify(graphite).send("prefix.meter.m1_rate", "2.00", timestamp);
        inOrder.verify(graphite).send("prefix.meter.mean_rate", "5.00", timestamp);
        inOrder.verify(graphite).flush();
        inOrder.verify(graphite).close();
        Mockito.verifyNoMoreInteractions(graphite);
    }
}

