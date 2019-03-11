package com.codahale.metrics;


import org.junit.Test;
import org.mockito.Mockito;


public class MetricRegistryListenerTest {
    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    private final MetricRegistryListener listener = new MetricRegistryListener.Base() {};

    @Test
    public void noOpsOnGaugeAdded() {
        listener.onGaugeAdded("blah", () -> {
            throw new RuntimeException("Should not be called");
        });
    }

    @Test
    public void noOpsOnCounterAdded() {
        listener.onCounterAdded("blah", counter);
        Mockito.verifyZeroInteractions(counter);
    }

    @Test
    public void noOpsOnHistogramAdded() {
        listener.onHistogramAdded("blah", histogram);
        Mockito.verifyZeroInteractions(histogram);
    }

    @Test
    public void noOpsOnMeterAdded() {
        listener.onMeterAdded("blah", meter);
        Mockito.verifyZeroInteractions(meter);
    }

    @Test
    public void noOpsOnTimerAdded() {
        listener.onTimerAdded("blah", timer);
        Mockito.verifyZeroInteractions(timer);
    }

    @Test
    public void doesNotExplodeWhenMetricsAreRemoved() {
        listener.onGaugeRemoved("blah");
        listener.onCounterRemoved("blah");
        listener.onHistogramRemoved("blah");
        listener.onMeterRemoved("blah");
        listener.onTimerRemoved("blah");
    }
}

