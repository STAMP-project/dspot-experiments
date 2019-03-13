/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.io;


import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class NIOTest {
    @Test
    public void testSelector() throws Exception {
        try (ServerSocket acceptor = new ServerSocket(0);Selector selector = Selector.open();SocketChannel client = SocketChannel.open(acceptor.getLocalSocketAddress());Socket server = acceptor.accept()) {
            server.setTcpNoDelay(true);
            // Make the client non blocking and register it with selector for reads
            client.configureBlocking(false);
            SelectionKey key = client.register(selector, SelectionKey.OP_READ);
            // assert it is not selected
            Assertions.assertTrue(key.isValid());
            Assertions.assertFalse(key.isReadable());
            Assertions.assertEquals(0, key.readyOps());
            // try selecting and assert nothing selected
            int selected = selector.selectNow();
            Assertions.assertEquals(0, selected);
            Assertions.assertEquals(0, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertFalse(key.isReadable());
            Assertions.assertEquals(0, key.readyOps());
            // Write a byte from server to client
            server.getOutputStream().write(42);
            server.getOutputStream().flush();
            // select again and assert selection found for read
            selected = selector.select(1000);
            Assertions.assertEquals(1, selected);
            Assertions.assertEquals(1, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // select again and see that it is not reselect, but stays selected
            selected = selector.select(100);
            Assertions.assertEquals(0, selected);
            Assertions.assertEquals(1, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // read the byte
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int len = client.read(buf);
            Assertions.assertEquals(1, len);
            buf.flip();
            Assertions.assertEquals(42, buf.get());
            buf.clear();
            // But this does not change the key
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // Even if we select again ?
            selected = selector.select(100);
            Assertions.assertEquals(0, selected);
            Assertions.assertEquals(1, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // Unless we remove the key from the select set
            // and then it is still flagged as isReadable()
            selector.selectedKeys().clear();
            Assertions.assertEquals(0, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // Now if we select again - it is still flagged as readable!!!
            selected = selector.select(100);
            Assertions.assertEquals(0, selected);
            Assertions.assertEquals(0, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isReadable());
            Assertions.assertEquals(1, key.readyOps());
            // Only when it is selected for something else does that state change.
            key.interestOps(((SelectionKey.OP_READ) | (SelectionKey.OP_WRITE)));
            selected = selector.select(1000);
            Assertions.assertEquals(1, selected);
            Assertions.assertEquals(1, selector.selectedKeys().size());
            Assertions.assertTrue(key.isValid());
            Assertions.assertTrue(key.isWritable());
            Assertions.assertFalse(key.isReadable());
            Assertions.assertEquals(SelectionKey.OP_WRITE, key.readyOps());
        }
    }
}

