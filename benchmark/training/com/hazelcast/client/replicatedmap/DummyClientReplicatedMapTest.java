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
package com.hazelcast.client.replicatedmap;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DummyClientReplicatedMapTest extends HazelcastTestSupport {
    private TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testGet() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance2.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testIsEmpty() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(map.isEmpty());
            }
        });
    }

    @Test
    public void testKeySet() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        final String key = generateKeyOwnedBy(instance2);
        final String value = randomString();
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Collection<String> keySet = map.keySet();
                Assert.assertEquals(1, keySet.size());
                Assert.assertEquals(key, keySet.iterator().next());
            }
        });
    }

    @Test
    public void testEntrySet() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        final String key = generateKeyOwnedBy(instance2);
        final String value = randomString();
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Set<Map.Entry<String, String>> entries = map.entrySet();
                Assert.assertEquals(1, entries.size());
                Map.Entry<String, String> entry = entries.iterator().next();
                Assert.assertEquals(key, entry.getKey());
                Assert.assertEquals(value, entry.getValue());
            }
        });
    }

    @Test
    public void testValues() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        final String key = generateKeyOwnedBy(instance2);
        final String value = randomString();
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Collection<String> values = map.values();
                Assert.assertEquals(1, values.size());
                Assert.assertEquals(value, values.iterator().next());
            }
        });
    }

    @Test
    public void testContainsKey() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        final String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        Assert.assertTrue(map.containsKey(key));
    }

    @Test
    public void testContainsValue() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        final String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        final String value = randomString();
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(map.containsValue(value));
            }
        });
    }

    @Test
    public void testSize() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, map.size());
            }
        });
    }

    @Test
    public void testClear() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        map.clear();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, map.size());
            }
        });
    }

    @Test
    public void testRemove() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        map.put(key, value);
        map.remove(key);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, map.size());
            }
        });
    }

    @Test
    public void testPutAll() throws Exception {
        HazelcastInstance instance1 = newHazelcastInstance();
        HazelcastInstance instance2 = newHazelcastInstance();
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig(instance1));
        final ReplicatedMap<String, String> map = client.getReplicatedMap(randomMapName());
        String key = generateKeyOwnedBy(instance2);
        int partitionId = instance1.getPartitionService().getPartition(key).getPartitionId();
        setPartitionId(map, partitionId);
        String value = randomString();
        HashMap<String, String> m = new HashMap<String, String>();
        m.put(key, value);
        map.putAll(m);
        Assert.assertEquals(value, map.get(key));
    }
}

