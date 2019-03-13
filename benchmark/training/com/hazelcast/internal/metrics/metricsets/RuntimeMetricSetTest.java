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
package com.hazelcast.internal.metrics.metricsets;


import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RuntimeMetricSetTest extends HazelcastTestSupport {
    private static final int TEN_MB = (10 * 1024) * 1024;

    private MetricsRegistryImpl metricsRegistry;

    private Runtime runtime;

    @Test
    public void utilityConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(RuntimeMetricSet.class);
    }

    @Test
    public void freeMemory() {
        final LongGauge gauge = metricsRegistry.newLongGauge("runtime.freeMemory");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(runtime.freeMemory(), gauge.read(), RuntimeMetricSetTest.TEN_MB);
            }
        });
    }

    @Test
    public void totalMemory() {
        final LongGauge gauge = metricsRegistry.newLongGauge("runtime.totalMemory");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(runtime.totalMemory(), gauge.read(), RuntimeMetricSetTest.TEN_MB);
            }
        });
    }

    @Test
    public void maxMemory() {
        final LongGauge gauge = metricsRegistry.newLongGauge("runtime.maxMemory");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(runtime.maxMemory(), gauge.read(), RuntimeMetricSetTest.TEN_MB);
            }
        });
    }

    @Test
    public void usedMemory() {
        final LongGauge gauge = metricsRegistry.newLongGauge("runtime.usedMemory");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                double expected = (runtime.totalMemory()) - (runtime.freeMemory());
                Assert.assertEquals(expected, gauge.read(), RuntimeMetricSetTest.TEN_MB);
            }
        });
    }

    @Test
    public void availableProcessors() {
        LongGauge gauge = metricsRegistry.newLongGauge("runtime.availableProcessors");
        Assert.assertEquals(runtime.availableProcessors(), gauge.read());
    }

    @Test
    public void uptime() {
        final LongGauge gauge = metricsRegistry.newLongGauge("runtime.uptime");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                double expected = ManagementFactory.getRuntimeMXBean().getUptime();
                Assert.assertEquals(expected, gauge.read(), TimeUnit.MINUTES.toMillis(1));
            }
        });
    }
}

