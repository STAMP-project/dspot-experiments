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
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CompletionListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheLoadAllTest extends CacheTestSupport {
    private static final int INSTANCE_COUNT = 2;

    private TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(CacheLoadAllTest.INSTANCE_COUNT);

    private HazelcastInstance[] hazelcastInstances;

    private HazelcastInstance hazelcastInstance;

    @Test
    public void testLoadAll() throws InterruptedException {
        ICache<String, String> cache = createCache();
        String cacheName = cache.getName();
        Map<String, String> entries = createAndFillEntries();
        final CountDownLatch latch = new CountDownLatch(1);
        cache.loadAll(entries.keySet(), true, new CompletionListener() {
            @Override
            public void onCompletion() {
                latch.countDown();
            }

            @Override
            public void onException(Exception e) {
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        // Verify that load-all works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        InternalPartitionService partitionService = node.getPartitionService();
        SerializationService serializationService = node.getSerializationService();
        // Verify that backup of load-all works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            Data keyData = serializationService.toData(key);
            int keyPartitionId = partitionService.getPartitionId(keyData);
            for (int i = 0; i < (CacheLoadAllTest.INSTANCE_COUNT); i++) {
                Node n = HazelcastTestSupport.getNode(hazelcastInstances[i]);
                ICacheService cacheService = n.getNodeEngine().getService(SERVICE_NAME);
                ICacheRecordStore recordStore = cacheService.getRecordStore(("/hz/" + cacheName), keyPartitionId);
                Assert.assertNotNull(recordStore);
                String actualValue = serializationService.toObject(recordStore.get(keyData, null));
                Assert.assertEquals(expectedValue, actualValue);
            }
        }
    }

    public static class TestCacheLoader implements CacheLoader<String, String> {
        @Override
        public String load(String key) throws CacheLoaderException {
            return CacheLoadAllTest.getValueOfKey(key);
        }

        @Override
        public Map<String, String> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
            Map<String, String> entries = new HashMap<String, String>();
            for (String key : keys) {
                entries.put(key, CacheLoadAllTest.getValueOfKey(key));
            }
            return entries;
        }
    }
}

