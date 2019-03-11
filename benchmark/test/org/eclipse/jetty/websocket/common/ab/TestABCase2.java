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
package org.eclipse.jetty.websocket.common.ab;


import OpCode.PING;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.api.ProtocolException;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.Generator;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class TestABCase2 {
    private WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);

    @Test
    public void testGenerate125OctetPingCase2_4() {
        byte[] bytes = new byte[125];
        for (int i = 0; i < (bytes.length); ++i) {
            bytes[i] = Integer.valueOf(Integer.toOctalString(i)).byteValue();
        }
        WebSocketFrame pingFrame = new PingFrame().setPayload(bytes);
        ByteBuffer actual = UnitGenerator.generate(pingFrame);
        ByteBuffer expected = ByteBuffer.allocate(((bytes.length) + 32));
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (bytes.length) & 127;
        expected.put(b);
        expected.put(bytes);
        expected.flip();
        ByteBufferAssert.assertEquals("buffers do not match", expected, actual);
    }

    @Test
    public void testGenerateBinaryPingCase2_3() {
        byte[] bytes = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        PingFrame pingFrame = new PingFrame().setPayload(bytes);
        ByteBuffer actual = UnitGenerator.generate(pingFrame);
        ByteBuffer expected = ByteBuffer.allocate(32);
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (bytes.length) & 127;
        expected.put(b);
        expected.put(bytes);
        expected.flip();
        ByteBufferAssert.assertEquals("buffers do not match", expected, actual);
    }

    @Test
    public void testGenerateEmptyPingCase2_1() {
        WebSocketFrame pingFrame = new PingFrame();
        ByteBuffer actual = UnitGenerator.generate(pingFrame);
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (137)), ((byte) (0)) });
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerateHelloPingCase2_2() {
        String message = "Hello, world!";
        byte[] messageBytes = StringUtil.getUtf8Bytes(message);
        PingFrame pingFrame = new PingFrame().setPayload(messageBytes);
        ByteBuffer actual = UnitGenerator.generate(pingFrame);
        ByteBuffer expected = ByteBuffer.allocate(32);
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (messageBytes.length) & 127;
        expected.put(b);
        expected.put(messageBytes);
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerateOversizedBinaryPingCase2_5_A() {
        byte[] bytes = new byte[126];
        Arrays.fill(bytes, ((byte) (0)));
        PingFrame pingFrame = new PingFrame();
        Assertions.assertThrows(WebSocketException.class, () -> pingFrame.setPayload(ByteBuffer.wrap(bytes)));
    }

    @Test
    public void testGenerateOversizedBinaryPingCase2_5_B() {
        byte[] bytes = new byte[126];
        Arrays.fill(bytes, ((byte) (0)));
        PingFrame pingFrame = new PingFrame();
        Assertions.assertThrows(WebSocketException.class, () -> pingFrame.setPayload(ByteBuffer.wrap(bytes)));
    }

    @Test
    public void testParse125OctetPingCase2_4() {
        byte[] bytes = new byte[125];
        for (int i = 0; i < (bytes.length); ++i) {
            bytes[i] = Integer.valueOf(Integer.toOctalString(i)).byteValue();
        }
        ByteBuffer expected = ByteBuffer.allocate(((bytes.length) + 32));
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (bytes.length) & 127;
        expected.put(b);
        expected.put(bytes);
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(PING, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("PingFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(bytes.length));
        Assertions.assertEquals(bytes.length, pActual.getPayloadLength(), "PingFrame.payload");
    }

    @Test
    public void testParseBinaryPingCase2_3() {
        byte[] bytes = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        ByteBuffer expected = ByteBuffer.allocate(32);
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (bytes.length) & 127;
        expected.put(b);
        expected.put(bytes);
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(PING, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("PingFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(bytes.length));
        Assertions.assertEquals(bytes.length, pActual.getPayloadLength(), "PingFrame.payload");
    }

    @Test
    public void testParseEmptyPingCase2_1() {
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (137)), ((byte) (0)) });
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(PING, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("PingFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(0));
        Assertions.assertEquals(0, pActual.getPayloadLength(), "PingFrame.payload");
    }

    @Test
    public void testParseHelloPingCase2_2() {
        String message = "Hello, world!";
        byte[] messageBytes = message.getBytes();
        ByteBuffer expected = ByteBuffer.allocate(32);
        expected.put(new byte[]{ ((byte) (137)) });
        byte b = 0;// no masking

        b |= (messageBytes.length) & 127;
        expected.put(b);
        expected.put(messageBytes);
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(PING, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("PingFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(message.length()));
        Assertions.assertEquals(message.length(), pActual.getPayloadLength(), "PingFrame.payload");
    }

    @Test
    public void testParseOversizedBinaryPingCase2_5() {
        byte[] bytes = new byte[126];
        Arrays.fill(bytes, ((byte) (0)));
        ByteBuffer expected = ByteBuffer.allocate(((bytes.length) + (Generator.MAX_HEADER_LENGTH)));
        byte b;
        // fin + op
        b = 0;
        b |= 128;// fin on

        b |= 9;// ping

        expected.put(b);
        // mask + len
        b = 0;
        b |= 0;// no masking

        b |= 126;// 2 byte len

        expected.put(b);
        // 2 byte len
        expected.putChar(((char) (bytes.length)));
        // payload
        expected.put(bytes);
        expected.flip();
        UnitParser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        ProtocolException x = Assertions.assertThrows(ProtocolException.class, () -> parser.parseQuietly(expected));
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Invalid control frame payload length"));
    }
}

