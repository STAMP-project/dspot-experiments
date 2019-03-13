package com.codahale.metrics;


import MetricAttribute.COUNT;
import MetricAttribute.M15_RATE;
import MetricAttribute.M5_RATE;
import MetricAttribute.MAX;
import MetricAttribute.MIN;
import MetricAttribute.P50;
import MetricAttribute.P95;
import MetricAttribute.P999;
import MetricAttribute.STDDEV;
import MetricFilter.ALL;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;


public class ConsoleReporterTest {
    private final Locale locale = Locale.US;

    private final TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");

    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    private final Clock clock = Mockito.mock(Clock.class);

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private final PrintStream output = new PrintStream(bytes);

    private final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).outputTo(output).formattedFor(locale).withClock(clock).formattedFor(timeZone).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).build();

    private String dateHeader;

    @Test
    public void reportsGaugeValues() throws Exception {
        final Gauge<Integer> gauge = () -> 1;
        reporter.report(map("gauge", gauge), map(), map(), map(), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Gauges ----------------------------------------------------------------------", "gauge", "             value = 1", "", ""));
    }

    @Test
    public void reportsCounterValues() throws Exception {
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(100L);
        reporter.report(map(), map("test.counter", counter), map(), map(), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Counters --------------------------------------------------------------------", "test.counter", "             count = 100", "", ""));
    }

    @Test
    public void reportsHistogramValues() throws Exception {
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
        reporter.report(map(), map(), map("test.histogram", histogram), map(), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Histograms ------------------------------------------------------------------", "test.histogram", "             count = 1", "               min = 4", "               max = 2", "              mean = 3.00", "            stddev = 5.00", "            median = 6.00", "              75% <= 7.00", "              95% <= 8.00", "              98% <= 9.00", "              99% <= 10.00", "            99.9% <= 11.00", "", ""));
    }

    @Test
    public void reportsMeterValues() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getMeanRate()).thenReturn(2.0);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(5.0);
        reporter.report(map(), map(), map(), map("test.meter", meter), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Meters ----------------------------------------------------------------------", "test.meter", "             count = 1", "         mean rate = 2.00 events/second", "     1-minute rate = 3.00 events/second", "     5-minute rate = 4.00 events/second", "    15-minute rate = 5.00 events/second", "", ""));
    }

    @Test
    public void reportsTimerValues() throws Exception {
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
        reporter.report(map(), map(), map(), map(), map("test.another.timer", timer));
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Timers ----------------------------------------------------------------------", "test.another.timer", "             count = 1", "         mean rate = 2.00 calls/second", "     1-minute rate = 3.00 calls/second", "     5-minute rate = 4.00 calls/second", "    15-minute rate = 5.00 calls/second", "               min = 300.00 milliseconds", "               max = 100.00 milliseconds", "              mean = 200.00 milliseconds", "            stddev = 400.00 milliseconds", "            median = 500.00 milliseconds", "              75% <= 600.00 milliseconds", "              95% <= 700.00 milliseconds", "              98% <= 800.00 milliseconds", "              99% <= 900.00 milliseconds", "            99.9% <= 1000.00 milliseconds", "", ""));
    }

    @Test
    public void reportMeterWithDisabledAttributes() throws Exception {
        Set<MetricAttribute> disabledMetricAttributes = EnumSet.of(M15_RATE, M5_RATE, COUNT);
        final ConsoleReporter customReporter = ConsoleReporter.forRegistry(registry).outputTo(output).formattedFor(locale).withClock(clock).formattedFor(timeZone).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(disabledMetricAttributes).build();
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getMeanRate()).thenReturn(2.0);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(5.0);
        customReporter.report(map(), map(), map(), map("test.meter", meter), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Meters ----------------------------------------------------------------------", "test.meter", "         mean rate = 2.00 events/second", "     1-minute rate = 3.00 events/second", "", ""));
    }

    @Test
    public void reportTimerWithDisabledAttributes() throws Exception {
        Set<MetricAttribute> disabledMetricAttributes = EnumSet.of(P50, P999, M5_RATE, MAX);
        final ConsoleReporter customReporter = ConsoleReporter.forRegistry(registry).outputTo(output).formattedFor(locale).withClock(clock).formattedFor(timeZone).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(disabledMetricAttributes).build();
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
        customReporter.report(map(), map(), map(), map(), map("test.another.timer", timer));
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Timers ----------------------------------------------------------------------", "test.another.timer", "             count = 1", "         mean rate = 2.00 calls/second", "     1-minute rate = 3.00 calls/second", "    15-minute rate = 5.00 calls/second", "               min = 300.00 milliseconds", "              mean = 200.00 milliseconds", "            stddev = 400.00 milliseconds", "              75% <= 600.00 milliseconds", "              95% <= 700.00 milliseconds", "              98% <= 800.00 milliseconds", "              99% <= 900.00 milliseconds", "", ""));
    }

    @Test
    public void reportHistogramWithDisabledAttributes() throws Exception {
        Set<MetricAttribute> disabledMetricAttributes = EnumSet.of(MIN, MAX, STDDEV, P95);
        final ConsoleReporter customReporter = ConsoleReporter.forRegistry(registry).outputTo(output).formattedFor(locale).withClock(clock).formattedFor(timeZone).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).disabledMetricAttributes(disabledMetricAttributes).build();
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
        customReporter.report(map(), map(), map("test.histogram", histogram), map(), map());
        assertThat(consoleOutput()).isEqualTo(lines(dateHeader, "", "-- Histograms ------------------------------------------------------------------", "test.histogram", "             count = 1", "              mean = 3.00", "            median = 6.00", "              75% <= 7.00", "              98% <= 9.00", "              99% <= 10.00", "            99.9% <= 11.00", "", ""));
    }
}

