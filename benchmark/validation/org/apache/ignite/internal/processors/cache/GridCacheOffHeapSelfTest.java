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


import Cache.Entry;
import CachePeekMode.OFFHEAP;
import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for cache swap.
 */
public class GridCacheOffHeapSelfTest extends GridCommonAbstractTest {
    /**
     * Saved versions.
     */
    private final Map<Integer, Object> versions = new HashMap<>();

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOffHeapIterator() throws Exception {
        try {
            startGrids(1);
            grid(0);
            IgniteCache<Integer, Integer> cache = grid(0).cache(DEFAULT_CACHE_NAME);
            for (int i = 0; i < 100; i++) {
                info(("Putting: " + i));
                cache.put(i, i);
            }
            int i = 0;
            for (Entry<Integer, Integer> e : cache.localEntries(OFFHEAP)) {
                Integer key = e.getKey();
                info(("Key: " + key));
                i++;
                cache.remove(e.getKey());
                assertNull(cache.get(key));
            }
            assertEquals(100, i);
            assert (cache.localSize()) == 0;
        } finally {
            stopAllGrids();
        }
    }

    /**
     *
     */
    private static class CacheValue {
        /**
         * Value.
         */
        @QuerySqlField
        private final int val;

        /**
         *
         *
         * @param val
         * 		Value.
         */
        private CacheValue(int val) {
            this.val = val;
        }

        /**
         *
         *
         * @return Value.
         */
        public int value() {
            return val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(GridCacheOffHeapSelfTest.CacheValue.class, this);
        }
    }
}

