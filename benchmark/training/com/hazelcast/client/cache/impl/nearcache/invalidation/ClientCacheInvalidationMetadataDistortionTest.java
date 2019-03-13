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


import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.client.cache.impl.HazelcastClientCachingProvider;
import com.hazelcast.client.cache.impl.nearcache.ClientNearCacheTestSupport;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class ClientCacheInvalidationMetadataDistortionTest extends ClientNearCacheTestSupport {
    private static final int CACHE_SIZE = 100000;

    private final AtomicBoolean stopTest = new AtomicBoolean();

    @Test
    public void ensure_nearCachedClient_and_member_data_sync_eventually() {
        final Config config = createConfig();
        final HazelcastInstance member = hazelcastFactory.newHazelcastInstance(config);
        CachingProvider provider = HazelcastServerCachingProvider.createCachingProvider(serverInstance);
        CacheManager serverCacheManager = provider.getCacheManager();
        // populated from member
        final Cache<Integer, Integer> memberCache = serverCacheManager.createCache(ClientNearCacheTestSupport.DEFAULT_CACHE_NAME, createCacheConfig(InMemoryFormat.BINARY));
        for (int i = 0; i < (ClientCacheInvalidationMetadataDistortionTest.CACHE_SIZE); i++) {
            memberCache.put(i, i);
        }
        ClientConfig clientConfig = createClientConfig();
        clientConfig.addNearCacheConfig(createNearCacheConfig(InMemoryFormat.BINARY));
        HazelcastClientProxy client = ((HazelcastClientProxy) (hazelcastFactory.newHazelcastClient(clientConfig)));
        CachingProvider clientCachingProvider = HazelcastClientCachingProvider.createCachingProvider(client);
        final Cache<Integer, Integer> clientCache = clientCachingProvider.getCacheManager().createCache(ClientNearCacheTestSupport.DEFAULT_CACHE_NAME, createCacheConfig(InMemoryFormat.BINARY));
        Thread populateNearCache = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    for (int i = 0; i < (ClientCacheInvalidationMetadataDistortionTest.CACHE_SIZE); i++) {
                        clientCache.get(i);
                    }
                } 
            }
        });
        Thread distortSequence = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionSequence(ClientNearCacheTestSupport.DEFAULT_CACHE_NAME, member);
                    sleepSeconds(1);
                } 
            }
        });
        Thread distortUuid = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionUuid(member);
                    sleepSeconds(5);
                } 
            }
        });
        Thread put = new Thread(new Runnable() {
            public void run() {
                // change some data
                while (!(stopTest.get())) {
                    int key = getInt(ClientCacheInvalidationMetadataDistortionTest.CACHE_SIZE);
                    int value = getInt(Integer.MAX_VALUE);
                    memberCache.put(key, value);
                    sleepAtLeastMillis(100);
                } 
            }
        });
        // start threads
        put.start();
        populateNearCache.start();
        distortSequence.start();
        distortUuid.start();
        sleepSeconds(60);
        // stop threads
        stopTest.set(true);
        assertJoinable(distortUuid, distortSequence, populateNearCache, put);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (ClientCacheInvalidationMetadataDistortionTest.CACHE_SIZE); i++) {
                    Integer valueSeenFromMember = memberCache.get(i);
                    Integer valueSeenFromClient = clientCache.get(i);
                    Assert.assertEquals(valueSeenFromMember, valueSeenFromClient);
                }
            }
        });
    }
}

