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


import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test lazy cache start on client nodes with inmemory cache.
 */
public class GridCacheDynamicLoadOnClientTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String PERSON_CACHE = "Person";

    /**
     * SQL schema name.
     */
    private static final String PERSON_SCHEMA = "test";

    /**
     * Number of element to add into cache.
     */
    private static final int CACHE_ELEMENT_COUNT = 10;

    /**
     * Full table name.
     */
    private static final String FULL_TABLE_NAME = ((GridCacheDynamicLoadOnClientTest.PERSON_SCHEMA) + ".") + (GridCacheDynamicLoadOnClientTest.PERSON_CACHE);

    /**
     * Client or server mode for configuration.
     */
    protected boolean client;

    /**
     * Instance of client node.
     */
    private static IgniteEx clientNode;

    /**
     * Instance of client node.
     */
    private static IgniteEx srvNode;

    /**
     * Test from client node batch merge through JDBC.
     *
     * @throws Exception
     * 		If failure.
     */
    @Test
    public void testBatchMerge() throws Exception {
        final int BATCH_SIZE = 7;
        try (Connection con = GridCacheDynamicLoadOnClientTest.connect(GridCacheDynamicLoadOnClientTest.clientNode);Statement stmt = con.createStatement()) {
            for (int idx = 0, i = 0; i < BATCH_SIZE; ++i , idx += i) {
                stmt.addBatch(((((((((((("merge into " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " (_key, name, orgId) values (") + (100 + idx)) + ",") + "'") + "batch-") + idx) + "'") + ",") + idx) + ")"));
            }
            int[] updCnts = stmt.executeBatch();
            assertEquals("Invalid update counts size", BATCH_SIZE, updCnts.length);
        }
    }

    /**
     * Test from client node to delete cache elements through JDBC.
     *
     * @throws Exception
     * 		If failure.
     */
    @Test
    public void testClientJdbcDelete() throws Exception {
        try (Connection con = GridCacheDynamicLoadOnClientTest.connect(GridCacheDynamicLoadOnClientTest.clientNode);Statement stmt = con.createStatement()) {
            int cnt = stmt.executeUpdate((("DELETE " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " WHERE _key=1"));
            Assert.assertEquals(1, cnt);
        }
        SqlFieldsQuery qry = new SqlFieldsQuery(("SELECT * FROM " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)));
        Assert.assertEquals(((GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT) - 1), getDefaultCacheOnClient().query(qry).getAll().size());
    }

    /**
     * Test from client node to insert into cache through JDBC.
     *
     * @throws Exception
     * 		If failure.
     */
    @Test
    public void testClientJdbcInsert() throws Exception {
        try (Connection con = GridCacheDynamicLoadOnClientTest.connect(GridCacheDynamicLoadOnClientTest.clientNode);Statement stmt = con.createStatement()) {
            int cnt = stmt.executeUpdate((("INSERT INTO " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + "(_key, name, orgId) VALUES(1000,'new_name', 10000)"));
            Assert.assertEquals(1, cnt);
        }
        SqlFieldsQuery qry = new SqlFieldsQuery(("SELECT * FROM " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)));
        Assert.assertEquals(((GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT) + 1), getDefaultCacheOnClient().query(qry).getAll().size());
    }

    /**
     * Test from client node to update cache elements through JDBC.
     *
     * @throws Exception
     * 		If failure.
     */
    @Test
    public void testClientJdbcUpdate() throws Exception {
        try (Connection con = GridCacheDynamicLoadOnClientTest.connect(GridCacheDynamicLoadOnClientTest.clientNode);Statement stmt = con.createStatement()) {
            int cnt = stmt.executeUpdate((("UPDATE " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " SET name = 'new_name'"));
            Assert.assertEquals(GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT, cnt);
        }
        SqlFieldsQuery qry = new SqlFieldsQuery((("SELECT * FROM " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " WHERE name = 'new_name'"));
        Assert.assertEquals(GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT, getDefaultCacheOnClient().query(qry).getAll().size());
    }

    /**
     * Test from client node to get cache elements through JDBC.
     *
     * @throws Exception
     * 		If failure.
     */
    @Test
    public void testClientJdbc() throws Exception {
        try (Connection con = GridCacheDynamicLoadOnClientTest.connect(GridCacheDynamicLoadOnClientTest.clientNode);Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(("SELECT count(*) FROM " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)));
            rs.next();
            Assert.assertEquals(GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT, rs.getInt(1));
        }
    }

    /**
     * Test from client node to put cache elements through cache API.
     */
    @Test
    public void testClientPut() {
        GridCacheDynamicLoadOnClientTest.clientNode.cache(GridCacheDynamicLoadOnClientTest.PERSON_CACHE).put((-100), new GridCacheDynamicLoadOnClientTest.Person((-100), "name-"));
        Assert.assertEquals(((GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT) + 1), GridCacheDynamicLoadOnClientTest.clientNode.cache(GridCacheDynamicLoadOnClientTest.PERSON_CACHE).size());
    }

    /**
     * Test DDL operation for not started cache on client node.
     */
    @Test
    public void testCreateIdxOnClient() {
        getDefaultCacheOnClient().query(new SqlFieldsQuery((("CREATE INDEX IDX_11 ON " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " (name asc)"))).getAll();
    }

    /**
     * Test DDL operation for not started cache on client node.
     */
    @Test
    public void testDropIdxOnClient() {
        GridCacheDynamicLoadOnClientTest.srvNode.getOrCreateCache(DEFAULT_CACHE_NAME).query(new SqlFieldsQuery((("CREATE INDEX IDX_TST ON " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)) + " (name desc)"))).getAll();
        // Due to client receive created index asynchronously we need add the ugly sleep.
        doSleep(2000);
        getDefaultCacheOnClient().query(new SqlFieldsQuery((("DROP INDEX " + (GridCacheDynamicLoadOnClientTest.PERSON_SCHEMA)) + ".IDX_TST"))).getAll();
    }

    /**
     * Test from client node to get cache elements through cache API.
     */
    @Test
    public void testClientSqlFieldsQuery() {
        SqlFieldsQuery qry = new SqlFieldsQuery(("SELECT * FROM " + (GridCacheDynamicLoadOnClientTest.FULL_TABLE_NAME)));
        Assert.assertEquals(GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT, getDefaultCacheOnClient().query(qry).getAll().size());
    }

    /**
     * Test from client node to get cache elements through cache API.
     */
    @Test
    public void testClientSqlQuery() {
        SqlQuery<Integer, GridCacheDynamicLoadOnClientTest.Person> qry = new SqlQuery(GridCacheDynamicLoadOnClientTest.PERSON_CACHE, ("FROM " + (GridCacheDynamicLoadOnClientTest.PERSON_CACHE)));
        Assert.assertEquals(GridCacheDynamicLoadOnClientTest.CACHE_ELEMENT_COUNT, GridCacheDynamicLoadOnClientTest.clientNode.getOrCreateCache(GridCacheDynamicLoadOnClientTest.PERSON_CACHE).query(qry).getAll().size());
    }

    /**
     * Test object to keep in cache.
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField
        int orgId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        Person(int orgId, String name) {
            this.orgId = orgId;
            this.name = name;
        }
    }
}

