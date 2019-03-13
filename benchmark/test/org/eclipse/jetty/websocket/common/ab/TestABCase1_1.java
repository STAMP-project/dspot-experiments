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


import OpCode.TEXT;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Text Message Spec testing the {@link Generator} and {@link Parser}
 */
public class TestABCase1_1 {
    private WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();

    @Test
    public void testGenerate125ByteTextCase1_1_2() {
        int length = 125;
        byte[] buf = new byte[length];
        Arrays.fill(buf, ((byte) ('*')));
        String text = new String(buf, StandardCharsets.UTF_8);
        Frame textFrame = new TextFrame().setPayload(text);
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= length & 127;
        expected.put(b);
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate126ByteTextCase1_1_3() {
        int length = 126;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        WebSocketFrame textFrame = new TextFrame().setPayload(builder.toString());
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        // expected.put((byte)((length>>8) & 0xFF));
        // expected.put((byte)(length & 0xFF));
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate127ByteTextCase1_1_4() {
        int length = 127;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        WebSocketFrame textFrame = new TextFrame().setPayload(builder.toString());
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= length & 126;
        expected.put(b);
        // expected.put((byte)((length>>8) & 0xFF));
        // expected.put((byte)(length & 0xFF));
        expected.putShort(((short) (length)));
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate128ByteTextCase1_1_5() {
        int length = 128;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        WebSocketFrame textFrame = new TextFrame().setPayload(builder.toString());
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(((byte) (length >> 8)));
        expected.put(((byte) (length & 255)));
        // expected.putShort((short)length);
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate65535ByteTextCase1_1_6() {
        int length = 65535;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        WebSocketFrame textFrame = new TextFrame().setPayload(builder.toString());
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(new byte[]{ ((byte) (255)), ((byte) (255)) });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerate65536ByteTextCase1_1_7() {
        int length = 65536;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append("*");
        }
        WebSocketFrame textFrame = new TextFrame().setPayload(builder.toString());
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate((length + 11));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= 127;
        expected.put(b);
        expected.put(new byte[]{ 0, 0, 0, 0, 0, 1, 0, 0 });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testGenerateEmptyTextCase1_1_1() {
        WebSocketFrame textFrame = new TextFrame().setPayload("");
        ByteBuffer actual = UnitGenerator.generate(textFrame);
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (129)), ((byte) (0)) });
        expected.flip();
        ByteBufferAssert.assertEquals(expected, actual, "buffers do not match");
    }

    @Test
    public void testParse125ByteTextCase1_1_2() {
        int length = 125;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
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
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParse126ByteTextCase1_1_3() {
        int length = 126;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
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
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParse127ByteTextCase1_1_4() {
        int length = 127;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
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
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParse128ByteTextCase1_1_5() {
        int length = 128;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
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
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // .assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParse65535ByteTextCase1_1_6() {
        int length = 65535;
        ByteBuffer expected = ByteBuffer.allocate((length + 5));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= 126;
        expected.put(b);
        expected.put(new byte[]{ ((byte) (255)), ((byte) (255)) });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        policy.setMaxTextMessageSize(length);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParse65536ByteTextCase1_1_7() {
        int length = 65536;
        ByteBuffer expected = ByteBuffer.allocate((length + 11));
        expected.put(new byte[]{ ((byte) (129)) });
        byte b = 0;// no masking

        b |= 127;
        expected.put(b);
        expected.put(new byte[]{ 0, 0, 0, 0, 0, 1, 0, 0 });
        for (int i = 0; i < length; ++i) {
            expected.put("*".getBytes());
        }
        expected.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.CLIENT);
        policy.setMaxTextMessageSize(length);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(length));
        // assertEquals(length, pActual.getPayloadData().length, "TextFrame.payload");
    }

    @Test
    public void testParseEmptyTextCase1_1_1() {
        ByteBuffer expected = ByteBuffer.allocate(5);
        expected.put(new byte[]{ ((byte) (129)), ((byte) (0)) });
        expected.flip();
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(expected);
        capture.assertHasFrame(TEXT, 1);
        Frame pActual = capture.getFrames().poll();
        MatcherAssert.assertThat("TextFrame.payloadLength", pActual.getPayloadLength(), Matchers.is(0));
    }
}

