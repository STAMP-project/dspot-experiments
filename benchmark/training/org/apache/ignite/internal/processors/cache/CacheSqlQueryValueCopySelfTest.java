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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.query.GridQueryProcessor;
import org.apache.ignite.internal.processors.query.GridRunningQueryInfo;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests modification of values returned by query iterators with enabled copy on read.
 */
public class CacheSqlQueryValueCopySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int KEYS = 100;

    /**
     * Tests two step query from dedicated client.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTwoStepSqlClientQuery() throws Exception {
        try (Ignite client = startGrid("client")) {
            IgniteCache<Integer, CacheSqlQueryValueCopySelfTest.Value> cache = client.cache(DEFAULT_CACHE_NAME);
            List<Entry<Integer, CacheSqlQueryValueCopySelfTest.Value>> all = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, CacheSqlQueryValueCopySelfTest.Value>(CacheSqlQueryValueCopySelfTest.Value.class, "select * from Value")).getAll();
            assertEquals(CacheSqlQueryValueCopySelfTest.KEYS, all.size());
            for (Entry<Integer, CacheSqlQueryValueCopySelfTest.Value> entry : all)
                entry.getValue().str = "after";

            check(cache);
            QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("select _val from Value"));
            List<List<?>> all0 = qry.getAll();
            assertEquals(CacheSqlQueryValueCopySelfTest.KEYS, all0.size());
            for (List<?> entry : all0)
                ((CacheSqlQueryValueCopySelfTest.Value) (entry.get(0))).str = "after";

            check(cache);
        }
    }

    /**
     * Test two step query without local reduce phase.
     */
    @Test
    public void testTwoStepSkipReduceSqlQuery() {
        IgniteCache<Integer, CacheSqlQueryValueCopySelfTest.Value> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        List<Entry<Integer, CacheSqlQueryValueCopySelfTest.Value>> all = cache.query(new org.apache.ignite.cache.query.SqlQuery<Integer, CacheSqlQueryValueCopySelfTest.Value>(CacheSqlQueryValueCopySelfTest.Value.class, "select * from Value").setPageSize(3)).getAll();
        assertEquals(CacheSqlQueryValueCopySelfTest.KEYS, all.size());
        for (Entry<Integer, CacheSqlQueryValueCopySelfTest.Value> entry : all)
            entry.getValue().str = "after";

        check(cache);
    }

    /**
     * Test two step query value copy.
     */
    @Test
    public void testTwoStepReduceSqlQuery() {
        IgniteCache<Integer, CacheSqlQueryValueCopySelfTest.Value> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        QueryCursor<List<?>> qry = cache.query(new SqlFieldsQuery("select _val from Value order by _key"));
        List<List<?>> all = qry.getAll();
        assertEquals(CacheSqlQueryValueCopySelfTest.KEYS, all.size());
        for (List<?> entry : all)
            ((CacheSqlQueryValueCopySelfTest.Value) (entry.get(0))).str = "after";

        check(cache);
    }

    /**
     * Tests local sql query.
     */
    @Test
    public void testLocalSqlQuery() {
        IgniteCache<Integer, CacheSqlQueryValueCopySelfTest.Value> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        org.apache.ignite.cache.query.SqlQuery<Integer, CacheSqlQueryValueCopySelfTest.Value> qry = new org.apache.ignite.cache.query.SqlQuery(CacheSqlQueryValueCopySelfTest.Value.class.getSimpleName(), "select * from Value");
        qry.setLocal(true);
        List<Entry<Integer, CacheSqlQueryValueCopySelfTest.Value>> all = cache.query(qry).getAll();
        assertFalse(all.isEmpty());
        for (Entry<Integer, CacheSqlQueryValueCopySelfTest.Value> entry : all)
            entry.getValue().str = "after";

        check(cache);
    }

    /**
     * Tests local sql query.
     */
    @Test
    public void testLocalSqlFieldsQuery() {
        IgniteCache<Integer, CacheSqlQueryValueCopySelfTest.Value> cache = grid(0).cache(DEFAULT_CACHE_NAME);
        QueryCursor<List<?>> cur = cache.query(new SqlFieldsQuery("select _val from Value").setLocal(true));
        List<List<?>> all = cur.getAll();
        assertFalse(all.isEmpty());
        for (List<?> entry : all)
            ((CacheSqlQueryValueCopySelfTest.Value) (entry.get(0))).str = "after";

        check(cache);
    }

    /**
     * Test collecting info about running.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRunningSqlFieldsQuery() throws Exception {
        IgniteInternalFuture<?> fut = runQueryAsync(new SqlFieldsQuery("select _val, sleep(1000) from Value limit 3"));
        Thread.sleep(500);
        GridQueryProcessor qryProc = grid(0).context().query();
        Collection<GridRunningQueryInfo> queries = qryProc.runningQueries(0);
        assertEquals(1, queries.size());
        fut.get();
        queries = qryProc.runningQueries(0);
        assertEquals(0, queries.size());
        SqlFieldsQuery qry = new SqlFieldsQuery("select _val, sleep(1000) from Value limit 3");
        qry.setLocal(true);
        fut = runQueryAsync(qry);
        Thread.sleep(500);
        queries = qryProc.runningQueries(0);
        assertEquals(1, queries.size());
        fut.get();
        queries = qryProc.runningQueries(0);
        assertEquals(0, queries.size());
    }

    /**
     * Test collecting info about running.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRunningSqlQuery() throws Exception {
        IgniteInternalFuture<?> fut = runQueryAsync(new org.apache.ignite.cache.query.SqlQuery<Integer, CacheSqlQueryValueCopySelfTest.Value>(CacheSqlQueryValueCopySelfTest.Value.class, "id > sleep(100)"));
        Thread.sleep(500);
        GridQueryProcessor qryProc = grid(0).context().query();
        Collection<GridRunningQueryInfo> queries = qryProc.runningQueries(0);
        assertEquals(1, queries.size());
        fut.get();
        queries = qryProc.runningQueries(0);
        assertEquals(0, queries.size());
        org.apache.ignite.cache.query.SqlQuery<Integer, CacheSqlQueryValueCopySelfTest.Value> qry = new org.apache.ignite.cache.query.SqlQuery(CacheSqlQueryValueCopySelfTest.Value.class, "id > sleep(100)");
        qry.setLocal(true);
        fut = runQueryAsync(qry);
        Thread.sleep(500);
        queries = qryProc.runningQueries(0);
        assertEquals(1, queries.size());
        fut.get();
        queries = qryProc.runningQueries(0);
        assertEquals(0, queries.size());
    }

    /**
     * Test collecting info about running.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCancelingSqlFieldsQuery() throws Exception {
        runQueryAsync(new SqlFieldsQuery("select * from (select _val, sleep(100) from Value limit 50)"));
        Thread.sleep(500);
        final GridQueryProcessor qryProc = grid(0).context().query();
        Collection<GridRunningQueryInfo> queries = qryProc.runningQueries(0);
        assertEquals(1, queries.size());
        final Collection<GridRunningQueryInfo> finalQueries = queries;
        for (GridRunningQueryInfo query : finalQueries)
            qryProc.cancelQueries(Collections.singleton(query.id()));

        int n = 100;
        // Give cluster some time to cancel query and cleanup resources.
        while (n > 0) {
            Thread.sleep(100);
            queries = qryProc.runningQueries(0);
            if (queries.isEmpty())
                break;

            log.info((">>>> Wait for cancel: " + n));
            n--;
        } 
        queries = qryProc.runningQueries(0);
        assertEquals(0, queries.size());
    }

    /**
     *
     */
    private static class Value {
        /**
         *
         */
        @QuerySqlField
        private int id;

        /**
         *
         */
        @QuerySqlField
        private String str;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param str
         * 		String.
         */
        public Value(int id, String str) {
            this.id = id;
            this.str = str;
        }
    }

    /**
     * Utility class with custom SQL functions.
     */
    public static class TestSQLFunctions {
        /**
         * Sleep function to simulate long running queries.
         *
         * @param x
         * 		Time to sleep.
         * @return Return specified argument.
         */
        @QuerySqlFunction
        public static long sleep(long x) {
            if (x >= 0)
                try {
                    Thread.sleep(x);
                } catch (InterruptedException ignored) {
                    // No-op.
                }

            return x;
        }
    }
}

