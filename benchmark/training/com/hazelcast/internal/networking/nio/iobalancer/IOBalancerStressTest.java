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
package com.hazelcast.internal.networking.nio.iobalancer;


import GroupProperty.IO_BALANCER_INTERVAL_SECONDS;
import GroupProperty.IO_THREAD_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.Comparator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class IOBalancerStressTest extends HazelcastTestSupport {
    @Rule
    public final OverridePropertyRule overridePropertyRule = OverridePropertyRule.set("hazelcast.io.load", "0");

    @Test
    public void testEachConnectionUseDifferentOwnerEventually() {
        Config config = new Config().setProperty(IO_BALANCER_INTERVAL_SECONDS.getName(), "1").setProperty(IO_THREAD_COUNT.getName(), "2");
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance instance3 = Hazelcast.newHazelcastInstance(config);
        instance2.shutdown();
        instance2 = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance1.getMap(HazelcastTestSupport.randomMapName());
        for (int i = 0; i < 10000; i++) {
            map.put(i, i);
        }
        assertBalanced(instance1);
        assertBalanced(instance2);
        assertBalanced(instance3);
    }

    private static class PipelineLoadComparator implements Comparator<MigratablePipeline> {
        @Override
        public int compare(MigratablePipeline pipeline1, MigratablePipeline pipeline2) {
            final long l1 = pipeline1.load();
            final long l2 = pipeline2.load();
            return l1 < l2 ? -1 : l1 == l2 ? 0 : 1;
        }
    }
}

