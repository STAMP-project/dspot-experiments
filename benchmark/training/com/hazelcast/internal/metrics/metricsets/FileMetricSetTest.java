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
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class FileMetricSetTest extends HazelcastTestSupport {
    private MetricsRegistryImpl metricsRegistry;

    @Test
    public void utilityConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(FileMetricSet.class);
    }

    @Test
    public void freeSpace() {
        File file = new File(System.getProperty("user.home"));
        LongGauge freeSpaceGauge = metricsRegistry.newLongGauge("file.partition[user.home].freeSpace");
        FileMetricSetTest.assertAlmostEquals(file.getFreeSpace(), freeSpaceGauge.read());
    }

    @Test
    public void totalSpace() {
        File file = new File(System.getProperty("user.home"));
        LongGauge totalSpaceGauge = metricsRegistry.newLongGauge("file.partition[user.home].totalSpace");
        FileMetricSetTest.assertAlmostEquals(file.getTotalSpace(), totalSpaceGauge.read());
    }

    @Test
    public void usableSpace() {
        File file = new File(System.getProperty("user.home"));
        LongGauge usableSpaceGauge = metricsRegistry.newLongGauge("file.partition[user.home].usableSpace");
        FileMetricSetTest.assertAlmostEquals(file.getUsableSpace(), usableSpaceGauge.read());
    }
}

