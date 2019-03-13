/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon.statistics;


import Version.POM_VERSION;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.ffwd.FastForward;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FastForwardReporterTest {
    private final FastForward ffwd = Mockito.mock(FastForward.class);

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private ScheduledExecutorService executor;

    private FastForwardReporter reporter;

    @Test
    public void testGauges() throws Exception {
        metricRegistry.register("some.gauge1", ((Gauge<Integer>) (() -> 1)));
        metricRegistry.register("some.gauge2", ((Gauge<Integer>) (() -> 2)));
        reporter.reportOnce();
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.allOf(FastForwardReporterTest.hasKey("helios.test"), FastForwardReporterTest.containsAttributes("what", "some.gauge1", "metric_type", "gauge"), FastForwardReporterTest.hasValue(1))));
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.allOf(FastForwardReporterTest.hasKey("helios.test"), FastForwardReporterTest.containsAttributes("what", "some.gauge2", "metric_type", "gauge"), FastForwardReporterTest.hasValue(2))));
    }

    @Test
    public void testCounter() throws Exception {
        metricRegistry.counter("counting.is.fun").inc(7982);
        reporter.reportOnce();
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.allOf(FastForwardReporterTest.hasKey("helios.test"), FastForwardReporterTest.containsAttributes("what", "counting.is.fun", "metric_type", "counter"), FastForwardReporterTest.hasValue(7982))));
    }

    @Test
    public void testMeter() throws Exception {
        metricRegistry.meter("the-meter");
        reporter.reportOnce();
        verifyMeterStats("the-meter", "meter");
    }

    @Test
    public void testHistogram() throws Exception {
        final Histogram h = metricRegistry.histogram("histo.gram");
        IntStream.range(1, 10).forEach(h::update);
        reporter.reportOnce();
        verifyHistogramStats("histo.gram", "histogram");
    }

    @Test
    public void testTimer() throws Exception {
        metricRegistry.timer("blah-timer");
        reporter.reportOnce();
        verifyHistogramStats("blah-timer", "timer");
        verifyMeterStats("blah-timer", "timer");
    }

    @Test
    public void testAttributesIncludeHeliosVersion() throws Exception {
        metricRegistry.register("something", ((Gauge<Integer>) (() -> 1)));
        reporter.reportOnce();
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.containsAttributes("helios_version", POM_VERSION)));
    }

    @Test
    public void testAttributesIncludeAdditionalAttributes() throws Exception {
        // a counter to keep track of how often the Supplier is called
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Map<String, String>> additionalAttributes = () -> {
            final int count = counter.incrementAndGet();
            return ImmutableMap.of("foo", "bar", "counter", String.valueOf(count));
        };
        this.reporter = new FastForwardReporter(ffwd, metricRegistry, executor, "helios.test", 30, TimeUnit.SECONDS, additionalAttributes);
        metricRegistry.register("gauge1", ((Gauge<Integer>) (() -> 1)));
        metricRegistry.register("gauge2", ((Gauge<Integer>) (() -> 2)));
        reporter.reportOnce();
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.containsAttributes("what", "gauge1", "foo", "bar", "counter", "1")));
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.containsAttributes("what", "gauge2", "foo", "bar", "counter", "1")));
        Mockito.reset(ffwd);
        reporter.reportOnce();
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.containsAttributes("what", "gauge1", "foo", "bar", "counter", "2")));
        Mockito.verify(ffwd).send(ArgumentMatchers.argThat(FastForwardReporterTest.containsAttributes("what", "gauge2", "foo", "bar", "counter", "2")));
    }
}

