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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import junit.framework.TestCase;


/**
 * Test for DatagramChannel
 */
public class DatagramChannelTest extends TestCase {
    private static final int CAPACITY_NORMAL = 200;

    private static final int CAPACITY_1KB = 1024;

    private static final int CAPACITY_64KB = 65536;

    private static final int CAPACITY_ZERO = 0;

    private static final int CAPACITY_ONE = 1;

    private static final int TIME_UNIT = 500;

    private InetSocketAddress localAddr1;

    private InetSocketAddress localAddr2;

    private DatagramChannel channel1;

    private DatagramChannel channel2;

    private DatagramSocket datagramSocket1;

    private DatagramSocket datagramSocket2;

    // The port to be used in test cases.
    private int testPort;

    // -------------------------------------------------------------------
    // Test for methods in abstract class.
    // -------------------------------------------------------------------
    /* Test method for 'java.nio.channels.DatagramChannel.validOps()' */
    public void testValidOps() {
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        int val = this.channel1.validOps();
        TestCase.assertEquals(5, val);
        TestCase.assertEquals(val, testMock.validOps());
        TestCase.assertEquals(val, testMocknull.validOps());
    }

    /* Test method for 'java.nio.channels.DatagramChannel.open()' */
    public void testOpen() {
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        TestCase.assertNull(testMocknull.provider());
        TestCase.assertNotNull(testMock.provider());
        TestCase.assertEquals(this.channel1.provider(), testMock.provider());
        TestCase.assertEquals(5, testMock.validOps());
    }

    /* Test method for 'java.nio.channels.DatagramChannel.read(ByteBuffer)' */
    public void testReadByteBufferArray() throws IOException {
        final int testNum = 0;
        long readres = testNum;
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        int bufSize = 10;
        ByteBuffer[] readBuf = null;
        try {
            this.channel1.read(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            readres = testMock.read(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        readBuf = new ByteBuffer[bufSize];
        try {
            readres = this.channel1.read(readBuf);
            TestCase.fail("Should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
        readres = testMock.read(readBuf);
        TestCase.assertEquals(testNum, readres);
        readres = testMocknull.read(readBuf);
        TestCase.assertEquals(testNum, readres);
    }

    /* Test method for 'java.nio.channels.DatagramChannel.read(ByteBuffer)' */
    public void testReadByteBufferArray_BufNull() throws IOException {
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        ByteBuffer[] readBuf = null;
        try {
            this.channel1.read(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            testMock.read(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            testMocknull.read(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
    }

    /* Test method for 'java.nio.channels.DatagramChannel.write(ByteBuffer)' */
    public void testWriteByteBuffer() throws IOException {
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        int bufSize = 10;
        ByteBuffer[] readBuf = null;
        try {
            this.channel1.write(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            testMock.write(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        readBuf = new ByteBuffer[bufSize];
        try {
            this.channel1.write(readBuf);
            TestCase.fail("Should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
        long writeres = 0;
        writeres = testMock.write(readBuf);
        TestCase.assertEquals(0, writeres);
        writeres = testMocknull.write(readBuf);
        TestCase.assertEquals(0, writeres);
    }

    /* Test method for 'java.nio.channels.DatagramChannel.write(ByteBuffer)' */
    public void testWriteByteBuffer_Bufnull() throws IOException {
        MockDatagramChannel testMock = new MockDatagramChannel(SelectorProvider.provider());
        MockDatagramChannel testMocknull = new MockDatagramChannel(null);
        ByteBuffer[] readBuf = null;
        try {
            this.channel1.write(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            testMock.write(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            testMocknull.write(readBuf);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
    }

    // -------------------------------------------------------------------
    // Test for socket()
    // -------------------------------------------------------------------
    /**
     * Test method for 'DatagramChannelImpl.socket()'
     *
     * @throws SocketException
     * 		
     */
    public void testSocket_BasicStatusBeforeConnect() throws SocketException {
        TestCase.assertFalse(this.channel1.isConnected());// not connected

        DatagramSocket s1 = this.channel1.socket();
        assertSocketBeforeConnect(s1);
        DatagramSocket s2 = this.channel1.socket();
        // same
        TestCase.assertSame(s1, s2);
    }

    /**
     * Test method for 'DatagramChannelImpl.socket()'
     *
     * @throws IOException
     * 		
     */
    public void testSocket_Block_BasicStatusAfterConnect() throws IOException {
        this.channel1.connect(localAddr1);
        DatagramSocket s1 = this.channel1.socket();
        assertSocketAfterConnect(s1);
        DatagramSocket s2 = this.channel1.socket();
        // same
        TestCase.assertSame(s1, s2);
    }

    public void testSocket_NonBlock_BasicStatusAfterConnect() throws IOException {
        this.channel1.connect(localAddr1);
        this.channel1.configureBlocking(false);
        DatagramSocket s1 = this.channel1.socket();
        assertSocketAfterConnect(s1);
        DatagramSocket s2 = this.channel1.socket();
        // same
        TestCase.assertSame(s1, s2);
    }

    /**
     * Test method for 'DatagramChannelImpl.socket()'
     *
     * @throws IOException
     * 		
     */
    public void testSocket_ActionsBeforeConnect() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());// not connected

        DatagramSocket s = this.channel1.socket();
        assertSocketActionBeforeConnect(s);
    }

    /**
     * Test method for 'DatagramChannelImpl.socket()'
     *
     * @throws IOException
     * 		
     */
    public void testSocket_Block_ActionsAfterConnect() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());// not connected

        this.channel1.connect(localAddr1);
        DatagramSocket s = this.channel1.socket();
        assertSocketActionAfterConnect(s);
    }

    public void testSocket_NonBlock_ActionsAfterConnect() throws IOException {
        this.channel1.connect(localAddr1);
        this.channel1.configureBlocking(false);
        DatagramSocket s = this.channel1.socket();
        assertSocketActionAfterConnect(s);
    }

    // -------------------------------------------------------------------
    // Test for configureBlocking()
    // -------------------------------------------------------------------
    public void testConfigureBlocking_Read() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        ByteBuffer buf = ByteBuffer.allocate(DatagramChannelTest.CAPACITY_1KB);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(((DatagramChannelTest.TIME_UNIT) * 5));
                    channel1.configureBlocking(false);
                    TestCase.assertFalse(channel1.isBlocking());
                    datagramSocket1.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }.start();
        SocketAddress addr = channel1.receive(buf);
        TestCase.assertNull(addr);
    }

    // -------------------------------------------------------------------
    // Test for isConnected()
    // -------------------------------------------------------------------
    /**
     * Test method for 'DatagramChannelImpl.isConnected()'
     *
     * @throws IOException
     * 		
     */
    public void testIsConnected_WithServer() throws IOException {
        connectLocalServer();
        disconnectAfterConnected();
        this.datagramSocket1.close();
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isConnected());
    }

    // -------------------------------------------------------------------
    // Test for connect()
    // -------------------------------------------------------------------
    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     */
    public void testConnect_BlockWithServer() throws IOException {
        // blocking mode
        TestCase.assertTrue(this.channel1.isBlocking());
        connectLocalServer();
        datagramSocket1.close();
        disconnectAfterConnected();
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     */
    public void testConnect_BlockNoServer() throws IOException {
        connectWithoutServer();
        disconnectAfterConnected();
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_NonBlockWithServer() throws IOException {
        // Non blocking mode
        this.channel1.configureBlocking(false);
        connectLocalServer();
        datagramSocket1.close();
        disconnectAfterConnected();
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_Null() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        try {
            this.channel1.connect(null);
            TestCase.fail("Should throw an IllegalArgumentException here.");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_UnsupportedType() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        class SubSocketAddress extends SocketAddress {
            private static final long serialVersionUID = 1L;

            public SubSocketAddress() {
                super();
            }
        }
        SocketAddress newTypeAddress = new SubSocketAddress();
        try {
            this.channel1.connect(newTypeAddress);
            TestCase.fail("Should throw an UnsupportedAddressTypeException here.");
        } catch (UnsupportedAddressTypeException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_Unresolved() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        InetSocketAddress unresolved = new InetSocketAddress("unresolved address", 1080);
        try {
            this.channel1.connect(unresolved);
            TestCase.fail("Should throw an UnresolvedAddressException here.");// $NON-NLS-1$

        } catch (UnresolvedAddressException e) {
            // OK.
        }
    }

    public void testConnect_EmptyHost() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        TestCase.assertEquals(this.channel1, this.channel1.connect(new InetSocketAddress("", 1081)));// $NON-NLS-1$

    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_ClosedChannelException() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        try {
            this.channel1.connect(localAddr1);
            TestCase.fail("Should throw ClosedChannelException.");// $NON-NLS-1$

        } catch (ClosedChannelException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_IllegalStateException() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        this.channel1.connect(localAddr1);
        TestCase.assertTrue(this.channel1.isConnected());
        // connect after connected.
        try {
            this.channel1.connect(localAddr1);
            TestCase.fail("Should throw IllegalStateException.");// $NON-NLS-1$

        } catch (IllegalStateException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.connect(SocketAddress)'
     *
     * @throws IOException
     * 		
     */
    public void testConnect_CheckOpenBeforeStatus() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        this.channel1.connect(localAddr1);
        TestCase.assertTrue(this.channel1.isConnected());
        // connect after connected.
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        // checking open is before checking status.
        try {
            this.channel1.connect(localAddr1);
            TestCase.fail("Should throw ClosedChannelException.");// $NON-NLS-1$

        } catch (ClosedChannelException e) {
            // OK.
        }
    }

    // -------------------------------------------------------------------
    // Test for disconnect()
    // -------------------------------------------------------------------
    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_BeforeConnect() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        TestCase.assertEquals(this.channel1, this.channel1.disconnect());
        TestCase.assertFalse(this.channel1.isConnected());
    }

    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_UnconnectedClosed() throws IOException {
        TestCase.assertFalse(this.channel1.isConnected());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        TestCase.assertEquals(this.channel1, this.channel1.disconnect());
        TestCase.assertFalse(this.channel1.isConnected());
    }

    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_BlockWithServerChannelClosed() throws IOException {
        TestCase.assertTrue(this.channel1.isBlocking());
        connectLocalServer();
        // disconnect after channel close
        this.channel1.close();
        disconnectAfterClosed();
    }

    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_NonBlockWithServerChannelClosed() throws IOException {
        this.channel1.configureBlocking(false);
        connectLocalServer();
        // disconnect after channel close
        this.channel1.close();
        disconnectAfterClosed();
    }

    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_BlockWithServerServerClosed() throws IOException {
        TestCase.assertTrue(this.channel1.isBlocking());
        connectLocalServer();
        // disconnect after server close
        this.datagramSocket1.close();
        TestCase.assertTrue(this.channel1.isOpen());
        TestCase.assertTrue(this.channel1.isConnected());
        disconnectAfterConnected();
    }

    /**
     * Test method for 'DatagramChannelImpl.disconnect()'
     *
     * @throws IOException
     * 		
     */
    public void testDisconnect_NonBlockWithServerServerClosed() throws IOException {
        this.channel1.configureBlocking(false);
        TestCase.assertFalse(this.channel1.isBlocking());
        connectLocalServer();
        // disconnect after server close
        this.datagramSocket1.close();
        TestCase.assertTrue(this.channel1.isOpen());
        TestCase.assertTrue(this.channel1.isConnected());
        disconnectAfterConnected();
    }

    // -------------------------------------------------------------------
    // Test for receive(): Behavior Without Server.
    // -------------------------------------------------------------------
    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedNull() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        try {
            this.channel1.receive(null);
            TestCase.fail("Should throw a NPE here.");// $NON-NLS-1$

        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedReadonly() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL).asReadOnlyBuffer();
        TestCase.assertTrue(dst.isReadOnly());
        try {
            this.channel1.receive(dst);
            TestCase.fail("Should throw an IllegalArgumentException here.");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedBufEmpty() throws Exception {
        this.channel1.configureBlocking(false);
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedBufZero() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_ZERO);
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedBufNotEmpty() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        // buf is not empty
        dst.put(((byte) (88)));
        TestCase.assertEquals((((dst.position()) + (DatagramChannelTest.CAPACITY_NORMAL)) - 1), dst.limit());
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedBufFull() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_ONE);
        // buf is full
        dst.put(((byte) (88)));
        TestCase.assertEquals(dst.position(), dst.limit());
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedClose() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        try {
            TestCase.assertNull(this.channel1.receive(dst));
            TestCase.fail("Should throw a ClosedChannelException here.");// $NON-NLS-1$

        } catch (ClosedChannelException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedCloseNull() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        // checking buffer before checking open
        try {
            this.channel1.receive(null);
            TestCase.fail("Should throw a NPE here.");// $NON-NLS-1$

        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_UnconnectedCloseReadonly() throws Exception {
        TestCase.assertFalse(this.channel1.isConnected());
        ByteBuffer dst = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL).asReadOnlyBuffer();
        TestCase.assertTrue(dst.isReadOnly());
        this.channel1.close();
        TestCase.assertFalse(this.channel1.isOpen());
        try {
            this.channel1.receive(dst);
            TestCase.fail("Should throw an IllegalArgumentException here.");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // OK.
        }
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerBufEmpty() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNonBlockNoServer(DatagramChannelTest.CAPACITY_NORMAL);
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_BlockNoServerNull() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        receiveNoServerNull();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerNull() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNoServerNull();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_BlockNoServerReadonly() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        receiveNoServerReadonly();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerReadonly() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNoServerReadonly();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerBufZero() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNonBlockNoServer(DatagramChannelTest.CAPACITY_ZERO);
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerBufNotEmpty() throws Exception {
        this.channel1.configureBlocking(false);
        connectWithoutServer();
        ByteBuffer dst = allocateNonEmptyBuf();
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerBufFull() throws Exception {
        this.channel1.configureBlocking(false);
        connectWithoutServer();
        ByteBuffer dst = allocateFullBuf();
        TestCase.assertNull(this.channel1.receive(dst));
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_BlockNoServerChannelClose() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        receiveNoServerChannelClose();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerChannelClose() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNoServerChannelClose();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_BlockNoServerCloseNull() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        receiveNoServerChannelCloseNull();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerCloseNull() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNoServerChannelCloseNull();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_NonBlockNoServerCloseReadonly() throws Exception {
        this.channel1.configureBlocking(false);
        receiveNoServerChannelCloseReadonly();
    }

    /**
     * Test method for 'DatagramChannelImpl.receive(ByteBuffer)'
     *
     * @throws Exception
     * 		
     */
    public void testReceive_BlockNoServerCloseReadonly() throws Exception {
        TestCase.assertTrue(this.channel1.isBlocking());
        receiveNoServerChannelCloseReadonly();
    }

    /* Test method for 'DatagramChannelImpl.send(ByteBuffer, SocketAddress)' */
    public void testSend_NoServerBlockingCommon() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        sendDataBlocking(localAddr1, writeBuf);
    }

    public void testSend_NoServerNonblockingCommon() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        sendDataNonBlocking(localAddr1, writeBuf);
    }

    public void testSend_NoServerTwice() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        sendDataBlocking(localAddr1, writeBuf);
        // can not buffer twice!
        TestCase.assertEquals(0, this.channel1.send(writeBuf, localAddr1));
        try {
            channel1.send(writeBuf, localAddr2);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // correct
        }
    }

    public void testSend_NoServerNonBlockingTwice() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        sendDataNonBlocking(localAddr1, writeBuf);
        // can not buffer twice!
        TestCase.assertEquals(0, this.channel1.send(writeBuf, localAddr1));
        try {
            channel1.send(writeBuf, localAddr2);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // correct
        }
    }

    public void testSend_NoServerBufNull() throws IOException {
        try {
            sendDataBlocking(localAddr1, null);
            TestCase.fail("Should throw a NPE here.");
        } catch (NullPointerException e) {
            // correct
        }
    }

    public void testSend_NoServerBufNullTwice() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            sendDataBlocking(localAddr1, null);
            TestCase.fail("Should throw a NPE here.");
        } catch (NullPointerException e) {
            // correct
        }
        sendDataBlocking(localAddr1, writeBuf);
        try {
            channel1.send(null, localAddr2);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
    }

    public void testSend_NoServerAddrNull() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            sendDataBlocking(null, writeBuf);
            TestCase.fail("Should throw a NPE here.");
        } catch (NullPointerException e) {
            // correct
        }
    }

    public void testSend_NoServerAddrNullTwice() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            sendDataBlocking(null, writeBuf);
            TestCase.fail("Should throw a NPE here.");
        } catch (NullPointerException e) {
            // correct
        }
        sendDataBlocking(localAddr1, writeBuf);
        try {
            channel1.send(writeBuf, null);
            TestCase.fail("Should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
    }

    // -------------------------------------------------------------------
    // Test for receive()and send(): Send and Receive with Real Data
    // -------------------------------------------------------------------
    public void testReceiveSend_Block_Normal() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByChannel("some normal string in testReceiveSend_Normal", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "some normal string in testReceiveSend_Normal");
    }

    public void testReceiveSend_Block_NotBound() throws Exception {
        // not bound
        sendByChannel("some normal string in testReceiveSend_Normal", localAddr2);
        ByteBuffer buf = ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL);
        TestCase.assertNull(channel1.receive(buf));
        TestCase.assertFalse(channel1.socket().isBound());
    }

    public void testReceiveSend_NonBlock_NotBound() throws Exception {
        // not bound
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        sendByChannel("some normal string in testReceiveSend_Normal", localAddr2);
        ByteBuffer buf = ByteBuffer.wrap(new byte[DatagramChannelTest.CAPACITY_NORMAL]);
        TestCase.assertNull(((InetSocketAddress) (this.channel1.receive(buf))));
    }

    public void testReceiveSend_Block_Normal_S2C() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByDatagramSocket("some normal string in testReceiveSend_Normal_S2C", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "some normal string in testReceiveSend_Normal_S2C");
    }

    public void testReceiveSend_Block_Normal_C2S() throws Exception {
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        String str1 = "some normal string in testReceiveSend_Normal_C2S";
        sendByChannel(str1, localAddr2);
        receiveByDatagramSocket(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, str1);
    }

    public void testReceiveSend_NonBlock_Normal_C2S() throws Exception {
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        String str1 = "some normal string in testReceiveSend_Normal_C2S";
        sendByChannel(str1, localAddr2);
        receiveByDatagramSocket(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, str1);
    }

    public void testReceiveSend_Normal_S2S() throws Exception {
        String msg = "normal string in testReceiveSend_Normal_S2S";
        this.datagramSocket1 = new DatagramSocket(testPort);
        DatagramPacket rdp = new DatagramPacket(msg.getBytes(), msg.length(), localAddr2);
        datagramSocket2 = new DatagramSocket(localAddr2.getPort());
        this.datagramSocket1.send(rdp);
        byte[] buf = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        this.datagramSocket2.setSoTimeout(DatagramChannelTest.TIME_UNIT);
        rdp = new DatagramPacket(buf, buf.length);
        this.datagramSocket2.receive(rdp);
        TestCase.assertEquals(new String(buf, 0, DatagramChannelTest.CAPACITY_NORMAL).trim(), msg);
    }

    public void testReceiveSend_Block_Empty() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByChannel("", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_NonBlock_Empty() throws Exception {
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        this.channel1.socket().bind(localAddr2);
        sendByChannel("", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_Block_Empty_S2C() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByDatagramSocket("", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_NonBlock_Empty_S2C() throws Exception {
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        this.channel1.socket().bind(localAddr2);
        sendByDatagramSocket("", localAddr2);
        receiveByChannel(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_Block_Empty_C2S() throws Exception {
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        sendByChannel("", localAddr2);
        receiveByDatagramSocket(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_NonBlock_Empty_C2S() throws Exception {
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        sendByChannel("", localAddr2);
        receiveByDatagramSocket(DatagramChannelTest.CAPACITY_NORMAL, localAddr2, "");
    }

    public void testReceiveSend_Empty_S2S() throws Exception {
        String msg = "";
        this.datagramSocket1 = new DatagramSocket(testPort);
        DatagramPacket rdp = new DatagramPacket(msg.getBytes(), msg.length(), localAddr2);
        datagramSocket2 = new DatagramSocket(localAddr2.getPort());
        this.datagramSocket1.send(rdp);
        byte[] buf = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        this.datagramSocket2.setSoTimeout(DatagramChannelTest.TIME_UNIT);
        rdp = new DatagramPacket(buf, buf.length);
        this.datagramSocket2.receive(rdp);
        TestCase.assertEquals(new String(buf, 0, DatagramChannelTest.CAPACITY_NORMAL).trim(), msg);
    }

    public void testReceiveSend_Block_Oversize() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByChannel("0123456789", localAddr2);
        receiveByChannel(5, localAddr2, "01234");
    }

    public void testReceiveSend_Block_Oversize_C2S() throws Exception {
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        sendByChannel("0123456789", localAddr2);
        receiveByDatagramSocket(5, localAddr2, "01234");
    }

    public void testReceiveSend_NonBlock_Oversize_C2S() throws Exception {
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        this.datagramSocket1 = new DatagramSocket(localAddr2.getPort());
        sendByChannel("0123456789", localAddr2);
        receiveByDatagramSocket(5, localAddr2, "01234");
    }

    public void testReceiveSend_Block_Oversize_S2C() throws Exception {
        this.channel1.socket().bind(localAddr2);
        sendByDatagramSocket("0123456789", localAddr2);
        receiveByChannel(5, localAddr2, "01234");
    }

    public void testReceiveSend_8K() throws Exception {
        StringBuffer str8k = new StringBuffer();
        for (int i = 0; i < (8 * (DatagramChannelTest.CAPACITY_1KB)); i++) {
            str8k.append('a');
        }
        String str = str8k.toString();
        this.channel1.socket().bind(localAddr2);
        sendByChannel(str, localAddr2);
        receiveByChannel((8 * (DatagramChannelTest.CAPACITY_1KB)), localAddr2, str);
    }

    public void testReceiveSend_64K() throws Exception {
        StringBuffer str64k = new StringBuffer();
        for (int i = 0; i < (DatagramChannelTest.CAPACITY_64KB); i++) {
            str64k.append('a');
        }
        String str = str64k.toString();
        try {
            Thread.sleep(DatagramChannelTest.TIME_UNIT);
            channel2.send(ByteBuffer.wrap(str.getBytes()), localAddr1);
            TestCase.fail("Should throw SocketException!");
        } catch (SocketException e) {
            // expected
        }
    }

    public void testRead_NoSecurity() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL);
        String strHello = "hello";
        localAddr1 = new InetSocketAddress("127.0.0.1", testPort);
        this.channel1.socket().bind(localAddr1);
        this.channel2.socket().bind(localAddr2);
        this.channel1.connect(localAddr2);
        this.channel2.send(ByteBuffer.wrap(strHello.getBytes()), localAddr1);
        TestCase.assertEquals(strHello.length(), this.channel1.read(buf));
        DatagramChannelTest.assertAscii(buf, strHello);
    }

    public void testReceive_Peek_NoSecurity_Nonblocking() throws Exception {
        String strHello = "hello";
        localAddr1 = new InetSocketAddress("127.0.0.1", testPort);
        this.channel1.socket().bind(localAddr1);
        sendByChannel(strHello, localAddr1);
        this.channel1.configureBlocking(false);
        // for accepted addr, no problem.
        ByteBuffer buf = ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress source = ((InetSocketAddress) (this.channel1.receive(buf)));
        TestCase.assertEquals(localAddr1.getAddress(), source.getAddress());
        DatagramChannelTest.assertAscii(buf, strHello);
    }

    /* Test method for 'DatagramChannelImpl.write(ByteBuffer)' */
    public void testWriteByteBuffer_Block() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        connectWriteBuf(localAddr1, writeBuf);
    }

    public void testWriteByteBuffer_NonBlock() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        this.channel1.configureBlocking(false);
        connectWriteBuf(localAddr1, writeBuf);
    }

    public void testWriteByteBuffer_Block_closed() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        noconnectWrite(writeBuf);
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.close();
        try {
            channel1.write(writeBuf);
            TestCase.fail("should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // correct
        }
    }

    public void testWriteByteBuffer_NonBlock_closed() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        // non block mode
        this.channel1.configureBlocking(false);
        noconnectWrite(writeBuf);
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.close();
        try {
            channel1.write(writeBuf);
            TestCase.fail("should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // correct
        }
    }

    public void testWriteByteBuffer_Block_BufNull() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(0);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(0, this.channel1.write(writeBuf));
        datagramSocket1.close();
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
    }

    public void testWriteByteBuffer_NonBlock_BufNull() throws IOException {
        ByteBuffer writeBuf = ByteBuffer.allocateDirect(0);
        InetSocketAddress ipAddr = localAddr1;
        // non block mode
        this.channel1.configureBlocking(false);
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(0, this.channel1.write(writeBuf));
        datagramSocket1.close();
        try {
            this.channel1.write(((ByteBuffer) (null)));
            TestCase.fail("Should throw NPE.");
        } catch (NullPointerException e) {
            // correct
        }
    }

    /* Test method for 'DatagramChannelImpl.write(ByteBuffer[], int, int)' */
    public void testWriteByteBufferArrayIntInt_Block() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        writeBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.write(writeBuf, 0, 2);
            TestCase.fail("Should throw NotYetConnectedException.");
        } catch (NotYetConnectedException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        TestCase.assertEquals(((DatagramChannelTest.CAPACITY_NORMAL) * 2), this.channel1.write(writeBuf, 0, 2));
        // cannot be buffered again!
        TestCase.assertEquals(0, this.channel1.write(writeBuf, 0, 1));
    }

    public void testWriteByteBufferArrayIntInt_NonBlock() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        writeBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        // non-block mode
        this.channel1.configureBlocking(false);
        try {
            this.channel1.write(writeBuf, 0, 2);
            TestCase.fail("Should throw NotYetConnectedException.");
        } catch (NotYetConnectedException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        TestCase.assertEquals(((DatagramChannelTest.CAPACITY_NORMAL) * 2), this.channel1.write(writeBuf, 0, 2));
        // cannot be buffered again!
        TestCase.assertEquals(0, this.channel1.write(writeBuf, 0, 1));
    }

    public void testWriteByteBufferArrayIntInt_NoConnectIndexBad() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        writeBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.write(writeBuf, (-1), 2);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        try {
            this.channel1.write(writeBuf, 0, (-1));
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        TestCase.assertEquals(((DatagramChannelTest.CAPACITY_NORMAL) * 2), this.channel1.write(writeBuf, 0, 2));
        // cannot be buffered again!
        TestCase.assertEquals(0, this.channel1.write(writeBuf, 0, 1));
    }

    public void testWriteByteBufferArrayIntInt_ConnectedIndexBad() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        writeBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        try {
            this.channel1.write(writeBuf, (-1), 2);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        try {
            this.channel1.write(writeBuf, 0, (-1));
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
    }

    public void testWriteByteBufferArrayIntInt_BufNullNoConnect() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            this.channel1.write(null, 0, 2);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            this.channel1.write(writeBuf, (-1), 2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            this.channel1.write(writeBuf, 0, 3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    public void testWriteByteBufferArrayIntInt_BufNullConnect() throws IOException {
        ByteBuffer[] writeBuf = new ByteBuffer[2];
        writeBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        try {
            this.channel1.write(null, 0, 2);
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            this.channel1.write(writeBuf, 0, 3);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        datagramSocket1.close();
        try {
            this.channel1.write(null, 0, 2);
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
    }

    // -------------------------------------------------------------------
    // Test for read()
    // -------------------------------------------------------------------
    /* Test method for 'DatagramChannelImpl.read(ByteBuffer)' */
    public void testReadByteBuffer() throws IOException {
        ByteBuffer readBuf = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            this.channel1.read(readBuf);
            TestCase.fail("should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
        this.channel1.connect(localAddr1);
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.configureBlocking(false);
        // note : blocking-mode will make the read process endless!
        TestCase.assertEquals(0, this.channel1.read(readBuf));
        this.channel1.close();
        try {
            this.channel1.read(readBuf);
            TestCase.fail("Should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // OK.
        }
    }

    public void testReadByteBuffer_bufNull() throws IOException {
        ByteBuffer readBuf = ByteBuffer.allocateDirect(0);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.read(readBuf);
            TestCase.fail("should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        try {
            channel1.read(((ByteBuffer) (null)));
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        this.channel1.configureBlocking(false);
        // note : blocking-mode will make the read process endless!
        TestCase.assertEquals(0, this.channel1.read(readBuf));
        datagramSocket1.close();
    }

    /* Test method for 'DatagramChannelImpl.read(ByteBuffer[], int, int)' */
    public void testReadByteBufferArrayIntInt() throws IOException {
        ByteBuffer[] readBuf = new ByteBuffer[2];
        readBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        readBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.read(readBuf, 0, 2);
            TestCase.fail("should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.configureBlocking(false);
        // note : blocking-mode will make the read process endless!
        TestCase.assertEquals(0, this.channel1.read(readBuf, 0, 1));
        TestCase.assertEquals(0, this.channel1.read(readBuf, 0, 2));
        datagramSocket1.close();
    }

    public void testReadByteBufferArrayIntInt_exceptions() throws IOException {
        // regression test for HARMONY-932
        try {
            DatagramChannel.open().read(new ByteBuffer[]{  }, 2, Integer.MAX_VALUE);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            DatagramChannel.open().read(new ByteBuffer[]{  }, (-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException expected) {
        }
        try {
            DatagramChannel.open().read(((ByteBuffer[]) (null)), 0, 0);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testReadByteBufferArrayIntInt_BufNull() throws IOException {
        ByteBuffer[] readBuf = new ByteBuffer[2];
        readBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        InetSocketAddress ipAddr = localAddr1;
        try {
            this.channel1.read(null, 0, 0);
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        this.channel1.connect(ipAddr);
        TestCase.assertTrue(this.channel1.isConnected());
        this.channel1.configureBlocking(false);
        // note : blocking-mode will make the read process endless!
        try {
            this.channel1.read(null, 0, 0);
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        TestCase.assertEquals(0, this.channel1.read(readBuf, 0, 1));
        try {
            this.channel1.read(readBuf, 0, 2);
            TestCase.fail("should throw NPE");
        } catch (NullPointerException e) {
            // correct
        }
        try {
            this.channel1.read(readBuf, 0, 3);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // correct
        }
        datagramSocket1.close();
    }

    // -------------------------------------------------------------------
    // test read and write
    // -------------------------------------------------------------------
    public void testReadWrite_configureBlock() throws Exception {
        byte[] targetArray = new byte[2];
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(DatagramChannelTest.TIME_UNIT);
                    channel1.configureBlocking(false);
                    channel1.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }.start();
        try {
            this.channel1.read(targetBuf);
            TestCase.fail("should throw AsynchronousCloseException");
        } catch (AsynchronousCloseException e) {
            // ok
        }
    }

    public void testReadWrite_Block_Zero() throws Exception {
        byte[] sourceArray = new byte[0];
        byte[] targetArray = new byte[0];
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(0, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        int readCount = this.channel2.read(targetBuf);
        TestCase.assertEquals(0, readCount);
    }

    public void testReadWrite_Block_Normal() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        readWriteReadData(this.channel1, sourceArray, this.channel2, targetArray, DatagramChannelTest.CAPACITY_NORMAL, "testReadWrite_Block_Normal");
    }

    public void testReadWrite_Block_Empty() throws Exception {
        // empty buf
        byte[] sourceArray = "".getBytes();
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(0, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        // empty message let the reader blocked
        closeBlockedReaderChannel2(targetBuf);
    }

    public void testReadWrite_changeBlock_Empty() throws Exception {
        // empty buf
        byte[] sourceArray = "".getBytes();
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(0, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        // empty message let the reader blocked
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(DatagramChannelTest.TIME_UNIT);
                    channel2.configureBlocking(false);
                    Thread.sleep(((DatagramChannelTest.TIME_UNIT) * 5));
                    channel2.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }.start();
        try {
            TestCase.assertTrue(this.channel2.isBlocking());
            this.channel2.read(targetBuf);
            TestCase.fail("Should throw AsynchronousCloseException");
        } catch (AsynchronousCloseException e) {
            TestCase.assertFalse(this.channel2.isBlocking());
            // OK.
        }
    }

    public void testReadWrite_Block_8KB() throws Exception {
        byte[] sourceArray = new byte[(DatagramChannelTest.CAPACITY_1KB) * 8];
        byte[] targetArray = new byte[(DatagramChannelTest.CAPACITY_1KB) * 8];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        readWriteReadData(this.channel1, sourceArray, this.channel2, targetArray, (8 * (DatagramChannelTest.CAPACITY_1KB)), "testReadWrite_Block_8KB");
    }

    public void testReadWrite_Block_64K() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_64KB];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        try {
            channel1.write(sourceBuf);
            TestCase.fail("Should throw IOException");
        } catch (IOException e) {
            // too big
        }
    }

    public void testReadWrite_Block_DifferentAddr() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr1);// the different addr

        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        // the wrong connected addr will make the read blocked.
        // we close the blocked channel
        closeBlockedReaderChannel2(targetBuf);
    }

    public void testReadWrite_Block_WriterNotBind() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        closeBlockedReaderChannel2(targetBuf);
    }

    public void testReadWrite_Block_WriterBindLater() throws Exception {
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        // bind and connect
        // writer channel1 is bound later
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(DatagramChannelTest.TIME_UNIT);
                    // bind later
                    byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
                    for (int i = 0; i < (sourceArray.length); i++) {
                        sourceArray[i] = ((byte) (i));
                    }
                    channel1.socket().bind(localAddr2);
                    channel1.connect(localAddr1);
                    // write later
                    ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
                    TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, channel1.write(sourceBuf));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }.start();
        int count = 0;
        int total = 0;
        long beginTime = System.currentTimeMillis();
        while ((total < (DatagramChannelTest.CAPACITY_NORMAL)) && ((count = channel2.read(targetBuf)) != (-1))) {
            total = total + count;
            // 3s timeout to avoid dead loop
            if (((System.currentTimeMillis()) - beginTime) > 3000) {
                break;
            }
        } 
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, total);
        TestCase.assertEquals(targetBuf.position(), total);
        targetBuf.flip();
        targetArray = targetBuf.array();
        for (int i = 0; i < (targetArray.length); i++) {
            TestCase.assertEquals(targetArray[i], ((byte) (i)));
        }
    }

    public void testReadWrite_Block_ReaderNotBind() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        // reader channel2 is not bound
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        closeBlockedReaderChannel2(targetBuf);
    }

    // -------------------------------------------------------------------
    // Test read and write in non-block mode.
    // -------------------------------------------------------------------
    public void testReadWrite_NonBlock_Normal() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        readWriteReadData(this.channel1, sourceArray, this.channel2, targetArray, DatagramChannelTest.CAPACITY_NORMAL, "testReadWrite_NonBlock_Normal");
    }

    public void testReadWrite_NonBlock_8KB() throws Exception {
        byte[] sourceArray = new byte[(DatagramChannelTest.CAPACITY_1KB) * 8];
        byte[] targetArray = new byte[(DatagramChannelTest.CAPACITY_1KB) * 8];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        readWriteReadData(this.channel1, sourceArray, this.channel2, targetArray, (8 * (DatagramChannelTest.CAPACITY_1KB)), "testReadWrite_NonBlock_8KB");
    }

    public void testReadWrite_NonBlock_DifferentAddr() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr1);// the different addr

        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        TestCase.assertEquals(0, this.channel2.read(targetBuf));
    }

    public void testReadWrite_NonBlock_WriterNotBind() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        // bind and connect
        this.channel1.connect(localAddr1);
        this.channel2.socket().bind(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        TestCase.assertEquals(0, this.channel2.read(targetBuf));
    }

    public void testReadWrite_NonBlock_ReaderNotBind() throws Exception {
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        byte[] targetArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        for (int i = 0; i < (sourceArray.length); i++) {
            sourceArray[i] = ((byte) (i));
        }
        this.channel1.configureBlocking(false);
        this.channel2.configureBlocking(false);
        // bind and connect
        this.channel1.socket().bind(localAddr2);
        this.channel1.connect(localAddr1);
        this.channel2.connect(localAddr2);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, this.channel1.write(sourceBuf));
        // read
        ByteBuffer targetBuf = ByteBuffer.wrap(targetArray);
        TestCase.assertEquals(0, this.channel2.read(targetBuf));
    }

    public void test_write_LBuffer_positioned() throws Exception {
        // Regression test for Harmony-683
        int position = 16;
        DatagramChannel dc = DatagramChannel.open();
        byte[] sourceArray = new byte[DatagramChannelTest.CAPACITY_NORMAL];
        dc.connect(localAddr1);
        // write
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        sourceBuf.position(position);
        TestCase.assertEquals(((DatagramChannelTest.CAPACITY_NORMAL) - position), dc.write(sourceBuf));
    }

    public void test_send_LBuffer_LSocketAddress_PositionNotZero() throws Exception {
        // regression test for Harmony-701
        int CAPACITY_NORMAL = 256;
        int position = 16;
        DatagramChannel dc = DatagramChannel.open();
        byte[] sourceArray = new byte[CAPACITY_NORMAL];
        // send ByteBuffer whose position is not zero
        ByteBuffer sourceBuf = ByteBuffer.wrap(sourceArray);
        sourceBuf.position(position);
        int ret = dc.send(sourceBuf, localAddr1);
        // assert send (256 - 16) bytes
        TestCase.assertEquals((CAPACITY_NORMAL - position), ret);
        // assert the position of ByteBuffer has been set
        TestCase.assertEquals(CAPACITY_NORMAL, sourceBuf.position());
    }

    /**
     *
     *
     * @unknown DatagramChannel#read(ByteBuffer[])
     */
    public void test_read_$LByteBuffer() throws Exception {
        // regression test for Harmony-754
        channel2.socket().bind(localAddr1);
        channel1.socket().bind(localAddr2);
        channel1.connect(localAddr1);
        channel2.connect(localAddr2);
        channel2.write(ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL));
        ByteBuffer[] readBuf = new ByteBuffer[2];
        readBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        readBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        channel1.configureBlocking(true);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, channel1.read(readBuf));
    }

    /**
     *
     *
     * @unknown DatagramChannel#read(ByteBuffer[],int,int)
     */
    public void test_read_$LByteBufferII() throws Exception {
        // regression test for Harmony-754
        channel2.socket().bind(localAddr1);
        channel1.socket().bind(localAddr2);
        channel1.connect(localAddr1);
        channel2.connect(localAddr2);
        channel2.write(ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL));
        ByteBuffer[] readBuf = new ByteBuffer[2];
        readBuf[0] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        readBuf[1] = ByteBuffer.allocateDirect(DatagramChannelTest.CAPACITY_NORMAL);
        channel1.configureBlocking(true);
        TestCase.assertEquals(DatagramChannelTest.CAPACITY_NORMAL, channel1.read(readBuf, 0, 2));
    }

    /**
     *
     *
     * @unknown DatagramChannel#read(ByteBuffer)
     */
    public void test_read_LByteBuffer_closed_nullBuf() throws Exception {
        // regression test for Harmony-754
        ByteBuffer c = null;
        DatagramChannel channel = DatagramChannel.open();
        channel.close();
        try {
            channel.read(c);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown DatagramChannel#read(ByteBuffer)
     */
    public void test_read_LByteBuffer_NotConnected_nullBuf() throws Exception {
        // regression test for Harmony-754
        ByteBuffer c = null;
        DatagramChannel channel = DatagramChannel.open();
        try {
            channel.read(c);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown DatagramChannel#read(ByteBuffer)
     */
    public void test_read_LByteBuffer_readOnlyBuf() throws Exception {
        // regression test for Harmony-754
        ByteBuffer c = ByteBuffer.allocate(1);
        DatagramChannel channel = DatagramChannel.open();
        try {
            channel.read(c.asReadOnlyBuffer());
            TestCase.fail("Should throw NotYetConnectedException");
        } catch (NotYetConnectedException e) {
        } catch (IllegalArgumentException e) {
            // expected
        }
        channel.connect(localAddr1);
        try {
            channel.read(c.asReadOnlyBuffer());
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown DatagramChannel#send(ByteBuffer, SocketAddress)
     */
    public void test_send_LByteBuffer_LSocketAddress_closed() throws IOException {
        // regression test for Harmony-913
        channel1.close();
        ByteBuffer buf = ByteBuffer.allocate(DatagramChannelTest.CAPACITY_NORMAL);
        try {
            channel1.send(buf, localAddr1);
            TestCase.fail("Should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // pass
        }
        try {
            channel1.send(null, localAddr1);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // pass
        }
        try {
            channel1.send(buf, null);
            TestCase.fail("Should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // pass
        }
        try {
            channel1.send(null, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // pass
        }
    }

    /**
     *
     *
     * @unknown DatagramChannel#socket()
     */
    public void test_socket_IllegalBlockingModeException() throws Exception {
        // regression test for Harmony-1036
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        DatagramSocket socket = channel.socket();
        try {
            socket.send(null);
            TestCase.fail("should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        try {
            socket.receive(null);
            TestCase.fail("should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
    }

    public void test_bounded_harmony6493() throws IOException {
        DatagramChannel server = DatagramChannel.open();
        InetSocketAddress addr = new InetSocketAddress("localhost", 0);
        server.socket().bind(addr);
        SocketAddress boundedAddress = server.socket().getLocalSocketAddress();
        DatagramChannel client = DatagramChannel.open();
        ByteBuffer sent = ByteBuffer.allocate(1024);
        sent.put("test".getBytes());
        sent.flip();
        client.send(sent, boundedAddress);
        TestCase.assertTrue(client.socket().isBound());
        server.close();
        client.close();
    }
}

