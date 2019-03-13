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
package org.eclipse.jetty.websocket.common.extensions;


import BatchMode.OFF;
import OpCode.TEXT;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.extensions.Extension;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.extensions.identity.IdentityExtension;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.OutgoingFramesCapture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class IdentityExtensionTest {
    /**
     * Verify that incoming frames are unmodified
     */
    @Test
    public void testIncomingFrames() {
        IncomingFramesCapture capture = new IncomingFramesCapture();
        Extension ext = new IdentityExtension();
        ext.setNextIncomingFrames(capture);
        Frame frame = new TextFrame().setPayload("hello");
        ext.incomingFrame(frame);
        capture.assertFrameCount(1);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame actual = capture.getFrames().poll();
        MatcherAssert.assertThat("Frame.opcode", actual.getOpCode(), Matchers.is(TEXT));
        MatcherAssert.assertThat("Frame.fin", actual.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", actual.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", actual.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", actual.isRsv3(), Matchers.is(false));
        ByteBuffer expected = BufferUtil.toBuffer("hello", StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Frame.payloadLength", actual.getPayloadLength(), Matchers.is(expected.remaining()));
        ByteBufferAssert.assertEquals("Frame.payload", expected, actual.getPayload().slice());
    }

    /**
     * Verify that outgoing frames are unmodified
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testOutgoingFrames() throws IOException {
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        Extension ext = new IdentityExtension();
        ext.setNextOutgoingFrames(capture);
        Frame frame = new TextFrame().setPayload("hello");
        ext.outgoingFrame(frame, null, OFF);
        capture.assertFrameCount(1);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame actual = capture.getFrames().getFirst();
        MatcherAssert.assertThat("Frame.opcode", actual.getOpCode(), Matchers.is(TEXT));
        MatcherAssert.assertThat("Frame.fin", actual.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", actual.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", actual.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", actual.isRsv3(), Matchers.is(false));
        ByteBuffer expected = BufferUtil.toBuffer("hello", StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Frame.payloadLength", actual.getPayloadLength(), Matchers.is(expected.remaining()));
        ByteBufferAssert.assertEquals("Frame.payload", expected, actual.getPayload().slice());
    }
}

