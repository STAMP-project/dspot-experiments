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
package com.hazelcast.client.map.impl.nearcache;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapNearCacheInvalidationTest extends ClientTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    private String mapName = randomMapName();

    @Test
    public void testBatchInvalidationRemovesEntries() {
        Config config = getConfig();
        ClientMapNearCacheInvalidationTest.configureBatching(config, 10, 1);
        ClientConfig clientConfig = getClientConfig(mapName);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        makeSureConnectedToServers(client, 2);
        IMap<Integer, Integer> serverMap = server.getMap(mapName);
        IMap<Integer, Integer> clientMap = client.getMap(mapName);
        int size = 1000;
        // fill serverMap
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        // fill Near Cache on client
        for (int i = 0; i < size; i++) {
            clientMap.get(i);
        }
        // generate invalidation data
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        ClientMapNearCacheInvalidationTest.assertNearCacheSizeEventually(clientMap, 0);
    }

    @Test
    public void testHigherBatchSize_shouldNotCauseAnyInvalidation_onClient() {
        Config config = getConfig();
        ClientMapNearCacheInvalidationTest.configureBatching(config, Integer.MAX_VALUE, Integer.MAX_VALUE);
        ClientConfig clientConfig = getClientConfig(mapName);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        makeSureConnectedToServers(client, 2);
        IMap<Integer, Integer> serverMap = server.getMap(mapName);
        IMap<Integer, Integer> clientMap = client.getMap(mapName);
        int size = 1000;
        // fill serverMap
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        // fill Near Cache on client
        for (int i = 0; i < size; i++) {
            clientMap.get(i);
        }
        // generate invalidation data
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        ClientMapNearCacheInvalidationTest.assertNearCacheSizeEventually(clientMap, size);
    }

    @Test
    public void testMapClear_shouldClearNearCaches_onOwnerAndBackupNodes() {
        Config config = getConfig();
        ClientMapNearCacheInvalidationTest.configureBatching(config, 10, 1);
        ClientConfig clientConfig = getClientConfig(mapName);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        makeSureConnectedToServers(client, 2);
        IMap<Integer, Integer> serverMap = server.getMap(mapName);
        IMap<Integer, Integer> clientMap = client.getMap(mapName);
        int size = 1000;
        // fill serverMap
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        // fill Near Cache on client
        for (int i = 0; i < size; i++) {
            clientMap.get(i);
        }
        serverMap.clear();
        ClientMapNearCacheInvalidationTest.assertNearCacheSizeEventually(clientMap, 0);
    }

    @Test
    public void testMapEvictAll_shouldClearNearCaches_onOwnerAndBackupNodes() {
        Config config = getConfig();
        ClientMapNearCacheInvalidationTest.configureBatching(config, 10, 1);
        ClientConfig clientConfig = getClientConfig(mapName);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        makeSureConnectedToServers(client, 2);
        IMap<Integer, Integer> serverMap = server.getMap(mapName);
        IMap<Integer, Integer> clientMap = client.getMap(mapName);
        int size = 1000;
        // fill serverMap
        for (int i = 0; i < size; i++) {
            serverMap.put(i, i);
        }
        // fill Near Cache on client
        for (int i = 0; i < size; i++) {
            clientMap.get(i);
        }
        serverMap.evictAll();
        ClientMapNearCacheInvalidationTest.assertNearCacheSizeEventually(clientMap, 0);
    }
}

