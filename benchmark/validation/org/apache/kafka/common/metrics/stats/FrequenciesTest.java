/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.metrics.stats;


import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.CompoundStat.NamedMeasurable;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class FrequenciesTest {
    private static final double DELTA = 1.0E-4;

    private MetricConfig config;

    private Time time;

    private Metrics metrics;

    @Test(expected = IllegalArgumentException.class)
    public void testFrequencyCenterValueAboveMax() {
        new Frequencies(4, 1.0, 4.0, freq("1", 1.0), freq("2", 20.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFrequencyCenterValueBelowMin() {
        new Frequencies(4, 1.0, 4.0, freq("1", 1.0), freq("2", (-20.0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoreFrequencyParametersThanBuckets() {
        new Frequencies(1, 1.0, 4.0, freq("1", 1.0), freq("2", (-20.0)));
    }

    @Test
    public void testBooleanFrequencies() {
        MetricName metricTrue = name("true");
        MetricName metricFalse = name("false");
        Frequencies frequencies = Frequencies.forBooleanValues(metricFalse, metricTrue);
        final NamedMeasurable falseMetric = frequencies.stats().get(0);
        final NamedMeasurable trueMetric = frequencies.stats().get(1);
        // Record 2 windows worth of values
        for (int i = 0; i != 25; ++i) {
            frequencies.record(config, 0.0, time.milliseconds());
        }
        for (int i = 0; i != 75; ++i) {
            frequencies.record(config, 1.0, time.milliseconds());
        }
        Assert.assertEquals(0.25, falseMetric.stat().measure(config, time.milliseconds()), FrequenciesTest.DELTA);
        Assert.assertEquals(0.75, trueMetric.stat().measure(config, time.milliseconds()), FrequenciesTest.DELTA);
        // Record 2 more windows worth of values
        for (int i = 0; i != 40; ++i) {
            frequencies.record(config, 0.0, time.milliseconds());
        }
        for (int i = 0; i != 60; ++i) {
            frequencies.record(config, 1.0, time.milliseconds());
        }
        Assert.assertEquals(0.4, falseMetric.stat().measure(config, time.milliseconds()), FrequenciesTest.DELTA);
        Assert.assertEquals(0.6, trueMetric.stat().measure(config, time.milliseconds()), FrequenciesTest.DELTA);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testUseWithMetrics() {
        MetricName name1 = name("1");
        MetricName name2 = name("2");
        MetricName name3 = name("3");
        MetricName name4 = name("4");
        Frequencies frequencies = new Frequencies(4, 1.0, 4.0, new Frequency(name1, 1.0), new Frequency(name2, 2.0), new Frequency(name3, 3.0), new Frequency(name4, 4.0));
        Sensor sensor = metrics.sensor("test", config);
        sensor.add(frequencies);
        Metric metric1 = this.metrics.metrics().get(name1);
        Metric metric2 = this.metrics.metrics().get(name2);
        Metric metric3 = this.metrics.metrics().get(name3);
        Metric metric4 = this.metrics.metrics().get(name4);
        // Record 2 windows worth of values
        for (int i = 0; i != 100; ++i) {
            frequencies.record(config, ((i % 4) + 1), time.milliseconds());
        }
        Assert.assertEquals(0.25, ((Double) (metric1.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.25, ((Double) (metric2.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.25, ((Double) (metric3.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.25, ((Double) (metric4.metricValue())), FrequenciesTest.DELTA);
        // Record 2 windows worth of values
        for (int i = 0; i != 100; ++i) {
            frequencies.record(config, ((i % 2) + 1), time.milliseconds());
        }
        Assert.assertEquals(0.5, ((Double) (metric1.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.5, ((Double) (metric2.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.0, ((Double) (metric3.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.0, ((Double) (metric4.metricValue())), FrequenciesTest.DELTA);
        // Record 1 window worth of values to overlap with the last window
        // that is half 1.0 and half 2.0
        for (int i = 0; i != 50; ++i) {
            frequencies.record(config, 4.0, time.milliseconds());
        }
        Assert.assertEquals(0.25, ((Double) (metric1.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.25, ((Double) (metric2.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.0, ((Double) (metric3.metricValue())), FrequenciesTest.DELTA);
        Assert.assertEquals(0.5, ((Double) (metric4.metricValue())), FrequenciesTest.DELTA);
    }
}

