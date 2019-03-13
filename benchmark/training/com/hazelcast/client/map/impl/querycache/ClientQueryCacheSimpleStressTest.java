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


import TruePredicate.INSTANCE;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class ClientQueryCacheSimpleStressTest extends HazelcastTestSupport {
    private final String mapName = randomString();

    private final String cacheName = randomString();

    private final ClientConfig config = new ClientConfig();

    private final int numberOfElementsToPut = 10000;

    private HazelcastInstance instance;

    @Test
    public void testStress() throws Exception {
        final IMap<Integer, Integer> map = instance.getMap(mapName);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (numberOfElementsToPut); i++) {
                    map.put(i, i);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        QueryCache<Integer, Integer> queryCache = map.getQueryCache(cacheName, INSTANCE, true);
        thread.join();
        assertQueryCacheSizeEventually(numberOfElementsToPut, queryCache);
    }
}

