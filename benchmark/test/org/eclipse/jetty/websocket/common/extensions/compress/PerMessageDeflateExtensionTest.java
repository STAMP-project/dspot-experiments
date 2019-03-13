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
package org.eclipse.jetty.websocket.common.extensions.compress;


import BatchMode.OFF;
import OpCode.CONTINUATION;
import OpCode.PING;
import OpCode.TEXT;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.toolchain.test.ByteBufferAssert;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.api.ProtocolException;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.extensions.AbstractExtensionTest;
import org.eclipse.jetty.websocket.common.extensions.ExtensionTool;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.OutgoingFramesCapture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Client side behavioral tests for permessage-deflate extension.
 * <p>
 * See: http://tools.ietf.org/html/draft-ietf-hybi-permessage-compression-15
 */
public class PerMessageDeflateExtensionTest extends AbstractExtensionTest {
    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testEndsWithTailBytes() {
        assertEndsWithTail("11223344", false);
        assertEndsWithTail("00", false);
        assertEndsWithTail("0000", false);
        assertEndsWithTail("FFFF0000", false);
        assertEndsWithTail("880000FFFF", true);
        assertEndsWithTail("0000FFFF", true);
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.1: A message compressed using 1 compressed DEFLATE block
     */
    @Test
    public void testDraft21_Hello_UnCompressedBlock() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // basic, 1 block, compressed with 0 compression level (aka, uncompressed).
        // (HEADER added for this test)
        // example frame from RFC
        tester.parseIncomingHex("0xc1 0x07", "0xf2 0x48 0xcd 0xc9 0xc9 0x07 0x00");
        tester.assertHasFrames("Hello");
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.1: A message compressed using 1 compressed DEFLATE block (with fragmentation)
     */
    @Test
    public void testDraft21_Hello_UnCompressedBlock_Fragmented() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // basic, 1 block, compressed with 0 compression level (aka, uncompressed).
        // Fragment 1
        // Fragment 2
        tester.parseIncomingHex("0x41 0x03 0xf2 0x48 0xcd", "0x80 0x04 0xc9 0xc9 0x07 0x00");
        tester.assertHasFrames(new TextFrame().setPayload("He").setFin(false), new ContinuationFrame().setPayload("llo").setFin(true));
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.2: Sharing LZ77 Sliding Window
     */
    @Test
    public void testDraft21_SharingL77SlidingWindow_ContextTakeover() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // context takeover (2 messages)
        // message 1
        // (HEADER added for this test)
        // message 2
        // (HEADER added for this test)
        tester.parseIncomingHex("0xc1 0x07", "0xf2 0x48 0xcd 0xc9 0xc9 0x07 0x00", "0xc1 0x07", "0xf2 0x48 0xcd 0xc9 0xc9 0x07 0x00");
        tester.assertHasFrames("Hello", "Hello");
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.2: Sharing LZ77 Sliding Window
     */
    @Test
    public void testDraft21_SharingL77SlidingWindow_NoContextTakeover() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // 2 message, shared LZ77 window
        // message 1
        // (HEADER added for this test)
        // message 2
        // (HEADER added for this test)
        tester.parseIncomingHex("0xc1 0x07", "0xf2 0x48 0xcd 0xc9 0xc9 0x07 0x00", "0xc1 0x05", "0xf2 0x00 0x11 0x00 0x00");
        tester.assertHasFrames("Hello", "Hello");
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.3: Using a DEFLATE Block with No Compression
     */
    @Test
    public void testDraft21_DeflateBlockWithNoCompression() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // 1 message / no compression
        // example frame
        tester.parseIncomingHex("0xc1 0x0b 0x00 0x05 0x00 0xfa 0xff 0x48 0x65 0x6c 0x6c 0x6f 0x00");
        tester.assertHasFrames("Hello");
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.4: Using a DEFLATE Block with BFINAL Set to 1
     */
    @Test
    public void testDraft21_DeflateBlockWithBFinal1() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // 1 message
        // header
        // example payload
        tester.parseIncomingHex("0xc1 0x08", "0xf3 0x48 0xcd 0xc9 0xc9 0x07 0x00 0x00");
        tester.assertHasFrames("Hello");
    }

    /**
     * Decode payload example as seen in draft-ietf-hybi-permessage-compression-21.
     * <p>
     * Section 8.2.3.5: Two DEFLATE Blocks in 1 Message
     */
    @Test
    public void testDraft21_TwoDeflateBlocksOneMessage() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // 1 message, 1 frame, 2 deflate blocks
        // (HEADER added for this test)
        tester.parseIncomingHex("0xc1 0x0d", "0xf2 0x48 0x05 0x00 0x00 0x00 0xff 0xff 0xca 0xc9 0xc9 0x07 0x00");
        tester.assertHasFrames("Hello");
    }

    /**
     * Decode fragmented message (3 parts: TEXT, CONTINUATION, CONTINUATION)
     */
    @Test
    public void testParseFragmentedMessage_Good() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        // 1 message, 3 frame
        // HEADER TEXT / fin=false / rsv1=true
        // HEADER CONTINUATION / fin=false / rsv1=false
        // HEADER CONTINUATION / fin=true / rsv1=false
        tester.parseIncomingHex("410C", "F248CDC9C95700000000FFFF", "000B", "0ACF2FCA4901000000FFFF", "8003", "520400");
        Frame txtFrame = new TextFrame().setPayload("Hello ").setFin(false);
        Frame con1Frame = new ContinuationFrame().setPayload("World").setFin(false);
        Frame con2Frame = new ContinuationFrame().setPayload("!").setFin(true);
        tester.assertHasFrames(txtFrame, con1Frame, con2Frame);
    }

    /**
     * Decode fragmented message (3 parts: TEXT, CONTINUATION, CONTINUATION)
     * <p>
     *     Continuation frames have RSV1 set, which MUST result in Failure
     * </p>
     */
    @Test
    public void testParseFragmentedMessage_BadRsv1() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate");
        tester.assertNegotiated("permessage-deflate");
        Assertions.assertThrows(ProtocolException.class, () -> // 1 message, 3 frame
        // Header TEXT / fin=false / rsv1=true
        // Payload
        // Header CONTINUATION / fin=false / rsv1=true
        // Payload
        // Header CONTINUATION / fin=true / rsv1=true
        // Payload
        tester.parseIncomingHex("410C", "F248CDC9C95700000000FFFF", "400B", "0ACF2FCA4901000000FFFF", "C003", "520400"));
    }

    /**
     * Incoming PING (Control Frame) should pass through extension unmodified
     */
    @Test
    public void testIncomingPing() {
        PerMessageDeflateExtension ext = new PerMessageDeflateExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("permessage-deflate");
        ext.setConfig(config);
        // Setup capture of incoming frames
        IncomingFramesCapture capture = new IncomingFramesCapture();
        // Wire up stack
        ext.setNextIncomingFrames(capture);
        String payload = "Are you there?";
        Frame ping = new PingFrame().setPayload(payload);
        ext.incomingFrame(ping);
        capture.assertFrameCount(1);
        capture.assertHasFrame(PING, 1);
        WebSocketFrame actual = capture.getFrames().poll();
        MatcherAssert.assertThat("Frame.opcode", actual.getOpCode(), Matchers.is(PING));
        MatcherAssert.assertThat("Frame.fin", actual.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", actual.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", actual.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", actual.isRsv3(), Matchers.is(false));
        ByteBuffer expected = BufferUtil.toBuffer(payload, StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Frame.payloadLength", actual.getPayloadLength(), Matchers.is(expected.remaining()));
        ByteBufferAssert.assertEquals("Frame.payload", expected, actual.getPayload().slice());
    }

    /**
     * Incoming Text Message fragmented into 3 pieces.
     */
    @Test
    public void testIncomingFragmented() {
        PerMessageDeflateExtension ext = new PerMessageDeflateExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("permessage-deflate");
        ext.setConfig(config);
        // Setup capture of incoming frames
        IncomingFramesCapture capture = new IncomingFramesCapture();
        // Wire up stack
        ext.setNextIncomingFrames(capture);
        String payload = "Are you there?";
        Frame ping = new PingFrame().setPayload(payload);
        ext.incomingFrame(ping);
        capture.assertFrameCount(1);
        capture.assertHasFrame(PING, 1);
        WebSocketFrame actual = capture.getFrames().poll();
        MatcherAssert.assertThat("Frame.opcode", actual.getOpCode(), Matchers.is(PING));
        MatcherAssert.assertThat("Frame.fin", actual.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", actual.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", actual.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", actual.isRsv3(), Matchers.is(false));
        ByteBuffer expected = BufferUtil.toBuffer(payload, StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Frame.payloadLength", actual.getPayloadLength(), Matchers.is(expected.remaining()));
        ByteBufferAssert.assertEquals("Frame.payload", expected, actual.getPayload().slice());
    }

    /**
     * Verify that incoming uncompressed frames are properly passed through
     */
    @Test
    public void testIncomingUncompressedFrames() {
        PerMessageDeflateExtension ext = new PerMessageDeflateExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("permessage-deflate");
        ext.setConfig(config);
        // Setup capture of incoming frames
        IncomingFramesCapture capture = new IncomingFramesCapture();
        // Wire up stack
        ext.setNextIncomingFrames(capture);
        // Quote
        List<String> quote = new ArrayList<>();
        quote.add("No amount of experimentation can ever prove me right;");
        quote.add("a single experiment can prove me wrong.");
        quote.add("-- Albert Einstein");
        // leave frames as-is, no compression, and pass into extension
        for (String q : quote) {
            TextFrame frame = new TextFrame().setPayload(q);
            frame.setRsv1(false);// indication to extension that frame is not compressed (ie: a normal frame)

            ext.incomingFrame(frame);
        }
        int len = quote.size();
        capture.assertFrameCount(len);
        capture.assertHasFrame(TEXT, len);
        String prefix;
        int i = 0;
        for (WebSocketFrame actual : capture.getFrames()) {
            prefix = ("Frame[" + i) + "]";
            MatcherAssert.assertThat((prefix + ".opcode"), actual.getOpCode(), Matchers.is(TEXT));
            MatcherAssert.assertThat((prefix + ".fin"), actual.isFin(), Matchers.is(true));
            MatcherAssert.assertThat((prefix + ".rsv1"), actual.isRsv1(), Matchers.is(false));
            MatcherAssert.assertThat((prefix + ".rsv2"), actual.isRsv2(), Matchers.is(false));
            MatcherAssert.assertThat((prefix + ".rsv3"), actual.isRsv3(), Matchers.is(false));
            ByteBuffer expected = BufferUtil.toBuffer(quote.get(i), StandardCharsets.UTF_8);
            MatcherAssert.assertThat((prefix + ".payloadLength"), actual.getPayloadLength(), Matchers.is(expected.remaining()));
            ByteBufferAssert.assertEquals((prefix + ".payload"), expected, actual.getPayload().slice());
            i++;
        }
    }

    /**
     * Outgoing PING (Control Frame) should pass through extension unmodified
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testOutgoingPing() throws IOException {
        PerMessageDeflateExtension ext = new PerMessageDeflateExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("permessage-deflate");
        ext.setConfig(config);
        // Setup capture of outgoing frames
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        // Wire up stack
        ext.setNextOutgoingFrames(capture);
        String payload = "Are you there?";
        Frame ping = new PingFrame().setPayload(payload);
        ext.outgoingFrame(ping, null, OFF);
        capture.assertFrameCount(1);
        capture.assertHasFrame(PING, 1);
        WebSocketFrame actual = capture.getFrames().getFirst();
        MatcherAssert.assertThat("Frame.opcode", actual.getOpCode(), Matchers.is(PING));
        MatcherAssert.assertThat("Frame.fin", actual.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", actual.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", actual.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", actual.isRsv3(), Matchers.is(false));
        ByteBuffer expected = BufferUtil.toBuffer(payload, StandardCharsets.UTF_8);
        MatcherAssert.assertThat("Frame.payloadLength", actual.getPayloadLength(), Matchers.is(expected.remaining()));
        ByteBufferAssert.assertEquals("Frame.payload", expected, actual.getPayload().slice());
    }

    /**
     * Outgoing Fragmented Message
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testOutgoingFragmentedMessage() throws IOException, InterruptedException {
        PerMessageDeflateExtension ext = new PerMessageDeflateExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("permessage-deflate");
        ext.setConfig(config);
        // Setup capture of outgoing frames
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        // Wire up stack
        ext.setNextOutgoingFrames(capture);
        Frame txtFrame = new TextFrame().setPayload("Hello ").setFin(false);
        Frame con1Frame = new ContinuationFrame().setPayload("World").setFin(false);
        Frame con2Frame = new ContinuationFrame().setPayload("!").setFin(true);
        ext.outgoingFrame(txtFrame, null, OFF);
        ext.outgoingFrame(con1Frame, null, OFF);
        ext.outgoingFrame(con2Frame, null, OFF);
        capture.assertFrameCount(3);
        WebSocketFrame capturedFrame;
        capturedFrame = capture.getFrames().poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Frame.opcode", capturedFrame.getOpCode(), Matchers.is(TEXT));
        MatcherAssert.assertThat("Frame.fin", capturedFrame.isFin(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv1", capturedFrame.isRsv1(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv2", capturedFrame.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", capturedFrame.isRsv3(), Matchers.is(false));
        capturedFrame = capture.getFrames().poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Frame.opcode", capturedFrame.getOpCode(), Matchers.is(CONTINUATION));
        MatcherAssert.assertThat("Frame.fin", capturedFrame.isFin(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv1", capturedFrame.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", capturedFrame.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", capturedFrame.isRsv3(), Matchers.is(false));
        capturedFrame = capture.getFrames().poll(1, TimeUnit.SECONDS);
        MatcherAssert.assertThat("Frame.opcode", capturedFrame.getOpCode(), Matchers.is(CONTINUATION));
        MatcherAssert.assertThat("Frame.fin", capturedFrame.isFin(), Matchers.is(true));
        MatcherAssert.assertThat("Frame.rsv1", capturedFrame.isRsv1(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv2", capturedFrame.isRsv2(), Matchers.is(false));
        MatcherAssert.assertThat("Frame.rsv3", capturedFrame.isRsv3(), Matchers.is(false));
    }

    @Test
    public void testPyWebSocket_Client_NoContextTakeover_ThreeOra() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate; client_max_window_bits; client_no_context_takeover");
        tester.assertNegotiated("permessage-deflate");
        // Captured from Pywebsocket (r790) - 3 messages with similar parts.
        // context takeover (3 messages)
        // ToraTora
        // AtoraFlora
        // PhloraTora
        tester.parseIncomingHex("c1 09 0a c9 2f 4a 0c 01  62 00 00", "c1 0b 72 2c c9 2f 4a 74  cb 01 12 00 00", "c1 0b 0a c8 c8 c9 2f 4a  0c 01 62 00 00");
        tester.assertHasFrames("ToraTora", "AtoraFlora", "PhloraTora");
    }

    @Test
    public void testPyWebSocket_Client_ToraToraTora() {
        ExtensionTool.Tester tester = clientExtensions.newTester("permessage-deflate; client_max_window_bits");
        tester.assertNegotiated("permessage-deflate");
        // Captured from Pywebsocket (r790) - "tora" sent 3 times.
        // context takeover (3 messages)
        // tora 1
        // tora 2
        // tora 3
        tester.parseIncomingHex("c1 06 2a c9 2f 4a 04 00", "c1 05 2a 01 62 00 00", "c1 04 02 61 00 00");
        tester.assertHasFrames("tora", "tora", "tora");
    }

    @Test
    public void testPyWebSocket_Server_NoContextTakeover_ThreeOra() {
        ExtensionTool.Tester tester = serverExtensions.newTester("permessage-deflate; client_max_window_bits; client_no_context_takeover");
        tester.assertNegotiated("permessage-deflate");
        // Captured from Pywebsocket (r790) - 3 messages with similar parts.
        // context takeover (3 messages)
        // ToraTora
        // AtoraFlora
        // PhloraTora
        tester.parseIncomingHex("c1 89 88 bc 1b b1 82 75  34 fb 84 bd 79 b1 88", "c1 8b 50 86 88 b2 22 aa  41 9d 1a f2 43 b3 42 86 88", "c1 8b e2 3e 05 53 e8 f6  cd 9a cd 74 09 52 80 3e 05");
        tester.assertHasFrames("ToraTora", "AtoraFlora", "PhloraTora");
    }

    @Test
    public void testPyWebSocket_Server_ToraToraTora() {
        ExtensionTool.Tester tester = serverExtensions.newTester("permessage-deflate; client_max_window_bits");
        tester.assertNegotiated("permessage-deflate");
        // Captured from Pywebsocket (r790) - "tora" sent 3 times.
        // context takeover (3 messages)
        // tora 1
        // tora 2
        // tora 3
        tester.parseIncomingHex("c1 86 69 39 fe 91 43 f0  d1 db 6d 39", "c1 85 2d f3 eb 96 07 f2  89 96 2d", "c1 84 53 ad a5 34 51 cc  a5 34");
        tester.assertHasFrames("tora", "tora", "tora");
    }
}

