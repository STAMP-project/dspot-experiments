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


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.jdbc.thin.ConnectionProperties;
import org.apache.ignite.internal.jdbc.thin.ConnectionPropertiesImpl;
import org.apache.ignite.internal.jdbc.thin.JdbcThinTcpIo;
import org.apache.ignite.internal.util.HostAndPortRange;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Connection test.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinConnectionSelfTest extends JdbcThinAbstractSelfTest {
    /**
     *
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1";

    /**
     * Client key store path.
     */
    private static final String CLI_KEY_STORE_PATH = (U.getIgniteHome()) + "/modules/clients/src/test/keystore/client.jks";

    /**
     * Server key store path.
     */
    private static final String SRV_KEY_STORE_PATH = (U.getIgniteHome()) + "/modules/clients/src/test/keystore/server.jks";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "EmptyTryBlock", "unused" })
    @Test
    public void testDefaults() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            // No-op.
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/")) {
            // No-op.
        }
    }

    /**
     * Test invalid endpoint.
     */
    @Test
    public void testInvalidEndpoint() {
        assertInvalid("jdbc:ignite:thin://", "Host name is empty");
        assertInvalid("jdbc:ignite:thin://:10000", "Host name is empty");
        assertInvalid("jdbc:ignite:thin://     :10000", "Host name is empty");
        assertInvalid("jdbc:ignite:thin://127.0.0.1:-1", "port range contains invalid port -1");
        assertInvalid("jdbc:ignite:thin://127.0.0.1:0", "port range contains invalid port 0");
        assertInvalid("jdbc:ignite:thin://127.0.0.1:100000", "port range contains invalid port 100000");
    }

    /**
     * Test invalid socket buffer sizes.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSocketBuffers() throws Exception {
        final int dfltDufSize = 64 * 1024;
        assertInvalid("jdbc:ignite:thin://127.0.0.1?socketSendBuffer=-1", "Property cannot be lower than 0 [name=socketSendBuffer, value=-1]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1?socketReceiveBuffer=-1", "Property cannot be lower than 0 [name=socketReceiveBuffer, value=-1]");
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
        // Note that SO_* options are hints, so we check that value is equals to either what we set or to default.
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?socketSendBuffer=1024")) {
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?socketReceiveBuffer=1024")) {
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
        try (Connection conn = DriverManager.getConnection(("jdbc:ignite:thin://127.0.0.1?" + "socketSendBuffer=1024&socketReceiveBuffer=2048"))) {
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(2048, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
    }

    /**
     * Test invalid socket buffer sizes with semicolon.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSocketBuffersSemicolon() throws Exception {
        final int dfltDufSize = 64 * 1024;
        assertInvalid("jdbc:ignite:thin://127.0.0.1;socketSendBuffer=-1", "Property cannot be lower than 0 [name=socketSendBuffer, value=-1]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1;socketReceiveBuffer=-1", "Property cannot be lower than 0 [name=socketReceiveBuffer, value=-1]");
        // Note that SO_* options are hints, so we check that value is equals to either what we set or to default.
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;socketSendBuffer=1024")) {
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;socketReceiveBuffer=1024")) {
            assertEquals(dfltDufSize, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
        try (Connection conn = DriverManager.getConnection(("jdbc:ignite:thin://127.0.0.1;" + "socketSendBuffer=1024;socketReceiveBuffer=2048"))) {
            assertEquals(1024, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketSendBuffer());
            assertEquals(2048, JdbcThinConnectionSelfTest.io(conn).connectionProperties().getSocketReceiveBuffer());
        }
    }

    /**
     * Test SQL hints.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlHints() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            assertHints(conn, false, false, false, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?distributedJoins=true")) {
            assertHints(conn, true, false, false, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?enforceJoinOrder=true")) {
            assertHints(conn, false, true, false, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?collocated=true")) {
            assertHints(conn, false, false, true, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?replicatedOnly=true")) {
            assertHints(conn, false, false, false, true, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?lazy=true")) {
            assertHints(conn, false, false, false, false, true, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?skipReducerOnUpdate=true")) {
            assertHints(conn, false, false, false, false, false, true);
        }
        try (Connection conn = DriverManager.getConnection(("jdbc:ignite:thin://127.0.0.1?distributedJoins=true&" + "enforceJoinOrder=true&collocated=true&replicatedOnly=true&lazy=true&skipReducerOnUpdate=true"))) {
            assertHints(conn, true, true, true, true, true, true);
        }
    }

    /**
     * Test SQL hints with semicolon.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlHintsSemicolon() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;distributedJoins=true")) {
            assertHints(conn, true, false, false, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;enforceJoinOrder=true")) {
            assertHints(conn, false, true, false, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;collocated=true")) {
            assertHints(conn, false, false, true, false, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;replicatedOnly=true")) {
            assertHints(conn, false, false, false, true, false, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;lazy=true")) {
            assertHints(conn, false, false, false, false, true, false);
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;skipReducerOnUpdate=true")) {
            assertHints(conn, false, false, false, false, false, true);
        }
        try (Connection conn = DriverManager.getConnection(("jdbc:ignite:thin://127.0.0.1;distributedJoins=true;" + "enforceJoinOrder=true;collocated=true;replicatedOnly=true;lazy=true;skipReducerOnUpdate=true"))) {
            assertHints(conn, true, true, true, true, true, true);
        }
    }

    /**
     * Test TCP no delay property handling.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTcpNoDelay() throws Exception {
        assertInvalid("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=0", "Invalid property value. [name=tcpNoDelay, val=0, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=1", "Invalid property value. [name=tcpNoDelay, val=1, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=false1", "Invalid property value. [name=tcpNoDelay, val=false1, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=true1", "Invalid property value. [name=tcpNoDelay, val=true1, choices=[true, false]]");
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=true")) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=True")) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=false")) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1?tcpNoDelay=False")) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
    }

    /**
     * Test TCP no delay property handling with semicolon.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTcpNoDelaySemicolon() throws Exception {
        assertInvalid("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=0", "Invalid property value. [name=tcpNoDelay, val=0, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=1", "Invalid property value. [name=tcpNoDelay, val=1, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=false1", "Invalid property value. [name=tcpNoDelay, val=false1, choices=[true, false]]");
        assertInvalid("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=true1", "Invalid property value. [name=tcpNoDelay, val=true1, choices=[true, false]]");
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=true")) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=True")) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=false")) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;tcpNoDelay=False")) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isTcpNoDelay());
        }
    }

    /**
     * Test autoCloseServerCursor property handling.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAutoCloseServerCursorProperty() throws Exception {
        String url = "jdbc:ignite:thin://127.0.0.1?autoCloseServerCursor";
        String err = "Invalid property value. [name=autoCloseServerCursor";
        assertInvalid((url + "=0"), err);
        assertInvalid((url + "=1"), err);
        assertInvalid((url + "=false1"), err);
        assertInvalid((url + "=true1"), err);
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=true"))) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=True"))) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=false"))) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=False"))) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
    }

    /**
     * Test autoCloseServerCursor property handling with semicolon.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAutoCloseServerCursorPropertySemicolon() throws Exception {
        String url = "jdbc:ignite:thin://127.0.0.1;autoCloseServerCursor";
        String err = "Invalid property value. [name=autoCloseServerCursor";
        assertInvalid((url + "=0"), err);
        assertInvalid((url + "=1"), err);
        assertInvalid((url + "=false1"), err);
        assertInvalid((url + "=true1"), err);
        try (Connection conn = DriverManager.getConnection((url + "=true"))) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=True"))) {
            assertTrue(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=false"))) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
        try (Connection conn = DriverManager.getConnection((url + "=False"))) {
            assertFalse(JdbcThinConnectionSelfTest.io(conn).connectionProperties().isAutoCloseServerCursor());
        }
    }

    /**
     * Test schema property in URL.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchema() throws Exception {
        assertInvalid("jdbc:ignite:thin://127.0.0.1/qwe/qwe", "Invalid URL format (only schema name is allowed in URL path parameter 'host:port[/schemaName]')");
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/public")) {
            assertEquals("Invalid schema", "PUBLIC", conn.getSchema());
        }
        try (Connection conn = DriverManager.getConnection((("jdbc:ignite:thin://127.0.0.1/\"" + (DEFAULT_CACHE_NAME)) + '"'))) {
            assertEquals("Invalid schema", DEFAULT_CACHE_NAME, conn.getSchema());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/_not_exist_schema_")) {
            assertEquals("Invalid schema", "_NOT_EXIST_SCHEMA_", conn.getSchema());
        }
    }

    /**
     * Test schema property in URL with semicolon.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSchemaSemicolon() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;schema=public")) {
            assertEquals("Invalid schema", "PUBLIC", conn.getSchema());
        }
        try (Connection conn = DriverManager.getConnection((("jdbc:ignite:thin://127.0.0.1;schema=\"" + (DEFAULT_CACHE_NAME)) + '"'))) {
            assertEquals("Invalid schema", DEFAULT_CACHE_NAME, conn.getSchema());
        }
        try (Connection conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1;schema=_not_exist_schema_")) {
            assertEquals("Invalid schema", "_NOT_EXIST_SCHEMA_", conn.getSchema());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testClose() throws Exception {
        final Connection conn;
        try (Connection conn0 = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1")) {
            conn = conn0;
            assert conn != null;
            assert !(conn.isClosed());
        }
        assert conn.isClosed();
        assert !(conn.isValid(2)) : "Connection must be closed";
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                conn.isValid((-2));
                return null;
            }
        }, SQLException.class, "Invalid timeout");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateStatement() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            try (Statement stmt = conn.createStatement()) {
                assertNotNull(stmt);
                stmt.close();
                conn.close();
                // Exception when called on closed connection
                checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                    @Override
                    public void run() throws Exception {
                        conn.createStatement();
                    }
                });
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateStatement2() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            int[] rsTypes = new int[]{ ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
            int[] rsConcurs = new int[]{ ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE };
            DatabaseMetaData meta = conn.getMetaData();
            for (final int type : rsTypes) {
                for (final int concur : rsConcurs) {
                    if (meta.supportsResultSetConcurrency(type, concur)) {
                        assert type == (ResultSet.TYPE_FORWARD_ONLY);
                        assert concur == (ResultSet.CONCUR_READ_ONLY);
                        try (Statement stmt = conn.createStatement(type, concur)) {
                            assertNotNull(stmt);
                            assertEquals(type, stmt.getResultSetType());
                            assertEquals(concur, stmt.getResultSetConcurrency());
                        }
                        continue;
                    }
                    GridTestUtils.assertThrows(log, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            return conn.createStatement(type, concur);
                        }
                    }, SQLFeatureNotSupportedException.class, null);
                }
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateStatement3() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            int[] rsTypes = new int[]{ ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
            int[] rsConcurs = new int[]{ ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE };
            int[] rsHoldabilities = new int[]{ ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT };
            DatabaseMetaData meta = conn.getMetaData();
            for (final int type : rsTypes) {
                for (final int concur : rsConcurs) {
                    for (final int holdabililty : rsHoldabilities) {
                        if (meta.supportsResultSetConcurrency(type, concur)) {
                            assert type == (ResultSet.TYPE_FORWARD_ONLY);
                            assert concur == (ResultSet.CONCUR_READ_ONLY);
                            try (Statement stmt = conn.createStatement(type, concur, holdabililty)) {
                                assertNotNull(stmt);
                                assertEquals(type, stmt.getResultSetType());
                                assertEquals(concur, stmt.getResultSetConcurrency());
                                assertEquals(holdabililty, stmt.getResultSetHoldability());
                            }
                            continue;
                        }
                        GridTestUtils.assertThrows(log, new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                return conn.createStatement(type, concur, holdabililty);
                            }
                        }, SQLFeatureNotSupportedException.class, null);
                    }
                }
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrepareStatement() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // null query text
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareStatement(null);
                }
            }, SQLException.class, "SQL string cannot be null");
            final String sqlText = "select * from test where param = ?";
            try (PreparedStatement prepared = conn.prepareStatement(sqlText)) {
                assertNotNull(prepared);
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.prepareStatement(sqlText);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrepareStatement3() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String sqlText = "select * from test where param = ?";
            int[] rsTypes = new int[]{ ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
            int[] rsConcurs = new int[]{ ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE };
            DatabaseMetaData meta = conn.getMetaData();
            for (final int type : rsTypes) {
                for (final int concur : rsConcurs) {
                    if (meta.supportsResultSetConcurrency(type, concur)) {
                        assert type == (ResultSet.TYPE_FORWARD_ONLY);
                        assert concur == (ResultSet.CONCUR_READ_ONLY);
                        // null query text
                        GridTestUtils.assertThrows(log, new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                return conn.prepareStatement(null, type, concur);
                            }
                        }, SQLException.class, "SQL string cannot be null");
                        continue;
                    }
                    GridTestUtils.assertThrows(log, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            return conn.prepareStatement(sqlText, type, concur);
                        }
                    }, SQLFeatureNotSupportedException.class, null);
                }
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            });
            conn.close();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrepareStatement4() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String sqlText = "select * from test where param = ?";
            int[] rsTypes = new int[]{ ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE };
            int[] rsConcurs = new int[]{ ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE };
            int[] rsHoldabilities = new int[]{ ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT };
            DatabaseMetaData meta = conn.getMetaData();
            for (final int type : rsTypes) {
                for (final int concur : rsConcurs) {
                    for (final int holdabililty : rsHoldabilities) {
                        if (meta.supportsResultSetConcurrency(type, concur)) {
                            assert type == (ResultSet.TYPE_FORWARD_ONLY);
                            assert concur == (ResultSet.CONCUR_READ_ONLY);
                            // null query text
                            GridTestUtils.assertThrows(log, new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    return conn.prepareStatement(null, type, concur, holdabililty);
                                }
                            }, SQLException.class, "SQL string cannot be null");
                            continue;
                        }
                        GridTestUtils.assertThrows(log, new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                return conn.prepareStatement(sqlText, type, concur, holdabililty);
                            }
                        }, SQLFeatureNotSupportedException.class, null);
                    }
                }
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.prepareStatement(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
                }
            });
            conn.close();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrepareStatementAutoGeneratedKeysUnsupported() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String sqlText = "insert into test (val) values (?)";
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareStatement(sqlText, Statement.RETURN_GENERATED_KEYS);
                }
            }, SQLFeatureNotSupportedException.class, "Auto generated keys are not supported.");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareStatement(sqlText, Statement.NO_GENERATED_KEYS);
                }
            }, SQLFeatureNotSupportedException.class, "Auto generated keys are not supported.");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareStatement(sqlText, new int[]{ 1 });
                }
            }, SQLFeatureNotSupportedException.class, "Auto generated keys are not supported.");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareStatement(sqlText, new String[]{ "ID" });
                }
            }, SQLFeatureNotSupportedException.class, "Auto generated keys are not supported.");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrepareCallUnsupported() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String sqlText = "exec test()";
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareCall(sqlText);
                }
            }, SQLFeatureNotSupportedException.class, "Callable functions are not supported.");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareCall(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }, SQLFeatureNotSupportedException.class, "Callable functions are not supported.");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.prepareCall(sqlText, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
                }
            }, SQLFeatureNotSupportedException.class, "Callable functions are not supported.");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNativeSql() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // null query text
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.nativeSQL(null);
                }
            }, SQLException.class, "SQL string cannot be null");
            final String sqlText = "select * from test";
            assertEquals(sqlText, conn.nativeSQL(sqlText));
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.nativeSQL(sqlText);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetAutoCommit() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            boolean ac0 = conn.getAutoCommit();
            conn.setAutoCommit((!ac0));
            // assert no exception
            conn.setAutoCommit(ac0);
            // assert no exception
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setAutoCommit(ac0);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCommit() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Should not be called in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.commit();
                    return null;
                }
            }, SQLException.class, "Transaction cannot be committed explicitly in auto-commit mode");
            assertTrue(conn.getAutoCommit());
            // Should not be called in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.commit();
                    return null;
                }
            }, SQLException.class, "Transaction cannot be committed explicitly in auto-commit mode.");
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.commit();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRollback() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Should not be called in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback();
                    return null;
                }
            }, SQLException.class, "Transaction cannot be rolled back explicitly in auto-commit mode.");
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.rollback();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testBeginFailsWhenMvccIsDisabled() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            conn.createStatement().execute("BEGIN");
            fail("Exception is expected");
        } catch (SQLException e) {
            assertEquals(TRANSACTION_STATE_EXCEPTION, e.getSQLState());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCommitIgnoredWhenMvccIsDisabled() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            conn.setAutoCommit(false);
            conn.createStatement().execute("COMMIT");
            conn.commit();
        }
        // assert no exception
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testRollbackIgnoredWhenMvccIsDisabled() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            conn.setAutoCommit(false);
            conn.createStatement().execute("ROLLBACK");
            conn.rollback();
        }
        // assert no exception
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetMetaData() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            DatabaseMetaData meta = conn.getMetaData();
            assertNotNull(meta);
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getMetaData();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetReadOnly() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setReadOnly(true);
                }
            });
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.isReadOnly();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetCatalog() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assert !(conn.getMetaData().supportsCatalogsInDataManipulation());
            assertNull(conn.getCatalog());
            conn.setCatalog("catalog");
            assertEquals(null, conn.getCatalog());
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setCatalog("");
                }
            });
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getCatalog();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetTransactionIsolation() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Invalid parameter value
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @SuppressWarnings("MagicConstant")
                @Override
                public Object call() throws Exception {
                    conn.setTransactionIsolation((-1));
                    return null;
                }
            }, SQLException.class, "Invalid transaction isolation level");
            // default level
            assertEquals(Connection.TRANSACTION_NONE, conn.getTransactionIsolation());
            int[] levels = new int[]{ Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, Connection.TRANSACTION_SERIALIZABLE };
            for (int level : levels) {
                conn.setTransactionIsolation(level);
                assertEquals(level, conn.getTransactionIsolation());
            }
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getTransactionIsolation();
                }
            });
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClearGetWarnings() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            SQLWarning warn = conn.getWarnings();
            assertNull(warn);
            conn.clearWarnings();
            warn = conn.getWarnings();
            assertNull(warn);
            conn.close();
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getWarnings();
                }
            });
            // Exception when called on closed connection
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.clearWarnings();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetTypeMap() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.getTypeMap();
                }
            }, SQLFeatureNotSupportedException.class, "Types mapping is not supported");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setTypeMap(new HashMap<String, Class<?>>());
                    return null;
                }
            }, SQLFeatureNotSupportedException.class, "Types mapping is not supported");
            conn.close();
            // Exception when called on closed connection
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.getTypeMap();
                }
            }, SQLException.class, "Connection is closed");
            // Exception when called on closed connection
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setTypeMap(new HashMap<String, Class<?>>());
                    return null;
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetHoldability() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // default value
            assertEquals(conn.getMetaData().getResultSetHoldability(), conn.getHoldability());
            assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, conn.getHoldability());
            conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
            assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, conn.getHoldability());
            // Invalid constant
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setHoldability((-1));
                    return null;
                }
            }, SQLException.class, "Invalid result set holdability value");
            conn.close();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.getHoldability();
                }
            }, SQLException.class, "Connection is closed");
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
                    return null;
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetSavepoint() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint();
                    return null;
                }
            }, SQLException.class, "Savepoint cannot be set in auto-commit mode");
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSetSavepointName() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Invalid arg
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint(null);
                    return null;
                }
            }, SQLException.class, "Savepoint name cannot be null");
            final String name = "savepoint";
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setSavepoint(name);
                    return null;
                }
            }, SQLException.class, "Savepoint cannot be set in auto-commit mode");
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSavepoint(name);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testRollbackSavePoint() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Invalid arg
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback(null);
                    return null;
                }
            }, SQLException.class, "Invalid savepoint");
            final Savepoint savepoint = getFakeSavepoint();
            // Disallowed in auto-commit mode
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.rollback(savepoint);
                    return null;
                }
            }, SQLException.class, "Auto-commit mode");
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.rollback(savepoint);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleaseSavepoint() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assert !(conn.getMetaData().supportsSavepoints());
            // Invalid arg
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.releaseSavepoint(null);
                    return null;
                }
            }, SQLException.class, "Savepoint cannot be null");
            final Savepoint savepoint = getFakeSavepoint();
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.releaseSavepoint(savepoint);
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.releaseSavepoint(savepoint);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateClob() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Unsupported
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createClob();
                }
            }, SQLFeatureNotSupportedException.class, "SQL-specific types are not supported");
            conn.close();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createClob();
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateBlob() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Unsupported
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createBlob();
                }
            }, SQLFeatureNotSupportedException.class, "SQL-specific types are not supported");
            conn.close();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createBlob();
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateNClob() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Unsupported
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createNClob();
                }
            }, SQLFeatureNotSupportedException.class, "SQL-specific types are not supported");
            conn.close();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createNClob();
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateSQLXML() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Unsupported
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createSQLXML();
                }
            }, SQLFeatureNotSupportedException.class, "SQL-specific types are not supported");
            conn.close();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createSQLXML();
                }
            }, SQLException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetClientInfoPair() throws Exception {
        // fail("https://issues.apache.org/jira/browse/IGNITE-5425");
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String name = "ApplicationName";
            final String val = "SelfTest";
            assertNull(conn.getWarnings());
            conn.setClientInfo(name, val);
            assertNull(conn.getClientInfo(val));
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getClientInfo(name);
                }
            });
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setClientInfo(name, val);
                    return null;
                }
            }, SQLClientInfoException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetClientInfoProperties() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String name = "ApplicationName";
            final String val = "SelfTest";
            final Properties props = new Properties();
            props.setProperty(name, val);
            conn.setClientInfo(props);
            Properties propsResult = conn.getClientInfo();
            assertNotNull(propsResult);
            assertTrue(propsResult.isEmpty());
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getClientInfo();
                }
            });
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setClientInfo(props);
                    return null;
                }
            }, SQLClientInfoException.class, "Connection is closed");
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateArrayOf() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final String typeName = "varchar";
            final String[] elements = new String[]{ "apple", "pear" };
            // Invalid typename
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.createArrayOf(null, null);
                    return null;
                }
            }, SQLException.class, "Type name cannot be null");
            // Unsupported
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createArrayOf(typeName, elements);
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createArrayOf(typeName, elements);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCreateStruct() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Invalid typename
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return conn.createStruct(null, null);
                }
            }, SQLException.class, "Type name cannot be null");
            final String typeName = "employee";
            final Object[] attrs = new Object[]{ 100, "Tom" };
            checkNotSupported(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createStruct(typeName, attrs);
                }
            });
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.createStruct(typeName, attrs);
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetSchema() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            assertEquals("PUBLIC", conn.getSchema());
            final String schema = "test";
            conn.setSchema(schema);
            assertEquals(schema.toUpperCase(), conn.getSchema());
            conn.setSchema((('"' + schema) + '"'));
            assertEquals(schema, conn.getSchema());
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setSchema(schema);
                }
            });
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getSchema();
                }
            });
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAbort() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // Invalid executor
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.abort(null);
                    return null;
                }
            }, SQLException.class, "Executor cannot be null");
            final Executor executor = Executors.newFixedThreadPool(1);
            conn.abort(executor);
            assertTrue(conn.isClosed());
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testGetSetNetworkTimeout() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            // default
            assertEquals(0, conn.getNetworkTimeout());
            final Executor executor = Executors.newFixedThreadPool(1);
            final int timeout = 1000;
            // Invalid timeout
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    conn.setNetworkTimeout(executor, (-1));
                    return null;
                }
            }, SQLException.class, "Network timeout cannot be negative");
            conn.setNetworkTimeout(executor, timeout);
            assertEquals(timeout, conn.getNetworkTimeout());
            conn.close();
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.getNetworkTimeout();
                }
            });
            checkConnectionClosed(new JdbcThinAbstractSelfTest.RunnableX() {
                @Override
                public void run() throws Exception {
                    conn.setNetworkTimeout(executor, timeout);
                }
            });
        }
    }

    /**
     * Test that attempting to supply invalid nested TX mode to driver fails on the client.
     */
    @Test
    public void testInvalidNestedTxMode() {
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                DriverManager.getConnection(((JdbcThinConnectionSelfTest.URL) + "/?nestedTransactionsMode=invalid"));
                return null;
            }
        }, SQLException.class, "Invalid nested transactions handling mode");
    }

    /**
     * Test that attempting to send unexpected name of nested TX mode to server on handshake yields an error.
     * We have to do this without explicit {@link Connection} as long as there's no other way to bypass validation and
     * supply a malformed {@link ConnectionProperties} to {@link JdbcThinTcpIo}.
     */
    @Test
    public void testInvalidNestedTxModeOnServerSide() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, SQLException {
        ConnectionPropertiesImpl connProps = new ConnectionPropertiesImpl();
        connProps.setAddresses(new HostAndPortRange[]{ new HostAndPortRange("127.0.0.1", DFLT_PORT, DFLT_PORT) });
        connProps.nestedTxMode("invalid");
        Constructor ctor = JdbcThinTcpIo.class.getDeclaredConstructor(ConnectionProperties.class);
        boolean acc = ctor.isAccessible();
        ctor.setAccessible(true);
        final JdbcThinTcpIo io = ((JdbcThinTcpIo) (ctor.newInstance(connProps)));
        try {
            GridTestUtils.assertThrows(null, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    io.start();
                    return null;
                }
            }, SQLException.class, "err=Invalid nested transactions handling mode: invalid");
        } finally {
            io.close();
            ctor.setAccessible(acc);
        }
    }

    /**
     *
     */
    @Test
    public void testSslClientAndPlainServer() {
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                DriverManager.getConnection((((((("jdbc:ignite:thin://127.0.0.1/?sslMode=require" + "&sslClientCertificateKeyStoreUrl=") + (JdbcThinConnectionSelfTest.CLI_KEY_STORE_PATH)) + "&sslClientCertificateKeyStorePassword=123456") + "&sslTrustCertificateKeyStoreUrl=") + (JdbcThinConnectionSelfTest.SRV_KEY_STORE_PATH)) + "&sslTrustCertificateKeyStorePassword=123456"));
                return null;
            }
        }, SQLException.class, "Failed to SSL connect to server");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultithreadingException() throws Exception {
        int threadCnt = 10;
        final boolean[] end = new boolean[]{ false };
        final SQLException[] exs = new SQLException[threadCnt];
        final AtomicInteger exCnt = new AtomicInteger(0);
        try (final Connection conn = DriverManager.getConnection(JdbcThinConnectionSelfTest.URL)) {
            final IgniteInternalFuture f = GridTestUtils.runMultiThreadedAsync(new Runnable() {
                @Override
                public void run() {
                    try {
                        conn.createStatement();
                        while (!(end[0]))
                            conn.createStatement().execute("SELECT 1");

                        conn.createStatement().execute("SELECT 1");
                    } catch (SQLException e) {
                        end[0] = true;
                        exs[exCnt.getAndIncrement()] = e;
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        fail(("Unexpected exception (see details above): " + (e.getMessage())));
                    }
                }
            }, threadCnt, "run-query");
            f.get();
            boolean exceptionFound = false;
            for (SQLException e : exs) {
                if ((e != null) && (e.getMessage().contains("Concurrent access to JDBC connection is not allowed")))
                    exceptionFound = true;

            }
            assertTrue("Concurrent access to JDBC connection is not allowed", exceptionFound);
        }
    }
}

