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
package org.eclipse.jetty.websocket.common.message;


import java.util.Arrays;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.io.LocalWebSocketSession;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class MessageOutputStreamTest {
    private static final Logger LOG = Log.getLogger(MessageOutputStreamTest.class);

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private WebSocketPolicy policy;

    private TrackingSocket socket;

    private LocalWebSocketSession session;

    @Test
    public void testMultipleWrites() throws Exception {
        try (MessageOutputStream stream = new MessageOutputStream(session)) {
            stream.write("Hello".getBytes("UTF-8"));
            stream.write(" ".getBytes("UTF-8"));
            stream.write("World".getBytes("UTF-8"));
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        MatcherAssert.assertThat("Message", msg, Matchers.allOf(Matchers.containsString("byte[11]"), Matchers.containsString("Hello World")));
    }

    @Test
    public void testSingleWrite() throws Exception {
        try (MessageOutputStream stream = new MessageOutputStream(session)) {
            stream.write("Hello World".getBytes("UTF-8"));
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        MatcherAssert.assertThat("Message", msg, Matchers.allOf(Matchers.containsString("byte[11]"), Matchers.containsString("Hello World")));
    }

    @Test
    public void testWriteMultipleBuffers() throws Exception {
        int bufsize = ((int) ((policy.getMaxBinaryMessageBufferSize()) * 2.5));
        byte[] buf = new byte[bufsize];
        MessageOutputStreamTest.LOG.debug("Buffer sizes: max:{}, test:{}", policy.getMaxBinaryMessageBufferSize(), bufsize);
        Arrays.fill(buf, ((byte) ('x')));
        buf[(bufsize - 1)] = ((byte) ('o'));// mark last entry for debugging

        try (MessageOutputStream stream = new MessageOutputStream(session)) {
            stream.write(buf);
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        MatcherAssert.assertThat("Message", msg, Matchers.allOf(Matchers.containsString((("byte[" + bufsize) + "]")), Matchers.containsString("xxxo>>>")));
    }
}

