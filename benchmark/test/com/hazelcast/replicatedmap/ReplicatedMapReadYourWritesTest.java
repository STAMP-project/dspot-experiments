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
package com.hazelcast.replicatedmap;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapReadYourWritesTest extends ReplicatedMapAbstractTest {
    @Test
    public void testReadYourWritesBySize() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        final int count = 100;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        map1.putAll(map);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(count, map1.size());
                Assert.assertEquals(count, map2.size());
            }
        });
    }

    @Test
    public void testReadYourWritesByGet() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<String, Integer> map1 = instance1.getReplicatedMap("default");
        ReplicatedMap<String, Integer> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < 1000; i++) {
            assertReadYourWriteByGet(instance2, map1, i);
            assertReadYourWriteByGet(instance1, map2, i);
        }
    }

    @Test
    public void testReadYourWritesByContainsKey() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<String, Integer> map1 = instance1.getReplicatedMap("default");
        ReplicatedMap<String, Integer> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < 1000; i++) {
            assertReadYourWriteByContainsKey(instance2, map1, i);
            assertReadYourWriteByContainsKey(instance1, map2, i);
        }
    }

    @Test
    public void testReadYourWritesByContainsValue() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<String, Integer> map1 = instance1.getReplicatedMap("default");
        ReplicatedMap<String, Integer> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < 1000; i++) {
            assertReadYourWriteByContainsValue(instance2, map1, i);
            assertReadYourWriteByContainsValue(instance1, map2, i);
        }
    }
}

