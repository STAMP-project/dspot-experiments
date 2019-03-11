/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DefaultHttp2LocalFlowController}.
 */
public class DefaultHttp2LocalFlowControllerTest {
    private static final int STREAM_ID = 1;

    private DefaultHttp2LocalFlowController controller;

    @Mock
    private Http2FrameWriter frameWriter;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private EventExecutor executor;

    @Mock
    private ChannelPromise promise;

    private DefaultHttp2Connection connection;

    @Test
    public void dataFrameShouldBeAccepted() throws Http2Exception {
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, 10, 0, false);
        verifyWindowUpdateNotSent();
    }

    @Test
    public void windowUpdateShouldSendOnceBytesReturned() throws Http2Exception {
        int dataSize = ((int) ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) * (DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO))) + 1;
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize, 0, false);
        // Return only a few bytes and verify that the WINDOW_UPDATE hasn't been sent.
        Assert.assertFalse(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, 10));
        verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
        verifyWindowUpdateNotSent(Http2CodecUtil.CONNECTION_STREAM_ID);
        // Return the rest and verify the WINDOW_UPDATE is sent.
        Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, (dataSize - 10)));
        verifyWindowUpdateSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize);
        verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, dataSize);
        Mockito.verifyNoMoreInteractions(frameWriter);
    }

    @Test
    public void connectionWindowShouldAutoRefillWhenDataReceived() throws Http2Exception {
        // Reconfigure controller to auto-refill the connection window.
        initController(true);
        int dataSize = ((int) ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) * (DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO))) + 1;
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize, 0, false);
        // Verify that we immediately refill the connection window.
        verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, dataSize);
        // Return only a few bytes and verify that the WINDOW_UPDATE hasn't been sent for the stream.
        Assert.assertFalse(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, 10));
        verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
        // Return the rest and verify the WINDOW_UPDATE is sent for the stream.
        Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, (dataSize - 10)));
        verifyWindowUpdateSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize);
        Mockito.verifyNoMoreInteractions(frameWriter);
    }

    @Test(expected = Http2Exception.class)
    public void connectionFlowControlExceededShouldThrow() throws Http2Exception {
        // Window exceeded because of the padding.
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, Http2CodecUtil.DEFAULT_WINDOW_SIZE, 1, true);
    }

    @Test
    public void windowUpdateShouldNotBeSentAfterEndOfStream() throws Http2Exception {
        int dataSize = ((int) ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) * (DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO))) + 1;
        // Set end-of-stream on the frame, so no window update will be sent for the stream.
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize, 0, true);
        verifyWindowUpdateNotSent(Http2CodecUtil.CONNECTION_STREAM_ID);
        verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
        Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize));
        verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, dataSize);
        verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
    }

    @Test
    public void halfWindowRemainingShouldUpdateAllWindows() throws Http2Exception {
        int dataSize = ((int) ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) * (DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO))) + 1;
        int initialWindowSize = Http2CodecUtil.DEFAULT_WINDOW_SIZE;
        int windowDelta = DefaultHttp2LocalFlowControllerTest.getWindowDelta(initialWindowSize, initialWindowSize, dataSize);
        // Don't set end-of-stream so we'll get a window update for the stream as well.
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize, 0, false);
        Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, dataSize));
        verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, windowDelta);
        verifyWindowUpdateSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID, windowDelta);
    }

    @Test
    public void initialWindowUpdateShouldAllowMoreFrames() throws Http2Exception {
        // Send a frame that takes up the entire window.
        int initialWindowSize = Http2CodecUtil.DEFAULT_WINDOW_SIZE;
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, initialWindowSize, 0, false);
        Assert.assertEquals(0, window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, initialWindowSize);
        Assert.assertEquals(initialWindowSize, window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        // Update the initial window size to allow another frame.
        int newInitialWindowSize = 2 * initialWindowSize;
        controller.initialWindowSize(newInitialWindowSize);
        Assert.assertEquals(newInitialWindowSize, window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        // Clear any previous calls to the writer.
        Mockito.reset(frameWriter);
        // Send the next frame and verify that the expected window updates were sent.
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, initialWindowSize, 0, false);
        Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, initialWindowSize));
        int delta = newInitialWindowSize - initialWindowSize;
        verifyWindowUpdateSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID, delta);
        verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, delta);
    }

    @Test
    public void connectionWindowShouldAdjustWithMultipleStreams() throws Http2Exception {
        int newStreamId = 3;
        connection.local().createStream(newStreamId, false);
        try {
            Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
            Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(Http2CodecUtil.CONNECTION_STREAM_ID));
            // Test that both stream and connection window are updated (or not updated) together
            int data1 = ((int) ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) * (DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO))) + 1;
            receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, data1, 0, false);
            verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
            verifyWindowUpdateNotSent(Http2CodecUtil.CONNECTION_STREAM_ID);
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(Http2CodecUtil.CONNECTION_STREAM_ID));
            Assert.assertTrue(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, data1));
            verifyWindowUpdateSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID, data1);
            verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, data1);
            Mockito.reset(frameWriter);
            // Create a scenario where data is depleted from multiple streams, but not enough data
            // to generate a window update on those streams. The amount will be enough to generate
            // a window update for the connection stream.
            --data1;
            int data2 = data1 >> 1;
            receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, data1, 0, false);
            receiveFlowControlledFrame(newStreamId, data1, 0, false);
            verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
            verifyWindowUpdateNotSent(newStreamId);
            verifyWindowUpdateNotSent(Http2CodecUtil.CONNECTION_STREAM_ID);
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(newStreamId));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - (data1 << 1)), window(Http2CodecUtil.CONNECTION_STREAM_ID));
            Assert.assertFalse(consumeBytes(DefaultHttp2LocalFlowControllerTest.STREAM_ID, data1));
            Assert.assertTrue(consumeBytes(newStreamId, data2));
            verifyWindowUpdateNotSent(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
            verifyWindowUpdateNotSent(newStreamId);
            verifyWindowUpdateSent(Http2CodecUtil.CONNECTION_STREAM_ID, (data1 + data2));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(DefaultHttp2LocalFlowControllerTest.STREAM_ID));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - data1), window(newStreamId));
            Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - (data1 - data2)), window(Http2CodecUtil.CONNECTION_STREAM_ID));
        } finally {
            connection.stream(newStreamId).close();
        }
    }

    @Test
    public void closeShouldConsumeBytes() throws Http2Exception {
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, 10, 0, false);
        Assert.assertEquals(10, controller.unconsumedBytes(connection.connectionStream()));
        stream(DefaultHttp2LocalFlowControllerTest.STREAM_ID).close();
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
    }

    @Test
    public void closeShouldNotConsumeConnectionWindowWhenAutoRefilled() throws Http2Exception {
        // Reconfigure controller to auto-refill the connection window.
        initController(true);
        receiveFlowControlledFrame(DefaultHttp2LocalFlowControllerTest.STREAM_ID, 10, 0, false);
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
        stream(DefaultHttp2LocalFlowControllerTest.STREAM_ID).close();
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
    }

    @Test
    public void dataReceivedForClosedStreamShouldImmediatelyConsumeBytes() throws Http2Exception {
        Http2Stream stream = stream(DefaultHttp2LocalFlowControllerTest.STREAM_ID);
        stream.close();
        receiveFlowControlledFrame(stream, 10, 0, false);
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
    }

    @Test
    public void dataReceivedForNullStreamShouldImmediatelyConsumeBytes() throws Http2Exception {
        receiveFlowControlledFrame(null, 10, 0, false);
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
    }

    @Test
    public void consumeBytesForNullStreamShouldIgnore() throws Http2Exception {
        controller.consumeBytes(null, 10);
        Assert.assertEquals(0, controller.unconsumedBytes(connection.connectionStream()));
    }

    @Test
    public void globalRatioShouldImpactStreams() throws Http2Exception {
        float ratio = 0.6F;
        controller.windowUpdateRatio(ratio);
        testRatio(ratio, ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) << 1), 3, false);
    }

    @Test
    public void streamlRatioShouldImpactStreams() throws Http2Exception {
        float ratio = 0.6F;
        testRatio(ratio, ((Http2CodecUtil.DEFAULT_WINDOW_SIZE) << 1), 3, true);
    }

    @Test
    public void consumeBytesForZeroNumBytesShouldIgnore() throws Http2Exception {
        Assert.assertFalse(controller.consumeBytes(connection.stream(DefaultHttp2LocalFlowControllerTest.STREAM_ID), 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void consumeBytesForNegativeNumBytesShouldFail() throws Http2Exception {
        Assert.assertFalse(controller.consumeBytes(connection.stream(DefaultHttp2LocalFlowControllerTest.STREAM_ID), (-1)));
    }
}

