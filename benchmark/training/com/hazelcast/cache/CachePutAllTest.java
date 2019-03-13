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
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceAccessor;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import javax.cache.Cache;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CachePutAllTest extends CacheTestSupport {
    private static final int INSTANCE_COUNT = 2;

    private TestHazelcastInstanceFactory factory = getInstanceFactory(CachePutAllTest.INSTANCE_COUNT);

    private HazelcastInstance[] hazelcastInstances;

    private HazelcastInstance hazelcastInstance;

    @Test
    public void testPutAll() {
        ICache<String, String> cache = createCache();
        String cacheName = cache.getName();
        Map<String, String> entries = createAndFillEntries();
        cache.putAll(entries);
        // Verify that put-all works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            String actualValue = cache.get(key);
            Assert.assertEquals(expectedValue, actualValue);
        }
        Node node = HazelcastTestSupport.getNode(hazelcastInstance);
        InternalPartitionService partitionService = node.getPartitionService();
        SerializationService serializationService = node.getSerializationService();
        // Verify that backup of put-all works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entries.get(key);
            Data keyData = serializationService.toData(key);
            int keyPartitionId = partitionService.getPartitionId(keyData);
            for (int i = 0; i < (CachePutAllTest.INSTANCE_COUNT); i++) {
                Node n = HazelcastTestSupport.getNode(hazelcastInstances[i]);
                ICacheService cacheService = n.getNodeEngine().getService(SERVICE_NAME);
                ICacheRecordStore recordStore = cacheService.getRecordStore(("/hz/" + cacheName), keyPartitionId);
                Assert.assertNotNull(recordStore);
                String actualValue = serializationService.toObject(recordStore.get(keyData, null));
                Assert.assertEquals(expectedValue, actualValue);
            }
        }
    }

    @Test
    public void testPutAllWithExpiration() {
        final long EXPIRATION_TIME_IN_MILLISECONDS = 5000;
        ExpiryPolicy expiryPolicy = new HazelcastExpiryPolicy(EXPIRATION_TIME_IN_MILLISECONDS, 0, 0);
        ICache<String, String> cache = createCache();
        Map<String, String> entries = createAndFillEntries();
        cache.putAll(entries, expiryPolicy);
        HazelcastTestSupport.sleepAtLeastMillis((EXPIRATION_TIME_IN_MILLISECONDS + 1000));
        // Verify that expiration of put-all works
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String actualValue = cache.get(key);
            Assert.assertNull(actualValue);
        }
    }

    @Test
    public void testPutAll_whenEntryExpiresOnCreate() {
        Factory<? extends ExpiryPolicy> expiryPolicyFactory = FactoryBuilder.factoryOf(new javax.cache.expiry.CreatedExpiryPolicy(Duration.ZERO));
        CacheConfig<String, String> cacheConfig = new CacheConfig<String, String>();
        cacheConfig.setTypes(String.class, String.class);
        cacheConfig.setExpiryPolicyFactory(expiryPolicyFactory);
        cacheConfig.setStatisticsEnabled(true);
        cacheConfig.setBackupCount(1);
        Cache<String, String> cache = createCache(cacheConfig);
        String key = HazelcastTestSupport.generateKeyOwnedBy(hazelcastInstance);
        // need to count the number of backup failures on backup member
        OperationService operationService = HazelcastTestSupport.getOperationService(hazelcastInstances[1]);
        MetricsRegistry metricsRegistry = HazelcastTestSupport.getMetricsRegistry(hazelcastInstances[1]);
        Assert.assertEquals(0L, OperationServiceAccessor.getFailedBackupsCount(hazelcastInstances[1]).get());
        Map<String, String> entries = new HashMap<String, String>();
        entries.put(key, HazelcastTestSupport.randomString());
        cache.putAll(entries);
        Assert.assertNull(cache.get(key));
        // force collect metrics
        metricsRegistry.collectMetrics(operationService);
        Assert.assertEquals(0L, OperationServiceAccessor.getFailedBackupsCount(hazelcastInstances[1]).get());
    }
}

