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


import com.hazelcast.cache.ICache;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings("WeakerAccess")
public class ClientCacheMetaDataGeneratorTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "CacheMetaDataGeneratorTest";

    private static final String PREFIXED_CACHE_NAME = "/hz/" + (ClientCacheMetaDataGeneratorTest.CACHE_NAME);

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private ClientConfig clientConfig;

    private CacheConfig<Integer, Integer> cacheConfig;

    private ICache<Integer, Integer> serverCache;

    private HazelcastInstance server;

    @Test
    public void destroying_cache_removes_related_metadata_when_near_cache_exists() {
        ICache<Integer, Integer> clientCache = createCacheFromNewClient();
        serverCache.put(1, 1);
        Assert.assertNotNull(ClientCacheMetaDataGeneratorTest.getMetaDataGenerator(server).getSequenceGenerators().get(ClientCacheMetaDataGeneratorTest.PREFIXED_CACHE_NAME));
        clientCache.destroy();
        Assert.assertNull(ClientCacheMetaDataGeneratorTest.getMetaDataGenerator(server).getSequenceGenerators().get(ClientCacheMetaDataGeneratorTest.PREFIXED_CACHE_NAME));
    }

    @Test
    public void destroying_cache_removes_related_metadata_when_near_cache_not_exists() {
        serverCache.put(1, 1);
        Assert.assertNull(ClientCacheMetaDataGeneratorTest.getMetaDataGenerator(server).getSequenceGenerators().get(ClientCacheMetaDataGeneratorTest.PREFIXED_CACHE_NAME));
        serverCache.destroy();
        Assert.assertNull(ClientCacheMetaDataGeneratorTest.getMetaDataGenerator(server).getSequenceGenerators().get(ClientCacheMetaDataGeneratorTest.PREFIXED_CACHE_NAME));
    }
}

