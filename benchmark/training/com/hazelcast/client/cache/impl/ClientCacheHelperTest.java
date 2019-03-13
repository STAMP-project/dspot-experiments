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
package com.hazelcast.client.cache.impl;


import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.Member;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientCacheHelperTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "fullCacheName";

    private static final String SIMPLE_CACHE_NAME = "cacheName";

    private TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastClientInstanceImpl client;

    private HazelcastClientInstanceImpl exceptionThrowingClient;

    private CacheConfig<String, String> newCacheConfig;

    private CacheConfig<String, String> cacheConfig;

    private ConcurrentMap<String, CacheConfig> configs;

    @Test
    public void testConstructor() {
        assertUtilityConstructor(ClientCacheHelper.class);
    }

    @Test
    public void testGetCacheConfig() {
        CacheConfig<String, String> cacheConfig = ClientCacheHelper.getCacheConfig(client, ClientCacheHelperTest.CACHE_NAME, ClientCacheHelperTest.CACHE_NAME);
        Assert.assertNull(cacheConfig);
    }

    @Test
    public void testGetCacheConfig_withSimpleCacheName() {
        CacheConfig<String, String> cacheConfig = ClientCacheHelper.getCacheConfig(client, ClientCacheHelperTest.SIMPLE_CACHE_NAME, ClientCacheHelperTest.SIMPLE_CACHE_NAME);
        Assert.assertNotNull(cacheConfig);
        Assert.assertEquals(ClientCacheHelperTest.SIMPLE_CACHE_NAME, cacheConfig.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCacheConfig_rethrowsExceptions() {
        ClientCacheHelper.getCacheConfig(exceptionThrowingClient, ClientCacheHelperTest.CACHE_NAME, "simpleCacheName");
    }

    @Test
    public void testCreateCacheConfig_whenSyncCreate_thenReturnNewConfig() {
        CacheConfig<String, String> actualConfig = ClientCacheHelper.createCacheConfig(client, newCacheConfig);
        Assert.assertNotEquals(cacheConfig, actualConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCacheConfig_rethrowsExceptions() {
        ClientCacheHelper.createCacheConfig(exceptionThrowingClient, newCacheConfig);
    }

    @Test
    public void testEnableStatisticManagementOnNodes() {
        ClientCacheHelper.enableStatisticManagementOnNodes(client, ClientCacheHelperTest.CACHE_NAME, false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnableStatisticManagementOnNodes_sneakyThrowsException() {
        Member member = Mockito.mock(Member.class);
        Mockito.when(member.getAddress()).thenThrow(new IllegalArgumentException("expected"));
        Collection<Member> members = Collections.singletonList(member);
        Mockito.when(exceptionThrowingClient.getClientClusterService().getMemberList()).thenReturn(members);
        ClientCacheHelper.enableStatisticManagementOnNodes(exceptionThrowingClient, ClientCacheHelperTest.CACHE_NAME, false, false);
    }
}

