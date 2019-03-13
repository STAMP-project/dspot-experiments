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
package org.eclipse.jetty.websocket.common;


import OpCode.PING;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class PingPayloadParserTest {
    @Test
    public void testBasicPingParsing() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        BufferUtil.clearToFill(buf);
        buf.put(new byte[]{ ((byte) (137)), 5, 72, 101, 108, 108, 111 });
        BufferUtil.flipToFlush(buf, 0);
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(PING, 1);
        PingFrame ping = ((PingFrame) (capture.getFrames().poll()));
        String actual = BufferUtil.toUTF8String(ping.getPayload());
        MatcherAssert.assertThat("PingFrame.payload", actual, Matchers.is("Hello"));
    }
}

