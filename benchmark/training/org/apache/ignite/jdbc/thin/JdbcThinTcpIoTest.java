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
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.internal.jdbc.thin.JdbcThinTcpIo;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for JdbcThinTcpIo.
 */
public class JdbcThinTcpIoTest extends GridCommonAbstractTest {
    /**
     * Server port range.
     */
    private static final int[] SERVER_PORT_RANGE = new int[]{ 59000, 59020 };

    /**
     * Inaccessible addresses.
     */
    private static final String[] INACCESSIBLE_ADDRESSES = new String[]{ "123.45.67.89", "123.45.67.90" };

    /**
     * Test connection to host which has inaccessible A-records.
     *
     * @throws SQLException
     * 		On connection error or reject.
     * @throws IOException
     * 		On IO error in handshake.
     */
    @Test
    public void testHostWithManyAddresses() throws IOException, InterruptedException, SQLException {
        CountDownLatch connectionAccepted = new CountDownLatch(1);
        try (ServerSocket sock = createServerSocket(connectionAccepted)) {
            String[] addrs = new String[]{ JdbcThinTcpIoTest.INACCESSIBLE_ADDRESSES[0], "127.0.0.1", JdbcThinTcpIoTest.INACCESSIBLE_ADDRESSES[1] };
            JdbcThinTcpIo jdbcThinTcpIo = createTcpIo(addrs, sock.getLocalPort());
            try {
                jdbcThinTcpIo.start(500);
                // Check connection
                assertTrue(connectionAccepted.await(1000, TimeUnit.MILLISECONDS));
            } finally {
                jdbcThinTcpIo.close();
            }
        }
    }

    /**
     * Test exception text (should contain inaccessible ip addresses list).
     *
     * @throws SQLException
     * 		On connection error or reject.
     * @throws IOException
     * 		On IO error in handshake.
     */
    @Test
    public void testExceptionMessage() throws IOException, SQLException {
        try (ServerSocket sock = createServerSocket(null)) {
            String[] addrs = new String[]{ JdbcThinTcpIoTest.INACCESSIBLE_ADDRESSES[0], JdbcThinTcpIoTest.INACCESSIBLE_ADDRESSES[1] };
            JdbcThinTcpIo jdbcThinTcpIo = createTcpIo(addrs, sock.getLocalPort());
            Throwable throwable = GridTestUtils.assertThrows(null, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    jdbcThinTcpIo.start(500);
                    return null;
                }
            }, SQLException.class, null);
            String msg = throwable.getMessage();
            for (Throwable sup : throwable.getSuppressed())
                msg += " " + (sup.getMessage());

            for (String addr : addrs)
                assertTrue(String.format("Exception message should contain %s", addr), msg.contains(addr));

        }
    }
}

