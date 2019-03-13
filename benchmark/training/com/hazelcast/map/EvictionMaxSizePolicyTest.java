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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.map.impl.eviction.EvictionChecker;
import com.hazelcast.map.impl.eviction.EvictorImpl;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EvictionMaxSizePolicyTest extends HazelcastTestSupport {
    private static final int PARTITION_COUNT = 271;

    @Test
    public void testPerNodePolicy() {
        int nodeCount = 1;
        testPerNodePolicy(nodeCount);
    }

    @Test
    public void testPerNodePolicy_withManyNodes() {
        int nodeCount = 2;
        testPerNodePolicy(nodeCount);
    }

    @Test
    public void testPerNodePolicy_afterGracefulShutdown() {
        int nodeCount = 2;
        int perNodeMaxSize = 1000;
        // eviction takes place if a partitions size exceeds this number
        // see EvictionChecker#toPerPartitionMaxSize
        double maxPartitionSize = ((1.0 * nodeCount) * perNodeMaxSize) / (EvictionMaxSizePolicyTest.PARTITION_COUNT);
        String mapName = "testPerNodePolicy_afterGracefulShutdown";
        Config config = createConfig(MaxSizePolicy.PER_NODE, perNodeMaxSize, mapName);
        // populate map from one of the nodes
        Collection<HazelcastInstance> nodes = createNodes(nodeCount, config);
        for (HazelcastInstance node : nodes) {
            IMap map = node.getMap(mapName);
            for (int i = 0; i < 5000; i++) {
                map.put(i, i);
            }
            node.shutdown();
            break;
        }
        for (HazelcastInstance node : nodes) {
            if (node.getLifecycleService().isRunning()) {
                int mapSize = node.getMap(mapName).size();
                String message = String.format(("map size is %d and it should be smaller " + "than maxPartitionSize * PARTITION_COUNT which is %.0f"), mapSize, (maxPartitionSize * (EvictionMaxSizePolicyTest.PARTITION_COUNT)));
                Assert.assertTrue(message, (mapSize <= (maxPartitionSize * (EvictionMaxSizePolicyTest.PARTITION_COUNT))));
            }
        }
    }

    /**
     * Eviction starts if a partitions' size exceeds this number:
     *
     * double maxPartitionSize = 1D * nodeCount * perNodeMaxSize / PARTITION_COUNT;
     *
     * when calculated `maxPartitionSize` is under 1, we should forcibly set it
     * to 1, otherwise all puts will immediately be removed by eviction.
     */
    @Test
    public void testPerNodePolicy_does_not_cause_unexpected_eviction_when_translated_partition_size_under_one() {
        String mapName = HazelcastTestSupport.randomMapName();
        int maxSize = (EvictionMaxSizePolicyTest.PARTITION_COUNT) / 2;
        Config config = createConfig(MaxSizePolicy.PER_NODE, maxSize, mapName);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap map = node.getMap(mapName);
        for (int i = 0; i < (EvictionMaxSizePolicyTest.PARTITION_COUNT); i++) {
            String keyForPartition = HazelcastTestSupport.generateKeyForPartition(node, i);
            map.put(keyForPartition, i);
            Assert.assertEquals(i, map.get(keyForPartition));
        }
    }

    @Test
    public void testOwnerAndBackupEntryCountsAreEqualAfterEviction_whenPerNodeMaxSizePolicyIsUsed() throws Exception {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createConfig(MaxSizePolicy.PER_NODE, 300, mapName);
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance node1 = instanceFactory.newHazelcastInstance(config);
        HazelcastInstance node2 = instanceFactory.newHazelcastInstance(config);
        IMap<Integer, Integer> map1 = node1.getMap(mapName);
        for (int i = 0; i < 2222; i++) {
            map1.put(i, i);
        }
        IMap map2 = node2.getMap(mapName);
        LocalMapStats localMapStats1 = map1.getLocalMapStats();
        LocalMapStats localMapStats2 = map2.getLocalMapStats();
        Assert.assertEquals(localMapStats1.getOwnedEntryCount(), localMapStats2.getBackupEntryCount());
        Assert.assertEquals(localMapStats2.getOwnedEntryCount(), localMapStats1.getBackupEntryCount());
    }

    @Test
    public void testPerPartitionPolicy() {
        final int perPartitionMaxSize = 1;
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createConfig(MaxSizePolicy.PER_PARTITION, perPartitionMaxSize, mapName);
        final Collection<IMap> maps = createMaps(mapName, config, nodeCount);
        populateMaps(maps, 1000);
        assertPerPartitionPolicyWorks(maps, perPartitionMaxSize);
    }

    @Test
    public void testUsedHeapSizePolicy() {
        final int perNodeHeapMaxSizeInMegaBytes = 10;
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createConfig(MaxSizePolicy.USED_HEAP_SIZE, perNodeHeapMaxSizeInMegaBytes, mapName);
        final Collection<IMap> maps = createMaps(mapName, config, nodeCount);
        setTestSizeEstimator(maps, MemoryUnit.MEGABYTES.toBytes(1));
        populateMaps(maps, 100);
        assertUsedHeapSizePolicyWorks(maps, perNodeHeapMaxSizeInMegaBytes);
    }

    @Test
    public void testFreeHeapSizePolicy() {
        final int freeHeapMinSizeInMegaBytes = 10;
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createConfig(MaxSizePolicy.FREE_HEAP_SIZE, freeHeapMinSizeInMegaBytes, mapName);
        final Collection<IMap> maps = createMaps(mapName, config, nodeCount);
        // make available free heap memory 5MB.
        // availableFree = maxMemoryMB - (totalMemoryMB - freeMemoryMB);
        final int totalMemoryMB = 15;
        final int freeMemoryMB = 0;
        final int maxMemoryMB = 20;
        EvictionMaxSizePolicyTest.setMockRuntimeMemoryInfoAccessor(maps, totalMemoryMB, freeMemoryMB, maxMemoryMB);
        populateMaps(maps, 100);
        // expecting map size = 0.
        // Since we are mocking free heap size and
        // it is always below the allowed min free heap size for this test.
        assertUsedFreeHeapPolicyTriggersEviction(maps);
    }

    @Test
    public void testUsedHeapPercentagePolicy() {
        final int maxUsedHeapPercentage = 49;
        final int nodeCount = 1;
        final int putCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createConfig(MaxSizePolicy.USED_HEAP_PERCENTAGE, maxUsedHeapPercentage, mapName);
        final Collection<IMap> maps = createMaps(mapName, config, nodeCount);
        // here order of `setTestMaxRuntimeMemoryInMegaBytes` and `setTestSizeEstimator`
        // should not be changed.
        setTestMaxRuntimeMemoryInMegaBytes(maps, 40);
        // object can be key or value, so heap-cost of a record = keyCost + valueCost
        final long oneObjectHeapCostInMegaBytes = 10;
        setTestSizeEstimator(maps, MemoryUnit.MEGABYTES.toBytes(oneObjectHeapCostInMegaBytes));
        populateMaps(maps, putCount);
        assertUsedHeapPercentagePolicyTriggersEviction(maps, putCount);
    }

    @Test
    public void testFreeHeapPercentagePolicy() {
        final int minFreeHeapPercentage = 51;
        final int nodeCount = 1;
        final int putCount = 1000;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createConfig(MaxSizePolicy.FREE_HEAP_PERCENTAGE, minFreeHeapPercentage, mapName);
        final Collection<IMap> maps = createMaps(mapName, config, nodeCount);
        // make available free heap memory 20MB.
        // availableFree = maxMemoryMB - (totalMemoryMB - freeMemoryMB);
        final int totalMemoryMB = 25;
        final int freeMemoryMB = 5;
        final int maxMemoryMB = 40;
        EvictionMaxSizePolicyTest.setMockRuntimeMemoryInfoAccessor(maps, totalMemoryMB, freeMemoryMB, maxMemoryMB);
        populateMaps(maps, putCount);
        // expecting map size = 0.
        // Since we are mocking free heap size and
        // it is always below the allowed min free heap size for this test.
        assertUsedFreeHeapPolicyTriggersEviction(maps);
    }

    private static final class TestEvictor extends EvictorImpl {
        TestEvictor(MapEvictionPolicy mapEvictionPolicy, EvictionChecker evictionChecker, IPartitionService partitionService) {
            super(mapEvictionPolicy, evictionChecker, partitionService, 1);
        }

        @Override
        public boolean checkEvictable(RecordStore recordStore) {
            return evictionChecker.checkEvictable(recordStore);
        }
    }
}

