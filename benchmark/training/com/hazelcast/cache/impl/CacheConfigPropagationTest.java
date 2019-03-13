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


import com.hazelcast.cache.CacheUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import javax.cache.CacheManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


// asserts contents of AbstractCacheService.configs
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheConfigPropagationTest extends HazelcastTestSupport {
    private static final String DYNAMIC_CACHE_NAME = "dynamic-cache";

    private static final String DECLARED_CACHE_NAME = "declared-cache-1";

    protected int clusterSize = 2;

    protected HazelcastInstance driver;

    protected TestHazelcastInstanceFactory factory;

    private CacheManager cacheManagerDriver;

    private HazelcastInstance[] members;

    @Test
    public void noPreJoinCacheConfig_whenCacheCreatedDynamically_viaCacheManager() {
        cacheManagerDriver.createCache(CacheConfigPropagationTest.DYNAMIC_CACHE_NAME, new com.hazelcast.config.CacheConfig<String, String>());
        CacheService cacheService = getCacheService(members[0]);
        assertNoPreJoinCacheConfig(cacheService);
    }

    @Test
    public void noPreJoinCacheConfig_whenCacheGet_viaCacheManager() {
        cacheManagerDriver.getCache(CacheConfigPropagationTest.DECLARED_CACHE_NAME);
        CacheService cacheService = getCacheService(members[0]);
        assertNoPreJoinCacheConfig(cacheService);
        Assert.assertNotNull("Cache config 'declared-cache-1' should exist in registered cache configs", cacheService.getCacheConfig(CacheUtil.getDistributedObjectName(CacheConfigPropagationTest.DECLARED_CACHE_NAME, null, null)));
    }

    @Test
    public void noPreJoinCacheConfig_whenCacheGet_viaHazelcastInstance() {
        driver.getCacheManager().getCache(CacheConfigPropagationTest.DECLARED_CACHE_NAME);
        CacheService cacheService = getCacheService(members[0]);
        assertNoPreJoinCacheConfig(cacheService);
    }

    @Test
    public void noPreJoinCacheConfig_onNewMember() {
        cacheManagerDriver.createCache(CacheConfigPropagationTest.DYNAMIC_CACHE_NAME, new com.hazelcast.config.CacheConfig<String, String>());
        HazelcastInstance newMember = factory.newHazelcastInstance(getConfig());
        CacheService cacheService = getCacheService(newMember);
        assertNoPreJoinCacheConfig(cacheService);
    }

    @Test
    public void cacheConfig_existsOnRemoteMember_immediatelyAfterCacheGet() {
        CacheService cacheServiceOnRemote = getCacheService(members[1]);
        String distributedObjectName = CacheUtil.getDistributedObjectName(CacheConfigPropagationTest.DECLARED_CACHE_NAME);
        Assert.assertNull(cacheServiceOnRemote.getCacheConfig(distributedObjectName));
        driver.getCacheManager().getCache(CacheConfigPropagationTest.DECLARED_CACHE_NAME);
        Assert.assertNotNull(cacheServiceOnRemote.getCacheConfig(distributedObjectName));
    }
}

