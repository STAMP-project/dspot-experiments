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
package com.hazelcast.flakeidgen.impl;


import FlakeIdGeneratorService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.test.annotation.SerializationSamplesExcluded;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class, SerializationSamplesExcluded.class })
public class FlakeIdGenerator_MemberIntegrationTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private TestHazelcastInstanceFactory factory;

    @Test
    public void smokeTest() throws Exception {
        HazelcastInstance instance = factory.newHazelcastInstance();
        final FlakeIdGenerator generator = instance.getFlakeIdGenerator("gen");
        FlakeIdConcurrencyTestUtil.concurrentlyGenerateIds(new com.hazelcast.util.function.Supplier<Long>() {
            @Override
            public Long get() {
                return generator.newId();
            }
        });
    }

    @Test
    public void statistics() {
        HazelcastInstance instance = factory.newHazelcastInstance();
        FlakeIdGenerator gen = instance.getFlakeIdGenerator("gen");
        gen.newId();
        FlakeIdGeneratorService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
        Map<String, LocalFlakeIdGeneratorStats> stats = service.getStats();
        Assert.assertTrue((!(stats.isEmpty())));
        Assert.assertTrue(stats.containsKey("gen"));
        LocalFlakeIdGeneratorStats genStats = stats.get("gen");
        Assert.assertEquals(1L, genStats.getBatchCount());
        Assert.assertTrue(((genStats.getIdCount()) > 0));
    }
}

