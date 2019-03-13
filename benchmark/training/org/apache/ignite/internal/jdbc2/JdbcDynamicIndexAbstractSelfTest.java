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
package org.apache.ignite.internal.jdbc2;


import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Test that checks indexes handling with JDBC.
 */
public abstract class JdbcDynamicIndexAbstractSelfTest extends JdbcAbstractDmlStatementSelfTest {
    /**
     *
     */
    private static final String CREATE_INDEX = "create index idx on Person (id desc)";

    /**
     *
     */
    private static final String DROP_INDEX = "drop index idx";

    /**
     *
     */
    private static final String CREATE_INDEX_IF_NOT_EXISTS = "create index if not exists idx on Person (id desc)";

    /**
     *
     */
    private static final String DROP_INDEX_IF_EXISTS = "drop index idx if exists";

    /**
     * Test that after index creation index is used by queries.
     */
    @Test
    public void testCreateIndex() throws SQLException {
        assertSize(3);
        assertColumnValues(30, 20, 10);
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
        // Test that local queries on all server nodes use new index.
        for (int i = 0; i < 3; i++) {
            List<List<?>> locRes = ignite(i).cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery(("explain select id from " + "Person where id = 5")).setLocal(true)).getAll();
            assertEquals(F.asList(Collections.singletonList(("SELECT\n" + ((("    ID\n" + "FROM \"default\".PERSON\n") + "    /* \"default\".IDX: ID = 5 */\n") + "WHERE ID = 5")))), locRes);
        }
        assertSize(3);
        assertColumnValues(30, 20, 10);
    }

    /**
     * Test that creating an index with duplicate name yields an error.
     */
    @Test
    public void testCreateIndexWithDuplicateName() throws SQLException {
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
        JdbcDynamicIndexAbstractSelfTest.assertSqlException(new JdbcDynamicIndexAbstractSelfTest.RunnableX() {
            /**
             * {@inheritDoc }
             */
            @Override
            public void run() throws Exception {
                jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
            }
        });
    }

    /**
     * Test that creating an index with duplicate name does not yield an error with {@code IF NOT EXISTS}.
     */
    @Test
    public void testCreateIndexIfNotExists() throws SQLException {
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
        // Despite duplicate name, this does not yield an error.
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX_IF_NOT_EXISTS);
    }

    /**
     * Test that after index drop there are no attempts to use it, and data state remains intact.
     */
    @Test
    public void testDropIndex() throws SQLException {
        assertSize(3);
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
        assertSize(3);
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.DROP_INDEX);
        // Test that no local queries on server nodes use new index.
        for (int i = 0; i < 3; i++) {
            List<List<?>> locRes = ignite(i).cache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery(("explain select id from " + "Person where id = 5")).setLocal(true)).getAll();
            assertEquals(F.asList(Collections.singletonList(("SELECT\n" + ((("    ID\n" + "FROM \"default\".PERSON\n") + "    /* \"default\".PERSON.__SCAN_ */\n") + "WHERE ID = 5")))), locRes);
        }
        assertSize(3);
    }

    /**
     * Test that dropping a non-existent index yields an error.
     */
    @Test
    public void testDropMissingIndex() {
        JdbcDynamicIndexAbstractSelfTest.assertSqlException(new JdbcDynamicIndexAbstractSelfTest.RunnableX() {
            /**
             * {@inheritDoc }
             */
            @Override
            public void run() throws Exception {
                jdbcRun(JdbcDynamicIndexAbstractSelfTest.DROP_INDEX);
            }
        });
    }

    /**
     * Test that dropping a non-existent index does not yield an error with {@code IF EXISTS}.
     */
    @Test
    public void testDropMissingIndexIfExists() throws SQLException {
        // Despite index missing, this does not yield an error.
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.DROP_INDEX_IF_EXISTS);
    }

    /**
     * Test that changes in cache affect index, and vice versa.
     */
    @Test
    public void testIndexState() throws SQLException {
        IgniteCache<String, JdbcAbstractDmlStatementSelfTest.Person> cache = cache();
        assertSize(3);
        assertColumnValues(30, 20, 10);
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.CREATE_INDEX);
        assertSize(3);
        assertColumnValues(30, 20, 10);
        cache.remove("m");
        assertColumnValues(30, 10);
        cache.put("a", new JdbcAbstractDmlStatementSelfTest.Person(4, "someVal", "a", 5));
        assertColumnValues(5, 30, 10);
        jdbcRun(JdbcDynamicIndexAbstractSelfTest.DROP_INDEX);
        assertColumnValues(5, 30, 10);
    }

    /**
     * Runnable which can throw checked exceptions.
     */
    private interface RunnableX {
        /**
         * Do run.
         *
         * @throws Exception
         * 		If failed.
         */
        public void run() throws Exception;
    }
}

