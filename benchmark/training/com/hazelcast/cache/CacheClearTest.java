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


import ICacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheClearTest extends CacheTestSupport {
    private static final int INSTANCE_COUNT = 2;

    private TestHazelcastInstanceFactory factory = getInstanceFactory(CacheClearTest.INSTANCE_COUNT);

    private HazelcastInstance[] hazelcastInstances;

    private HazelcastInstance hazelcastInstance;

    @Test
    public void testClear() {
        ICache<String, String> cache = createCache();
        String cacheName = cache.getName();
        Map<String, String> entries = createAndFillEntries();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
        // Verify that put works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        InternalPartitionService partitionService = node.getPartitionService();
        SerializationService serializationService = node.getSerializationService();
        // Verify that backup of put works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            Data keyData = serializationService.toData(key);
            int keyPartitionId = partitionService.getPartitionId(keyData);
            for (int i = 0; i < (CacheClearTest.INSTANCE_COUNT); i++) {
                Node n = HazelcastTestSupport.getNode(hazelcastInstances[i]);
                ICacheService cacheService = n.getNodeEngine().getService(SERVICE_NAME);
                ICacheRecordStore recordStore = cacheService.getRecordStore(("/hz/" + cacheName), keyPartitionId);
                Assert.assertNotNull(recordStore);
                String actualValue = serializationService.toObject(recordStore.get(keyData, null));
                Assert.assertEquals(expectedValue, actualValue);
            }
        }
        cache.clear();
        // Verify that clear works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String actualValue = cache.get(key);
            Assert.assertNull(actualValue);
        }
        // Verify that backup of clear works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            Data keyData = serializationService.toData(key);
            int keyPartitionId = partitionService.getPartitionId(keyData);
            for (int i = 0; i < (CacheClearTest.INSTANCE_COUNT); i++) {
                Node n = HazelcastTestSupport.getNode(hazelcastInstances[i]);
                ICacheService cacheService = n.getNodeEngine().getService(SERVICE_NAME);
                ICacheRecordStore recordStore = cacheService.getRecordStore(("/hz/" + cacheName), keyPartitionId);
                Assert.assertNotNull(recordStore);
                String actualValue = serializationService.toObject(recordStore.get(keyData, null));
                Assert.assertNull(actualValue);
            }
        }
    }

    @Test
    public void testInvalidationListenerCallCount() {
        final ICache<String, String> cache = createCache();
        Map<String, String> entries = createAndFillEntries();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
        // Verify that put works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        final AtomicInteger counter = new AtomicInteger(0);
        final CacheConfig config = cache.getConfiguration(CacheConfig.class);
        registerInvalidationListener(new CacheEventListener() {
            @Override
            public void handleEvent(Object eventObject) {
                if (eventObject instanceof Invalidation) {
                    Invalidation event = ((Invalidation) (eventObject));
                    if ((null == (event.getKey())) && (config.getNameWithPrefix().equals(event.getName()))) {
                        counter.incrementAndGet();
                    }
                }
            }
        }, config.getNameWithPrefix());
        cache.clear();
        // Make sure that one event is received
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, counter.get());
            }
        }, 5);
        // Make sure that the callback is not called for a while
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(((counter.get()) <= 1));
            }
        }, 3);
    }
}

