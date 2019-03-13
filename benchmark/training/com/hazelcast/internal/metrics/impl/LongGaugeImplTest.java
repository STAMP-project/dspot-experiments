/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.metrics.impl;


import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class LongGaugeImplTest {
    private MetricsRegistryImpl metricsRegistry;

    class SomeObject {
        @Probe
        long longField = 10;

        @Probe
        double doubleField = 10.8;
    }

    @Test
    public void getName() {
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        String actual = gauge.getName();
        Assert.assertEquals("foo", actual);
    }

    // ============ getLong ===========================
    @Test
    public void whenNoProbeSet() {
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        long actual = gauge.read();
        Assert.assertEquals(0, actual);
    }

    @Test
    public void whenDoubleProbe() {
        metricsRegistry.register(this, "foo", ProbeLevel.MANDATORY, new com.hazelcast.internal.metrics.DoubleProbeFunction<LongGaugeImplTest>() {
            @Override
            public double get(LongGaugeImplTest source) throws Exception {
                return 10;
            }
        });
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        long actual = gauge.read();
        Assert.assertEquals(10, actual);
    }

    @Test
    public void whenLongProbe() {
        metricsRegistry.register(this, "foo", ProbeLevel.MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object o) throws Exception {
                return 10;
            }
        });
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        Assert.assertEquals(10, gauge.read());
    }

    @Test
    public void whenProbeThrowsException() {
        metricsRegistry.register(this, "foo", ProbeLevel.MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object o) {
                throw new RuntimeException();
            }
        });
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        long actual = gauge.read();
        Assert.assertEquals(0, actual);
    }

    @Test
    public void whenLongProbeField() {
        LongGaugeImplTest.SomeObject someObject = new LongGaugeImplTest.SomeObject();
        metricsRegistry.scanAndRegister(someObject, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.longField");
        Assert.assertEquals(10, gauge.read());
    }

    @Test
    public void whenDoubleProbeField() {
        LongGaugeImplTest.SomeObject someObject = new LongGaugeImplTest.SomeObject();
        metricsRegistry.scanAndRegister(someObject, "foo");
        LongGauge gauge = metricsRegistry.newLongGauge("foo.doubleField");
        Assert.assertEquals(Math.round(someObject.doubleField), gauge.read());
    }

    @Test
    public void whenReregister() {
        metricsRegistry.register(this, "foo", ProbeLevel.MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object o) throws Exception {
                return 10;
            }
        });
        LongGauge gauge = metricsRegistry.newLongGauge("foo");
        gauge.read();
        metricsRegistry.register(this, "foo", ProbeLevel.MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object o) throws Exception {
                return 11;
            }
        });
        Assert.assertEquals(11, gauge.read());
    }
}

