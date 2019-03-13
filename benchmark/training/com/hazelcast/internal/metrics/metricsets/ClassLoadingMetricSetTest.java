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
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClassLoadingMetricSetTest extends HazelcastTestSupport {
    private static final ClassLoadingMXBean BEAN = ManagementFactory.getClassLoadingMXBean();

    private MetricsRegistryImpl metricsRegistry;

    @Test
    public void utilityConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ClassLoadingMetricSet.class);
    }

    @Test
    public void loadedClassesCount() {
        final LongGauge gauge = metricsRegistry.newLongGauge("classloading.loadedClassesCount");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(ClassLoadingMetricSetTest.BEAN.getLoadedClassCount(), gauge.read(), 100);
            }
        });
    }

    @Test
    public void totalLoadedClassesCount() {
        final LongGauge gauge = metricsRegistry.newLongGauge("classloading.totalLoadedClassesCount");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(ClassLoadingMetricSetTest.BEAN.getTotalLoadedClassCount(), gauge.read(), 100);
            }
        });
    }

    @Test
    public void unloadedClassCount() {
        final LongGauge gauge = metricsRegistry.newLongGauge("classloading.unloadedClassCount");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(ClassLoadingMetricSetTest.BEAN.getUnloadedClassCount(), gauge.read(), 100);
            }
        });
    }
}

