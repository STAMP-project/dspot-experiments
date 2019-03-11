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


import OpCode.BINARY;
import OpCode.CONTINUATION;
import OpCode.PING;
import OpCode.PONG;
import OpCode.TEXT;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Collection of Example packets as found in <a href="https://tools.ietf.org/html/rfc6455#section-5.7">RFC 6455 Examples section</a>
 */
public class RFC6455ExamplesParserTest {
    @Test
    public void testFragmentedUnmaskedTextMessage() {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        ByteBuffer buf = ByteBuffer.allocate(16);
        BufferUtil.clearToFill(buf);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // A fragmented unmasked text message (part 1 of 2 "Hel")
        buf.put(new byte[]{ ((byte) (1)), ((byte) (3)), 72, ((byte) (101)), 108 });
        // Parse #1
        BufferUtil.flipToFlush(buf, 0);
        parser.parse(buf);
        // part 2 of 2 "lo" (A continuation frame of the prior text message)
        BufferUtil.flipToFill(buf);
        buf.put(new byte[]{ ((byte) (128)), 2, 108, 111 });
        // Parse #2
        BufferUtil.flipToFlush(buf, 0);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        capture.assertHasFrame(CONTINUATION, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        String actual = BufferUtil.toUTF8String(txt.getPayload());
        MatcherAssert.assertThat("TextFrame[0].data", actual, Matchers.is("Hel"));
        txt = capture.getFrames().poll();
        actual = BufferUtil.toUTF8String(txt.getPayload());
        MatcherAssert.assertThat("TextFrame[1].data", actual, Matchers.is("lo"));
    }

    @Test
    public void testSingleMaskedPongRequest() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // Unmasked Pong request
        buf.put(new byte[]{ ((byte) (138)), ((byte) (133)), 55, ((byte) (250)), 33, 61, 127, ((byte) (159)), 77, 81, 88 });
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(PONG, 1);
        WebSocketFrame pong = capture.getFrames().poll();
        String actual = BufferUtil.toUTF8String(pong.getPayload());
        MatcherAssert.assertThat("PongFrame.payload", actual, Matchers.is("Hello"));
    }

    @Test
    public void testSingleMaskedTextMessage() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // A single-frame masked text message
        buf.put(new byte[]{ ((byte) (129)), ((byte) (133)), 55, ((byte) (250)), 33, 61, 127, ((byte) (159)), 77, 81, 88 });
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        String actual = BufferUtil.toUTF8String(txt.getPayload());
        MatcherAssert.assertThat("TextFrame.payload", actual, Matchers.is("Hello"));
    }

    @Test
    public void testSingleUnmasked256ByteBinaryMessage() {
        int dataSize = 256;
        ByteBuffer buf = ByteBuffer.allocate((dataSize + 10));
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // 256 bytes binary message in a single unmasked frame
        buf.put(new byte[]{ ((byte) (130)), 126 });
        buf.putShort(((short) (256)));// 16 bit size

        for (int i = 0; i < dataSize; i++) {
            buf.put(((byte) (68)));
        }
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(BINARY, 1);
        Frame bin = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", bin.getPayloadLength(), Matchers.is(dataSize));
        ByteBuffer data = bin.getPayload();
        MatcherAssert.assertThat("BinaryFrame.payload.length", data.remaining(), Matchers.is(dataSize));
        for (int i = 0; i < dataSize; i++) {
            MatcherAssert.assertThat((("BinaryFrame.payload[" + i) + "]"), data.get(i), Matchers.is(((byte) (68))));
        }
    }

    @Test
    public void testSingleUnmasked64KByteBinaryMessage() {
        int dataSize = 1024 * 64;
        ByteBuffer buf = ByteBuffer.allocate((dataSize + 10));
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // 64 Kbytes binary message in a single unmasked frame
        buf.put(new byte[]{ ((byte) (130)), 127 });
        buf.putLong(dataSize);// 64bit size

        for (int i = 0; i < dataSize; i++) {
            buf.put(((byte) (119)));
        }
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(BINARY, 1);
        Frame bin = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", bin.getPayloadLength(), Matchers.is(dataSize));
        ByteBuffer data = bin.getPayload();
        MatcherAssert.assertThat("BinaryFrame.payload.length", data.remaining(), Matchers.is(dataSize));
        for (int i = 0; i < dataSize; i++) {
            MatcherAssert.assertThat((("BinaryFrame.payload[" + i) + "]"), data.get(i), Matchers.is(((byte) (119))));
        }
    }

    @Test
    public void testSingleUnmaskedPingRequest() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // Unmasked Ping request
        buf.put(new byte[]{ ((byte) (137)), 5, 72, 101, 108, 108, 111 });
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(PING, 1);
        WebSocketFrame ping = capture.getFrames().poll();
        String actual = BufferUtil.toUTF8String(ping.getPayload());
        MatcherAssert.assertThat("PingFrame.payload", actual, Matchers.is("Hello"));
    }

    @Test
    public void testSingleUnmaskedTextMessage() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // A single-frame unmasked text message
        buf.put(new byte[]{ ((byte) (129)), 5, 72, 101, 108, 108, 111 });
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        capture.assertHasFrame(TEXT, 1);
        WebSocketFrame txt = capture.getFrames().poll();
        String actual = BufferUtil.toUTF8String(txt.getPayload());
        MatcherAssert.assertThat("TextFrame.payload", actual, Matchers.is("Hello"));
    }
}

