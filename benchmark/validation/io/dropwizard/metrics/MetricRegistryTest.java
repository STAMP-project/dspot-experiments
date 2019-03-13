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


import org.junit.Test;
import org.mockito.Mockito;


public class MetricRegistryTest {
    private static final MetricName TIMER2 = MetricName.build("timer");

    private static final MetricName METER2 = MetricName.build("meter");

    private static final MetricName HISTOGRAM2 = MetricName.build("histogram");

    private static final MetricName COUNTER = MetricName.build("counter");

    private static final MetricName COUNTER2 = MetricName.build("counter2");

    private static final MetricName GAUGE = MetricName.build("gauge");

    private static final MetricName GAUGE2 = MetricName.build("gauge2");

    private static final MetricName THING = MetricName.build("thing");

    private final MetricRegistryListener listener = Mockito.mock(MetricRegistryListener.class);

    private final MetricRegistry registry = new MetricRegistry();

    @SuppressWarnings("unchecked")
    private final Gauge<String> gauge = Mockito.mock(Gauge.class);

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    @Test
    public void registeringAGaugeTriggersANotification() throws Exception {
        assertThat(registry.register(MetricRegistryTest.THING, gauge)).isEqualTo(gauge);
        Mockito.verify(listener).onGaugeAdded(MetricRegistryTest.THING, gauge);
    }

    @Test
    public void removingAGaugeTriggersANotification() throws Exception {
        registry.register(MetricRegistryTest.THING, gauge);
        assertThat(registry.remove(MetricRegistryTest.THING)).isTrue();
        Mockito.verify(listener).onGaugeRemoved(MetricRegistryTest.THING);
    }

    @Test
    public void registeringACounterTriggersANotification() throws Exception {
        assertThat(registry.register(MetricRegistryTest.THING, counter)).isEqualTo(counter);
        Mockito.verify(listener).onCounterAdded(MetricRegistryTest.THING, counter);
    }

    @Test
    public void accessingACounterRegistersAndReusesTheCounter() throws Exception {
        final Counter counter1 = registry.counter(MetricRegistryTest.THING);
        final Counter counter2 = registry.counter(MetricRegistryTest.THING);
        assertThat(counter1).isSameAs(counter2);
        Mockito.verify(listener).onCounterAdded(MetricRegistryTest.THING, counter1);
    }

    @Test
    public void removingACounterTriggersANotification() throws Exception {
        registry.register(MetricRegistryTest.THING, counter);
        assertThat(registry.remove(MetricRegistryTest.THING)).isTrue();
        Mockito.verify(listener).onCounterRemoved(MetricRegistryTest.THING);
    }

    @Test
    public void registeringAHistogramTriggersANotification() throws Exception {
        assertThat(registry.register(MetricRegistryTest.THING, histogram)).isEqualTo(histogram);
        Mockito.verify(listener).onHistogramAdded(MetricRegistryTest.THING, histogram);
    }

    @Test
    public void accessingAHistogramRegistersAndReusesIt() throws Exception {
        final Histogram histogram1 = registry.histogram(MetricRegistryTest.THING);
        final Histogram histogram2 = registry.histogram(MetricRegistryTest.THING);
        assertThat(histogram1).isSameAs(histogram2);
        Mockito.verify(listener).onHistogramAdded(MetricRegistryTest.THING, histogram1);
    }

    @Test
    public void removingAHistogramTriggersANotification() throws Exception {
        registry.register(MetricRegistryTest.THING, histogram);
        assertThat(registry.remove(MetricRegistryTest.THING)).isTrue();
        Mockito.verify(listener).onHistogramRemoved(MetricRegistryTest.THING);
    }

    @Test
    public void registeringAMeterTriggersANotification() throws Exception {
        assertThat(registry.register(MetricRegistryTest.THING, meter)).isEqualTo(meter);
        Mockito.verify(listener).onMeterAdded(MetricRegistryTest.THING, meter);
    }

    @Test
    public void accessingAMeterRegistersAndReusesIt() throws Exception {
        final Meter meter1 = registry.meter(MetricRegistryTest.THING);
        final Meter meter2 = registry.meter(MetricRegistryTest.THING);
        assertThat(meter1).isSameAs(meter2);
        Mockito.verify(listener).onMeterAdded(MetricRegistryTest.THING, meter1);
    }

    @Test
    public void removingAMeterTriggersANotification() throws Exception {
        registry.register(MetricRegistryTest.THING, meter);
        assertThat(registry.remove(MetricRegistryTest.THING)).isTrue();
        Mockito.verify(listener).onMeterRemoved(MetricRegistryTest.THING);
    }

    @Test
    public void registeringATimerTriggersANotification() throws Exception {
        assertThat(registry.register(MetricRegistryTest.THING, timer)).isEqualTo(timer);
        Mockito.verify(listener).onTimerAdded(MetricRegistryTest.THING, timer);
    }

    @Test
    public void accessingATimerRegistersAndReusesIt() throws Exception {
        final Timer timer1 = registry.timer(MetricRegistryTest.THING);
        final Timer timer2 = registry.timer(MetricRegistryTest.THING);
        assertThat(timer1).isSameAs(timer2);
        Mockito.verify(listener).onTimerAdded(MetricRegistryTest.THING, timer1);
    }

    @Test
    public void removingATimerTriggersANotification() throws Exception {
        registry.register(MetricRegistryTest.THING, timer);
        assertThat(registry.remove(MetricRegistryTest.THING)).isTrue();
        Mockito.verify(listener).onTimerRemoved(MetricRegistryTest.THING);
    }

    @Test
    public void addingAListenerWithExistingMetricsCatchesItUp() throws Exception {
        registry.register(MetricRegistryTest.GAUGE2, gauge);
        registry.register(MetricRegistryTest.COUNTER2, counter);
        registry.register(MetricRegistryTest.HISTOGRAM2, histogram);
        registry.register(MetricRegistryTest.METER2, meter);
        registry.register(MetricRegistryTest.TIMER2, timer);
        final MetricRegistryListener other = Mockito.mock(MetricRegistryListener.class);
        registry.addListener(other);
        Mockito.verify(other).onGaugeAdded(MetricRegistryTest.GAUGE2, gauge);
        Mockito.verify(other).onCounterAdded(MetricRegistryTest.COUNTER2, counter);
        Mockito.verify(other).onHistogramAdded(MetricRegistryTest.HISTOGRAM2, histogram);
        Mockito.verify(other).onMeterAdded(MetricRegistryTest.METER2, meter);
        Mockito.verify(other).onTimerAdded(MetricRegistryTest.TIMER2, timer);
    }

    @Test
    public void aRemovedListenerDoesNotReceiveUpdates() throws Exception {
        registry.register(MetricRegistryTest.GAUGE, gauge);
        registry.removeListener(listener);
        registry.register(MetricRegistryTest.GAUGE2, gauge);
        Mockito.verify(listener, Mockito.never()).onGaugeAdded(MetricRegistryTest.GAUGE2, gauge);
    }

    @Test
    public void hasAMapOfRegisteredGauges() throws Exception {
        registry.register(MetricRegistryTest.GAUGE2, gauge);
        assertThat(registry.getGauges()).contains(entry(MetricRegistryTest.GAUGE2, gauge));
    }

    @Test
    public void hasAMapOfRegisteredCounters() throws Exception {
        registry.register(MetricRegistryTest.COUNTER2, counter);
        assertThat(registry.getCounters()).contains(entry(MetricRegistryTest.COUNTER2, counter));
    }

    @Test
    public void hasAMapOfRegisteredHistograms() throws Exception {
        registry.register(MetricRegistryTest.HISTOGRAM2, histogram);
        assertThat(registry.getHistograms()).contains(entry(MetricRegistryTest.HISTOGRAM2, histogram));
    }

    @Test
    public void hasAMapOfRegisteredMeters() throws Exception {
        registry.register(MetricRegistryTest.METER2, meter);
        assertThat(registry.getMeters()).contains(entry(MetricRegistryTest.METER2, meter));
    }

    @Test
    public void hasAMapOfRegisteredTimers() throws Exception {
        registry.register(MetricRegistryTest.TIMER2, timer);
        assertThat(registry.getTimers()).contains(entry(MetricRegistryTest.TIMER2, timer));
    }

    @Test
    public void hasASetOfRegisteredMetricNames() throws Exception {
        registry.register(MetricRegistryTest.GAUGE2, gauge);
        registry.register(MetricRegistryTest.COUNTER2, counter);
        registry.register(MetricRegistryTest.HISTOGRAM2, histogram);
        registry.register(MetricRegistryTest.METER2, meter);
        registry.register(MetricRegistryTest.TIMER2, timer);
        assertThat(registry.getNames()).containsOnly(MetricRegistryTest.GAUGE2, MetricRegistryTest.COUNTER2, MetricRegistryTest.HISTOGRAM2, MetricRegistryTest.METER2, MetricRegistryTest.TIMER2);
    }

    @Test
    public void registersMultipleMetrics() throws Exception {
        final MetricSet metrics = () -> {
            final Map<MetricName, Metric> metrics1 = new HashMap<>();
            metrics1.put(GAUGE2, gauge);
            metrics1.put(COUNTER2, counter);
            return metrics1;
        };
        registry.registerAll(metrics);
        assertThat(registry.getNames()).containsOnly(MetricRegistryTest.GAUGE2, MetricRegistryTest.COUNTER2);
    }

    @Test
    public void registersMultipleMetricsWithAPrefix() throws Exception {
        final MetricName myCounter = MetricName.build("my.counter");
        final MetricName myGauge = MetricName.build("my.gauge");
        final MetricSet metrics = () -> {
            final Map<MetricName, Metric> metrics1 = new HashMap<>();
            metrics1.put(GAUGE, gauge);
            metrics1.put(COUNTER, counter);
            return metrics1;
        };
        registry.register("my", metrics);
        assertThat(registry.getNames()).containsOnly(myGauge, myCounter);
    }

    @Test
    public void registersRecursiveMetricSets() throws Exception {
        final MetricSet inner = () -> {
            final Map<MetricName, Metric> metrics = new HashMap<>();
            metrics.put(GAUGE, gauge);
            return metrics;
        };
        final MetricSet outer = () -> {
            final Map<MetricName, Metric> metrics = new HashMap<>();
            metrics.put(MetricName.build("inner"), inner);
            metrics.put(COUNTER, counter);
            return metrics;
        };
        registry.register("my", outer);
        final MetricName myCounter = MetricName.build("my.counter");
        final MetricName myInnerGauge = MetricName.build("my.inner.gauge");
        assertThat(registry.getNames()).containsOnly(myInnerGauge, myCounter);
    }

    @Test
    public void registersMetricsFromAnotherRegistry() throws Exception {
        MetricRegistry other = new MetricRegistry();
        other.register(MetricRegistryTest.GAUGE, gauge);
        registry.register("nested", other);
        assertThat(registry.getNames()).containsOnly(MetricName.build("nested.gauge"));
    }

    @Test
    public void concatenatesStringsToFormADottedName() throws Exception {
        assertThat(MetricRegistry.name("one", "two", "three")).isEqualTo(MetricName.build("one.two.three"));
    }

    @Test
    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public void elidesNullValuesFromNamesWhenOnlyOneNullPassedIn() throws Exception {
        assertThat(MetricRegistry.name("one", ((String) (null)))).isEqualTo(MetricName.build("one"));
    }

    @Test
    public void elidesNullValuesFromNamesWhenManyNullsPassedIn() throws Exception {
        assertThat(MetricRegistry.name("one", null, null)).isEqualTo(MetricName.build("one"));
    }

    @Test
    public void elidesNullValuesFromNamesWhenNullAndNotNullPassedIn() throws Exception {
        assertThat(MetricRegistry.name("one", null, "three")).isEqualTo(MetricName.build("one.three"));
    }

    @Test
    public void elidesEmptyStringsFromNames() throws Exception {
        assertThat(MetricRegistry.name("one", "", "three")).isEqualTo(MetricName.build("one.three"));
    }

    @Test
    public void concatenatesClassNamesWithStringsToFormADottedName() throws Exception {
        assertThat(MetricRegistry.name(MetricRegistryTest.class, "one", "two")).isEqualTo(MetricName.build("io.dropwizard.metrics.MetricRegistryTest.one.two"));
    }

    @Test
    public void concatenatesClassesWithoutCanonicalNamesWithStrings() throws Exception {
        final Gauge<String> g = () -> null;
        assertThat(MetricRegistry.name(g.getClass(), "one", "two")).isEqualTo(MetricName.build(((g.getClass().getName()) + ".one.two")));
    }

    @Test
    public void removesMetricsMatchingAFilter() throws Exception {
        final MetricName timer1 = MetricName.build("timer-1");
        final MetricName timer2 = MetricName.build("timer-2");
        final MetricName histogram1 = MetricName.build("histogram-1");
        registry.timer(timer1);
        registry.timer(timer2);
        registry.histogram(histogram1);
        assertThat(registry.getNames()).contains(timer1, timer2, histogram1);
        registry.removeMatching(( name, metric) -> name.getKey().endsWith("1"));
        assertThat(registry.getNames()).doesNotContain(timer1, histogram1);
        assertThat(registry.getNames()).contains(timer2);
        Mockito.verify(listener).onTimerRemoved(timer1);
        Mockito.verify(listener).onHistogramRemoved(histogram1);
    }
}

