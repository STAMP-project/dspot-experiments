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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.api.extensions.IncomingFrames;
import org.eclipse.jetty.websocket.api.extensions.OutgoingFrames;
import org.eclipse.jetty.websocket.common.Generator;
import org.eclipse.jetty.websocket.common.extensions.AbstractExtensionTest;
import org.eclipse.jetty.websocket.common.extensions.ExtensionTool;
import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.OutgoingNetworkBytesCapture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DeflateFrameExtensionTest extends AbstractExtensionTest {
    private static final Logger LOG = Log.getLogger(DeflateFrameExtensionTest.class);

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testBlockheadClient_HelloThere() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Blockhead Client - "Hello" then "There" via unit test
        // "Hello"
        // "There"
        tester.parseIncomingHex("c18700000000f248cdc9c90700", "c187000000000ac9482d4a0500");
        tester.assertHasFrames("Hello", "There");
    }

    @Test
    public void testChrome20_Hello() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Chrome 20.x - "Hello" (sent from browser)
        // "Hello"
        tester.parseIncomingHex("c187832b5c11716391d84a2c5c");
        tester.assertHasFrames("Hello");
    }

    @Test
    public void testChrome20_HelloThere() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Chrome 20.x - "Hello" then "There" (sent from browser)
        // "Hello"
        // There
        tester.parseIncomingHex("c1877b1971db8951bc12b21e71", "c18759edc8f4532480d913e8c8");
        tester.assertHasFrames("Hello", "There");
    }

    @Test
    public void testChrome20_Info() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Chrome 20.x - "info:" (sent from browser)
        // example payload
        tester.parseIncomingHex("c187ca4def7f0081a4b47d4fef");
        tester.assertHasFrames("info:");
    }

    @Test
    public void testChrome20_TimeTime() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Chrome 20.x - "time:" then "time:" once more (sent from browser)
        // "time:"
        // "time:"
        tester.parseIncomingHex("c18782467424a88fb869374474", "c1853cfda17f16fcb07f3c");
        tester.assertHasFrames("time:", "time:");
    }

    @Test
    public void testPyWebSocket_TimeTimeTime() {
        ExtensionTool.Tester tester = serverExtensions.newTester("deflate-frame");
        tester.assertNegotiated("deflate-frame");
        // Captured from Pywebsocket (r781) - "time:" sent 3 times.
        // "time:"
        // "time:"
        // "time:"
        tester.parseIncomingHex(("c1876b100104" + "41d9cd49de1201"), ("c1852ae3ff01" + "00e2ee012a"), ("c18435558caa" + "37468caa"));
        tester.assertHasFrames("time:", "time:", "time:");
    }

    @Test
    public void testCompress_TimeTimeTime() {
        // What pywebsocket produces for "time:", "time:", "time:"
        String[] expected = new String[]{ "2AC9CC4DB50200", "2A01110000", "02130000" };
        // Lets see what we produce
        CapturedHexPayloads capture = new CapturedHexPayloads();
        DeflateFrameExtension ext = new DeflateFrameExtension();
        init(ext);
        ext.setNextOutgoingFrames(capture);
        ext.outgoingFrame(new TextFrame().setPayload("time:"), null, OFF);
        ext.outgoingFrame(new TextFrame().setPayload("time:"), null, OFF);
        ext.outgoingFrame(new TextFrame().setPayload("time:"), null, OFF);
        List<String> actual = capture.getCaptured();
        MatcherAssert.assertThat("Compressed Payloads", actual, Matchers.contains(expected));
    }

    @Test
    public void testDeflateBasics() throws Exception {
        // Setup deflater basics
        Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
        compressor.setStrategy(Deflater.DEFAULT_STRATEGY);
        // Text to compress
        String text = "info:";
        byte[] uncompressed = StringUtil.getUtf8Bytes(text);
        // Prime the compressor
        compressor.reset();
        compressor.setInput(uncompressed, 0, uncompressed.length);
        compressor.finish();
        // Perform compression
        ByteBuffer outbuf = ByteBuffer.allocate(64);
        BufferUtil.clearToFill(outbuf);
        while (!(compressor.finished())) {
            byte[] out = new byte[64];
            int len = compressor.deflate(out, 0, out.length, Deflater.SYNC_FLUSH);
            if (len > 0) {
                outbuf.put(out, 0, len);
            }
        } 
        compressor.end();
        BufferUtil.flipToFlush(outbuf, 0);
        byte[] compressed = BufferUtil.toArray(outbuf);
        // Clear the BFINAL bit that has been set by the compressor.end() call.
        // In the real implementation we never end() the compressor.
        compressed[0] &= 254;
        String actual = TypeUtil.toHexString(compressed);
        String expected = "CaCc4bCbB70200";// what pywebsocket produces

        MatcherAssert.assertThat("Compressed data", actual, Matchers.is(expected));
    }

    @Test
    public void testGeneratedTwoFrames() throws IOException {
        WebSocketPolicy policy = WebSocketPolicy.newClientPolicy();
        DeflateFrameExtension ext = new DeflateFrameExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(policy);
        ext.setConfig(new ExtensionConfig(ext.getName()));
        Generator generator = new Generator(policy, bufferPool, true);
        generator.configureFromExtensions(Collections.singletonList(ext));
        OutgoingNetworkBytesCapture capture = new OutgoingNetworkBytesCapture(generator);
        ext.setNextOutgoingFrames(capture);
        ext.outgoingFrame(new TextFrame().setPayload("Hello"), null, OFF);
        ext.outgoingFrame(new TextFrame(), null, OFF);
        ext.outgoingFrame(new TextFrame().setPayload("There"), null, OFF);
        capture.assertBytes(0, "c107f248cdc9c90700");
    }

    @Test
    public void testInflateBasics() throws Exception {
        // should result in "info:" text if properly inflated
        byte[] rawbuf = TypeUtil.fromHexString("CaCc4bCbB70200");// what pywebsocket produces

        // byte rawbuf[] = TypeUtil.fromHexString("CbCc4bCbB70200"); // what java produces
        Inflater inflater = new Inflater(true);
        inflater.reset();
        inflater.setInput(rawbuf, 0, rawbuf.length);
        byte[] outbuf = new byte[64];
        int len = inflater.inflate(outbuf);
        inflater.end();
        MatcherAssert.assertThat("Inflated length", len, Matchers.greaterThan(4));
        String actual = StringUtil.toUTF8String(outbuf, 0, len);
        MatcherAssert.assertThat("Inflated text", actual, Matchers.is("info:"));
    }

    @Test
    public void testPyWebSocketServer_Hello() {
        // Captured from PyWebSocket - "Hello" (echo from server)
        byte[] rawbuf = TypeUtil.fromHexString("c107f248cdc9c90700");
        assertIncoming(rawbuf, "Hello");
    }

    @Test
    public void testPyWebSocketServer_Long() {
        // Captured from PyWebSocket - Long Text (echo from server)
        byte[] rawbuf = TypeUtil.fromHexString(("c1421cca410a80300c44d1abccce9df7" + ((("f018298634d05631138ab7b7b8fdef1f" + "dc0282e2061d575a45f6f2686bab25e1") + "3fb7296fa02b5885eb3b0379c394f461") + "98cafd03")));
        assertIncoming(rawbuf, "It's a big enough umbrella but it's always me that ends up getting wet.");
    }

    @Test
    public void testPyWebSocketServer_Medium() {
        // Captured from PyWebSocket - "stackoverflow" (echo from server)
        byte[] rawbuf = TypeUtil.fromHexString("c10f2a2e494ccece2f4b2d4acbc92f0700");
        assertIncoming(rawbuf, "stackoverflow");
    }

    /**
     * Make sure that the server generated compressed form for "Hello" is consistent with what PyWebSocket creates.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testServerGeneratedHello() throws IOException {
        assertOutgoing("Hello", "c107f248cdc9c90700");
    }

    /**
     * Make sure that the server generated compressed form for "There" is consistent with what PyWebSocket creates.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testServerGeneratedThere() throws IOException {
        assertOutgoing("There", "c1070ac9482d4a0500");
    }

    @Test
    public void testCompressAndDecompressBigPayload() throws Exception {
        byte[] input = new byte[1024 * 1024];
        // Make them not compressible.
        new Random().nextBytes(input);
        int maxMessageSize = (1024 * 1024) + 8192;
        DeflateFrameExtension clientExtension = new DeflateFrameExtension();
        clientExtension.setBufferPool(bufferPool);
        clientExtension.setPolicy(WebSocketPolicy.newClientPolicy());
        clientExtension.getPolicy().setMaxBinaryMessageSize(maxMessageSize);
        clientExtension.getPolicy().setMaxBinaryMessageBufferSize(maxMessageSize);
        clientExtension.setConfig(ExtensionConfig.parse("deflate-frame"));
        final DeflateFrameExtension serverExtension = new DeflateFrameExtension();
        serverExtension.setBufferPool(bufferPool);
        serverExtension.setPolicy(WebSocketPolicy.newServerPolicy());
        serverExtension.getPolicy().setMaxBinaryMessageSize(maxMessageSize);
        serverExtension.getPolicy().setMaxBinaryMessageBufferSize(maxMessageSize);
        serverExtension.setConfig(ExtensionConfig.parse("deflate-frame"));
        // Chain the next element to decompress.
        clientExtension.setNextOutgoingFrames(new OutgoingFrames() {
            @Override
            public void outgoingFrame(Frame frame, WriteCallback callback, BatchMode batchMode) {
                DeflateFrameExtensionTest.LOG.debug("outgoingFrame({})", frame);
                serverExtension.incomingFrame(frame);
                callback.writeSuccess();
            }
        });
        final ByteArrayOutputStream result = new ByteArrayOutputStream(input.length);
        serverExtension.setNextIncomingFrames(new IncomingFrames() {
            @Override
            public void incomingFrame(Frame frame) {
                DeflateFrameExtensionTest.LOG.debug("incomingFrame({})", frame);
                try {
                    result.write(BufferUtil.toArray(frame.getPayload()));
                } catch (IOException x) {
                    throw new RuntimeIOException(x);
                }
            }
        });
        BinaryFrame frame = new BinaryFrame();
        frame.setPayload(input);
        frame.setFin(true);
        clientExtension.outgoingFrame(frame, null, OFF);
        Assertions.assertArrayEquals(input, result.toByteArray());
    }
}

