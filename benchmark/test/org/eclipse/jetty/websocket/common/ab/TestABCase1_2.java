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


import OpCode.BINARY;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Binary Message Spec testing the {@link Generator} and {@link Parser}
 */
public class TestABCase1_2 {
    private WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();

    @Test
    public void testGenerate125ByteBinaryCase1_2_2() {
        int length = 125;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 127;
        expected.put(b);
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate126ByteBinaryCase1_2_3() {
        int length = 126;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        // expected.put((byte)((length>>8) & 0xFF));
        // expected.put((byte)(length & 0xFF));
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate127ByteBinaryCase1_2_4() {
        int length = 127;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        // expected.put((byte)((length>>8) & 0xFF));
        // expected.put((byte)(length & 0xFF));
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate128ByteBinaryCase1_2_5() {
        int length = 128;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(((byte) (length >> 8)));
        expected.put(((byte) (length & 255)));
        // expected.putShort((short)length);
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate65535ByteBinaryCase1_2_6() {
        int length = 65535;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(new byte[]{ ((byte) (255)), ((byte) (255)) });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate65536ByteBinaryCase1_2_7() {
        int length = 65536;
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (int i = 0; i < length; ++i) {
            bb.put("*".getBytes());
        }
        bb.flip();
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(bb);
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 11));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 127;
        expected.put(b);
        expected.put(new byte[]{ 0, 0, 0, 0, 0, 1, 0, 0 });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerateEmptyBinaryCase1_2_1() {
        WebSocketFrame binaryFrame = new BinaryFrame().setPayload(new byte[]{  });
        ByteBuffer actual = UnitGenerator.generate(binaryFrame);
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (130)), ((byte) (0)) });
        BufferUtil.flipToFlush(expected, 0);
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testParse125ByteBinaryCase1_2_2() {
        int length = 125;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 127;
        expected.put(b);
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParse126ByteBinaryCase1_2_3() {
        int length = 126;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParse127ByteBinaryCase1_2_4() {
        int length = 127;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // .assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParse128ByteBinaryCase1_2_5() {
        int length = 128;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParse65535ByteBinaryCase1_2_6() {
        int length = 65535;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(new byte[]{ ((byte) (255)), ((byte) (255)) });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        policy.setMaxBinaryMessageSize(length);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParse65536ByteBinaryCase1_2_7() {
        int length = 65536;
        ByteBuffer expected = ByteBuffer.allocate((length + 11));
        expected.put(new byte[]{ ((byte) (130)) });
        byte b = 0;// no masking

        b |= 127;
        expected.put(b);
        expected.put(new byte[]{ 0, 0, 0, 0, 0, 1, 0, 0 });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        policy.setMaxBinaryMessageSize(length);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "BinaryFrame.payload");
    }

    @Test
    public void testParseEmptyBinaryCase1_2_1() {
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (130)), ((byte) (0)) });
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(BINARY, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("BinaryFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(0));
        // assertNull(pActual.getPayloadData(), "BinaryFrame.payload");
    }
}

