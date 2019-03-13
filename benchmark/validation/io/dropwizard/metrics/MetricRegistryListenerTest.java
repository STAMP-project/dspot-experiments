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


public class MetricRegistryListenerTest {
    private static final MetricName BLAH = MetricName.build("blah");

    private final Gauge gauge = Mockito.mock(Gauge.class);

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    private final MetricRegistryListener listener = new MetricRegistryListener.Base() {};

    @Test
    public void noOpsOnGaugeAdded() throws Exception {
        listener.onGaugeAdded(MetricRegistryListenerTest.BLAH, gauge);
        Mockito.verifyZeroInteractions(gauge);
    }

    @Test
    public void noOpsOnCounterAdded() throws Exception {
        listener.onCounterAdded(MetricRegistryListenerTest.BLAH, counter);
        Mockito.verifyZeroInteractions(counter);
    }

    @Test
    public void noOpsOnHistogramAdded() throws Exception {
        listener.onHistogramAdded(MetricRegistryListenerTest.BLAH, histogram);
        Mockito.verifyZeroInteractions(histogram);
    }

    @Test
    public void noOpsOnMeterAdded() throws Exception {
        listener.onMeterAdded(MetricRegistryListenerTest.BLAH, meter);
        Mockito.verifyZeroInteractions(meter);
    }

    @Test
    public void noOpsOnTimerAdded() throws Exception {
        listener.onTimerAdded(MetricRegistryListenerTest.BLAH, timer);
        Mockito.verifyZeroInteractions(timer);
    }

    @Test
    public void doesNotExplodeWhenMetricsAreRemoved() throws Exception {
        listener.onGaugeRemoved(MetricRegistryListenerTest.BLAH);
        listener.onCounterRemoved(MetricRegistryListenerTest.BLAH);
        listener.onHistogramRemoved(MetricRegistryListenerTest.BLAH);
        listener.onMeterRemoved(MetricRegistryListenerTest.BLAH);
        listener.onTimerRemoved(MetricRegistryListenerTest.BLAH);
    }
}

