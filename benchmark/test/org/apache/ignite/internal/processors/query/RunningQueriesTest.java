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


import Cache.Entry;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.testframework.GridTestUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;


/**
 * Tests for running queries.
 */
public class RunningQueriesTest extends AbstractIndexingCommonTest {
    /**
     * Timeout in sec.
     */
    private static final long TIMEOUT_IN_SEC = 5;

    /**
     * Timeout in sec.
     */
    private static final long TIMEOUT_IN_MS = (RunningQueriesTest.TIMEOUT_IN_SEC) * 1000;

    /**
     * Barrier.
     */
    private static volatile CyclicBarrier barrier;

    /**
     * Ignite.
     */
    private static IgniteEx ignite;

    /**
     * Node count.
     */
    private static final int NODE_CNT = 2;

    /**
     * Restarts the grid if if the last test failed.
     */
    @Rule
    public final TestWatcher restarter = new TestWatcher() {
        /**
         * {@inheritDoc }
         */
        @Override
        protected void failed(Throwable e, Description lastTest) {
            try {
                log().error((((("Last test failed [name=" + (lastTest.getMethodName())) + ", reason=") + (e.getMessage())) + "]. Restarting the grid."));
                // Release the indexing.
                if ((RunningQueriesTest.barrier) != null)
                    RunningQueriesTest.barrier.reset();

                stopAllGrids();
                beforeTestsStarted();
                log().error("Grid restarted.");
            } catch (Exception restartFailure) {
                throw new RuntimeException((((("Failed to recover after test failure [test=" + (lastTest.getMethodName())) + ", reason=") + (e.getMessage())) + "]. Subsequent test results of this test class are incorrect."), restartFailure);
            }
        }
    };

    /**
     * Check cleanup running queries on node stop.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testCloseRunningQueriesOnNodeStop() throws Exception {
        IgniteEx ign = startGrid(getConfiguration("TST"));
        IgniteCache<Integer, Integer> cache = ign.getOrCreateCache(new org.apache.ignite.configuration.CacheConfiguration<Integer, Integer>().setName("TST").setQueryEntities(Collections.singletonList(new QueryEntity(Integer.class, Integer.class))));
        for (int i = 0; i < 10000; i++)
            cache.put(i, i);

        cache.query(new SqlFieldsQuery("SELECT * FROM Integer order by _key"));
        Assert.assertEquals("Should be one running query", 1, ign.context().query().runningQueries((-1)).size());
        ign.close();
        Assert.assertEquals(0, ign.context().query().runningQueries((-1)).size());
    }

    /**
     * Check auto cleanup running queries on fully read iterator.
     */
    @SuppressWarnings("CodeBlock2Expr")
    @Test
    public void testAutoCloseQueryAfterIteratorIsExhausted() {
        IgniteCache<Object, Object> cache = RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME);
        for (int i = 0; i < 100; i++)
            cache.put(i, i);

        FieldsQueryCursor<List<?>> query = cache.query(new SqlFieldsQuery("SELECT * FROM Integer order by _key"));
        query.iterator().forEachRemaining(( e) -> {
            Assert.assertEquals("Should be one running query", 1, RunningQueriesTest.ignite.context().query().runningQueries((-1)).size());
        });
        assertNoRunningQueries();
    }

    /**
     * Check cluster wide query id generation.
     */
    @Test
    public void testClusterWideQueryIdGeneration() {
        RunningQueriesTest.newBarrier(1);
        IgniteCache<Object, Object> cache = RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME);
        for (int i = 0; i < 100; i++) {
            FieldsQueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery("SELECT * FROM Integer WHERE 1 = 1"));
            Collection<GridRunningQueryInfo> runningQueries = RunningQueriesTest.ignite.context().query().runningQueries((-1));
            assertEquals(1, runningQueries.size());
            GridRunningQueryInfo r = runningQueries.iterator().next();
            assertEquals((((RunningQueriesTest.ignite.context().localNodeId()) + "_") + (r.id())), r.globalQueryId());
            cursor.close();
        }
    }

    /**
     * Check tracking running queries for Select.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testQueries() throws Exception {
        RunningQueriesTest.newBarrier(3);
        IgniteCache<Object, Object> cache = RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME);
        IgniteInternalFuture<List<List<?>>> fut1 = GridTestUtils.runAsync(() -> cache.query(new SqlFieldsQuery("SELECT * FROM /* comment */ Integer WHERE 1 = 1")).getAll());
        IgniteInternalFuture<List<Entry<Integer, Integer>>> fut2 = GridTestUtils.runAsync(() -> cache.query(new SqlQuery<Integer, Integer>(.class, "FROM /* comment */ Integer WHERE 1 = 1")).getAll());
        Assert.assertTrue(GridTestUtils.waitForCondition(() -> (RunningQueriesTest.barrier.getNumberWaiting()) == 2, RunningQueriesTest.TIMEOUT_IN_MS));
        Collection<GridRunningQueryInfo> runningQueries = RunningQueriesTest.ignite.context().query().runningQueries((-1));
        assertEquals(2, runningQueries.size());
        for (GridRunningQueryInfo info : runningQueries)
            assertTrue(("Failed to find comment in query: " + (info.query())), info.query().contains("/* comment */"));

        assertNoRunningQueries(RunningQueriesTest.ignite);
        RunningQueriesTest.awaitTimeout();
        fut1.get(RunningQueriesTest.TIMEOUT_IN_MS);
        fut2.get(RunningQueriesTest.TIMEOUT_IN_MS);
    }

    /**
     * Check tracking running queries for DROP INDEX.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testQueryDdlDropIndex() throws Exception {
        RunningQueriesTest.newBarrier(1);
        RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("CREATE TABLE tst_idx_drop(id long PRIMARY KEY, cnt integer)"));
        RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("CREATE INDEX tst_idx_drop_idx ON tst_idx_drop(cnt)"));
        testQueryDDL("DROP INDEX tst_idx_drop_idx");
    }

    /**
     * Check tracking running queries for CREATE INDEX.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testQueryDdlCreateIndex() throws Exception {
        RunningQueriesTest.newBarrier(1);
        RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("CREATE TABLE tst_idx_create(id long PRIMARY KEY, cnt integer)"));
        testQueryDDL("CREATE INDEX tst_idx_create_idx ON tst_idx_create(cnt)");
    }

    /**
     * Check tracking running queries for DROP TABLE.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testQueryDdlDropTable() throws Exception {
        RunningQueriesTest.newBarrier(1);
        RunningQueriesTest.ignite.cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery("CREATE TABLE tst_drop(id long PRIMARY KEY, cnt integer)"));
        testQueryDDL("DROP TABLE tst_drop");
    }

    /**
     * Check tracking running queries for CREATE TABLE.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testQueryDdlCreateTable() throws Exception {
        testQueryDDL("CREATE TABLE tst_create(id long PRIMARY KEY, cnt integer)");
    }

    /**
     * Check tracking running queries for batches.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testJdbcBatchDML() throws Exception {
        RunningQueriesTest.newBarrier(2);
        try (Connection conn = GridTestUtils.connect(RunningQueriesTest.ignite, null);Statement stmt = conn.createStatement()) {
            conn.setSchema("\"default\"");
            final int BATCH_SIZE = 10;
            int key = 0;
            for (int i = 0; i < BATCH_SIZE; i++) {
                while (RunningQueriesTest.ignite.affinity(DEFAULT_CACHE_NAME).isPrimary(RunningQueriesTest.ignite.localNode(), key))
                    key++;

                stmt.addBatch((((("insert into Integer (_key, _val) values (" + key) + ",") + key) + ")"));
                key++;
            }
            IgniteInternalFuture<int[]> fut = GridTestUtils.runAsync(stmt::executeBatch);
            for (int i = 0; i < BATCH_SIZE; i++) {
                assertWaitingOnBarrier();
                Collection<GridRunningQueryInfo> runningQueries = RunningQueriesTest.ignite.context().query().runningQueries((-1));
                assertEquals(1, runningQueries.size());
                RunningQueriesTest.awaitTimeout();
                assertWaitingOnBarrier();
                RunningQueriesTest.awaitTimeout();
            }
            fut.get(RunningQueriesTest.TIMEOUT_IN_MS);
        }
    }

    /**
     * Check tracking running queries for multi-statements.
     *
     * @throws Exception
     * 		Exception in case of failure.
     */
    @Test
    public void testMultiStatement() throws Exception {
        RunningQueriesTest.newBarrier(2);
        int key = 0;
        int[] notAffinityKey = new int[2];
        for (int i = 0; i < (notAffinityKey.length); i++) {
            while (RunningQueriesTest.ignite.affinity(DEFAULT_CACHE_NAME).isPrimary(RunningQueriesTest.ignite.localNode(), key))
                key++;

            notAffinityKey[i] = key;
            key++;
        }
        String[] queries = new String[]{ "create table test(ID int primary key, NAME varchar(20))", ("insert into test (ID, NAME) values (" + (notAffinityKey[0])) + ", 'name')", ("insert into test (ID, NAME) values (" + (notAffinityKey[1])) + ", 'name')", "SELECT * FROM test" };
        String sql = String.join(";", queries);
        try (Connection conn = GridTestUtils.connect(RunningQueriesTest.ignite, null);Statement stmt = conn.createStatement()) {
            IgniteInternalFuture<Boolean> fut = GridTestUtils.runAsync(() -> stmt.execute(sql));
            for (String query : queries) {
                assertWaitingOnBarrier();
                List<GridRunningQueryInfo> runningQueries = ((List<GridRunningQueryInfo>) (RunningQueriesTest.ignite.context().query().runningQueries((-1))));
                assertEquals(1, runningQueries.size());
                assertEquals(query, runningQueries.get(0).query());
                RunningQueriesTest.awaitTimeout();
            }
            fut.get(RunningQueriesTest.TIMEOUT_IN_MS);
        }
    }

    /**
     * Check tracking running queries for stream COPY command.
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testCopyCommand() throws Exception {
        try (Connection conn = GridTestUtils.connect(RunningQueriesTest.ignite, null);Statement stmt = conn.createStatement()) {
            conn.setSchema("\"default\"");
            RunningQueriesTest.newBarrier(1);
            stmt.execute("CREATE TABLE Person(id integer primary key, age integer, firstName varchar, lastname varchar)");
            String path = Objects.requireNonNull(resolveIgnitePath("/modules/clients/src/test/resources/bulkload1.csv")).getAbsolutePath();
            RunningQueriesTest.newBarrier(2);
            String sql = (((("copy from '" + path) + "'") + " into Person") + " (_key, age, firstName, lastName)") + " format csv charset 'ascii'";
            IgniteInternalFuture<Integer> fut = GridTestUtils.runAsync(() -> stmt.executeUpdate(sql));
            assertWaitingOnBarrier();
            List<GridRunningQueryInfo> runningQueries = ((List<GridRunningQueryInfo>) (RunningQueriesTest.ignite.context().query().runningQueries((-1))));
            assertEquals(1, runningQueries.size());
            assertEquals(sql, runningQueries.get(0).query());
            RunningQueriesTest.awaitTimeout();
            fut.get(RunningQueriesTest.TIMEOUT_IN_MS);
        }
    }

    /**
     * Blocking indexing processor.
     */
    private static class BlockingIndexing extends IgniteH2Indexing {
        /**
         * {@inheritDoc }
         */
        @Override
        public List<FieldsQueryCursor<List<?>>> querySqlFields(String schemaName, SqlFieldsQuery qry, @Nullable
        SqlClientContext cliCtx, boolean keepBinary, boolean failOnMultipleStmts, GridQueryCancel cancel) {
            List<FieldsQueryCursor<List<?>>> res = super.querySqlFields(schemaName, qry, cliCtx, keepBinary, failOnMultipleStmts, cancel);
            try {
                RunningQueriesTest.awaitTimeout();
            } catch (Exception e) {
                throw new IgniteException(e);
            }
            return res;
        }
    }
}

