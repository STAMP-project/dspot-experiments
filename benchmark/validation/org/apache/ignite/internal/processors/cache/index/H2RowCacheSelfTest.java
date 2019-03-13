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


import java.util.Random;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.processors.query.h2.H2RowCache;
import org.junit.Test;


/**
 * Tests H2RowCacheRegistry.
 */
@SuppressWarnings({ "unchecked", "ConstantConditions" })
public class H2RowCacheSelfTest extends AbstractIndexingCommonTest {
    /**
     * Keys count.
     */
    private static final int ENTRIES = 1000;

    /**
     * Random generator.
     */
    private static final Random RND = new Random(System.currentTimeMillis());

    /**
     *
     */
    @Test
    public void testDestroyCacheCreation() {
        final String cacheName0 = "cache0";
        final String cacheName1 = "cache1";
        grid().getOrCreateCache(cacheConfiguration(cacheName0, false));
        int grpId = grid().cachex(cacheName0).context().groupId();
        assertNull(rowCache(grid(), grpId));
        grid().getOrCreateCache(cacheConfiguration(cacheName1, true));
        assertEquals(grpId, grid().cachex(cacheName1).context().groupId());
        assertNotNull(rowCache(grid(), grpId));
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDestroyCacheSingleCacheInGroup() throws IgniteCheckedException {
        checkDestroyCache();
    }

    /**
     *
     *
     * @throws IgniteCheckedException
     * 		If failed.
     */
    @Test
    public void testDestroyCacheWithOtherCacheInGroup() throws IgniteCheckedException {
        grid().getOrCreateCache(cacheConfiguration("cacheWithoutOnheapCache", false));
        checkDestroyCache();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteEntryCacheSingleCacheInGroup() throws Exception {
        checkDeleteEntry();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeleteEntryWithOtherCacheInGroup() throws Exception {
        grid().getOrCreateCache(cacheConfiguration("cacheWithoutOnheapCache", false));
        checkDeleteEntry();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateEntryCacheSingleCacheInGroup() throws Exception {
        checkDeleteEntry();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUpdateEntryWithOtherCacheInGroup() throws Exception {
        grid().getOrCreateCache(cacheConfiguration("cacheWithoutOnheapCache", false));
        checkUpdateEntry();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFixedSize() throws Exception {
        int maxSize = 100;
        String cacheName = "cacheWithLimitedSize";
        CacheConfiguration ccfg = cacheConfiguration(cacheName, true).setSqlOnheapCacheMaxSize(maxSize);
        IgniteCache cache = grid().getOrCreateCache(ccfg);
        int grpId = grid().cachex(cacheName).context().groupId();
        // Fill half.
        for (int i = 0; i < (maxSize / 2); i++)
            cache.put(i, new H2RowCacheSelfTest.Value(1));

        H2RowCache rowCache = rowCache(grid(), grpId);
        assertEquals(0, rowCache.size());
        // Warmup cache.
        cache.query(new SqlFieldsQuery("SELECT * FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals((maxSize / 2), rowCache.size());
        // Query again - are there any leaks?
        cache.query(new SqlFieldsQuery("SELECT * FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals((maxSize / 2), rowCache.size());
        // Fill up to limit.
        for (int i = maxSize / 2; i < maxSize; i++)
            cache.put(i, new H2RowCacheSelfTest.Value(1));

        assertEquals((maxSize / 2), rowCache.size());
        cache.query(new SqlFieldsQuery("SELECT * FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals(maxSize, rowCache.size());
        // Out of limit.
        for (int i = maxSize; i < (maxSize * 2); i++)
            cache.put(i, new H2RowCacheSelfTest.Value(1));

        assertEquals(maxSize, rowCache.size());
        cache.query(new SqlFieldsQuery("SELECT * FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals(maxSize, rowCache.size());
        // Delete all.
        cache.query(new SqlFieldsQuery("DELETE FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals(0, rowCache.size());
        cache.query(new SqlFieldsQuery("SELECT * FROM Value").setDataPageScanEnabled(false)).getAll();
        assertEquals(0, rowCache.size());
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
        private String strVal;

        /**
         *
         *
         * @param k
         * 		Key.
         */
        Value(int k) {
            lVal = k;
            strVal = "val_" + k;
        }
    }
}

