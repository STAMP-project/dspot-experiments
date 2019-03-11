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


import Http2Connection.Listener;
import Http2Error.INTERNAL_ERROR;
import State.CLOSED;
import State.HALF_CLOSED_LOCAL;
import State.HALF_CLOSED_REMOTE;
import State.OPEN;
import State.RESERVED_LOCAL;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.handler.codec.http2.Http2Connection.Endpoint;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link DefaultHttp2Connection}.
 */
public class DefaultHttp2ConnectionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultHttp2Connection server;

    private DefaultHttp2Connection client;

    private static DefaultEventLoopGroup group;

    @Mock
    private Listener clientListener;

    @Mock
    private Listener clientListener2;

    @Test
    public void getStreamWithoutStreamShouldReturnNull() {
        Assert.assertNull(server.stream(100));
    }

    @Test
    public void removeAllStreamsWithEmptyStreams() throws InterruptedException {
        testRemoveAllStreams();
    }

    @Test
    public void removeAllStreamsWithJustOneLocalStream() throws Http2Exception, InterruptedException {
        client.local().createStream(3, false);
        testRemoveAllStreams();
    }

    @Test
    public void removeAllStreamsWithJustOneRemoveStream() throws Http2Exception, InterruptedException {
        client.remote().createStream(2, false);
        testRemoveAllStreams();
    }

    @Test
    public void removeAllStreamsWithManyActiveStreams() throws Http2Exception, InterruptedException {
        Endpoint<Http2RemoteFlowController> remote = client.remote();
        Endpoint<Http2LocalFlowController> local = client.local();
        for (int c = 3, s = 2; c < 5000; c += 2 , s += 2) {
            local.createStream(c, false);
            remote.createStream(s, false);
        }
        testRemoveAllStreams();
    }

    @Test
    public void removeIndividualStreamsWhileCloseDoesNotNPE() throws Http2Exception, InterruptedException {
        final Http2Stream streamA = client.local().createStream(3, false);
        final Http2Stream streamB = client.remote().createStream(2, false);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                streamA.close();
                streamB.close();
                return null;
            }
        }).when(clientListener2).onStreamClosed(ArgumentMatchers.any(Http2Stream.class));
        try {
            client.addListener(clientListener2);
            testRemoveAllStreams();
        } finally {
            client.removeListener(clientListener2);
        }
    }

    @Test
    public void removeAllStreamsWhileIteratingActiveStreams() throws Http2Exception, InterruptedException {
        final Endpoint<Http2RemoteFlowController> remote = client.remote();
        final Endpoint<Http2LocalFlowController> local = client.local();
        for (int c = 3, s = 2; c < 5000; c += 2 , s += 2) {
            local.createStream(c, false);
            remote.createStream(s, false);
        }
        final Promise<Void> promise = DefaultHttp2ConnectionTest.group.next().newPromise();
        final CountDownLatch latch = new CountDownLatch(client.numActiveStreams());
        client.forEachActiveStream(new Http2StreamVisitor() {
            @Override
            public boolean visit(Http2Stream stream) {
                client.close(promise).addListener(new io.netty.util.concurrent.FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        Assert.assertTrue(promise.isDone());
                        latch.countDown();
                    }
                });
                return true;
            }
        });
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void removeAllStreamsWhileIteratingActiveStreamsAndExceptionOccurs() throws Http2Exception, InterruptedException {
        final Endpoint<Http2RemoteFlowController> remote = client.remote();
        final Endpoint<Http2LocalFlowController> local = client.local();
        for (int c = 3, s = 2; c < 5000; c += 2 , s += 2) {
            local.createStream(c, false);
            remote.createStream(s, false);
        }
        final Promise<Void> promise = DefaultHttp2ConnectionTest.group.next().newPromise();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            client.forEachActiveStream(new Http2StreamVisitor() {
                @Override
                public boolean visit(Http2Stream stream) throws Http2Exception {
                    // This close call is basically a noop, because the following statement will throw an exception.
                    client.close(promise);
                    // Do an invalid operation while iterating.
                    remote.createStream(3, false);
                    return true;
                }
            });
        } catch (Http2Exception ignored) {
            client.close(promise).addListener(new io.netty.util.concurrent.FutureListener<Void>() {
                @Override
                public void operationComplete(Future<Void> future) throws Exception {
                    Assert.assertTrue(promise.isDone());
                    latch.countDown();
                }
            });
        }
        Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void goAwayReceivedShouldCloseStreamsGreaterThanLastStream() throws Exception {
        Http2Stream stream1 = client.local().createStream(3, false);
        Http2Stream stream2 = client.local().createStream(5, false);
        Http2Stream remoteStream = client.remote().createStream(4, false);
        Assert.assertEquals(OPEN, stream1.state());
        Assert.assertEquals(OPEN, stream2.state());
        client.goAwayReceived(3, 8, null);
        Assert.assertEquals(OPEN, stream1.state());
        Assert.assertEquals(CLOSED, stream2.state());
        Assert.assertEquals(OPEN, remoteStream.state());
        Assert.assertEquals(3, client.local().lastStreamKnownByPeer());
        Assert.assertEquals(5, client.local().lastStreamCreated());
        // The remote endpoint must not be affected by a received GOAWAY frame.
        Assert.assertEquals((-1), client.remote().lastStreamKnownByPeer());
        Assert.assertEquals(OPEN, remoteStream.state());
    }

    @Test
    public void goAwaySentShouldCloseStreamsGreaterThanLastStream() throws Exception {
        Http2Stream stream1 = server.remote().createStream(3, false);
        Http2Stream stream2 = server.remote().createStream(5, false);
        Http2Stream localStream = server.local().createStream(4, false);
        server.goAwaySent(3, 8, null);
        Assert.assertEquals(OPEN, stream1.state());
        Assert.assertEquals(CLOSED, stream2.state());
        Assert.assertEquals(3, server.remote().lastStreamKnownByPeer());
        Assert.assertEquals(5, server.remote().lastStreamCreated());
        // The local endpoint must not be affected by a sent GOAWAY frame.
        Assert.assertEquals((-1), server.local().lastStreamKnownByPeer());
        Assert.assertEquals(OPEN, localStream.state());
    }

    @Test
    public void serverCreateStreamShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.local().createStream(2, false);
        Assert.assertEquals(2, stream.id());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertEquals(1, server.numActiveStreams());
        Assert.assertEquals(2, server.local().lastStreamCreated());
        stream = server.local().createStream(4, true);
        Assert.assertEquals(4, stream.id());
        Assert.assertEquals(HALF_CLOSED_LOCAL, stream.state());
        Assert.assertEquals(2, server.numActiveStreams());
        Assert.assertEquals(4, server.local().lastStreamCreated());
        stream = server.remote().createStream(3, true);
        Assert.assertEquals(3, stream.id());
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Assert.assertEquals(3, server.numActiveStreams());
        Assert.assertEquals(3, server.remote().lastStreamCreated());
        stream = server.remote().createStream(5, false);
        Assert.assertEquals(5, stream.id());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertEquals(4, server.numActiveStreams());
        Assert.assertEquals(5, server.remote().lastStreamCreated());
    }

    @Test
    public void clientCreateStreamShouldSucceed() throws Http2Exception {
        Http2Stream stream = client.remote().createStream(2, false);
        Assert.assertEquals(2, stream.id());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertEquals(1, client.numActiveStreams());
        Assert.assertEquals(2, client.remote().lastStreamCreated());
        stream = client.remote().createStream(4, true);
        Assert.assertEquals(4, stream.id());
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Assert.assertEquals(2, client.numActiveStreams());
        Assert.assertEquals(4, client.remote().lastStreamCreated());
        Assert.assertTrue(stream.isHeadersReceived());
        stream = client.local().createStream(3, true);
        Assert.assertEquals(3, stream.id());
        Assert.assertEquals(HALF_CLOSED_LOCAL, stream.state());
        Assert.assertEquals(3, client.numActiveStreams());
        Assert.assertEquals(3, client.local().lastStreamCreated());
        Assert.assertTrue(stream.isHeadersSent());
        stream = client.local().createStream(5, false);
        Assert.assertEquals(5, stream.id());
        Assert.assertEquals(OPEN, stream.state());
        Assert.assertEquals(4, client.numActiveStreams());
        Assert.assertEquals(5, client.local().lastStreamCreated());
    }

    @Test
    public void serverReservePushStreamShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, true);
        Http2Stream pushStream = server.local().reservePushStream(2, stream);
        Assert.assertEquals(2, pushStream.id());
        Assert.assertEquals(RESERVED_LOCAL, pushStream.state());
        Assert.assertEquals(1, server.numActiveStreams());
        Assert.assertEquals(2, server.local().lastStreamCreated());
    }

    @Test
    public void clientReservePushStreamShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, true);
        Http2Stream pushStream = server.local().reservePushStream(4, stream);
        Assert.assertEquals(4, pushStream.id());
        Assert.assertEquals(RESERVED_LOCAL, pushStream.state());
        Assert.assertEquals(1, server.numActiveStreams());
        Assert.assertEquals(4, server.local().lastStreamCreated());
    }

    @Test
    public void serverRemoteIncrementAndGetStreamShouldSucceed() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldSucceed(server.remote());
    }

    @Test
    public void serverLocalIncrementAndGetStreamShouldSucceed() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldSucceed(server.local());
    }

    @Test
    public void clientRemoteIncrementAndGetStreamShouldSucceed() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldSucceed(client.remote());
    }

    @Test
    public void clientLocalIncrementAndGetStreamShouldSucceed() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldSucceed(client.local());
    }

    @Test(expected = Http2NoMoreStreamIdsException.class)
    public void serverRemoteIncrementAndGetStreamShouldRespectOverflow() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldRespectOverflow(server.remote(), Integer.MAX_VALUE);
    }

    @Test(expected = Http2NoMoreStreamIdsException.class)
    public void serverLocalIncrementAndGetStreamShouldRespectOverflow() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldRespectOverflow(server.local(), ((Integer.MAX_VALUE) - 1));
    }

    @Test(expected = Http2NoMoreStreamIdsException.class)
    public void clientRemoteIncrementAndGetStreamShouldRespectOverflow() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldRespectOverflow(client.remote(), ((Integer.MAX_VALUE) - 1));
    }

    @Test(expected = Http2NoMoreStreamIdsException.class)
    public void clientLocalIncrementAndGetStreamShouldRespectOverflow() throws Http2Exception {
        DefaultHttp2ConnectionTest.incrementAndGetStreamShouldRespectOverflow(client.local(), Integer.MAX_VALUE);
    }

    @Test(expected = Http2Exception.class)
    public void newStreamBehindExpectedShouldThrow() throws Http2Exception {
        server.local().createStream(0, true);
    }

    @Test(expected = Http2Exception.class)
    public void newStreamNotForServerShouldThrow() throws Http2Exception {
        server.local().createStream(11, true);
    }

    @Test(expected = Http2Exception.class)
    public void newStreamNotForClientShouldThrow() throws Http2Exception {
        client.local().createStream(10, true);
    }

    @Test(expected = Http2Exception.class)
    public void createShouldThrowWhenMaxAllowedStreamsOpenExceeded() throws Http2Exception {
        server.local().maxActiveStreams(0);
        server.local().createStream(2, true);
    }

    @Test(expected = Http2Exception.class)
    public void serverCreatePushShouldFailOnRemoteEndpointWhenMaxAllowedStreamsExceeded() throws Http2Exception {
        server = new DefaultHttp2Connection(true, 0);
        server.remote().maxActiveStreams(1);
        Http2Stream requestStream = server.remote().createStream(3, false);
        server.remote().reservePushStream(2, requestStream);
    }

    @Test(expected = Http2Exception.class)
    public void clientCreatePushShouldFailOnRemoteEndpointWhenMaxAllowedStreamsExceeded() throws Http2Exception {
        client = new DefaultHttp2Connection(false, 0);
        client.remote().maxActiveStreams(1);
        Http2Stream requestStream = client.remote().createStream(2, false);
        client.remote().reservePushStream(4, requestStream);
    }

    @Test
    public void serverCreatePushShouldSucceedOnLocalEndpointWhenMaxAllowedStreamsExceeded() throws Http2Exception {
        server = new DefaultHttp2Connection(true, 0);
        server.local().maxActiveStreams(1);
        Http2Stream requestStream = server.remote().createStream(3, false);
        Assert.assertNotNull(server.local().reservePushStream(2, requestStream));
    }

    @Test(expected = Http2Exception.class)
    public void reserveWithPushDisallowedShouldThrow() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, true);
        server.remote().allowPushTo(false);
        server.local().reservePushStream(2, stream);
    }

    @Test(expected = Http2Exception.class)
    public void goAwayReceivedShouldDisallowLocalCreation() throws Http2Exception {
        server.goAwayReceived(0, 1L, EMPTY_BUFFER);
        server.local().createStream(3, true);
    }

    @Test
    public void goAwayReceivedShouldAllowRemoteCreation() throws Http2Exception {
        server.goAwayReceived(0, 1L, EMPTY_BUFFER);
        server.remote().createStream(3, true);
    }

    @Test(expected = Http2Exception.class)
    public void goAwaySentShouldDisallowRemoteCreation() throws Http2Exception {
        server.goAwaySent(0, 1L, EMPTY_BUFFER);
        server.remote().createStream(2, true);
    }

    @Test
    public void goAwaySentShouldAllowLocalCreation() throws Http2Exception {
        server.goAwaySent(0, 1L, EMPTY_BUFFER);
        server.local().createStream(2, true);
    }

    @Test
    public void closeShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, true);
        stream.close();
        Assert.assertEquals(CLOSED, stream.state());
        Assert.assertEquals(0, server.numActiveStreams());
    }

    @Test
    public void closeLocalWhenOpenShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, false);
        stream.closeLocalSide();
        Assert.assertEquals(HALF_CLOSED_LOCAL, stream.state());
        Assert.assertEquals(1, server.numActiveStreams());
    }

    @Test
    public void closeRemoteWhenOpenShouldSucceed() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, false);
        stream.closeRemoteSide();
        Assert.assertEquals(HALF_CLOSED_REMOTE, stream.state());
        Assert.assertEquals(1, server.numActiveStreams());
    }

    @Test
    public void closeOnlyOpenSideShouldClose() throws Http2Exception {
        Http2Stream stream = server.remote().createStream(3, true);
        stream.closeLocalSide();
        Assert.assertEquals(CLOSED, stream.state());
        Assert.assertEquals(0, server.numActiveStreams());
    }

    @SuppressWarnings("NumericOverflow")
    @Test(expected = Http2Exception.class)
    public void localStreamInvalidStreamIdShouldThrow() throws Http2Exception {
        client.local().createStream(((Integer.MAX_VALUE) + 2), false);
    }

    @SuppressWarnings("NumericOverflow")
    @Test(expected = Http2Exception.class)
    public void remoteStreamInvalidStreamIdShouldThrow() throws Http2Exception {
        client.remote().createStream(((Integer.MAX_VALUE) + 1), false);
    }

    /**
     * We force {@link #clientListener} methods to all throw a {@link RuntimeException} and verify the following:
     * <ol>
     * <li>all listener methods are called for both {@link #clientListener} and {@link #clientListener2}</li>
     * <li>{@link #clientListener2} is notified after {@link #clientListener}</li>
     * <li>{@link #clientListener2} methods are all still called despite {@link #clientListener}'s
     * method throwing a {@link RuntimeException}</li>
     * </ol>
     */
    @Test
    public void listenerThrowShouldNotPreventOtherListenersFromBeingNotified() throws Http2Exception {
        final boolean[] calledArray = new boolean[128];
        // The following setup will ensure that clientListener throws exceptions, and marks a value in an array
        // such that clientListener2 will verify that is is set or fail the test.
        int methodIndex = 0;
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamActive(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamActive(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamHalfClosed(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamHalfClosed(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamClosed(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamClosed(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamRemoved(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamRemoved(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onGoAwaySent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onGoAwaySent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onGoAwayReceived(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onGoAwayReceived(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerExceptionThrower(calledArray, methodIndex)).when(clientListener).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
        Mockito.doAnswer(new DefaultHttp2ConnectionTest.ListenerVerifyCallAnswer(calledArray, (methodIndex++))).when(clientListener2).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
        // Now we add clientListener2 and exercise all listener functionality
        try {
            client.addListener(clientListener2);
            Http2Stream stream = client.local().createStream(3, false);
            Mockito.verify(clientListener).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener2).onStreamAdded(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener).onStreamActive(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener2).onStreamActive(ArgumentMatchers.any(Http2Stream.class));
            Http2Stream reservedStream = client.remote().reservePushStream(2, stream);
            Mockito.verify(clientListener, Mockito.never()).onStreamActive(DefaultHttp2ConnectionTest.streamEq(reservedStream));
            Mockito.verify(clientListener2, Mockito.never()).onStreamActive(DefaultHttp2ConnectionTest.streamEq(reservedStream));
            reservedStream.open(false);
            Mockito.verify(clientListener).onStreamActive(DefaultHttp2ConnectionTest.streamEq(reservedStream));
            Mockito.verify(clientListener2).onStreamActive(DefaultHttp2ConnectionTest.streamEq(reservedStream));
            stream.closeLocalSide();
            Mockito.verify(clientListener).onStreamHalfClosed(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener2).onStreamHalfClosed(ArgumentMatchers.any(Http2Stream.class));
            stream.close();
            Mockito.verify(clientListener).onStreamClosed(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener2).onStreamClosed(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener).onStreamRemoved(ArgumentMatchers.any(Http2Stream.class));
            Mockito.verify(clientListener2).onStreamRemoved(ArgumentMatchers.any(Http2Stream.class));
            client.goAwaySent(client.connectionStream().id(), INTERNAL_ERROR.code(), EMPTY_BUFFER);
            Mockito.verify(clientListener).onGoAwaySent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
            Mockito.verify(clientListener2).onGoAwaySent(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
            client.goAwayReceived(client.connectionStream().id(), INTERNAL_ERROR.code(), EMPTY_BUFFER);
            Mockito.verify(clientListener).onGoAwayReceived(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
            Mockito.verify(clientListener2).onGoAwayReceived(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuf.class));
        } finally {
            client.removeListener(clientListener2);
        }
    }

    private static final class ListenerExceptionThrower implements Answer<Void> {
        private static final RuntimeException FAKE_EXCEPTION = new RuntimeException("Fake Exception");

        private final boolean[] array;

        private final int index;

        ListenerExceptionThrower(boolean[] array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            array[index] = true;
            throw DefaultHttp2ConnectionTest.ListenerExceptionThrower.FAKE_EXCEPTION;
        }
    }

    private static final class ListenerVerifyCallAnswer implements Answer<Void> {
        private final boolean[] array;

        private final int index;

        ListenerVerifyCallAnswer(boolean[] array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Assert.assertTrue(array[index]);
            return null;
        }
    }
}

