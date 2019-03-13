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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Since sql (unlike cache api) doesn't remove expired rows, we need to check that expired rows are filtered by the
 * cursor. Background expired rows cleanup is turned off.
 */
public class H2RowExpireTimeIndexSelfTest extends GridCommonAbstractTest {
    /**
     * In so milliseconds since creation row can be treated as expired.
     */
    private static final long EXPIRE_IN_MS_FROM_CREATE = 100L;

    /**
     * How many milliseconds we are going to wait til, cache data row become expired.
     */
    private static final long WAIT_MS_TIL_EXPIRED = (H2RowExpireTimeIndexSelfTest.EXPIRE_IN_MS_FROM_CREATE) * 2L;

    /**
     * Expired row check of the tree index in case {@link H2TreeIndex#find(Session, SearchRow, SearchRow)} optimizes
     * returned cursor as SingleRowCursor.
     */
    @Test
    public void testTreeIndexSingleRow() throws Exception {
        IgniteCache<Integer, Integer> cache = createTestCache();
        cache.put(1, 2);
        cache.put(3, 4);
        putExpireInYear(cache, 5, 6);
        putExpiredSoon(cache, 42, 43);
        U.sleep(H2RowExpireTimeIndexSelfTest.WAIT_MS_TIL_EXPIRED);
        {
            List<List<?>> expired = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer where _key = 42")).getAll();
            Assert.assertTrue(("Expired row should not be returned by sql. Result = " + expired), expired.isEmpty());
        }
        {
            List<List<?>> expired = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer where id >= 42 and id <= 42")).getAll();
            Assert.assertTrue(("Expired row should not be returned by sql. Result = " + expired), expired.isEmpty());
        }
        {
            List<List<?>> expired = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer where id >= 5 and id <= 5")).getAll();
            assertEqualsCollections(Collections.singletonList(Arrays.asList(5, 6)), expired);
        }
    }

    /**
     * Expired row check of the tree index in case {@link H2TreeIndex#find(Session, SearchRow, SearchRow)} doesn't
     * perform one-row optimization and returns {@link H2Cursor}.
     */
    @Test
    public void testTreeIndexManyRows() throws Exception {
        IgniteCache<Integer, Integer> cache = createTestCache();
        cache.put(1, 2);
        cache.put(3, 4);
        putExpireInYear(cache, 5, 6);
        putExpiredSoon(cache, 42, 43);
        putExpiredSoon(cache, 77, 88);
        U.sleep(H2RowExpireTimeIndexSelfTest.WAIT_MS_TIL_EXPIRED);
        {
            List<List<?>> mixed = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer WHERE id >= 5")).getAll();
            assertEqualsCollections(Collections.singletonList(Arrays.asList(5, 6)), mixed);
        }
        {
            List<List<?>> mixed = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer WHERE id >= 3")).getAll();
            assertEqualsCollections(Arrays.asList(Arrays.asList(3, 4), Arrays.asList(5, 6)), mixed);
        }
        {
            List<List<?>> expired = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer WHERE id >= 42")).getAll();
            Assert.assertTrue(("Expired row should not be returned by sql. Result = " + expired), expired.isEmpty());
        }
    }

    /**
     * Expired row check if hash index is used.
     */
    @Test
    public void testHashIndex() throws Exception {
        IgniteCache<Integer, Integer> cache = createTestCache();
        cache.put(1, 2);
        cache.put(3, 4);
        putExpireInYear(cache, 5, 6);
        putExpiredSoon(cache, 42, 43);
        putExpiredSoon(cache, 77, 88);
        U.sleep(H2RowExpireTimeIndexSelfTest.WAIT_MS_TIL_EXPIRED);
        List<List<?>> mixed = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer USE INDEX (\"_key_PK_hash\")")).getAll();
        List<List<Integer>> exp = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6));
        assertEqualsCollections(exp, mixed);
        List<List<?>> expired = cache.query(new SqlFieldsQuery("SELECT * FROM \"notEager\".Integer USE INDEX (\"_key_PK_hash\") WHERE id >= 42 and id <= 42")).getAll();
        Assert.assertTrue(("Expired row should not be returned by sql. Result = " + expired), expired.isEmpty());
    }
}

