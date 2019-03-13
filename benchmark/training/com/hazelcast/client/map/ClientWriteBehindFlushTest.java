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
package com.hazelcast.client.map;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.mapstore.writebehind.MapStoreWithCounter;
import com.hazelcast.map.impl.mapstore.writebehind.TemporaryBlockerMapStore;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings("WeakerAccess")
public class ClientWriteBehindFlushTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "default";

    private TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testWriteBehindQueues_emptied_onOwnerAndBackupNodes() {
        MapStoreWithCounter mapStore = new MapStoreWithCounter<Integer, String>();
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setImplementation(mapStore).setWriteDelaySeconds(3000);
        Config config = getConfig();
        config.getMapConfig(ClientWriteBehindFlushTest.MAP_NAME).setMapStoreConfig(mapStoreConfig);
        HazelcastInstance member1 = hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance member2 = hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance member3 = hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        IMap<Integer, Integer> map = client.getMap(ClientWriteBehindFlushTest.MAP_NAME);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        map.flush();
        assertWriteBehindQueuesEmpty(ClientWriteBehindFlushTest.MAP_NAME, Arrays.asList(member1, member2, member3));
    }

    @Test
    public void testFlush_shouldNotCause_concurrentStoreOperation() {
        int blockStoreOperationSeconds = 5;
        TemporaryBlockerMapStore store = new TemporaryBlockerMapStore(blockStoreOperationSeconds);
        Config config = newMapStoredConfig(store, 2);
        hazelcastFactory.newHazelcastInstance(config);
        IMap<String, String> map = hazelcastFactory.newHazelcastClient(getClientConfig()).getMap(ClientWriteBehindFlushTest.MAP_NAME);
        map.put("key", "value");
        map.flush();
        Assert.assertEquals("Expecting only one store after flush", 1, store.getStoreOperationCount());
    }
}

