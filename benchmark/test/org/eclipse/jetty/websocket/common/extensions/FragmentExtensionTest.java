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
package org.eclipse.jetty.websocket.common.extensions;


import BatchMode.OFF;
import OpCode.PING;
import OpCode.TEXT;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.SaneFrameOrderingAssertion;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.extensions.fragment.FragmentExtension;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.io.FutureWriteCallback;
import org.eclipse.jetty.websocket.common.test.ByteBufferAssert;
import org.eclipse.jetty.websocket.common.test.IncomingFramesCapture;
import org.eclipse.jetty.websocket.common.test.OutgoingFramesCapture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


@SuppressWarnings("Duplicates")
public class FragmentExtensionTest {
    private static final Logger LOG = Log.getLogger(FragmentExtensionTest.class);

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    /**
     * Verify that incoming frames are passed thru without modification
     */
    @Test
    public void testIncomingFrames() {
        IncomingFramesCapture capture = new IncomingFramesCapture();
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newClientPolicy());
        ExtensionConfig config = ExtensionConfig.parse("fragment;maxLength=4");
        ext.setConfig(config);
        ext.setNextIncomingFrames(capture);
        // Quote
        List<String> quote = new ArrayList<>();
        quote.add("No amount of experimentation can ever prove me right;");
        quote.add("a single experiment can prove me wrong.");
        quote.add("-- Albert Einstein");
        // Manually create frame and pass into extension
        for (String q : quote) {
            Frame frame = new TextFrame().setPayload(q);
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
     * Incoming PING (Control Frame) should pass through extension unmodified
     */
    @Test
    public void testIncomingPing() {
        IncomingFramesCapture capture = new IncomingFramesCapture();
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("fragment;maxLength=4");
        ext.setConfig(config);
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
     * Verify that outgoing text frames are fragmented by the maxLength configuration.
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testOutgoingFramesByMaxLength() throws IOException, InterruptedException {
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("fragment;maxLength=20");
        ext.setConfig(config);
        ext.setNextOutgoingFrames(capture);
        // Quote
        List<String> quote = new ArrayList<>();
        quote.add("No amount of experimentation can ever prove me right;");
        quote.add("a single experiment can prove me wrong.");
        quote.add("-- Albert Einstein");
        // Write quote as separate frames
        for (String section : quote) {
            Frame frame = new TextFrame().setPayload(section);
            ext.outgoingFrame(frame, null, OFF);
        }
        // Expected Frames
        List<WebSocketFrame> expectedFrames = new ArrayList<>();
        expectedFrames.add(new TextFrame().setPayload("No amount of experim").setFin(false));
        expectedFrames.add(new ContinuationFrame().setPayload("entation can ever pr").setFin(false));
        expectedFrames.add(new ContinuationFrame().setPayload("ove me right;").setFin(true));
        expectedFrames.add(new TextFrame().setPayload("a single experiment ").setFin(false));
        expectedFrames.add(new ContinuationFrame().setPayload("can prove me wrong.").setFin(true));
        expectedFrames.add(new TextFrame().setPayload("-- Albert Einstein").setFin(true));
        // capture.dump();
        int len = expectedFrames.size();
        capture.assertFrameCount(len);
        String prefix;
        LinkedBlockingDeque<WebSocketFrame> frames = capture.getFrames();
        for (int i = 0; i < len; i++) {
            prefix = ("Frame[" + i) + "]";
            WebSocketFrame actualFrame = frames.poll(1, TimeUnit.SECONDS);
            WebSocketFrame expectedFrame = expectedFrames.get(i);
            // System.out.printf("actual: %s%n",actualFrame);
            // System.out.printf("expect: %s%n",expectedFrame);
            // Validate Frame
            MatcherAssert.assertThat((prefix + ".opcode"), actualFrame.getOpCode(), Matchers.is(expectedFrame.getOpCode()));
            MatcherAssert.assertThat((prefix + ".fin"), actualFrame.isFin(), Matchers.is(expectedFrame.isFin()));
            MatcherAssert.assertThat((prefix + ".rsv1"), actualFrame.isRsv1(), Matchers.is(expectedFrame.isRsv1()));
            MatcherAssert.assertThat((prefix + ".rsv2"), actualFrame.isRsv2(), Matchers.is(expectedFrame.isRsv2()));
            MatcherAssert.assertThat((prefix + ".rsv3"), actualFrame.isRsv3(), Matchers.is(expectedFrame.isRsv3()));
            // Validate Payload
            ByteBuffer expectedData = expectedFrame.getPayload().slice();
            ByteBuffer actualData = actualFrame.getPayload().slice();
            MatcherAssert.assertThat((prefix + ".payloadLength"), actualData.remaining(), Matchers.is(expectedData.remaining()));
            ByteBufferAssert.assertEquals((prefix + ".payload"), expectedData, actualData);
        }
    }

    /**
     * Verify that outgoing text frames are not fragmented by default configuration (which has no maxLength specified)
     *
     * @throws IOException
     * 		on test failure
     */
    @Test
    public void testOutgoingFramesDefaultConfig() throws Exception {
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("fragment");
        ext.setConfig(config);
        ext.setNextOutgoingFrames(capture);
        // Quote
        List<String> quote = new ArrayList<>();
        quote.add("No amount of experimentation can ever prove me right;");
        quote.add("a single experiment can prove me wrong.");
        quote.add("-- Albert Einstein");
        // Write quote as separate frames
        for (String section : quote) {
            Frame frame = new TextFrame().setPayload(section);
            ext.outgoingFrame(frame, null, OFF);
        }
        // Expected Frames
        List<WebSocketFrame> expectedFrames = new ArrayList<>();
        expectedFrames.add(new TextFrame().setPayload("No amount of experimentation can ever prove me right;"));
        expectedFrames.add(new TextFrame().setPayload("a single experiment can prove me wrong."));
        expectedFrames.add(new TextFrame().setPayload("-- Albert Einstein"));
        // capture.dump();
        int len = expectedFrames.size();
        capture.assertFrameCount(len);
        String prefix;
        LinkedBlockingDeque<WebSocketFrame> frames = capture.getFrames();
        for (int i = 0; i < len; i++) {
            prefix = ("Frame[" + i) + "]";
            WebSocketFrame actualFrame = frames.poll(1, TimeUnit.SECONDS);
            WebSocketFrame expectedFrame = expectedFrames.get(i);
            // Validate Frame
            MatcherAssert.assertThat((prefix + ".opcode"), actualFrame.getOpCode(), Matchers.is(expectedFrame.getOpCode()));
            MatcherAssert.assertThat((prefix + ".fin"), actualFrame.isFin(), Matchers.is(expectedFrame.isFin()));
            MatcherAssert.assertThat((prefix + ".rsv1"), actualFrame.isRsv1(), Matchers.is(expectedFrame.isRsv1()));
            MatcherAssert.assertThat((prefix + ".rsv2"), actualFrame.isRsv2(), Matchers.is(expectedFrame.isRsv2()));
            MatcherAssert.assertThat((prefix + ".rsv3"), actualFrame.isRsv3(), Matchers.is(expectedFrame.isRsv3()));
            // Validate Payload
            ByteBuffer expectedData = expectedFrame.getPayload().slice();
            ByteBuffer actualData = actualFrame.getPayload().slice();
            MatcherAssert.assertThat((prefix + ".payloadLength"), actualData.remaining(), Matchers.is(expectedData.remaining()));
            ByteBufferAssert.assertEquals((prefix + ".payload"), expectedData, actualData);
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
        OutgoingFramesCapture capture = new OutgoingFramesCapture();
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse("fragment;maxLength=4");
        ext.setConfig(config);
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
     * Ensure that FragmentExtension honors the correct order of websocket frames.
     *
     * @see <a href="https://github.com/eclipse/jetty.project/issues/2491">eclipse/jetty.project#2491</a>
     */
    @Test
    public void testLargeSmallTextAlternating() throws Exception {
        final int largeMessageSize = 60000;
        byte[] buf = new byte[largeMessageSize];
        Arrays.fill(buf, ((byte) ('x')));
        String largeMessage = new String(buf, StandardCharsets.UTF_8);
        final int fragmentCount = 10;
        final int fragmentLength = largeMessageSize / fragmentCount;
        final int messageCount = 10000;
        FragmentExtension ext = new FragmentExtension();
        ext.setBufferPool(bufferPool);
        ext.setPolicy(WebSocketPolicy.newServerPolicy());
        ExtensionConfig config = ExtensionConfig.parse(("fragment;maxLength=" + fragmentLength));
        ext.setConfig(config);
        SaneFrameOrderingAssertion saneFrameOrderingAssertion = new SaneFrameOrderingAssertion();
        ext.setNextOutgoingFrames(saneFrameOrderingAssertion);
        CompletableFuture<Integer> enqueuedFrameCountFut = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            // Run Server Task
            int frameCount = 0;
            BatchMode batchMode = BatchMode.OFF;
            try {
                for (int i = 0; i < messageCount; i++) {
                    int messageId = i;
                    FutureWriteCallback callback = new FutureWriteCallback();
                    WebSocketFrame frame;
                    if ((i % 2) == 0) {
                        frame = new TextFrame().setPayload(largeMessage);
                        frameCount += fragmentCount;
                    } else {
                        frame = new TextFrame().setPayload(("Short Message: " + i));
                        frameCount++;
                    }
                    ext.outgoingFrame(frame, callback, batchMode);
                    callback.get();
                }
                enqueuedFrameCountFut.complete(frameCount);
            } catch (Throwable t) {
                enqueuedFrameCountFut.completeExceptionally(t);
            }
        });
        int enqueuedFrameCount = enqueuedFrameCountFut.get(5, TimeUnit.SECONDS);
        int expectedFrameCount = (messageCount / 2) * fragmentCount;// large messages

        expectedFrameCount += messageCount / 2;// + short messages

        MatcherAssert.assertThat("Saw expected frame count", saneFrameOrderingAssertion.frameCount, Matchers.is(expectedFrameCount));
        MatcherAssert.assertThat("Enqueued expected frame count", enqueuedFrameCount, Matchers.is(expectedFrameCount));
    }
}

