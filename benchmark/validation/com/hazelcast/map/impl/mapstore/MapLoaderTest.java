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
package com.hazelcast.map.impl.mapstore;


import EvictionPolicy.LRU;
import GroupProperty.PARTITION_COUNT;
import MapStoreConfig.InitialLoadMode.EAGER;
import MapStoreConfig.InitialLoadMode.LAZY;
import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.core.MapStoreFactory;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.mapstore.writebehind.TestMapUsingMapStoreBuilder;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLoaderTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSenderAndBackupTerminates_AfterInitialLoad() {
        final ILogger logger = Logger.getLogger(MapLoaderTest.class);
        String name = HazelcastTestSupport.randomString();
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setImplementation(new MapLoaderTest.DummyMapLoader());
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        logger.info("Starting cluster with 5 members");
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(5);
        HazelcastInstance[] instances = instanceFactory.newInstances(config);
        for (HazelcastInstance instance : instances) {
            instance.getLifecycleService().addLifecycleListener(new MapLoaderTest.LoggingLifecycleListener(instance.getName()));
        }
        IMap<Object, Object> map = instances[0].getMap(name);
        map.clear();
        HazelcastInstance[] ownerAndReplicas = findOwnerAndReplicas(instances, name);
        logger.info("Terminating 2 nodes from ownerOrReplica");
        ownerAndReplicas[0].getLifecycleService().terminate();
        ownerAndReplicas[1].getLifecycleService().terminate();
        logger.info("2 nodes got terminated");
        HazelcastTestSupport.assertClusterSizeEventually(3, ownerAndReplicas[3]);
        logger.info("Cluster size is 3 now");
        map = ownerAndReplicas[3].getMap(name);
        logger.info("Loading all items into the map");
        map.loadAll(false);
        logger.info("All items are loaded into the map");
        Assert.assertEquals(MapLoaderTest.DummyMapLoader.DEFAULT_SIZE, map.size());
    }

    @Test
    public void givenSpecificKeysWereReloaded_whenLoadAllIsCalled_thenAllEntriesAreLoadedFromTheStore() {
        String name = HazelcastTestSupport.randomString();
        int keysInMapStore = 10000;
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setImplementation(new MapLoaderTest.DummyMapLoader(keysInMapStore));
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance[] instances = instanceFactory.newInstances(config);
        IMap<Integer, Integer> map = instances[0].getMap(name);
        // load specific keys
        map.loadAll(TestCollectionUtils.setOfValuesBetween(0, keysInMapStore), true);
        // remove everything
        map.clear();
        // assert loadAll() with load all entries provided by the mapLoader
        map.loadAll(true);
        Assert.assertEquals(keysInMapStore, map.size());
    }

    @Test
    public void testNullChecks_withMapStore_nullInKeys() {
        String name = "testNullChecks_withMapStore";
        int keysInMapStore = 10000;
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setImplementation(new MapLoaderTest.DummyMapLoader(keysInMapStore));
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        final Set<String> keys = new HashSet<String>();
        keys.add("key");
        keys.add(null);
        expectedException.expect(NullPointerException.class);
        map.loadAll(keys, true);
    }

    @Test
    public void testNullKey_loadAll() {
        String name = "testNullIn_loadAll";
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(LAZY).setImplementation(new com.hazelcast.core.MapLoader<String, String>() {
            @Override
            public String load(String key) {
                if (key.equals("1")) {
                    return "1";
                }
                if (key.equals("2")) {
                    return "2";
                }
                if (key.equals("3")) {
                    return "3";
                }
                return null;
            }

            @Override
            public Map<String, String> loadAll(Collection<String> keys) {
                Map<String, String> val = new HashMap<String, String>();
                if (keys.contains("1")) {
                    val.put("1", "1");
                }
                if (keys.contains("2")) {
                    val.put(null, "2");
                }
                if (keys.contains("3")) {
                    val.put("3", "3");
                }
                return val;
            }

            @Override
            public Iterable<String> loadAllKeys() {
                List<String> keys = new ArrayList<String>();
                keys.add("1");
                keys.add("2");
                keys.add("3");
                return keys;
            }
        });
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        try {
            map.size();
            // We can't expect that since the exception transmission in map-loader is heavily dependant on operation execution.
            // See: https://github.com/hazelcast/hazelcast/issues/11931
            // fail("Expected a NPE due to a null key in a MapLoader");
        } catch (NullPointerException e) {
            Assert.assertEquals("Key loaded by a MapLoader cannot be null.", e.getMessage());
        }
        try {
            Assert.assertEquals(2, map.size());
            Assert.assertEquals("1", map.get("1"));
            Assert.assertEquals("2", map.get("2"));
            Assert.assertEquals("3", map.get("3"));
        } catch (NullPointerException e) {
            handleNpeFromKnownIssue(e);
        }
    }

    @Test
    public void testNullValue_loadAll() {
        String name = "testNullIn_loadAll";
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(LAZY).setImplementation(new com.hazelcast.core.MapLoader<String, String>() {
            @Override
            public String load(String key) {
                if (key.equals("1")) {
                    return "1";
                }
                if (key.equals("2")) {
                    return null;
                }
                if (key.equals("3")) {
                    return "3";
                }
                return null;
            }

            @Override
            public Map<String, String> loadAll(Collection<String> keys) {
                Map<String, String> val = new HashMap<String, String>();
                if (keys.contains("1")) {
                    val.put("1", "1");
                }
                if (keys.contains("2")) {
                    val.put("2", null);
                }
                if (keys.contains("3")) {
                    val.put("3", "3");
                }
                return val;
            }

            @Override
            public Iterable<String> loadAllKeys() {
                List<String> keys = new ArrayList<String>();
                keys.add("1");
                keys.add("2");
                keys.add("3");
                return keys;
            }
        });
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        // THIS DOES NOT THROW ANY EXCEPTION
        map.size();
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("1", map.get("1"));
        Assert.assertEquals(null, map.get("2"));
        Assert.assertEquals("3", map.get("3"));
    }

    @Test
    public void testNullValue_loadAll_withInterceptor() {
        String name = "testNullIn_loadAll";
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(LAZY).setImplementation(new com.hazelcast.core.MapLoader<String, String>() {
            @Override
            public String load(String key) {
                if (key.equals("1")) {
                    return "1";
                }
                if (key.equals("2")) {
                    return null;
                }
                if (key.equals("3")) {
                    return "3";
                }
                return null;
            }

            @Override
            public Map<String, String> loadAll(Collection<String> keys) {
                Map<String, String> val = new HashMap<String, String>();
                if (keys.contains("1")) {
                    val.put("1", "1");
                }
                if (keys.contains("2")) {
                    val.put("2", null);
                }
                if (keys.contains("3")) {
                    val.put("3", "3");
                }
                return val;
            }

            @Override
            public Iterable<String> loadAllKeys() {
                List<String> keys = new ArrayList<String>();
                keys.add("1");
                keys.add("2");
                keys.add("3");
                return keys;
            }
        });
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        map.addInterceptor(new MapLoaderTest.TestInterceptor());
        try {
            map.size();
            // We can't expect that since the exception transmission in map-loader is heavily dependant on operation execution.
            // See: https://github.com/hazelcast/hazelcast/issues/11931
            // fail("Expected a NPE due to a null value in a MapLoader");
        } catch (NullPointerException e) {
            Assert.assertEquals("Value loaded by a MapLoader cannot be null.", e.getMessage());
        }
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("1", map.get("1"));
        Assert.assertEquals(null, map.get("2"));
        Assert.assertEquals("3", map.get("3"));
    }

    @Test
    public void testNullKey_loadAllKeys() {
        String name = "testNullIn_loadAllKeys";
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(LAZY).setImplementation(new com.hazelcast.core.MapLoader<String, String>() {
            @Override
            public String load(String key) {
                if (key.equals("1")) {
                    return "1";
                }
                if (key.equals("2")) {
                    return "2";
                }
                if (key.equals("3")) {
                    return "3";
                }
                return null;
            }

            @Override
            public Map<String, String> loadAll(Collection keys) {
                Map<String, String> val = new HashMap<String, String>();
                if (keys.contains("1")) {
                    val.put("1", "1");
                }
                if (keys.contains("2")) {
                    val.put("2", "2");
                }
                if (keys.contains("3")) {
                    val.put("3", "3");
                }
                return val;
            }

            @Override
            public Iterable<String> loadAllKeys() {
                List<String> keys = new ArrayList<String>();
                keys.add("1");
                keys.add(null);
                keys.add("3");
                return keys;
            }
        });
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        try {
            map.size();
            // We can't expect that since the exception transmission in map-loader is heavily dependant on operation execution.
            // See: https://github.com/hazelcast/hazelcast/issues/11931
            // fail("Expected a NPE due to a null key in a MapLoader");
        } catch (NullPointerException e) {
            Assert.assertEquals("Key loaded by a MapLoader cannot be null.", e.getMessage());
        }
        try {
            Assert.assertEquals(0, map.size());
            Assert.assertEquals("1", map.get("1"));
            Assert.assertEquals("2", map.get("2"));
            Assert.assertEquals("3", map.get("3"));
        } catch (NullPointerException e) {
            handleNpeFromKnownIssue(e);
        }
    }

    @Test
    public void testNullChecks_withMapStore_nullKeys() {
        String name = "testNullChecks_withMapStore";
        int keysInMapStore = 10000;
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setImplementation(new MapLoaderTest.DummyMapLoader(keysInMapStore));
        Config config = new Config();
        config.getMapConfig(name).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap(name);
        expectedException.expect(NullPointerException.class);
        map.loadAll(null, true);
    }

    /**
     * https://github.com/hazelcast/hazelcast/issues/1770
     */
    @Test
    public void test1770() {
        final AtomicBoolean loadAllCalled = new AtomicBoolean();
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setImplementation(new com.hazelcast.core.MapLoader<Object, Object>() {
            @Override
            public Object load(Object key) {
                return null;
            }

            @Override
            public Map<Object, Object> loadAll(Collection keys) {
                loadAllCalled.set(true);
                return new HashMap<Object, Object>();
            }

            @Override
            public Set<Object> loadAllKeys() {
                return new HashSet<Object>(Collections.singletonList(1));
            }
        });
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig().setEnabled(true).setUrl("http://127.0.0.1:8090/mancenter");
        Config config = getConfig().setManagementCenterConfig(managementCenterConfig);
        MapConfig mapConfig = config.getMapConfig("foo").setMapStoreConfig(mapStoreConfig);
        HazelcastInstance hz = createHazelcastInstance(config);
        hz.getMap(mapConfig.getName());
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse("LoadAll should not have been called", loadAllCalled.get());
            }
        }, 10);
    }

    @Test
    public void testMapLoaderLoadUpdatingIndex_noPreload() {
        final int nodeCount = 3;
        String mapName = HazelcastTestSupport.randomString();
        MapLoaderTest.SampleIndexableObjectMapLoader loader = new MapLoaderTest.SampleIndexableObjectMapLoader();
        Config config = createMapConfig(mapName, loader);
        MapLoaderTest.NodeBuilder nodeBuilder = new MapLoaderTest.NodeBuilder(nodeCount, config).build();
        HazelcastInstance node = nodeBuilder.getRandomNode();
        IMap<Integer, MapLoaderTest.SampleIndexableObject> map = node.getMap(mapName);
        for (int i = 0; i < 10; i++) {
            map.put(i, new MapLoaderTest.SampleIndexableObject(("My-" + i), i));
        }
        SqlPredicate predicate = new SqlPredicate("name='My-5'");
        assertPredicateResultCorrect(map, predicate);
    }

    @Test
    public void testMapLoaderLoadUpdatingIndex_withPreload() {
        final int nodeCount = 3;
        String mapName = HazelcastTestSupport.randomString();
        MapLoaderTest.SampleIndexableObjectMapLoader loader = new MapLoaderTest.SampleIndexableObjectMapLoader();
        loader.preloadValues = true;
        Config config = createMapConfig(mapName, loader);
        MapLoaderTest.NodeBuilder nodeBuilder = new MapLoaderTest.NodeBuilder(nodeCount, config).build();
        HazelcastInstance node = nodeBuilder.getRandomNode();
        IMap<Integer, MapLoaderTest.SampleIndexableObject> map = node.getMap(mapName);
        SqlPredicate predicate = new SqlPredicate("name='My-5'");
        assertLoadAllKeysCount(loader, 1);
        assertPredicateResultCorrect(map, predicate);
    }

    @Test
    public void testGetAll_putsLoadedItemsToIMap() {
        Integer[] requestedKeys = new Integer[]{ 1, 2, 3 };
        AtomicInteger loadedKeysCounter = new AtomicInteger(0);
        MapStore<Integer, Integer> mapStore = createMapLoader(loadedKeysCounter);
        IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withPartitionCount(1).build();
        Set<Integer> keySet = new HashSet<Integer>(Arrays.asList(requestedKeys));
        map.getAll(keySet);
        map.getAll(keySet);
        map.getAll(keySet);
        Assert.assertEquals(requestedKeys.length, loadedKeysCounter.get());
    }

    @Test(timeout = TimeConstants.MINUTE)
    public void testMapCanBeLoaded_whenLoadAllKeysThrowsExceptionFirstTime() {
        Config config = getConfig();
        com.hazelcast.core.MapLoader failingMapLoader = new MapLoaderTest.FailingMapLoader();
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setImplementation(failingMapLoader);
        MapConfig mapConfig = config.getMapConfig(getClass().getName()).setMapStoreConfig(mapStoreConfig);
        final ILogger logger = Logger.getLogger(MapLoaderTest.LoggingLifecycleListener.class);
        HazelcastInstance[] hz = createHazelcastInstanceFactory(2).newInstances(config, 2);
        final IMap map = hz[0].getMap(mapConfig.getName());
        Throwable exception = null;
        try {
            map.get(HazelcastTestSupport.generateKeyNotOwnedBy(hz[0]));
        } catch (Throwable e) {
            exception = e;
        }
        Assert.assertNotNull("Exception wasn't propagated", exception);
        // In the first map load, partitions are notified asynchronously
        // by the com.hazelcast.map.impl.MapKeyLoader.sendKeyLoadCompleted
        // method and also some partitions are notified twice.
        // Because of this, a subsequent map load might get completed with the
        // results of the first map load.
        // This is why a subsequent map load might fail with the exception from
        // a previous load. In this case, we need to try again.
        // An alternative would be to wait for all partitions to be notified by
        // the result from the first load before initiating a second load but
        // unfortunately we can't observe this as some partitions are completed
        // twice and we might just end up observing the first completion.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                try {
                    map.loadAll(true);
                    Assert.assertEquals(1, map.size());
                } catch (IllegalStateException e) {
                    logger.info("Map load observed result from a previous load, retrying...", e);
                }
            }
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMapLoaderHittingEvictionOnInitialLoad() {
        String mapName = "testMapLoaderHittingEvictionOnInitialLoad";
        int sizePerPartition = 1;
        int partitionCount = 10;
        int entriesCount = 1000000;
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig().setMaxSizePolicy(MaxSizePolicy.PER_PARTITION).setSize(sizePerPartition);
        MapStoreConfig storeConfig = new MapStoreConfig().setEnabled(true).setInitialLoadMode(EAGER).setImplementation(new MapLoaderTest.SimpleLoader(entriesCount));
        Config config = getConfig().setProperty(PARTITION_COUNT.getName(), String.valueOf(partitionCount));
        config.getMapConfig(mapName).setEvictionPolicy(LRU).setEvictionPercentage(50).setMinEvictionCheckMillis(0).setMaxSizeConfig(maxSizeConfig).setMapStoreConfig(storeConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap imap = instance.getMap(mapName);
        imap.addInterceptor(new MapLoaderTest.TestInterceptor());
        Assert.assertEquals((sizePerPartition * partitionCount), imap.size());
    }

    private static final class SimpleLoader implements com.hazelcast.core.MapLoader<Integer, Integer> {
        private final int entriesCount;

        SimpleLoader(int entriesCount) {
            this.entriesCount = entriesCount;
        }

        @Override
        public Integer load(Integer key) {
            return key;
        }

        @Override
        public Map<Integer, Integer> loadAll(Collection<Integer> keys) {
            Map<Integer, Integer> entries = new HashMap<Integer, Integer>(keys.size());
            for (Integer key : keys) {
                entries.put(key, key);
            }
            return entries;
        }

        @Override
        public Iterable<Integer> loadAllKeys() {
            Collection<Integer> keys = new ArrayList<Integer>();
            for (int i = 0; i < (entriesCount); i++) {
                keys.add(i);
            }
            return keys;
        }
    }

    private static class TestInterceptor implements MapInterceptor , Serializable {
        @Override
        public Object interceptGet(Object value) {
            return null;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            return null;
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            return null;
        }

        @Override
        public void afterRemove(Object value) {
        }
    }

    public static class DummyMapLoader implements com.hazelcast.core.MapLoader<Integer, Integer> {
        static final int DEFAULT_SIZE = 1000;

        final Map<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>(MapLoaderTest.DummyMapLoader.DEFAULT_SIZE);

        public DummyMapLoader() {
            this(MapLoaderTest.DummyMapLoader.DEFAULT_SIZE);
        }

        public DummyMapLoader(int size) {
            for (int i = 0; i < size; i++) {
                map.put(i, i);
            }
        }

        @Override
        public Integer load(Integer key) {
            return map.get(key);
        }

        @Override
        public Map<Integer, Integer> loadAll(Collection<Integer> keys) {
            HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
            for (Integer key : keys) {
                hashMap.put(key, map.get(key));
            }
            return hashMap;
        }

        @Override
        public Iterable<Integer> loadAllKeys() {
            return map.keySet();
        }
    }

    public static class SampleIndexableObjectMapLoader implements com.hazelcast.core.MapLoader<Integer, MapLoaderTest.SampleIndexableObject> , MapStoreFactory<Integer, MapLoaderTest.SampleIndexableObject> {
        volatile boolean preloadValues = false;

        private MapLoaderTest.SampleIndexableObject[] values = new MapLoaderTest.SampleIndexableObject[10];

        private Set<Integer> keys = new HashSet<Integer>();

        private AtomicInteger loadAllKeysCallCount = new AtomicInteger(0);

        public SampleIndexableObjectMapLoader() {
            for (int i = 0; i < 10; i++) {
                keys.add(i);
                values[i] = new MapLoaderTest.SampleIndexableObject(("My-" + i), i);
            }
        }

        @Override
        public MapLoaderTest.SampleIndexableObject load(Integer key) {
            if (!(preloadValues)) {
                return null;
            }
            return values[key];
        }

        @Override
        public Map<Integer, MapLoaderTest.SampleIndexableObject> loadAll(Collection<Integer> keys) {
            if (!(preloadValues)) {
                return Collections.emptyMap();
            }
            Map<Integer, MapLoaderTest.SampleIndexableObject> data = new HashMap<Integer, MapLoaderTest.SampleIndexableObject>();
            for (Integer key : keys) {
                data.put(key, values[key]);
            }
            return data;
        }

        @Override
        public Set<Integer> loadAllKeys() {
            if (!(preloadValues)) {
                return Collections.emptySet();
            }
            loadAllKeysCallCount.incrementAndGet();
            return Collections.unmodifiableSet(keys);
        }

        @Override
        public com.hazelcast.core.MapLoader<Integer, MapLoaderTest.SampleIndexableObject> newMapStore(String mapName, Properties properties) {
            return this;
        }
    }

    public static class SampleIndexableObject implements Serializable {
        String name;

        Integer value;

        SampleIndexableObject() {
        }

        SampleIndexableObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    static class FailingMapLoader extends MapStoreAdapter {
        AtomicBoolean first = new AtomicBoolean(true);

        @Override
        public Set loadAllKeys() {
            if (first.compareAndSet(true, false)) {
                throw new IllegalStateException("Intentional exception");
            }
            return Collections.singleton("key");
        }

        @Override
        public Map loadAll(Collection keys) {
            return Collections.singletonMap("key", "value");
        }
    }

    private class NodeBuilder {
        private final int nodeCount;

        private final Config config;

        private final Random random = new Random();

        private final TestHazelcastInstanceFactory factory;

        private HazelcastInstance[] nodes;

        NodeBuilder(int nodeCount, Config config) {
            this.nodeCount = nodeCount;
            this.config = config;
            this.factory = createHazelcastInstanceFactory(nodeCount);
        }

        MapLoaderTest.NodeBuilder build() {
            nodes = factory.newInstances(config);
            return this;
        }

        HazelcastInstance getRandomNode() {
            final int nodeIndex = random.nextInt(nodeCount);
            return nodes[nodeIndex];
        }
    }

    private static class LoggingLifecycleListener implements LifecycleListener {
        private final String nodeInfo;

        private final ILogger logger;

        private LoggingLifecycleListener(String nodeInfo) {
            this.nodeInfo = nodeInfo;
            logger = Logger.getLogger(MapLoaderTest.LoggingLifecycleListener.class);
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            logger.info(((("State changed for " + (nodeInfo)) + " to ") + (event.getState())));
        }
    }
}

