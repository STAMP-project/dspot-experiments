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


public class MessageWriterTest {
    private static final Logger LOG = Log.getLogger(MessageWriterTest.class);

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private WebSocketPolicy policy;

    private TrackingSocket socket;

    private LocalWebSocketSession session;

    @Test
    public void testMultipleWrites() throws Exception {
        try (MessageWriter stream = new MessageWriter(session)) {
            stream.write("Hello");
            stream.write(" ");
            stream.write("World");
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        MatcherAssert.assertThat("Message", msg, Matchers.is("Hello World"));
    }

    @Test
    public void testSingleWrite() throws Exception {
        try (MessageWriter stream = new MessageWriter(session)) {
            stream.append("Hello World");
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        MatcherAssert.assertThat("Message", msg, Matchers.is("Hello World"));
    }

    @Test
    public void testWriteMultipleBuffers() throws Exception {
        int bufsize = ((int) ((policy.getMaxTextMessageBufferSize()) * 2.5));
        char[] buf = new char[bufsize];
        if (MessageWriterTest.LOG.isDebugEnabled())
            MessageWriterTest.LOG.debug("Buffer size: {}", bufsize);

        Arrays.fill(buf, 'x');
        buf[(bufsize - 1)] = 'o';// mark last entry for debugging

        try (MessageWriter stream = new MessageWriter(session)) {
            stream.write(buf);
        }
        MatcherAssert.assertThat("Socket.messageQueue.size", socket.messageQueue.size(), Matchers.is(1));
        String msg = socket.messageQueue.poll();
        String expected = new String(buf);
        MatcherAssert.assertThat("Message", msg, Matchers.is(expected));
    }
}

