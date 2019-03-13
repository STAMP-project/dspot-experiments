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


import OpCode.TEXT;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GeneratorParserRoundtripTest {
    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testParserAndGenerator() throws Exception {
        WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();
        Generator gen = new Generator(policy, bufferPool);
        Parser parser = new Parser(policy, bufferPool);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        String message = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        ByteBuffer out = bufferPool.acquire(8192, false);
        try {
            // Generate Buffer
            BufferUtil.flipToFill(out);
            WebSocketFrame frame = new TextFrame().setPayload(message);
            ByteBuffer header = gen.generateHeaderBytes(frame);
            ByteBuffer payload = frame.getPayload();
            out.put(header);
            out.put(payload);
            // Parse Buffer
            BufferUtil.flipToFlush(out, 0);
            parser.parse(out);
        } finally {
            bufferPool.release(out);
        }
        // Validate
        capture.assertHasFrame(TEXT, 1);
        TextFrame txt = ((TextFrame) (capture.getFrames().poll()));
        MatcherAssert.assertThat("Text parsed", txt.getPayloadAsUTF8(), Matchers.is(message));
    }

    @Test
    public void testParserAndGeneratorMasked() throws Exception {
        Generator gen = new Generator(WebSocketPolicy.newClientPolicy(), bufferPool);
        Parser parser = new Parser(WebSocketPolicy.newServerPolicy(), bufferPool);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        String message = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        ByteBuffer out = bufferPool.acquire(8192, false);
        BufferUtil.flipToFill(out);
        try {
            // Setup Frame
            WebSocketFrame frame = new TextFrame().setPayload(message);
            // Add masking
            byte[] mask = new byte[4];
            Arrays.fill(mask, ((byte) (255)));
            frame.setMask(mask);
            // Generate Buffer
            ByteBuffer header = gen.generateHeaderBytes(frame);
            ByteBuffer payload = frame.getPayload();
            out.put(header);
            out.put(payload);
            // Parse Buffer
            BufferUtil.flipToFlush(out, 0);
            parser.parse(out);
        } finally {
            bufferPool.release(out);
        }
        // Validate
        capture.assertHasFrame(TEXT, 1);
        TextFrame txt = ((TextFrame) (capture.getFrames().poll()));
        Assertions.assertTrue(txt.isMasked(), "Text.isMasked");
        MatcherAssert.assertThat("Text parsed", txt.getPayloadAsUTF8(), Matchers.is(message));
    }
}

