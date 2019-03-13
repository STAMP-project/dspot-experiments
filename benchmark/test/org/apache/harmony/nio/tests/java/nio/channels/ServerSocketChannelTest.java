/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.nio.tests.java.nio.channels;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import junit.framework.TestCase;


/* test for ServerSocketChannel */
public class ServerSocketChannelTest extends TestCase {
    private static final int CAPACITY_NORMAL = 200;

    private static final int CAPACITY_64KB = 65536;

    private static final int TIME_UNIT = 200;

    private InetSocketAddress localAddr1;

    private ServerSocketChannel serverChannel;

    private SocketChannel clientChannel;

    // -------------------------------------------------------------------
    // Test for methods in abstract class.
    // -------------------------------------------------------------------
    /* Test method for 'java.nio.channels.ServerSocketChannel.validOps()' */
    public void testValidOps() {
        MockServerSocketChannel testMSChnlnull = new MockServerSocketChannel(null);
        MockServerSocketChannel testMSChnl = new MockServerSocketChannel(SelectorProvider.provider());
        TestCase.assertEquals(SelectionKey.OP_ACCEPT, this.serverChannel.validOps());
        TestCase.assertEquals(SelectionKey.OP_ACCEPT, testMSChnl.validOps());
        TestCase.assertEquals(SelectionKey.OP_ACCEPT, testMSChnlnull.validOps());
    }

    /* Test method for 'java.nio.channels.ServerSocketChannel.open()' */
    public void testOpen() {
        MockServerSocketChannel testMSChnl = new MockServerSocketChannel(null);
        MockServerSocketChannel testMSChnlnotnull = new MockServerSocketChannel(SelectorProvider.provider());
        TestCase.assertEquals(SelectionKey.OP_ACCEPT, testMSChnlnotnull.validOps());
        TestCase.assertNull(testMSChnl.provider());
        TestCase.assertNotNull(testMSChnlnotnull.provider());
        TestCase.assertNotNull(this.serverChannel.provider());
        TestCase.assertEquals(testMSChnlnotnull.provider(), this.serverChannel.provider());
    }

    // -------------------------------------------------------------------
    // Test for socket()
    // -------------------------------------------------------------------
    /* Test method for 'java.nio.channels.ServerSocketChannel.socket()' */
    public void testSocket_Block_BeforeClose() throws Exception {
        TestCase.assertTrue(this.serverChannel.isOpen());
        TestCase.assertTrue(this.serverChannel.isBlocking());
        ServerSocket s1 = this.serverChannel.socket();
        TestCase.assertFalse(s1.isClosed());
        assertSocketNotAccepted(s1);
        ServerSocket s2 = this.serverChannel.socket();
        // same
        TestCase.assertSame(s1, s2);
        // socket close makes the channel close
        s1.close();
        TestCase.assertFalse(this.serverChannel.isOpen());
    }

    public void testSocket_NonBlock_BeforeClose() throws Exception {
        TestCase.assertTrue(this.serverChannel.isOpen());
        this.serverChannel.configureBlocking(false);
        ServerSocket s1 = this.serverChannel.socket();
        TestCase.assertFalse(s1.isClosed());
        assertSocketNotAccepted(s1);
        ServerSocket s2 = this.serverChannel.socket();
        // same
        TestCase.assertSame(s1, s2);
        // socket close makes the channel close
        s1.close();
        TestCase.assertFalse(this.serverChannel.isOpen());
    }

    public void testSocket_Block_Closed() throws Exception {
        this.serverChannel.close();
        TestCase.assertFalse(this.serverChannel.isOpen());
        TestCase.assertTrue(this.serverChannel.isBlocking());
        ServerSocket s1 = this.serverChannel.socket();
        TestCase.assertTrue(s1.isClosed());
        assertSocketNotAccepted(s1);
        ServerSocket s2 = this.serverChannel.socket();
        // same
        TestCase.assertSame(s1, s2);
    }

    public void testSocket_NonBlock_Closed() throws Exception {
        this.serverChannel.configureBlocking(false);
        this.serverChannel.close();
        TestCase.assertFalse(this.serverChannel.isBlocking());
        TestCase.assertFalse(this.serverChannel.isOpen());
        ServerSocket s1 = this.serverChannel.socket();
        TestCase.assertTrue(s1.isClosed());
        assertSocketNotAccepted(s1);
        ServerSocket s2 = this.serverChannel.socket();
        // same
        TestCase.assertSame(s1, s2);
    }

    public void testChannelBasicStatus() {
        ServerSocket gotSocket = this.serverChannel.socket();
        TestCase.assertFalse(gotSocket.isClosed());
        TestCase.assertTrue(this.serverChannel.isBlocking());
        TestCase.assertFalse(this.serverChannel.isRegistered());
        TestCase.assertEquals(SelectionKey.OP_ACCEPT, this.serverChannel.validOps());
        TestCase.assertEquals(SelectorProvider.provider(), this.serverChannel.provider());
    }

    // -------------------------------------------------------------------
    // Test for accept()
    // -------------------------------------------------------------------
    /* Test method for 'java.nio.channels.ServerSocketChannel.accept()' */
    public void testAccept_Block_NotYetBound() throws IOException {
        TestCase.assertTrue(this.serverChannel.isOpen());
        TestCase.assertTrue(this.serverChannel.isBlocking());
        try {
            this.serverChannel.accept();
            TestCase.fail("Should throw NotYetBoundException");
        } catch (NotYetBoundException e) {
            // correct
        }
    }

    public void testAccept_NonBlock_NotYetBound() throws IOException {
        TestCase.assertTrue(this.serverChannel.isOpen());
        this.serverChannel.configureBlocking(false);
        try {
            this.serverChannel.accept();
            TestCase.fail("Should throw NotYetBoundException");
        } catch (NotYetBoundException e) {
            // correct
        }
    }

    public void testAccept_ClosedChannel() throws Exception {
        this.serverChannel.close();
        TestCase.assertFalse(this.serverChannel.isOpen());
        try {
            this.serverChannel.accept();
            TestCase.fail("Should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // OK.
        }
    }

    public void testAccept_Block_NoConnect() throws IOException {
        TestCase.assertTrue(this.serverChannel.isBlocking());
        ServerSocket gotSocket = this.serverChannel.socket();
        gotSocket.bind(localAddr1);
        // blocking mode , will block and wait for ever...
        // so must close the server channel with another thread.
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(ServerSocketChannelTest.TIME_UNIT);
                    ServerSocketChannelTest.this.serverChannel.close();
                } catch (Exception e) {
                    TestCase.fail(("Fail to close the server channel because of" + (e.getClass().getName())));
                }
            }
        }.start();
        try {
            this.serverChannel.accept();
            TestCase.fail("Should throw a AsynchronousCloseException");
        } catch (AsynchronousCloseException e) {
            // OK.
        }
    }

    public void testAccept_NonBlock_NoConnect() throws IOException {
        ServerSocket gotSocket = this.serverChannel.socket();
        gotSocket.bind(localAddr1);
        this.serverChannel.configureBlocking(false);
        // non-blocking mode , will immediately return
        TestCase.assertNull(this.serverChannel.accept());
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_read_Blocking_RealData() throws IOException {
        serverChannel.socket().bind(localAddr1);
        ByteBuffer buf = ByteBuffer.allocate(ServerSocketChannelTest.CAPACITY_NORMAL);
        for (int i = 0; i < (ServerSocketChannelTest.CAPACITY_NORMAL); i++) {
            buf.put(((byte) (i)));
        }
        clientChannel.connect(localAddr1);
        Socket serverSocket = serverChannel.accept().socket();
        InputStream in = serverSocket.getInputStream();
        buf.flip();
        clientChannel.write(buf);
        clientChannel.close();
        assertReadResult(in, ServerSocketChannelTest.CAPACITY_NORMAL);
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_read_NonBlocking_RealData() throws Exception {
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(localAddr1);
        ByteBuffer buf = ByteBuffer.allocate(ServerSocketChannelTest.CAPACITY_NORMAL);
        for (int i = 0; i < (ServerSocketChannelTest.CAPACITY_NORMAL); i++) {
            buf.put(((byte) (i)));
        }
        buf.flip();
        clientChannel.connect(localAddr1);
        Socket serverSocket = serverChannel.accept().socket();
        InputStream in = serverSocket.getInputStream();
        clientChannel.write(buf);
        clientChannel.close();
        assertReadResult(in, ServerSocketChannelTest.CAPACITY_NORMAL);
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_write_Blocking_RealData() throws IOException {
        TestCase.assertTrue(serverChannel.isBlocking());
        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(localAddr1);
        byte[] writeContent = new byte[ServerSocketChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (writeContent.length); i++) {
            writeContent[i] = ((byte) (i));
        }
        clientChannel.connect(localAddr1);
        Socket socket = serverChannel.accept().socket();
        OutputStream out = socket.getOutputStream();
        out.write(writeContent);
        out.flush();
        socket.close();
        assertWriteResult(ServerSocketChannelTest.CAPACITY_NORMAL);
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_write_NonBlocking_RealData() throws Exception {
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        serverSocket.bind(localAddr1);
        byte[] writeContent = new byte[ServerSocketChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (ServerSocketChannelTest.CAPACITY_NORMAL); i++) {
            writeContent[i] = ((byte) (i));
        }
        clientChannel.connect(localAddr1);
        Socket clientSocket = serverChannel.accept().socket();
        OutputStream out = clientSocket.getOutputStream();
        out.write(writeContent);
        clientSocket.close();
        assertWriteResult(ServerSocketChannelTest.CAPACITY_NORMAL);
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_read_LByteBuffer_Blocking_ReadWriteRealLargeData() throws IOException, InterruptedException {
        serverChannel.socket().bind(localAddr1);
        ByteBuffer buf = ByteBuffer.allocate(ServerSocketChannelTest.CAPACITY_64KB);
        for (int i = 0; i < (ServerSocketChannelTest.CAPACITY_64KB); i++) {
            buf.put(((byte) (i)));
        }
        buf.flip();
        clientChannel.connect(localAddr1);
        ServerSocketChannelTest.WriteChannelThread writeThread = new ServerSocketChannelTest.WriteChannelThread(clientChannel, buf);
        writeThread.start();
        Socket socket = serverChannel.accept().socket();
        InputStream in = socket.getInputStream();
        assertReadResult(in, ServerSocketChannelTest.CAPACITY_64KB);
        writeThread.join();
        // check if the thread threw any exceptions
        if ((writeThread.exception) != null) {
            throw writeThread.exception;
        }
    }

    class WriteChannelThread extends Thread {
        SocketChannel channel;

        ByteBuffer buffer;

        IOException exception;

        public WriteChannelThread(SocketChannel channel, ByteBuffer buffer) {
            this.channel = channel;
            this.buffer = buffer;
        }

        public void run() {
            try {
                channel.write(buffer);
                channel.close();
            } catch (IOException e) {
                exception = e;
            }
        }
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_read_LByteBuffer_NonBlocking_ReadWriteRealLargeData() throws Exception {
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(localAddr1);
        ByteBuffer buf = ByteBuffer.allocate(ServerSocketChannelTest.CAPACITY_64KB);
        for (int i = 0; i < (ServerSocketChannelTest.CAPACITY_64KB); i++) {
            buf.put(((byte) (i)));
        }
        buf.flip();
        clientChannel.connect(localAddr1);
        ServerSocketChannelTest.WriteChannelThread writeThread = new ServerSocketChannelTest.WriteChannelThread(clientChannel, buf);
        writeThread.start();
        Socket socket = serverChannel.accept().socket();
        InputStream in = socket.getInputStream();
        assertReadResult(in, ServerSocketChannelTest.CAPACITY_64KB);
        writeThread.join();
        // check if the thread threw any exceptions
        if ((writeThread.exception) != null) {
            throw writeThread.exception;
        }
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_write_LByteBuffer_NonBlocking_ReadWriteRealLargeData() throws Exception {
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(localAddr1);
        byte[] writeContent = new byte[ServerSocketChannelTest.CAPACITY_64KB];
        for (int i = 0; i < (writeContent.length); i++) {
            writeContent[i] = ((byte) (i));
        }
        clientChannel.connect(localAddr1);
        Socket socket = serverChannel.accept().socket();
        ServerSocketChannelTest.WriteSocketThread writeThread = new ServerSocketChannelTest.WriteSocketThread(socket, writeContent);
        writeThread.start();
        assertWriteResult(ServerSocketChannelTest.CAPACITY_64KB);
        writeThread.join();
        // check if the thread threw any exceptions
        if ((writeThread.exception) != null) {
            throw writeThread.exception;
        }
    }

    class WriteSocketThread extends Thread {
        Socket socket;

        byte[] buffer;

        IOException exception;

        public WriteSocketThread(Socket socket, byte[] buffer) {
            this.socket = socket;
            this.buffer = buffer;
        }

        public void run() {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(buffer);
                socket.close();
            } catch (IOException e) {
                exception = e;
            }
        }
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#accept().socket()
     */
    public void test_write_LByteBuffer_Blocking_ReadWriteRealLargeData() throws Exception {
        serverChannel.socket().bind(localAddr1);
        byte[] writeContent = new byte[ServerSocketChannelTest.CAPACITY_64KB];
        for (int i = 0; i < (writeContent.length); i++) {
            writeContent[i] = ((byte) (i));
        }
        clientChannel.connect(localAddr1);
        Socket socket = serverChannel.accept().socket();
        ServerSocketChannelTest.WriteSocketThread writeThread = new ServerSocketChannelTest.WriteSocketThread(socket, writeContent);
        writeThread.start();
        assertWriteResult(ServerSocketChannelTest.CAPACITY_64KB);
        writeThread.join();
        // check if the thread threw any exceptions
        if ((writeThread.exception) != null) {
            throw writeThread.exception;
        }
    }

    /**
     *
     *
     * @unknown ServerSocketChannel#socket().getSoTimeout()
     */
    public void test_accept_SOTIMEOUT() throws IOException {
        // regression test for Harmony-707
        final int SO_TIMEOUT = 10;
        ServerSocketChannel sc = ServerSocketChannel.open();
        try {
            ServerSocket ss = sc.socket();
            ss.bind(localAddr1);
            sc.configureBlocking(false);
            ss.setSoTimeout(SO_TIMEOUT);
            SocketChannel client = sc.accept();
            // non blocking mode, returns null since there are no pending connections.
            TestCase.assertNull(client);
            int soTimeout = ss.getSoTimeout();
            // Harmony fails here.
            TestCase.assertEquals(SO_TIMEOUT, soTimeout);
        } finally {
            sc.close();
        }
    }

    /**
     *
     *
     * @unknown ServerSocket#socket().accept()
     */
    public void test_socket_accept_Blocking_NotBound() throws IOException {
        // regression test for Harmony-748
        ServerSocket gotSocket = serverChannel.socket();
        serverChannel.configureBlocking(true);
        try {
            gotSocket.accept();
            TestCase.fail("Should throw an IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        serverChannel.close();
        try {
            gotSocket.accept();
            TestCase.fail("Should throw an IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown ServerSocket#socket().accept()
     */
    public void test_socket_accept_Nonblocking_NotBound() throws IOException {
        // regression test for Harmony-748
        ServerSocket gotSocket = serverChannel.socket();
        serverChannel.configureBlocking(false);
        try {
            gotSocket.accept();
            TestCase.fail("Should throw an IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        serverChannel.close();
        try {
            gotSocket.accept();
            TestCase.fail("Should throw an IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown ServerSocket#socket().accept()
     */
    public void test_socket_accept_Nonblocking_Bound() throws IOException {
        // regression test for Harmony-748
        serverChannel.configureBlocking(false);
        ServerSocket gotSocket = serverChannel.socket();
        gotSocket.bind(localAddr1);
        try {
            gotSocket.accept();
            TestCase.fail("Should throw an IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        serverChannel.close();
        try {
            gotSocket.accept();
            TestCase.fail("Should throw a ClosedChannelException");
        } catch (ClosedChannelException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown ServerSocket#socket().accept()
     */
    public void test_socket_accept_Blocking_Bound() throws IOException {
        // regression test for Harmony-748
        serverChannel.configureBlocking(true);
        ServerSocket gotSocket = serverChannel.socket();
        gotSocket.bind(localAddr1);
        serverChannel.close();
        try {
            gotSocket.accept();
            TestCase.fail("Should throw a ClosedChannelException");
        } catch (ClosedChannelException e) {
            // expected
        }
    }

    /**
     * Regression test for HARMONY-4961
     */
    public void test_socket_getLocalPort() throws IOException {
        serverChannel.socket().bind(localAddr1);
        clientChannel.connect(localAddr1);
        SocketChannel myChannel = serverChannel.accept();
        int port = myChannel.socket().getLocalPort();
        TestCase.assertEquals(localAddr1.getPort(), port);
        myChannel.close();
        clientChannel.close();
        serverChannel.close();
    }

    /**
     * Regression test for HARMONY-6375
     */
    public void test_accept_configureBlocking() throws Exception {
        InetSocketAddress localAddr = new InetSocketAddress("localhost", 0);
        serverChannel.socket().bind(localAddr);
        // configure the channel non-blocking
        // when it is accepting in main thread
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(ServerSocketChannelTest.TIME_UNIT);
                    serverChannel.configureBlocking(false);
                    serverChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        try {
            serverChannel.accept();
            TestCase.fail("should throw AsynchronousCloseException");
        } catch (AsynchronousCloseException e) {
            // expected
        }
        serverChannel.close();
    }
}

