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


import java.util.ArrayList;
import java.util.Collection;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.QueryDetailMetrics;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for cache query details metrics.
 */
public abstract class CacheAbstractQueryDetailMetricsSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int QRY_DETAIL_METRICS_SIZE = 3;

    /**
     * Grid count.
     */
    protected int gridCnt;

    /**
     * Cache mode.
     */
    protected CacheMode cacheMode;

    /**
     * Test metrics for SQL fields queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from String");
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for SQL fields queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(cache, qry, false);
    }

    /**
     * Test metrics for failed SQL queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsQueryFailedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from UNKNOWN");
        checkQueryFailedMetrics(cache, qry);
    }

    /**
     * Test metrics eviction.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testQueryMetricsEviction() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        // Execute several DIFFERENT queries with guaranteed DIFFERENT time of execution.
        cache.query(new SqlFieldsQuery("select * from String")).getAll();
        Thread.sleep(100);
        cache.query(new SqlFieldsQuery("select count(*) from String")).getAll();
        Thread.sleep(100);
        cache.query(new SqlFieldsQuery("select * from String limit 1")).getAll();
        Thread.sleep(100);
        cache.query(new SqlFieldsQuery("select * from String limit 2")).getAll();
        Thread.sleep(100);
        cache.query(new ScanQuery()).getAll();
        Thread.sleep(100);
        cache.query(new SqlQuery("String", "from String")).getAll();
        CacheAbstractQueryDetailMetricsSelfTest.waitingFor(cache, "size", CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE);
        for (int i = 0; i < (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
            checkMetrics(cache, CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE, i, 1, 1, 0, false);

        // Check that collected metrics contains correct items: metrics for last 3 queries.
        Collection<? extends QueryDetailMetrics> metrics = cache.queryDetailMetrics();
        String lastMetrics = "";
        for (QueryDetailMetrics m : metrics)
            lastMetrics += (((m.queryType()) + " ") + (m.query())) + ";";

        assertTrue(lastMetrics.contains("SQL_FIELDS select * from String limit 2;"));
        assertTrue(lastMetrics.contains("SCAN A;"));
        assertTrue(lastMetrics.contains("SELECT \"A\".\"STRING\"._KEY, \"A\".\"STRING\"._VAL from String;"));
        cache = grid(0).context().cache().jcache("B");
        cache.query(new SqlFieldsQuery("select * from String")).getAll();
        cache.query(new SqlFieldsQuery("select count(*) from String")).getAll();
        cache.query(new SqlFieldsQuery("select * from String limit 1")).getAll();
        cache.query(new SqlFieldsQuery("select * from String limit 2")).getAll();
        cache.query(new ScanQuery()).getAll();
        cache.query(new SqlQuery("String", "from String")).getAll();
        CacheAbstractQueryDetailMetricsSelfTest.waitingFor(cache, "size", CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE);
        for (int i = 0; i < (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
            checkMetrics(cache, CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE, i, 1, 1, 0, false);

        if ((gridCnt) > 1) {
            cache = grid(1).context().cache().jcache("A");
            cache.query(new SqlFieldsQuery("select * from String")).getAll();
            cache.query(new SqlFieldsQuery("select count(*) from String")).getAll();
            cache.query(new SqlFieldsQuery("select * from String limit 1")).getAll();
            cache.query(new SqlFieldsQuery("select * from String limit 2")).getAll();
            cache.query(new ScanQuery()).getAll();
            cache.query(new SqlQuery("String", "from String")).getAll();
            CacheAbstractQueryDetailMetricsSelfTest.waitingFor(cache, "size", CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE);
            for (int i = 0; i < (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
                checkMetrics(cache, CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE, i, 1, 1, 0, false);

            cache = grid(1).context().cache().jcache("B");
            cache.query(new SqlFieldsQuery("select * from String")).getAll();
            cache.query(new SqlFieldsQuery("select count(*) from String")).getAll();
            cache.query(new SqlFieldsQuery("select * from String limit 1")).getAll();
            cache.query(new SqlFieldsQuery("select * from String limit 2")).getAll();
            cache.query(new ScanQuery()).getAll();
            cache.query(new SqlQuery("String", "from String")).getAll();
            CacheAbstractQueryDetailMetricsSelfTest.waitingFor(cache, "size", CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE);
            for (int i = 0; i < (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
                checkMetrics(cache, CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE, i, 1, 1, 0, false);

        }
    }

    /**
     *
     */
    private static class Worker extends Thread {
        /**
         *
         */
        private final IgniteCache cache;

        /**
         *
         */
        private final Query qry;

        /**
         *
         */
        Worker(IgniteCache cache, Query qry) {
            this.cache = cache;
            this.qry = qry;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            cache.query(qry).getAll();
        }
    }

    /**
     * Test metrics if queries executed from several threads.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testQueryMetricsMultithreaded() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        Collection<CacheAbstractQueryDetailMetricsSelfTest.Worker> workers = new ArrayList<>();
        int repeat = 10;
        for (int k = 0; k < repeat; k++) {
            // Execute as match queries as history size to avoid eviction.
            for (int i = 1; i <= (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
                workers.add(new CacheAbstractQueryDetailMetricsSelfTest.Worker(cache, new SqlFieldsQuery(("select * from String limit " + i))));

        }
        for (CacheAbstractQueryDetailMetricsSelfTest.Worker worker : workers)
            worker.start();

        for (CacheAbstractQueryDetailMetricsSelfTest.Worker worker : workers)
            worker.join();

        for (int i = 0; i < (CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE); i++)
            checkMetrics(cache, CacheAbstractQueryDetailMetricsSelfTest.QRY_DETAIL_METRICS_SIZE, i, repeat, repeat, 0, false);

    }

    /**
     * Test metrics for Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        ScanQuery<Integer, String> qry = new ScanQuery();
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        ScanQuery<Integer, String> qry = new ScanQuery();
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(cache, qry, true);
    }

    /**
     * Test metrics for failed Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testScanQueryFailedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        ScanQuery<Integer, String> qry = new ScanQuery(Integer.MAX_VALUE);
        checkQueryFailedMetrics(cache, qry);
    }

    /**
     * Test metrics for Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlQuery<Integer, String> qry = new SqlQuery("String", "from String");
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlQuery<Integer, String> qry = new SqlQuery("String", "from String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(cache, qry, true);
    }

    /**
     * Test metrics for Sql queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        TextQuery qry = new TextQuery("String", "1");
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for Sql queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        TextQuery qry = new TextQuery("String", "1");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(cache, qry, true);
    }

    /**
     * Test metrics for failed Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryFailedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        TextQuery qry = new TextQuery("Unknown", "zzz");
        checkQueryFailedMetrics(cache, qry);
    }

    /**
     * Test metrics for SQL cross cache queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsCrossCacheQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from \"B\".String");
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for SQL cross cache queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsCrossCacheQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from \"B\".String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(cache, qry, false);
    }

    /**
     * Test metrics for failed SQL cross cache queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsCrossCacheQueryFailedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from \"G\".String");
        checkQueryFailedMetrics(cache, qry);
    }
}

