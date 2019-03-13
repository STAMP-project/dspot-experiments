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


import OpCode.CLOSE;
import StatusCode.NORMAL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.eclipse.jetty.websocket.common.util.MaskedByteBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static OpCode.CLOSE;


public class ClosePayloadParserTest {
    @Test
    public void testGameOver() {
        String expectedReason = "Game Over";
        byte[] utf = expectedReason.getBytes(StandardCharsets.UTF_8);
        ByteBuffer payload = ByteBuffer.allocate(((utf.length) + 2));
        payload.putChar(((char) (NORMAL)));
        payload.put(utf, 0, utf.length);
        payload.flip();
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.put(((byte) (128 | (CLOSE))));// fin + close

        buf.put(((byte) (128 | (payload.remaining()))));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, payload);
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(CLOSE, 1);
        CloseInfo close = new CloseInfo(capture.getFrames().poll());
        MatcherAssert.assertThat("CloseFrame.statusCode", close.getStatusCode(), Matchers.is(NORMAL));
        MatcherAssert.assertThat("CloseFrame.data", close.getReason(), Matchers.is(expectedReason));
    }
}

