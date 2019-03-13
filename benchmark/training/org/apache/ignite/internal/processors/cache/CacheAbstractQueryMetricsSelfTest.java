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
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for cache query metrics.
 */
public abstract class CacheAbstractQueryMetricsSelfTest extends GridCommonAbstractTest {
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
     * Test metrics for Sql queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlQueryMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlQuery qry = new SqlQuery("String", "from String");
        checkQueryMetrics(cache, qry);
    }

    /**
     * Test metrics for Sql queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlQueryNotFullyFetchedMetrics() throws Exception {
        IgniteCache<Integer, String> cache = grid(0).context().cache().jcache("A");
        SqlQuery qry = new SqlQuery("String", "from String");
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
        Collection<CacheAbstractQueryMetricsSelfTest.Worker> workers = new ArrayList<>();
        int repeat = 100;
        for (int i = 0; i < repeat; i++) {
            workers.add(new CacheAbstractQueryMetricsSelfTest.Worker(cache, new SqlFieldsQuery(("select * from String limit " + i))));
            workers.add(new CacheAbstractQueryMetricsSelfTest.Worker(cache, new SqlQuery("String", "from String")));
            workers.add(new CacheAbstractQueryMetricsSelfTest.Worker(cache, new ScanQuery()));
            workers.add(new CacheAbstractQueryMetricsSelfTest.Worker(cache, new TextQuery("String", "1")));
        }
        for (CacheAbstractQueryMetricsSelfTest.Worker worker : workers)
            worker.start();

        for (CacheAbstractQueryMetricsSelfTest.Worker worker : workers)
            worker.join();

        checkMetrics(cache, (repeat * 4), (repeat * 4), 0, false);
    }
}

