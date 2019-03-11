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


import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;


public class ScheduledReporterTest {
    private final Gauge gauge = Mockito.mock(Gauge.class);

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    private final MetricRegistry registry = new MetricRegistry();

    private final ScheduledReporter reporter = Mockito.spy(new ScheduledReporter(registry, "example", MetricFilter.ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS) {
        @Override
        public void report(SortedMap<MetricName, Gauge> gauges, SortedMap<MetricName, Counter> counters, SortedMap<MetricName, Histogram> histograms, SortedMap<MetricName, Meter> meters, SortedMap<MetricName, Timer> timers) {
            // nothing doing!
        }
    });

    @Test
    public void pollsPeriodically() throws Exception {
        Thread.sleep(500);
        Mockito.verify(reporter, Mockito.times(2)).report(map("gauge", gauge), map("counter", counter), map("histogram", histogram), map("meter", meter), map("timer", timer));
    }
}

