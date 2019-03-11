/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.loaderWriter.writebehind;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BasicClusteredWriteBehindPassthroughTest {
    private static final URI CLUSTER_URI = URI.create("terracotta://example.com:9540/clustered-write-behind");

    private RecordingLoaderWriter<Long, String> loaderWriter;

    private final List<BasicClusteredWriteBehindPassthroughTest.Record> cacheRecords = new ArrayList<>();

    private static final String CACHE_NAME = "cache-1";

    private static final long KEY = 1L;

    @Test
    public void testBasicClusteredWriteBehind() {
        try (PersistentCacheManager cacheManager = createCacheManager()) {
            Cache<Long, String> cache = cacheManager.getCache(BasicClusteredWriteBehindPassthroughTest.CACHE_NAME, Long.class, String.class);
            for (int i = 0; i < 10; i++) {
                put(cache, String.valueOf(i));
            }
            assertValue(cache, String.valueOf(9));
            verifyRecords(cache);
            cache.clear();
        }
    }

    @Test
    public void testWriteBehindMultipleClients() {
        try (PersistentCacheManager cacheManager1 = createCacheManager();PersistentCacheManager cacheManager2 = createCacheManager()) {
            Cache<Long, String> client1 = cacheManager1.getCache(BasicClusteredWriteBehindPassthroughTest.CACHE_NAME, Long.class, String.class);
            Cache<Long, String> client2 = cacheManager2.getCache(BasicClusteredWriteBehindPassthroughTest.CACHE_NAME, Long.class, String.class);
            put(client1, "The one from client1");
            put(client2, "The one one from client2");
            assertValue(client1, "The one one from client2");
            remove(client1);
            put(client2, "The one from client2");
            put(client1, "The one one from client1");
            assertValue(client2, "The one one from client1");
            remove(client2);
            assertValue(client1, null);
            put(client1, "The one from client1");
            put(client1, "The one one from client1");
            remove(client2);
            put(client2, "The one from client2");
            put(client2, "The one one from client2");
            remove(client1);
            assertValue(client2, null);
            verifyRecords(client1);
            client1.clear();
        }
    }

    @Test
    public void testClusteredWriteBehindCAS() {
        try (PersistentCacheManager cacheManager = createCacheManager()) {
            Cache<Long, String> cache = cacheManager.getCache(BasicClusteredWriteBehindPassthroughTest.CACHE_NAME, Long.class, String.class);
            putIfAbsent(cache, "First value", true);
            assertValue(cache, "First value");
            putIfAbsent(cache, "Second value", false);
            assertValue(cache, "First value");
            put(cache, "First value again");
            assertValue(cache, "First value again");
            replace(cache, "Replaced First value", true);
            assertValue(cache, "Replaced First value");
            replace(cache, "Replaced First value", "Replaced First value again", true);
            assertValue(cache, "Replaced First value again");
            replace(cache, "Replaced First", "Tried Replacing First value again", false);
            assertValue(cache, "Replaced First value again");
            condRemove(cache, "Replaced First value again", true);
            assertValue(cache, null);
            replace(cache, "Trying to replace value", false);
            assertValue(cache, null);
            put(cache, "new value", true);
            assertValue(cache, "new value");
            condRemove(cache, "new value", false);
            verifyRecords(cache);
            cache.clear();
        }
    }

    @Test
    public void testClusteredWriteBehindLoading() {
        try (CacheManager cacheManager = createCacheManager()) {
            Cache<Long, String> cache = cacheManager.getCache(BasicClusteredWriteBehindPassthroughTest.CACHE_NAME, Long.class, String.class);
            put(cache, "Some value");
            tryFlushingUpdatesToSOR(cache);
            cache.clear();
            Assert.assertThat(cache.get(BasicClusteredWriteBehindPassthroughTest.KEY), Matchers.notNullValue());
            cache.clear();
        }
    }

    private static final class Record {
        private final Long key;

        private final String value;

        private Record(Long key, String value) {
            this.key = key;
            this.value = value;
        }

        Long getKey() {
            return key;
        }

        String getValue() {
            return value;
        }
    }
}

