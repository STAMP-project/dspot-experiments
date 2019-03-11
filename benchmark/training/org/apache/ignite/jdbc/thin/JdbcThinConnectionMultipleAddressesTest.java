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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.internal.util.lang.GridAbsPredicate;
import org.apache.ignite.mxbean.ClientProcessorMXBean;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * JDBC driver reconnect test with multiple addresses.
 */
@SuppressWarnings("ThrowableNotThrown")
public class JdbcThinConnectionMultipleAddressesTest extends JdbcThinAbstractSelfTest {
    /**
     * Nodes count.
     */
    private static final int NODES_CNT = 3;

    /**
     *
     */
    private static final String URL_PORT_RANGE = (("jdbc:ignite:thin://127.0.0.1:" + (ClientConnectorConfiguration.DFLT_PORT)) + "..") + ((ClientConnectorConfiguration.DFLT_PORT) + 10);

    /**
     * Jdbc ports.
     */
    private static ArrayList<Integer> jdbcPorts = new ArrayList<>();

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesConnect() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMultipleAddressesTest.url())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                ResultSet rs = stmt.getResultSet();
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
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
    public void testPortRangeConnect() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinConnectionMultipleAddressesTest.URL_PORT_RANGE)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                ResultSet rs = stmt.getResultSet();
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
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
    public void testMultipleAddressesOneNodeFailoverOnStatementExecute() throws Exception {
        checkReconnectOnStatementExecute(JdbcThinConnectionMultipleAddressesTest.url(), false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesAllNodesFailoverOnStatementExecute() throws Exception {
        checkReconnectOnStatementExecute(JdbcThinConnectionMultipleAddressesTest.url(), true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPortRangeAllNodesFailoverOnStatementExecute() throws Exception {
        checkReconnectOnStatementExecute(JdbcThinConnectionMultipleAddressesTest.URL_PORT_RANGE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesOneNodeFailoverOnResultSet() throws Exception {
        checkReconnectOnResultSet(JdbcThinConnectionMultipleAddressesTest.url(), false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesAllNodesFailoverOnResultSet() throws Exception {
        checkReconnectOnResultSet(JdbcThinConnectionMultipleAddressesTest.url(), true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPortRangeAllNodesFailoverOnResultSet() throws Exception {
        checkReconnectOnResultSet(JdbcThinConnectionMultipleAddressesTest.URL_PORT_RANGE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesOneNodeFailoverOnMeta() throws Exception {
        checkReconnectOnMeta(JdbcThinConnectionMultipleAddressesTest.url(), false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesAllNodesFailoverOnMeta() throws Exception {
        checkReconnectOnMeta(JdbcThinConnectionMultipleAddressesTest.url(), true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPortRangeAllNodesFailoverOnMeta() throws Exception {
        checkReconnectOnMeta(JdbcThinConnectionMultipleAddressesTest.URL_PORT_RANGE, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddressesOneNodeFailoverOnStreaming() throws Exception {
        checkReconnectOnStreaming(JdbcThinConnectionMultipleAddressesTest.url(), false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClientConnectionMXBean() throws Exception {
        Connection conn = DriverManager.getConnection(JdbcThinConnectionMultipleAddressesTest.URL_PORT_RANGE);
        try {
            final Statement stmt0 = conn.createStatement();
            stmt0.execute("SELECT 1");
            ResultSet rs0 = stmt0.getResultSet();
            ClientProcessorMXBean serverMxBean = null;
            // Find node which client is connected to.
            for (int i = 0; i < (JdbcThinConnectionMultipleAddressesTest.NODES_CNT); i++) {
                serverMxBean = clientProcessorBean(i);
                if (!(serverMxBean.getConnections().isEmpty()))
                    break;

            }
            assertNotNull("No ClientConnections MXBean found.", serverMxBean);
            serverMxBean.dropAllConnections();
            GridTestUtils.assertThrows(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    stmt0.execute("SELECT 1");
                    return null;
                }
            }, SQLException.class, "Failed to communicate with Ignite cluster");
            assertTrue(rs0.isClosed());
            assertTrue(stmt0.isClosed());
            assertTrue(getActiveClients().isEmpty());
            final Statement stmt1 = conn.createStatement();
            stmt1.execute("SELECT 1");
            ResultSet rs1 = stmt1.getResultSet();
            // Check active clients.
            List<String> activeClients = getActiveClients();
            assertEquals(1, activeClients.size());
            assertTrue(rs1.next());
            assertEquals(1, rs1.getInt(1));
            rs1.close();
            stmt1.close();
        } finally {
            conn.close();
        }
        boolean allClosed = GridTestUtils.waitForCondition(new GridAbsPredicate() {
            @Override
            public boolean apply() {
                return getActiveClients().isEmpty();
            }
        }, 10000);
        assertTrue(allClosed);
    }
}

