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
package org.apache.zookeeper.server.quorum;


import UnifiedServerSocket.UnifiedSocket;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketOptions;
import java.util.concurrent.ExecutorService;
import org.apache.zookeeper.ZKTestCase;
import org.apache.zookeeper.common.X509TestContext;
import org.apache.zookeeper.common.X509Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test makes sure that certain operations on a UnifiedServerSocket do not
 * trigger blocking mode detection. This is necessary to ensure that the
 * Leader's accept() thread doesn't get blocked.
 */
@RunWith(Parameterized.class)
public class UnifiedServerSocketModeDetectionTest extends ZKTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(UnifiedServerSocketModeDetectionTest.class);

    private static File tempDir;

    private static X509TestContext x509TestContext;

    private boolean useSecureClient;

    private X509Util x509Util;

    private UnifiedServerSocket listeningSocket;

    private UnifiedSocket serverSideSocket;

    private Socket clientSocket;

    private ExecutorService workerPool;

    private int port;

    private InetSocketAddress localServerAddress;

    public UnifiedServerSocketModeDetectionTest(Boolean useSecureClient) {
        this.useSecureClient = useSecureClient;
    }

    @Test
    public void testGetInetAddress() {
        serverSideSocket.getInetAddress();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetLocalAddress() {
        serverSideSocket.getLocalAddress();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetPort() {
        serverSideSocket.getPort();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetLocalPort() {
        serverSideSocket.getLocalPort();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetRemoteSocketAddress() {
        serverSideSocket.getRemoteSocketAddress();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetLocalSocketAddress() {
        serverSideSocket.getLocalSocketAddress();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetInputStream() throws IOException {
        serverSideSocket.getInputStream();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetOutputStream() throws IOException {
        serverSideSocket.getOutputStream();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testGetTcpNoDelay() throws IOException {
        serverSideSocket.getTcpNoDelay();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetTcpNoDelay() throws IOException {
        boolean tcpNoDelay = serverSideSocket.getTcpNoDelay();
        tcpNoDelay = !tcpNoDelay;
        serverSideSocket.setTcpNoDelay(tcpNoDelay);
        Assert.assertFalse(serverSideSocket.isModeKnown());
        Assert.assertEquals(tcpNoDelay, serverSideSocket.getTcpNoDelay());
    }

    @Test
    public void testGetSoLinger() throws IOException {
        serverSideSocket.getSoLinger();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetSoLinger() throws IOException {
        int soLinger = serverSideSocket.getSoLinger();
        if (soLinger == (-1)) {
            // enable it if disabled
            serverSideSocket.setSoLinger(true, 1);
            Assert.assertFalse(serverSideSocket.isModeKnown());
            Assert.assertEquals(1, serverSideSocket.getSoLinger());
        } else {
            // disable it if enabled
            serverSideSocket.setSoLinger(false, (-1));
            Assert.assertFalse(serverSideSocket.isModeKnown());
            Assert.assertEquals((-1), serverSideSocket.getSoLinger());
        }
    }

    @Test
    public void testGetSoTimeout() throws IOException {
        serverSideSocket.getSoTimeout();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetSoTimeout() throws IOException {
        int timeout = serverSideSocket.getSoTimeout();
        timeout = timeout + 10;
        serverSideSocket.setSoTimeout(timeout);
        Assert.assertFalse(serverSideSocket.isModeKnown());
        Assert.assertEquals(timeout, serverSideSocket.getSoTimeout());
    }

    @Test
    public void testGetSendBufferSize() throws IOException {
        serverSideSocket.getSendBufferSize();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetSendBufferSize() throws IOException {
        serverSideSocket.setSendBufferSize(((serverSideSocket.getSendBufferSize()) + 1024));
        Assert.assertFalse(serverSideSocket.isModeKnown());
        // Note: the new buffer size is a hint and socket implementation
        // is free to ignore it, so we don't verify that we get back the
        // same value.
    }

    @Test
    public void testGetReceiveBufferSize() throws IOException {
        serverSideSocket.getReceiveBufferSize();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetReceiveBufferSize() throws IOException {
        serverSideSocket.setReceiveBufferSize(((serverSideSocket.getReceiveBufferSize()) + 1024));
        Assert.assertFalse(serverSideSocket.isModeKnown());
        // Note: the new buffer size is a hint and socket implementation
        // is free to ignore it, so we don't verify that we get back the
        // same value.
    }

    @Test
    public void testGetKeepAlive() throws IOException {
        serverSideSocket.getKeepAlive();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetKeepAlive() throws IOException {
        boolean keepAlive = serverSideSocket.getKeepAlive();
        keepAlive = !keepAlive;
        serverSideSocket.setKeepAlive(keepAlive);
        Assert.assertFalse(serverSideSocket.isModeKnown());
        Assert.assertEquals(keepAlive, serverSideSocket.getKeepAlive());
    }

    @Test
    public void testGetTrafficClass() throws IOException {
        serverSideSocket.getTrafficClass();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetTrafficClass() throws IOException {
        serverSideSocket.setTrafficClass(SocketOptions.IP_TOS);
        Assert.assertFalse(serverSideSocket.isModeKnown());
        // Note: according to the Socket javadocs, setTrafficClass() may be
        // ignored by socket implementations, so we don't check that the value
        // we set is returned.
    }

    @Test
    public void testGetReuseAddress() throws IOException {
        serverSideSocket.getReuseAddress();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testSetReuseAddress() throws IOException {
        boolean reuseAddress = serverSideSocket.getReuseAddress();
        reuseAddress = !reuseAddress;
        serverSideSocket.setReuseAddress(reuseAddress);
        Assert.assertFalse(serverSideSocket.isModeKnown());
        Assert.assertEquals(reuseAddress, serverSideSocket.getReuseAddress());
    }

    @Test
    public void testClose() throws IOException {
        serverSideSocket.close();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testShutdownInput() throws IOException {
        serverSideSocket.shutdownInput();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testShutdownOutput() throws IOException {
        serverSideSocket.shutdownOutput();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testIsConnected() {
        serverSideSocket.isConnected();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testIsBound() {
        serverSideSocket.isBound();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testIsClosed() {
        serverSideSocket.isClosed();
        Assert.assertFalse(serverSideSocket.isModeKnown());
    }

    @Test
    public void testIsInputShutdown() throws IOException {
        serverSideSocket.isInputShutdown();
        Assert.assertFalse(serverSideSocket.isModeKnown());
        serverSideSocket.shutdownInput();
        Assert.assertTrue(serverSideSocket.isInputShutdown());
    }

    @Test
    public void testIsOutputShutdown() throws IOException {
        serverSideSocket.isOutputShutdown();
        Assert.assertFalse(serverSideSocket.isModeKnown());
        serverSideSocket.shutdownOutput();
        Assert.assertTrue(serverSideSocket.isOutputShutdown());
    }
}

