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
package org.apache.harmony.tests.java.nio.channels.spi;


import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;
import junit.framework.TestCase;


/**
 * Tests for AbstractSelectableChannel
 */
public class AbstractSelectableChannelTest extends TestCase {
    private AbstractSelectableChannelTest.MockSelectableChannel testChannel;

    /**
     *
     *
     * @unknown AbstractSelectableChannel#implCloseChannel()
     */
    public void test_implClose() throws IOException {
        testChannel.isImplCloseSelectableChannelCalled = false;
        testChannel.implCloseSelectableChannelCount = 0;
        testChannel.close();
        TestCase.assertFalse(testChannel.isOpen());
        TestCase.assertTrue(testChannel.isImplCloseSelectableChannelCalled);
        TestCase.assertEquals(1, testChannel.implCloseSelectableChannelCount);
        testChannel = new AbstractSelectableChannelTest.MockSelectableChannel(SelectorProvider.provider());
        testChannel.isImplCloseSelectableChannelCalled = false;
        testChannel.implCloseSelectableChannelCount = 0;
        // close twice.
        // make sure implCloseSelectableChannelCount is called only once.
        testChannel.close();
        testChannel.close();
        TestCase.assertFalse(testChannel.isOpen());
        TestCase.assertTrue(testChannel.isImplCloseSelectableChannelCalled);
        TestCase.assertEquals(1, testChannel.implCloseSelectableChannelCount);
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#provider()
     */
    public void test_provider() {
        SelectorProvider provider = testChannel.provider();
        TestCase.assertSame(SelectorProvider.provider(), provider);
        testChannel = new AbstractSelectableChannelTest.MockSelectableChannel(null);
        provider = testChannel.provider();
        TestCase.assertNull(provider);
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#isBlocking()
     */
    public void test_isBlocking() throws IOException {
        TestCase.assertTrue(testChannel.isBlocking());
        testChannel.configureBlocking(false);
        TestCase.assertFalse(testChannel.isBlocking());
        testChannel.configureBlocking(true);
        TestCase.assertTrue(testChannel.isBlocking());
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#blockingLock()
     */
    public void test_blockingLock() {
        Object gotObj = testChannel.blockingLock();
        TestCase.assertNotNull(gotObj);
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#register(Selector, int, Object)
     */
    public void test_register_LSelectorILObject() throws IOException {
        TestCase.assertFalse(testChannel.isRegistered());
        Selector acceptSelector1 = SelectorProvider.provider().openSelector();
        Selector acceptSelector2 = new MockAbstractSelector(SelectorProvider.provider());
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        SelectionKey acceptKey = sc.register(acceptSelector1, SelectionKey.OP_READ, null);
        TestCase.assertNotNull(acceptKey);
        TestCase.assertTrue(acceptKey.isValid());
        TestCase.assertSame(sc, acceptKey.channel());
        // test that sc.register invokes Selector.register()
        acceptKey = sc.register(acceptSelector2, SelectionKey.OP_READ, null);
        TestCase.assertNull(acceptKey);
        // Regression test to ensure acceptance of a selector with empty
        // interest set.
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        Selector selector = Selector.open();
        channel.register(selector, 0);
        selector.close();
        channel.close();
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#register(Selector, int, Object)
     */
    public void test_register_LSelectorILObject_IllegalArgument() throws IOException {
        Selector acceptSelector = SelectorProvider.provider().openSelector();
        TestCase.assertTrue(acceptSelector.isOpen());
        AbstractSelectableChannelTest.MockSelectableChannel msc = new AbstractSelectableChannelTest.MockSelectableChannel(SelectorProvider.provider());
        msc.configureBlocking(false);
        // in nonblocking mode
        try {
            // different SelectionKey with validOps
            msc.register(acceptSelector, SelectionKey.OP_READ, null);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            msc.register(null, 0, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        // in nonblocking mode, if selector closed
        acceptSelector.close();
        try {
            msc.register(acceptSelector, SelectionKey.OP_READ, null);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            msc.register(null, 0, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            msc.register(acceptSelector, 0, null);
            TestCase.fail("Should throw IllegalSelectorException");
        } catch (IllegalSelectorException e) {
            // expected
        }
        acceptSelector = SelectorProvider.provider().openSelector();
        // test in blocking mode
        msc.configureBlocking(true);
        try {
            msc.register(acceptSelector, SelectionKey.OP_READ, null);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            msc.register(null, 0, null);
            TestCase.fail("Should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        acceptSelector.close();
        // in blocking mode, if selector closed
        try {
            msc.register(acceptSelector, SelectionKey.OP_READ, null);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            msc.register(null, 0, null);
            TestCase.fail("Should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
        // register with an object
        Object argObj = new Object();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        try {
            sc.register(null, SelectionKey.OP_READ, argObj);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        // if channel closed
        msc.close();
        try {
            msc.register(acceptSelector, SelectionKey.OP_READ, null);
            TestCase.fail("Should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#keyFor(Selector)
     */
    public void test_keyfor_LSelector() throws Exception {
        SocketChannel sc = SocketChannel.open();
        Object argObj = new Object();
        sc.configureBlocking(false);
        Selector acceptSelector = SelectorProvider.provider().openSelector();
        Selector acceptSelectorOther = SelectorProvider.provider().openSelector();
        SelectionKey acceptKey = sc.register(acceptSelector, SelectionKey.OP_READ, argObj);
        TestCase.assertEquals(sc.keyFor(acceptSelector), acceptKey);
        SelectionKey acceptKeyObjNull = sc.register(acceptSelector, SelectionKey.OP_READ, null);
        TestCase.assertSame(sc.keyFor(acceptSelector), acceptKeyObjNull);
        TestCase.assertSame(acceptKeyObjNull, acceptKey);
        SelectionKey acceptKeyOther = sc.register(acceptSelectorOther, SelectionKey.OP_READ, null);
        TestCase.assertSame(sc.keyFor(acceptSelectorOther), acceptKeyOther);
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#configureBlocking(boolean)
     */
    public void test_configureBlocking_Z_IllegalBlockingMode() throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        Selector acceptSelector = SelectorProvider.provider().openSelector();
        SelectionKey acceptKey = sc.register(acceptSelector, SelectionKey.OP_READ, null);
        TestCase.assertEquals(sc.keyFor(acceptSelector), acceptKey);
        SelectableChannel getChannel = sc.configureBlocking(false);
        TestCase.assertEquals(getChannel, sc);
        try {
            sc.configureBlocking(true);
            TestCase.fail("Should throw IllegalBlockingModeException");
        } catch (IllegalBlockingModeException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown AbstractSelectableChannel#configureBlocking(boolean)
     */
    public void test_configureBlocking_Z() throws Exception {
        AbstractSelectableChannelTest.MockSelectableChannel mock = new AbstractSelectableChannelTest.MockSelectableChannel(SelectorProvider.provider());
        // default blocking mode is true
        // the implConfigureBlocking is only invoked if the given mode is different with current one
        mock.configureBlocking(true);
        TestCase.assertFalse(mock.implConfigureBlockingCalled);
        mock.configureBlocking(false);
        TestCase.assertTrue(mock.implConfigureBlockingCalled);
    }

    private class MockSelectableChannel extends AbstractSelectableChannel {
        private boolean isImplCloseSelectableChannelCalled = false;

        private int implCloseSelectableChannelCount = 0;

        private boolean implConfigureBlockingCalled = false;

        public MockSelectableChannel(SelectorProvider arg0) {
            super(arg0);
        }

        protected void implCloseSelectableChannel() throws IOException {
            isImplCloseSelectableChannelCalled = true;
            ++(implCloseSelectableChannelCount);
        }

        protected void implConfigureBlocking(boolean arg0) throws IOException {
            implConfigureBlockingCalled = true;
        }

        public int validOps() {
            return SelectionKey.OP_ACCEPT;
        }
    }
}

