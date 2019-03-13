/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.internal;


import Attributes.Key;
import ClientTransport.PingCallback;
import Grpc.TRANSPORT_ATTR_LOCAL_ADDR;
import Grpc.TRANSPORT_ATTR_REMOTE_ADDR;
import GrpcAttributes.ATTR_CLIENT_EAG_ATTRS;
import GrpcAttributes.ATTR_SECURITY_LEVEL;
import GrpcUtil.CALL_OPTIONS_RPC_OWNED_BY_BALANCER;
import ManagedClientTransport.Listener;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.BINARY_BYTE_MARSHALLER;
import MethodDescriptor.MethodType.UNKNOWN;
import RpcProgress.PROCESSED;
import Status.CANCELLED;
import Status.Code;
import Status.Code.RESOURCE_EXHAUSTED;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import Status.OK;
import Status.UNAVAILABLE;
import TransportTracer.Factory;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ClientStreamTracer;
import io.grpc.ClientStreamTracer.StreamInfo;
import io.grpc.InternalChannelz.SocketStats;
import io.grpc.InternalChannelz.TransportStats;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;
import io.grpc.internal.testing.TestClientStreamTracer;
import io.grpc.internal.testing.TestServerStreamTracer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE;


/**
 * Standard unit tests for {@link ClientTransport}s and {@link ServerTransport}s.
 */
@RunWith(JUnit4.class)
public abstract class AbstractTransportTest {
    private static final int TIMEOUT_MS = 5000;

    private static final Attributes.Key<String> ADDITIONAL_TRANSPORT_ATTR_KEY = Key.create("additional-attr");

    private static final Attributes.Key<String> EAG_ATTR_KEY = Key.create("eag-attr");

    private static final Attributes EAG_ATTRS = Attributes.newBuilder().set(io.grpc.internal.EAG_ATTR_KEY, "value").build();

    protected final Factory fakeClockTransportTracer = new TransportTracer.Factory(new TimeProvider() {
        @Override
        public long currentTimeNanos() {
            return fakeCurrentTimeNanos();
        }
    });

    /**
     * When non-null, will be shut down during tearDown(). However, it _must_ have been started with
     * {@code serverListener}, otherwise tearDown() can't wait for shutdown which can put following
     * tests in an indeterminate state.
     */
    private InternalServer server;

    private ServerTransport serverTransport;

    private ManagedClientTransport client;

    private MethodDescriptor<String, String> methodDescriptor = MethodDescriptor.<String, String>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(AbstractTransportTest.StringMarshaller.INSTANCE).setResponseMarshaller(AbstractTransportTest.StringMarshaller.INSTANCE).build();

    private CallOptions callOptions;

    private Metadata.Key<String> asciiKey = Metadata.Key.of("ascii-key", ASCII_STRING_MARSHALLER);

    private Metadata.Key<String> binaryKey = Metadata.Key.of("key-bin", AbstractTransportTest.StringBinaryMarshaller.INSTANCE);

    private final Metadata.Key<String> tracerHeaderKey = Metadata.Key.of("tracer-key", ASCII_STRING_MARSHALLER);

    private final String tracerKeyValue = "tracer-key-value";

    private Listener mockClientTransportListener = Mockito.mock(Listener.class);

    private AbstractTransportTest.MockServerListener serverListener = new AbstractTransportTest.MockServerListener();

    private ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);

    private final TestClientStreamTracer clientStreamTracer1 = new TestClientStreamTracer();

    private final TestClientStreamTracer clientStreamTracer2 = new TestClientStreamTracer();

    private final ClientStreamTracer.Factory clientStreamTracerFactory = Mockito.mock(ClientStreamTracer.Factory.class, AdditionalAnswers.delegatesTo(new ClientStreamTracer.Factory() {
        final ArrayDeque<TestClientStreamTracer> tracers = new ArrayDeque(Arrays.asList(clientStreamTracer1, clientStreamTracer2));

        @Override
        public ClientStreamTracer newClientStreamTracer(StreamInfo info, Metadata metadata) {
            metadata.put(tracerHeaderKey, tracerKeyValue);
            TestClientStreamTracer tracer = tracers.poll();
            if (tracer != null) {
                return tracer;
            }
            return new TestClientStreamTracer();
        }
    }));

    private final TestServerStreamTracer serverStreamTracer1 = new TestServerStreamTracer();

    private final TestServerStreamTracer serverStreamTracer2 = new TestServerStreamTracer();

    private final ServerStreamTracer.Factory serverStreamTracerFactory = Mockito.mock(ServerStreamTracer.Factory.class, AdditionalAnswers.delegatesTo(new ServerStreamTracer.Factory() {
        final ArrayDeque<TestServerStreamTracer> tracers = new ArrayDeque(Arrays.asList(serverStreamTracer1, serverStreamTracer2));

        @Override
        public ServerStreamTracer newServerStreamTracer(String fullMethodName, Metadata headers) {
            TestServerStreamTracer tracer = tracers.poll();
            if (tracer != null) {
                return tracer;
            }
            return new TestServerStreamTracer();
        }
    }));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // TODO(ejona):
    // multiple streams on same transport
    // multiple client transports to same server
    // halfClose to trigger flush (client and server)
    // flow control pushes back (client and server)
    // flow control provides precisely number of messages requested (client and server)
    // onReady called when buffer drained (on server and client)
    // test no start reentrancy (esp. during failure) (transport and call)
    // multiple requests/responses (verifying contents received)
    // server transport shutdown triggers client shutdown (via GOAWAY)
    // queued message InputStreams are closed on stream cancel
    // (and maybe exceptions handled)
    /**
     * Test for issue https://github.com/grpc/grpc-java/issues/1682
     */
    @Test
    public void frameAfterRstStreamShouldNotBreakClientChannel() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        // Try to create a sequence of frames so that the client receives a HEADERS or DATA frame
        // after having sent a RST_STREAM to the server. Previously, this would have broken the
        // Netty channel.
        ClientStream stream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        stream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        stream.flush();
        stream.writeMessage(methodDescriptor.streamRequest("foo"));
        stream.flush();
        stream.cancel(CANCELLED);
        stream.flush();
        serverStreamCreation.stream.writeHeaders(new Metadata());
        serverStreamCreation.stream.flush();
        serverStreamCreation.stream.writeMessage(methodDescriptor.streamResponse("bar"));
        serverStreamCreation.stream.flush();
        Assert.assertEquals(CANCELLED, clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        ClientStreamListener mockClientStreamListener2 = Mockito.mock(ClientStreamListener.class);
        // Test that the channel is still usable i.e. we can receive headers from the server on a
        // new stream.
        stream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        stream.start(mockClientStreamListener2);
        serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverStreamCreation.stream.writeHeaders(new Metadata());
        serverStreamCreation.stream.flush();
        Mockito.verify(mockClientStreamListener2, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).headersRead(ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void serverNotListening() throws Exception {
        // Start server to just acquire a port.
        server.start(serverListener);
        client = newClientTransport(server);
        server.shutdown();
        Assert.assertTrue(serverListener.waitForShutdown(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        server = null;
        InOrder inOrder = Mockito.inOrder(mockClientTransportListener);
        AbstractTransportTest.runIfNotNull(client.start(mockClientTransportListener));
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        inOrder.verify(mockClientTransportListener).transportShutdown(statusCaptor.capture());
        AbstractTransportTest.assertCodeEquals(UNAVAILABLE, statusCaptor.getValue());
        inOrder.verify(mockClientTransportListener).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportReady();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void clientStartStop() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        InOrder inOrder = Mockito.inOrder(mockClientTransportListener);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        Status shutdownReason = UNAVAILABLE.withDescription("shutdown called");
        client.shutdown(shutdownReason);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        inOrder.verify(mockClientTransportListener).transportShutdown(ArgumentMatchers.same(shutdownReason));
        inOrder.verify(mockClientTransportListener).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void clientStartAndStopOnceConnected() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        InOrder inOrder = Mockito.inOrder(mockClientTransportListener);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportReady();
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.shutdown(UNAVAILABLE);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        inOrder.verify(mockClientTransportListener).transportShutdown(ArgumentMatchers.any(Status.class));
        inOrder.verify(mockClientTransportListener).transportTerminated();
        Assert.assertTrue(serverTransportListener.waitForTermination(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        server.shutdown();
        Assert.assertTrue(serverListener.waitForShutdown(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        server = null;
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void checkClientAttributes() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        Assume.assumeTrue(((client) instanceof ConnectionClientTransport));
        ConnectionClientTransport connectionClient = ((ConnectionClientTransport) (client));
        AbstractTransportTest.startTransport(connectionClient, mockClientTransportListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportReady();
        Assert.assertNotNull("security level should be set in client attributes", connectionClient.getAttributes().get(ATTR_SECURITY_LEVEL));
    }

    @Test
    public void serverAlreadyListening() throws Exception {
        client = null;
        server.start(serverListener);
        int port = -1;
        SocketAddress addr = server.getListenSocketAddress();
        if (addr instanceof InetSocketAddress) {
            port = ((InetSocketAddress) (addr)).getPort();
        }
        InternalServer server2 = Iterables.getOnlyElement(newServer(port, Arrays.asList(serverStreamTracerFactory)));
        thrown.expect(IOException.class);
        server2.start(new AbstractTransportTest.MockServerListener());
    }

    @Test
    public void openStreamPreventsTermination() throws Exception {
        server.start(serverListener);
        int port = -1;
        SocketAddress addr = server.getListenSocketAddress();
        if (addr instanceof InetSocketAddress) {
            port = ((InetSocketAddress) (addr)).getPort();
        }
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        client.shutdown(UNAVAILABLE);
        client = null;
        server.shutdown();
        serverTransport.shutdown();
        serverTransport = null;
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportShutdown(ArgumentMatchers.any(Status.class));
        Assert.assertTrue(serverListener.waitForShutdown(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        // A new server should be able to start listening, since the current server has given up
        // resources. There may be cases this is impossible in the future, but for now it is a useful
        // property.
        serverListener = new AbstractTransportTest.MockServerListener();
        server = Iterables.getOnlyElement(newServer(port, Arrays.asList(serverStreamTracerFactory)));
        server.start(serverListener);
        // Try to "flush" out any listener notifications on client and server. This also ensures that
        // the stream still functions.
        serverStream.writeHeaders(new Metadata());
        clientStream.halfClose();
        Assert.assertNotNull(clientStreamListener.headers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverStreamListener.awaitHalfClosed(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(false);
        Assert.assertFalse(serverTransportListener.isTerminated());
        clientStream.cancel(CANCELLED);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        Assert.assertTrue(serverTransportListener.waitForTermination(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shutdownNowKillsClientStream() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status status = Status.UNKNOWN.withDescription("test shutdownNow");
        client.shutdownNow(status);
        client = null;
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportShutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        Assert.assertTrue(serverTransportListener.waitForTermination(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverTransportListener.isTerminated());
        Assert.assertEquals(status, clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status serverStatus = serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertFalse(serverStatus.isOk());
        Assert.assertTrue(clientStreamTracer1.await(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        AbstractTransportTest.assertStatusEquals(status, clientStreamTracer1.getStatus());
        Assert.assertTrue(serverStreamTracer1.await(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        AbstractTransportTest.assertStatusEquals(serverStatus, serverStreamTracer1.getStatus());
    }

    @Test
    public void shutdownNowKillsServerStream() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status shutdownStatus = Status.UNKNOWN.withDescription("test shutdownNow");
        serverTransport.shutdownNow(shutdownStatus);
        serverTransport = null;
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportShutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        Assert.assertTrue(serverTransportListener.waitForTermination(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverTransportListener.isTerminated());
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertFalse(clientStreamStatus.isOk());
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(clientStreamTracer1.await(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        AbstractTransportTest.assertStatusEquals(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertTrue(serverStreamTracer1.await(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        AbstractTransportTest.assertStatusEquals(shutdownStatus, serverStreamTracer1.getStatus());
        // Generally will be same status provided to shutdownNow, but InProcessTransport can't
        // differentiate between client and server shutdownNow. The status is not really used on
        // server-side, so we don't care much.
        Assert.assertNotNull(serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void ping() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientTransport.PingCallback mockPingCallback = Mockito.mock(PingCallback.class);
        try {
            client.ping(mockPingCallback, MoreExecutors.directExecutor());
        } catch (UnsupportedOperationException ex) {
            // Transport doesn't support ping, so this neither passes nor fails.
            Assume.assumeTrue(false);
        }
        Mockito.verify(mockPingCallback, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).onSuccess(Matchers.anyLong());
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void ping_duringShutdown() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        // Stream prevents termination
        ClientStream stream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        stream.start(clientStreamListener);
        client.shutdown(UNAVAILABLE);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportShutdown(ArgumentMatchers.any(Status.class));
        ClientTransport.PingCallback mockPingCallback = Mockito.mock(PingCallback.class);
        try {
            client.ping(mockPingCallback, MoreExecutors.directExecutor());
        } catch (UnsupportedOperationException ex) {
            // Transport doesn't support ping, so this neither passes nor fails.
            Assume.assumeTrue(false);
        }
        Mockito.verify(mockPingCallback, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).onSuccess(Matchers.anyLong());
        stream.cancel(CANCELLED);
    }

    @Test
    public void ping_afterTermination() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportReady();
        Status shutdownReason = UNAVAILABLE.withDescription("shutdown called");
        client.shutdown(shutdownReason);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        ClientTransport.PingCallback mockPingCallback = Mockito.mock(PingCallback.class);
        try {
            client.ping(mockPingCallback, MoreExecutors.directExecutor());
        } catch (UnsupportedOperationException ex) {
            // Transport doesn't support ping, so this neither passes nor fails.
            Assume.assumeTrue(false);
        }
        Mockito.verify(mockPingCallback, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).onFailure(throwableCaptor.capture());
        Status status = Status.fromThrowable(throwableCaptor.getValue());
        Assert.assertSame(shutdownReason, status);
    }

    @Test
    public void newStream_duringShutdown() throws Exception {
        InOrder inOrder = Mockito.inOrder(clientStreamTracerFactory);
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        // Stream prevents termination
        ClientStream stream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        inOrder.verify(clientStreamTracerFactory).newClientStreamTracer(ArgumentMatchers.any(StreamInfo.class), ArgumentMatchers.any(Metadata.class));
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        stream.start(clientStreamListener);
        client.shutdown(UNAVAILABLE);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportShutdown(ArgumentMatchers.any(Status.class));
        ClientStream stream2 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        inOrder.verify(clientStreamTracerFactory).newClientStreamTracer(ArgumentMatchers.any(StreamInfo.class), ArgumentMatchers.any(Metadata.class));
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener2 = new AbstractTransportTest.ClientStreamListenerBase();
        stream2.start(clientStreamListener2);
        Status clientStreamStatus2 = clientStreamListener2.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamListener2.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        AbstractTransportTest.assertCodeEquals(UNAVAILABLE, clientStreamStatus2);
        Assert.assertNull(clientStreamTracer2.getInboundTrailers());
        Assert.assertSame(clientStreamStatus2, clientStreamTracer2.getStatus());
        // Make sure earlier stream works.
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        // TODO(zdapeng): Increased timeout to 20 seconds to see if flakiness of #2328 persists. Take
        // further action after sufficient observation.
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail((20 * (AbstractTransportTest.TIMEOUT_MS)), TimeUnit.MILLISECONDS);
        serverStreamCreation.stream.close(OK, new Metadata());
        AbstractTransportTest.assertCodeEquals(OK, clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void newStream_afterTermination() throws Exception {
        // We expect the same general behavior as duringShutdown, but for some transports (e.g., Netty)
        // dealing with afterTermination is harder than duringShutdown.
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportReady();
        Status shutdownReason = UNAVAILABLE.withDescription("shutdown called");
        client.shutdown(shutdownReason);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportTerminated();
        Thread.sleep(100);
        ClientStream stream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        stream.start(clientStreamListener);
        Assert.assertEquals(shutdownReason, clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(ArgumentMatchers.anyBoolean());
        Mockito.verify(clientStreamTracerFactory).newClientStreamTracer(ArgumentMatchers.any(StreamInfo.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(shutdownReason, clientStreamTracer1.getStatus());
        // Assert no interactions
        Assert.assertNull(serverStreamTracer1.getServerCallInfo());
    }

    @Test
    public void transportInUse_balancerRpcsNotCounted() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        // stream1 is created by balancer through Subchannel.asChannel(), which is marked by
        // CALL_OPTIONS_RPC_OWNED_BY_BALANCER in CallOptions.  It won't be counted for in-use signal.
        ClientStream stream1 = client.newStream(methodDescriptor, new Metadata(), callOptions.withOption(CALL_OPTIONS_RPC_OWNED_BY_BALANCER, Boolean.TRUE));
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener1 = new AbstractTransportTest.ClientStreamListenerBase();
        stream1.start(clientStreamListener1);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation1 = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // stream2 is the normal RPC, and will be counted for in-use
        ClientStream stream2 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener2 = new AbstractTransportTest.ClientStreamListenerBase();
        stream2.start(clientStreamListener2);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        AbstractTransportTest.StreamCreation serverStreamCreation2 = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        stream2.halfClose();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(false);
        serverStreamCreation2.stream.close(OK, new Metadata());
        // As soon as stream2 is closed, even though stream1 is still open, the transport will report
        // in-use == false.
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        stream1.halfClose();
        serverStreamCreation1.stream.close(OK, new Metadata());
        // Verify that the callback has been called only once for true and false respectively
        Mockito.verify(mockClientTransportListener).transportInUse(true);
        Mockito.verify(mockClientTransportListener).transportInUse(false);
    }

    @Test
    public void transportInUse_normalClose() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream stream1 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener1 = new AbstractTransportTest.ClientStreamListenerBase();
        stream1.start(clientStreamListener1);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation1 = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ClientStream stream2 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener2 = new AbstractTransportTest.ClientStreamListenerBase();
        stream2.start(clientStreamListener2);
        AbstractTransportTest.StreamCreation serverStreamCreation2 = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        stream1.halfClose();
        serverStreamCreation1.stream.close(OK, new Metadata());
        stream2.halfClose();
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(false);
        serverStreamCreation2.stream.close(OK, new Metadata());
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        // Verify that the callback has been called only once for true and false respectively
        Mockito.verify(mockClientTransportListener).transportInUse(true);
        Mockito.verify(mockClientTransportListener).transportInUse(false);
    }

    @Test
    public void transportInUse_clientCancel() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream stream1 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener1 = new AbstractTransportTest.ClientStreamListenerBase();
        stream1.start(clientStreamListener1);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(true);
        ClientStream stream2 = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener2 = new AbstractTransportTest.ClientStreamListenerBase();
        stream2.start(clientStreamListener2);
        stream1.cancel(CANCELLED);
        Mockito.verify(mockClientTransportListener, Mockito.never()).transportInUse(false);
        stream2.cancel(CANCELLED);
        Mockito.verify(mockClientTransportListener, Mockito.timeout(AbstractTransportTest.TIMEOUT_MS)).transportInUse(false);
        // Verify that the callback has been called only once for true and false respectively
        Mockito.verify(mockClientTransportListener).transportInUse(true);
        Mockito.verify(mockClientTransportListener).transportInUse(false);
    }

    @Test
    public void basicStream() throws Exception {
        InOrder clientInOrder = Mockito.inOrder(clientStreamTracerFactory);
        InOrder serverInOrder = Mockito.inOrder(serverStreamTracerFactory);
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        // This attribute is available right after transport is started
        assertThat(getAttributes().get(ATTR_CLIENT_EAG_ATTRS)).isSameAs(AbstractTransportTest.EAG_ATTRS);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        Metadata clientHeaders = new Metadata();
        clientHeaders.put(asciiKey, "client");
        clientHeaders.put(asciiKey, "dupvalue");
        clientHeaders.put(asciiKey, "dupvalue");
        clientHeaders.put(binaryKey, "?binaryclient");
        clientHeaders.put(binaryKey, "dup,value");
        Metadata clientHeadersCopy = new Metadata();
        clientHeadersCopy.merge(clientHeaders);
        ClientStream clientStream = client.newStream(methodDescriptor, clientHeaders, callOptions);
        ArgumentCaptor<ClientStreamTracer.StreamInfo> streamInfoCaptor = ArgumentCaptor.forClass(null);
        clientInOrder.verify(clientStreamTracerFactory).newClientStreamTracer(streamInfoCaptor.capture(), ArgumentMatchers.same(clientHeaders));
        ClientStreamTracer.StreamInfo streamInfo = streamInfoCaptor.getValue();
        assertThat(streamInfo.getTransportAttrs()).isSameAs(getAttributes());
        assertThat(streamInfo.getCallOptions()).isSameAs(callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertTrue(clientStreamTracer1.awaitOutboundHeaders(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertEquals(methodDescriptor.getFullMethodName(), serverStreamCreation.method);
        Assert.assertEquals(Lists.newArrayList(clientHeadersCopy.getAll(asciiKey)), Lists.newArrayList(serverStreamCreation.headers.getAll(asciiKey)));
        Assert.assertEquals(Lists.newArrayList(clientHeadersCopy.getAll(binaryKey)), Lists.newArrayList(serverStreamCreation.headers.getAll(binaryKey)));
        Assert.assertEquals(tracerKeyValue, serverStreamCreation.headers.get(tracerHeaderKey));
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        serverInOrder.verify(serverStreamTracerFactory).newServerStreamTracer(ArgumentMatchers.eq(methodDescriptor.getFullMethodName()), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals("additional attribute value", serverStream.getAttributes().get(io.grpc.internal.ADDITIONAL_TRANSPORT_ATTR_KEY));
        Assert.assertNotNull(serverStream.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR));
        Assert.assertNotNull(serverStream.getAttributes().get(TRANSPORT_ATTR_LOCAL_ADDR));
        // This attribute is still available when the transport is connected
        assertThat(getAttributes().get(ATTR_CLIENT_EAG_ATTRS)).isSameAs(AbstractTransportTest.EAG_ATTRS);
        serverStream.request(1);
        Assert.assertTrue(clientStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(clientStream.isReady());
        clientStream.writeMessage(methodDescriptor.streamRequest("Hello!"));
        assertThat(clientStreamTracer1.nextOutboundEvent()).isEqualTo("outboundMessage(0)");
        clientStream.flush();
        InputStream message = serverStreamListener.messageQueue.poll(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertEquals("Hello!", methodDescriptor.parseRequest(message));
        message.close();
        assertThat(clientStreamTracer1.nextOutboundEvent()).matches("outboundMessageSent\\(0, -?[0-9]+, -?[0-9]+\\)");
        if (sizesReported()) {
            assertThat(clientStreamTracer1.getOutboundWireSize()).isGreaterThan(0L);
            assertThat(clientStreamTracer1.getOutboundUncompressedSize()).isGreaterThan(0L);
        } else {
            assertThat(clientStreamTracer1.getOutboundWireSize()).isEqualTo(0L);
            assertThat(clientStreamTracer1.getOutboundUncompressedSize()).isEqualTo(0L);
        }
        assertThat(serverStreamTracer1.nextInboundEvent()).isEqualTo("inboundMessage(0)");
        Assert.assertNull("no additional message expected", serverStreamListener.messageQueue.poll());
        clientStream.halfClose();
        Assert.assertTrue(serverStreamListener.awaitHalfClosed(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        if (sizesReported()) {
            assertThat(serverStreamTracer1.getInboundWireSize()).isGreaterThan(0L);
            assertThat(serverStreamTracer1.getInboundUncompressedSize()).isGreaterThan(0L);
        } else {
            assertThat(serverStreamTracer1.getInboundWireSize()).isEqualTo(0L);
            assertThat(serverStreamTracer1.getInboundUncompressedSize()).isEqualTo(0L);
        }
        assertThat(serverStreamTracer1.nextInboundEvent()).matches("inboundMessageRead\\(0, -?[0-9]+, -?[0-9]+\\)");
        Metadata serverHeaders = new Metadata();
        serverHeaders.put(asciiKey, "server");
        serverHeaders.put(asciiKey, "dupvalue");
        serverHeaders.put(asciiKey, "dupvalue");
        serverHeaders.put(binaryKey, "?binaryserver");
        serverHeaders.put(binaryKey, "dup,value");
        Metadata serverHeadersCopy = new Metadata();
        serverHeadersCopy.merge(serverHeaders);
        serverStream.writeHeaders(serverHeaders);
        Metadata headers = clientStreamListener.headers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(headers);
        assertAsciiMetadataValuesEqual(serverHeadersCopy.getAll(asciiKey), headers.getAll(asciiKey));
        Assert.assertEquals(Lists.newArrayList(serverHeadersCopy.getAll(binaryKey)), Lists.newArrayList(headers.getAll(binaryKey)));
        clientStream.request(1);
        Assert.assertTrue(serverStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverStream.isReady());
        serverStream.writeMessage(methodDescriptor.streamResponse("Hi. Who are you?"));
        assertThat(serverStreamTracer1.nextOutboundEvent()).isEqualTo("outboundMessage(0)");
        serverStream.flush();
        message = clientStreamListener.messageQueue.poll(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull("message expected", message);
        assertThat(serverStreamTracer1.nextOutboundEvent()).matches("outboundMessageSent\\(0, -?[0-9]+, -?[0-9]+\\)");
        if (sizesReported()) {
            assertThat(serverStreamTracer1.getOutboundWireSize()).isGreaterThan(0L);
            assertThat(serverStreamTracer1.getOutboundUncompressedSize()).isGreaterThan(0L);
        } else {
            assertThat(serverStreamTracer1.getOutboundWireSize()).isEqualTo(0L);
            assertThat(serverStreamTracer1.getOutboundUncompressedSize()).isEqualTo(0L);
        }
        Assert.assertTrue(clientStreamTracer1.getInboundHeaders());
        assertThat(clientStreamTracer1.nextInboundEvent()).isEqualTo("inboundMessage(0)");
        Assert.assertEquals("Hi. Who are you?", methodDescriptor.parseResponse(message));
        assertThat(clientStreamTracer1.nextInboundEvent()).matches("inboundMessageRead\\(0, -?[0-9]+, -?[0-9]+\\)");
        if (sizesReported()) {
            assertThat(clientStreamTracer1.getInboundWireSize()).isGreaterThan(0L);
            assertThat(clientStreamTracer1.getInboundUncompressedSize()).isGreaterThan(0L);
        } else {
            assertThat(clientStreamTracer1.getInboundWireSize()).isEqualTo(0L);
            assertThat(clientStreamTracer1.getInboundUncompressedSize()).isEqualTo(0L);
        }
        message.close();
        Assert.assertNull("no additional message expected", clientStreamListener.messageQueue.poll());
        Status status = OK.withDescription("That was normal");
        Metadata trailers = new Metadata();
        trailers.put(asciiKey, "trailers");
        trailers.put(asciiKey, "dupvalue");
        trailers.put(asciiKey, "dupvalue");
        trailers.put(binaryKey, "?binarytrailers");
        trailers.put(binaryKey, "dup,value");
        serverStream.close(status, trailers);
        Assert.assertNull(serverStreamTracer1.nextInboundEvent());
        Assert.assertNull(serverStreamTracer1.nextOutboundEvent());
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertSame(status, serverStreamTracer1.getStatus());
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertNull(clientStreamTracer1.nextInboundEvent());
        Assert.assertNull(clientStreamTracer1.nextOutboundEvent());
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals(status.getDescription(), clientStreamStatus.getDescription());
        assertAsciiMetadataValuesEqual(trailers.getAll(asciiKey), clientStreamTrailers.getAll(asciiKey));
        Assert.assertEquals(Lists.newArrayList(trailers.getAll(binaryKey)), Lists.newArrayList(clientStreamTrailers.getAll(binaryKey)));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void authorityPropagation() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientHeaders = new Metadata();
        ClientStream clientStream = client.newStream(methodDescriptor, clientHeaders, callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        Assert.assertEquals(testAuthority(server), serverStream.getAuthority());
    }

    @Test
    public void zeroMessageStream() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        clientStream.halfClose();
        Assert.assertTrue(serverStreamListener.awaitHalfClosed(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        serverStream.writeHeaders(new Metadata());
        Assert.assertNotNull(clientStreamListener.headers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status status = OK.withDescription("Nice talking to you");
        serverStream.close(status, new Metadata());
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamTrailers);
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals(status.getDescription(), clientStreamStatus.getDescription());
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertTrue(clientStreamTracer1.getInboundHeaders());
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertSame(status, serverStreamTracer1.getStatus());
    }

    @Test
    public void earlyServerClose_withServerHeaders() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        serverStream.writeHeaders(new Metadata());
        Assert.assertNotNull(clientStreamListener.headers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status strippedStatus = OK.withDescription("Hello. Goodbye.");
        Status status = strippedStatus.withCause(new Exception());
        serverStream.close(status, new Metadata());
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamTrailers);
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals("Hello. Goodbye.", clientStreamStatus.getDescription());
        Assert.assertNull(clientStreamStatus.getCause());
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertTrue(clientStreamTracer1.getInboundHeaders());
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertSame(status, serverStreamTracer1.getStatus());
    }

    @Test
    public void earlyServerClose_noServerHeaders() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status strippedStatus = OK.withDescription("Hellogoodbye");
        Status status = strippedStatus.withCause(new Exception());
        Metadata trailers = new Metadata();
        trailers.put(asciiKey, "trailers");
        trailers.put(asciiKey, "dupvalue");
        trailers.put(asciiKey, "dupvalue");
        trailers.put(binaryKey, "?binarytrailers");
        serverStream.close(status, trailers);
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals("Hellogoodbye", clientStreamStatus.getDescription());
        // Cause should not be transmitted to the client.
        Assert.assertNull(clientStreamStatus.getCause());
        Assert.assertEquals(Lists.newArrayList(trailers.getAll(asciiKey)), Lists.newArrayList(clientStreamTrailers.getAll(asciiKey)));
        Assert.assertEquals(Lists.newArrayList(trailers.getAll(binaryKey)), Lists.newArrayList(clientStreamTrailers.getAll(binaryKey)));
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertSame(status, serverStreamTracer1.getStatus());
    }

    @Test
    public void earlyServerClose_serverFailure() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status strippedStatus = INTERNAL.withDescription("I'm not listening");
        Status status = strippedStatus.withCause(new Exception());
        serverStream.close(status, new Metadata());
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamTrailers);
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals(status.getDescription(), clientStreamStatus.getDescription());
        Assert.assertNull(clientStreamStatus.getCause());
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertSame(status, serverStreamTracer1.getStatus());
    }

    @Test
    public void earlyServerClose_serverFailure_withClientCancelOnListenerClosed() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.runIfNotNull(client.start(mockClientTransportListener));
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        final ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase() {
            @Override
            public void closed(Status status, Metadata trailers) {
                super.closed(status, trailers);
                // This simulates the blocking calls which can trigger clientStream.cancel().
                clientStream.cancel(CANCELLED.withCause(status.asRuntimeException()));
            }

            @Override
            public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
                super.closed(status, rpcProgress, trailers);
                // This simulates the blocking calls which can trigger clientStream.cancel().
                clientStream.cancel(CANCELLED.withCause(status.asRuntimeException()));
            }
        };
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status strippedStatus = INTERNAL.withDescription("I'm not listening");
        Status status = strippedStatus.withCause(new Exception());
        serverStream.close(status, new Metadata());
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Metadata clientStreamTrailers = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamTrailers);
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals(status.getDescription(), clientStreamStatus.getDescription());
        Assert.assertNull(clientStreamStatus.getCause());
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertSame(clientStreamTrailers, clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Assert.assertSame(status, serverStreamTracer1.getStatus());
    }

    @Test
    public void clientCancel() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status status = CANCELLED.withDescription("Nevermind").withCause(new Exception());
        clientStream.cancel(status);
        Assert.assertEquals(status, clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status serverStatus = serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotEquals(Status.Code.OK, serverStatus.getCode());
        // Cause should not be transmitted between client and server
        Assert.assertNull(serverStatus.getCause());
        clientStream.cancel(status);
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(status, clientStreamTracer1.getStatus());
        Assert.assertSame(serverStatus, serverStreamTracer1.getStatus());
    }

    @Test
    public void clientCancelFromWithinMessageRead() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        final SettableFuture<Boolean> closedCalled = SettableFuture.create();
        final ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        final Status status = CANCELLED.withDescription("nevermind");
        clientStream.start(new ClientStreamListener() {
            private boolean messageReceived = false;

            @Override
            public void headersRead(Metadata headers) {
            }

            @Override
            public void closed(Status status, Metadata trailers) {
                closed(status, PROCESSED, trailers);
            }

            @Override
            public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
                Assert.assertEquals(CANCELLED.getCode(), status.getCode());
                Assert.assertEquals("nevermind", status.getDescription());
                closedCalled.set(true);
            }

            @Override
            public void messagesAvailable(MessageProducer producer) {
                InputStream message;
                while ((message = producer.next()) != null) {
                    Assert.assertFalse("too many messages received", messageReceived);
                    messageReceived = true;
                    Assert.assertEquals("foo", methodDescriptor.parseResponse(message));
                    clientStream.cancel(status);
                } 
            }

            @Override
            public void onReady() {
            }
        });
        clientStream.halfClose();
        clientStream.request(1);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertEquals(methodDescriptor.getFullMethodName(), serverStreamCreation.method);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Assert.assertTrue(serverStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverStream.isReady());
        serverStream.writeHeaders(new Metadata());
        serverStream.writeMessage(methodDescriptor.streamRequest("foo"));
        serverStream.flush();
        // Block until closedCalled was set.
        closedCalled.get(5, TimeUnit.SECONDS);
        serverStream.close(OK, new Metadata());
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertTrue(clientStreamTracer1.getInboundHeaders());
        if (sizesReported()) {
            assertThat(clientStreamTracer1.getInboundWireSize()).isGreaterThan(0L);
            assertThat(clientStreamTracer1.getInboundUncompressedSize()).isGreaterThan(0L);
            assertThat(serverStreamTracer1.getOutboundWireSize()).isGreaterThan(0L);
            assertThat(serverStreamTracer1.getOutboundUncompressedSize()).isGreaterThan(0L);
        } else {
            assertThat(clientStreamTracer1.getInboundWireSize()).isEqualTo(0L);
            assertThat(clientStreamTracer1.getInboundUncompressedSize()).isEqualTo(0L);
            assertThat(serverStreamTracer1.getOutboundWireSize()).isEqualTo(0L);
            assertThat(serverStreamTracer1.getOutboundUncompressedSize()).isEqualTo(0L);
        }
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(status, clientStreamTracer1.getStatus());
        // There is a race between client cancelling and server closing.  The final status seen by the
        // server is non-deterministic.
        Assert.assertTrue(serverStreamTracer1.await(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(serverStreamTracer1.getStatus());
    }

    @Test
    public void serverCancel() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        Status status = DEADLINE_EXCEEDED.withDescription("It was bound to happen").withCause(new Exception());
        serverStream.cancel(status);
        Assert.assertEquals(status, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        // Presently we can't sent much back to the client in this case. Verify that is the current
        // behavior for consistency between transports.
        AbstractTransportTest.assertCodeEquals(CANCELLED, clientStreamStatus);
        // Cause should not be transmitted between server and client
        Assert.assertNull(clientStreamStatus.getCause());
        Mockito.verify(clientStreamTracerFactory).newClientStreamTracer(ArgumentMatchers.any(StreamInfo.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(clientStreamTracer1.getOutboundHeaders());
        Assert.assertNull(clientStreamTracer1.getInboundTrailers());
        Assert.assertSame(clientStreamStatus, clientStreamTracer1.getStatus());
        Mockito.verify(serverStreamTracerFactory).newServerStreamTracer(ArgumentMatchers.anyString(), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(status, serverStreamTracer1.getStatus());
        // Second cancellation shouldn't trigger additional callbacks
        serverStream.cancel(status);
        doPingPong(serverListener);
    }

    @Test
    public void flowControlPushBack() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertEquals(methodDescriptor.getFullMethodName(), serverStreamCreation.method);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        serverStream.writeHeaders(new Metadata());
        String largeMessage;
        {
            int size = 1 * 1024;
            StringBuilder sb = new StringBuilder(size);
            for (int i = 0; i < size; i++) {
                sb.append('a');
            }
            largeMessage = sb.toString();
        }
        serverStream.request(1);
        Assert.assertTrue(clientStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(clientStream.isReady());
        final int maxToSend = 10 * 1024;
        int clientSent;
        // Verify that flow control will push back on client.
        for (clientSent = 0; clientStream.isReady(); clientSent++) {
            if (clientSent > maxToSend) {
                // It seems like flow control isn't working. _Surely_ flow control would have pushed-back
                // already. If this is normal, please configure the transport to buffer less.
                Assert.fail("Too many messages sent before isReady() returned false");
            }
            clientStream.writeMessage(methodDescriptor.streamRequest(largeMessage));
            clientStream.flush();
        }
        Assert.assertTrue((clientSent > 0));
        // Make sure there are at least a few messages buffered.
        for (; clientSent < 5; clientSent++) {
            clientStream.writeMessage(methodDescriptor.streamResponse(largeMessage));
            clientStream.flush();
        }
        doPingPong(serverListener);
        int serverReceived = verifyMessageCountAndClose(serverStreamListener.messageQueue, 1);
        clientStream.request(1);
        Assert.assertTrue(serverStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverStream.isReady());
        int serverSent;
        // Verify that flow control will push back on server.
        for (serverSent = 0; serverStream.isReady(); serverSent++) {
            if (serverSent > maxToSend) {
                // It seems like flow control isn't working. _Surely_ flow control would have pushed-back
                // already. If this is normal, please configure the transport to buffer less.
                Assert.fail("Too many messages sent before isReady() returned false");
            }
            serverStream.writeMessage(methodDescriptor.streamResponse(largeMessage));
            serverStream.flush();
        }
        Assert.assertTrue((serverSent > 0));
        // Make sure there are at least a few messages buffered.
        for (; serverSent < 5; serverSent++) {
            serverStream.writeMessage(methodDescriptor.streamResponse(largeMessage));
            serverStream.flush();
        }
        doPingPong(serverListener);
        int clientReceived = verifyMessageCountAndClose(clientStreamListener.messageQueue, 1);
        serverStream.request(3);
        clientStream.request(3);
        doPingPong(serverListener);
        clientReceived += verifyMessageCountAndClose(clientStreamListener.messageQueue, 3);
        serverReceived += verifyMessageCountAndClose(serverStreamListener.messageQueue, 3);
        // Request the rest
        serverStream.request(clientSent);
        clientStream.request(serverSent);
        clientReceived += verifyMessageCountAndClose(clientStreamListener.messageQueue, (serverSent - clientReceived));
        serverReceived += verifyMessageCountAndClose(serverStreamListener.messageQueue, (clientSent - serverReceived));
        Assert.assertTrue(clientStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(clientStream.isReady());
        Assert.assertTrue(serverStreamListener.awaitOnReadyAndDrain(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(serverStream.isReady());
        // Request four more
        for (int i = 0; i < 5; i++) {
            clientStream.writeMessage(methodDescriptor.streamRequest(largeMessage));
            clientStream.flush();
            serverStream.writeMessage(methodDescriptor.streamResponse(largeMessage));
            serverStream.flush();
        }
        doPingPong(serverListener);
        clientReceived += verifyMessageCountAndClose(clientStreamListener.messageQueue, 4);
        serverReceived += verifyMessageCountAndClose(serverStreamListener.messageQueue, 4);
        // Drain exactly how many messages are left
        serverStream.request(1);
        clientStream.request(1);
        clientReceived += verifyMessageCountAndClose(clientStreamListener.messageQueue, 1);
        serverReceived += verifyMessageCountAndClose(serverStreamListener.messageQueue, 1);
        // And now check that the streams can still complete gracefully
        clientStream.writeMessage(methodDescriptor.streamRequest(largeMessage));
        clientStream.flush();
        clientStream.halfClose();
        doPingPong(serverListener);
        Assert.assertFalse(serverStreamListener.awaitHalfClosed(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        serverStream.request(1);
        serverReceived += verifyMessageCountAndClose(serverStreamListener.messageQueue, 1);
        Assert.assertEquals((clientSent + 6), serverReceived);
        Assert.assertTrue(serverStreamListener.awaitHalfClosed(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        serverStream.writeMessage(methodDescriptor.streamResponse(largeMessage));
        serverStream.flush();
        Status status = OK.withDescription("... quite a lengthy discussion");
        serverStream.close(status, new Metadata());
        doPingPong(serverListener);
        try {
            clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
            Assert.fail("Expected TimeoutException");
        } catch (TimeoutException expectedException) {
        }
        clientStream.request(1);
        clientReceived += verifyMessageCountAndClose(clientStreamListener.messageQueue, 1);
        Assert.assertEquals((serverSent + 6), clientReceived);
        AbstractTransportTest.assertCodeEquals(OK, serverStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Status clientStreamStatus = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertEquals(status.getCode(), clientStreamStatus.getCode());
        Assert.assertEquals(status.getDescription(), clientStreamStatus.getDescription());
    }

    @Test
    public void interactionsAfterServerStreamCloseAreNoops() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        // boilerplate
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.StreamCreation server = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // setup
        clientStream.request(1);
        server.stream.close(INTERNAL, new Metadata());
        Assert.assertNotNull(clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        // Ensure that for a closed ServerStream, interactions are noops
        server.stream.writeHeaders(new Metadata());
        server.stream.writeMessage(methodDescriptor.streamResponse("response"));
        server.stream.close(INTERNAL, new Metadata());
        // Make sure new streams still work properly
        doPingPong(serverListener);
    }

    @Test
    public void interactionsAfterClientStreamCancelAreNoops() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        // boilerplate
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        ClientStreamListener clientListener = Mockito.mock(ClientStreamListener.class);
        clientStream.start(clientListener);
        AbstractTransportTest.StreamCreation server = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // setup
        server.stream.request(1);
        clientStream.cancel(Status.UNKNOWN);
        Assert.assertNotNull(server.listener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        // Ensure that for a cancelled ClientStream, interactions are noops
        clientStream.writeMessage(methodDescriptor.streamRequest("request"));
        clientStream.halfClose();
        clientStream.cancel(Status.UNKNOWN);
        // Make sure new streams still work properly
        doPingPong(serverListener);
    }

    @Test
    public void transportTracer_streamStarted() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!(haveTransportTracer())) {
            return;
        }
        // start first stream
        long serverFirstTimestampNanos;
        long clientFirstTimestampNanos;
        {
            TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
            Assert.assertEquals(0, serverBefore.streamsStarted);
            Assert.assertEquals(0, serverBefore.lastRemoteStreamCreatedTimeNanos);
            TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
            Assert.assertEquals(0, clientBefore.streamsStarted);
            Assert.assertEquals(0, clientBefore.lastRemoteStreamCreatedTimeNanos);
            ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
            AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
            clientStream.start(clientStreamListener);
            AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
            TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
            Assert.assertEquals(1, serverAfter.streamsStarted);
            serverFirstTimestampNanos = serverAfter.lastRemoteStreamCreatedTimeNanos;
            Assert.assertEquals(fakeCurrentTimeNanos(), serverAfter.lastRemoteStreamCreatedTimeNanos);
            TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
            Assert.assertEquals(1, clientAfter.streamsStarted);
            clientFirstTimestampNanos = clientAfter.lastLocalStreamCreatedTimeNanos;
            Assert.assertEquals(fakeCurrentTimeNanos(), clientFirstTimestampNanos);
            ServerStream serverStream = serverStreamCreation.stream;
            serverStream.close(OK, new Metadata());
        }
        final long elapsedMillis = 100;
        advanceClock(100, TimeUnit.MILLISECONDS);
        // start second stream
        {
            TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
            Assert.assertEquals(1, serverBefore.streamsStarted);
            TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
            Assert.assertEquals(1, clientBefore.streamsStarted);
            ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
            AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
            clientStream.start(clientStreamListener);
            AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
            TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
            Assert.assertEquals(2, serverAfter.streamsStarted);
            Assert.assertEquals(TimeUnit.MILLISECONDS.toNanos(elapsedMillis), ((serverAfter.lastRemoteStreamCreatedTimeNanos) - serverFirstTimestampNanos));
            Assert.assertEquals(fakeCurrentTimeNanos(), serverAfter.lastRemoteStreamCreatedTimeNanos);
            TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
            Assert.assertEquals(2, clientAfter.streamsStarted);
            Assert.assertEquals(TimeUnit.MILLISECONDS.toNanos(elapsedMillis), ((clientAfter.lastLocalStreamCreatedTimeNanos) - clientFirstTimestampNanos));
            Assert.assertEquals(fakeCurrentTimeNanos(), clientAfter.lastLocalStreamCreatedTimeNanos);
            ServerStream serverStream = serverStreamCreation.stream;
            serverStream.close(OK, new Metadata());
        }
    }

    @Test
    public void transportTracer_server_streamEnded_ok() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        if (!(haveTransportTracer())) {
            return;
        }
        TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(0, serverBefore.streamsSucceeded);
        Assert.assertEquals(0, serverBefore.streamsFailed);
        TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(0, clientBefore.streamsSucceeded);
        Assert.assertEquals(0, clientBefore.streamsFailed);
        clientStream.halfClose();
        serverStream.close(OK, new Metadata());
        // do not validate stats until close() has been called on client
        Assert.assertNotNull(clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(1, serverAfter.streamsSucceeded);
        Assert.assertEquals(0, serverAfter.streamsFailed);
        TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(1, clientAfter.streamsSucceeded);
        Assert.assertEquals(0, clientAfter.streamsFailed);
    }

    @Test
    public void transportTracer_server_streamEnded_nonOk() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        if (!(haveTransportTracer())) {
            return;
        }
        TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(0, serverBefore.streamsFailed);
        Assert.assertEquals(0, serverBefore.streamsSucceeded);
        TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(0, clientBefore.streamsFailed);
        Assert.assertEquals(0, clientBefore.streamsSucceeded);
        serverStream.close(Status.UNKNOWN, new Metadata());
        // do not validate stats until close() has been called on client
        Assert.assertNotNull(clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        Assert.assertNotNull(clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(1, serverAfter.streamsFailed);
        Assert.assertEquals(0, serverAfter.streamsSucceeded);
        TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(1, clientAfter.streamsFailed);
        Assert.assertEquals(0, clientAfter.streamsSucceeded);
        client.shutdown(UNAVAILABLE);
    }

    @Test
    public void transportTracer_client_streamEnded_nonOk() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!(haveTransportTracer())) {
            return;
        }
        TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(0, serverBefore.streamsFailed);
        Assert.assertEquals(0, serverBefore.streamsSucceeded);
        TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(0, clientBefore.streamsFailed);
        Assert.assertEquals(0, clientBefore.streamsSucceeded);
        clientStream.cancel(Status.UNKNOWN);
        // do not validate stats until close() has been called on server
        Assert.assertNotNull(serverStreamCreation.listener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
        TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(1, serverAfter.streamsFailed);
        Assert.assertEquals(0, serverAfter.streamsSucceeded);
        TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(1, clientAfter.streamsFailed);
        Assert.assertEquals(0, clientAfter.streamsSucceeded);
    }

    @Test
    public void transportTracer_server_receive_msg() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        AbstractTransportTest.ServerStreamListenerBase serverStreamListener = serverStreamCreation.listener;
        if (!(haveTransportTracer())) {
            return;
        }
        TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(0, serverBefore.messagesReceived);
        Assert.assertEquals(0, serverBefore.lastMessageReceivedTimeNanos);
        TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(0, clientBefore.messagesSent);
        Assert.assertEquals(0, clientBefore.lastMessageSentTimeNanos);
        serverStream.request(1);
        clientStream.writeMessage(methodDescriptor.streamRequest("request"));
        clientStream.flush();
        clientStream.halfClose();
        verifyMessageCountAndClose(serverStreamListener.messageQueue, 1);
        TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(1, serverAfter.messagesReceived);
        Assert.assertEquals(fakeCurrentTimeNanos(), serverAfter.lastMessageReceivedTimeNanos);
        TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(1, clientAfter.messagesSent);
        Assert.assertEquals(fakeCurrentTimeNanos(), clientAfter.lastMessageSentTimeNanos);
        serverStream.close(OK, new Metadata());
    }

    @Test
    public void transportTracer_server_send_msg() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        if (!(haveTransportTracer())) {
            return;
        }
        TransportStats serverBefore = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(0, serverBefore.messagesSent);
        Assert.assertEquals(0, serverBefore.lastMessageSentTimeNanos);
        TransportStats clientBefore = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(0, clientBefore.messagesReceived);
        Assert.assertEquals(0, clientBefore.lastMessageReceivedTimeNanos);
        clientStream.request(1);
        serverStream.writeHeaders(new Metadata());
        serverStream.writeMessage(methodDescriptor.streamResponse("response"));
        serverStream.flush();
        verifyMessageCountAndClose(clientStreamListener.messageQueue, 1);
        TransportStats serverAfter = AbstractTransportTest.getTransportStats(serverTransportListener.transport);
        Assert.assertEquals(1, serverAfter.messagesSent);
        Assert.assertEquals(fakeCurrentTimeNanos(), serverAfter.lastMessageSentTimeNanos);
        TransportStats clientAfter = AbstractTransportTest.getTransportStats(client);
        Assert.assertEquals(1, clientAfter.messagesReceived);
        Assert.assertEquals(fakeCurrentTimeNanos(), clientAfter.lastMessageReceivedTimeNanos);
        serverStream.close(OK, new Metadata());
    }

    @Test
    public void socketStats() throws Exception {
        server.start(serverListener);
        ManagedClientTransport client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        ServerStream serverStream = serverStreamCreation.stream;
        SocketAddress serverAddress = clientStream.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR);
        SocketAddress clientAddress = serverStream.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR);
        SocketStats clientSocketStats = client.getStats().get();
        Assert.assertEquals(("clientLocal " + (clientStream.getAttributes())), clientAddress, clientSocketStats.local);
        Assert.assertEquals(("clientRemote " + (clientStream.getAttributes())), serverAddress, clientSocketStats.remote);
        // very basic sanity check that socket options are populated
        Assert.assertNotNull(clientSocketStats.socketOptions.lingerSeconds);
        Assert.assertTrue(clientSocketStats.socketOptions.others.containsKey("SO_SNDBUF"));
        SocketStats serverSocketStats = serverTransportListener.transport.getStats().get();
        Assert.assertEquals(("serverLocal " + (serverStream.getAttributes())), serverAddress, serverSocketStats.local);
        Assert.assertEquals(("serverRemote " + (serverStream.getAttributes())), clientAddress, serverSocketStats.remote);
        // very basic sanity check that socket options are populated
        Assert.assertNotNull(serverSocketStats.socketOptions.lingerSeconds);
        Assert.assertTrue(serverSocketStats.socketOptions.others.containsKey("SO_SNDBUF"));
    }

    /**
     * This assumes the server limits metadata size to GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE.
     */
    @Test
    public void serverChecksInboundMetadataSize() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        Metadata tooLargeMetadata = new Metadata();
        tooLargeMetadata.put(Metadata.Key.of("foo-bin", BINARY_BYTE_MARSHALLER), new byte[DEFAULT_MAX_HEADER_LIST_SIZE]);
        ClientStream clientStream = client.newStream(methodDescriptor, tooLargeMetadata, callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        clientStream.writeMessage(methodDescriptor.streamRequest("foo"));
        clientStream.halfClose();
        clientStream.request(1);
        // Server shouldn't have created a stream, so nothing to clean up on server-side
        // If this times out, the server probably isn't noticing the metadata size
        Status status = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        List<Status.Code> codeOptions = Arrays.asList(Status.Code.UNKNOWN, RESOURCE_EXHAUSTED, Status.Code.INTERNAL);
        if (!(codeOptions.contains(status.getCode()))) {
            Assert.fail(("Status code was not expected: " + status));
        }
    }

    /**
     * This assumes the client limits metadata size to GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE.
     */
    @Test
    public void clientChecksInboundMetadataSize_header() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        Metadata tooLargeMetadata = new Metadata();
        tooLargeMetadata.put(Metadata.Key.of("foo-bin", BINARY_BYTE_MARSHALLER), new byte[DEFAULT_MAX_HEADER_LIST_SIZE]);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        clientStream.writeMessage(methodDescriptor.streamRequest("foo"));
        clientStream.halfClose();
        clientStream.request(1);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverStreamCreation.stream.request(1);
        serverStreamCreation.stream.writeHeaders(tooLargeMetadata);
        serverStreamCreation.stream.writeMessage(methodDescriptor.streamResponse("response"));
        serverStreamCreation.stream.close(OK, new Metadata());
        Status status = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        List<Status.Code> codeOptions = Arrays.asList(Status.Code.UNKNOWN, RESOURCE_EXHAUSTED, Status.Code.INTERNAL);
        if (!(codeOptions.contains(status.getCode()))) {
            Assert.fail(("Status code was not expected: " + status));
        }
        Assert.assertFalse(clientStreamListener.headers.isDone());
    }

    /**
     * This assumes the client limits metadata size to GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE.
     */
    @Test
    public void clientChecksInboundMetadataSize_trailer() throws Exception {
        server.start(serverListener);
        client = newClientTransport(server);
        AbstractTransportTest.startTransport(client, mockClientTransportListener);
        AbstractTransportTest.MockServerTransportListener serverTransportListener = serverListener.takeListenerOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverTransport = serverTransportListener.transport;
        Metadata.Key<String> tellTaleKey = Metadata.Key.of("tell-tale", ASCII_STRING_MARSHALLER);
        Metadata tooLargeMetadata = new Metadata();
        tooLargeMetadata.put(tellTaleKey, "true");
        tooLargeMetadata.put(Metadata.Key.of("foo-bin", BINARY_BYTE_MARSHALLER), new byte[DEFAULT_MAX_HEADER_LIST_SIZE]);
        ClientStream clientStream = client.newStream(methodDescriptor, new Metadata(), callOptions);
        AbstractTransportTest.ClientStreamListenerBase clientStreamListener = new AbstractTransportTest.ClientStreamListenerBase();
        clientStream.start(clientStreamListener);
        clientStream.writeMessage(methodDescriptor.streamRequest("foo"));
        clientStream.halfClose();
        clientStream.request(1);
        AbstractTransportTest.StreamCreation serverStreamCreation = serverTransportListener.takeStreamOrFail(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        serverStreamCreation.stream.request(1);
        serverStreamCreation.stream.writeHeaders(new Metadata());
        serverStreamCreation.stream.writeMessage(methodDescriptor.streamResponse("response"));
        serverStreamCreation.stream.close(OK, tooLargeMetadata);
        Status status = clientStreamListener.status.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        List<Status.Code> codeOptions = Arrays.asList(Status.Code.UNKNOWN, RESOURCE_EXHAUSTED, Status.Code.INTERNAL);
        if (!(codeOptions.contains(status.getCode()))) {
            Assert.fail(("Status code was not expected: " + status));
        }
        Metadata metadata = clientStreamListener.trailers.get(AbstractTransportTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertNull(metadata.get(tellTaleKey));
    }

    private static class MockServerListener implements ServerListener {
        public final BlockingQueue<AbstractTransportTest.MockServerTransportListener> listeners = new LinkedBlockingQueue<>();

        private final SettableFuture<?> shutdown = SettableFuture.create();

        @Override
        public ServerTransportListener transportCreated(ServerTransport transport) {
            AbstractTransportTest.MockServerTransportListener listener = new AbstractTransportTest.MockServerTransportListener(transport);
            listeners.add(listener);
            return listener;
        }

        @Override
        public void serverShutdown() {
            Assert.assertTrue(shutdown.set(null));
        }

        public boolean waitForShutdown(long timeout, TimeUnit unit) throws InterruptedException {
            return AbstractTransportTest.waitForFuture(shutdown, timeout, unit);
        }

        public AbstractTransportTest.MockServerTransportListener takeListenerOrFail(long timeout, TimeUnit unit) throws InterruptedException {
            AbstractTransportTest.MockServerTransportListener listener = listeners.poll(timeout, unit);
            if (listener == null) {
                Assert.fail("Timed out waiting for server transport");
            }
            return listener;
        }
    }

    private static class MockServerTransportListener implements ServerTransportListener {
        public final ServerTransport transport;

        public final BlockingQueue<AbstractTransportTest.StreamCreation> streams = new LinkedBlockingQueue<>();

        private final SettableFuture<?> terminated = SettableFuture.create();

        public MockServerTransportListener(ServerTransport transport) {
            this.transport = transport;
        }

        @Override
        public void streamCreated(ServerStream stream, String method, Metadata headers) {
            AbstractTransportTest.ServerStreamListenerBase listener = new AbstractTransportTest.ServerStreamListenerBase();
            streams.add(new AbstractTransportTest.StreamCreation(stream, method, headers, listener));
            stream.setListener(listener);
        }

        @Override
        public Attributes transportReady(Attributes attributes) {
            return Attributes.newBuilder().setAll(attributes).set(io.grpc.internal.ADDITIONAL_TRANSPORT_ATTR_KEY, "additional attribute value").build();
        }

        @Override
        public void transportTerminated() {
            Assert.assertTrue(terminated.set(null));
        }

        public boolean waitForTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return AbstractTransportTest.waitForFuture(terminated, timeout, unit);
        }

        public boolean isTerminated() {
            return terminated.isDone();
        }

        public AbstractTransportTest.StreamCreation takeStreamOrFail(long timeout, TimeUnit unit) throws InterruptedException {
            AbstractTransportTest.StreamCreation stream = streams.poll(timeout, unit);
            if (stream == null) {
                Assert.fail("Timed out waiting for server stream");
            }
            return stream;
        }
    }

    private static class ServerStreamListenerBase implements ServerStreamListener {
        private final BlockingQueue<InputStream> messageQueue = new LinkedBlockingQueue<>();

        // Would have used Void instead of Object, but null elements are not allowed
        private final BlockingQueue<Object> readyQueue = new LinkedBlockingQueue<>();

        private final CountDownLatch halfClosedLatch = new CountDownLatch(1);

        private final SettableFuture<Status> status = SettableFuture.create();

        private boolean awaitOnReady(int timeout, TimeUnit unit) throws Exception {
            return (readyQueue.poll(timeout, unit)) != null;
        }

        private boolean awaitOnReadyAndDrain(int timeout, TimeUnit unit) throws Exception {
            if (!(awaitOnReady(timeout, unit))) {
                return false;
            }
            // Throw the rest away
            readyQueue.drainTo(Lists.newArrayList());
            return true;
        }

        private boolean awaitHalfClosed(int timeout, TimeUnit unit) throws Exception {
            return halfClosedLatch.await(timeout, unit);
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            if (status.isDone()) {
                Assert.fail("messagesAvailable invoked after closed");
            }
            InputStream message;
            while ((message = producer.next()) != null) {
                messageQueue.add(message);
            } 
        }

        @Override
        public void onReady() {
            if (status.isDone()) {
                Assert.fail("onReady invoked after closed");
            }
            readyQueue.add(new Object());
        }

        @Override
        public void halfClosed() {
            if (status.isDone()) {
                Assert.fail("halfClosed invoked after closed");
            }
            halfClosedLatch.countDown();
        }

        @Override
        public void closed(Status status) {
            if (this.status.isDone()) {
                Assert.fail("closed invoked more than once");
            }
            this.status.set(status);
        }
    }

    private static class ClientStreamListenerBase implements ClientStreamListener {
        private final BlockingQueue<InputStream> messageQueue = new LinkedBlockingQueue<>();

        // Would have used Void instead of Object, but null elements are not allowed
        private final BlockingQueue<Object> readyQueue = new LinkedBlockingQueue<>();

        private final SettableFuture<Metadata> headers = SettableFuture.create();

        private final SettableFuture<Metadata> trailers = SettableFuture.create();

        private final SettableFuture<Status> status = SettableFuture.create();

        private boolean awaitOnReady(int timeout, TimeUnit unit) throws Exception {
            return (readyQueue.poll(timeout, unit)) != null;
        }

        private boolean awaitOnReadyAndDrain(int timeout, TimeUnit unit) throws Exception {
            if (!(awaitOnReady(timeout, unit))) {
                return false;
            }
            // Throw the rest away
            readyQueue.drainTo(Lists.newArrayList());
            return true;
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            if (status.isDone()) {
                Assert.fail("messagesAvailable invoked after closed");
            }
            InputStream message;
            while ((message = producer.next()) != null) {
                messageQueue.add(message);
            } 
        }

        @Override
        public void onReady() {
            if (status.isDone()) {
                Assert.fail("onReady invoked after closed");
            }
            readyQueue.add(new Object());
        }

        @Override
        public void headersRead(Metadata headers) {
            if (status.isDone()) {
                Assert.fail("headersRead invoked after closed");
            }
            this.headers.set(headers);
        }

        @Override
        public void closed(Status status, Metadata trailers) {
            closed(status, PROCESSED, trailers);
        }

        @Override
        public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
            if (this.status.isDone()) {
                Assert.fail("headersRead invoked after closed");
            }
            this.status.set(status);
            this.trailers.set(trailers);
        }
    }

    private static class StreamCreation {
        public final ServerStream stream;

        public final String method;

        public final Metadata headers;

        public final AbstractTransportTest.ServerStreamListenerBase listener;

        public StreamCreation(ServerStream stream, String method, Metadata headers, AbstractTransportTest.ServerStreamListenerBase listener) {
            this.stream = stream;
            this.method = method;
            this.headers = headers;
            this.listener = listener;
        }
    }

    private static class StringMarshaller implements MethodDescriptor.Marshaller<String> {
        public static final AbstractTransportTest.StringMarshaller INSTANCE = new AbstractTransportTest.StringMarshaller();

        @Override
        public InputStream stream(String value) {
            return new ByteArrayInputStream(value.getBytes(Charsets.UTF_8));
        }

        @Override
        public String parse(InputStream stream) {
            try {
                return new String(IoUtils.toByteArray(stream), Charsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class StringBinaryMarshaller implements Metadata.BinaryMarshaller<String> {
        public static final AbstractTransportTest.StringBinaryMarshaller INSTANCE = new AbstractTransportTest.StringBinaryMarshaller();

        @Override
        public byte[] toBytes(String value) {
            return value.getBytes(Charsets.UTF_8);
        }

        @Override
        public String parseBytes(byte[] serialized) {
            return new String(serialized, Charsets.UTF_8);
        }
    }
}

