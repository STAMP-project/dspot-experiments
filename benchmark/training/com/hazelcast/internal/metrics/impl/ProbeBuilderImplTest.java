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


import ProbeLevel.INFO;
import ProbeUnit.BYTES;
import ProbeUnit.COUNT;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeBuilder;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.ProbeUnit;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ProbeBuilderImplTest {
    @Probe
    private int probe1 = 1;

    @Probe(name = "secondProbe", level = ProbeLevel.MANDATORY, unit = ProbeUnit.BYTES)
    private int probe2 = 2;

    @Probe(level = ProbeLevel.DEBUG)
    private int probe3 = 3;

    @Test
    public void test_scanAndRegister() {
        MetricsRegistryImpl registry = new MetricsRegistryImpl(Logger.getLogger(MetricsRegistryImpl.class), ProbeLevel.INFO);
        registry.newProbeBuilder().withTag("tag1", "value1").scanAndRegister(this);
        assertProbes(registry);
    }

    @Test
    public void test_register() {
        MetricsRegistryImpl registry = new MetricsRegistryImpl(Logger.getLogger(MetricsRegistryImpl.class), ProbeLevel.INFO);
        ProbeBuilder builder = registry.newProbeBuilder().withTag("tag1", "value1");
        builder.register(this, "probe1", INFO, COUNT, new com.hazelcast.internal.metrics.LongProbeFunction<ProbeBuilderImplTest>() {
            @Override
            public long get(ProbeBuilderImplTest source) {
                return source.probe1;
            }
        });
        builder.register(this, "secondProbe", INFO, BYTES, new com.hazelcast.internal.metrics.LongProbeFunction<ProbeBuilderImplTest>() {
            @Override
            public long get(ProbeBuilderImplTest source) {
                return source.probe2;
            }
        });
        assertProbes(registry);
    }
}

