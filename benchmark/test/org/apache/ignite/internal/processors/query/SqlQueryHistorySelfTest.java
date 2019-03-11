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
package org.apache.ignite.internal.processors.query;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Check query history metrics from server node.
 */
public class SqlQueryHistorySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int QUERY_HISTORY_SIZE = 3;

    /**
     *
     */
    private static TcpDiscoveryIpFinder ipFinder = new TcpDiscoveryVmIpFinder(true);

    /**
     * Test metrics for JDBC.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testJdbcSelectQueryHistory() throws Exception {
        String qry = "select * from A.String";
        checkQueryMetrics(qry);
    }

    /**
     * Test metrics for JDBC in case not fully resultset is not fully read.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testJdbcSelectNotFullyFetchedQueryHistory() throws Exception {
        String qry = "select * from A.String";
        try (Connection conn = GridTestUtils.connect(queryNode(), null);Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1);
            ResultSet rs = stmt.executeQuery(qry);
            assertTrue(rs.next());
            checkMetrics(0, 0, 0, 0, true);
        }
    }

    /**
     * Test metrics for failed SQL queries.
     */
    @Test
    public void testJdbcQueryHistoryFailed() {
        try (Connection conn = GridTestUtils.connect(queryNode(), null);Statement stmt = conn.createStatement()) {
            stmt.executeQuery("select * from A.String where A.fail()=1");
            fail("Query should be failed.");
        } catch (Exception ignore) {
            // No-Op
        }
        checkMetrics(1, 0, 1, 1, true);
    }

    /**
     * Test metrics for JDBC in case of DDL and DML
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testJdbcQueryHistoryForDmlAndDdl() throws Exception {
        List<String> cmds = Arrays.asList("create table TST(id int PRIMARY KEY, name varchar)", "insert into TST(id) values(1)", "commit");
        try (Connection conn = GridTestUtils.connect(queryNode(), null);Statement stmt = conn.createStatement()) {
            for (String cmd : cmds)
                stmt.execute(cmd);

        }
        checkSeriesCommand(cmds);
    }

    /**
     * Test metrics for SQL fields queries.
     */
    @Test
    public void testSqlFieldsQueryHistory() {
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from String");
        checkQueryMetrics(qry);
    }

    /**
     * Test metrics for SQL fields queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsQueryHistoryNotFullyFetched() throws Exception {
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(qry, false);
    }

    /**
     * Test metrics for failed SQL queries.
     */
    @Test
    public void testSqlFieldsQueryHistoryFailed() {
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from String where fail()=1");
        checkQueryFailedMetrics(qry);
    }

    /**
     * Test metrics eviction.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testQueryHistoryForDmlAndDdl() throws Exception {
        IgniteCache<Integer, String> cache = queryNode().context().cache().jcache("A");
        List<String> cmds = Arrays.asList("create table TST(id int PRIMARY KEY, name varchar)", "insert into TST(id) values(1)", "commit");
        cmds.forEach(( cmd) -> cache.query(new SqlFieldsQuery(cmd)).getAll());
        checkSeriesCommand(cmds);
    }

    /**
     * Test metrics eviction.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testQueryHistoryEviction() throws Exception {
        IgniteCache<Integer, String> cache = queryNode().context().cache().jcache("A");
        cache.query(new SqlFieldsQuery("select * from String")).getAll();
        cache.query(new SqlFieldsQuery("select count(*) from String")).getAll();
        cache.query(new SqlFieldsQuery("select * from String limit 1")).getAll();
        cache.query(new SqlFieldsQuery("select * from String limit 2")).getAll();
        cache.query(new SqlQuery("String", "from String")).getAll();
        waitingFor("size", SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE);
        for (int i = 0; i < (SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE); i++)
            checkMetrics(SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE, i, 1, 0, false);

        // Check that collected metrics contains correct items: metrics for last N queries.
        Collection<QueryHistoryMetrics> metrics = runningQueryManager().queryHistoryMetrics().values();
        assertEquals(SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE, metrics.size());
        Set<String> qries = metrics.stream().map(QueryHistoryMetrics::query).collect(Collectors.toSet());
        assertTrue(qries.contains("SELECT \"A\".\"STRING\"._KEY, \"A\".\"STRING\"._VAL from String"));
        assertTrue(qries.contains("select * from String limit 2"));
        assertTrue(qries.contains("select * from String limit 1"));
    }

    /**
     * Test metrics if queries executed from several threads.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testQueryHistoryMultithreaded() throws Exception {
        IgniteCache<Integer, String> cache = queryNode().context().cache().jcache("A");
        Collection<SqlQueryHistorySelfTest.Worker> workers = new ArrayList<>();
        int repeat = 10;
        for (int k = 0; k < repeat; k++) {
            // Execute as match queries as history size to avoid eviction.
            for (int i = 1; i <= (SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE); i++)
                workers.add(new SqlQueryHistorySelfTest.Worker(cache, new SqlFieldsQuery(("select * from String limit " + i))));

        }
        for (SqlQueryHistorySelfTest.Worker worker : workers)
            worker.start();

        for (SqlQueryHistorySelfTest.Worker worker : workers)
            worker.join();

        for (int i = 0; i < (SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE); i++)
            checkMetrics(SqlQueryHistorySelfTest.QUERY_HISTORY_SIZE, i, repeat, 0, false);

    }

    /**
     * Test metrics for Scan queries.
     */
    @Test
    public void testScanQueryHistory() {
        ScanQuery<Integer, String> qry = new ScanQuery();
        checkNoQueryMetrics(qry);
    }

    /**
     * Test metrics for Scan queries.
     */
    @Test
    public void testSqlQueryHistory() {
        SqlQuery<Integer, String> qry = new SqlQuery("String", "from String");
        checkQueryMetrics(qry);
    }

    /**
     * Test metrics for Scan queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlQueryHistoryNotFullyFetched() throws Exception {
        SqlQuery<Integer, String> qry = new SqlQuery("String", "from String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(qry, true);
    }

    /**
     * Test metrics for Sql queries.
     */
    @Test
    public void testTextQueryMetrics() {
        TextQuery qry = new TextQuery("String", "1");
        checkNoQueryMetrics(qry);
    }

    /**
     * Test metrics for Sql queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryHistoryNotFullyFetched() throws Exception {
        TextQuery qry = new TextQuery("String", "1");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(qry, true);
    }

    /**
     * Test metrics for SQL cross cache queries.
     */
    @Test
    public void testSqlFieldsCrossCacheQueryHistory() {
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from \"B\".String");
        checkQueryMetrics(qry);
    }

    /**
     * Test metrics for SQL cross cache queries.
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testSqlFieldsQueryHistoryCrossCacheQueryNotFullyFetched() throws Exception {
        SqlFieldsQuery qry = new SqlFieldsQuery("select * from \"B\".String");
        qry.setPageSize(10);
        checkQueryNotFullyFetchedMetrics(qry, false);
    }

    /**
     *
     */
    public static class Functions {
        /**
         *
         */
        @QuerySqlFunction
        public static int fail() {
            throw new IgniteSQLException("SQL function fail for test purpuses");
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
}

