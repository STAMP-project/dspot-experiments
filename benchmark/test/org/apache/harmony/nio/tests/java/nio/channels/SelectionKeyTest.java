/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.nio.tests.java.nio.channels;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import junit.framework.TestCase;
import tests.support.Support_PortManager;


/* Tests for SelectionKey and its default implementation */
public class SelectionKeyTest extends TestCase {
    Selector selector;

    SocketChannel sc;

    SelectionKey selectionKey;

    private static String LOCAL_ADDR = "127.0.0.1";

    static class MockSelectionKey extends SelectionKey {
        private int interestOps;

        MockSelectionKey(int ops) {
            interestOps = ops;
        }

        public void cancel() {
            // do nothing
        }

        public SelectableChannel channel() {
            return null;
        }

        public int interestOps() {
            return 0;
        }

        public SelectionKey interestOps(int operations) {
            return null;
        }

        public boolean isValid() {
            return true;
        }

        public int readyOps() {
            return interestOps;
        }

        public Selector selector() {
            return null;
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#attach(Object)
     */
    public void test_attach() {
        SelectionKeyTest.MockSelectionKey mockSelectionKey = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        // no previous, return null
        Object o = new Object();
        Object check = mockSelectionKey.attach(o);
        TestCase.assertNull(check);
        // null parameter is ok
        check = mockSelectionKey.attach(null);
        TestCase.assertSame(o, check);
        check = mockSelectionKey.attach(o);
        TestCase.assertNull(check);
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#attachment()
     */
    public void test_attachment() {
        SelectionKeyTest.MockSelectionKey mockSelectionKey = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        TestCase.assertNull(mockSelectionKey.attachment());
        Object o = new Object();
        mockSelectionKey.attach(o);
        TestCase.assertSame(o, mockSelectionKey.attachment());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#channel()
     */
    public void test_channel() {
        TestCase.assertSame(sc, selectionKey.channel());
        // can be invoked even canceled
        selectionKey.cancel();
        TestCase.assertSame(sc, selectionKey.channel());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#interestOps()
     */
    public void test_interestOps() {
        TestCase.assertEquals(SelectionKey.OP_CONNECT, selectionKey.interestOps());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#interestOps(int)
     */
    public void test_interestOpsI() {
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        TestCase.assertEquals(SelectionKey.OP_WRITE, selectionKey.interestOps());
        try {
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);
            TestCase.fail("should throw IAE.");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
        try {
            selectionKey.interestOps((~(sc.validOps())));
            TestCase.fail("should throw IAE.");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
        try {
            selectionKey.interestOps((-1));
            TestCase.fail("should throw IAE.");
        } catch (IllegalArgumentException ex) {
            // expected;
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isValid()
     */
    public void test_isValid() {
        TestCase.assertTrue(selectionKey.isValid());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isValid()
     */
    public void test_isValid_KeyCancelled() {
        selectionKey.cancel();
        TestCase.assertFalse(selectionKey.isValid());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isValid()
     */
    public void test_isValid_ChannelColsed() throws IOException {
        sc.close();
        TestCase.assertFalse(selectionKey.isValid());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isValid()
     */
    public void test_isValid_SelectorClosed() throws IOException {
        selector.close();
        TestCase.assertFalse(selectionKey.isValid());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isAcceptable()
     */
    public void test_isAcceptable() throws IOException {
        SelectionKeyTest.MockSelectionKey mockSelectionKey1 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        TestCase.assertTrue(mockSelectionKey1.isAcceptable());
        SelectionKeyTest.MockSelectionKey mockSelectionKey2 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_CONNECT);
        TestCase.assertFalse(mockSelectionKey2.isAcceptable());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isConnectable()
     */
    public void test_isConnectable() {
        SelectionKeyTest.MockSelectionKey mockSelectionKey1 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_CONNECT);
        TestCase.assertTrue(mockSelectionKey1.isConnectable());
        SelectionKeyTest.MockSelectionKey mockSelectionKey2 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        TestCase.assertFalse(mockSelectionKey2.isConnectable());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isReadable()
     */
    public void test_isReadable() {
        SelectionKeyTest.MockSelectionKey mockSelectionKey1 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_READ);
        TestCase.assertTrue(mockSelectionKey1.isReadable());
        SelectionKeyTest.MockSelectionKey mockSelectionKey2 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        TestCase.assertFalse(mockSelectionKey2.isReadable());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#isWritable()
     */
    public void test_isWritable() {
        SelectionKeyTest.MockSelectionKey mockSelectionKey1 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_WRITE);
        TestCase.assertTrue(mockSelectionKey1.isWritable());
        SelectionKeyTest.MockSelectionKey mockSelectionKey2 = new SelectionKeyTest.MockSelectionKey(SelectionKey.OP_ACCEPT);
        TestCase.assertFalse(mockSelectionKey2.isWritable());
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#cancel()
     */
    public void test_cancel() {
        selectionKey.cancel();
        try {
            selectionKey.isAcceptable();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.isConnectable();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.isReadable();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.isWritable();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.readyOps();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.interestOps(SelectionKey.OP_CONNECT);
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
        try {
            selectionKey.interestOps();
            TestCase.fail("should throw CancelledKeyException.");
        } catch (CancelledKeyException ex) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#readyOps()
     */
    public void test_readyOps() throws IOException {
        int port = Support_PortManager.getNextPort();
        ServerSocket ss = new ServerSocket(port);
        try {
            sc.connect(new InetSocketAddress(SelectionKeyTest.LOCAL_ADDR, port));
            TestCase.assertEquals(0, selectionKey.readyOps());
            TestCase.assertFalse(selectionKey.isConnectable());
            selector.select();
            TestCase.assertEquals(SelectionKey.OP_CONNECT, selectionKey.readyOps());
        } finally {
            ss.close();
            ss = null;
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.SelectionKey#selector()
     */
    public void test_selector() {
        TestCase.assertSame(selector, selectionKey.selector());
        selectionKey.cancel();
        TestCase.assertSame(selector, selectionKey.selector());
    }
}

