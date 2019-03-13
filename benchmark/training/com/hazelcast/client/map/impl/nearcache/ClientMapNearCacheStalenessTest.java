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


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.IMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapNearCacheStalenessTest extends HazelcastTestSupport {
    private static final int ENTRY_COUNT = 10;

    private static final int NEAR_CACHE_INVALIDATOR_THREAD_COUNT = 3;

    private static final int NEAR_CACHE_PUTTER_THREAD_COUNT = 10;

    private static final int NEAR_CACHE_REMOVER_THREAD_COUNT = 3;

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private IMap<Integer, Integer> clientMap;

    private IMap<Integer, Integer> memberMap;

    protected TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testNearCache_notContainsStaleValue_whenUpdatedByMultipleThreads() {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (ClientMapNearCacheStalenessTest.NEAR_CACHE_INVALIDATOR_THREAD_COUNT); i++) {
            Thread putter = new com.hazelcast.map.impl.nearcache.MapNearCacheStalenessTest.NearCacheInvalidator(stop, memberMap, ClientMapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(putter);
        }
        for (int i = 0; i < (ClientMapNearCacheStalenessTest.NEAR_CACHE_PUTTER_THREAD_COUNT); i++) {
            Thread getter = new com.hazelcast.map.impl.nearcache.MapNearCacheStalenessTest.NearCachePutter(stop, clientMap, ClientMapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(getter);
        }
        for (int i = 0; i < (ClientMapNearCacheStalenessTest.NEAR_CACHE_REMOVER_THREAD_COUNT); i++) {
            Thread remover = new com.hazelcast.map.impl.nearcache.MapNearCacheStalenessTest.NearCacheRemover(stop, clientMap, ClientMapNearCacheStalenessTest.ENTRY_COUNT);
            threads.add(remover);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        sleepSeconds(5);
        stop.set(true);
        for (Thread thread : threads) {
            assertJoinable(thread);
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                ClientMapNearCacheStalenessTest.assertNoStaleDataExistInNearCache(clientMap);
            }
        });
    }
}

