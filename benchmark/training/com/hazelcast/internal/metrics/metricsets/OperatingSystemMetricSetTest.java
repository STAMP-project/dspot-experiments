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


import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperatingSystemMetricSetTest extends HazelcastTestSupport {
    private MetricsRegistryImpl metricsRegistry;

    @Test
    public void utilityConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(OperatingSystemMetricSet.class);
    }

    @Test
    public void testGenericSensors() {
        assertContainsSensor("os.systemLoadAverage");
    }

    @Test
    public void testComSunManagementUnixOperatingSystemMXBean() {
        assumeOperatingSystemMXBeanType("com.sun.management.UnixOperatingSystemMXBean");
        assertContainsSensor("os.maxFileDescriptorCount");
        assertContainsSensor("os.openFileDescriptorCount");
    }

    @Test
    public void testComSunManagementOperatingSystemMXBean() {
        assumeOperatingSystemMXBeanType("com.sun.management.OperatingSystemMXBean");
        assertContainsSensor("os.committedVirtualMemorySize");
        assertContainsSensor("os.freePhysicalMemorySize");
        assertContainsSensor("os.freeSwapSpaceSize");
        assertContainsSensor("os.processCpuTime");
        assertContainsSensor("os.totalPhysicalMemorySize");
        assertContainsSensor("os.totalSwapSpaceSize");
        HazelcastTestSupport.assumeThatNoJDK6();
        // only available in JDK 7+
        assertContainsSensor("os.processCpuLoad");
        assertContainsSensor("os.systemCpuLoad");
    }

    @Test
    public void registerMethod_whenDouble() {
        OperatingSystemMetricSetTest.FakeOperatingSystemBean fakeOperatingSystemBean = new OperatingSystemMetricSetTest.FakeOperatingSystemBean();
        OperatingSystemMetricSet.registerMethod(metricsRegistry, fakeOperatingSystemBean, "doubleMethod", "doubleMethod");
        DoubleGauge gauge = metricsRegistry.newDoubleGauge("doubleMethod");
        Assert.assertEquals(fakeOperatingSystemBean.doubleMethod(), gauge.read(), 0.1);
    }

    @Test
    public void registerMethod_whenLong() {
        metricsRegistry = new MetricsRegistryImpl(Logger.getLogger(MetricsRegistryImpl.class), ProbeLevel.INFO);
        OperatingSystemMetricSetTest.FakeOperatingSystemBean fakeOperatingSystemBean = new OperatingSystemMetricSetTest.FakeOperatingSystemBean();
        OperatingSystemMetricSet.registerMethod(metricsRegistry, fakeOperatingSystemBean, "longMethod", "longMethod");
        LongGauge gauge = metricsRegistry.newLongGauge("longMethod");
        Assert.assertEquals(fakeOperatingSystemBean.longMethod(), gauge.read());
    }

    @Test
    public void registerMethod_whenNotExist() {
        metricsRegistry = new MetricsRegistryImpl(Logger.getLogger(MetricsRegistryImpl.class), ProbeLevel.INFO);
        OperatingSystemMetricSetTest.FakeOperatingSystemBean fakeOperatingSystemBean = new OperatingSystemMetricSetTest.FakeOperatingSystemBean();
        OperatingSystemMetricSet.registerMethod(metricsRegistry, fakeOperatingSystemBean, "notExist", "notExist");
        boolean parameterExist = metricsRegistry.getNames().contains("notExist");
        Assert.assertFalse(parameterExist);
    }

    public static class FakeOperatingSystemBean {
        public double doubleMethod() {
            return 10;
        }

        public long longMethod() {
            return 10;
        }
    }
}

