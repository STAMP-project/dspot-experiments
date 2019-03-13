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


import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import org.eclipse.jetty.util.BufferUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SocketChannelEndPointOpenCloseTest {
    public static class EndPointPair {
        public SocketChannelEndPoint client;

        public SocketChannelEndPoint server;
    }

    static ServerSocketChannel connector;

    @Test
    public void testClientServerExchange() throws Exception {
        SocketChannelEndPointOpenCloseTest.EndPointPair c = newConnection();
        ByteBuffer buffer = BufferUtil.allocate(4096);
        // Client sends a request
        c.client.flush(BufferUtil.toBuffer("request"));
        // Server receives the request
        int len = c.server.fill(buffer);
        Assertions.assertEquals(7, len);
        Assertions.assertEquals("request", BufferUtil.toString(buffer));
        // Client and server are open
        Assertions.assertTrue(c.client.isOpen());
        Assertions.assertFalse(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertFalse(c.server.isOutputShutdown());
        // Server sends response and closes output
        c.server.flush(BufferUtil.toBuffer("response"));
        c.server.shutdownOutput();
        // client server are open, server is oshut
        Assertions.assertTrue(c.client.isOpen());
        Assertions.assertFalse(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
        // Client reads response
        BufferUtil.clear(buffer);
        len = c.client.fill(buffer);
        Assertions.assertEquals(8, len);
        Assertions.assertEquals("response", BufferUtil.toString(buffer));
        // Client and server are open, server is oshut
        Assertions.assertTrue(c.client.isOpen());
        Assertions.assertFalse(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
        // Client reads -1
        BufferUtil.clear(buffer);
        len = c.client.fill(buffer);
        Assertions.assertEquals((-1), len);
        // Client and server are open, server is oshut, client is ishut
        Assertions.assertTrue(c.client.isOpen());
        Assertions.assertFalse(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
        // Client shutsdown output, which is a close because already ishut
        c.client.shutdownOutput();
        // Client is closed. Server is open and oshut
        Assertions.assertFalse(c.client.isOpen());
        Assertions.assertTrue(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
        // Server reads close
        BufferUtil.clear(buffer);
        len = c.server.fill(buffer);
        Assertions.assertEquals((-1), len);
        // Client and Server are closed
        Assertions.assertFalse(c.client.isOpen());
        Assertions.assertTrue(c.client.isOutputShutdown());
        Assertions.assertFalse(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
    }

    @Test
    public void testClientClose() throws Exception {
        SocketChannelEndPointOpenCloseTest.EndPointPair c = newConnection();
        ByteBuffer buffer = BufferUtil.allocate(4096);
        c.client.flush(BufferUtil.toBuffer("request"));
        int len = c.server.fill(buffer);
        Assertions.assertEquals(7, len);
        Assertions.assertEquals("request", BufferUtil.toString(buffer));
        Assertions.assertTrue(c.client.isOpen());
        Assertions.assertFalse(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertFalse(c.server.isOutputShutdown());
        c.client.close();
        Assertions.assertFalse(c.client.isOpen());
        Assertions.assertTrue(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertFalse(c.server.isOutputShutdown());
        len = c.server.fill(buffer);
        Assertions.assertEquals((-1), len);
        Assertions.assertFalse(c.client.isOpen());
        Assertions.assertTrue(c.client.isOutputShutdown());
        Assertions.assertTrue(c.server.isOpen());
        Assertions.assertFalse(c.server.isOutputShutdown());
        c.server.shutdownOutput();
        Assertions.assertFalse(c.client.isOpen());
        Assertions.assertTrue(c.client.isOutputShutdown());
        Assertions.assertFalse(c.server.isOpen());
        Assertions.assertTrue(c.server.isOutputShutdown());
    }
}

