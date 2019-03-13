package com.codahale.metrics.collectd;


import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.util.concurrent.TimeUnit;
import org.collectd.api.ValueList;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class CollectdReporterTest {
    @ClassRule
    public static Receiver receiver = new Receiver(25826);

    private final MetricRegistry registry = new MetricRegistry();

    private CollectdReporter reporter;

    @Test
    public void reportsByteGauges() throws Exception {
        reportsGauges(((byte) (128)));
    }

    @Test
    public void reportsShortGauges() throws Exception {
        reportsGauges(((short) (2048)));
    }

    @Test
    public void reportsIntegerGauges() throws Exception {
        reportsGauges(42);
    }

    @Test
    public void reportsLongGauges() throws Exception {
        reportsGauges(Long.MAX_VALUE);
    }

    @Test
    public void reportsFloatGauges() throws Exception {
        reportsGauges(0.25);
    }

    @Test
    public void reportsDoubleGauges() throws Exception {
        reportsGauges(0.125);
    }

    @Test
    public void reportsBooleanGauges() throws Exception {
        reporter.report(map("gauge", ((Gauge) (() -> true))), map(), map(), map(), map());
        ValueList data = CollectdReporterTest.receiver.next();
        assertThat(data.getValues()).containsExactly(1.0);
        reporter.report(map("gauge", ((Gauge) (() -> false))), map(), map(), map(), map());
        data = CollectdReporterTest.receiver.next();
        assertThat(data.getValues()).containsExactly(0.0);
    }

    @Test
    public void doesNotReportStringGauges() throws Exception {
        reporter.report(map("unsupported", ((Gauge) (() -> "value"))), map(), map(), map(), map());
        assertThat(CollectdReporterTest.receiver.next()).isNull();
    }

    @Test
    public void reportsCounters() throws Exception {
        Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(42L);
        reporter.report(map(), map("api.rest.requests.count", counter), map(), map(), map());
        ValueList data = CollectdReporterTest.receiver.next();
        assertThat(data.getValues()).containsExactly(42.0);
    }

    @Test
    public void reportsMeters() throws Exception {
        Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(2.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getMeanRate()).thenReturn(5.0);
        reporter.report(map(), map(), map(), map("api.rest.requests", meter), map());
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(1.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(2.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(3.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(4.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(5.0);
    }

    @Test
    public void reportsHistograms() throws Exception {
        Histogram histogram = Mockito.mock(Histogram.class);
        Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(histogram.getCount()).thenReturn(1L);
        Mockito.when(histogram.getSnapshot()).thenReturn(snapshot);
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
        reporter.report(map(), map(), map("histogram", histogram), map(), map());
        for (int i = 1; i <= 11; i++) {
            assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(((double) (i)));
        }
    }

    @Test
    public void reportsTimers() throws Exception {
        Timer timer = Mockito.mock(Timer.class);
        Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(timer.getSnapshot()).thenReturn(snapshot);
        Mockito.when(timer.getCount()).thenReturn(1L);
        Mockito.when(timer.getSnapshot()).thenReturn(snapshot);
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
        Mockito.when(timer.getOneMinuteRate()).thenReturn(11.0);
        Mockito.when(timer.getFiveMinuteRate()).thenReturn(12.0);
        Mockito.when(timer.getFifteenMinuteRate()).thenReturn(13.0);
        Mockito.when(timer.getMeanRate()).thenReturn(14.0);
        reporter.report(map(), map(), map(), map(), map("timer", timer));
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(1.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(100.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(200.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(300.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(400.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(500.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(600.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(700.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(800.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(900.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(1000.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(11.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(12.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(13.0);
        assertThat(CollectdReporterTest.receiver.next().getValues()).containsExactly(14.0);
    }

    @Test
    public void sanitizesMetricName() throws Exception {
        Counter counter = registry.counter("dash-illegal.slash/illegal");
        counter.inc();
        reporter.report();
        ValueList values = CollectdReporterTest.receiver.next();
        assertThat(values.getPlugin()).isEqualTo("dash_illegal.slash_illegal");
    }

    @Test
    public void testUnableSetSecurityLevelToSignWithoutUsername() {
        assertThatIllegalArgumentException().isThrownBy(() -> CollectdReporter.forRegistry(registry).withHostName("eddie").withSecurityLevel(SecurityLevel.SIGN).withPassword("t1_g3r").build(new Sender("localhost", 25826))).withMessage("username is required for securityLevel: SIGN");
    }

    @Test
    public void testUnableSetSecurityLevelToSignWithoutPassword() {
        assertThatIllegalArgumentException().isThrownBy(() -> CollectdReporter.forRegistry(registry).withHostName("eddie").withSecurityLevel(SecurityLevel.SIGN).withUsername("scott").build(new Sender("localhost", 25826))).withMessage("password is required for securityLevel: SIGN");
    }
}

