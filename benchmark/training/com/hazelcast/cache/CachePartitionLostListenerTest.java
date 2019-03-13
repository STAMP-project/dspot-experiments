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
package com.hazelcast.cache;


import CacheEventType.PARTITION_LOST;
import CacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.CachePartitionEventData;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.cache.impl.event.CachePartitionLostEvent;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.partition.AbstractPartitionLostListenerTest;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachePartitionLostListenerTest extends AbstractPartitionLostListenerTest {
    public static class EventCollectingCachePartitionLostListener implements CachePartitionLostListener {
        private final List<CachePartitionLostEvent> events = Collections.synchronizedList(new LinkedList<CachePartitionLostEvent>());

        private final int backupCount;

        public EventCollectingCachePartitionLostListener(int backupCount) {
            this.backupCount = backupCount;
        }

        @Override
        public void partitionLost(CachePartitionLostEvent event) {
            this.events.add(event);
        }

        public List<CachePartitionLostEvent> getEvents() {
            synchronized(events) {
                return new ArrayList<CachePartitionLostEvent>(events);
            }
        }

        public int getBackupCount() {
            return backupCount;
        }
    }

    @Test
    public void test_partitionLostListenerInvoked() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp(1);
        final HazelcastInstance instance = instances.get(0);
        HazelcastServerCachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(instance);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CacheConfig<Integer, String> config = new CacheConfig<Integer, String>();
        Cache<Integer, String> cache = cacheManager.createCache(getIthCacheName(0), config);
        ICache iCache = cache.unwrap(ICache.class);
        final CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener listener = new CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener(0);
        iCache.addPartitionLostListener(listener);
        final IPartitionLostEvent internalEvent = new IPartitionLostEvent(1, 1, null);
        CacheService cacheService = HazelcastTestSupport.getNode(instance).getNodeEngine().getService(SERVICE_NAME);
        cacheService.onPartitionLost(internalEvent);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                List<CachePartitionLostEvent> events = listener.getEvents();
                Assert.assertEquals(1, events.size());
                CachePartitionLostEvent event = events.get(0);
                Assert.assertEquals(internalEvent.getPartitionId(), event.getPartitionId());
                Assert.assertEquals(getIthCacheName(0), event.getSource());
                Assert.assertEquals(getIthCacheName(0), event.getName());
                Assert.assertEquals(instance.getCluster().getLocalMember(), event.getMember());
                Assert.assertEquals(PARTITION_LOST, event.getEventType());
            }
        });
        cacheManager.destroyCache(getIthCacheName(0));
        cacheManager.close();
        cachingProvider.close();
    }

    @Test
    public void test_partitionLostListenerInvoked_whenNodeCrashed() {
        List<HazelcastInstance> instances = getCreatedInstancesShuffledAfterWarmedUp(2);
        HazelcastInstance survivingInstance = instances.get(0);
        HazelcastInstance terminatingInstance = instances.get(1);
        HazelcastServerCachingProvider cachingProvider = HazelcastServerCachingProvider.createCachingProvider(survivingInstance);
        CacheManager cacheManager = cachingProvider.getCacheManager();
        CacheConfig<Integer, String> config = new CacheConfig<Integer, String>();
        config.setBackupCount(0);
        Cache<Integer, String> cache = cacheManager.createCache(getIthCacheName(0), config);
        ICache iCache = cache.unwrap(ICache.class);
        final CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener listener = new CachePartitionLostListenerTest.EventCollectingCachePartitionLostListener(0);
        iCache.addPartitionLostListener(listener);
        final Set<Integer> survivingPartitionIds = new HashSet<Integer>();
        Node survivingNode = HazelcastTestSupport.getNode(survivingInstance);
        Address survivingAddress = survivingNode.getThisAddress();
        for (IPartition partition : survivingNode.getPartitionService().getPartitions()) {
            if (survivingAddress.equals(partition.getReplicaAddress(0))) {
                survivingPartitionIds.add(partition.getPartitionId());
            }
        }
        terminatingInstance.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(survivingInstance);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final List<CachePartitionLostEvent> events = listener.getEvents();
                Assert.assertFalse(events.isEmpty());
                for (CachePartitionLostEvent event : events) {
                    Assert.assertFalse(survivingPartitionIds.contains(event.getPartitionId()));
                }
            }
        });
        cacheManager.destroyCache(getIthCacheName(0));
        cacheManager.close();
        cachingProvider.close();
    }

    @Test
    public void test_cachePartitionEventData_serialization() throws IOException {
        CachePartitionEventData cachePartitionEventData = new CachePartitionEventData("cacheName", 1, null);
        ObjectDataOutput output = Mockito.mock(ObjectDataOutput.class);
        cachePartitionEventData.writeData(output);
        Mockito.verify(output).writeUTF("cacheName");
        Mockito.verify(output).writeInt(1);
    }

    @Test
    public void test_cachePartitionEventData_deserialization() throws IOException {
        CachePartitionEventData cachePartitionEventData = new CachePartitionEventData("", 0, null);
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(input.readUTF()).thenReturn("cacheName");
        Mockito.when(input.readInt()).thenReturn(1);
        cachePartitionEventData.readData(input);
        Assert.assertEquals("cacheName", cachePartitionEventData.getName());
        Assert.assertEquals(1, cachePartitionEventData.getPartitionId());
    }

    @Test
    public void testCachePartitionLostEventFilter() {
        CachePartitionLostEventFilter filter = new CachePartitionLostEventFilter();
        Assert.assertEquals(new CachePartitionLostEventFilter(), filter);
        Assert.assertFalse(filter.eval(null));
    }
}

