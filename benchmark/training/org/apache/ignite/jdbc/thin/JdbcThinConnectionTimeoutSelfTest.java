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


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.concurrent.Executor;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Jdbc Thin Connection timeout tests.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinConnectionTimeoutSelfTest extends JdbcThinAbstractSelfTest {
    /**
     * IP finder.
     */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1/";

    /**
     * Server thread pull size.
     */
    private static final int SERVER_THREAD_POOL_SIZE = 4;

    /**
     * Nodes count.
     */
    private static final byte NODES_COUNT = 3;

    /**
     * Max table rows.
     */
    private static final int MAX_ROWS = 10000;

    /**
     * Executor stub
     */
    private static final Executor EXECUTOR_STUB = (Runnable command) -> {
    };

    /**
     * Connection.
     */
    private Connection conn;

    /**
     * Statement.
     */
    private Statement stmt;

    /**
     *
     */
    @Test
    public void testSettingNegativeConnectionTimeout() {
        GridTestUtils.assertThrows(log, () -> {
            conn.setNetworkTimeout(EXECUTOR_STUB, (-1));
            return null;
        }, SQLException.class, "Network timeout cannot be negative.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionTimeoutRetrieval() throws Exception {
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 2000);
        assertEquals(2000, conn.getNetworkTimeout());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionTimeout() throws Exception {
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 1000);
        GridTestUtils.assertThrows(log, () -> {
            stmt.execute("select sleep_func(2000)");
            return null;
        }, SQLException.class, "Connection timed out.");
        GridTestUtils.assertThrows(log, () -> {
            stmt.execute("select 1");
            return null;
        }, SQLException.class, "Statement is closed.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryTimeoutOccursBeforeConnectionTimeout() throws Exception {
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 10000);
        stmt.setQueryTimeout(1);
        GridTestUtils.assertThrows(log, () -> {
            stmt.executeQuery("select sleep_func(10) from Integer;");
            return null;
        }, SQLTimeoutException.class, "The query was cancelled while executing.");
        stmt.execute("select 1");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConnectionTimeoutUpdate() throws Exception {
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 5000);
        stmt.execute("select sleep_func(1000)");
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 500);
        GridTestUtils.assertThrows(log, () -> {
            stmt.execute("select sleep_func(1000)");
            return null;
        }, SQLException.class, "Connection timed out.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCancelingTimedOutStatement() throws Exception {
        conn.setNetworkTimeout(JdbcThinConnectionTimeoutSelfTest.EXECUTOR_STUB, 1);
        GridTestUtils.runAsync(() -> {
            try {
                Thread.sleep(1000);
                GridTestUtils.assertThrows(log, () -> {
                    stmt.cancel();
                    return null;
                }, .class, "Statement is closed.");
            } catch ( e) {
                log.error("Unexpected exception.", e);
                fail("Unexpected exception");
            }
        });
        GridTestUtils.runAsync(() -> {
            try {
                GridTestUtils.assertThrows(log, () -> {
                    stmt.execute("select sleep_func(1000)");
                    return null;
                }, .class, "Connection timed out.");
            } catch ( e) {
                log.error("Unexpected exception.", e);
                fail("Unexpected exception");
            }
        });
    }
}

