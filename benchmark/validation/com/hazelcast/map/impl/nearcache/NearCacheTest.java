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


import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import EvictionPolicy.LRU;
import EvictionPolicy.NONE;
import EvictionPolicy.RANDOM;
import GroupProperty.PARTITION_COUNT;
import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.map.impl.proxy.NearCachedMapProxyImpl;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NearCacheTest extends NearCacheTestSupport {
    @Parameterized.Parameter
    public boolean batchInvalidationEnabled;

    @Test
    public void testBasicUsage() {
        int clusterSize = 3;
        int mapSize = 5000;
        final String mapName = "testBasicUsage";
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(true));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(clusterSize);
        final HazelcastInstance[] instances = factory.newInstances(config);
        IMap<Integer, Integer> map = instances[0].getMap(mapName);
        populateMap(map, mapSize);
        for (HazelcastInstance instance : instances) {
            IMap<Integer, Integer> instanceMap = instance.getMap(mapName);
            for (int i = 0; i < mapSize; i++) {
                Assert.assertNotNull(instanceMap.get(i));
            }
        }
        for (int i = 0; i < mapSize; i++) {
            map.put(i, (i * 2));
        }
        for (HazelcastInstance instance : instances) {
            IMap<Object, Object> m = instance.getMap(mapName);
            for (int i = 0; i < mapSize; i++) {
                Assert.assertNotNull(m.get(i));
            }
        }
        for (HazelcastInstance instance : instances) {
            int nearCacheSize = getNearCache(mapName, instance).size();
            Assert.assertTrue(("Near Cache size should be > 0 but was " + nearCacheSize), (nearCacheSize > 0));
        }
        map.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    NearCache nearCache = getNearCache(mapName, instance);
                    int size = nearCache.size();
                    Assert.assertEquals(("Near Cache size should be 0 but was " + size), 0, size);
                }
            }
        });
    }

    @Test
    public void test_whenEmptyMap_thenPopulatedNearCacheShouldReturnNull_neverNULL_OBJECT() {
        int size = 10;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig());
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        // populate map
        IMap<Integer, Integer> map = instance.getMap(mapName);
        for (int i = 0; i < size; i++) {
            // populate Near Cache
            Assert.assertNull(map.get(i));
            // fetch value from Near Cache
            Assert.assertNull(map.get(i));
        }
    }

    @Test
    public void test_whenCacheIsFullPutOnSameKeyShouldUpdateValue_withEvictionPolicyIsNONE() {
        int size = 10;
        int maxSize = 5;
        IMap<Integer, Integer> map = getMapConfiguredWithMaxSizeAndPolicy(NONE, maxSize);
        populateMap(map, size);
        populateNearCache(map, size);
        Assert.assertEquals(maxSize, getNearCacheSize(map));
        Assert.assertEquals(1, map.get(1).intValue());
        map.put(1, 1502);
        Assert.assertEquals(1502, map.get(1).intValue());
        Assert.assertEquals(1502, map.get(1).intValue());
    }

    @Test
    public void testNearCacheEviction() {
        String mapName = "testNearCacheEviction";
        NearCacheConfig nearCacheConfig = newNearCacheConfigWithEntryCountEviction(LRU, NearCacheTestSupport.MAX_CACHE_SIZE);
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        HazelcastInstance hazelcastInstance = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = hazelcastInstance.getMap(mapName);
        testNearCacheEviction(map, NearCacheTestSupport.MAX_CACHE_SIZE);
    }

    @Test
    public void testNearCacheEviction_withMapClear() {
        final int size = 10;
        String mapName = "testNearCacheEvictionWithMapClear";
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setInvalidateOnChange(true);
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hazelcastInstance1 = factory.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = hazelcastInstance1.getMap(mapName);
        final IMap<Integer, Integer> map2 = hazelcastInstance2.getMap(mapName);
        // populate map
        populateMap(map1, size);
        // populate Near Caches
        populateNearCache(map1, size);
        populateNearCache(map2, size);
        // clear map should trigger Near Cache eviction
        map1.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                // assert that the Near Cache doesn't return any cached values
                for (int i = 0; i < size; i++) {
                    Assert.assertNull(map1.get(i));
                    Assert.assertNull(map2.get(i));
                }
                Assert.assertEquals(0, getNearCacheStats(map1).getEvictions());
                Assert.assertEquals(0, getNearCacheStats(map2).getEvictions());
            }
        });
    }

    @Test
    public void testNearCacheStats() {
        int mapSize = 1000;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(false));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance[] instances = factory.newInstances(config);
        // populate map
        IMap<Integer, Integer> map = instances[0].getMap(mapName);
        populateMap(map, mapSize);
        // populate Near Cache
        populateNearCache(map, mapSize);
        NearCacheStats stats = getNearCacheStats(map);
        long ownedEntryCount = stats.getOwnedEntryCount();
        long misses = stats.getMisses();
        Assert.assertTrue(String.format("Near Cache entry count should be > %d but were %d", 400, ownedEntryCount), (ownedEntryCount > 400));
        Assert.assertEquals(String.format("Near Cache misses should be %d but were %d", mapSize, misses), mapSize, misses);
        // make some hits
        populateNearCache(map, mapSize);
        long hits = stats.getHits();
        misses = stats.getMisses();
        long hitsAndMisses = hits + misses;
        Assert.assertTrue(String.format("Near Cache hits should be > %d but were %d", 400, hits), (hits > 400));
        Assert.assertTrue(String.format("Near Cache misses should be > %d but were %d", 400, misses), (misses > 400));
        Assert.assertEquals(String.format("Near Cache hits + misses should be %s but were %d", (mapSize * 2), hitsAndMisses), (mapSize * 2), hitsAndMisses);
    }

    @Test
    public void testNearCacheMemoryCostCalculation() {
        testNearCacheMemoryCostCalculation(1);
    }

    @Test
    public void testNearCacheMemoryCostCalculation_withConcurrentCacheMisses() {
        testNearCacheMemoryCostCalculation(10);
    }

    @Test
    public void testNearCacheInvalidationByUsingMapPutAll() {
        int clusterSize = 3;
        int mapSize = 5000;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(true));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(clusterSize);
        HazelcastInstance[] instances = factory.newInstances(config);
        final IMap<Integer, Integer> map = instances[0].getMap(mapName);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        // more-or-less (count / no_of_nodes) should be in the Near Cache now
        Assert.assertTrue(((getNearCacheSize(map)) > ((mapSize / clusterSize) - (mapSize * 0.1))));
        Map<Integer, Integer> invalidationMap = new HashMap<Integer, Integer>(mapSize);
        populateMap(invalidationMap, mapSize);
        // this should invalidate the Near Cache
        map.putAll(invalidationMap);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("Invalidation is not working on putAll()", 0, getNearCacheSize(map));
            }
        });
    }

    @Test
    public void testMapContainsKey_withNearCache() {
        int clusterSize = 3;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(true));
        HazelcastInstance instance = createHazelcastInstanceFactory(clusterSize).newInstances(config)[0];
        IMap<String, String> map = instance.getMap(mapName);
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.get("key1");
        map.get("key2");
        map.get("key3");
        Assert.assertTrue(map.containsKey("key1"));
        Assert.assertFalse(map.containsKey("key5"));
        map.remove("key1");
        Assert.assertFalse(map.containsKey("key5"));
        Assert.assertTrue(map.containsKey("key2"));
        Assert.assertFalse(map.containsKey("key1"));
    }

    @Test
    public void testCacheLocalEntries() {
        int instanceCount = 2;
        String mapName = "testCacheLocalEntries";
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setCacheLocalEntries(true);
        nearCacheConfig.setInvalidateOnChange(false);
        Config config = getConfig();
        MapConfig mapConfig = config.getMapConfig(mapName);
        mapConfig.setNearCacheConfig(nearCacheConfig);
        HazelcastInstance[] instances = createHazelcastInstanceFactory(instanceCount).newInstances(config);
        IMap<String, String> map = instances[0].getMap(mapName);
        for (int i = 0; i < (NearCacheTestSupport.MAX_CACHE_SIZE); i++) {
            map.put(("key" + i), ("value" + i));
        }
        // warm-up cache
        for (int i = 0; i < (NearCacheTestSupport.MAX_CACHE_SIZE); i++) {
            map.get(("key" + i));
        }
        int nearCacheSize = getNearCacheSize(map);
        Assert.assertEquals(String.format("Near Cache size should be %d but was %d", NearCacheTestSupport.MAX_CACHE_SIZE, nearCacheSize), NearCacheTestSupport.MAX_CACHE_SIZE, nearCacheSize);
    }

    // issue 1570
    @Test
    public void testNullValueNearCache() {
        int clusterSize = 2;
        String mapName = "testNullValueNearCache";
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig());
        HazelcastInstance instance = createHazelcastInstanceFactory(clusterSize).newInstances(config)[0];
        IMap<String, String> map = instance.getMap(mapName);
        for (int i = 0; i < (NearCacheTestSupport.MAX_CACHE_SIZE); i++) {
            Assert.assertNull(map.get(("key" + i)));
        }
        for (int i = 0; i < (NearCacheTestSupport.MAX_CACHE_SIZE); i++) {
            Assert.assertNull(map.get(("key" + i)));
        }
        LocalMapStats stats = map.getLocalMapStats();
        Assert.assertTrue(String.format("Near Cache operation count should be < %d but was %d", ((NearCacheTestSupport.MAX_CACHE_SIZE) * 2), stats.getGetOperationCount()), ((stats.getGetOperationCount()) < ((NearCacheTestSupport.MAX_CACHE_SIZE) * 2)));
    }

    @Test
    public void testGetAll() {
        int mapSize = 1000;
        String mapName = "testGetAllWithNearCache";
        Config config = getConfig();
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setInvalidateOnChange(false);
        config.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        TestHazelcastInstanceFactory hazelcastInstanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance[] instances = hazelcastInstanceFactory.newInstances(config);
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastInstance hazelcastInstance = instances[0];
        InternalPartitionService partitionService = HazelcastTestSupport.getPartitionService(hazelcastInstance);
        // populate map
        IMap<Integer, Integer> map = hazelcastInstance.getMap(mapName);
        populateMap(map, mapSize);
        Set<Integer> keys = map.keySet();
        // populate Near Cache
        int expectedHits = 0;
        for (int i = 0; i < mapSize; i++) {
            map.get(i);
            int partitionId = partitionService.getPartitionId(i);
            if (!(partitionService.isPartitionOwner(partitionId))) {
                // map proxy stores non-owned entries (belong to partition which its not owner) inside its Near Cache
                expectedHits++;
            }
        }
        // generate Near Cache hits
        Map<Integer, Integer> allEntries = map.getAll(keys);
        for (int i = 0; i < mapSize; i++) {
            Assert.assertEquals(i, ((int) (allEntries.get(i))));
        }
        // check Near Cache hits
        long hits = getNearCacheStats(map).getHits();
        Assert.assertEquals(String.format("Near Cache hits should be %d but were %d", expectedHits, hits), expectedHits, hits);
    }

    @Test
    public void testGetAsync() {
        int mapSize = 1000;
        int expectedHits = 400;
        String mapName = "testGetAsyncWithNearCache";
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(false));
        TestHazelcastInstanceFactory hazelcastInstanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = hazelcastInstanceFactory.newHazelcastInstance(config);
        hazelcastInstanceFactory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance1.getMap(mapName);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        for (int i = 0; i < mapSize; i++) {
            map.getAsync(i);
        }
        long hits = getNearCacheStats(map).getHits();
        Assert.assertTrue(String.format("Near Cache hits should be > %d but were %d", expectedHits, hits), (hits > expectedHits));
    }

    @Test
    public void testGetAsyncPopulatesNearCache() throws Exception {
        int mapSize = 1000;
        String mapName = "testGetAsyncPopulatesNearCache";
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInvalidateOnChange(false));
        TestHazelcastInstanceFactory hazelcastInstanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz = hazelcastInstanceFactory.newHazelcastInstance(config);
        hazelcastInstanceFactory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = hz.getMap(mapName);
        populateMap(map, mapSize);
        // populate Near Cache
        for (int i = 0; i < mapSize; i++) {
            Future future = map.getAsync(i);
            future.get();
        }
        // generate Near Cache hits
        populateNearCache(map, mapSize);
        long ownedEntryCount = getNearCacheStats(map).getOwnedEntryCount();
        Assert.assertTrue(String.format("Near Cache should be populated but current size is %d", ownedEntryCount), (ownedEntryCount > 0));
    }

    @Test
    public void testAfterLoadAllWithDefinedKeysNearCacheIsInvalidated() {
        int mapSize = 1000;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfigWithMapStoreConfig(mapName);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        Set<Integer> keys = map.keySet();
        map.loadAll(keys, true);
        Assert.assertEquals(0, getNearCacheStats(map).getOwnedEntryCount());
    }

    @Test
    public void testAfterLoadAllNearCacheIsInvalidated() {
        int mapSize = 1000;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfigWithMapStoreConfig(mapName);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        map.loadAll(true);
        Assert.assertEquals(0, getNearCacheStats(map).getOwnedEntryCount());
    }

    @Test
    public void testAfterSubmitToKeyWithCallbackNearCacheIsInvalidated() throws Exception {
        int mapSize = 1000;
        String mapName = HazelcastTestSupport.randomMapName();
        Random random = new Random();
        Config config = createNearCachedMapConfig(mapName);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        final CountDownLatch latch = new CountDownLatch(10);
        ExecutionCallback<Integer> callback = new ExecutionCallback<Integer>() {
            @Override
            public void onResponse(Integer response) {
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        };
        int randomKey = random.nextInt(mapSize);
        map.submitToKey(randomKey, new com.hazelcast.map.AbstractEntryProcessor<Integer, Integer>() {
            @Override
            public Object process(Map.Entry<Integer, Integer> entry) {
                int currentValue = entry.getValue();
                int newValue = currentValue + 1;
                entry.setValue(newValue);
                return newValue;
            }
        }, callback);
        latch.await(3, TimeUnit.SECONDS);
        Assert.assertEquals((mapSize - 1), getNearCacheStats(map).getOwnedEntryCount());
    }

    @Test
    public void testAfterExecuteOnEntriesNearCacheIsInvalidated() {
        int mapSize = 10;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfig(mapName);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap(mapName);
        for (int i = 0; i < mapSize; i++) {
            map.put(i, new SampleTestObjects.Employee(i, "", 0, true, 0.0));
        }
        populateNearCache(map, mapSize);
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate predicate = e.get("salary").equal(0);
        map.executeOnEntries(new com.hazelcast.map.AbstractEntryProcessor<Integer, SampleTestObjects.Employee>() {
            @Override
            public Object process(Map.Entry<Integer, SampleTestObjects.Employee> entry) {
                SampleTestObjects.Employee employee = entry.getValue();
                double currentSalary = employee.getSalary();
                double newSalary = currentSalary + 10;
                employee.setSalary(newSalary);
                return newSalary;
            }
        }, predicate);
        Assert.assertEquals(0, getNearCacheStats(map).getOwnedEntryCount());
    }

    @Test
    public void testNearCacheInvalidation_WithLFU_whenMaxSizeExceeded() {
        int mapSize = 2000;
        final int maxSize = 1000;
        final IMap<Integer, Integer> map = getMapConfiguredWithMaxSizeAndPolicy(LFU, maxSize);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                triggerNearCacheEviction(map);
                long ownedEntryCount = getNearCacheStats(map).getOwnedEntryCount();
                Assert.assertEquals(("owned entry count " + ownedEntryCount), maxSize, ownedEntryCount);
            }
        });
    }

    @Test
    public void testNearCacheInvalidation_WithLRU_whenMaxSizeExceeded() {
        int mapSize = 2000;
        final int maxSize = 1000;
        final IMap<Integer, Integer> map = getMapConfiguredWithMaxSizeAndPolicy(LRU, maxSize);
        populateMap(map, mapSize);
        populateNearCache(map, mapSize);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                triggerNearCacheEviction(map);
                long ownedEntryCount = getNearCacheStats(map).getOwnedEntryCount();
                Assert.assertEquals(("owned entry count " + ownedEntryCount), maxSize, ownedEntryCount);
            }
        });
    }

    @Test
    public void testNearCacheInvalidation_WithRandom_whenMaxSizeExceeded() {
        testNearCacheInvalidation_whenMaxSizeExceeded(RANDOM);
    }

    @Test
    public void testNearCacheInvalidation_WitNone_whenMaxSizeExceeded() {
        testNearCacheInvalidation_whenMaxSizeExceeded(NONE);
    }

    @Test
    public void testNearCacheGetAsyncTwice() throws Exception {
        String mapName = HazelcastTestSupport.randomName();
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setInMemoryFormat(OBJECT);
        nearCacheConfig.getEvictionConfig().setMaximumSizePolicy(ENTRY_COUNT).setSize(10);
        Config config = getConfig();
        config.addMapConfig(new MapConfig(mapName).setNearCacheConfig(nearCacheConfig));
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        map.getAsync(1).get();
        HazelcastTestSupport.sleepMillis(1000);
        Assert.assertNull(map.getAsync(1).get());
    }

    /**
     * Near Cache has its own eviction/expiration mechanism, so eviction/expiration on an IMap
     * should not force any Near Cache eviction/expiration. Exceptions from this rule are direct calls to
     * <ul>
     * <li>{@link IMap#evict(Object)}</li>
     * <li>{@link IMap#evictAll()}</li>
     * </ul>
     */
    @Test
    public void testNearCacheEntriesNotExpired_afterIMapExpiration() {
        int mapSize = 3;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfig(mapName);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap(mapName);
        CountDownLatch latch = new CountDownLatch(mapSize);
        addEntryEvictedListener(map, latch);
        populateMapWithExpirableEntries(map, mapSize, 3, TimeUnit.SECONDS);
        populateNearCache(map, mapSize);
        int nearCacheSizeBeforeExpiration = getNearCacheSize(map);
        waitUntilEvictionEventsReceived(latch);
        // wait some extra time for possible events
        HazelcastTestSupport.sleepSeconds(2);
        int nearCacheSizeAfterExpiration = getNearCacheSize(map);
        NearCacheStats stats = getNearCacheStats(map);
        Assert.assertEquals(0, stats.getExpirations());
        Assert.assertEquals(0, stats.getEvictions());
        Assert.assertEquals(nearCacheSizeBeforeExpiration, nearCacheSizeAfterExpiration);
    }

    @Test
    public void testMapEvictAll_clearsLocalNearCache() {
        int size = 1000;
        final String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfig(mapName);
        config.setProperty(PARTITION_COUNT.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance1.getMap(mapName);
        populateMap(map, size);
        populateNearCache(map, size);
        map.evictAll();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, getNearCacheSize(instance1.getMap(mapName)));
                Assert.assertEquals(0, getNearCacheSize(instance2.getMap(mapName)));
                Assert.assertEquals(0, getNearCacheSize(instance3.getMap(mapName)));
            }
        });
    }

    @Test
    public void testMapClear_clearsLocalNearCache() {
        int size = 1000;
        final String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNearCachedMapConfig(mapName);
        config.setProperty(PARTITION_COUNT.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance1.getMap(mapName);
        populateMap(map, size);
        populateNearCache(map, size);
        map.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, getNearCacheSize(instance1.getMap(mapName)));
                Assert.assertEquals(0, getNearCacheSize(instance2.getMap(mapName)));
                Assert.assertEquals(0, getNearCacheSize(instance3.getMap(mapName)));
            }
        });
    }

    @Test
    public void testNearCacheTTLRecordsExpired() {
        NearCacheConfig nearCacheConfig = newNearCacheConfig().setTimeToLiveSeconds(NearCacheTestSupport.MAX_TTL_SECONDS).setInvalidateOnChange(false);
        testNearCacheExpiration(nearCacheConfig);
    }

    @Test
    public void testNearCacheMaxIdleRecordsExpired() {
        NearCacheConfig nearCacheConfig = newNearCacheConfig().setMaxIdleSeconds(NearCacheTestSupport.MAX_IDLE_SECONDS).setInvalidateOnChange(false);
        testNearCacheExpiration(nearCacheConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNearCache_whenInMemoryFormatIsNative_thenThrowIllegalArgumentException() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setNearCacheConfig(newNearCacheConfig().setInMemoryFormat(NATIVE));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        instance.getMap(mapName);
    }

    @Test
    public void multiple_get_on_non_existing_key_generates_one_miss() throws Exception {
        String mapName = "test";
        Config config = getConfig();
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setCacheLocalEntries(true);
        config.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap map = node.getMap(mapName);
        map.get(1);
        map.get(1);
        map.get(1);
        NearCacheStats nearCacheStats = map.getLocalMapStats().getNearCacheStats();
        Assert.assertEquals(1, nearCacheStats.getMisses());
    }

    @Test
    public void smoke_near_cache_population() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        String mapName = "test";
        int mapSize = 1000;
        // 1. create cluster
        Config config = getConfig();
        HazelcastInstance server1 = factory.newHazelcastInstance(config);
        HazelcastInstance server2 = factory.newHazelcastInstance(config);
        HazelcastInstance server3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, server1, server2, server3);
        // 2. populate server side map
        IMap<Integer, Integer> nodeMap = server1.getMap(mapName);
        for (int i = 0; i < mapSize; i++) {
            nodeMap.put(i, i);
        }
        // 3. add client with Near Cache
        NearCacheConfig nearCacheConfig = newNearCacheConfig();
        nearCacheConfig.setInvalidateOnChange(true);
        nearCacheConfig.setCacheLocalEntries(true);
        nearCacheConfig.setName(mapName);
        Config nearCachedConfig = getConfig();
        nearCachedConfig.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        HazelcastInstance client = factory.newHazelcastInstance(nearCachedConfig);
        // 4. populate Near Cache
        final IMap<Integer, Integer> nearCachedMap = client.getMap(mapName);
        for (int i = 0; i < mapSize; i++) {
            Assert.assertNotNull(nearCachedMap.get(i));
        }
        // 5. assert number of entries in client Near Cache
        Assert.assertEquals(mapSize, ((NearCachedMapProxyImpl) (nearCachedMap)).getNearCache().size());
    }
}

