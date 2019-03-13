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
package com.hazelcast.internal.diagnostics;


import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class MetricsPluginTest extends AbstractDiagnosticsPluginTest {
    private MetricsPlugin plugin;

    private MetricsRegistry metricsRegistry;

    @Test
    public void testGetPeriodMillis() {
        long periodMillis = plugin.getPeriodMillis();
        Assert.assertEquals(TimeUnit.SECONDS.toMillis(1), periodMillis);
    }

    @Test
    public void testRunWithProblematicProbe() {
        metricsRegistry.register(this, "broken", ProbeLevel.MANDATORY, new LongProbeFunction() {
            @Override
            public long get(Object source) {
                throw new RuntimeException("error");
            }
        });
        plugin.run(logWriter);
        assertContains("broken=java.lang.RuntimeException:error");
    }

    @Test
    public void testRun() {
        plugin.run(logWriter);
        // we just test a few to make sure the metrics are written
        assertContains("client.endpoint.count=0");
        assertContains("operation.responseQueueSize=0");
    }
}

