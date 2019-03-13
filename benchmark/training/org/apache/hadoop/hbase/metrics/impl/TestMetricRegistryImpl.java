/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.metrics.impl;


import java.util.Map;
import java.util.Optional;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.metrics.Counter;
import org.apache.hadoop.hbase.metrics.Meter;
import org.apache.hadoop.hbase.metrics.Metric;
import org.apache.hadoop.hbase.metrics.MetricRegistryInfo;
import org.apache.hadoop.hbase.metrics.Timer;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.hadoop.hbase.metrics.Gauge.getValue;


@Category(SmallTests.class)
public class TestMetricRegistryImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetricRegistryImpl.class);

    private MetricRegistryInfo info;

    private MetricRegistryImpl registry;

    @Test
    public void testCounter() {
        Counter counter = registry.counter("mycounter");
        Assert.assertNotNull(counter);
        counter.increment(42L);
        Optional<Metric> metric = registry.get("mycounter");
        Assert.assertTrue(metric.isPresent());
        Assert.assertEquals(42L, ((long) (getCount())));
    }

    @Test
    public void testRegisterGauge() {
        registry.register("mygauge", new org.apache.hadoop.hbase.metrics.Gauge<Long>() {
            @Override
            public Long getValue() {
                return 42L;
            }
        });
        Optional<Metric> metric = registry.get("mygauge");
        Assert.assertTrue(metric.isPresent());
        Assert.assertEquals(42L, ((long) (getValue())));
    }

    @Test
    public void testRegisterGaugeLambda() {
        // register a Gauge using lambda expression
        registry.register("gaugeLambda", () -> 42L);
        Optional<Metric> metric = registry.get("gaugeLambda");
        Assert.assertTrue(metric.isPresent());
        Assert.assertEquals(42L, ((long) (getValue())));
    }

    @Test
    public void testTimer() {
        Timer timer = registry.timer("mytimer");
        Assert.assertNotNull(timer);
        timer.updateNanos(100);
    }

    @Test
    public void testMeter() {
        Meter meter = registry.meter("mymeter");
        Assert.assertNotNull(meter);
        meter.mark();
    }

    @Test
    public void testRegister() {
        CounterImpl counter = new CounterImpl();
        registry.register("mycounter", counter);
        counter.increment(42L);
        Optional<Metric> metric = registry.get("mycounter");
        Assert.assertTrue(metric.isPresent());
        Assert.assertEquals(42L, ((long) (getCount())));
    }

    @Test
    public void testDoubleRegister() {
        org.apache.hadoop.hbase.metrics.Gauge g1 = registry.register("mygauge", () -> 42L);
        org.apache.hadoop.hbase.metrics.Gauge g2 = registry.register("mygauge", () -> 52L);
        // second gauge is ignored if it exists
        Assert.assertEquals(g1, g2);
        Optional<Metric> metric = registry.get("mygauge");
        Assert.assertTrue(metric.isPresent());
        Assert.assertEquals(42L, ((long) (getValue())));
        Counter c1 = registry.counter("mycounter");
        Counter c2 = registry.counter("mycounter");
        Assert.assertEquals(c1, c2);
    }

    @Test
    public void testGetMetrics() {
        CounterImpl counter = new CounterImpl();
        registry.register("mycounter", counter);
        org.apache.hadoop.hbase.metrics.Gauge gauge = registry.register("mygauge", () -> 42L);
        Timer timer = registry.timer("mytimer");
        Map<String, Metric> metrics = registry.getMetrics();
        Assert.assertEquals(3, metrics.size());
        Assert.assertEquals(counter, metrics.get("mycounter"));
        Assert.assertEquals(gauge, metrics.get("mygauge"));
        Assert.assertEquals(timer, metrics.get("mytimer"));
    }
}

