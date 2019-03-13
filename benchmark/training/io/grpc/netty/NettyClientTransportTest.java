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


import Attributes.Key;
import CallOptions.DEFAULT;
import ChannelOption.SO_KEEPALIVE;
import ChannelOption.SO_LINGER;
import ClientAuth.REQUIRE;
import ClientTransport.PingCallback;
import Code.RESOURCE_EXHAUSTED;
import Grpc.TRANSPORT_ATTR_LOCAL_ADDR;
import Grpc.TRANSPORT_ATTR_REMOTE_ADDR;
import Grpc.TRANSPORT_ATTR_SSL_SESSION;
import GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE;
import ManagedClientTransport.Listener;
import MethodDescriptor.MethodType.UNARY;
import RpcProgress.PROCESSED;
import Status.Code.INTERNAL;
import Status.OK;
import Status.UNAVAILABLE;
import SupportedCipherSuiteFilter.INSTANCE;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.ChannelLogger;
import io.grpc.InternalChannelz;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.internal.ClientStream;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.ClientTransport;
import io.grpc.internal.FakeClock;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerStreamListener;
import io.grpc.internal.ServerTransport;
import io.grpc.internal.ServerTransportListener;
import io.grpc.internal.TransportTracer;
import io.grpc.internal.testing.TestUtils;
import io.grpc.netty.NettyChannelBuilder.LocalSocketPicker;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.StreamBufferingEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.AsciiString;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.net.ssl.SSLHandshakeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import static Utils.HTTP;


/**
 * Tests for {@link NettyClientTransport}.
 */
@RunWith(JUnit4.class)
public class NettyClientTransportTest {
    private static final SslContext SSL_CONTEXT = NettyClientTransportTest.createSslContext();

    @Mock
    private Listener clientTransportListener;

    private final List<NettyClientTransport> transports = new ArrayList<>();

    private final LinkedBlockingQueue<Attributes> serverTransportAttributesList = new LinkedBlockingQueue<>();

    private final NioEventLoopGroup group = new NioEventLoopGroup(1);

    private final NettyClientTransportTest.EchoServerListener serverListener = new NettyClientTransportTest.EchoServerListener();

    private final InternalChannelz channelz = new InternalChannelz();

    private Runnable tooManyPingsRunnable = new Runnable() {
        // Throwing is useless in this method, because Netty doesn't propagate the exception
        @Override
        public void run() {
        }
    };

    private Attributes eagAttributes = Attributes.EMPTY;

    private ProtocolNegotiator negotiator = ProtocolNegotiators.serverTls(NettyClientTransportTest.SSL_CONTEXT);

    private InetSocketAddress address;

    private String authority;

    private NettyServer server;

    @Test
    public void testToString() throws Exception {
        address = TestUtils.testServerAddress(new InetSocketAddress(12345));
        authority = GrpcUtil.authorityFromHostAndPort(address.getHostString(), address.getPort());
        String s = newTransport(newNegotiator()).toString();
        transports.clear();
        Assert.assertTrue(("Unexpected: " + s), s.contains("NettyClientTransport"));
        Assert.assertTrue(("Unexpected: " + s), s.contains(address.toString()));
    }

    @Test
    public void addDefaultUserAgent() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator());
        callMeMaybe(transport.start(clientTransportListener));
        // Send a single RPC and wait for the response.
        new NettyClientTransportTest.Rpc(transport).halfClose().waitForResponse();
        // Verify that the received headers contained the User-Agent.
        Assert.assertEquals(1, serverListener.streamListeners.size());
        Metadata headers = serverListener.streamListeners.get(0).headers;
        Assert.assertEquals(GrpcUtil.getGrpcUserAgent("netty", null), headers.get(USER_AGENT_KEY));
    }

    @Test
    public void setSoLingerChannelOption() throws IOException {
        startServer();
        Map<ChannelOption<?>, Object> channelOptions = new HashMap<>();
        // set SO_LINGER option
        int soLinger = 123;
        channelOptions.put(SO_LINGER, soLinger);
        NettyClientTransport transport = /* user agent */
        new NettyClientTransport(address, new io.netty.channel.ReflectiveChannelFactory(NioSocketChannel.class), channelOptions, group, newNegotiator(), Http2CodecUtil.DEFAULT_WINDOW_SIZE, DEFAULT_MAX_MESSAGE_SIZE, GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE, KEEPALIVE_TIME_NANOS_DISABLED, 1L, false, authority, null, tooManyPingsRunnable, new TransportTracer(), Attributes.EMPTY, new NettyClientTransportTest.SocketPicker(), new NettyClientTransportTest.FakeChannelLogger());
        transports.add(transport);
        callMeMaybe(transport.start(clientTransportListener));
        // verify SO_LINGER has been set
        ChannelConfig config = transport.channel().config();
        Assert.assertTrue((config instanceof SocketChannelConfig));
        Assert.assertEquals(soLinger, getSoLinger());
    }

    @Test
    public void overrideDefaultUserAgent() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, DEFAULT_MAX_HEADER_LIST_SIZE, "testUserAgent", true);
        callMeMaybe(transport.start(clientTransportListener));
        new NettyClientTransportTest.Rpc(transport, new Metadata()).halfClose().waitForResponse();
        // Verify that the received headers contained the User-Agent.
        Assert.assertEquals(1, serverListener.streamListeners.size());
        Metadata receivedHeaders = serverListener.streamListeners.get(0).headers;
        Assert.assertEquals(GrpcUtil.getGrpcUserAgent("netty", "testUserAgent"), receivedHeaders.get(USER_AGENT_KEY));
    }

    @Test
    public void maxMessageSizeShouldBeEnforced() throws Throwable {
        startServer();
        // Allow the response payloads of up to 1 byte.
        NettyClientTransport transport = newTransport(newNegotiator(), 1, DEFAULT_MAX_HEADER_LIST_SIZE, null, true);
        callMeMaybe(transport.start(clientTransportListener));
        try {
            // Send a single RPC and wait for the response.
            new NettyClientTransportTest.Rpc(transport).halfClose().waitForResponse();
            Assert.fail("Expected the stream to fail.");
        } catch (ExecutionException e) {
            Status status = Status.fromThrowable(e);
            Assert.assertEquals(RESOURCE_EXHAUSTED, status.getCode());
            Assert.assertTrue(("Missing exceeds maximum from: " + (status.getDescription())), status.getDescription().contains("exceeds maximum"));
        }
    }

    /**
     * Verifies that we can create multiple TLS client transports from the same builder.
     */
    @Test
    public void creatingMultipleTlsTransportsShouldSucceed() throws Exception {
        startServer();
        // Create a couple client transports.
        ProtocolNegotiator negotiator = newNegotiator();
        for (int index = 0; index < 2; ++index) {
            NettyClientTransport transport = newTransport(negotiator);
            callMeMaybe(transport.start(clientTransportListener));
        }
        // Send a single RPC on each transport.
        final List<NettyClientTransportTest.Rpc> rpcs = new ArrayList(transports.size());
        for (NettyClientTransport transport : transports) {
            rpcs.add(new NettyClientTransportTest.Rpc(transport).halfClose());
        }
        // Wait for the RPCs to complete.
        for (NettyClientTransportTest.Rpc rpc : rpcs) {
            rpc.waitForResponse();
        }
    }

    @Test
    public void negotiationFailurePropagatesToStatus() throws Exception {
        negotiator = ProtocolNegotiators.serverPlaintext();
        startServer();
        final NettyClientTransportTest.NoopProtocolNegotiator negotiator = new NettyClientTransportTest.NoopProtocolNegotiator();
        final NettyClientTransport transport = newTransport(negotiator);
        callMeMaybe(transport.start(clientTransportListener));
        final Status failureStatus = UNAVAILABLE.withDescription("oh noes!");
        transport.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                negotiator.handler.fail(transport.channel().pipeline().context(negotiator.handler), failureStatus.asRuntimeException());
            }
        });
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        try {
            rpc.waitForClose();
            Assert.fail("expected exception");
        } catch (ExecutionException ex) {
            Assert.assertSame(failureStatus, getStatus());
        }
    }

    @Test
    public void tlsNegotiationFailurePropagatesToStatus() throws Exception {
        File serverCert = TestUtils.loadCert("server1.pem");
        File serverKey = TestUtils.loadCert("server1.key");
        // Don't trust ca.pem, so that client auth fails
        SslContext sslContext = GrpcSslContexts.forServer(serverCert, serverKey).ciphers(TestUtils.preferredTestCiphers(), INSTANCE).clientAuth(REQUIRE).build();
        negotiator = ProtocolNegotiators.serverTls(sslContext);
        startServer();
        File caCert = TestUtils.loadCert("ca.pem");
        File clientCert = TestUtils.loadCert("client.pem");
        File clientKey = TestUtils.loadCert("client.key");
        SslContext clientContext = GrpcSslContexts.forClient().trustManager(caCert).ciphers(TestUtils.preferredTestCiphers(), INSTANCE).keyManager(clientCert, clientKey).build();
        ProtocolNegotiator negotiator = ProtocolNegotiators.tls(clientContext);
        final NettyClientTransport transport = newTransport(negotiator);
        callMeMaybe(transport.start(clientTransportListener));
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        try {
            rpc.waitForClose();
            Assert.fail("expected exception");
        } catch (ExecutionException ex) {
            StatusException sre = ((StatusException) (ex.getCause()));
            Assert.assertEquals(Status.Code.UNAVAILABLE, sre.getStatus().getCode());
            assertThat(sre.getCause()).isInstanceOf(SSLHandshakeException.class);
            assertThat(sre.getCause().getMessage()).contains("SSLV3_ALERT_HANDSHAKE_FAILURE");
        }
    }

    @Test
    public void channelExceptionDuringNegotiatonPropagatesToStatus() throws Exception {
        negotiator = ProtocolNegotiators.serverPlaintext();
        startServer();
        NettyClientTransportTest.NoopProtocolNegotiator negotiator = new NettyClientTransportTest.NoopProtocolNegotiator();
        NettyClientTransport transport = newTransport(negotiator);
        callMeMaybe(transport.start(clientTransportListener));
        final Status failureStatus = UNAVAILABLE.withDescription("oh noes!");
        transport.channel().pipeline().fireExceptionCaught(failureStatus.asRuntimeException());
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        try {
            rpc.waitForClose();
            Assert.fail("expected exception");
        } catch (ExecutionException ex) {
            Assert.assertSame(failureStatus, getStatus());
        }
    }

    @Test
    public void handlerExceptionDuringNegotiatonPropagatesToStatus() throws Exception {
        negotiator = ProtocolNegotiators.serverPlaintext();
        startServer();
        final NettyClientTransportTest.NoopProtocolNegotiator negotiator = new NettyClientTransportTest.NoopProtocolNegotiator();
        final NettyClientTransport transport = newTransport(negotiator);
        callMeMaybe(transport.start(clientTransportListener));
        final Status failureStatus = UNAVAILABLE.withDescription("oh noes!");
        transport.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    negotiator.handler.exceptionCaught(transport.channel().pipeline().context(negotiator.handler), failureStatus.asRuntimeException());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        try {
            rpc.waitForClose();
            Assert.fail("expected exception");
        } catch (ExecutionException ex) {
            Assert.assertSame(failureStatus, getStatus());
        }
    }

    @Test
    public void bufferedStreamsShouldBeClosedWhenConnectionTerminates() throws Exception {
        // Only allow a single stream active at a time.
        startServer(1, DEFAULT_MAX_HEADER_LIST_SIZE);
        NettyClientTransport transport = newTransport(newNegotiator());
        callMeMaybe(transport.start(clientTransportListener));
        // Send a dummy RPC in order to ensure that the updated SETTINGS_MAX_CONCURRENT_STREAMS
        // has been received by the remote endpoint.
        new NettyClientTransportTest.Rpc(transport).halfClose().waitForResponse();
        // Create 3 streams, but don't half-close. The transport will buffer the second and third.
        NettyClientTransportTest.Rpc[] rpcs = new NettyClientTransportTest.Rpc[]{ new NettyClientTransportTest.Rpc(transport), new NettyClientTransportTest.Rpc(transport), new NettyClientTransportTest.Rpc(transport) };
        // Wait for the response for the stream that was actually created.
        rpcs[0].waitForResponse();
        // Now forcibly terminate the connection from the server side.
        serverListener.transports.get(0).channel().pipeline().firstContext().close();
        // Now wait for both listeners to be closed.
        for (int i = 1; i < (rpcs.length); i++) {
            try {
                rpcs[i].waitForClose();
                Assert.fail("Expected the RPC to fail");
            } catch (ExecutionException e) {
                // Expected.
                Throwable t = getRootCause(e);
                // Make sure that the Http2ChannelClosedException got replaced with the real cause of
                // the shutdown.
                Assert.assertFalse((t instanceof StreamBufferingEncoder.Http2ChannelClosedException));
            }
        }
    }

    public static class CantConstructChannel extends NioSocketChannel {
        /**
         * Constructor. It doesn't work. Feel free to try. But it doesn't work.
         */
        public CantConstructChannel() {
            // Use an Error because we've seen cases of channels failing to construct due to classloading
            // problems (like mixing different versions of Netty), and those involve Errors.
            throw new NettyClientTransportTest.CantConstructChannelError();
        }
    }

    private static class CantConstructChannelError extends Error {}

    @Test
    public void failingToConstructChannelShouldFailGracefully() throws Exception {
        address = TestUtils.testServerAddress(new InetSocketAddress(12345));
        authority = GrpcUtil.authorityFromHostAndPort(address.getHostString(), address.getPort());
        NettyClientTransport transport = new NettyClientTransport(address, new io.netty.channel.ReflectiveChannelFactory(NettyClientTransportTest.CantConstructChannel.class), new HashMap<ChannelOption<?>, Object>(), group, newNegotiator(), Http2CodecUtil.DEFAULT_WINDOW_SIZE, DEFAULT_MAX_MESSAGE_SIZE, GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE, KEEPALIVE_TIME_NANOS_DISABLED, 1, false, authority, null, tooManyPingsRunnable, new TransportTracer(), Attributes.EMPTY, new NettyClientTransportTest.SocketPicker(), new NettyClientTransportTest.FakeChannelLogger());
        transports.add(transport);
        // Should not throw
        callMeMaybe(transport.start(clientTransportListener));
        // And RPCs and PINGs should fail cleanly, reporting the failure
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport);
        try {
            rpc.waitForResponse();
            Assert.fail("Expected exception");
        } catch (Exception ex) {
            if (!((getRootCause(ex)) instanceof NettyClientTransportTest.CantConstructChannelError)) {
                throw new AssertionError("Could not find expected error", ex);
            }
        }
        final SettableFuture<Object> pingResult = SettableFuture.create();
        FakeClock clock = new FakeClock();
        ClientTransport.PingCallback pingCallback = new ClientTransport.PingCallback() {
            @Override
            public void onSuccess(long roundTripTimeNanos) {
                pingResult.set(roundTripTimeNanos);
            }

            @Override
            public void onFailure(Throwable cause) {
                pingResult.setException(cause);
            }
        };
        transport.ping(pingCallback, clock.getScheduledExecutorService());
        Assert.assertFalse(pingResult.isDone());
        clock.runDueTasks();
        Assert.assertTrue(pingResult.isDone());
        try {
            pingResult.get();
            Assert.fail("Expected exception");
        } catch (Exception ex) {
            if (!((getRootCause(ex)) instanceof NettyClientTransportTest.CantConstructChannelError)) {
                throw new AssertionError("Could not find expected error", ex);
            }
        }
    }

    @Test
    public void channelFactoryShouldSetSocketOptionKeepAlive() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, DEFAULT_MAX_HEADER_LIST_SIZE, "testUserAgent", true, new io.netty.channel.ReflectiveChannelFactory(NioSocketChannel.class));
        callMeMaybe(transport.start(clientTransportListener));
        assertThat(transport.channel().config().getOption(SO_KEEPALIVE)).isTrue();
    }

    @Test
    public void channelFactoryShouldNNotSetSocketOptionKeepAlive() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, DEFAULT_MAX_HEADER_LIST_SIZE, "testUserAgent", true, new io.netty.channel.ReflectiveChannelFactory(LocalChannel.class));
        callMeMaybe(transport.start(clientTransportListener));
        assertThat(transport.channel().config().getOption(SO_KEEPALIVE)).isNull();
    }

    @Test
    public void maxHeaderListSizeShouldBeEnforcedOnClient() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, 1, null, true);
        callMeMaybe(transport.start(clientTransportListener));
        try {
            // Send a single RPC and wait for the response.
            new NettyClientTransportTest.Rpc(transport, new Metadata()).halfClose().waitForResponse();
            Assert.fail(("The stream should have been failed due to client received header exceeds header list" + " size limit!"));
        } catch (Exception e) {
            Throwable rootCause = getRootCause(e);
            Status status = ((StatusException) (rootCause)).getStatus();
            Assert.assertEquals(INTERNAL, status.getCode());
            Assert.assertEquals("HTTP/2 error code: PROTOCOL_ERROR\nReceived Rst Stream", status.getDescription());
        }
    }

    @Test
    public void maxHeaderListSizeShouldBeEnforcedOnServer() throws Exception {
        startServer(100, 1);
        NettyClientTransport transport = newTransport(newNegotiator());
        callMeMaybe(transport.start(clientTransportListener));
        try {
            // Send a single RPC and wait for the response.
            new NettyClientTransportTest.Rpc(transport, new Metadata()).halfClose().waitForResponse();
            Assert.fail(("The stream should have been failed due to server received header exceeds header list" + " size limit!"));
        } catch (Exception e) {
            Status status = Status.fromThrowable(e);
            Assert.assertEquals(status.toString(), INTERNAL, status.getCode());
        }
    }

    @Test
    public void getAttributes_negotiatorHandler() throws Exception {
        address = TestUtils.testServerAddress(new InetSocketAddress(12345));
        authority = GrpcUtil.authorityFromHostAndPort(address.getHostString(), address.getPort());
        NettyClientTransport transport = newTransport(new NettyClientTransportTest.NoopProtocolNegotiator());
        callMeMaybe(transport.start(clientTransportListener));
        Assert.assertNotNull(transport.getAttributes());
    }

    @Test
    public void getEagAttributes_negotiatorHandler() throws Exception {
        address = TestUtils.testServerAddress(new InetSocketAddress(12345));
        authority = GrpcUtil.authorityFromHostAndPort(address.getHostString(), address.getPort());
        NettyClientTransportTest.NoopProtocolNegotiator npn = new NettyClientTransportTest.NoopProtocolNegotiator();
        eagAttributes = Attributes.newBuilder().set(Key.create("trash"), "value").build();
        NettyClientTransport transport = newTransport(npn);
        callMeMaybe(transport.start(clientTransportListener));
        // EAG Attributes are available before the negotiation is complete
        Assert.assertSame(eagAttributes, npn.grpcHandler.getEagAttributes());
    }

    @Test
    public void clientStreamGetsAttributes() throws Exception {
        startServer();
        NettyClientTransport transport = newTransport(newNegotiator());
        callMeMaybe(transport.start(clientTransportListener));
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        rpc.waitForResponse();
        Assert.assertNotNull(rpc.stream.getAttributes().get(TRANSPORT_ATTR_SSL_SESSION));
        Assert.assertEquals(address, rpc.stream.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR));
        Attributes serverTransportAttrs = serverTransportAttributesList.poll(1, TimeUnit.SECONDS);
        Assert.assertNotNull(serverTransportAttrs);
        SocketAddress clientAddr = serverTransportAttrs.get(TRANSPORT_ATTR_REMOTE_ADDR);
        Assert.assertNotNull(clientAddr);
        Assert.assertEquals(clientAddr, rpc.stream.getAttributes().get(TRANSPORT_ATTR_LOCAL_ADDR));
    }

    @Test
    public void keepAliveEnabled() throws Exception {
        startServer();
        NettyClientTransport transport = /* user agent */
        /* keep alive */
        newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, DEFAULT_MAX_HEADER_LIST_SIZE, null, true);
        callMeMaybe(transport.start(clientTransportListener));
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        rpc.waitForResponse();
        Assert.assertNotNull(transport.keepAliveManager());
    }

    @Test
    public void keepAliveDisabled() throws Exception {
        startServer();
        NettyClientTransport transport = /* user agent */
        /* keep alive */
        newTransport(newNegotiator(), DEFAULT_MAX_MESSAGE_SIZE, DEFAULT_MAX_HEADER_LIST_SIZE, null, false);
        callMeMaybe(transport.start(clientTransportListener));
        NettyClientTransportTest.Rpc rpc = new NettyClientTransportTest.Rpc(transport).halfClose();
        rpc.waitForResponse();
        Assert.assertNull(transport.keepAliveManager());
    }

    private static class Rpc {
        static final String MESSAGE = "hello";

        static final MethodDescriptor<String, String> METHOD = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("testService/test").setRequestMarshaller(NettyClientTransportTest.StringMarshaller.INSTANCE).setResponseMarshaller(NettyClientTransportTest.StringMarshaller.INSTANCE).build();

        final ClientStream stream;

        final NettyClientTransportTest.TestClientStreamListener listener = new NettyClientTransportTest.TestClientStreamListener();

        Rpc(NettyClientTransport transport) {
            this(transport, new Metadata());
        }

        Rpc(NettyClientTransport transport, Metadata headers) {
            stream = transport.newStream(NettyClientTransportTest.Rpc.METHOD, headers, DEFAULT);
            stream.start(listener);
            stream.request(1);
            stream.writeMessage(new ByteArrayInputStream(NettyClientTransportTest.Rpc.MESSAGE.getBytes(Charsets.UTF_8)));
            stream.flush();
        }

        NettyClientTransportTest.Rpc halfClose() {
            stream.halfClose();
            return this;
        }

        void waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
            listener.responseFuture.get(10, TimeUnit.SECONDS);
        }

        void waitForClose() throws InterruptedException, ExecutionException, TimeoutException {
            listener.closedFuture.get(10, TimeUnit.SECONDS);
        }
    }

    private static final class TestClientStreamListener implements ClientStreamListener {
        final SettableFuture<Void> closedFuture = SettableFuture.create();

        final SettableFuture<Void> responseFuture = SettableFuture.create();

        @Override
        public void headersRead(Metadata headers) {
        }

        @Override
        public void closed(Status status, Metadata trailers) {
            closed(status, PROCESSED, trailers);
        }

        @Override
        public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
            if (status.isOk()) {
                closedFuture.set(null);
            } else {
                StatusException e = status.asException();
                closedFuture.setException(e);
                responseFuture.setException(e);
            }
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            if ((producer.next()) != null) {
                responseFuture.set(null);
            }
        }

        @Override
        public void onReady() {
        }
    }

    private static final class EchoServerStreamListener implements ServerStreamListener {
        final ServerStream stream;

        final String method;

        final Metadata headers;

        EchoServerStreamListener(ServerStream stream, String method, Metadata headers) {
            this.stream = stream;
            this.method = method;
            this.headers = headers;
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            InputStream message;
            while ((message = producer.next()) != null) {
                // Just echo back the message.
                stream.writeMessage(message);
                stream.flush();
            } 
        }

        @Override
        public void onReady() {
        }

        @Override
        public void halfClosed() {
            // Just close when the client closes.
            stream.close(OK, new Metadata());
        }

        @Override
        public void closed(Status status) {
        }
    }

    private final class EchoServerListener implements ServerListener {
        final List<NettyServerTransport> transports = new ArrayList<>();

        final List<NettyClientTransportTest.EchoServerStreamListener> streamListeners = Collections.synchronizedList(new ArrayList<NettyClientTransportTest.EchoServerStreamListener>());

        @Override
        public ServerTransportListener transportCreated(final ServerTransport transport) {
            transports.add(((NettyServerTransport) (transport)));
            return new ServerTransportListener() {
                @Override
                public void streamCreated(ServerStream stream, String method, Metadata headers) {
                    NettyClientTransportTest.EchoServerStreamListener listener = new NettyClientTransportTest.EchoServerStreamListener(stream, method, headers);
                    stream.setListener(listener);
                    stream.writeHeaders(new Metadata());
                    stream.request(1);
                    streamListeners.add(listener);
                }

                @Override
                public Attributes transportReady(Attributes transportAttrs) {
                    serverTransportAttributesList.add(transportAttrs);
                    return transportAttrs;
                }

                @Override
                public void transportTerminated() {
                }
            };
        }

        @Override
        public void serverShutdown() {
        }
    }

    private static final class StringMarshaller implements Marshaller<String> {
        static final NettyClientTransportTest.StringMarshaller INSTANCE = new NettyClientTransportTest.StringMarshaller();

        @Override
        public InputStream stream(String value) {
            return new ByteArrayInputStream(value.getBytes(Charsets.UTF_8));
        }

        @Override
        public String parse(InputStream stream) {
            try {
                return new String(ByteStreams.toByteArray(stream), Charsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class NoopHandler extends ProtocolNegotiators.AbstractBufferingHandler {
        public NoopHandler(GrpcHttp2ConnectionHandler grpcHandler) {
            super(grpcHandler);
        }
    }

    private static class NoopProtocolNegotiator implements ProtocolNegotiator {
        GrpcHttp2ConnectionHandler grpcHandler;

        NettyClientTransportTest.NoopHandler handler;

        @Override
        public ChannelHandler newHandler(final GrpcHttp2ConnectionHandler grpcHandler) {
            this.grpcHandler = grpcHandler;
            return handler = new NettyClientTransportTest.NoopHandler(grpcHandler);
        }

        @Override
        public AsciiString scheme() {
            return HTTP;
        }

        @Override
        public void close() {
        }
    }

    private static final class SocketPicker extends LocalSocketPicker {
        @Nullable
        @Override
        public SocketAddress createSocketAddress(SocketAddress remoteAddress, Attributes attrs) {
            return null;
        }
    }

    private static final class FakeChannelLogger extends ChannelLogger {
        @Override
        public void log(ChannelLogLevel level, String message) {
        }

        @Override
        public void log(ChannelLogLevel level, String messageFormat, Object... args) {
        }
    }
}

