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
package com.hazelcast.test.starter.answer.test;


import GroupProperty.PARTITION_COUNT;
import NodeState.ACTIVE;
import PartitionServiceState.SAFE;
import SetService.SERVICE_NAME;
import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.CollectionService;
import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.set.SetService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.TestPartitionUtils;
import com.hazelcast.internal.partition.impl.PartitionServiceState;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapPartitionContainer;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.starter.HazelcastStarter;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class AnswerTest extends HazelcastTestSupport {
    private HazelcastInstance hz;

    @Test
    public void testHazelcastInstanceImpl() {
        HazelcastInstanceImpl hazelcastInstance = HazelcastStarter.getHazelcastInstanceImpl(hz);
        LifecycleService lifecycleService = hazelcastInstance.getLifecycleService();
        Assert.assertNotNull("LifecycleService should not be null ", lifecycleService);
        Assert.assertTrue("Expected LifecycleService.isRunning() to be true", lifecycleService.isRunning());
    }

    @Test
    public void testNode() {
        Node node = HazelcastStarter.getNode(hz);
        Assert.assertNotNull("Node should not be null", node);
        Assert.assertNotNull("NodeEngine should not be null", node.getNodeEngine());
        Assert.assertNotNull("ClusterService should not be null", node.getClusterService());
        Assert.assertEquals("Expected NodeState to be ACTIVE", ACTIVE, node.getState());
        Assert.assertTrue("Expected isRunning() to be true", node.isRunning());
        Assert.assertTrue("Expected isMaster() to be true", node.isMaster());
        Address localAddress = hz.getCluster().getLocalMember().getAddress();
        Assert.assertEquals("Expected the same address from HazelcastInstance and Node", localAddress, node.getThisAddress());
    }

    @Test
    public void testNodeEngine() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        Assert.assertNotNull("NodeEngine should not be null", nodeEngine);
        HazelcastInstance hazelcastInstance = nodeEngine.getHazelcastInstance();
        Assert.assertNotNull("HazelcastInstance should not be null", hazelcastInstance);
        SerializationService serializationService = nodeEngine.getSerializationService();
        Assert.assertNotNull("SerializationService should not be null", serializationService);
        InternalOperationService operationService = nodeEngine.getOperationService();
        Assert.assertNotNull("InternalOperationService should not be null", operationService);
        CollectionService collectionService = nodeEngine.getService(SERVICE_NAME);
        Assert.assertNotNull("CollectionService from ISet should not be null", collectionService);
        collectionService = nodeEngine.getService(ListService.SERVICE_NAME);
        Assert.assertNotNull("CollectionService from IList should not be null", collectionService);
        MultiMapService multiMapService = nodeEngine.getService(MultiMapService.SERVICE_NAME);
        Assert.assertNotNull("MultiMapService should not be null", multiMapService);
    }

    @Test
    public void testClusterService() {
        Node node = HazelcastStarter.getNode(hz);
        ClusterServiceImpl clusterService = node.getClusterService();
        MemberImpl localMember = clusterService.getLocalMember();
        Assert.assertNotNull("localMember should not be null", localMember);
        Assert.assertTrue("Member should be the local member", localMember.localMember());
        Assert.assertFalse("Member should be no lite member", localMember.isLiteMember());
        Assert.assertEquals("Expected the same address from Node and local member", node.getThisAddress(), localMember.getAddress());
        MemberImpl member = clusterService.getMember(node.getThisAddress());
        Assert.assertEquals("Expected the same member via getMember(thisAddress) as the local member", localMember, member);
    }

    @Test
    public void testPartitionService() {
        Node node = HazelcastStarter.getNode(hz);
        InternalPartitionService partitionService = node.getPartitionService();
        int expectedPartitionCount = Integer.parseInt(hz.getConfig().getProperty(PARTITION_COUNT.getName()));
        IPartition[] partitions = partitionService.getPartitions();
        Assert.assertNotNull("partitions should not be null", partitions);
        HazelcastTestSupport.assertEqualsStringFormat("Expected %s partitions, but found %s", expectedPartitionCount, partitions.length);
        int partitionCount = partitionService.getPartitionCount();
        HazelcastTestSupport.assertEqualsStringFormat("Expected partitionCount of %s, but was %s", expectedPartitionCount, partitionCount);
        InternalPartition partition = partitionService.getPartition((expectedPartitionCount / 2));
        Assert.assertNotNull("partition should not be null", partition);
        Assert.assertTrue("partition should be local", partition.isLocal());
        Assert.assertEquals("partition should be owned by this node", node.getThisAddress(), partition.getOwnerOrNull());
        PartitionServiceState partitionServiceState = TestPartitionUtils.getPartitionServiceState(hz);
        Assert.assertEquals("Expected SAFE PartitionServiceState (before shutdown)", SAFE, partitionServiceState);
        hz.shutdown();
        partitionServiceState = TestPartitionUtils.getPartitionServiceState(hz);
        Assert.assertEquals("Expected SAFE PartitionServiceState (after shutdown)", SAFE, partitionServiceState);
    }

    @Test
    public void testSerializationService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        int original = 42;
        Data data = serializationService.toData(original);
        Assert.assertNotNull("data should not be null", data);
        Assert.assertFalse("data should be no proxy class", Proxy.isProxyClass(data.getClass()));
        Assert.assertEquals("toObject() should return original value", original, ((Integer) (serializationService.toObject(data))).intValue());
        SerializationService localSerializationService = new DefaultSerializationServiceBuilder().build();
        Data localData = localSerializationService.toData(original);
        Assert.assertEquals("data should be the same as from local SerializationService", localData, data);
    }

    @Test
    public void testSetService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        SetService setService = nodeEngine.getService(SERVICE_NAME);
        Assert.assertEquals(SERVICE_NAME, setService.getServiceName());
        ConcurrentMap<String, ? extends CollectionContainer> containerMap = setService.getContainerMap();
        Assert.assertTrue("containerMap should be empty", containerMap.isEmpty());
        ISet<Object> set = hz.getSet("mySet");
        set.add(42);
        Assert.assertFalse("containerMap should be empty", containerMap.isEmpty());
        CollectionContainer container = containerMap.get("mySet");
        Assert.assertEquals("Expected one item in the collection container", 1, container.size());
        Collection<CollectionItem> collection = container.getCollection();
        Assert.assertEquals("Expected one primary item in the container", 1, collection.size());
        Map<Long, CollectionItem> backupMap = container.getMap();
        Assert.assertEquals("Expected one backup item in the container", 1, backupMap.size());
        Collection<CollectionItem> values = backupMap.values();
        Iterator<CollectionItem> iterator = values.iterator();
        Assert.assertNotNull("containerMap iterator should not be null", iterator);
        Assert.assertTrue("containerMap iterator should have a next item", iterator.hasNext());
        CollectionItem collectionItem = iterator.next();
        Assert.assertNotNull("collectionItem should not be null", collectionItem);
        Data dataValue = collectionItem.getValue();
        Assert.assertNotNull("collectionItem should have a value", dataValue);
        Object value = serializationService.toObject(dataValue);
        Assert.assertEquals("Expected collectionItem value to be 42", 42, value);
        Assert.assertTrue("set should contain 42", set.contains(42));
        set.clear();
        Assert.assertFalse("set should not contain 42", set.contains(42));
        set.destroy();
    }

    @Test
    public void testQueueService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        QueueService queueService = nodeEngine.getService(QueueService.SERVICE_NAME);
        IQueue<Object> queue = hz.getQueue("myQueue");
        queue.add(42);
        QueueContainer container = queueService.getOrCreateContainer("myQueue", false);
        Assert.assertNotNull("container should not be null", container);
        Assert.assertEquals("Expected one item in the queue container", 1, container.size());
        Collection<QueueItem> collection = container.getItemQueue();
        Assert.assertEquals("Expected one primary item in the container", 1, collection.size());
        Map<Long, QueueItem> backupMap = container.getBackupMap();
        Assert.assertEquals("Expected one backup item in the container", 1, backupMap.size());
        Collection<QueueItem> values = backupMap.values();
        Iterator<QueueItem> iterator = values.iterator();
        Assert.assertNotNull("backupMap iterator should not be null", iterator);
        Assert.assertTrue("backupMap iterator should have a next item", iterator.hasNext());
        QueueItem queueItem = iterator.next();
        Assert.assertNotNull("queueItem should not be null", queueItem);
        Data dataValue = queueItem.getData();
        Assert.assertNotNull("queueItem should have a value", dataValue);
        Object value = serializationService.toObject(dataValue);
        Assert.assertEquals("Expected collectionItem value to be 42", 42, value);
        Assert.assertTrue("queue should contain 42", queue.contains(42));
        queue.clear();
        Assert.assertFalse("queue should not contain 42", queue.contains(42));
        queue.destroy();
    }

    @Test
    public void testCacheService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        HazelcastInstanceImpl hazelcastInstance = HazelcastStarter.getHazelcastInstanceImpl(hz);
        SerializationService serializationService = nodeEngine.getSerializationService();
        String key = HazelcastTestSupport.randomString();
        Data keyData = serializationService.toData(key);
        int partitionId = hz.getPartitionService().getPartition(key).getPartitionId();
        CachingProvider provider = HazelcastServerCachingProvider.createCachingProvider(hazelcastInstance);
        HazelcastCacheManager cacheManager = ((HazelcastServerCacheManager) (provider.getCacheManager()));
        Cache<String, Integer> cache = cacheManager.getCache("myCache");
        Assert.assertNull("cache should be null", cache);
        CacheConfig<String, Integer> cacheConfig = new CacheConfig<String, Integer>("myCache");
        cache = cacheManager.createCache("myCache", cacheConfig);
        Assert.assertNotNull("cache should not be null", cache);
        cacheManager.getCache("myCache");
        Assert.assertNotNull("cache should not be null", cache);
        cache.put(key, 23);
        // ICacheRecordStore access
        CacheService cacheService = nodeEngine.getService(CacheService.SERVICE_NAME);
        String cacheNameWithPrefix = cacheManager.getCacheNameWithPrefix("myCache");
        ICacheRecordStore recordStore = cacheService.getRecordStore(cacheNameWithPrefix, partitionId);
        Assert.assertNotNull("recordStore should not be null", recordStore);
        Assert.assertEquals("Expected one item in the recordStore", 1, recordStore.size());
        Object dataValue = recordStore.get(keyData, null);
        Assert.assertNotNull("dataValue should not be null", dataValue);
        Integer value = serializationService.toObject(dataValue);
        Assert.assertNotNull("Expected value not to be null", value);
        Assert.assertEquals("Expected the value to be 23", 23, ((int) (value)));
        // EntryProcessor invocation
        Map<String, EntryProcessorResult<Integer>> resultMap;
        int result;
        result = cache.invoke(key, new AnswerTest.IntegerValueEntryProcessor());
        Assert.assertEquals("Expected the result to be -23 (after invoke())", (-23), result);
        result = cache.invoke(key, new AnswerTest.IntegerValueEntryProcessor(), 42);
        Assert.assertEquals("Expected the value to be 42 (after invoke())", 42, result);
        resultMap = cache.invokeAll(Collections.singleton(key), new AnswerTest.IntegerValueEntryProcessor());
        result = resultMap.get(key).get();
        Assert.assertEquals("Expected the value to be -23 (after invokeAll())", (-23), result);
        resultMap = cache.invokeAll(Collections.singleton(key), new AnswerTest.IntegerValueEntryProcessor(), 42);
        result = resultMap.get(key).get();
        Assert.assertEquals("Expected the value to be 42 (after invokeAll())", 42, result);
        // clear and destroy
        Assert.assertTrue("cache should contain key", cache.containsKey(key));
        cache.clear();
        Assert.assertFalse("cache should not contain key", cache.containsKey(key));
        cacheManager.destroyCache("myCache");
    }

    @Test
    public void testMapService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        String key = HazelcastTestSupport.randomString();
        Data keyData = serializationService.toData(key);
        int partitionId = hz.getPartitionService().getPartition(key).getPartitionId();
        MapService mapService = nodeEngine.getService(MapService.SERVICE_NAME);
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        Assert.assertNotNull("mapServiceContext should not be null", mapServiceContext);
        PartitionContainer partitionContainer = mapServiceContext.getPartitionContainer(partitionId);
        Assert.assertNotNull("partitionContainer should not be null", partitionContainer);
        RecordStore recordStore = partitionContainer.getExistingRecordStore("myMap");
        Assert.assertNull("recordStore should be null", recordStore);
        IMap<Object, Object> map = hz.getMap("myMap");
        map.put(key, 23);
        recordStore = partitionContainer.getExistingRecordStore("myMap");
        Assert.assertNotNull("recordStore should not be null", recordStore);
        Assert.assertEquals("Expected one item in the recordStore", 1, recordStore.size());
        Object dataValue = recordStore.get(keyData, true, null);
        Assert.assertNotNull("dataValue should not be null", dataValue);
        Integer value = serializationService.toObject(dataValue);
        Assert.assertNotNull("Expected value not to be null", value);
        Assert.assertEquals("Expected value to be 23", 23, ((int) (value)));
        Assert.assertTrue("map should contain key", map.containsKey(key));
        map.clear();
        Assert.assertFalse("map should not contain key", map.containsKey(key));
        map.destroy();
    }

    @Test
    public void testMultiMapService() {
        Node node = HazelcastStarter.getNode(hz);
        NodeEngineImpl nodeEngine = node.getNodeEngine();
        MultiMapService multiMapService = nodeEngine.getService(MultiMapService.SERVICE_NAME);
        SerializationService serializationService = nodeEngine.getSerializationService();
        String key = HazelcastTestSupport.randomString();
        Data keyData = serializationService.toData(key);
        int partitionId = hz.getPartitionService().getPartition(key).getPartitionId();
        MultiMap<String, String> multiMap = hz.getMultiMap("myMultiMap");
        multiMap.put(key, "value1");
        multiMap.put(key, "value2");
        MultiMapPartitionContainer partitionContainer = multiMapService.getPartitionContainer(partitionId);
        MultiMapContainer multiMapContainer = partitionContainer.getMultiMapContainer("myMultiMap");
        ConcurrentMap<Data, MultiMapValue> multiMapValues = multiMapContainer.getMultiMapValues();
        for (Map.Entry<Data, MultiMapValue> entry : multiMapValues.entrySet()) {
            Data actualKeyData = entry.getKey();
            MultiMapValue multiMapValue = entry.getValue();
            String actualKey = serializationService.toObject(actualKeyData);
            Assert.assertEquals(keyData, actualKeyData);
            Assert.assertEquals(key, actualKey);
            Collection<MultiMapRecord> collection = multiMapValue.getCollection(false);
            Collection<String> actualValues = new ArrayList<String>(collection.size());
            for (MultiMapRecord record : collection) {
                String value = serializationService.toObject(record.getObject());
                actualValues.add(value);
            }
            Assert.assertEquals("MultiMapValue should contain 2 MultiMapRecords", 2, actualValues.size());
            Assert.assertTrue("MultiMapValue should contain value1", actualValues.contains("value1"));
            Assert.assertTrue("MultiMapValue should contain value2", actualValues.contains("value2"));
        }
        Assert.assertTrue("multiMap should contain key", multiMap.containsKey(key));
        multiMap.clear();
        Assert.assertFalse("multiMap should not contain key", multiMap.containsKey(key));
        multiMap.destroy();
    }

    private static class IntegerValueEntryProcessor implements Serializable , EntryProcessor<String, Integer, Integer> {
        @Override
        public Integer process(MutableEntry<String, Integer> entry, Object... arguments) throws EntryProcessorException {
            if ((arguments.length) > 0) {
                return ((Integer) (arguments[0]));
            }
            return -(entry.getValue());
        }
    }
}

