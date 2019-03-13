/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.ignite;


import CachePeekMode.ALL;
import IgniteCacheOperation.REMOVE;
import IgniteConstants.IGNITE_CACHE_KEY;
import IgniteConstants.IGNITE_CACHE_OPERATION;
import IgniteConstants.IGNITE_CACHE_QUERY;
import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache.Entry;
import org.apache.camel.CamelException;
import org.apache.camel.util.ObjectHelper;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.Query;
import org.junit.Test;


public class IgniteCacheTest extends AbstractIgniteTest {
    @Test
    public void testAddEntry() {
        template.requestBodyAndHeader("ignite-cache:testcache1?operation=PUT", "1234", IGNITE_CACHE_KEY, "abcd");
        assert_().that(ignite().cache("testcache1").size(ALL)).isEqualTo(1);
        assert_().that(ignite().cache("testcache1").get("abcd")).isEqualTo("1234");
    }

    @Test
    public void testAddEntrySet() {
        template.requestBody("ignite-cache:testcache1?operation=PUT", ImmutableMap.of("abcd", "1234", "efgh", "5678"));
        assert_().that(ignite().cache("testcache1").size(ALL)).isEqualTo(2);
        assert_().that(ignite().cache("testcache1").get("abcd")).isEqualTo("1234");
        assert_().that(ignite().cache("testcache1").get("efgh")).isEqualTo("5678");
    }

    @Test
    public void testGetOne() {
        testAddEntry();
        String result = template.requestBody("ignite-cache:testcache1?operation=GET", "abcd", String.class);
        assert_().that(result).isEqualTo("1234");
        result = template.requestBodyAndHeader("ignite-cache:testcache1?operation=GET", "this value won't be used", IGNITE_CACHE_KEY, "abcd", String.class);
        assert_().that(result).isEqualTo("1234");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMany() {
        IgniteCache<String, String> cache = ignite().getOrCreateCache("testcache1");
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            cache.put(("k" + i), ("v" + i));
            keys.add(("k" + i));
        }
        Map<String, String> result = template.requestBody("ignite-cache:testcache1?operation=GET", keys, Map.class);
        for (String k : keys) {
            assert_().that(result.get(k)).isEqualTo(k.replace("k", "v"));
        }
    }

    @Test
    public void testGetSize() {
        IgniteCache<String, String> cache = ignite().getOrCreateCache("testcache1");
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            cache.put(("k" + i), ("v" + i));
            keys.add(("k" + i));
        }
        Integer result = template.requestBody("ignite-cache:testcache1?operation=SIZE", keys, Integer.class);
        assert_().that(result).isEqualTo(100);
    }

    @Test
    public void testQuery() {
        IgniteCache<String, String> cache = ignite().getOrCreateCache("testcache1");
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            cache.put(("k" + i), ("v" + i));
            keys.add(("k" + i));
        }
        Query<Entry<String, String>> query = new org.apache.ignite.cache.query.ScanQuery(new org.apache.ignite.lang.IgniteBiPredicate<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean apply(String key, String value) {
                return (Integer.parseInt(key.replace("k", ""))) >= 50;
            }
        });
        List results = template.requestBodyAndHeader("ignite-cache:testcache1?operation=QUERY", keys, IGNITE_CACHE_QUERY, query, List.class);
        assert_().that(results.size()).isEqualTo(50);
    }

    @Test
    public void testGetManyTreatCollectionsAsCacheObjects() {
        IgniteCache<Object, String> cache = ignite().getOrCreateCache("testcache1");
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            cache.put(("k" + i), ("v" + i));
            keys.add(("k" + i));
        }
        // Also add a cache entry with the entire Set as a key.
        cache.put(keys, "---");
        String result = template.requestBody("ignite-cache:testcache1?operation=GET&treatCollectionsAsCacheObjects=true", keys, String.class);
        assert_().that(result).isEqualTo("---");
    }

    @Test
    public void testRemoveEntry() {
        IgniteCache<String, String> cache = ignite().getOrCreateCache("testcache1");
        cache.put("abcd", "1234");
        cache.put("efgh", "5678");
        assert_().that(cache.size(ALL)).isEqualTo(2);
        template.requestBody("ignite-cache:testcache1?operation=REMOVE", "abcd");
        assert_().that(cache.size(ALL)).isEqualTo(1);
        assert_().that(cache.get("abcd")).isNull();
        template.requestBodyAndHeader("ignite-cache:testcache1?operation=REMOVE", "this value won't be used", IGNITE_CACHE_KEY, "efgh");
        assert_().that(cache.size(ALL)).isEqualTo(0);
        assert_().that(cache.get("efgh")).isNull();
    }

    @Test
    public void testClearCache() {
        IgniteCache<String, String> cache = ignite().getOrCreateCache("testcache1");
        for (int i = 0; i < 100; i++) {
            cache.put(("k" + i), ("v" + i));
        }
        assert_().that(cache.size(ALL)).isEqualTo(100);
        template.requestBody("ignite-cache:testcache1?operation=CLEAR", "this value won't be used");
        assert_().that(cache.size(ALL)).isEqualTo(0);
    }

    @Test
    public void testHeaderSetRemoveEntry() {
        testAddEntry();
        String result = template.requestBody("ignite-cache:testcache1?operation=GET", "abcd", String.class);
        assert_().that(result).isEqualTo("1234");
        result = template.requestBodyAndHeader("ignite-cache:testcache1?operation=GET", "abcd", IGNITE_CACHE_OPERATION, REMOVE, String.class);
        // The body has not changed, but the cache entry is gone.
        assert_().that(result).isEqualTo("abcd");
        assert_().that(ignite().cache("testcache1").size(ALL)).isEqualTo(0);
    }

    @Test
    public void testAddEntryNoCacheCreation() {
        try {
            template.requestBodyAndHeader("ignite-cache:testcache2?operation=PUT&failIfInexistentCache=true", "1234", IGNITE_CACHE_KEY, "abcd");
        } catch (Exception e) {
            assert_().that(ObjectHelper.getException(CamelException.class, e).getMessage()).startsWith("Ignite cache testcache2 doesn't exist");
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void testAddEntryDoNotPropagateIncomingBody() {
        Object result = template.requestBodyAndHeader("ignite-cache:testcache1?operation=PUT&propagateIncomingBodyIfNoReturnValue=false", "1234", IGNITE_CACHE_KEY, "abcd", Object.class);
        assert_().that(ignite().cache("testcache1").size(ALL)).isEqualTo(1);
        assert_().that(ignite().cache("testcache1").get("abcd")).isEqualTo("1234");
        assert_().that(result).isNull();
    }
}

