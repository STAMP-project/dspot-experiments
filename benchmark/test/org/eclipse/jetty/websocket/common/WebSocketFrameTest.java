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
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.common.frames.CloseFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.junit.jupiter.api.Test;


public class WebSocketFrameTest {
    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private Generator strictGenerator;

    private Generator laxGenerator;

    @Test
    public void testLaxInvalidClose() {
        WebSocketFrame frame = new CloseFrame().setFin(false);
        ByteBuffer actual = generateWholeFrame(laxGenerator, frame);
        String expected = "0800";
        assertFrameHex("Lax Invalid Close Frame", expected, actual);
    }

    @Test
    public void testLaxInvalidPing() {
        WebSocketFrame frame = new PingFrame().setFin(false);
        ByteBuffer actual = generateWholeFrame(laxGenerator, frame);
        String expected = "0900";
        assertFrameHex("Lax Invalid Ping Frame", expected, actual);
    }

    @Test
    public void testStrictValidClose() {
        CloseInfo close = new CloseInfo(StatusCode.NORMAL);
        ByteBuffer actual = generateWholeFrame(strictGenerator, close.asFrame());
        String expected = "880203E8";
        assertFrameHex("Strict Valid Close Frame", expected, actual);
    }

    @Test
    public void testStrictValidPing() {
        WebSocketFrame frame = new PingFrame();
        ByteBuffer actual = generateWholeFrame(strictGenerator, frame);
        String expected = "8900";
        assertFrameHex("Strict Valid Ping Frame", expected, actual);
    }

    @Test
    public void testRsv1() {
        TextFrame frame = new TextFrame();
        frame.setPayload("Hi");
        frame.setRsv1(true);
        laxGenerator.setRsv1InUse(true);
        ByteBuffer actual = generateWholeFrame(laxGenerator, frame);
        String expected = "C1024869";
        assertFrameHex("Lax Text Frame with RSV1", expected, actual);
    }

    @Test
    public void testRsv2() {
        TextFrame frame = new TextFrame();
        frame.setPayload("Hi");
        frame.setRsv2(true);
        laxGenerator.setRsv2InUse(true);
        ByteBuffer actual = generateWholeFrame(laxGenerator, frame);
        String expected = "A1024869";
        assertFrameHex("Lax Text Frame with RSV2", expected, actual);
    }

    @Test
    public void testRsv3() {
        TextFrame frame = new TextFrame();
        frame.setPayload("Hi");
        frame.setRsv3(true);
        laxGenerator.setRsv3InUse(true);
        ByteBuffer actual = generateWholeFrame(laxGenerator, frame);
        String expected = "91024869";
        assertFrameHex("Lax Text Frame with RSV3", expected, actual);
    }
}

