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
package com.hazelcast.map.impl.querycache;


import com.hazelcast.config.Config;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EventLostListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class QueryCacheSimpleStressTest extends HazelcastTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Integer> TRUE_PREDICATE = TruePredicate.INSTANCE;

    private final String mapName = HazelcastTestSupport.randomString();

    private final String cacheName = HazelcastTestSupport.randomString();

    private final Config config = new Config();

    private final int numberOfElementsToPut = 10000;

    @Test
    public void testStress() throws Exception {
        final IMap<Integer, Integer> map = getMap();
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
        final QueryCache<Integer, Integer> queryCache = map.getQueryCache(cacheName, QueryCacheSimpleStressTest.TRUE_PREDICATE, true);
        queryCache.addEntryListener(new EventLostListener() {
            @Override
            public void eventLost(EventLostEvent event) {
                queryCache.tryRecover();
            }
        }, true);
        thread.join();
        assertQueryCacheSizeEventually(numberOfElementsToPut, queryCache);
    }
}

