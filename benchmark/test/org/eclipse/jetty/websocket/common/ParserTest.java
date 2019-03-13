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
import OpCode.CONTINUATION;
import OpCode.PING;
import OpCode.PONG;
import OpCode.TEXT;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.api.ProtocolException;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.DataFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.PongFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.eclipse.jetty.websocket.common.test.UnitParser;
import org.eclipse.jetty.websocket.common.util.Hex;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ParserTest {
    /**
     * Similar to the server side 5.15 testcase. A normal 2 fragment text text message, followed by another continuation.
     */
    @Test
    public void testParseCase5_15() {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload("fragment1").setFin(false));
        send.add(new ContinuationFrame().setPayload("fragment2").setFin(true));
        send.add(new ContinuationFrame().setPayload("fragment3").setFin(false));// bad frame

        send.add(new TextFrame().setPayload("fragment4").setFin(true));
        send.add(asFrame());
        ByteBuffer completeBuf = UnitGenerator.generate(send);
        UnitParser parser = new UnitParser();
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        ProtocolException x = Assertions.assertThrows(ProtocolException.class, () -> parser.parseQuietly(completeBuf));
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("CONTINUATION frame without prior !FIN"));
    }

    /**
     * Similar to the server side 5.18 testcase. Text message fragmented as 2 frames, both as opcode=TEXT
     */
    @Test
    public void testParseCase5_18() {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload("fragment1").setFin(false));
        send.add(new TextFrame().setPayload("fragment2").setFin(true));// bad frame, must be continuation

        send.add(asFrame());
        ByteBuffer completeBuf = UnitGenerator.generate(send);
        UnitParser parser = new UnitParser();
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        ProtocolException x = Assertions.assertThrows(ProtocolException.class, () -> parser.parseQuietly(completeBuf));
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Unexpected TEXT frame"));
    }

    /**
     * Similar to the server side 5.19 testcase. text message, send in 5 frames/fragments, with 2 pings in the mix.
     */
    @Test
    public void testParseCase5_19() {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload("f1").setFin(false));
        send.add(new ContinuationFrame().setPayload(",f2").setFin(false));
        send.add(new PingFrame().setPayload("pong-1"));
        send.add(new ContinuationFrame().setPayload(",f3").setFin(false));
        send.add(new ContinuationFrame().setPayload(",f4").setFin(false));
        send.add(new PingFrame().setPayload("pong-2"));
        send.add(new ContinuationFrame().setPayload(",f5").setFin(true));
        send.add(asFrame());
        ByteBuffer completeBuf = UnitGenerator.generate(send);
        UnitParser parser = new UnitParser();
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        parser.parseQuietly(completeBuf);
        capture.assertHasFrame(TEXT, 1);
        capture.assertHasFrame(CONTINUATION, 4);
        capture.assertHasFrame(CLOSE, 1);
        capture.assertHasFrame(PING, 2);
    }

    /**
     * Similar to the server side 5.6 testcase. pong, then text, then close frames.
     */
    @Test
    public void testParseCase5_6() {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PongFrame().setPayload("ping"));
        send.add(new TextFrame().setPayload("hello, world"));
        send.add(asFrame());
        ByteBuffer completeBuf = UnitGenerator.generate(send);
        UnitParser parser = new UnitParser();
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        parse(completeBuf);
        capture.assertHasFrame(TEXT, 1);
        capture.assertHasFrame(CLOSE, 1);
        capture.assertHasFrame(PONG, 1);
    }

    /**
     * Similar to the server side 6.2.3 testcase. Lots of small 1 byte UTF8 Text frames, representing 1 overall text message.
     */
    @Test
    public void testParseCase6_2_3() {
        String utf8 = "Hello-\uc2b5@\uc39f\uc3a4\uc3bc\uc3a0\uc3a1-UTF-8!!";
        byte[] msg = StringUtil.getUtf8Bytes(utf8);
        List<WebSocketFrame> send = new ArrayList<>();
        int textCount = 0;
        int continuationCount = 0;
        int len = msg.length;
        boolean continuation = false;
        byte[] mini;
        for (int i = 0; i < len; i++) {
            DataFrame frame = null;
            if (continuation) {
                frame = new ContinuationFrame();
                continuationCount++;
            } else {
                frame = new TextFrame();
                textCount++;
            }
            mini = new byte[1];
            mini[0] = msg[i];
            frame.setPayload(ByteBuffer.wrap(mini));
            boolean isLast = i >= (len - 1);
            frame.setFin(isLast);
            send.add(frame);
            continuation = true;
        }
        send.add(asFrame());
        ByteBuffer completeBuf = UnitGenerator.generate(send);
        UnitParser parser = new UnitParser();
        IncomingFramesCapture capture = new IncomingFramesCapture();
        setIncomingFramesHandler(capture);
        parse(completeBuf);
        capture.assertHasFrame(TEXT, textCount);
        capture.assertHasFrame(CONTINUATION, continuationCount);
        capture.assertHasFrame(CLOSE, 1);
    }

    @Test
    public void testParseNothing() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        // Put nothing in the buffer.
        buf.flip();
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        parser.parse(buf);
        MatcherAssert.assertThat("Frame Count", capture.getFrames().size(), Matchers.is(0));
    }

    @Test
    public void testWindowedParseLargeFrame() {
        // Create frames
        byte[] payload = new byte[65536];
        Arrays.fill(payload, ((byte) ('*')));
        List<WebSocketFrame> frames = new ArrayList<>();
        TextFrame text = new TextFrame();
        text.setPayload(ByteBuffer.wrap(payload));
        text.setMask(Hex.asByteArray("11223344"));
        frames.add(text);
        frames.add(asFrame());
        // Build up raw (network bytes) buffer
        ByteBuffer networkBytes = UnitGenerator.generate(frames);
        // Parse, in 4096 sized windows
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        Parser parser = new UnitParser(policy);
        IncomingFramesCapture capture = new IncomingFramesCapture();
        parser.setIncomingFramesHandler(capture);
        while ((networkBytes.remaining()) > 0) {
            ByteBuffer window = networkBytes.slice();
            int windowSize = Math.min(window.remaining(), 4096);
            window.limit(windowSize);
            parser.parse(window);
            networkBytes.position(((networkBytes.position()) + windowSize));
        } 
        MatcherAssert.assertThat("Frame Count", capture.getFrames().size(), Matchers.is(2));
        WebSocketFrame frame = capture.getFrames().poll();
        MatcherAssert.assertThat("Frame[0].opcode", frame.getOpCode(), Matchers.is(TEXT));
        ByteBuffer actualPayload = frame.getPayload();
        MatcherAssert.assertThat("Frame[0].payload.length", actualPayload.remaining(), Matchers.is(payload.length));
        // Should be all '*' characters (if masking is correct)
        for (int i = actualPayload.position(); i < (actualPayload.remaining()); i++) {
            MatcherAssert.assertThat("Frame[0].payload[i]", actualPayload.get(i), Matchers.is(((byte) ('*'))));
        }
    }
}

