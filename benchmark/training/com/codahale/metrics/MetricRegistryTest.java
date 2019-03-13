package com.codahale.metrics;


import org.junit.Test;
import org.mockito.Mockito;


public class MetricRegistryTest {
    private final MetricRegistryListener listener = Mockito.mock(MetricRegistryListener.class);

    private final MetricRegistry registry = new MetricRegistry();

    private final Gauge<String> gauge = () -> "";

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    @Test
    public void registeringAGaugeTriggersANotification() {
        assertThat(registry.register("thing", gauge)).isEqualTo(gauge);
        Mockito.verify(listener).onGaugeAdded("thing", gauge);
    }

    @Test
    public void removingAGaugeTriggersANotification() {
        registry.register("thing", gauge);
        assertThat(registry.remove("thing")).isTrue();
        Mockito.verify(listener).onGaugeRemoved("thing");
    }

    @Test
    public void registeringACounterTriggersANotification() {
        assertThat(registry.register("thing", counter)).isEqualTo(counter);
        Mockito.verify(listener).onCounterAdded("thing", counter);
    }

    @Test
    public void accessingACounterRegistersAndReusesTheCounter() {
        final Counter counter1 = registry.counter("thing");
        final Counter counter2 = registry.counter("thing");
        assertThat(counter1).isSameAs(counter2);
        Mockito.verify(listener).onCounterAdded("thing", counter1);
    }

    @Test
    public void accessingACustomCounterRegistersAndReusesTheCounter() {
        final MetricRegistry.MetricSupplier<Counter> supplier = () -> counter;
        final Counter counter1 = registry.counter("thing", supplier);
        final Counter counter2 = registry.counter("thing", supplier);
        assertThat(counter1).isSameAs(counter2);
        Mockito.verify(listener).onCounterAdded("thing", counter1);
    }

    @Test
    public void removingACounterTriggersANotification() {
        registry.register("thing", counter);
        assertThat(registry.remove("thing")).isTrue();
        Mockito.verify(listener).onCounterRemoved("thing");
    }

    @Test
    public void registeringAHistogramTriggersANotification() {
        assertThat(registry.register("thing", histogram)).isEqualTo(histogram);
        Mockito.verify(listener).onHistogramAdded("thing", histogram);
    }

    @Test
    public void accessingAHistogramRegistersAndReusesIt() {
        final Histogram histogram1 = registry.histogram("thing");
        final Histogram histogram2 = registry.histogram("thing");
        assertThat(histogram1).isSameAs(histogram2);
        Mockito.verify(listener).onHistogramAdded("thing", histogram1);
    }

    @Test
    public void accessingACustomHistogramRegistersAndReusesIt() {
        final MetricRegistry.MetricSupplier<Histogram> supplier = () -> histogram;
        final Histogram histogram1 = registry.histogram("thing", supplier);
        final Histogram histogram2 = registry.histogram("thing", supplier);
        assertThat(histogram1).isSameAs(histogram2);
        Mockito.verify(listener).onHistogramAdded("thing", histogram1);
    }

    @Test
    public void removingAHistogramTriggersANotification() {
        registry.register("thing", histogram);
        assertThat(registry.remove("thing")).isTrue();
        Mockito.verify(listener).onHistogramRemoved("thing");
    }

    @Test
    public void registeringAMeterTriggersANotification() {
        assertThat(registry.register("thing", meter)).isEqualTo(meter);
        Mockito.verify(listener).onMeterAdded("thing", meter);
    }

    @Test
    public void accessingAMeterRegistersAndReusesIt() {
        final Meter meter1 = registry.meter("thing");
        final Meter meter2 = registry.meter("thing");
        assertThat(meter1).isSameAs(meter2);
        Mockito.verify(listener).onMeterAdded("thing", meter1);
    }

    @Test
    public void accessingACustomMeterRegistersAndReusesIt() {
        final MetricRegistry.MetricSupplier<Meter> supplier = () -> meter;
        final Meter meter1 = registry.meter("thing", supplier);
        final Meter meter2 = registry.meter("thing", supplier);
        assertThat(meter1).isSameAs(meter2);
        Mockito.verify(listener).onMeterAdded("thing", meter1);
    }

    @Test
    public void removingAMeterTriggersANotification() {
        registry.register("thing", meter);
        assertThat(registry.remove("thing")).isTrue();
        Mockito.verify(listener).onMeterRemoved("thing");
    }

    @Test
    public void registeringATimerTriggersANotification() {
        assertThat(registry.register("thing", timer)).isEqualTo(timer);
        Mockito.verify(listener).onTimerAdded("thing", timer);
    }

    @Test
    public void accessingATimerRegistersAndReusesIt() {
        final Timer timer1 = registry.timer("thing");
        final Timer timer2 = registry.timer("thing");
        assertThat(timer1).isSameAs(timer2);
        Mockito.verify(listener).onTimerAdded("thing", timer1);
    }

    @Test
    public void accessingACustomTimerRegistersAndReusesIt() {
        final MetricRegistry.MetricSupplier<Timer> supplier = () -> timer;
        final Timer timer1 = registry.timer("thing", supplier);
        final Timer timer2 = registry.timer("thing", supplier);
        assertThat(timer1).isSameAs(timer2);
        Mockito.verify(listener).onTimerAdded("thing", timer1);
    }

    @Test
    public void removingATimerTriggersANotification() {
        registry.register("thing", timer);
        assertThat(registry.remove("thing")).isTrue();
        Mockito.verify(listener).onTimerRemoved("thing");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void accessingACustomGaugeRegistersAndReusesIt() {
        final MetricRegistry.MetricSupplier<Gauge> supplier = () -> gauge;
        final Gauge gauge1 = registry.gauge("thing", supplier);
        final Gauge gauge2 = registry.gauge("thing", supplier);
        assertThat(gauge1).isSameAs(gauge2);
        Mockito.verify(listener).onGaugeAdded("thing", gauge1);
    }

    @Test
    public void addingAListenerWithExistingMetricsCatchesItUp() {
        registry.register("gauge", gauge);
        registry.register("counter", counter);
        registry.register("histogram", histogram);
        registry.register("meter", meter);
        registry.register("timer", timer);
        final MetricRegistryListener other = Mockito.mock(MetricRegistryListener.class);
        registry.addListener(other);
        Mockito.verify(other).onGaugeAdded("gauge", gauge);
        Mockito.verify(other).onCounterAdded("counter", counter);
        Mockito.verify(other).onHistogramAdded("histogram", histogram);
        Mockito.verify(other).onMeterAdded("meter", meter);
        Mockito.verify(other).onTimerAdded("timer", timer);
    }

    @Test
    public void aRemovedListenerDoesNotReceiveUpdates() {
        registry.register("gauge", gauge);
        registry.removeListener(listener);
        registry.register("gauge2", gauge);
        Mockito.verify(listener, Mockito.never()).onGaugeAdded("gauge2", gauge);
    }

    @Test
    public void hasAMapOfRegisteredGauges() {
        registry.register("gauge", gauge);
        assertThat(registry.getGauges()).contains(entry("gauge", gauge));
    }

    @Test
    public void hasAMapOfRegisteredCounters() {
        registry.register("counter", counter);
        assertThat(registry.getCounters()).contains(entry("counter", counter));
    }

    @Test
    public void hasAMapOfRegisteredHistograms() {
        registry.register("histogram", histogram);
        assertThat(registry.getHistograms()).contains(entry("histogram", histogram));
    }

    @Test
    public void hasAMapOfRegisteredMeters() {
        registry.register("meter", meter);
        assertThat(registry.getMeters()).contains(entry("meter", meter));
    }

    @Test
    public void hasAMapOfRegisteredTimers() {
        registry.register("timer", timer);
        assertThat(registry.getTimers()).contains(entry("timer", timer));
    }

    @Test
    public void hasASetOfRegisteredMetricNames() {
        registry.register("gauge", gauge);
        registry.register("counter", counter);
        registry.register("histogram", histogram);
        registry.register("meter", meter);
        registry.register("timer", timer);
        assertThat(registry.getNames()).containsOnly("gauge", "counter", "histogram", "meter", "timer");
    }

    @Test
    public void registersMultipleMetrics() {
        final MetricSet metrics = () -> {
            final Map<String, Metric> m = new HashMap<>();
            m.put("gauge", gauge);
            m.put("counter", counter);
            return m;
        };
        registry.registerAll(metrics);
        assertThat(registry.getNames()).containsOnly("gauge", "counter");
    }

    @Test
    public void registersMultipleMetricsWithAPrefix() {
        final MetricSet metrics = () -> {
            final Map<String, Metric> m = new HashMap<>();
            m.put("gauge", gauge);
            m.put("counter", counter);
            return m;
        };
        registry.register("my", metrics);
        assertThat(registry.getNames()).containsOnly("my.gauge", "my.counter");
    }

    @Test
    public void registersRecursiveMetricSets() {
        final MetricSet inner = () -> {
            final Map<String, Metric> m = new HashMap<>();
            m.put("gauge", gauge);
            return m;
        };
        final MetricSet outer = () -> {
            final Map<String, Metric> m = new HashMap<>();
            m.put("inner", inner);
            m.put("counter", counter);
            return m;
        };
        registry.register("my", outer);
        assertThat(registry.getNames()).containsOnly("my.inner.gauge", "my.counter");
    }

    @Test
    public void registersMetricsFromAnotherRegistry() {
        MetricRegistry other = new MetricRegistry();
        other.register("gauge", gauge);
        registry.register("nested", other);
        assertThat(registry.getNames()).containsOnly("nested.gauge");
    }

    @Test
    public void concatenatesStringsToFormADottedName() {
        assertThat(MetricRegistry.name("one", "two", "three")).isEqualTo("one.two.three");
    }

    @Test
    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public void elidesNullValuesFromNamesWhenOnlyOneNullPassedIn() {
        assertThat(MetricRegistry.name("one", ((String) (null)))).isEqualTo("one");
    }

    @Test
    public void elidesNullValuesFromNamesWhenManyNullsPassedIn() {
        assertThat(MetricRegistry.name("one", null, null)).isEqualTo("one");
    }

    @Test
    public void elidesNullValuesFromNamesWhenNullAndNotNullPassedIn() {
        assertThat(MetricRegistry.name("one", null, "three")).isEqualTo("one.three");
    }

    @Test
    public void elidesEmptyStringsFromNames() {
        assertThat(MetricRegistry.name("one", "", "three")).isEqualTo("one.three");
    }

    @Test
    public void concatenatesClassNamesWithStringsToFormADottedName() {
        assertThat(MetricRegistry.name(MetricRegistryTest.class, "one", "two")).isEqualTo("com.codahale.metrics.MetricRegistryTest.one.two");
    }

    @Test
    public void concatenatesClassesWithoutCanonicalNamesWithStrings() {
        final Gauge<String> g = () -> null;
        assertThat(MetricRegistry.name(g.getClass(), "one", "two")).matches("com\\.codahale\\.metrics\\.MetricRegistryTest.+?\\.one\\.two");
    }

    @Test
    public void removesMetricsMatchingAFilter() {
        registry.timer("timer-1");
        registry.timer("timer-2");
        registry.histogram("histogram-1");
        assertThat(registry.getNames()).contains("timer-1", "timer-2", "histogram-1");
        registry.removeMatching(( name, metric) -> name.endsWith("1"));
        assertThat(registry.getNames()).doesNotContain("timer-1", "histogram-1");
        assertThat(registry.getNames()).contains("timer-2");
        Mockito.verify(listener).onTimerRemoved("timer-1");
        Mockito.verify(listener).onHistogramRemoved("histogram-1");
    }
}

