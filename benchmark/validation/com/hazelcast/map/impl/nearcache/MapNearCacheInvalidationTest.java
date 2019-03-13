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
package com.hazelcast.map.impl.nearcache;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapNearCacheInvalidationTest extends HazelcastTestSupport {
    private final String mapName = HazelcastTestSupport.randomMapName();

    @Test
    public void testBatchInvalidationRemovesEntries() {
        Config config = getConfig(mapName).setProperty(GroupProperty.PARTITION_COUNT.getName(), "1");
        MapNearCacheInvalidationTest.configureBatching(config, 12, 1);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = node1.getMap(mapName);
        final IMap<Integer, Integer> map2 = node2.getMap(mapName);
        int size = 1000;
        // fill map-1
        for (int i = 0; i < size; i++) {
            map1.put(i, i);
        }
        // fill Near Cache on node-1
        for (int i = 0; i < size; i++) {
            map1.get(i);
        }
        // fill Near Cache on node-2
        for (int i = 0; i < size; i++) {
            map2.get(i);
        }
        // generate invalidation data
        for (int i = 0; i < size; i++) {
            map1.put(i, i);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                NearCache nearCache1 = getNearCache();
                NearCache nearCache2 = getNearCache();
                Assert.assertEquals(0, ((nearCache1.size()) + (nearCache2.size())));
            }
        });
    }

    @Test
    public void testHigherBatchSize_shouldNotCauseAnyInvalidation_onRemoteNode() {
        Config config = getConfig(mapName);
        MapNearCacheInvalidationTest.configureBatching(config, Integer.MAX_VALUE, Integer.MAX_VALUE);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        IMap<String, Integer> map1 = node1.getMap(mapName);
        IMap<String, Integer> map2 = node2.getMap(mapName);
        int size = 1000;
        List<String> keys = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            keys.add(HazelcastTestSupport.generateKeyOwnedBy(node1));
        }
        // fill map-1
        for (int i = 0; i < size; i++) {
            map1.put(keys.get(i), i);
        }
        // fill Near Cache on node-1
        for (int i = 0; i < size; i++) {
            map1.get(keys.get(i));
        }
        // fill Near Cache on node-2
        for (int i = 0; i < size; i++) {
            map2.get(keys.get(i));
        }
        // generate invalidation data
        for (int i = 0; i < size; i++) {
            map1.put(keys.get(i), i);
        }
        NearCache nearCache1 = getNearCache();
        NearCache nearCache2 = getNearCache();
        // Near Cache on one node should be invalidated completely, other node should not receive any event
        // (due to the higher invalidation batch-size)
        Assert.assertEquals(size, ((nearCache1.size()) + (nearCache2.size())));
    }

    @Test
    public void testMapClear_shouldClearNearCaches_onOwnerAndBackupNodes() {
        Config config = getConfig(mapName).setProperty(GroupProperty.PARTITION_COUNT.getName(), "1");
        MapNearCacheInvalidationTest.configureBatching(config, 5, 5);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = node1.getMap(mapName);
        final IMap<Integer, Integer> map2 = node2.getMap(mapName);
        int size = 1000;
        // fill map-1
        for (int i = 0; i < size; i++) {
            map1.put(i, i);
        }
        // fill Near Cache on node-1
        for (int i = 0; i < size; i++) {
            map1.get(i);
        }
        // fill Near Cache on node-2
        for (int i = 0; i < size; i++) {
            map2.get(i);
        }
        map1.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                NearCache nearCache1 = getNearCache();
                NearCache nearCache2 = getNearCache();
                Assert.assertEquals(0, ((nearCache1.size()) + (nearCache2.size())));
            }
        });
    }

    @Test
    public void testMapEvictAll_shouldClearNearCaches_onOwnerAndBackupNodes() {
        Config config = getConfig(mapName).setProperty(GroupProperty.PARTITION_COUNT.getName(), "1");
        MapNearCacheInvalidationTest.configureBatching(config, 5, 5);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = node1.getMap(mapName);
        final IMap<Integer, Integer> map2 = node2.getMap(mapName);
        int size = 1000;
        // fill map-1
        for (int i = 0; i < size; i++) {
            map1.put(i, i);
        }
        // fill Near Cache on node-1
        for (int i = 0; i < size; i++) {
            map1.get(i);
        }
        // fill Near Cache on node-2
        for (int i = 0; i < size; i++) {
            map2.get(i);
        }
        map1.evictAll();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                NearCache nearCache1 = getNearCache();
                NearCache nearCache2 = getNearCache();
                Assert.assertEquals(0, ((nearCache1.size()) + (nearCache2.size())));
            }
        });
    }
}

