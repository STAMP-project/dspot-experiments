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


import ProbeLevel.MANDATORY;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MetricsRegistryImplTest extends HazelcastTestSupport {
    private MetricsRegistryImpl metricsRegistry;

    @Test
    public void modCount() {
        long modCount = metricsRegistry.modCount();
        metricsRegistry.register(this, "foo", MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object obj) throws Exception {
                return 1;
            }
        });
        Assert.assertEquals((modCount + 1), metricsRegistry.modCount());
        metricsRegistry.deregister(this);
        Assert.assertEquals((modCount + 2), metricsRegistry.modCount());
    }

    // ================ newLongGauge ======================
    @Test(expected = NullPointerException.class)
    public void newGauge_whenNullName() {
        metricsRegistry.newLongGauge(null);
    }

    @Test
    public void newGauge_whenNotExistingMetric() {
        LongGaugeImpl gauge = metricsRegistry.newLongGauge("foo");
        Assert.assertNotNull(gauge);
        Assert.assertEquals("foo", gauge.getName());
        Assert.assertEquals(0, gauge.read());
    }

    @Test
    public void newGauge_whenExistingMetric() {
        LongGaugeImpl first = metricsRegistry.newLongGauge("foo");
        LongGaugeImpl second = metricsRegistry.newLongGauge("foo");
        Assert.assertNotSame(first, second);
    }

    // ================ getNames ======================
    @Test
    public void getNames() {
        Set<String> expected = new HashSet<String>();
        expected.add("first");
        expected.add("second");
        expected.add("third");
        for (String name : expected) {
            metricsRegistry.register(this, name, MANDATORY, new LongProbeFunction() {
                @Override
                public long get(Object obj) throws Exception {
                    return 0;
                }
            });
        }
        Set<String> names = metricsRegistry.getNames();
        for (String name : expected) {
            HazelcastTestSupport.assertContains(names, name);
        }
    }

    @Test
    public void shutdown() {
        metricsRegistry.shutdown();
    }
}

