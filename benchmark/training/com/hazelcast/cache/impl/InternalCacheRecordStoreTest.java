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
package com.hazelcast.cache.impl;


import ICacheService.SERVICE_NAME;
import com.hazelcast.cache.CacheFromDifferentNodesTest;
import com.hazelcast.cache.CacheTestSupport;
import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static ICacheService.SERVICE_NAME;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InternalCacheRecordStoreTest extends CacheTestSupport {
    private TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);

    /**
     * Test for issue: https://github.com/hazelcast/hazelcast/issues/6618
     */
    @Test
    public void batchEventMapShouldBeCleanedAfterRemoveAll() {
        String cacheName = HazelcastTestSupport.randomString();
        CacheConfig<Integer, String> config = createCacheConfig();
        CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String> listener = new CacheFromDifferentNodesTest.SimpleEntryListener<Integer, String>();
        MutableCacheEntryListenerConfiguration<Integer, String> listenerConfiguration = new MutableCacheEntryListenerConfiguration<Integer, String>(FactoryBuilder.factoryOf(listener), null, true, true);
        config.addCacheEntryListenerConfiguration(listenerConfiguration);
        Cache<Integer, String> cache = cacheManager.createCache(cacheName, config);
        Assert.assertNotNull(cache);
        Integer key = 1;
        String value = "value";
        cache.put(key, value);
        HazelcastInstance instance = ((HazelcastCacheManager) (cacheManager)).getHazelcastInstance();
        int partitionId = instance.getPartitionService().getPartition(key).getPartitionId();
        cache.removeAll();
        Node node = HazelcastTestSupport.getNode(instance);
        Assert.assertNotNull(node);
        ICacheService cacheService = getService(SERVICE_NAME);
        AbstractCacheRecordStore recordStore = ((AbstractCacheRecordStore) (cacheService.getRecordStore(("/hz/" + cacheName), partitionId)));
        Assert.assertEquals(0, recordStore.batchEvent.size());
    }

    /**
     * Test for issue: https://github.com/hazelcast/hazelcast/issues/6983
     */
    @Test
    public void ownerStateShouldBeUpdatedAfterMigration() throws Exception {
        HazelcastCacheManager hzCacheManager = ((HazelcastCacheManager) (cacheManager));
        HazelcastInstance instance1 = hzCacheManager.getHazelcastInstance();
        Node node1 = HazelcastTestSupport.getNode(instance1);
        NodeEngineImpl nodeEngine1 = node1.getNodeEngine();
        InternalPartitionService partitionService1 = nodeEngine1.getPartitionService();
        int partitionCount = partitionService1.getPartitionCount();
        String cacheName = HazelcastTestSupport.randomName();
        CacheConfig cacheConfig = new CacheConfig().setName(cacheName);
        Cache<String, String> cache = cacheManager.createCache(cacheName, cacheConfig);
        String fullCacheName = hzCacheManager.getCacheNameWithPrefix(cacheName);
        for (int i = 0; i < partitionCount; i++) {
            String key = HazelcastTestSupport.generateKeyForPartition(instance1, i);
            cache.put(key, "Value");
        }
        for (int i = 0; i < partitionCount; i++) {
            verifyPrimaryState(node1, fullCacheName, i, true);
        }
        HazelcastInstance instance2 = getHazelcastInstance();
        Node node2 = HazelcastTestSupport.getNode(instance2);
        HazelcastTestSupport.warmUpPartitions(instance1, instance2);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2);
        for (int i = 0; i < partitionCount; i++) {
            boolean ownedByNode1 = partitionService1.isPartitionOwner(i);
            if (ownedByNode1) {
                verifyPrimaryState(node1, fullCacheName, i, true);
                verifyPrimaryState(node2, fullCacheName, i, false);
            } else {
                verifyPrimaryState(node1, fullCacheName, i, false);
                verifyPrimaryState(node2, fullCacheName, i, true);
            }
        }
    }

    static class CachePrimaryStateGetterOperation extends AbstractNamedOperation {
        private boolean isPrimary;

        private CachePrimaryStateGetterOperation() {
        }

        private CachePrimaryStateGetterOperation(String cacheName) {
            super(cacheName);
        }

        @Override
        public void run() throws Exception {
            ICacheService cacheService = getService();
            AbstractCacheRecordStore cacheRecordStore = ((AbstractCacheRecordStore) (cacheService.getRecordStore(name, HazelcastTestSupport.getPartitionId())));
            isPrimary = cacheRecordStore.primary;
        }

        @Override
        public Object getResponse() {
            return isPrimary;
        }

        @Override
        public String getServiceName() {
            return SERVICE_NAME;
        }

        @Override
        public int getFactoryId() {
            return InternalCacheRecordStoreTest.InternalCacheRecordStoreTestFactory.F_ID;
        }

        @Override
        public int getId() {
            return InternalCacheRecordStoreTest.InternalCacheRecordStoreTestFactory.INTERNAL_CACHE_PRIMARY_STATE_GETTER;
        }
    }

    static class InternalCacheRecordStoreTestFactory implements DataSerializableFactory {
        static final int F_ID = 1;

        static final int INTERNAL_CACHE_PRIMARY_STATE_GETTER = 0;

        @Override
        public IdentifiedDataSerializable create(int typeId) {
            if (typeId == (InternalCacheRecordStoreTest.InternalCacheRecordStoreTestFactory.INTERNAL_CACHE_PRIMARY_STATE_GETTER)) {
                return new InternalCacheRecordStoreTest.CachePrimaryStateGetterOperation();
            } else {
                throw new UnsupportedOperationException(("Could not create instance of type ID " + typeId));
            }
        }
    }
}

