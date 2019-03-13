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
package com.hazelcast.client.map.impl.querycache;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientQueryCacheCreateDestroyTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void create_after_destroy_gives_fresh_query_cache_instance() {
        final String mapName = "someMap";
        final String queryCacheName = "testCache";
        HazelcastInstance server = newHazelcastInstance();
        HazelcastInstance client = factory.newHazelcastClient(ClientQueryCacheCreateDestroyTest.newClientConfigWithQueryCache(mapName, queryCacheName));
        // create client-side query-cache
        IMap clientMap = client.getMap(mapName);
        QueryCache clientQueryCache = clientMap.getQueryCache(queryCacheName);
        clientQueryCache.destroy();
        QueryCache newQueryCache = clientMap.getQueryCache(queryCacheName);
        Assert.assertFalse((clientQueryCache == newQueryCache));
    }

    @Test
    public void recreated_queryCache_gets_updates_from_restarted_server() {
        final String mapName = "someMap";
        final String queryCacheName = "testCache";
        // start server
        HazelcastInstance server = newHazelcastInstance();
        server.getMap(mapName);
        // start client with query-cache config
        HazelcastInstance client = factory.newHazelcastClient(ClientQueryCacheCreateDestroyTest.newClientConfigWithQueryCache(mapName, queryCacheName));
        // create client-side query-cache
        IMap clientMap = client.getMap(mapName);
        QueryCache clientQueryCache = clientMap.getQueryCache(queryCacheName);
        // shutdown all members, at this point only client is alive
        factory.shutdownAllMembers();
        // start new server to emulate server re-start
        HazelcastInstance newServer = newHazelcastInstance();
        IMap newServerMap = newServer.getMap(mapName);
        // populate new map on server
        for (int i = 0; i < 1000; i++) {
            newServerMap.put(i, i);
        }
        // destroy client query-cache from client-side to re-create it,
        // otherwise it stays tied with previous server
        clientQueryCache.destroy();
        // new client-side query-cache should have all updates from server side
        clientQueryCache = clientMap.getQueryCache(queryCacheName);
        int queryCacheSize = clientQueryCache.size();
        Assert.assertEquals(1000, queryCacheSize);
    }

    @Test
    public void multiple_getQueryCache_calls_returns_same_query_cache_instance() {
        final String mapName = "someMap";
        final String queryCacheName = "testCache";
        HazelcastInstance server = newHazelcastInstance();
        HazelcastInstance client = factory.newHazelcastClient(ClientQueryCacheCreateDestroyTest.newClientConfigWithQueryCache(mapName, queryCacheName));
        // create client-side query-cache
        IMap clientMap = client.getMap(mapName);
        QueryCache clientQueryCache1 = clientMap.getQueryCache(queryCacheName);
        QueryCache clientQueryCache2 = clientMap.getQueryCache(queryCacheName);
        Assert.assertTrue((clientQueryCache1 == clientQueryCache2));
    }
}

