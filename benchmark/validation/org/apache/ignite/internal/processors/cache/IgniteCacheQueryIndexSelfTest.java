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
package org.apache.ignite.internal.processors.cache;


import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.junit.Test;


/**
 * Tests for cache query index.
 */
public class IgniteCacheQueryIndexSelfTest extends GridCacheAbstractSelfTest {
    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     * Entry count.
     */
    private static final int ENTRY_CNT = 10;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWithoutStoreLoad() throws Exception {
        IgniteCache<Integer, IgniteCacheQueryIndexSelfTest.CacheValue> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        for (int i = 0; i < (IgniteCacheQueryIndexSelfTest.ENTRY_CNT); i++)
            cache.put(i, new IgniteCacheQueryIndexSelfTest.CacheValue(i));

        checkCache(cache);
        checkQuery(cache);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWithStoreLoad() throws Exception {
        for (int i = 0; i < (IgniteCacheQueryIndexSelfTest.ENTRY_CNT); i++)
            storeStgy.putToStore(i, new IgniteCacheQueryIndexSelfTest.CacheValue(i));

        IgniteCache<Integer, IgniteCacheQueryIndexSelfTest.CacheValue> cache0 = grid(0).cache(DEFAULT_CACHE_NAME);
        cache0.loadCache(null);
        checkCache(cache0);
        checkQuery(cache0);
    }

    /**
     * Test cache value.
     */
    private static class CacheValue {
        @QuerySqlField
        private final int val;

        CacheValue(int val) {
            this.val = val;
        }

        int value() {
            return val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheQueryIndexSelfTest.CacheValue.class, this);
        }
    }
}

