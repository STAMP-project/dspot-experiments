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
package libcore.java.nio.channels;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import junit.framework.TestCase;


public class OldSocketChannelTest extends TestCase {
    private static final int CAPACITY_NORMAL = 200;

    private static final int CAPACITY_HUGE = 512 * 1024;

    private InetSocketAddress localAddr1;

    private SocketChannel channel1;

    private SocketChannel channel2;

    private ServerSocket server1;

    private static final int TIMEOUT = 60000;

    // -------------------------------------------------------------------
    // Test for methods in abstract class.
    // -------------------------------------------------------------------
    public void testConstructor() throws IOException {
        SocketChannel channel = SelectorProvider.provider().openSocketChannel();
        TestCase.assertNotNull(channel);
        TestCase.assertSame(SelectorProvider.provider(), channel.provider());
        channel = SocketChannel.open();
        TestCase.assertNotNull(channel);
        TestCase.assertSame(SelectorProvider.provider(), channel.provider());
        OldSocketChannelTest.MockSocketChannel chan = new OldSocketChannelTest.MockSocketChannel(SelectorProvider.provider());
        TestCase.assertTrue(chan.isConstructorCalled);
    }

    public void testValidOps() {
        OldSocketChannelTest.MockSocketChannel testMSChannel = new OldSocketChannelTest.MockSocketChannel(null);
        TestCase.assertEquals(13, this.channel1.validOps());
        TestCase.assertEquals(13, testMSChannel.validOps());
    }

    public void testOpen() throws IOException {
        ByteBuffer[] buf = new ByteBuffer[1];
        buf[0] = ByteBuffer.allocateDirect(OldSocketChannelTest.CAPACITY_NORMAL);
        OldSocketChannelTest.MockSocketChannel testMSChannel = new OldSocketChannelTest.MockSocketChannel(null);
        OldSocketChannelTest.MockSocketChannel testMSChannelnotnull = new OldSocketChannelTest.MockSocketChannel(SelectorProvider.provider());
        SocketChannel testSChannel = OldSocketChannelTest.MockSocketChannel.open();
        TestCase.assertTrue(testSChannel.isOpen());
        TestCase.assertNull(testMSChannel.provider());
        TestCase.assertNotNull(testSChannel.provider());
        TestCase.assertEquals(SelectorProvider.provider(), testSChannel.provider());
        TestCase.assertNotNull(testMSChannelnotnull.provider());
        TestCase.assertEquals(this.channel1.provider(), testMSChannelnotnull.provider());
        try {
            this.channel1.write(buf);
            TestCase.fail("Should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
    }

    public void testIsOpen() throws Exception {
        TestCase.assertTrue(this.channel1.isOpen());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
    }

    public void testIsConnected() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());// not connected

        this.channel1.configureBlocking(false);
        TestCase.assertFalse(this.channel1.connect(localAddr1));
        TestCase.assertFalse(this.channel1.isConnected());
        TestCase.assertTrue(this.channel1.isConnectionPending());
        TestCase.assertTrue(tryFinish());
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isConnected());
    }

    public void testIsConnectionPending() throws Exception {
        // ensure
        ensureServerClosed();
        this.channel1.configureBlocking(false);
        TestCase.assertFalse(this.channel1.isConnectionPending());
        // finish
        try {
            this.channel1.finishConnect();
            TestCase.fail("Should throw NoConnectionPendingException");
        } catch (NoConnectionPendingException e) {
            // OK.
        }
        TestCase.assertFalse(this.channel1.isConnectionPending());
        // connect
        TestCase.assertFalse(this.channel1.connect(localAddr1));
        TestCase.assertTrue(this.channel1.isConnectionPending());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isConnectionPending());
    }

    public void testChannelBasicStatus() {
        Socket gotSocket = this.channel1.socket();
        TestCase.assertFalse(gotSocket.isClosed());
        TestCase.assertTrue(this.channel1.isBlocking());
        TestCase.assertFalse(this.channel1.isRegistered());
        TestCase.assertEquals((((SelectionKey.OP_CONNECT) | (SelectionKey.OP_READ)) | (SelectionKey.OP_WRITE)), this.channel1.validOps());
        TestCase.assertEquals(SelectorProvider.provider(), this.channel1.provider());
    }

    public void testOpenSocketAddress() throws IOException {
        this.channel1 = SocketChannel.open(localAddr1);
        TestCase.assertTrue(this.channel1.isConnected());
        SocketAddress newTypeAddress = new OldSocketChannelTest.SubSocketAddress();
        try {
            this.channel1 = SocketChannel.open(newTypeAddress);
            TestCase.fail("Should throw UnexpectedAddressTypeException");
        } catch (UnsupportedAddressTypeException e) {
            // expected
        }
        SocketAddress unresolvedAddress = InetSocketAddress.createUnresolved("127.0.0.1", 8080);
        try {
            this.channel1 = SocketChannel.open(unresolvedAddress);
            TestCase.fail("Should throw UnresolvedAddressException");
        } catch (UnresolvedAddressException e) {
            // expected
        }
        SocketChannel channel1IP = null;
        try {
            channel1IP = SocketChannel.open(null);
            TestCase.fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // correct
        }
        TestCase.assertNull(channel1IP);
    }

    public void test_socketChannel_read_DirectByteBuffer() throws IOException, InterruptedException {
        // RoboVM note: This test has been modified to properly close down the ServerThread and
        // to close the SocketChannels opened even if the test fails half-way through.
        Thread server = null;
        try {
            final ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.socket().bind(null, 0);
            server = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 2; ++i) {
                            ByteBuffer buf = ByteBuffer.allocate(10);
                            buf.put(OldSocketChannelTest.data);
                            buf.rewind();
                            ssc.accept().write(buf);
                        }
                    } catch (Exception ignored) {
                    }
                }
            };
            server.start();
            // First test with array based byte buffer
            ByteBuffer buf = null;
            SocketChannel sc = SocketChannel.open();
            try {
                sc.connect(ssc.socket().getLocalSocketAddress());
                buf = ByteBuffer.allocate(OldSocketChannelTest.data.length);
                buf.limit(((OldSocketChannelTest.data.length) / 2));
                sc.read(buf);
                buf.limit(buf.capacity());
                sc.read(buf);
            } finally {
                sc.close();
            }
            // Make sure the buffer is filled correctly
            buf.rewind();
            assertSameContent(OldSocketChannelTest.data, buf);
            // Now test with direct byte buffer
            sc = SocketChannel.open();
            try {
                sc.connect(ssc.socket().getLocalSocketAddress());
                buf = ByteBuffer.allocateDirect(OldSocketChannelTest.data.length);
                buf.limit(((OldSocketChannelTest.data.length) / 2));
                sc.read(buf);
                buf.limit(buf.capacity());
                sc.read(buf);
            } finally {
                sc.close();
            }
            // Make sure the buffer is filled correctly
            buf.rewind();
            assertSameContent(OldSocketChannelTest.data, buf);
        } finally {
            OldSocketChannelTest.done = true;
            server.interrupt();
            server.join(2000);
        }
    }

    public static boolean done = false;

    public static byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    class MockSocketChannel extends SocketChannel {
        private boolean isConstructorCalled = false;

        public MockSocketChannel(SelectorProvider provider) {
            super(provider);
            isConstructorCalled = true;
        }

        public Socket socket() {
            return null;
        }

        public boolean isConnected() {
            return false;
        }

        public boolean isConnectionPending() {
            return false;
        }

        public boolean connect(SocketAddress address) throws IOException {
            return false;
        }

        public boolean finishConnect() throws IOException {
            return false;
        }

        public int read(ByteBuffer target) throws IOException {
            return 0;
        }

        public long read(ByteBuffer[] targets, int offset, int length) throws IOException {
            return 0;
        }

        public int write(ByteBuffer source) throws IOException {
            return 0;
        }

        public long write(ByteBuffer[] sources, int offset, int length) throws IOException {
            return 0;
        }

        protected void implCloseSelectableChannel() throws IOException {
            // empty
        }

        protected void implConfigureBlocking(boolean blockingMode) throws IOException {
            // empty
        }
    }

    class SubSocketAddress extends SocketAddress {
        private static final long serialVersionUID = 1L;

        // empty
        public SubSocketAddress() {
            super();
        }
    }
}

