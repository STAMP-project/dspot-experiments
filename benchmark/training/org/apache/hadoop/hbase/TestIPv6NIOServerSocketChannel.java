/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase;


import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Locale;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests whether ServerSocketChannel works over ipv6, which ZooKeeper
 * depends on. On Windows Oracle JDK 6, creating a ServerSocketChannel throws
 * java.net.SocketException: Address family not supported by protocol family
 * exception. It is a known JVM bug, seems to be only resolved for JDK7:
 * http://bugs.sun.com/view_bug.do?bug_id=6230761
 *
 * For this test, we check that whether we are effected by this bug, and if so
 * the test ensures that we are running with java.net.preferIPv4Stack=true, so
 * that ZK will not fail to bind to ipv6 address using ClientCnxnSocketNIO.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestIPv6NIOServerSocketChannel {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIPv6NIOServerSocketChannel.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestIPv6NIOServerSocketChannel.class);

    /**
     * Checks whether we are effected by the JDK issue on windows, and if so
     * ensures that we are running with preferIPv4Stack=true.
     */
    @Test
    public void testServerSocket() throws IOException {
        byte[] addr = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
        InetAddress inetAddr = InetAddress.getByAddress(addr);
        try {
            bindServerSocket(inetAddr);
            bindNIOServerSocket(inetAddr);
            // if on *nix or windows JDK7, both will pass
        } catch (SocketException ex) {
            // On Windows JDK6, we will get expected exception:
            // java.net.SocketException: Address family not supported by protocol family
            // or java.net.SocketException: Protocol family not supported
            Assert.assertFalse((ex instanceof BindException));
            Assert.assertTrue(ex.getMessage().toLowerCase(Locale.ROOT).contains("protocol family"));
            TestIPv6NIOServerSocketChannel.LOG.info("Received expected exception:", ex);
            // if this is the case, ensure that we are running on preferIPv4=true
            ensurePreferIPv4();
        }
    }

    /**
     * Tests whether every InetAddress we obtain by resolving can open a
     * ServerSocketChannel.
     */
    @Test
    public void testServerSocketFromLocalhostResolution() throws IOException {
        InetAddress[] addrs = new InetAddress[]{ InetAddress.getLocalHost() };
        for (InetAddress addr : addrs) {
            TestIPv6NIOServerSocketChannel.LOG.info(("Resolved localhost as: " + addr));
            bindServerSocket(addr);
            bindNIOServerSocket(addr);
        }
    }
}

