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


import Http2Error.STREAM_CLOSED;
import Http2RemoteFlowController.FlowControlled;
import Http2RemoteFlowController.Listener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link DefaultHttp2RemoteFlowController}.
 */
public abstract class DefaultHttp2RemoteFlowControllerTest {
    private static final int STREAM_A = 1;

    private static final int STREAM_B = 3;

    private static final int STREAM_C = 5;

    private static final int STREAM_D = 7;

    private DefaultHttp2RemoteFlowController controller;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Mock
    private ChannelConfig config;

    @Mock
    private EventExecutor executor;

    @Mock
    private ChannelPromise promise;

    @Mock
    private Listener listener;

    private DefaultHttp2Connection connection;

    @Test
    public void initialWindowSizeShouldOnlyChangeStreams() throws Http2Exception {
        controller.initialWindowSize(0);
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        assertWritabilityChanged(1, false);
    }

    @Test
    public void windowUpdateShouldChangeConnectionWindow() throws Http2Exception {
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, 100);
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) + 100), window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void windowUpdateShouldChangeStreamWindow() throws Http2Exception {
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 100);
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) + 100), window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void payloadSmallerThanWindowShouldBeWrittenImmediately() throws Http2Exception {
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        data.assertNotWritten();
        Mockito.verifyZeroInteractions(listener);
        controller.writePendingBytes();
        data.assertFullyWritten();
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void emptyPayloadShouldBeWrittenImmediately() throws Http2Exception {
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(0);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        data.assertNotWritten();
        controller.writePendingBytes();
        data.assertFullyWritten();
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void unflushedPayloadsShouldBeDroppedOnCancel() throws Http2Exception {
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5);
        Http2Stream streamA = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        streamA.close();
        controller.writePendingBytes();
        data.assertNotWritten();
        controller.writePendingBytes();
        data.assertNotWritten();
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(streamA);
        Assert.assertFalse(controller.isWritable(streamA));
    }

    @Test
    public void payloadsShouldMerge() throws Http2Exception {
        controller.initialWindowSize(15);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data1 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5, true);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data2 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10, true);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data1);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data2);
        data1.assertNotWritten();
        data1.assertNotWritten();
        data2.assertMerged();
        controller.writePendingBytes();
        data1.assertFullyWritten();
        data2.assertNotWritten();
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
    }

    @Test
    public void flowControllerCorrectlyAccountsForBytesWithMerge() throws Http2Exception {
        controller.initialWindowSize(112);// This must be more than the total merged frame size 110

        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data1 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5, 2, true);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data2 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5, 100, true);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data1);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data2);
        data1.assertNotWritten();
        data1.assertNotWritten();
        data2.assertMerged();
        controller.writePendingBytes();
        data1.assertFullyWritten();
        data2.assertNotWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
    }

    @Test
    public void stalledStreamShouldQueuePayloads() throws Http2Exception {
        controller.initialWindowSize(0);
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(15);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled moreData = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(0);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, moreData);
        controller.writePendingBytes();
        moreData.assertNotWritten();
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void queuedPayloadsReceiveErrorOnStreamClose() throws Http2Exception {
        controller.initialWindowSize(0);
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(15);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled moreData = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(0);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, moreData);
        controller.writePendingBytes();
        moreData.assertNotWritten();
        connection.stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A).close();
        data.assertError(STREAM_CLOSED);
        moreData.assertError(STREAM_CLOSED);
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void payloadLargerThanWindowShouldWritePartial() throws Http2Exception {
        controller.initialWindowSize(5);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.reset(listener);
        final DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        // Verify that a partial frame of 5 remains to be sent
        data.assertPartiallyWritten(5);
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void windowUpdateAndFlushShouldTriggerWrite() throws Http2Exception {
        controller.initialWindowSize(10);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(20);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled moreData = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, moreData);
        controller.writePendingBytes();
        data.assertPartiallyWritten(10);
        moreData.assertNotWritten();
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.reset(listener);
        resetCtx();
        // Update the window and verify that the rest of data and some of moreData are written
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 15);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Mockito.reset(listener);
        controller.writePendingBytes();
        data.assertFullyWritten();
        moreData.assertPartiallyWritten(5);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - 25), window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(10, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(10, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(10, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
    }

    @Test
    public void initialWindowUpdateShouldSendPayload() throws Http2Exception {
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, ((-(window(Http2CodecUtil.CONNECTION_STREAM_ID))) + 10));
        assertWritabilityChanged(0, true);
        Mockito.reset(listener);
        controller.initialWindowSize(0);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Verify that the entire frame was sent.
        controller.initialWindowSize(10);
        data.assertFullyWritten();
        assertWritabilityChanged(0, false);
    }

    @Test
    public void successiveSendsShouldNotInteract() throws Http2Exception {
        // Collapse the connection window to force queueing.
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, (-(window(Http2CodecUtil.CONNECTION_STREAM_ID))));
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataA = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        // Queue data for stream A and allow most of it to be written.
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, dataA);
        controller.writePendingBytes();
        dataA.assertNotWritten();
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, 8);
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        controller.writePendingBytes();
        dataA.assertPartiallyWritten(8);
        Assert.assertEquals(65527, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        // Queue data for stream B and allow the rest of A and all of B to be written.
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataB = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_B, dataB);
        controller.writePendingBytes();
        dataB.assertNotWritten();
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, 12);
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        controller.writePendingBytes();
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        assertWritabilityChanged(0, false);
        // Verify the rest of A is written.
        dataA.assertFullyWritten();
        Assert.assertEquals(65525, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        dataB.assertFullyWritten();
        Assert.assertEquals(65525, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void negativeWindowShouldNotThrowException() throws Http2Exception {
        final int initWindow = 20;
        final int secondWindowSize = 10;
        controller.initialWindowSize(initWindow);
        assertWritabilityChanged(0, true);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data1 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(initWindow);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data2 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(5);
        // Deplete the stream A window to 0
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data1);
        controller.writePendingBytes();
        data1.assertFullyWritten();
        Assert.assertTrue(((window(Http2CodecUtil.CONNECTION_STREAM_ID)) > 0));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        // Make the window size for stream A negative
        controller.initialWindowSize((initWindow - secondWindowSize));
        Assert.assertEquals((-secondWindowSize), window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        // Queue up a write. It should not be written now because the window is negative
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data2);
        controller.writePendingBytes();
        data2.assertNotWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        // Open the window size back up a bit (no send should happen)
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 5);
        controller.writePendingBytes();
        Assert.assertEquals((-5), window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        data2.assertNotWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        // Open the window size back up a bit (no send should happen)
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 5);
        controller.writePendingBytes();
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        data2.assertNotWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        // Open the window size back up and allow the write to happen
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 5);
        controller.writePendingBytes();
        data2.assertFullyWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
    }

    @Test
    public void initialWindowUpdateShouldSendEmptyFrame() throws Http2Exception {
        controller.initialWindowSize(0);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        // First send a frame that will get buffered.
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10, false);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Now send an empty frame on the same stream and verify that it's also buffered.
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data2 = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(0, false);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data2);
        controller.writePendingBytes();
        data2.assertNotWritten();
        // Re-expand the window and verify that both frames were sent.
        controller.initialWindowSize(10);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        data.assertFullyWritten();
        data2.assertFullyWritten();
    }

    @Test
    public void initialWindowUpdateShouldSendPartialFrame() throws Http2Exception {
        controller.initialWindowSize(0);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Verify that a partial frame of 5 was sent.
        controller.initialWindowSize(5);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        data.assertPartiallyWritten(5);
    }

    @Test
    public void connectionWindowUpdateShouldSendFrame() throws Http2Exception {
        // Set the connection window size to zero.
        exhaustStreamWindow(Http2CodecUtil.CONNECTION_STREAM_ID);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        // Verify that the entire frame was sent.
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, 10);
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        data.assertNotWritten();
        controller.writePendingBytes();
        data.assertFullyWritten();
        assertWritabilityChanged(0, false);
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - 10), window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
    }

    @Test
    public void connectionWindowUpdateShouldSendPartialFrame() throws Http2Exception {
        // Set the connection window size to zero.
        exhaustStreamWindow(Http2CodecUtil.CONNECTION_STREAM_ID);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Verify that a partial frame of 5 was sent.
        incrementWindowSize(Http2CodecUtil.CONNECTION_STREAM_ID, 5);
        data.assertNotWritten();
        assertWritabilityChanged(0, false);
        Mockito.reset(listener);
        controller.writePendingBytes();
        data.assertPartiallyWritten(5);
        assertWritabilityChanged(0, false);
        Assert.assertEquals(0, window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - 5), window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
    }

    @Test
    public void streamWindowUpdateShouldSendFrame() throws Http2Exception {
        // Set the stream window size to zero.
        exhaustStreamWindow(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Verify that the entire frame was sent.
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 10);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        data.assertNotWritten();
        controller.writePendingBytes();
        data.assertFullyWritten();
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - 10), window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
    }

    @Test
    public void streamWindowUpdateShouldSendPartialFrame() throws Http2Exception {
        // Set the stream window size to zero.
        exhaustStreamWindow(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled data = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(10);
        sendData(DefaultHttp2RemoteFlowControllerTest.STREAM_A, data);
        controller.writePendingBytes();
        data.assertNotWritten();
        // Verify that a partial frame of 5 was sent.
        incrementWindowSize(DefaultHttp2RemoteFlowControllerTest.STREAM_A, 5);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
        Mockito.reset(listener);
        data.assertNotWritten();
        controller.writePendingBytes();
        data.assertPartiallyWritten(5);
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) - 5), window(Http2CodecUtil.CONNECTION_STREAM_ID));
        Assert.assertEquals(0, window(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Assert.assertEquals(Http2CodecUtil.DEFAULT_WINDOW_SIZE, window(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
    }

    @Test
    public void flowControlledWriteThrowsAnException() throws Exception {
        final Http2RemoteFlowController.FlowControlled flowControlled = DefaultHttp2RemoteFlowControllerTest.mockedFlowControlledThatThrowsOnWrite();
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                stream.closeLocalSide();
                return null;
            }
        }).when(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        int windowBefore = window(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        controller.addFlowControlled(stream, flowControlled);
        controller.writePendingBytes();
        Mockito.verify(flowControlled, Mockito.atLeastOnce()).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        Mockito.verify(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(flowControlled, Mockito.never()).writeComplete();
        Assert.assertEquals(90, (windowBefore - (window(DefaultHttp2RemoteFlowControllerTest.STREAM_A))));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
    }

    @Test
    public void flowControlledWriteAndErrorThrowAnException() throws Exception {
        final Http2RemoteFlowController.FlowControlled flowControlled = DefaultHttp2RemoteFlowControllerTest.mockedFlowControlledThatThrowsOnWrite();
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        final RuntimeException fakeException = new RuntimeException("error failed");
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                throw fakeException;
            }
        }).when(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        int windowBefore = window(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        try {
            controller.addFlowControlled(stream, flowControlled);
            controller.writePendingBytes();
            Assert.fail();
        } catch (Http2Exception e) {
            Assert.assertSame(fakeException, e.getCause());
        } catch (Throwable t) {
            Assert.fail();
        }
        Mockito.verify(flowControlled, Mockito.atLeastOnce()).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        Mockito.verify(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(flowControlled, Mockito.never()).writeComplete();
        Assert.assertEquals(90, (windowBefore - (window(DefaultHttp2RemoteFlowControllerTest.STREAM_A))));
        Mockito.verifyZeroInteractions(listener);
    }

    @Test
    public void flowControlledWriteCompleteThrowsAnException() throws Exception {
        final Http2RemoteFlowController.FlowControlled flowControlled = Mockito.mock(FlowControlled.class);
        Http2Stream streamA = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        final AtomicInteger size = new AtomicInteger(150);
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return size.get();
            }
        }).when(flowControlled).size();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                size.addAndGet((-50));
                return null;
            }
        }).when(flowControlled).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                throw new RuntimeException("writeComplete failed");
            }
        }).when(flowControlled).writeComplete();
        int windowBefore = window(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        controller.addFlowControlled(stream, flowControlled);
        controller.writePendingBytes();
        Mockito.verify(flowControlled, Mockito.times(3)).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        Mockito.verify(flowControlled, Mockito.never()).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(flowControlled).writeComplete();
        Assert.assertEquals(150, (windowBefore - (window(DefaultHttp2RemoteFlowControllerTest.STREAM_A))));
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(streamA);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(streamA));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
    }

    @Test
    public void closeStreamInFlowControlledError() throws Exception {
        final Http2RemoteFlowController.FlowControlled flowControlled = Mockito.mock(FlowControlled.class);
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        Mockito.when(flowControlled.size()).thenReturn(100);
        Mockito.doThrow(new RuntimeException("write failed")).when(flowControlled).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                stream.close();
                return null;
            }
        }).when(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        controller.addFlowControlled(stream, flowControlled);
        controller.writePendingBytes();
        Mockito.verify(flowControlled).write(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt());
        Mockito.verify(flowControlled).error(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Throwable.class));
        Mockito.verify(flowControlled, Mockito.never()).writeComplete();
        Mockito.verify(listener, Mockito.times(1)).writabilityChanged(stream);
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C));
        Mockito.verify(listener, Mockito.never()).writabilityChanged(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D));
        Assert.assertFalse(controller.isWritable(stream));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_B)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_C)));
        Assert.assertTrue(controller.isWritable(stream(DefaultHttp2RemoteFlowControllerTest.STREAM_D)));
    }

    @Test
    public void nonWritableChannelDoesNotAttemptToWrite() throws Exception {
        // Start the channel as not writable and exercise the public methods of the flow controller
        // making sure no frames are written.
        setChannelWritability(false);
        assertWritabilityChanged(1, false);
        Mockito.reset(listener);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataA = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(1);
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataB = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(1);
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        controller.addFlowControlled(stream, dataA);
        controller.writePendingBytes();
        dataA.assertNotWritten();
        controller.incrementWindowSize(stream, 100);
        controller.writePendingBytes();
        dataA.assertNotWritten();
        controller.addFlowControlled(stream, dataB);
        controller.writePendingBytes();
        dataA.assertNotWritten();
        dataB.assertNotWritten();
        assertWritabilityChanged(0, false);
        // Now change the channel to writable and make sure frames are written.
        setChannelWritability(true);
        assertWritabilityChanged(1, true);
        controller.writePendingBytes();
        dataA.assertFullyWritten();
        dataB.assertFullyWritten();
    }

    @Test
    public void contextShouldSendQueuedFramesWhenSet() throws Exception {
        // Re-initialize the controller so we can ensure the context hasn't been set yet.
        initConnectionAndController();
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataA = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(1);
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        // Queue some frames
        controller.addFlowControlled(stream, dataA);
        dataA.assertNotWritten();
        controller.incrementWindowSize(stream, 100);
        dataA.assertNotWritten();
        assertWritabilityChanged(0, false);
        // Set the controller
        controller.channelHandlerContext(ctx);
        dataA.assertFullyWritten();
        assertWritabilityChanged(1, true);
    }

    @Test
    public void initialWindowSizeWithNoContextShouldNotThrow() throws Exception {
        // Re-initialize the controller so we can ensure the context hasn't been set yet.
        initConnectionAndController();
        // This should not throw.
        controller.initialWindowSize((1024 * 100));
        DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled dataA = new DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled(1);
        final Http2Stream stream = stream(DefaultHttp2RemoteFlowControllerTest.STREAM_A);
        // Queue some frames
        controller.addFlowControlled(stream, dataA);
        dataA.assertNotWritten();
        // Set the controller
        controller.channelHandlerContext(ctx);
        dataA.assertFullyWritten();
    }

    @Test(expected = AssertionError.class)
    public void invalidParentStreamIdThrows() {
        controller.updateDependencyTree(DefaultHttp2RemoteFlowControllerTest.STREAM_D, (-1), Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
    }

    @Test(expected = AssertionError.class)
    public void invalidChildStreamIdThrows() {
        controller.updateDependencyTree((-1), DefaultHttp2RemoteFlowControllerTest.STREAM_D, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
    }

    @Test(expected = AssertionError.class)
    public void connectionChildStreamIdThrows() {
        controller.updateDependencyTree(0, DefaultHttp2RemoteFlowControllerTest.STREAM_D, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
    }

    @Test(expected = AssertionError.class)
    public void invalidWeightTooSmallThrows() {
        controller.updateDependencyTree(DefaultHttp2RemoteFlowControllerTest.STREAM_A, DefaultHttp2RemoteFlowControllerTest.STREAM_D, ((short) ((Http2CodecUtil.MIN_WEIGHT) - 1)), true);
    }

    @Test(expected = AssertionError.class)
    public void invalidWeightTooBigThrows() {
        controller.updateDependencyTree(DefaultHttp2RemoteFlowControllerTest.STREAM_A, DefaultHttp2RemoteFlowControllerTest.STREAM_D, ((short) ((Http2CodecUtil.MAX_WEIGHT) + 1)), true);
    }

    @Test(expected = AssertionError.class)
    public void dependencyOnSelfThrows() {
        controller.updateDependencyTree(DefaultHttp2RemoteFlowControllerTest.STREAM_A, DefaultHttp2RemoteFlowControllerTest.STREAM_A, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, true);
    }

    private static final class FakeFlowControlled implements Http2RemoteFlowController.FlowControlled {
        private int currentPadding;

        private int currentPayloadSize;

        private int originalPayloadSize;

        private int originalPadding;

        private boolean writeCalled;

        private final boolean mergeable;

        private boolean merged;

        private Throwable t;

        private FakeFlowControlled(int size) {
            this(size, false);
        }

        private FakeFlowControlled(int size, boolean mergeable) {
            this(size, 0, mergeable);
        }

        private FakeFlowControlled(int payloadSize, int padding, boolean mergeable) {
            currentPayloadSize = originalPayloadSize = payloadSize;
            currentPadding = originalPadding = padding;
            this.mergeable = mergeable;
        }

        @Override
        public int size() {
            return (currentPayloadSize) + (currentPadding);
        }

        private int originalSize() {
            return (originalPayloadSize) + (originalPadding);
        }

        @Override
        public void error(ChannelHandlerContext ctx, Throwable t) {
            this.t = t;
        }

        @Override
        public void writeComplete() {
        }

        @Override
        public void write(ChannelHandlerContext ctx, int allowedBytes) {
            if ((allowedBytes <= 0) && ((size()) != 0)) {
                // Write has been called but no data can be written
                return;
            }
            writeCalled = true;
            int written = Math.min(size(), allowedBytes);
            if (written > (currentPayloadSize)) {
                written -= currentPayloadSize;
                currentPayloadSize = 0;
                currentPadding -= written;
            } else {
                currentPayloadSize -= written;
            }
        }

        @Override
        public boolean merge(ChannelHandlerContext ctx, Http2RemoteFlowController.FlowControlled next) {
            if ((mergeable) && (next instanceof DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled)) {
                DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled ffcNext = ((DefaultHttp2RemoteFlowControllerTest.FakeFlowControlled) (next));
                originalPayloadSize += ffcNext.originalPayloadSize;
                currentPayloadSize += ffcNext.originalPayloadSize;
                currentPadding = originalPadding = Math.max(originalPadding, ffcNext.originalPadding);
                ffcNext.merged = true;
                return true;
            }
            return false;
        }

        public int written() {
            return (originalSize()) - (size());
        }

        public void assertNotWritten() {
            Assert.assertFalse(writeCalled);
        }

        public void assertPartiallyWritten(int expectedWritten) {
            assertPartiallyWritten(expectedWritten, 0);
        }

        public void assertPartiallyWritten(int expectedWritten, int delta) {
            Assert.assertTrue(writeCalled);
            Assert.assertEquals(expectedWritten, written(), delta);
        }

        public void assertFullyWritten() {
            Assert.assertTrue(writeCalled);
            Assert.assertEquals(0, currentPayloadSize);
            Assert.assertEquals(0, currentPadding);
        }

        public boolean assertMerged() {
            return merged;
        }

        public void assertError(Http2Error error) {
            Assert.assertNotNull(t);
            if (error != null) {
                Assert.assertSame(error, ((Http2Exception) (t)).error());
            }
        }
    }
}

