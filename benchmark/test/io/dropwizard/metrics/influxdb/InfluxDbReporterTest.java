/**
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
package io.dropwizard.metrics.influxdb;


import io.dropwizard.metrics.Counter;
import io.dropwizard.metrics.Histogram;
import io.dropwizard.metrics.Meter;
import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.Snapshot;
import io.dropwizard.metrics.Timer;
import io.dropwizard.metrics.influxdb.data.InfluxDbPoint;
import io.dropwizard.metrics.influxdb.data.InfluxDbWriteObject;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class InfluxDbReporterTest {
    @Mock
    private InfluxDbSender influxDb;

    @Mock
    private InfluxDbWriteObject writeObject;

    @Mock
    private MetricRegistry registry;

    private InfluxDbReporter reporter;

    @Test
    public void reportsCounters() throws Exception {
        final Counter counter = Mockito.mock(Counter.class);
        Mockito.when(counter.getCount()).thenReturn(100L);
        reporter.report(this.map(), this.map("counter", counter), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        System.out.println(("point = " + point));
        /* assertThat(point.getMeasurement()).isEqualTo("counter");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("count", 100L));
         */
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
        reporter.report(this.map(), this.map(), this.map("histogram", histogram), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("histogram");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(13);
        assertThat(point.getFields()).contains(entry("max", 2L));
        assertThat(point.getFields()).contains(entry("mean", 3.0));
        assertThat(point.getFields()).contains(entry("min", 4L));
        assertThat(point.getFields()).contains(entry("std-dev", 5.0));
        assertThat(point.getFields()).contains(entry("median", 6.0));
        assertThat(point.getFields()).contains(entry("75-percentile", 7.0));
        assertThat(point.getFields()).contains(entry("95-percentile", 8.0));
        assertThat(point.getFields()).contains(entry("98-percentile", 9.0));
        assertThat(point.getFields()).contains(entry("99-percentile", 10.0));
        assertThat(point.getFields()).contains(entry("999-percentile", 11.0));
         */
    }

    @Test
    public void reportsMeters() throws Exception {
        final Meter meter = Mockito.mock(Meter.class);
        Mockito.when(meter.getCount()).thenReturn(1L);
        Mockito.when(meter.getOneMinuteRate()).thenReturn(2.0);
        Mockito.when(meter.getFiveMinuteRate()).thenReturn(3.0);
        Mockito.when(meter.getFifteenMinuteRate()).thenReturn(4.0);
        Mockito.when(meter.getMeanRate()).thenReturn(5.0);
        reporter.report(this.map(), this.map(), this.map(), this.map("meter", meter), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("meter");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(5);
        assertThat(point.getFields()).contains(entry("count", 1L));
        assertThat(point.getFields()).contains(entry("one-minute", 2.0));
        assertThat(point.getFields()).contains(entry("five-minute", 3.0));
        assertThat(point.getFields()).contains(entry("fifteen-minute", 4.0));
        assertThat(point.getFields()).contains(entry("mean-rate", 5.0));
         */
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
        Mockito.when(snapshot.getMin()).thenReturn(TimeUnit.MILLISECONDS.toNanos(100));
        Mockito.when(snapshot.getMean()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(200))));
        Mockito.when(snapshot.getMax()).thenReturn(TimeUnit.MILLISECONDS.toNanos(300));
        Mockito.when(snapshot.getStdDev()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(400))));
        Mockito.when(snapshot.getMedian()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(500))));
        Mockito.when(snapshot.get75thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(600))));
        Mockito.when(snapshot.get95thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(700))));
        Mockito.when(snapshot.get98thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(800))));
        Mockito.when(snapshot.get99thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(900))));
        Mockito.when(snapshot.get999thPercentile()).thenReturn(((double) (TimeUnit.MILLISECONDS.toNanos(1000))));
        Mockito.when(timer.getSnapshot()).thenReturn(snapshot);
        reporter.report(this.map(), this.map(), this.map(), this.map(), map("timer", timer));
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("timer");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(17);
        assertThat(point.getFields()).contains(entry("count", 1L));
        assertThat(point.getFields()).contains(entry("mean-rate", 2.0));
        assertThat(point.getFields()).contains(entry("one-minute", 3.0));
        assertThat(point.getFields()).contains(entry("five-minute", 4.0));
        assertThat(point.getFields()).contains(entry("fifteen-minute", 5.0));
        assertThat(point.getFields()).contains(entry("min", 100.0));
        assertThat(point.getFields()).contains(entry("mean", 200.0));
        assertThat(point.getFields()).contains(entry("max", 300.0));
        assertThat(point.getFields()).contains(entry("std-dev", 400.0));
        assertThat(point.getFields()).contains(entry("median", 500.0));
        assertThat(point.getFields()).contains(entry("75-percentile", 600.0));
        assertThat(point.getFields()).contains(entry("95-percentile", 700.0));
        assertThat(point.getFields()).contains(entry("98-percentile", 800.0));
        assertThat(point.getFields()).contains(entry("99-percentile", 900.0));
        assertThat(point.getFields()).contains(entry("999-percentile", 1000.0));
         */
    }

    @Test
    public void reportsIntegerGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1)), this.map(), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("gauge");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("value", 1));
         */
    }

    @Test
    public void reportsLongGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1L)), this.map(), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("gauge");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("value", 1L));
         */
    }

    @Test
    public void reportsFloatGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1.1F)), this.map(), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("gauge");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("value", 1.1f));
         */
    }

    @Test
    public void reportsDoubleGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(1.1)), this.map(), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("gauge");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("value", 1.1));
         */
    }

    @Test
    public void reportsByteGaugeValues() throws Exception {
        reporter.report(map("gauge", gauge(((byte) (1)))), this.map(), this.map(), this.map(), this.map());
        final ArgumentCaptor<InfluxDbPoint> influxDbPointCaptor = ArgumentCaptor.forClass(InfluxDbPoint.class);
        Mockito.verify(influxDb, Mockito.atLeastOnce()).appendPoints(influxDbPointCaptor.capture());
        InfluxDbPoint point = influxDbPointCaptor.getValue();
        /* assertThat(point.getMeasurement()).isEqualTo("gauge");
        assertThat(point.getFields()).isNotEmpty();
        assertThat(point.getFields()).hasSize(1);
        assertThat(point.getFields()).contains(entry("value", (byte) 1));
         */
    }
}

