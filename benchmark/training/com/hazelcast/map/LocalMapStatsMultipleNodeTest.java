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
package com.hazelcast.map;


import EvictionPolicy.LRU;
import GroupProperty.PARTITION_COUNT;
import MaxSizeConfig.MaxSizePolicy.PER_PARTITION;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalMapStatsMultipleNodeTest extends HazelcastTestSupport {
    @Test
    public void testHits_whenMultipleNodes() throws InterruptedException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance[] instances = factory.newInstances(getConfig());
        MultiMap<Object, Object> multiMap0 = instances[0].getMultiMap("testHits_whenMultipleNodes");
        MultiMap<Object, Object> multiMap1 = instances[1].getMultiMap("testHits_whenMultipleNodes");
        // InternalPartitionService is used in order to determine owners of these keys that we will use.
        InternalPartitionService partitionService = HazelcastTestSupport.getNode(instances[0]).getPartitionService();
        Address address = partitionService.getPartitionOwner(partitionService.getPartitionId("test1"));
        boolean inFirstInstance = address.equals(HazelcastTestSupport.getNode(instances[0]).getThisAddress());
        multiMap0.get("test0");
        multiMap0.put("test1", 1);
        multiMap1.get("test1");
        Assert.assertEquals((inFirstInstance ? 1 : 0), multiMap0.getLocalMultiMapStats().getHits());
        Assert.assertEquals((inFirstInstance ? 0 : 1), multiMap1.getLocalMultiMapStats().getHits());
        multiMap0.get("test1");
        multiMap1.get("test1");
        Assert.assertEquals((inFirstInstance ? 0 : 3), multiMap1.getLocalMultiMapStats().getHits());
    }

    @Test
    public void testPutStats_afterPutAll() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance[] instances = factory.newInstances(getConfig());
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 1; i <= 5000; i++) {
            map.put(i, i);
        }
        IMap<Integer, Integer> iMap = instances[0].getMap("example");
        iMap.putAll(map);
        final LocalMapStats localMapStats = iMap.getLocalMapStats();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(5000, localMapStats.getPutOperationCount());
            }
        });
    }

    @Test
    public void testLocalMapStats_withMemberGroups() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        final String[] firstMemberGroup = new String[]{ "127.0.0.1", "127.0.0.2" };
        final String[] secondMemberGroup = new String[]{ "127.0.0.3" };
        final Config config = createConfig(mapName, firstMemberGroup, secondMemberGroup);
        final String[] addressArray = concatenateArrays(firstMemberGroup, secondMemberGroup);
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(addressArray);
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        final HazelcastInstance node3 = factory.newHazelcastInstance(config);
        final IMap<Object, Object> test = node3.getMap(mapName);
        test.put(1, 1);
        assertBackupEntryCount(1, mapName, factory.getAllHazelcastInstances());
    }

    @Test
    public void testLocalMapStats_preservedAfterEviction() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        config.getProperties().setProperty(PARTITION_COUNT.getName(), "5");
        MapConfig mapConfig = config.getMapConfig(mapName);
        mapConfig.setEvictionPolicy(LRU);
        MaxSizeConfig maxSizeConfig = mapConfig.getMaxSizeConfig();
        maxSizeConfig.setMaxSizePolicy(PER_PARTITION);
        maxSizeConfig.setSize(25);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(mapName);
        final CountDownLatch entryEvictedLatch = new CountDownLatch(700);
        map.addEntryListener(new EntryEvictedListener() {
            @Override
            public void entryEvicted(EntryEvent event) {
                entryEvictedLatch.countDown();
            }
        }, true);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
            Assert.assertEquals(i, map.get(i));
        }
        LocalMapStats localMapStats = map.getLocalMapStats();
        Assert.assertEquals(1000, localMapStats.getHits());
        Assert.assertEquals(1000, localMapStats.getPutOperationCount());
        Assert.assertEquals(1000, localMapStats.getGetOperationCount());
        HazelcastTestSupport.assertOpenEventually(entryEvictedLatch);
        localMapStats = map.getLocalMapStats();
        Assert.assertEquals(1000, localMapStats.getHits());
        Assert.assertEquals(1000, localMapStats.getPutOperationCount());
        Assert.assertEquals(1000, localMapStats.getGetOperationCount());
    }
}

