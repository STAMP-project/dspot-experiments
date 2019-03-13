/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.netty;


import Http2CodecUtil.DEFAULT_WINDOW_SIZE;
import io.grpc.InternalChannelz.TransportStats;
import io.grpc.internal.FakeClock;
import io.grpc.internal.TransportTracer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Base class for Netty handler unit tests.
 */
@RunWith(JUnit4.class)
public abstract class NettyHandlerTestBase<T extends Http2ConnectionHandler> {
    private ByteBuf content;

    private EmbeddedChannel channel;

    private ChannelHandlerContext ctx;

    private Http2FrameWriter frameWriter;

    private Http2FrameReader frameReader;

    private T handler;

    private WriteQueue writeQueue;

    protected final TransportTracer transportTracer = new TransportTracer();

    protected int flowControlWindow = Http2CodecUtil.DEFAULT_WINDOW_SIZE;

    private final FakeClock fakeClock = new FakeClock();

    private final class FakeClockSupportedChanel extends EmbeddedChannel {
        EventLoop eventLoop;

        FakeClockSupportedChanel(ChannelHandler... handlers) {
            super(handlers);
        }

        @Override
        public EventLoop eventLoop() {
            if ((eventLoop) == null) {
                createEventLoop();
            }
            return eventLoop;
        }

        void createEventLoop() {
            EventLoop realEventLoop = super.eventLoop();
            if (realEventLoop == null) {
                return;
            }
            eventLoop = Mockito.mock(EventLoop.class, AdditionalAnswers.delegatesTo(realEventLoop));
            Mockito.doAnswer(new Answer<ScheduledFuture<Void>>() {
                @Override
                public ScheduledFuture<Void> answer(InvocationOnMock invocation) throws Throwable {
                    Runnable command = ((Runnable) (invocation.getArguments()[0]));
                    Long delay = ((Long) (invocation.getArguments()[1]));
                    TimeUnit timeUnit = ((TimeUnit) (invocation.getArguments()[2]));
                    return new FakeClockScheduledNettyFuture(eventLoop, command, delay, timeUnit);
                }
            }).when(eventLoop).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        }
    }

    private final class FakeClockScheduledNettyFuture extends DefaultPromise<Void> implements ScheduledFuture<Void> {
        final java.util.concurrent.ScheduledFuture<?> future;

        FakeClockScheduledNettyFuture(EventLoop eventLoop, final Runnable command, long delay, TimeUnit timeUnit) {
            super(eventLoop);
            Runnable wrap = new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    } catch (Throwable t) {
                        setFailure(t);
                        return;
                    }
                    if (!(isDone())) {
                        Promise<Void> unused = setSuccess(null);
                    }
                    // else: The command itself, such as a shutdown task, might have cancelled all the
                    // scheduled tasks already.
                }
            };
            future = fakeClock.getScheduledExecutorService().schedule(wrap, delay, timeUnit);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (future.cancel(mayInterruptIfRunning)) {
                return super.cancel(mayInterruptIfRunning);
            }
            return false;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return Math.max(future.getDelay(unit), 1L);// never return zero or negative delay.

        }

        @Override
        public int compareTo(Delayed o) {
            return future.compareTo(o);
        }
    }

    @Test
    public void dataPingSentOnHeaderRecieved() throws Exception {
        manualSetUp();
        makeStream();
        AbstractNettyHandler handler = ((AbstractNettyHandler) (handler()));
        handler.setAutoTuneFlowControl(true);
        channelRead(dataFrame(3, false, content()));
        Assert.assertEquals(1, handler.flowControlPing().getPingCount());
    }

    @Test
    public void dataPingAckIsRecognized() throws Exception {
        manualSetUp();
        makeStream();
        AbstractNettyHandler handler = ((AbstractNettyHandler) (handler()));
        handler.setAutoTuneFlowControl(true);
        channelRead(dataFrame(3, false, content()));
        long pingData = handler.flowControlPing().payload();
        channelRead(pingFrame(true, pingData));
        Assert.assertEquals(1, handler.flowControlPing().getPingCount());
        Assert.assertEquals(1, handler.flowControlPing().getPingReturn());
    }

    @Test
    public void dataSizeSincePingAccumulates() throws Exception {
        manualSetUp();
        makeStream();
        AbstractNettyHandler handler = ((AbstractNettyHandler) (handler()));
        handler.setAutoTuneFlowControl(true);
        long frameData = 123456;
        ByteBuf buff = ctx().alloc().buffer(16);
        buff.writeLong(frameData);
        int length = buff.readableBytes();
        channelRead(dataFrame(3, false, buff.copy()));
        channelRead(dataFrame(3, false, buff.copy()));
        channelRead(dataFrame(3, false, buff.copy()));
        Assert.assertEquals((length * 3), handler.flowControlPing().getDataSincePing());
    }

    @Test
    public void windowUpdateMatchesTarget() throws Exception {
        manualSetUp();
        Http2Stream connectionStream = connection().connectionStream();
        Http2LocalFlowController localFlowController = connection().local().flowController();
        makeStream();
        AbstractNettyHandler handler = ((AbstractNettyHandler) (handler()));
        handler.setAutoTuneFlowControl(true);
        ByteBuf data = ctx().alloc().buffer(1024);
        while (data.isWritable()) {
            data.writeLong(1111);
        } 
        int length = data.readableBytes();
        ByteBuf frame = dataFrame(3, false, data.copy());
        channelRead(frame);
        int accumulator = length;
        // 40 is arbitrary, any number large enough to trigger a window update would work
        for (int i = 0; i < 40; i++) {
            channelRead(dataFrame(3, false, data.copy()));
            accumulator += length;
        }
        long pingData = handler.flowControlPing().payload();
        channelRead(pingFrame(true, pingData));
        Assert.assertEquals(accumulator, handler.flowControlPing().getDataSincePing());
        Assert.assertEquals((2 * accumulator), localFlowController.initialWindowSize(connectionStream));
    }

    @Test
    public void windowShouldNotExceedMaxWindowSize() throws Exception {
        manualSetUp();
        makeStream();
        AbstractNettyHandler handler = ((AbstractNettyHandler) (handler()));
        handler.setAutoTuneFlowControl(true);
        Http2Stream connectionStream = connection().connectionStream();
        Http2LocalFlowController localFlowController = connection().local().flowController();
        int maxWindow = handler.flowControlPing().maxWindow();
        handler.flowControlPing().setDataSizeSincePing(maxWindow);
        long payload = handler.flowControlPing().payload();
        channelRead(pingFrame(true, payload));
        Assert.assertEquals(maxWindow, localFlowController.initialWindowSize(connectionStream));
    }

    @Test
    public void transportTracer_windowSizeDefault() throws Exception {
        manualSetUp();
        TransportStats transportStats = transportTracer.getStats();
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, transportStats.remoteFlowControlWindow);
        Assert.assertEquals(flowControlWindow, transportStats.localFlowControlWindow);
    }

    @Test
    public void transportTracer_windowSize() throws Exception {
        flowControlWindow = 1024 * 1024;
        manualSetUp();
        TransportStats transportStats = transportTracer.getStats();
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, transportStats.remoteFlowControlWindow);
        Assert.assertEquals(flowControlWindow, transportStats.localFlowControlWindow);
    }

    @Test
    public void transportTracer_windowUpdate_remote() throws Exception {
        manualSetUp();
        TransportStats before = transportTracer.getStats();
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, before.remoteFlowControlWindow);
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, before.localFlowControlWindow);
        ByteBuf serializedSettings = windowUpdate(0, 1000);
        channelRead(serializedSettings);
        TransportStats after = transportTracer.getStats();
        Assert.assertEquals(((Http2CodecUtil.DEFAULT_WINDOW_SIZE) + 1000), after.remoteFlowControlWindow);
        Assert.assertEquals(flowControlWindow, after.localFlowControlWindow);
    }

    @Test
    public void transportTracer_windowUpdate_local() throws Exception {
        manualSetUp();
        TransportStats before = transportTracer.getStats();
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, before.remoteFlowControlWindow);
        Assert.assertEquals(flowControlWindow, before.localFlowControlWindow);
        // If the window size is below a certain threshold, netty will wait to apply the update.
        // Use a large increment to be sure that it exceeds the threshold.
        connection().local().flowController().incrementWindowSize(connection().connectionStream(), (8 * (Http2CodecUtil.DEFAULT_WINDOW_SIZE)));
        TransportStats after = transportTracer.getStats();
        Assert.assertEquals(DEFAULT_WINDOW_SIZE, after.remoteFlowControlWindow);
        Assert.assertEquals(((flowControlWindow) + (8 * (Http2CodecUtil.DEFAULT_WINDOW_SIZE))), connection().local().flowController().windowSize(connection().connectionStream()));
    }
}

