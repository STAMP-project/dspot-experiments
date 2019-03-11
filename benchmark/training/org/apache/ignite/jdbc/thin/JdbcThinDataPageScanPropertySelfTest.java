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
package org.apache.ignite.jdbc.thin;


import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.processors.query.GridQueryCancel;
import org.apache.ignite.internal.processors.query.SqlClientContext;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Check that data page scan property defined in the thin driver correctly passed to Indexing.
 */
public class JdbcThinDataPageScanPropertySelfTest extends GridCommonAbstractTest {
    /**
     * Batch size for streaming/batch mode.
     */
    private static final int BATCH_SIZE = 10;

    /**
     * How many queries to execute in total in case bach/streaming mode. Should be greater than batch size.
     */
    private static final int TOTAL_QUERIES_TO_EXECUTE = 25;

    /**
     * Initial size of the test table.
     */
    private static final int INITIAL_ROWS_CNT = 100;

    /**
     * Verify single queries.
     */
    @Test
    public void testDataPageScanSingle() throws Exception {
        checkDataPageScan("SELECT * FROM TEST WHERE val > 42", null);
        checkDataPageScan("UPDATE TEST SET val = val + 1 WHERE val > 10", null);
        checkDataPageScan("SELECT id FROM TEST WHERE val < 3", true);
        checkDataPageScan("UPDATE TEST SET val = val + 3 WHERE val < 3", true);
        checkDataPageScan("SELECT val FROM TEST WHERE id = 5", false);
        checkDataPageScan("UPDATE TEST SET val = val - 5 WHERE val < 100", false);
    }

    /**
     * Verify the case property is set on connection and batched operations are performed.
     */
    @Test
    public void testDataPageScanBatching() throws Exception {
        checkDataPageScanInBatch("UPDATE TEST SET val = ? WHERE val > 10", null);
        checkDataPageScanInBatch("UPDATE TEST SET val = val + 3 WHERE val < ?", true);
        checkDataPageScanInBatch("UPDATE TEST SET val = val - 5 WHERE val < ?", false);
    }

    /**
     * Indexing that remembers all the sql fields queries have been executed.
     */
    private static class IndexingWithQueries extends IgniteH2Indexing {
        /**
         * All the queries that have been executed using this indexing.
         */
        static final Queue<SqlFieldsQuery> queries = new LinkedBlockingQueue<>();

        /**
         * {@inheritDoc }
         */
        @Override
        public List<FieldsQueryCursor<List<?>>> querySqlFields(String schemaName, SqlFieldsQuery qry, @Nullable
        SqlClientContext cliCtx, boolean keepBinary, boolean failOnMultipleStmts, GridQueryCancel cancel) {
            JdbcThinDataPageScanPropertySelfTest.IndexingWithQueries.queries.add(qry);
            return super.querySqlFields(schemaName, qry, cliCtx, keepBinary, failOnMultipleStmts, cancel);
        }
    }
}

