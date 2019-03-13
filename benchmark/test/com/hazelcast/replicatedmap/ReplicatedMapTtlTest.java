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
package com.hazelcast.replicatedmap;


import ReplicatedMapService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class ReplicatedMapTtlTest extends ReplicatedMapAbstractTest {
    @Test
    public void testPutWithTTL_withMigration() {
        int nodeCount = 1;
        int keyCount = 10000;
        int operationCount = 10000;
        int threadCount = 15;
        int ttl = 500;
        testPutWithTTL(nodeCount, keyCount, operationCount, threadCount, ttl, true);
    }

    @Test
    public void testPutWithTTL_withoutMigration() {
        int nodeCount = 5;
        int keyCount = 10000;
        int operationCount = 10000;
        int threadCount = 10;
        int ttl = 500;
        testPutWithTTL(nodeCount, keyCount, operationCount, threadCount, ttl, false);
    }

    @Test
    public void clear_empties_internal_ttl_schedulers() {
        HazelcastInstance node = createHazelcastInstance();
        String mapName = "test";
        ReplicatedMap map = node.getReplicatedMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i, 100, TimeUnit.DAYS);
        }
        map.clear();
        ReplicatedMapTtlTest.assertAllTtlSchedulersEmpty(map);
    }

    @Test
    public void remove_empties_internal_ttl_schedulers() {
        HazelcastInstance node = createHazelcastInstance();
        String mapName = "test";
        ReplicatedMap map = node.getReplicatedMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i, 100, TimeUnit.DAYS);
        }
        for (int i = 0; i < 1000; i++) {
            map.remove(i);
        }
        ReplicatedMapTtlTest.assertAllTtlSchedulersEmpty(map);
    }

    @Test
    public void service_reset_empties_internal_ttl_schedulers() {
        HazelcastInstance node = createHazelcastInstance();
        String mapName = "test";
        ReplicatedMap map = node.getReplicatedMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i, 100, TimeUnit.DAYS);
        }
        ReplicatedMapService service = HazelcastTestSupport.getNodeEngineImpl(node).getService(SERVICE_NAME);
        service.reset();
        ReplicatedMapTtlTest.assertAllTtlSchedulersEmpty(map);
    }
}

