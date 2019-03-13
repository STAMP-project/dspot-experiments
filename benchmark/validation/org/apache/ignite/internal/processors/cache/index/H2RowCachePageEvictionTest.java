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
package org.apache.ignite.internal.processors.cache.index;


import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.junit.Test;


/**
 * Tests for H2RowCacheRegistry with page eviction.
 */
public class H2RowCachePageEvictionTest extends AbstractIndexingCommonTest {
    /**
     * Entries count.
     */
    private static final int ENTRIES = 10000;

    /**
     * Offheap size for memory policy.
     */
    private static final int SIZE = (12 * 1024) * 1024;

    /**
     * Test time.
     */
    private static final int TEST_TIME = 3 * 60000;

    /**
     * Default policy name.
     */
    private static final String DATA_REGION_NAME = "default";

    /**
     * Default policy name.
     */
    private static final String CACHE_NAME = "cache";

    /**
     * Random generator.
     */
    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    /**
     * Default policy name.
     */
    private static boolean persistenceEnabled;

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testEvictPagesWithDiskStorageSingleCacheInGroup() throws Exception {
        H2RowCachePageEvictionTest.persistenceEnabled = true;
        startGrid();
        grid().active(true);
        checkRowCacheOnPageEviction();
    }

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testEvictPagesWithDiskStorageWithOtherCacheInGroup() throws Exception {
        H2RowCachePageEvictionTest.persistenceEnabled = true;
        startGrid();
        grid().active(true);
        grid().getOrCreateCache(cacheConfiguration("cacheWithoutOnHeapCache", false));
        checkRowCacheOnPageEviction();
    }

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testEvictPagesWithoutDiskStorageSingleCacheInGroup() throws Exception {
        H2RowCachePageEvictionTest.persistenceEnabled = false;
        startGrid();
        checkRowCacheOnPageEviction();
    }

    /**
     *
     *
     * @throws Exception
     * 		On error.
     */
    @Test
    public void testEvictPagesWithoutDiskStorageWithOtherCacheInGroup() throws Exception {
        H2RowCachePageEvictionTest.persistenceEnabled = false;
        startGrid();
        grid().getOrCreateCache(cacheConfiguration("cacheWithoutOnHeapCache", false));
        checkRowCacheOnPageEviction();
    }

    /**
     *
     */
    private static class Value {
        /**
         * Long value.
         */
        @QuerySqlField
        private long lVal;

        /**
         * String value.
         */
        @QuerySqlField
        private byte[] bytes = new byte[1024];

        /**
         *
         *
         * @param k
         * 		Key.
         */
        Value(int k) {
            lVal = k;
            H2RowCachePageEvictionTest.RND.nextBytes(bytes);
        }
    }
}

