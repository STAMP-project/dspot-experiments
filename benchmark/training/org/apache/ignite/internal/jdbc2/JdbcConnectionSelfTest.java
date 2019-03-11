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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Connection test.
 */
public class JdbcConnectionSelfTest extends GridCommonAbstractTest {
    /**
     * Custom cache name.
     */
    private static final String CUSTOM_CACHE_NAME = "custom-cache";

    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     * Daemon node flag.
     */
    private boolean daemon;

    /**
     * Client node flag.
     */
    private boolean client;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefaults() throws Exception {
        String url = (IgniteJdbcDriver.CFG_URL_PREFIX) + (configURL());
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
            assertTrue(ignite().configuration().isClientMode());
        }
        try (Connection conn = DriverManager.getConnection((url + '/'))) {
            assertNotNull(conn);
            assertTrue(ignite().configuration().isClientMode());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeId() throws Exception {
        String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "nodeId=") + (grid(0).localNode().id())) + '@') + (configURL());
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
        }
        url = ((((((IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=") + (JdbcConnectionSelfTest.CUSTOM_CACHE_NAME)) + ":nodeId=") + (grid(0).localNode().id())) + '@') + (configURL());
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWrongNodeId() throws Exception {
        UUID wrongId = UUID.randomUUID();
        final String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "nodeId=") + wrongId) + '@') + (configURL());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (Connection conn = DriverManager.getConnection(url)) {
                    return conn;
                }
            }
        }, SQLException.class, ("Failed to establish connection with node (is it a server node?): " + wrongId));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientNodeId() throws Exception {
        client = true;
        IgniteEx client = ((IgniteEx) (startGrid()));
        UUID clientId = client.localNode().id();
        final String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "nodeId=") + clientId) + '@') + (configURL());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (Connection conn = DriverManager.getConnection(url)) {
                    return conn;
                }
            }
        }, SQLException.class, ("Failed to establish connection with node (is it a server node?): " + clientId));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDaemonNodeId() throws Exception {
        daemon = true;
        IgniteEx daemon = startGrid(JdbcConnectionSelfTest.GRID_CNT);
        UUID daemonId = daemon.localNode().id();
        final String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "nodeId=") + daemonId) + '@') + (configURL());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (Connection conn = DriverManager.getConnection(url)) {
                    return conn;
                }
            }
        }, SQLException.class, ("Failed to establish connection with node (is it a server node?): " + daemonId));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCustomCache() throws Exception {
        String url = ((((IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=") + (JdbcConnectionSelfTest.CUSTOM_CACHE_NAME)) + '@') + (configURL());
        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWrongCache() throws Exception {
        final String url = ((IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=wrongCacheName@") + (configURL());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try (Connection conn = DriverManager.getConnection(url)) {
                    return conn;
                }
            }
        }, SQLException.class, "Client is invalid. Probably cache name is wrong.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClose() throws Exception {
        String url = (IgniteJdbcDriver.CFG_URL_PREFIX) + (configURL());
        try (final Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            conn.close();
            assertTrue(conn.isClosed());
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.isValid(2);
                    return null;
                }
            }, SQLException.class, "Connection is closed.");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTxAllowedCommit() throws Exception {
        String url = ((IgniteJdbcDriver.CFG_URL_PREFIX) + "transactionsAllowed=true@") + (configURL());
        try (final Connection conn = DriverManager.getConnection(url)) {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            conn.commit();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTxAllowedRollback() throws Exception {
        String url = ((IgniteJdbcDriver.CFG_URL_PREFIX) + "transactionsAllowed=true@") + (configURL());
        try (final Connection conn = DriverManager.getConnection(url)) {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
            conn.setAutoCommit(false);
            conn.rollback();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlHints() throws Exception {
        try (final Connection conn = DriverManager.getConnection((((IgniteJdbcDriver.CFG_URL_PREFIX) + "enforceJoinOrder=true@") + (configURL())))) {
            assertTrue(isEnforceJoinOrder());
            assertFalse(isDistributedJoins());
            assertFalse(isCollocatedQuery());
            assertFalse(isLazy());
            assertFalse(skipReducerOnUpdate());
        }
        try (final Connection conn = DriverManager.getConnection((((IgniteJdbcDriver.CFG_URL_PREFIX) + "distributedJoins=true@") + (configURL())))) {
            assertFalse(isEnforceJoinOrder());
            assertTrue(isDistributedJoins());
            assertFalse(isCollocatedQuery());
            assertFalse(isLazy());
            assertFalse(skipReducerOnUpdate());
        }
        try (final Connection conn = DriverManager.getConnection((((IgniteJdbcDriver.CFG_URL_PREFIX) + "collocated=true@") + (configURL())))) {
            assertFalse(isEnforceJoinOrder());
            assertFalse(isDistributedJoins());
            assertTrue(isCollocatedQuery());
            assertFalse(isLazy());
            assertFalse(skipReducerOnUpdate());
        }
        try (final Connection conn = DriverManager.getConnection((((IgniteJdbcDriver.CFG_URL_PREFIX) + "lazy=true@") + (configURL())))) {
            assertFalse(isEnforceJoinOrder());
            assertFalse(isDistributedJoins());
            assertFalse(isCollocatedQuery());
            assertTrue(isLazy());
            assertFalse(skipReducerOnUpdate());
        }
        try (final Connection conn = DriverManager.getConnection((((IgniteJdbcDriver.CFG_URL_PREFIX) + "skipReducerOnUpdate=true@") + (configURL())))) {
            assertFalse(isEnforceJoinOrder());
            assertFalse(isDistributedJoins());
            assertFalse(isCollocatedQuery());
            assertFalse(isLazy());
            assertTrue(skipReducerOnUpdate());
        }
    }
}

