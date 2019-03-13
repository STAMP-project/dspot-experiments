package com.codahale.metrics;


import MetricFilter.ALL;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class CsvReporterTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    private final Clock clock = Mockito.mock(Clock.class);

    private File dataDirectory;

    private CsvReporter reporter;

    @Test
    public void reportsGaugeValues() throws Exception {
        final Gauge<Integer> gauge = () -> 1;
        reporter.report(map("gauge", gauge), map(), map(), map(), map());
        assertThat(fileContents("gauge.csv")).isEqualTo(csv("t,value", "19910191,1"));
    }

    @Test
    public void reportsCounterValues() throws Exception {
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(100L);
        reporter.report(map(), map("test.counter", counter), map(), map(), map());
        assertThat(fileContents("test.counter.csv")).isEqualTo(csv("t,count", "19910191,100"));
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
        assertThat(fileContents("test.histogram.csv")).isEqualTo(csv("t,count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999", "19910191,1,2,3.000000,4,5.000000,6.000000,7.000000,8.000000,9.000000,10.000000,11.000000"));
    }

    @Test
    public void reportsMeterValues() throws Exception {
        final Meter meter = mockMeter();
        reporter.report(map(), map(), map(), map("test.meter", meter), map());
        assertThat(fileContents("test.meter.csv")).isEqualTo(csv("t,count,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit", "19910191,1,2.000000,3.000000,4.000000,5.000000,events/second"));
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
        assertThat(fileContents("test.another.timer.csv")).isEqualTo(csv("t,count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit", "19910191,1,100.000000,200.000000,300.000000,400.000000,500.000000,600.000000,700.000000,800.000000,900.000000,1000.000000,2.000000,3.000000,4.000000,5.000000,calls/second,milliseconds"));
    }

    @Test
    public void testCsvFileProviderIsUsed() {
        CsvFileProvider fileProvider = Mockito.mock(CsvFileProvider.class);
        Mockito.when(fileProvider.getFile(dataDirectory, "gauge")).thenReturn(new File(dataDirectory, "guage.csv"));
        CsvReporter reporter = CsvReporter.forRegistry(registry).withCsvFileProvider(fileProvider).build(dataDirectory);
        final Gauge<Integer> gauge = () -> 1;
        reporter.report(map("gauge", gauge), map(), map(), map(), map());
        Mockito.verify(fileProvider).getFile(dataDirectory, "gauge");
    }

    @Test
    public void itFormatsWithCustomSeparator() throws Exception {
        final Meter meter = mockMeter();
        CsvReporter customSeparatorReporter = CsvReporter.forRegistry(registry).formatFor(Locale.US).withSeparator("|").convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).withClock(clock).filter(ALL).build(dataDirectory);
        customSeparatorReporter.report(map(), map(), map(), map("test.meter", meter), map());
        assertThat(fileContents("test.meter.csv")).isEqualTo(csv("t,count,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit", "19910191|1|2.000000|3.000000|4.000000|5.000000|events/second"));
    }
}

