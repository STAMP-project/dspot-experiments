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
package com.hazelcast.client.cache.impl.nearcache.invalidation;


import CacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.CacheEventHandler;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.client.cache.impl.HazelcastClientCachingProvider;
import com.hazelcast.client.cache.impl.nearcache.ClientNearCacheTestSupport;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig.LocalUpdatePolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.NearCacheRecordStore;
import com.hazelcast.internal.nearcache.impl.DefaultNearCache;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.internal.nearcache.impl.invalidation.StaleReadDetector;
import com.hazelcast.internal.nearcache.impl.store.AbstractNearCacheRecordStore;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category(NightlyTest.class)
public class ClientCacheInvalidationMemberAddRemoveTest extends ClientNearCacheTestSupport {
    private static final int TEST_RUN_SECONDS = 30;

    private static final int KEY_COUNT = 1000;

    private static final int INVALIDATION_BATCH_SIZE = 100;

    private static final int RECONCILIATION_INTERVAL_SECS = 30;

    private static final int NEAR_CACHE_POPULATE_THREAD_COUNT = 5;

    private HazelcastInstance secondNode;

    @Parameterized.Parameter
    public LocalUpdatePolicy localUpdatePolicy;

    @Test
    public void ensure_nearCachedClient_and_member_data_sync_eventually() {
        final AtomicBoolean stopTest = new AtomicBoolean();
        final Config config = createConfig();
        secondNode = hazelcastFactory.newHazelcastInstance(config);
        CachingProvider provider = HazelcastServerCachingProvider.createCachingProvider(serverInstance);
        final CacheManager serverCacheManager = provider.getCacheManager();
        // populated from member
        final Cache<Integer, Integer> memberCache = serverCacheManager.createCache(ClientNearCacheTestSupport.DEFAULT_CACHE_NAME, createCacheConfig(InMemoryFormat.BINARY));
        for (int i = 0; i < (ClientCacheInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
            memberCache.put(i, i);
        }
        ClientConfig clientConfig = createClientConfig().addNearCacheConfig(createNearCacheConfig(InMemoryFormat.BINARY));
        HazelcastClientProxy client = ((HazelcastClientProxy) (hazelcastFactory.newHazelcastClient(clientConfig)));
        CachingProvider clientCachingProvider = HazelcastClientCachingProvider.createCachingProvider(client);
        final Cache<Integer, Integer> clientCache = clientCachingProvider.getCacheManager().createCache(ClientNearCacheTestSupport.DEFAULT_CACHE_NAME, createCacheConfig(InMemoryFormat.BINARY));
        ArrayList<Thread> threads = new ArrayList<Thread>();
        // continuously adds and removes member
        Thread shadowMember = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    HazelcastInstance member = hazelcastFactory.newHazelcastInstance(config);
                    sleepSeconds(5);
                    member.getLifecycleService().terminate();
                } 
            }
        });
        threads.add(shadowMember);
        for (int i = 0; i < (ClientCacheInvalidationMemberAddRemoveTest.NEAR_CACHE_POPULATE_THREAD_COUNT); i++) {
            // populates client Near Cache
            Thread populateClientNearCache = new Thread(new Runnable() {
                public void run() {
                    while (!(stopTest.get())) {
                        for (int i = 0; i < (ClientCacheInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
                            clientCache.get(i);
                        }
                    } 
                }
            });
            threads.add(populateClientNearCache);
        }
        // updates data from member
        Thread putFromMember = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    int key = getInt(ClientCacheInvalidationMemberAddRemoveTest.KEY_COUNT);
                    int value = getInt(Integer.MAX_VALUE);
                    memberCache.put(key, value);
                    sleepAtLeastMillis(2);
                } 
            }
        });
        threads.add(putFromMember);
        Thread clearFromMember = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    memberCache.clear();
                    sleepSeconds(3);
                } 
            }
        });
        threads.add(clearFromMember);
        // start threads
        for (Thread thread : threads) {
            thread.start();
        }
        // stress system some seconds
        sleepSeconds(ClientCacheInvalidationMemberAddRemoveTest.TEST_RUN_SECONDS);
        // stop threads
        stopTest.set(true);
        for (Thread thread : threads) {
            assertJoinable(thread);
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (ClientCacheInvalidationMemberAddRemoveTest.KEY_COUNT); i++) {
                    Integer valueSeenFromMember = memberCache.get(i);
                    Integer valueSeenFromClient = clientCache.get(i);
                    String msg = createFailureMessage(i);
                    Assert.assertEquals(msg, valueSeenFromMember, valueSeenFromClient);
                }
            }

            @SuppressWarnings("unchecked")
            private String createFailureMessage(int i) {
                int partitionId = getPartitionService(serverInstance).getPartitionId(i);
                NearCacheRecordStore nearCacheRecordStore = getNearCacheRecordStore();
                NearCacheRecord record = nearCacheRecordStore.getRecord(i);
                long recordSequence = (record == null) ? NO_SEQUENCE : record.getInvalidationSequence();
                // member-1
                MetaDataGenerator metaDataGenerator1 = getMetaDataGenerator(serverInstance);
                long memberSequence1 = metaDataGenerator1.currentSequence(("/hz/" + (ClientNearCacheTestSupport.DEFAULT_CACHE_NAME)), partitionId);
                UUID memberUuid1 = metaDataGenerator1.getUuidOrNull(partitionId);
                // member-2
                MetaDataGenerator metaDataGenerator2 = getMetaDataGenerator(secondNode);
                long memberSequence2 = metaDataGenerator2.currentSequence(("/hz/" + (ClientNearCacheTestSupport.DEFAULT_CACHE_NAME)), partitionId);
                UUID memberUuid2 = metaDataGenerator2.getUuidOrNull(partitionId);
                StaleReadDetector staleReadDetector = getStaleReadDetector(((AbstractNearCacheRecordStore) (nearCacheRecordStore)));
                MetaDataContainer metaDataContainer = staleReadDetector.getMetaDataContainer(partitionId);
                return String.format(("On client: [uuid=%s, partition=%d, onRecordSequence=%d, latestSequence=%d, staleSequence=%d]," + "%nOn members: [memberUuid1=%s, memberSequence1=%d, memberUuid2=%s, memberSequence2=%d]"), metaDataContainer.getUuid(), partitionId, recordSequence, metaDataContainer.getSequence(), metaDataContainer.getStaleSequence(), memberUuid1, memberSequence1, memberUuid2, memberSequence2);
            }

            private MetaDataGenerator getMetaDataGenerator(HazelcastInstance node) {
                CacheService service = getNodeEngineImpl(node).getService(SERVICE_NAME);
                CacheEventHandler cacheEventHandler = service.getCacheEventHandler();
                return cacheEventHandler.getMetaDataGenerator();
            }

            private NearCacheRecordStore getNearCacheRecordStore() {
                NearCache nearCache = getNearCache();
                DefaultNearCache defaultNearCache = ((DefaultNearCache) (nearCache.unwrap(DefaultNearCache.class)));
                return defaultNearCache.getNearCacheRecordStore();
            }
        });
    }
}

