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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.map.QueryCache;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheMapLoaderTest extends HazelcastTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Integer> TRUE_PREDICATE = TruePredicate.INSTANCE;

    @Test
    public void testQueryCache_includesLoadedEntries_after_get() {
        String mapName = HazelcastTestSupport.randomString();
        String cacheName = HazelcastTestSupport.randomString();
        Config config = getConfig(mapName, cacheName);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = AbstractQueryCacheTestSupport.getMap(node, mapName);
        final QueryCache<Integer, Integer> cache = map.getQueryCache(cacheName, QueryCacheMapLoaderTest.TRUE_PREDICATE, true);
        map.get(1);
        map.get(2);
        map.get(3);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(3, cache.size());
            }
        });
    }

    @Test
    public void testQueryCache_includesLoadedEntries_after_getAll() {
        String mapName = HazelcastTestSupport.randomString();
        String cacheName = HazelcastTestSupport.randomString();
        Config config = getConfig(mapName, cacheName);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = AbstractQueryCacheTestSupport.getMap(node, mapName);
        final QueryCache<Integer, Integer> cache = map.getQueryCache(cacheName, QueryCacheMapLoaderTest.TRUE_PREDICATE, true);
        map.getAll(new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(3, cache.size());
            }
        });
    }

    private static class TestMapLoader extends MapStoreAdapter<Integer, Integer> {
        private final ConcurrentMap<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();

        public TestMapLoader() {
            map.put(1, 1);
            map.put(2, 2);
            map.put(3, 4);
        }

        @Override
        public Integer load(Integer key) {
            return map.get(key);
        }

        @Override
        public Map<Integer, Integer> loadAll(Collection<Integer> keys) {
            Map<Integer, Integer> results = new HashMap<Integer, Integer>();
            for (Integer key : keys) {
                results.put(key, map.get(key));
            }
            return results;
        }

        @Override
        public Iterable<Integer> loadAllKeys() {
            return Collections.emptySet();
        }
    }
}

