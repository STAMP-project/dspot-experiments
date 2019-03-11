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


import EmptyHttp2Headers.INSTANCE;
import Http2CodecUtil.MAX_HEADER_LIST_SIZE;
import Http2Error.CANCEL;
import Http2Error.INTERNAL_ERROR;
import Http2Error.REFUSED_STREAM;
import Http2Exception.StreamException;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests the full HTTP/2 framing stack including the connection and preface handlers.
 */
public class Http2ConnectionRoundtripTest {
    private static final long DEFAULT_AWAIT_TIMEOUT_SECONDS = 15;

    @Mock
    private Http2FrameListener clientListener;

    @Mock
    private Http2FrameListener serverListener;

    private Http2ConnectionHandler http2Client;

    private Http2ConnectionHandler http2Server;

    private ServerBootstrap sb;

    private Bootstrap cb;

    private Channel serverChannel;

    private volatile Channel serverConnectedChannel;

    private Channel clientChannel;

    private Http2TestUtil.FrameCountDown serverFrameCountDown;

    private CountDownLatch requestLatch;

    private CountDownLatch serverSettingsAckLatch;

    private CountDownLatch dataLatch;

    private CountDownLatch trailersLatch;

    private CountDownLatch goAwayLatch;

    @Test
    public void inflightFrameAfterStreamResetShouldNotMakeConnectionUnusable() throws Exception {
        bootstrapEnv(1, 1, 2, 1);
        final CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                ChannelHandlerContext ctx = invocationOnMock.getArgument(0);
                http2Server.encoder().writeHeaders(ctx, ((Integer) (invocationOnMock.getArgument(1))), ((Http2Headers) (invocationOnMock.getArgument(2))), 0, false, ctx.newPromise());
                http2Server.flush(ctx);
                return null;
            }
        }).when(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                latch.countDown();
                return null;
            }
        }).when(clientListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(5), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // Create a single stream by sending a HEADERS frame to the server.
        final short weight = 16;
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, weight, false, 0, false, newPromise());
                http2Client.flush(ctx());
                http2Client.encoder().writeRstStream(ctx(), 3, INTERNAL_ERROR.code(), newPromise());
                http2Client.flush(ctx());
            }
        });
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 5, headers, 0, weight, false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(latch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    public void headersWithEndStreamShouldNotSendError() throws Exception {
        bootstrapEnv(1, 1, 2, 1);
        // Create a single stream by sending a HEADERS frame to the server.
        final short weight = 16;
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, weight, false, 0, true, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(weight), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        // Wait for some time to see if a go_away or reset frame will be received.
        Thread.sleep(1000);
        // Verify that no errors have been received.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(serverListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        // The server will not respond, and so don't wait for graceful shutdown
        setClientGracefulShutdownTime(0);
    }

    @Test
    public void encodeViolatesMaxHeaderListSizeCanStillUseConnection() throws Exception {
        bootstrapEnv(1, 2, 1, 0, 0);
        final CountDownLatch serverSettingsAckLatch1 = new CountDownLatch(2);
        final CountDownLatch serverSettingsAckLatch2 = new CountDownLatch(3);
        final CountDownLatch clientSettingsLatch1 = new CountDownLatch(3);
        final CountDownLatch serverRevHeadersLatch = new CountDownLatch(1);
        final CountDownLatch clientHeadersLatch = new CountDownLatch(1);
        final CountDownLatch clientDataWrite = new CountDownLatch(1);
        final AtomicReference<Throwable> clientHeadersWriteException = new AtomicReference<Throwable>();
        final AtomicReference<Throwable> clientHeadersWriteException2 = new AtomicReference<Throwable>();
        final AtomicReference<Throwable> clientDataWriteException = new AtomicReference<Throwable>();
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                serverSettingsAckLatch1.countDown();
                serverSettingsAckLatch2.countDown();
                return null;
            }
        }).when(serverListener).onSettingsAckRead(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                clientSettingsLatch1.countDown();
                return null;
            }
        }).when(clientListener).onSettingsRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Http2Settings.class));
        // Manually add a listener for when we receive the expected headers on the server.
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                serverRevHeadersLatch.countDown();
                return null;
            }
        }).when(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(5), ArgumentMatchers.eq(headers), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        // Set the maxHeaderListSize to 100 so we may be able to write some headers, but not all. We want to verify
        // that we don't corrupt state if some can be written but not all.
        Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeSettings(serverCtx(), new Http2Settings().copyFrom(http2Server.decoder().localSettings()).maxHeaderListSize(100), serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch1.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, false, newPromise()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        clientHeadersWriteException.set(future.cause());
                    }
                });
                // It is expected that this write should fail locally and the remote peer will never see this.
                http2Client.encoder().writeData(ctx(), 3, Unpooled.buffer(), 0, true, newPromise()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        clientDataWriteException.set(future.cause());
                        clientDataWrite.countDown();
                    }
                });
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(clientDataWrite.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertNotNull("Header encode should have exceeded maxHeaderListSize!", clientHeadersWriteException.get());
        Assert.assertNotNull("Data on closed stream should fail!", clientDataWriteException.get());
        // Set the maxHeaderListSize to the max value so we can send the headers.
        Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeSettings(serverCtx(), new Http2Settings().copyFrom(http2Server.decoder().localSettings()).maxHeaderListSize(MAX_HEADER_LIST_SIZE), serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        Assert.assertTrue(clientSettingsLatch1.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(serverSettingsAckLatch2.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 5, headers, 0, true, newPromise()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        clientHeadersWriteException2.set(future.cause());
                        clientHeadersLatch.countDown();
                    }
                });
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(clientHeadersLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertNull("Client write of headers should succeed with increased header list size!", clientHeadersWriteException2.get());
        Assert.assertTrue(serverRevHeadersLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener, Mockito.never()).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // Verify that no errors have been received.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(serverListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(clientListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void testSettingsAckIsSentBeforeUsingFlowControl() throws Exception {
        bootstrapEnv(1, 1, 1, 1);
        final CountDownLatch serverSettingsAckLatch1 = new CountDownLatch(1);
        final CountDownLatch serverSettingsAckLatch2 = new CountDownLatch(2);
        final CountDownLatch serverDataLatch = new CountDownLatch(1);
        final CountDownLatch clientWriteDataLatch = new CountDownLatch(1);
        final byte[] data = new byte[]{ 1, 2, 3, 4, 5 };
        final ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                serverSettingsAckLatch1.countDown();
                serverSettingsAckLatch2.countDown();
                return null;
            }
        }).when(serverListener).onSettingsAckRead(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock in) throws Throwable {
                ByteBuf buf = ((ByteBuf) (in.getArguments()[2]));
                int padding = ((Integer) (in.getArguments()[3]));
                int processedBytes = (buf.readableBytes()) + padding;
                buf.readBytes(out, buf.readableBytes());
                serverDataLatch.countDown();
                return processedBytes;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.anyBoolean());
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        // The server initially reduces the connection flow control window to 0.
        Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeSettings(serverCtx(), new Http2Settings().copyFrom(http2Server.decoder().localSettings()).initialWindowSize(0), serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch1.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // The client should now attempt to send data, but the window size is 0 so it will be queued in the flow
        // controller.
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.encoder().writeData(ctx(), 3, Unpooled.wrappedBuffer(data), 0, true, newPromise());
                http2Client.flush(ctx());
                clientWriteDataLatch.countDown();
            }
        });
        Assert.assertTrue(clientWriteDataLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Now the server opens up the connection window to allow the client to send the pending data.
        Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeSettings(serverCtx(), new Http2Settings().copyFrom(http2Server.decoder().localSettings()).initialWindowSize(data.length), serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch2.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(serverDataLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertArrayEquals(data, out.toByteArray());
        // Verify that no errors have been received.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(serverListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(clientListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void priorityUsingHigherValuedStreamIdDoesNotPreventUsingLowerStreamId() throws Exception {
        bootstrapEnv(1, 1, 2, 0);
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writePriority(ctx(), 5, 3, ((short) (14)), false, newPromise());
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener).onPriorityRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(5), ArgumentMatchers.eq(3), ArgumentMatchers.eq(((short) (14))), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        // Verify that no errors have been received.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(serverListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(clientListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void headersUsingHigherValuedStreamIdPreventsUsingLowerStreamId() throws Exception {
        bootstrapEnv(1, 1, 1, 0);
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 5, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.encoder().frameWriter().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(5), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener, Mockito.never()).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(Http2Headers.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // Client should receive a RST_STREAM for stream 3, but there is not Http2Stream object so the listener is never
        // notified.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(serverListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
        Mockito.verify(clientListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void headersWriteForPeerStreamWhichWasResetShouldNotGoAway() throws Exception {
        bootstrapEnv(1, 1, 1, 0);
        final CountDownLatch serverGotRstLatch = new CountDownLatch(1);
        final CountDownLatch serverWriteHeadersLatch = new CountDownLatch(1);
        final AtomicReference<Throwable> serverWriteHeadersCauseRef = new AtomicReference<Throwable>();
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        final int streamId = 3;
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), streamId, headers, Http2CodecUtil.CONNECTION_STREAM_ID, Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT, false, 0, false, newPromise());
                http2Client.encoder().writeRstStream(ctx(), streamId, CANCEL.code(), newPromise());
                http2Client.flush(ctx());
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (streamId == ((Integer) (invocationOnMock.getArgument(1)))) {
                    serverGotRstLatch.countDown();
                }
                return null;
            }
        }).when(serverListener).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(streamId), ArgumentMatchers.anyLong());
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(serverGotRstLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(streamId), ArgumentMatchers.eq(headers), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        // Now have the server attempt to send a headers frame simulating some asynchronous work.
        Http2TestUtil.runInChannel(serverConnectedChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeHeaders(serverCtx(), streamId, headers, 0, true, serverNewPromise()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        serverWriteHeadersCauseRef.set(future.cause());
                        serverWriteHeadersLatch.countDown();
                    }
                });
                http2Server.flush(serverCtx());
            }
        });
        Assert.assertTrue(serverWriteHeadersLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Throwable serverWriteHeadersCause = serverWriteHeadersCauseRef.get();
        Assert.assertNotNull(serverWriteHeadersCause);
        MatcherAssert.assertThat(serverWriteHeadersCauseRef.get(), CoreMatchers.not(CoreMatchers.instanceOf(Http2Exception.class)));
        // Server should receive a RST_STREAM for stream 3.
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.verify(clientListener, Mockito.never()).onRstStreamRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong());
    }

    @Test
    public void http2ExceptionInPipelineShouldCloseConnection() throws Exception {
        bootstrapEnv(1, 1, 2, 1);
        // Create a latch to track when the close occurs.
        final CountDownLatch closeLatch = new CountDownLatch(1);
        clientChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                closeLatch.countDown();
            }
        });
        // Create a single stream by sending a HEADERS frame to the server.
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        // Wait for the server to create the stream.
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Add a handler that will immediately throw an exception.
        clientChannel.pipeline().addFirst(new ChannelHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Fake Exception");
            }
        });
        // Wait for the close to occur.
        Assert.assertTrue(closeLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertFalse(clientChannel.isOpen());
    }

    @Test
    public void listenerExceptionShouldCloseConnection() throws Exception {
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Mockito.doThrow(new RuntimeException("Fake Exception")).when(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        bootstrapEnv(1, 0, 1, 1);
        // Create a latch to track when the close occurs.
        final CountDownLatch closeLatch = new CountDownLatch(1);
        clientChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                closeLatch.countDown();
            }
        });
        // Create a single stream by sending a HEADERS frame to the server.
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        // Wait for the server to create the stream.
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Wait for the close to occur.
        Assert.assertTrue(closeLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertFalse(clientChannel.isOpen());
    }

    private enum WriteEmptyBufferMode {

        SINGLE_END_OF_STREAM,
        SECOND_END_OF_STREAM,
        SINGLE_WITH_TRAILERS,
        SECOND_WITH_TRAILERS;}

    @Test
    public void writeOfEmptyReleasedBufferSingleBufferQueuedInFlowControllerShouldFail() throws Exception {
        writeOfEmptyReleasedBufferQueuedInFlowControllerShouldFail(Http2ConnectionRoundtripTest.WriteEmptyBufferMode.SINGLE_END_OF_STREAM);
    }

    @Test
    public void writeOfEmptyReleasedBufferSingleBufferTrailersQueuedInFlowControllerShouldFail() throws Exception {
        writeOfEmptyReleasedBufferQueuedInFlowControllerShouldFail(Http2ConnectionRoundtripTest.WriteEmptyBufferMode.SINGLE_WITH_TRAILERS);
    }

    @Test
    public void writeOfEmptyReleasedBufferMultipleBuffersQueuedInFlowControllerShouldFail() throws Exception {
        writeOfEmptyReleasedBufferQueuedInFlowControllerShouldFail(Http2ConnectionRoundtripTest.WriteEmptyBufferMode.SECOND_END_OF_STREAM);
    }

    @Test
    public void writeOfEmptyReleasedBufferMultipleBuffersTrailersQueuedInFlowControllerShouldFail() throws Exception {
        writeOfEmptyReleasedBufferQueuedInFlowControllerShouldFail(Http2ConnectionRoundtripTest.WriteEmptyBufferMode.SECOND_WITH_TRAILERS);
    }

    @Test
    public void writeFailureFlowControllerRemoveFrame() throws Exception {
        bootstrapEnv(1, 1, 2, 1);
        final ChannelPromise dataPromise = newPromise();
        final ChannelPromise assertPromise = newPromise();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, INSTANCE, 0, ((short) (16)), false, 0, false, newPromise());
                clientChannel.pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        ReferenceCountUtil.release(msg);
                        // Ensure we update the window size so we will try to write the rest of the frame while
                        // processing the flush.
                        http2Client.encoder().flowController().initialWindowSize(8);
                        promise.setFailure(new IllegalStateException());
                    }
                });
                http2Client.encoder().flowController().initialWindowSize(4);
                http2Client.encoder().writeData(ctx(), 3, Http2ConnectionRoundtripTest.randomBytes(8), 0, false, dataPromise);
                Assert.assertTrue(http2Client.encoder().flowController().hasFlowControlled(http2Client.connection().stream(3)));
                http2Client.flush(ctx());
                try {
                    // The Frame should have been removed after the write failed.
                    Assert.assertFalse(http2Client.encoder().flowController().hasFlowControlled(http2Client.connection().stream(3)));
                    assertPromise.setSuccess();
                } catch (Throwable error) {
                    assertPromise.setFailure(error);
                }
            }
        });
        try {
            dataPromise.get();
            Assert.fail();
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), Matchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));
        }
        assertPromise.sync();
    }

    @Test
    public void nonHttp2ExceptionInPipelineShouldNotCloseConnection() throws Exception {
        bootstrapEnv(1, 1, 2, 1);
        // Create a latch to track when the close occurs.
        final CountDownLatch closeLatch = new CountDownLatch(1);
        clientChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                closeLatch.countDown();
            }
        });
        // Create a single stream by sending a HEADERS frame to the server.
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        // Wait for the server to create the stream.
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Add a handler that will immediately throw an exception.
        clientChannel.pipeline().addFirst(new ChannelHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                throw new RuntimeException("Fake Exception");
            }
        });
        // The close should NOT occur.
        Assert.assertFalse(closeLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(clientChannel.isOpen());
        // Set the timeout very low because we know graceful shutdown won't complete
        setClientGracefulShutdownTime(0);
    }

    @Test
    public void noMoreStreamIdsShouldSendGoAway() throws Exception {
        bootstrapEnv(1, 1, 3, 1, 1);
        // Don't wait for the server to close streams
        setClientGracefulShutdownTime(0);
        // Create a single stream by sending a HEADERS frame to the server.
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, true, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), ((Integer.MAX_VALUE) + 1), headers, 0, ((short) (16)), false, 0, true, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(goAwayLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(serverListener).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2Error.PROTOCOL_ERROR.code()), ArgumentMatchers.any(ByteBuf.class));
    }

    @Test
    public void createStreamAfterReceiveGoAwayShouldNotSendGoAway() throws Exception {
        bootstrapEnv(1, 1, 2, 1, 1);
        // We want both sides to do graceful shutdown during the test.
        setClientGracefulShutdownTime(10000);
        setServerGracefulShutdownTime(10000);
        final CountDownLatch clientGoAwayLatch = new CountDownLatch(1);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                clientGoAwayLatch.countDown();
                return null;
            }
        }).when(clientListener).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        // Create a single stream by sending a HEADERS frame to the server.
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Server has received the headers, so the stream is open
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Http2TestUtil.runInChannel(serverChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeGoAway(serverCtx(), 3, Http2Error.NO_ERROR.code(), EMPTY_BUFFER, serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        // wait for the client to receive the GO_AWAY.
        Assert.assertTrue(clientGoAwayLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Mockito.verify(clientListener).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(Http2Error.NO_ERROR.code()), ArgumentMatchers.any(ByteBuf.class));
        final AtomicReference<ChannelFuture> clientWriteAfterGoAwayFutureRef = new AtomicReference<ChannelFuture>();
        final CountDownLatch clientWriteAfterGoAwayLatch = new CountDownLatch(1);
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                ChannelFuture f = http2Client.encoder().writeHeaders(ctx(), 5, headers, 0, ((short) (16)), false, 0, true, newPromise());
                clientWriteAfterGoAwayFutureRef.set(f);
                http2Client.flush(ctx());
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        clientWriteAfterGoAwayLatch.countDown();
                    }
                });
            }
        });
        // Wait for the client's write operation to complete.
        Assert.assertTrue(clientWriteAfterGoAwayLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        ChannelFuture clientWriteAfterGoAwayFuture = clientWriteAfterGoAwayFutureRef.get();
        Assert.assertNotNull(clientWriteAfterGoAwayFuture);
        Throwable clientCause = clientWriteAfterGoAwayFuture.cause();
        MatcherAssert.assertThat(clientCause, Matchers.is(CoreMatchers.instanceOf(StreamException.class)));
        Assert.assertEquals(REFUSED_STREAM.code(), error().code());
        // Wait for the server to receive a GO_AWAY, but this is expected to timeout!
        Assert.assertFalse(goAwayLatch.await(1, TimeUnit.SECONDS));
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        // Shutdown shouldn't wait for the server to close streams
        setClientGracefulShutdownTime(0);
        setServerGracefulShutdownTime(0);
    }

    @Test
    public void createStreamSynchronouslyAfterGoAwayReceivedShouldFailLocally() throws Exception {
        bootstrapEnv(1, 1, 2, 1, 1);
        final CountDownLatch clientGoAwayLatch = new CountDownLatch(1);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                clientGoAwayLatch.countDown();
                return null;
            }
        }).when(clientListener).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        // We want both sides to do graceful shutdown during the test.
        setClientGracefulShutdownTime(10000);
        setServerGracefulShutdownTime(10000);
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        final AtomicReference<ChannelFuture> clientWriteAfterGoAwayFutureRef = new AtomicReference<ChannelFuture>();
        final CountDownLatch clientWriteAfterGoAwayLatch = new CountDownLatch(1);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                ChannelFuture f = http2Client.encoder().writeHeaders(ctx(), 5, headers, 0, ((short) (16)), false, 0, true, newPromise());
                clientWriteAfterGoAwayFutureRef.set(f);
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        clientWriteAfterGoAwayLatch.countDown();
                    }
                });
                http2Client.flush(ctx());
                return null;
            }
        }).when(clientListener).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, true, newPromise());
                http2Client.flush(ctx());
            }
        });
        Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // Server has received the headers, so the stream is open
        Assert.assertTrue(requestLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Http2TestUtil.runInChannel(serverChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                http2Server.encoder().writeGoAway(serverCtx(), 3, Http2Error.NO_ERROR.code(), EMPTY_BUFFER, serverNewPromise());
                http2Server.flush(serverCtx());
            }
        });
        // Wait for the client's write operation to complete.
        Assert.assertTrue(clientWriteAfterGoAwayLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        ChannelFuture clientWriteAfterGoAwayFuture = clientWriteAfterGoAwayFutureRef.get();
        Assert.assertNotNull(clientWriteAfterGoAwayFuture);
        Throwable clientCause = clientWriteAfterGoAwayFuture.cause();
        MatcherAssert.assertThat(clientCause, Matchers.is(CoreMatchers.instanceOf(StreamException.class)));
        Assert.assertEquals(REFUSED_STREAM.code(), error().code());
        // Wait for the server to receive a GO_AWAY, but this is expected to timeout!
        Assert.assertFalse(goAwayLatch.await(1, TimeUnit.SECONDS));
        Mockito.verify(serverListener, Mockito.never()).onGoAwayRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        // Shutdown shouldn't wait for the server to close streams
        setClientGracefulShutdownTime(0);
        setServerGracefulShutdownTime(0);
    }

    @Test
    public void flowControlProperlyChunksLargeMessage() throws Exception {
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        // Create a large message to send.
        final int length = 10485760;// 10MB

        // Create a buffer filled with random bytes.
        final ByteBuf data = Http2ConnectionRoundtripTest.randomBytes(length);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock in) throws Throwable {
                ByteBuf buf = ((ByteBuf) (in.getArguments()[2]));
                int padding = ((Integer) (in.getArguments()[3]));
                int processedBytes = (buf.readableBytes()) + padding;
                buf.readBytes(out, buf.readableBytes());
                return processedBytes;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.anyBoolean());
        try {
            // Initialize the data latch based on the number of bytes expected.
            bootstrapEnv(length, 1, 2, 1);
            // Create the stream and send all of the data at once.
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, false, newPromise());
                    http2Client.encoder().writeData(ctx(), 3, data.retainedDuplicate(), 0, false, newPromise());
                    // Write trailers.
                    http2Client.encoder().writeHeaders(ctx(), 3, headers, 0, ((short) (16)), false, 0, true, newPromise());
                    http2Client.flush(ctx());
                }
            });
            // Wait for the trailers to be received.
            Assert.assertTrue(serverSettingsAckLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            Assert.assertTrue(trailersLatch.await(Http2ConnectionRoundtripTest.DEFAULT_AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            // Verify that headers and trailers were received.
            Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
            Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
            // Verify we received all the bytes.
            Assert.assertEquals(0, dataLatch.getCount());
            out.flush();
            byte[] received = out.toByteArray();
            Assert.assertArrayEquals(data.array(), received);
        } finally {
            // Don't wait for server to close streams
            setClientGracefulShutdownTime(0);
            data.release();
            out.close();
        }
    }

    @Test
    public void stressTest() throws Exception {
        final Http2Headers headers = Http2ConnectionRoundtripTest.dummyHeaders();
        int length = 10;
        final ByteBuf data = Http2ConnectionRoundtripTest.randomBytes(length);
        final String dataAsHex = ByteBufUtil.hexDump(data);
        final long pingData = 8;
        final int numStreams = 2000;
        // Collect all the ping buffers as we receive them at the server.
        final long[] receivedPings = new long[numStreams];
        Mockito.doAnswer(new Answer<Void>() {
            int nextIndex;

            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                receivedPings[((nextIndex)++)] = ((Long) (in.getArguments()[1]));
                return null;
            }
        }).when(serverListener).onPingRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(Long.class));
        // Collect all the data buffers as we receive them at the server.
        final StringBuilder[] receivedData = new StringBuilder[numStreams];
        Mockito.doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock in) throws Throwable {
                int streamId = ((Integer) (in.getArguments()[1]));
                ByteBuf buf = ((ByteBuf) (in.getArguments()[2]));
                int padding = ((Integer) (in.getArguments()[3]));
                int processedBytes = (buf.readableBytes()) + padding;
                int streamIndex = (streamId - 3) / 2;
                StringBuilder builder = receivedData[streamIndex];
                if (builder == null) {
                    builder = new StringBuilder(dataAsHex.length());
                    receivedData[streamIndex] = builder;
                }
                builder.append(ByteBufUtil.hexDump(buf));
                return processedBytes;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        try {
            bootstrapEnv((numStreams * length), 1, (numStreams * 4), numStreams);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    int upperLimit = 3 + (2 * numStreams);
                    for (int streamId = 3; streamId < upperLimit; streamId += 2) {
                        // Send a bunch of data on each stream.
                        http2Client.encoder().writeHeaders(ctx(), streamId, headers, 0, ((short) (16)), false, 0, false, newPromise());
                        http2Client.encoder().writePing(ctx(), false, pingData, newPromise());
                        http2Client.encoder().writeData(ctx(), streamId, data.retainedSlice(), 0, false, newPromise());
                        // Write trailers.
                        http2Client.encoder().writeHeaders(ctx(), streamId, headers, 0, ((short) (16)), false, 0, true, newPromise());
                        http2Client.flush(ctx());
                    }
                }
            });
            // Wait for all frames to be received.
            Assert.assertTrue(serverSettingsAckLatch.await(60, TimeUnit.SECONDS));
            Assert.assertTrue(trailersLatch.await(60, TimeUnit.SECONDS));
            Mockito.verify(serverListener, Mockito.times(numStreams)).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
            Mockito.verify(serverListener, Mockito.times(numStreams)).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((short) (16))), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
            Mockito.verify(serverListener, Mockito.times(numStreams)).onPingRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.any(long.class));
            Mockito.verify(serverListener, Mockito.never()).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
            for (StringBuilder builder : receivedData) {
                Assert.assertEquals(dataAsHex, builder.toString());
            }
            for (long receivedPing : receivedPings) {
                Assert.assertEquals(pingData, receivedPing);
            }
        } finally {
            // Don't wait for server to close streams
            setClientGracefulShutdownTime(0);
            data.release();
        }
    }
}

