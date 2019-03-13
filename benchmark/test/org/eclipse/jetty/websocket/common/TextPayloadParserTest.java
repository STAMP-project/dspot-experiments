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


import OpCode.CONTINUATION;
import OpCode.TEXT;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.eclipse.jetty.websocket.api.MessageTooLargeException;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.eclipse.jetty.websocket.common.util.MaskedByteBuffer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class TextPayloadParserTest {
    @Test
    public void testFrameTooLargeDueToPolicy() throws Exception {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        // Artificially small buffer/payload
        policy.setInputBufferSize(1024);// read buffer

        policy.setMaxTextMessageBufferSize(1024);// streaming buffer (not used in this test)

        policy.setMaxTextMessageSize(1024);// actual maximum text message size policy

        byte[] utf = new byte[2048];
        Arrays.fill(utf, ((byte) ('a')));
        MatcherAssert.assertThat("Must be a medium length payload", utf.length, Matchers.allOf(Matchers.greaterThan(126), Matchers.lessThan(65535)));
        ByteBuffer buf = ByteBuffer.allocate(((utf.length) + 8));
        buf.put(((byte) (129)));// text frame, fin = true

        buf.put(((byte) (128 | 126)));// 0x7E == 126 (a 2 byte payload length)

        buf.putShort(((short) (utf.length)));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, utf);
        buf.flip();
        UnitParser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        Assertions.assertThrows(MessageTooLargeException.class, () -> parser.parseQuietly(buf));
    }

    @Test
    public void testLongMaskedText() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 3500; i++) {
            sb.append("Hell\uff4f Big W\uff4frld ");
        }
        sb.append(". The end.");
        String expectedText = sb.toString();
        byte[] utf = expectedText.getBytes(StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Must be a long length payload", utf.length, Matchers.greaterThan(65535));
        ByteBuffer buf = ByteBuffer.allocate(((utf.length) + 32));
        buf.put(((byte) (129)));// text frame, fin = true

        buf.put(((byte) (128 | 127)));// 0x7F == 127 (a 8 byte payload length)

        buf.putLong(utf.length);
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, utf);
        buf.flip();
        WebSocketPolicy policy = WebSocketPolicy.newServerPolicy();
        policy.setMaxTextMessageSize(100000);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.data", txt.getPayloadAsUTF8(), Matchers.is(expectedText));
    }

    @Test
    public void testMediumMaskedText() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 14; i++) {
            sb.append("Hell\uff4f Medium W\uff4frld ");
        }
        sb.append(". The end.");
        String expectedText = sb.toString();
        byte[] utf = expectedText.getBytes(StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Must be a medium length payload", utf.length, Matchers.allOf(Matchers.greaterThan(126), Matchers.lessThan(65535)));
        ByteBuffer buf = ByteBuffer.allocate(((utf.length) + 10));
        buf.put(((byte) (129)));
        buf.put(((byte) (128 | 126)));// 0x7E == 126 (a 2 byte payload length)

        buf.putShort(((short) (utf.length)));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, utf);
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.data", txt.getPayloadAsUTF8(), Matchers.is(expectedText));
    }

    @Test
    public void testShortMaskedFragmentedText() throws Exception {
        String part1 = "Hello ";
        String part2 = "World";
        byte[] b1 = part1.getBytes(StandardCharsets.UTF_8);
        byte[] b2 = part2.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(32);
        // part 1
        buf.put(((byte) (1)));// no fin + text

        buf.put(((byte) (128 | (b1.length))));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, b1);
        // part 2
        buf.put(((byte) (128)));// fin + continuation

        buf.put(((byte) (128 | (b2.length))));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, b2);
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        capture.assertHasFrame(CONTINUATION, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame[0].data", txt.getPayloadAsUTF8(), Matchers.is(part1));
        txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame[1].data", txt.getPayloadAsUTF8(), Matchers.is(part2));
    }

    @Test
    public void testShortMaskedText() throws Exception {
        String expectedText = "Hello World";
        byte[] utf = expectedText.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.put(((byte) (129)));
        buf.put(((byte) (128 | (utf.length))));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, utf);
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.data", txt.getPayloadAsUTF8(), Matchers.is(expectedText));
    }

    @Test
    public void testShortMaskedUtf8Text() throws Exception {
        String expectedText = "Hell\uff4f W\uff4frld";
        byte[] utf = expectedText.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(24);
        buf.put(((byte) (129)));
        buf.put(((byte) (128 | (utf.length))));
        MaskedByteBuffer.putMask(buf);
        MaskedByteBuffer.putPayload(buf, utf);
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.data", txt.getPayloadAsUTF8(), Matchers.is(expectedText));
    }
}

