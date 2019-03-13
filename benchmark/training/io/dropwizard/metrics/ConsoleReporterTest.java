/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.metrics;


import MetricFilter.ALL;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;


public class ConsoleReporterTest {
    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    private final Clock clock = Mockito.mock(Clock.class);

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private final PrintStream output = new PrintStream(bytes);

    private final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).outputTo(output).formattedFor(Locale.US).withClock(clock).formattedFor(TimeZone.getTimeZone("PST")).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).filter(ALL).build();

    @Test
    public void reportsGaugeValues() throws Exception {
        final Gauge gauge = Mockito.mock(Gauge.class);
        Mockito.when(gauge.getValue()).thenReturn(1);
        reporter.report(map("gauge", gauge), this.map(), this.map(), this.map(), this.map());
        assertThat(consoleOutput()).isEqualTo(lines("3/17/13 6:04:36 PM =============================================================", "", "-- Gauges ----------------------------------------------------------------------", "gauge", "             value = 1", "", ""));
    }

    @Test
    public void reportsCounterValues() throws Exception {
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(100L);
        reporter.report(this.map(), map("test.counter", counter), this.map(), this.map(), this.map());
        assertThat(consoleOutput()).isEqualTo(lines("3/17/13 6:04:36 PM =============================================================", "", "-- Counters --------------------------------------------------------------------", "test.counter", "             count = 100", "", ""));
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
        reporter.report(this.map(), this.map(), map("test.histogram", histogram), this.map(), this.map());
        assertThat(consoleOutput()).isEqualTo(lines("3/17/13 6:04:36 PM =============================================================", "", "-- Histograms ------------------------------------------------------------------", "test.histogram", "             count = 1", "               min = 4", "               max = 2", "              mean = 3.00", "            stddev = 5.00", "            median = 6.00", "              75% <= 7.00", "              95% <= 8.00", "              98% <= 9.00", "              99% <= 10.00", "            99.9% <= 11.00", "", ""));
    }

    @Test
    public void reportsMeterValues() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getMeanRate()).thenReturn(2.0);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(5.0);
        reporter.report(this.map(), this.map(), this.map(), map("test.meter", meter), this.map());
        assertThat(consoleOutput()).isEqualTo(lines("3/17/13 6:04:36 PM =============================================================", "", "-- Meters ----------------------------------------------------------------------", "test.meter", "             count = 1", "         mean rate = 2.00 events/second", "     1-minute rate = 3.00 events/second", "     5-minute rate = 4.00 events/second", "    15-minute rate = 5.00 events/second", "", ""));
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
        reporter.report(this.map(), this.map(), this.map(), this.map(), map("test.another.timer", timer));
        assertThat(consoleOutput()).isEqualTo(lines("3/17/13 6:04:36 PM =============================================================", "", "-- Timers ----------------------------------------------------------------------", "test.another.timer", "             count = 1", "         mean rate = 2.00 calls/second", "     1-minute rate = 3.00 calls/second", "     5-minute rate = 4.00 calls/second", "    15-minute rate = 5.00 calls/second", "               min = 300.00 milliseconds", "               max = 100.00 milliseconds", "              mean = 200.00 milliseconds", "            stddev = 400.00 milliseconds", "            median = 500.00 milliseconds", "              75% <= 600.00 milliseconds", "              95% <= 700.00 milliseconds", "              98% <= 800.00 milliseconds", "              99% <= 900.00 milliseconds", "            99.9% <= 1000.00 milliseconds", "", ""));
    }
}

