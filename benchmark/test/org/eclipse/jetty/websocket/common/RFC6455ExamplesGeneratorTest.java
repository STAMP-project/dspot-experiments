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


import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.PongFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.junit.jupiter.api.Test;


public class RFC6455ExamplesGeneratorTest {
    private static final int FUDGE = 32;

    @Test
    public void testFragmentedUnmaskedTextMessage() {
        WebSocketFrame text1 = new TextFrame().setPayload("Hel").setFin(false);
        WebSocketFrame text2 = new ContinuationFrame().setPayload("lo");
        ByteBuffer actual1 = UnitGenerator.generate(text1);
        ByteBuffer actual2 = UnitGenerator.generate(text2);
        ByteBuffer expected1 = ByteBuffer.allocate(5);
        expected1.put(new byte[]{ ((byte) (1)), ((byte) (3)), ((byte) (72)), ((byte) (101)), ((byte) (108)) });
        ByteBuffer expected2 = ByteBuffer.allocate(4);
        expected2.put(new byte[]{ ((byte) (128)), ((byte) (2)), ((byte) (108)), ((byte) (111)) });
        expected1.flip();
        expected2.flip();
        ByteBufferAssert.assertEquals("t1 buffers are not equal", expected1, actual1);
        ByteBufferAssert.assertEquals("t2 buffers are not equal", expected2, actual2);
    }

    @Test
    public void testSingleMaskedPongRequest() {
        PongFrame pong = new PongFrame().setPayload("Hello");
        pong.setMask(new byte[]{ 55, ((byte) (250)), 33, 61 });
        ByteBuffer actual = UnitGenerator.generate(pong);
        ByteBuffer expected = ByteBuffer.allocate(11);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // Unmasked Pong request
        expected.put(new byte[]{ ((byte) (138)), ((byte) (133)), 55, ((byte) (250)), 33, 61, 127, ((byte) (159)), 77, 81, 88 });
        expected.flip();// make readable

        ByteBufferAssert.assertEquals("pong buffers are not equal", expected, actual);
    }

    @Test
    public void testSingleMaskedTextMessage() {
        WebSocketFrame text = new TextFrame().setPayload("Hello");
        text.setMask(new byte[]{ 55, ((byte) (250)), 33, 61 });
        ByteBuffer actual = UnitGenerator.generate(text);
        ByteBuffer expected = ByteBuffer.allocate(11);
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // A single-frame masked text message
        expected.put(new byte[]{ ((byte) (129)), ((byte) (133)), 55, ((byte) (250)), 33, 61, 127, ((byte) (159)), 77, 81, 88 });
        expected.flip();// make readable

        ByteBufferAssert.assertEquals("masked text buffers are not equal", expected, actual);
    }

    @Test
    public void testSingleUnmasked256ByteBinaryMessage() {
        int dataSize = 256;
        BinaryFrame binary = new BinaryFrame();
        byte[] payload = new byte[dataSize];
        Arrays.fill(payload, ((byte) (68)));
        binary.setPayload(ByteBuffer.wrap(payload));
        ByteBuffer actual = UnitGenerator.generate(binary);
        ByteBuffer expected = ByteBuffer.allocate((dataSize + (RFC6455ExamplesGeneratorTest.FUDGE)));
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // 256 bytes binary message in a single unmasked frame
        expected.put(new byte[]{ ((byte) (130)), ((byte) (126)) });
        expected.putShort(((short) (256)));
        for (int i = 0; i < dataSize; i++) {
            expected.put(((byte) (68)));
        }
        expected.flip();
        ByteBufferAssert.assertEquals("binary buffers are not equal", expected, actual);
    }

    @Test
    public void testSingleUnmasked64KBinaryMessage() {
        int dataSize = 1024 * 64;
        BinaryFrame binary = new BinaryFrame();
        byte[] payload = new byte[dataSize];
        Arrays.fill(payload, ((byte) (68)));
        binary.setPayload(ByteBuffer.wrap(payload));
        ByteBuffer actual = UnitGenerator.generate(binary);
        ByteBuffer expected = ByteBuffer.allocate((dataSize + 10));
        // Raw bytes as found in RFC 6455, Section 5.7 - Examples
        // 64k bytes binary message in a single unmasked frame
        expected.put(new byte[]{ ((byte) (130)), ((byte) (127)) });
        expected.putInt(0);
        expected.putInt(65536);
        for (int i = 0; i < dataSize; i++) {
            expected.put(((byte) (68)));
        }
        expected.flip();
        ByteBufferAssert.assertEquals("binary buffers are not equal", expected, actual);
    }

    @Test
    public void testSingleUnmaskedPingRequest() throws Exception {
        PingFrame ping = new PingFrame().setPayload("Hello");
        ByteBuffer actual = UnitGenerator.generate(ping);
        ByteBuffer expected = ByteBuffer.allocate(10);
        expected.put(new byte[]{ ((byte) (137)), ((byte) (5)), ((byte) (72)), ((byte) (101)), ((byte) (108)), ((byte) (108)), ((byte) (111)) });
        expected.flip();// make readable

        ByteBufferAssert.assertEquals("Ping buffers", expected, actual);
    }

    @Test
    public void testSingleUnmaskedTextMessage() {
        WebSocketFrame text = new TextFrame().setPayload("Hello");
        ByteBuffer actual = UnitGenerator.generate(text);
        ByteBuffer expected = ByteBuffer.allocate(10);
        expected.put(new byte[]{ ((byte) (129)), ((byte) (5)), ((byte) (72)), ((byte) (101)), ((byte) (108)), ((byte) (108)), ((byte) (111)) });
        expected.flip();
        ByteBufferAssert.assertEquals("t1 buffers are not equal", expected, actual);
    }
}

