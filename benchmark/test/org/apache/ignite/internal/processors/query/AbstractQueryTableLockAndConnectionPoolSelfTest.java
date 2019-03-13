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


import java.util.Iterator;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.QueryRetryException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.util.typedef.X;
import org.junit.Test;


/**
 * Tests for query execution check cases for correct table lock/unlock.
 */
public abstract class AbstractQueryTableLockAndConnectionPoolSelfTest extends AbstractIndexingCommonTest {
    /**
     * Keys count.
     */
    private static final int KEY_CNT = 500;

    /**
     * Base query argument.
     */
    private static final int BASE_QRY_ARG = 50;

    /**
     * Size for small pages.
     */
    private static final int PAGE_SIZE_SMALL = 12;

    /**
     * Test duration.
     */
    private static final long TEST_DUR = 10000L;

    /**
     * Test local query execution.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleNode() throws Exception {
        checkSingleNode(1);
    }

    /**
     * Test local query execution.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleNodeWithParallelism() throws Exception {
        checkSingleNode(4);
    }

    /**
     * Test query execution with multiple topology nodes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleNodes() throws Exception {
        checkMultipleNodes(1);
    }

    /**
     * Test query execution with multiple topology nodes with query parallelism.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleNodesWithParallelism() throws Exception {
        checkMultipleNodes(4);
    }

    /**
     * Test DDL operation on table with high load queries.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleNodeTablesLockQueryAndDDLMultithreaded() throws Exception {
        final Ignite srv = startGrid(0);
        AbstractQueryTableLockAndConnectionPoolSelfTest.populateBaseQueryData(srv, 1);
        checkTablesLockQueryAndDDLMultithreaded(srv);
        checkTablesLockQueryAndDropColumnMultithreaded(srv);
    }

    /**
     * Test DDL operation on table with high load queries.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleNodeWithParallelismTablesLockQueryAndDDLMultithreaded() throws Exception {
        final Ignite srv = startGrid(0);
        AbstractQueryTableLockAndConnectionPoolSelfTest.populateBaseQueryData(srv, 4);
        checkTablesLockQueryAndDDLMultithreaded(srv);
        checkTablesLockQueryAndDropColumnMultithreaded(srv);
    }

    /**
     * Test DDL operation on table with high load queries.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleNodesWithTablesLockQueryAndDDLMultithreaded() throws Exception {
        Ignite srv0 = startGrid(0);
        Ignite srv1 = startGrid(1);
        startGrid(2);
        Ignite cli;
        try {
            Ignition.setClientMode(true);
            cli = startGrid(3);
        } finally {
            Ignition.setClientMode(false);
        }
        AbstractQueryTableLockAndConnectionPoolSelfTest.populateBaseQueryData(srv0, 1);
        checkTablesLockQueryAndDDLMultithreaded(srv0);
        checkTablesLockQueryAndDDLMultithreaded(srv1);
        checkTablesLockQueryAndDDLMultithreaded(cli);
        checkTablesLockQueryAndDropColumnMultithreaded(srv0);
        checkTablesLockQueryAndDropColumnMultithreaded(srv1);
        // TODO: +++ DDL DROP COLUMN CacheContext == null on CLI
        // checkTablesLockQueryAndDropColumnMultithreaded(cli);
    }

    /**
     * Test DDL operation on table with high load queries.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleNodesWithParallelismTablesLockQueryAndDDLMultithreaded() throws Exception {
        Ignite srv0 = startGrid(0);
        Ignite srv1 = startGrid(1);
        startGrid(2);
        Ignite cli;
        try {
            Ignition.setClientMode(true);
            cli = startGrid(3);
        } finally {
            Ignition.setClientMode(false);
        }
        AbstractQueryTableLockAndConnectionPoolSelfTest.populateBaseQueryData(srv0, 4);
        checkTablesLockQueryAndDDLMultithreaded(srv0);
        checkTablesLockQueryAndDDLMultithreaded(srv1);
        checkTablesLockQueryAndDDLMultithreaded(cli);
        checkTablesLockQueryAndDropColumnMultithreaded(srv0);
        checkTablesLockQueryAndDropColumnMultithreaded(srv1);
        // TODO: +++ DDL DROP COLUMN CacheContext == null on CLI
        // checkTablesLockQueryAndDropColumnMultithreaded(cli);
    }

    /**
     * Test release reserved partition after query complete (results is bigger than one page).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionReservationSeveralPagesResults() throws Exception {
        checkReleasePartitionReservation(AbstractQueryTableLockAndConnectionPoolSelfTest.PAGE_SIZE_SMALL);
    }

    /**
     * Test release reserved partition after query complete (results is placed on one page).
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionReservationOnePageResults() throws Exception {
        checkReleasePartitionReservation(AbstractQueryTableLockAndConnectionPoolSelfTest.KEY_CNT);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFetchFromRemovedTable() throws Exception {
        Ignite srv = startGrid(0);
        execute(srv, "CREATE TABLE TEST (id int primary key, val int)");
        for (int i = 0; i < 10; ++i)
            execute(srv, (((("INSERT INTO TEST VALUES (" + i) + ", ") + i) + ")"));

        FieldsQueryCursor<List<?>> cur = execute(srv, new SqlFieldsQuery("SELECT * from TEST").setPageSize(1));
        Iterator<List<?>> it = cur.iterator();
        it.next();
        execute(srv, "DROP TABLE TEST");
        try {
            while (it.hasNext())
                it.next();

            if (lazy())
                fail("Retry exception must be thrown");

        } catch (Exception e) {
            if (!(lazy())) {
                log.error("In lazy=false mode the query must be finished successfully", e);
                fail("In lazy=false mode the query must be finished successfully");
            } else
                assertNotNull(X.cause(e, QueryRetryException.class));

        }
    }

    /**
     * Person class.
     */
    private static class Person {
        /**
         * ID.
         */
        @QuerySqlField(index = true)
        private long id;

        /**
         * Name.
         */
        @QuerySqlField
        private String name;

        /**
         * Constructor.
         *
         * @param id
         * 		ID.
         */
        public Person(long id) {
            this.id = id;
            this.name = AbstractQueryTableLockAndConnectionPoolSelfTest.nameForId(id);
        }

        /**
         *
         *
         * @return ID.
         */
        public long id() {
            return id;
        }

        /**
         *
         *
         * @return Name.
         */
        public String name() {
            return name;
        }
    }

    /**
     * Company class.
     */
    private static class PersonTask {
        /**
         * ID.
         */
        @QuerySqlField(index = true)
        private long id;

        @QuerySqlField(index = true)
        private long persId;

        /**
         * Name.
         */
        @QuerySqlField
        private long time;

        /**
         * Constructor.
         *
         * @param id
         * 		ID.
         */
        public PersonTask(long id) {
            this.id = id;
            this.persId = id;
            this.time = id;
        }

        /**
         *
         *
         * @return ID.
         */
        public long id() {
            return id;
        }

        /**
         *
         *
         * @return Name.
         */
        public long time() {
            return time;
        }
    }
}

