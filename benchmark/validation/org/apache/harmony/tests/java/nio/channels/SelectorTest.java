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
package org.apache.harmony.tests.java.nio.channels;


import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;


/* Tests for Selector and its default implementation */
public class SelectorTest extends TestCase {
    private static final int WAIT_TIME = 100;

    private SocketAddress localAddress;

    private Selector selector;

    private ServerSocketChannel ssc;

    private enum SelectType {

        NULL,
        TIMEOUT,
        NOW;}

    /**
     *
     *
     * @unknown java.nio.channels.Selector#open()
     */
    public void test_open() throws IOException {
        TestCase.assertNotNull(selector);
    }

    /**
     *
     *
     * @unknown Selector#isOpen()
     */
    public void test_isOpen() throws IOException {
        TestCase.assertTrue(selector.isOpen());
        selector.close();
        TestCase.assertFalse(selector.isOpen());
    }

    /**
     *
     *
     * @unknown java.nio.channels.Selector#provider()
     */
    public void test_provider() throws IOException {
        // should be system default provider
        TestCase.assertNotNull(selector.provider());
        TestCase.assertSame(SelectorProvider.provider(), selector.provider());
    }

    /**
     *
     *
     * @unknown java.nio.channels.Selector#keys()
     */
    public void test_keys() throws IOException {
        SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
        Set<SelectionKey> keySet = selector.keys();
        Set<SelectionKey> keySet2 = selector.keys();
        TestCase.assertSame(keySet, keySet2);
        TestCase.assertEquals(1, keySet.size());
        SelectionKey key2 = keySet.iterator().next();
        TestCase.assertEquals(key, key2);
        // Any attempt to modify keys will cause UnsupportedOperationException
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        SelectionKey key3 = sc.register(selector, SelectionKey.OP_READ);
        try {
            keySet2.add(key3);
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keySet2.remove(key3);
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            keySet2.clear();
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        selector.close();
        try {
            selector.keys();
            TestCase.fail("should throw ClosedSelectorException");
        } catch (ClosedSelectorException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.Selector#keys()
     */
    public void test_selectedKeys() throws IOException {
        SocketChannel sc = SocketChannel.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        try {
            int count = 0;
            sc.connect(localAddress);
            count = blockingSelect(SelectorTest.SelectType.NULL, 0);
            TestCase.assertEquals(1, count);
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Set<SelectionKey> selectedKeys2 = selector.selectedKeys();
            TestCase.assertSame(selectedKeys, selectedKeys2);
            TestCase.assertEquals(1, selectedKeys.size());
            TestCase.assertEquals(ssc.keyFor(selector), selectedKeys.iterator().next());
            // add one key into selectedKeys
            try {
                selectedKeys.add(ssc.keyFor(selector));
                TestCase.fail("Should throw UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                // expected
            }
            // no exception should be thrown
            selectedKeys.clear();
            Set<SelectionKey> selectedKeys3 = selector.selectedKeys();
            TestCase.assertSame(selectedKeys, selectedKeys3);
            ssc.keyFor(selector).cancel();
            TestCase.assertEquals(0, selectedKeys.size());
            selector.close();
            try {
                selector.selectedKeys();
                TestCase.fail("should throw ClosedSelectorException");
            } catch (ClosedSelectorException e) {
                // expected
            }
        } finally {
            sc.close();
        }
    }

    /**
     *
     *
     * @unknown java.nio.channel.Selector#selectNow()
     */
    public void test_selectNow() throws IOException {
        assert_select_OP_ACCEPT(SelectorTest.SelectType.NOW, 0);
        assert_select_OP_CONNECT(SelectorTest.SelectType.NOW, 0);
        assert_select_OP_READ(SelectorTest.SelectType.NOW, 0);
        assert_select_OP_WRITE(SelectorTest.SelectType.NOW, 0);
    }

    /**
     *
     *
     * @unknown java.nio.channel.Selector#selectNow()
     */
    public void test_selectNow_SelectorClosed() throws IOException {
        assert_select_SelectorClosed(SelectorTest.SelectType.NOW, 0);
    }

    public void test_selectNow_Timeout() throws IOException {
        // make sure selectNow doesn't block
        selector.selectNow();
    }

    // J2ObjC b/64848117
    /**
     *
     *
     * @unknown java.nio.channel.Selector#select()
     */
    // public void test_select() throws IOException {
    // assert_select_OP_ACCEPT(SelectType.NULL, 0);
    // assert_select_OP_CONNECT(SelectType.NULL, 0);
    // assert_select_OP_READ(SelectType.NULL, 0);
    // assert_select_OP_WRITE(SelectType.NULL, 0);
    // }
    /**
     *
     *
     * @unknown java.nio.channel.Selector#select()
     */
    public void test_select_SelectorClosed() throws IOException {
        assert_select_SelectorClosed(SelectorTest.SelectType.NULL, 0);
    }

    // J2ObjC b/64848117
    /**
     *
     *
     * @unknown java.nio.channel.Selector#select(long)
     */
    // public void test_selectJ() throws IOException {
    // assert_select_OP_ACCEPT(SelectType.TIMEOUT, 0);
    // assert_select_OP_CONNECT(SelectType.TIMEOUT, 0);
    // assert_select_OP_READ(SelectType.TIMEOUT, 0);
    // assert_select_OP_WRITE(SelectType.TIMEOUT, 0);
    // 
    // assert_select_OP_ACCEPT(SelectType.TIMEOUT, WAIT_TIME);
    // assert_select_OP_CONNECT(SelectType.TIMEOUT, WAIT_TIME);
    // assert_select_OP_READ(SelectType.TIMEOUT, WAIT_TIME);
    // assert_select_OP_WRITE(SelectType.TIMEOUT, WAIT_TIME);
    // }
    /**
     *
     *
     * @unknown java.nio.channel.Selector#select(long)
     */
    public void test_selectJ_SelectorClosed() throws IOException {
        assert_select_SelectorClosed(SelectorTest.SelectType.TIMEOUT, 0);
        selector = Selector.open();
        assert_select_SelectorClosed(SelectorTest.SelectType.TIMEOUT, SelectorTest.WAIT_TIME);
    }

    /**
     *
     *
     * @unknown java.nio.channel.Selector#select(long)
     */
    public void test_selectJ_Exception() throws IOException {
        try {
            selector.select((-1));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void test_selectJ_Timeout() throws IOException {
        // make sure select(timeout) doesn't block
        selector.select(SelectorTest.WAIT_TIME);
    }

    public void test_selectJ_Empty_Keys() throws IOException {
        // regression test, see HARMONY-3888.
        // make sure select(long) does wait for specified amount of
        // time if keys.size() == 0 (initial state of selector).
        final long SELECT_TIMEOUT_MS = 2000;
        long t0 = System.nanoTime();
        selector.select(SELECT_TIMEOUT_MS);
        long t1 = System.nanoTime();
        long waitMs = ((t1 - t0) / 1000L) / 1000L;
        TestCase.assertTrue((waitMs >= SELECT_TIMEOUT_MS));
        TestCase.assertTrue((waitMs < (5 * SELECT_TIMEOUT_MS)));
    }

    /**
     *
     *
     * @unknown java.nio.channels.Selector#wakeup()
     */
    public void test_wakeup() throws IOException {
        /* make sure the test does not block on select */
        selector.wakeup();
        selectOnce(SelectorTest.SelectType.NULL, 0);
        selector.wakeup();
        selectOnce(SelectorTest.SelectType.TIMEOUT, 0);
        // try to wakeup select. The invocation sequence of wakeup and select
        // doesn't affect test result.
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(SelectorTest.WAIT_TIME);
                } catch (InterruptedException e) {
                    // ignore
                }
                selector.wakeup();
            }
        }.start();
        selectOnce(SelectorTest.SelectType.NULL, 0);
        // try to wakeup select. The invocation sequence of wakeup and select
        // doesn't affect test result.
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(SelectorTest.WAIT_TIME);
                } catch (InterruptedException e) {
                    // ignore
                }
                selector.wakeup();
            }
        }.start();
        selectOnce(SelectorTest.SelectType.TIMEOUT, 0);
    }

    public void test_keySetViewsModifications() throws IOException {
        Set<SelectionKey> keys = selector.keys();
        SelectionKey key1 = ssc.register(selector, SelectionKey.OP_ACCEPT);
        TestCase.assertTrue(keys.contains(key1));
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        SelectionKey key2 = sc.register(selector, SelectionKey.OP_READ);
        TestCase.assertTrue(keys.contains(key1));
        TestCase.assertTrue(keys.contains(key2));
        key1.cancel();
        TestCase.assertTrue(keys.contains(key1));
        selector.selectNow();
        TestCase.assertFalse(keys.contains(key1));
        TestCase.assertTrue(keys.contains(key2));
    }

    /**
     * This test cancels a key while selecting to verify that the cancelled
     * key set is processed both before and after the call to the underlying
     * operating system.
     */
    public void test_cancelledKeys() throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final AtomicBoolean complete = new AtomicBoolean();
        final Pipe pipe = Pipe.open();
        pipe.source().configureBlocking(false);
        final SelectionKey key = pipe.source().register(selector, SelectionKey.OP_READ);
        Thread thread = new Thread() {
            public void run() {
                try {
                    // make sure to call key.cancel() while the main thread is selecting
                    Thread.sleep(500);
                    key.cancel();
                    TestCase.assertFalse(key.isValid());
                    pipe.sink().write(ByteBuffer.allocate(4));// unblock select()

                } catch (Throwable e) {
                    failure.set(e);
                } finally {
                    complete.set(true);
                }
            }
        };
        TestCase.assertTrue(key.isValid());
        thread.start();
        do {
            TestCase.assertEquals(0, selector.select(5000));// blocks

            TestCase.assertEquals(0, selector.selectedKeys().size());
        } while (!(complete.get()) );// avoid spurious interrupts

        TestCase.assertFalse(key.isValid());
        thread.join();
        TestCase.assertNull(failure.get());
    }

    public void testOpChange() throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_CONNECT);
        try {
            sc.connect(localAddress);
            int count = blockingSelect(SelectorTest.SelectType.TIMEOUT, 100);
            TestCase.assertEquals(1, count);
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            TestCase.assertEquals(1, selectedKeys.size());
            SelectionKey key = selectedKeys.iterator().next();
            TestCase.assertEquals(sc.keyFor(selector), key);
            TestCase.assertEquals(SelectionKey.OP_CONNECT, key.readyOps());
            // select again, it should return 0
            count = selectOnce(SelectorTest.SelectType.TIMEOUT, 100);
            TestCase.assertEquals(0, count);
            // but selectedKeys remains the same as previous
            TestCase.assertSame(selectedKeys, selector.selectedKeys());
            sc.finishConnect();
            // same selector, but op is changed
            SelectionKey key1 = sc.register(selector, SelectionKey.OP_WRITE);
            TestCase.assertEquals(key, key1);
            count = blockingSelect(SelectorTest.SelectType.TIMEOUT, 100);
            TestCase.assertEquals(1, count);
            selectedKeys = selector.selectedKeys();
            TestCase.assertEquals(1, selectedKeys.size());
            key = selectedKeys.iterator().next();
            TestCase.assertEquals(key, key1);
            TestCase.assertEquals(SelectionKey.OP_WRITE, key.readyOps());
            selectedKeys.clear();
        } finally {
            try {
                ssc.accept().close();
            } catch (Exception e) {
                // do nothing
            }
            try {
                sc.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}

