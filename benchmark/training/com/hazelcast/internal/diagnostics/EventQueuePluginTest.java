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
package com.hazelcast.internal.diagnostics;


import MapService.SERVICE_NAME;
import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.ICache;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.collection.impl.collection.CollectionEvent;
import com.hazelcast.collection.impl.queue.QueueEvent;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.core.ItemListener;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ItemCounter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.spi.CachingProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EventQueuePluginTest extends AbstractDiagnosticsPluginTest {
    private static final int EVENT_COUNTER = 1000;

    private static final String MAP_NAME = "myMap";

    private static final String CACHE_NAME = "myCache";

    private static final String QUEUE_NAME = "myQueue";

    private static final String LIST_NAME = "myList";

    private static final String SET_NAME = "mySet";

    private final CountDownLatch listenerLatch = new CountDownLatch(1);

    private HazelcastInstance hz;

    private EventQueuePlugin plugin;

    private ItemCounter<String> itemCounter;

    @Test
    public void testMap() {
        final IMap<Integer, Integer> map = hz.getMap(EventQueuePluginTest.MAP_NAME);
        map.addLocalEntryListener(new com.hazelcast.map.listener.EntryAddedListener<Integer, Integer>() {
            @Override
            public void entryAdded(EntryEvent<Integer, Integer> event) {
                HazelcastTestSupport.assertOpenEventually(listenerLatch);
            }
        });
        map.addLocalEntryListener(new EntryRemovedListener() {
            @Override
            public void entryRemoved(EntryEvent event) {
                HazelcastTestSupport.assertOpenEventually(listenerLatch);
            }
        });
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (int i = 0; i < (EventQueuePluginTest.EVENT_COUNTER); i++) {
                    int key = random.nextInt(Integer.MAX_VALUE);
                    map.putAsync(key, 23);
                    map.removeAsync(key);
                }
            }
        });
        assertContainsEventually((("IMap '" + (EventQueuePluginTest.MAP_NAME)) + "' ADDED sampleCount="), (("IMap '" + (EventQueuePluginTest.MAP_NAME)) + "' REMOVED sampleCount="));
    }

    @Test
    public void testCache() {
        CompleteConfiguration<Integer, Integer> cacheConfig = new javax.cache.configuration.MutableConfiguration<Integer, Integer>().addCacheEntryListenerConfiguration(new javax.cache.configuration.MutableCacheEntryListenerConfiguration<Integer, Integer>(FactoryBuilder.factoryOf(new EventQueuePluginTest.TestCacheListener()), null, true, true));
        CachingProvider memberProvider = HazelcastServerCachingProvider.createCachingProvider(hz);
        HazelcastServerCacheManager memberCacheManager = ((HazelcastServerCacheManager) (memberProvider.getCacheManager()));
        final ICache<Integer, Integer> cache = memberCacheManager.createCache(EventQueuePluginTest.CACHE_NAME, cacheConfig);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (int i = 0; i < (EventQueuePluginTest.EVENT_COUNTER); i++) {
                    int key = random.nextInt(Integer.MAX_VALUE);
                    cache.putAsync(key, 23);
                    cache.removeAsync(key);
                }
            }
        });
        assertContainsEventually((("ICache '/hz/" + (EventQueuePluginTest.CACHE_NAME)) + "' CREATED sampleCount="), (("ICache '/hz/" + (EventQueuePluginTest.CACHE_NAME)) + "' REMOVED sampleCount="));
    }

    @Test
    public void testQueue() {
        final IQueue<Integer> queue = hz.getQueue(EventQueuePluginTest.QUEUE_NAME);
        queue.addItemListener(new EventQueuePluginTest.TestItemListener(), true);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (int i = 0; i < (EventQueuePluginTest.EVENT_COUNTER); i++) {
                    int key = random.nextInt(Integer.MAX_VALUE);
                    queue.add(key);
                    queue.poll();
                }
            }
        });
        assertContainsEventually((("IQueue '" + (EventQueuePluginTest.QUEUE_NAME)) + "' ADDED sampleCount="), (("IQueue '" + (EventQueuePluginTest.QUEUE_NAME)) + "' REMOVED sampleCount="));
    }

    @Test
    public void testList() {
        final IList<Integer> list = hz.getList(EventQueuePluginTest.LIST_NAME);
        list.addItemListener(new EventQueuePluginTest.TestItemListener(), true);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (int i = 0; i < (EventQueuePluginTest.EVENT_COUNTER); i++) {
                    int key = random.nextInt(EventQueuePluginTest.EVENT_COUNTER);
                    list.add(key);
                }
            }
        });
        assertContainsEventually((("IList '" + (EventQueuePluginTest.LIST_NAME)) + "' ADDED sampleCount="));
    }

    @Test
    public void testSet() {
        final ISet<Integer> set = hz.getSet(EventQueuePluginTest.SET_NAME);
        set.addItemListener(new EventQueuePluginTest.TestItemListener(), true);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                for (int i = 0; i < (EventQueuePluginTest.EVENT_COUNTER); i++) {
                    int key = random.nextInt(Integer.MAX_VALUE);
                    set.add(key);
                    set.remove(key);
                }
            }
        });
        assertContainsEventually((("ISet '" + (EventQueuePluginTest.SET_NAME)) + "' ADDED sampleCount="), (("ISet '" + (EventQueuePluginTest.SET_NAME)) + "' REMOVED sampleCount="));
    }

    @Test
    public void testSampleRunnable() {
        Address caller = new Address();
        Data data = Mockito.mock(Data.class);
        EntryEventData mapEventAdded = new EntryEventData("source", "mapName", caller, data, data, data, EntryEventType.ADDED.getType());
        EntryEventData mapEventUpdated = new EntryEventData("source", "mapName", caller, data, data, data, EntryEventType.UPDATED.getType());
        EntryEventData mapEventRemoved = new EntryEventData("source", "mapName", caller, data, data, data, EntryEventType.REMOVED.getType());
        assertSampleRunnable("IMap 'mapName' ADDED", mapEventAdded, SERVICE_NAME);
        assertSampleRunnable("IMap 'mapName' UPDATED", mapEventUpdated, SERVICE_NAME);
        assertSampleRunnable("IMap 'mapName' REMOVED", mapEventRemoved, SERVICE_NAME);
        CacheEventData cacheEventCreated = new com.hazelcast.cache.impl.CacheEventDataImpl("cacheName", CacheEventType.CREATED, data, data, data, true);
        CacheEventData cacheEventUpdated = new com.hazelcast.cache.impl.CacheEventDataImpl("cacheName", CacheEventType.UPDATED, data, data, data, true);
        CacheEventData cacheEventRemoved = new com.hazelcast.cache.impl.CacheEventDataImpl("cacheName", CacheEventType.REMOVED, data, data, data, true);
        CacheEventSet CacheEventSetCreated = new CacheEventSet(CacheEventType.CREATED, Collections.singleton(cacheEventCreated), 1);
        CacheEventSet CacheEventSetUpdated = new CacheEventSet(CacheEventType.UPDATED, Collections.singleton(cacheEventUpdated), 1);
        CacheEventSet cacheEventSetRemoved = new CacheEventSet(CacheEventType.REMOVED, Collections.singleton(cacheEventRemoved), 1);
        assertSampleRunnable("ICache 'cacheName' CREATED", CacheEventSetCreated, CacheService.SERVICE_NAME);
        assertSampleRunnable("ICache 'cacheName' UPDATED", CacheEventSetUpdated, CacheService.SERVICE_NAME);
        assertSampleRunnable("ICache 'cacheName' REMOVED", cacheEventSetRemoved, CacheService.SERVICE_NAME);
        List<CacheEventData> cacheEventData = Arrays.asList(cacheEventCreated, cacheEventUpdated, cacheEventRemoved);
        Set<CacheEventData> cacheEvents = new HashSet<CacheEventData>(cacheEventData);
        CacheEventSet cacheEventSetAll = new CacheEventSet(CacheEventType.EXPIRED, cacheEvents, 1);
        assertCacheEventSet(cacheEventSetAll, "ICache 'cacheName' CREATED", "ICache 'cacheName' UPDATED", "ICache 'cacheName' REMOVED");
        QueueEvent queueEventAdded = new QueueEvent("queueName", data, ItemEventType.ADDED, caller);
        QueueEvent queueEventRemoved = new QueueEvent("queueName", data, ItemEventType.REMOVED, caller);
        assertSampleRunnable("IQueue 'queueName' ADDED", queueEventAdded, QueueService.SERVICE_NAME);
        assertSampleRunnable("IQueue 'queueName' REMOVED", queueEventRemoved, QueueService.SERVICE_NAME);
        CollectionEvent setEventAdded = new CollectionEvent("setName", data, ItemEventType.ADDED, caller);
        CollectionEvent setEventRemoved = new CollectionEvent("setName", data, ItemEventType.REMOVED, caller);
        assertSampleRunnable("ISet 'setName' ADDED", setEventAdded, SetService.SERVICE_NAME);
        assertSampleRunnable("ISet 'setName' REMOVED", setEventRemoved, SetService.SERVICE_NAME);
        CollectionEvent listEventAdded = new CollectionEvent("listName", data, ItemEventType.ADDED, caller);
        CollectionEvent listEventRemoved = new CollectionEvent("listName", data, ItemEventType.REMOVED, caller);
        assertSampleRunnable("IList 'listName' ADDED", listEventAdded, ListService.SERVICE_NAME);
        assertSampleRunnable("IList 'listName' REMOVED", listEventRemoved, ListService.SERVICE_NAME);
        assertSampleRunnable("Object", new Object(), AtomicLongService.SERVICE_NAME);
        assertSampleRunnable(new EventQueuePluginTest.TestEvent(), EventQueuePluginTest.TestEvent.class.getName());
    }

    private final class TestCacheListener implements Serializable , CacheEntryCreatedListener<Integer, Integer> , CacheEntryRemovedListener<Integer, Integer> {
        public TestCacheListener() {
        }

        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends Integer, ? extends Integer>> cacheEntryEvents) throws CacheEntryListenerException {
            HazelcastTestSupport.assertOpenEventually(listenerLatch);
        }

        @Override
        public void onRemoved(Iterable<CacheEntryEvent<? extends Integer, ? extends Integer>> cacheEntryEvents) throws CacheEntryListenerException {
            HazelcastTestSupport.assertOpenEventually(listenerLatch);
        }
    }

    private final class TestItemListener implements ItemListener<Integer> {
        @Override
        public void itemAdded(ItemEvent<Integer> item) {
            HazelcastTestSupport.assertOpenEventually(listenerLatch);
        }

        @Override
        public void itemRemoved(ItemEvent<Integer> item) {
            HazelcastTestSupport.assertOpenEventually(listenerLatch);
        }
    }

    private static class TestEvent implements Runnable {
        @Override
        public void run() {
        }
    }
}

