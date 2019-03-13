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
package io.grpc.okhttp;


import AbstractStream.TransportState;
import ByteString.EMPTY;
import CallOptions.DEFAULT;
import Code.RESOURCE_EXHAUSTED;
import ErrorCode.CANCEL;
import ErrorCode.ENHANCE_YOUR_CALM;
import ErrorCode.FLOW_CONTROL_ERROR;
import ErrorCode.NO_ERROR;
import ErrorCode.PROTOCOL_ERROR;
import ErrorCode.REFUSED_STREAM;
import GrpcUtil.USER_AGENT_KEY;
import HeadersMode.HTTP_20_HEADERS;
import ManagedClientTransport.Listener;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import MethodType.BIDI_STREAMING;
import MethodType.CLIENT_STREAMING;
import MethodType.SERVER_STREAMING;
import Status.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import Status.OK;
import Status.UNAVAILABLE;
import Status.UNKNOWN;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.HttpConnectProxiedSocketAddress;
import io.grpc.InternalChannelz.TransportStats;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.ClientTransport;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.ManagedClientTransport;
import io.grpc.internal.TransportTracer;
import io.grpc.okhttp.internal.ConnectionSpec;
import io.grpc.okhttp.internal.framed.ErrorCode;
import io.grpc.okhttp.internal.framed.FrameReader;
import io.grpc.okhttp.internal.framed.FrameWriter;
import io.grpc.okhttp.internal.framed.Header;
import io.grpc.okhttp.internal.framed.Settings;
import io.grpc.testing.TestMethodDescriptors;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okio.Buffer;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static OkHttpChannelBuilder.INTERNAL_DEFAULT_CONNECTION_SPEC;


/**
 * Tests for {@link OkHttpClientTransport}.
 */
@RunWith(JUnit4.class)
public class OkHttpClientTransportTest {
    private static final int TIME_OUT_MS = 2000;

    private static final int INITIAL_WINDOW_SIZE = 65535;

    private static final String NETWORK_ISSUE_MESSAGE = "network issue";

    private static final String ERROR_MESSAGE = "simulated error";

    // The gRPC header length, which includes 1 byte compression flag and 4 bytes message length.
    private static final int HEADER_LENGTH = 5;

    private static final Status SHUTDOWN_REASON = UNAVAILABLE.withDescription("for test");

    private static final HttpConnectProxiedSocketAddress NO_PROXY = null;

    private static final int DEFAULT_START_STREAM_ID = 3;

    private static final int DEFAULT_MAX_INBOUND_METADATA_SIZE = Integer.MAX_VALUE;

    private static final Attributes EAG_ATTRS = Attributes.EMPTY;

    @Rule
    public final Timeout globalTimeout = Timeout.seconds(10);

    private FrameWriter frameWriter;

    private MethodDescriptor<Void, Void> method = TestMethodDescriptors.voidMethod();

    @Mock
    private Listener transportListener;

    private final SocketFactory socketFactory = null;

    private final SSLSocketFactory sslSocketFactory = null;

    private final HostnameVerifier hostnameVerifier = null;

    private final TransportTracer transportTracer = new TransportTracer();

    private final Queue<Buffer> capturedBuffer = new ArrayDeque<>();

    private OkHttpClientTransport clientTransport;

    private OkHttpClientTransportTest.MockFrameReader frameReader;

    private Socket socket;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private long nanoTime;// backs a ticker, for testing ping round-trip time measurement


    private SettableFuture<Void> connectedFuture;

    private OkHttpClientTransportTest.DelayConnectedCallback delayConnectedCallback;

    private Runnable tooManyPingsRunnable = new Runnable() {
        @Override
        public void run() {
            throw new AssertionError();
        }
    };

    @Test
    public void testToString() throws Exception {
        InetSocketAddress address = InetSocketAddress.createUnresolved("hostname", 31415);
        clientTransport = /* agent= */
        new OkHttpClientTransport(address, "hostname", null, OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, INTERNAL_DEFAULT_CONNECTION_SPEC, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, OkHttpClientTransportTest.NO_PROXY, tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, transportTracer);
        String s = clientTransport.toString();
        Assert.assertTrue(("Unexpected: " + s), s.contains("OkHttpClientTransport"));
        Assert.assertTrue(("Unexpected: " + s), s.contains(address.toString()));
    }

    @Test
    public void maxMessageSizeShouldBeEnforced() throws Exception {
        // Allow the response payloads of up to 1 byte.
        startTransport(3, null, true, 1, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, null);
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        assertContainStream(3);
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        Assert.assertNotNull(listener.headers);
        // Receive the message.
        final String message = "Hello Client";
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(message);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        listener.waitUntilStreamClosed();
        Assert.assertEquals(RESOURCE_EXHAUSTED, listener.status.getCode());
        shutdownAndVerify();
    }

    /**
     * When nextFrame throws IOException, the transport should be aborted.
     */
    @Test
    public void nextFrameThrowIoException() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        stream1.request(1);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        stream2.request(1);
        Assert.assertEquals(2, activeStreamCount());
        assertContainStream(3);
        assertContainStream(5);
        frameReader.throwIoExceptionForNextFrame();
        listener1.waitUntilStreamClosed();
        listener2.waitUntilStreamClosed();
        Assert.assertEquals(0, activeStreamCount());
        Assert.assertEquals(INTERNAL.getCode(), listener1.status.getCode());
        Assert.assertEquals(OkHttpClientTransportTest.NETWORK_ISSUE_MESSAGE, listener1.status.getCause().getMessage());
        Assert.assertEquals(INTERNAL.getCode(), listener2.status.getCode());
        Assert.assertEquals(OkHttpClientTransportTest.NETWORK_ISSUE_MESSAGE, listener2.status.getCause().getMessage());
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    /**
     * Test that even if an Error is thrown from the reading loop of the transport,
     * it can still clean up and call transportShutdown() and transportTerminated() as expected
     * by the channel.
     */
    @Test
    public void nextFrameThrowsError() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        Assert.assertEquals(1, activeStreamCount());
        assertContainStream(3);
        frameReader.throwErrorForNextFrame();
        listener.waitUntilStreamClosed();
        Assert.assertEquals(0, activeStreamCount());
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertEquals(OkHttpClientTransportTest.ERROR_MESSAGE, listener.status.getCause().getMessage());
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void nextFrameReturnFalse() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        frameReader.nextFrameAtEndOfStream();
        listener.waitUntilStreamClosed();
        Assert.assertEquals(UNAVAILABLE.getCode(), listener.status.getCode());
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void readMessages() throws Exception {
        initTransport();
        final int numMessages = 10;
        final String message = "Hello Client";
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(numMessages);
        assertContainStream(3);
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        Assert.assertNotNull(listener.headers);
        for (int i = 0; i < numMessages; i++) {
            Buffer buffer = OkHttpClientTransportTest.createMessageFrame((message + i));
            frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        }
        frameHandler().headers(true, true, 3, 0, grpcResponseTrailers(), HTTP_20_HEADERS);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(OK, listener.status);
        Assert.assertNotNull(listener.trailers);
        Assert.assertEquals(numMessages, listener.messages.size());
        for (int i = 0; i < numMessages; i++) {
            Assert.assertEquals((message + i), listener.messages.get(i));
        }
        shutdownAndVerify();
    }

    @Test
    public void receivedHeadersForInvalidStreamShouldKillConnection() throws Exception {
        initTransport();
        // Empty headers block without correct content type or status
        frameHandler().headers(false, false, 3, 0, new ArrayList<Header>(), HTTP_20_HEADERS);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).goAway(ArgumentMatchers.eq(0), ArgumentMatchers.eq(PROTOCOL_ERROR), ArgumentMatchers.any(byte[].class));
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void receivedDataForInvalidStreamShouldKillConnection() throws Exception {
        initTransport();
        frameHandler().data(false, 3, OkHttpClientTransportTest.createMessageFrame(new String(new char[1000])), 1000);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).goAway(ArgumentMatchers.eq(0), ArgumentMatchers.eq(PROTOCOL_ERROR), ArgumentMatchers.any(byte[].class));
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void invalidInboundHeadersCancelStream() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        assertContainStream(3);
        // Headers block without correct content type or status
        frameHandler().headers(false, false, 3, 0, Arrays.asList(new Header("random", "4")), HTTP_20_HEADERS);
        // Now wait to receive 1000 bytes of data so we can have a better error message before
        // cancelling the streaam.
        frameHandler().data(false, 3, OkHttpClientTransportTest.createMessageFrame(new String(new char[1000])), 1000);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        Assert.assertNull(listener.headers);
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertNotNull(listener.trailers);
        Assert.assertEquals("4", listener.trailers.get(Key.of("random", ASCII_STRING_MARSHALLER)));
        shutdownAndVerify();
    }

    @Test
    public void invalidInboundTrailersPropagateToMetadata() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        assertContainStream(3);
        // Headers block with EOS without correct content type or status
        frameHandler().headers(true, true, 3, 0, Arrays.asList(new Header("random", "4")), HTTP_20_HEADERS);
        Assert.assertNull(listener.headers);
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertNotNull(listener.trailers);
        Assert.assertEquals("4", listener.trailers.get(Key.of("random", ASCII_STRING_MARSHALLER)));
        shutdownAndVerify();
    }

    @Test
    public void readStatus() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        assertContainStream(3);
        frameHandler().headers(true, true, 3, 0, grpcResponseTrailers(), HTTP_20_HEADERS);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(Status.Code.OK, listener.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void receiveReset() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        assertContainStream(3);
        frameHandler().rstStream(3, PROTOCOL_ERROR);
        listener.waitUntilStreamClosed();
        assertThat(listener.status.getDescription()).contains("Rst Stream");
        assertThat(listener.status.getCode()).isEqualTo(Code.INTERNAL);
        shutdownAndVerify();
    }

    @Test
    public void receiveResetNoError() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        assertContainStream(3);
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame("a message");
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        frameHandler().headers(true, true, 3, 0, grpcResponseTrailers(), HTTP_20_HEADERS);
        frameHandler().rstStream(3, NO_ERROR);
        stream.request(1);
        listener.waitUntilStreamClosed();
        Assert.assertTrue(listener.status.isOk());
        shutdownAndVerify();
    }

    @Test
    public void cancelStream() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        getStream(3).cancel(CANCELLED);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        listener.waitUntilStreamClosed();
        Assert.assertEquals(OkHttpClientTransport.toGrpcStatus(CANCEL).getCode(), listener.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void addDefaultUserAgent() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        Header userAgentHeader = new Header(USER_AGENT_KEY.name(), GrpcUtil.getGrpcUserAgent("okhttp", null));
        List<Header> expectedHeaders = Arrays.asList(Headers.SCHEME_HEADER, Headers.METHOD_HEADER, new Header(Header.TARGET_AUTHORITY, "notarealauthority:80"), new Header(Header.TARGET_PATH, ("/" + (method.getFullMethodName()))), userAgentHeader, Headers.CONTENT_TYPE_HEADER, Headers.TE_HEADER);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).synStream(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), ArgumentMatchers.eq(expectedHeaders));
        getStream(3).cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void overrideDefaultUserAgent() throws Exception {
        startTransport(3, null, true, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, "fakeUserAgent");
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        List<Header> expectedHeaders = Arrays.asList(Headers.SCHEME_HEADER, Headers.METHOD_HEADER, new Header(Header.TARGET_AUTHORITY, "notarealauthority:80"), new Header(Header.TARGET_PATH, ("/" + (method.getFullMethodName()))), new Header(USER_AGENT_KEY.name(), GrpcUtil.getGrpcUserAgent("okhttp", "fakeUserAgent")), Headers.CONTENT_TYPE_HEADER, Headers.TE_HEADER);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).synStream(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), ArgumentMatchers.eq(expectedHeaders));
        getStream(3).cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void cancelStreamForDeadlineExceeded() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        getStream(3).cancel(DEADLINE_EXCEEDED);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        listener.waitUntilStreamClosed();
        shutdownAndVerify();
    }

    @Test
    public void writeMessage() throws Exception {
        initTransport();
        final String message = "Hello Server";
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        InputStream input = new ByteArrayInputStream(message.getBytes(Charsets.UTF_8));
        Assert.assertEquals(12, input.available());
        stream.writeMessage(input);
        stream.flush();
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq((12 + (OkHttpClientTransportTest.HEADER_LENGTH))));
        Buffer sentFrame = capturedBuffer.poll();
        Assert.assertEquals(OkHttpClientTransportTest.createMessageFrame(message), sentFrame);
        stream.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void transportTracer_windowSizeDefault() throws Exception {
        initTransport();
        TransportStats stats = OkHttpClientTransportTest.getTransportStats(clientTransport);
        Assert.assertEquals(OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, stats.remoteFlowControlWindow);
        // okhttp does not track local window sizes
        Assert.assertEquals((-1), stats.localFlowControlWindow);
    }

    @Test
    public void transportTracer_windowSize_remote() throws Exception {
        initTransport();
        TransportStats before = OkHttpClientTransportTest.getTransportStats(clientTransport);
        Assert.assertEquals(OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, before.remoteFlowControlWindow);
        // okhttp does not track local window sizes
        Assert.assertEquals((-1), before.localFlowControlWindow);
        frameHandler().windowUpdate(0, 1000);
        TransportStats after = OkHttpClientTransportTest.getTransportStats(clientTransport);
        Assert.assertEquals(((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) + 1000), after.remoteFlowControlWindow);
        // okhttp does not track local window sizes
        Assert.assertEquals((-1), after.localFlowControlWindow);
    }

    @Test
    public void windowUpdate() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        stream1.request(2);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        stream2.request(2);
        Assert.assertEquals(2, activeStreamCount());
        stream1 = getStream(3);
        stream2 = getStream(5);
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        frameHandler().headers(false, false, 5, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        int messageLength = (OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) / 4;
        byte[] fakeMessage = new byte[messageLength];
        // Stream 1 receives a message
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        int messageFrameLength = ((int) (buffer.size()));
        frameHandler().data(false, 3, buffer, messageFrameLength);
        // Stream 2 receives a message
        buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        frameHandler().data(false, 5, buffer, messageFrameLength);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(ArgumentMatchers.eq(0), ArgumentMatchers.eq((((long) (2)) * messageFrameLength)));
        Mockito.reset(frameWriter);
        // Stream 1 receives another message
        buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        frameHandler().data(false, 3, buffer, messageFrameLength);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(ArgumentMatchers.eq(3), ArgumentMatchers.eq((((long) (2)) * messageFrameLength)));
        // Stream 2 receives another message
        buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        frameHandler().data(false, 5, buffer, messageFrameLength);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(ArgumentMatchers.eq(5), ArgumentMatchers.eq((((long) (2)) * messageFrameLength)));
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(ArgumentMatchers.eq(0), ArgumentMatchers.eq((((long) (2)) * messageFrameLength)));
        stream1.cancel(CANCELLED);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        listener1.waitUntilStreamClosed();
        Assert.assertEquals(OkHttpClientTransport.toGrpcStatus(CANCEL).getCode(), listener1.status.getCode());
        stream2.cancel(CANCELLED);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(5), ArgumentMatchers.eq(CANCEL));
        listener2.waitUntilStreamClosed();
        Assert.assertEquals(OkHttpClientTransport.toGrpcStatus(CANCEL).getCode(), listener2.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void windowUpdateWithInboundFlowControl() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        int messageLength = ((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) / 2) + 1;
        byte[] fakeMessage = new byte[messageLength];
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        long messageFrameLength = buffer.size();
        frameHandler().data(false, 3, buffer, ((int) (messageFrameLength)));
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(idCaptor.capture(), ArgumentMatchers.eq(messageFrameLength));
        // Should only send window update for the connection.
        Assert.assertEquals(1, idCaptor.getAllValues().size());
        Assert.assertEquals(0, ((int) (idCaptor.getValue())));
        stream.request(1);
        // We return the bytes for the stream window as we read the message.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(ArgumentMatchers.eq(3), ArgumentMatchers.eq(messageFrameLength));
        getStream(3).cancel(CANCELLED);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        listener.waitUntilStreamClosed();
        Assert.assertEquals(OkHttpClientTransport.toGrpcStatus(CANCEL).getCode(), listener.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void outboundFlowControl() throws Exception {
        outboundFlowControl(OkHttpClientTransportTest.INITIAL_WINDOW_SIZE);
    }

    @Test
    public void outboundFlowControl_smallWindowSize() throws Exception {
        outboundFlowControl(100);
    }

    @Test
    public void outboundFlowControl_bigWindowSize() throws Exception {
        outboundFlowControl(((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) * 2));
    }

    @Test
    public void outboundFlowControlWithInitialWindowSizeChange() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        int messageLength = 20;
        setInitialWindowSize(((OkHttpClientTransportTest.HEADER_LENGTH) + 10));
        InputStream input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        // part of the message can be sent.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(((OkHttpClientTransportTest.HEADER_LENGTH) + 10)));
        // Avoid connection flow control.
        frameHandler().windowUpdate(0, ((OkHttpClientTransportTest.HEADER_LENGTH) + 10));
        // Increase initial window size
        setInitialWindowSize(((OkHttpClientTransportTest.HEADER_LENGTH) + 20));
        // The rest data should be sent.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(10));
        frameHandler().windowUpdate(0, 10);
        // Decrease initial window size to HEADER_LENGTH, since we've already sent
        // out HEADER_LENGTH + 20 bytes data, the window size should be -20 now.
        setInitialWindowSize(OkHttpClientTransportTest.HEADER_LENGTH);
        // Get 20 tokens back, still can't send any data.
        frameHandler().windowUpdate(3, 20);
        input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        // Only the previous two write operations happened.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS).times(2)).data(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.anyInt());
        // Get enough tokens to send the pending message.
        frameHandler().windowUpdate(3, ((OkHttpClientTransportTest.HEADER_LENGTH) + 20));
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(((OkHttpClientTransportTest.HEADER_LENGTH) + 20)));
        stream.cancel(CANCELLED);
        listener.waitUntilStreamClosed();
        shutdownAndVerify();
    }

    @Test
    public void outboundFlowControlWithInitialWindowSizeChangeInMiddleOfStream() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        int messageLength = 20;
        setInitialWindowSize(((OkHttpClientTransportTest.HEADER_LENGTH) + 10));
        InputStream input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        // part of the message can be sent.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(((OkHttpClientTransportTest.HEADER_LENGTH) + 10)));
        // Avoid connection flow control.
        frameHandler().windowUpdate(0, ((OkHttpClientTransportTest.HEADER_LENGTH) + 20));
        // Increase initial window size
        setInitialWindowSize(((OkHttpClientTransportTest.HEADER_LENGTH) + 20));
        // wait until pending frames sent (inOrder doesn't support timeout)
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS).atLeastOnce()).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(10));
        // It should ack the settings, then send remaining message.
        InOrder inOrder = Mockito.inOrder(frameWriter);
        inOrder.verify(frameWriter).ackSettings(ArgumentMatchers.any(Settings.class));
        inOrder.verify(frameWriter).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq(10));
        stream.cancel(CANCELLED);
        listener.waitUntilStreamClosed();
        shutdownAndVerify();
    }

    @Test
    public void stopNormally() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        Assert.assertEquals(2, activeStreamCount());
        clientTransport.shutdown(OkHttpClientTransportTest.SHUTDOWN_REASON);
        Assert.assertEquals(2, activeStreamCount());
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(OkHttpClientTransportTest.SHUTDOWN_REASON));
        stream1.cancel(CANCELLED);
        stream2.cancel(CANCELLED);
        listener1.waitUntilStreamClosed();
        listener2.waitUntilStreamClosed();
        Assert.assertEquals(0, activeStreamCount());
        Assert.assertEquals(CANCELLED.getCode(), listener1.status.getCode());
        Assert.assertEquals(CANCELLED.getCode(), listener2.status.getCode());
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).goAway(ArgumentMatchers.eq(0), ArgumentMatchers.eq(NO_ERROR), ((byte[]) (ArgumentMatchers.any())));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void receiveGoAway() throws Exception {
        initTransport();
        // start 2 streams.
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        stream1.request(1);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        stream2.request(1);
        Assert.assertEquals(2, activeStreamCount());
        // Receive goAway, max good id is 3.
        frameHandler().goAway(3, CANCEL, EMPTY);
        // Transport should be in STOPPING state.
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.never()).transportTerminated();
        // Stream 2 should be closed.
        listener2.waitUntilStreamClosed();
        Assert.assertEquals(1, activeStreamCount());
        Assert.assertEquals(CANCELLED.getCode(), listener2.status.getCode());
        // New stream should be failed.
        assertNewStreamFail();
        // But stream 1 should be able to send.
        final String sentMessage = "Should I also go away?";
        OkHttpClientStream stream = getStream(3);
        InputStream input = new ByteArrayInputStream(sentMessage.getBytes(Charsets.UTF_8));
        Assert.assertEquals(22, input.available());
        stream.writeMessage(input);
        stream.flush();
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq((22 + (OkHttpClientTransportTest.HEADER_LENGTH))));
        Buffer sentFrame = capturedBuffer.poll();
        Assert.assertEquals(OkHttpClientTransportTest.createMessageFrame(sentMessage), sentFrame);
        // And read.
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        final String receivedMessage = "No, you are fine.";
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(receivedMessage);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        frameHandler().headers(true, true, 3, 0, grpcResponseTrailers(), HTTP_20_HEADERS);
        listener1.waitUntilStreamClosed();
        Assert.assertEquals(1, listener1.messages.size());
        Assert.assertEquals(receivedMessage, listener1.messages.get(0));
        // The transport should be stopped after all active streams finished.
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void streamIdExhausted() throws Exception {
        int startId = (Integer.MAX_VALUE) - 2;
        initTransport(startId);
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        // New stream should be failed.
        assertNewStreamFail();
        // The alive stream should be functional, receives a message.
        frameHandler().headers(false, false, startId, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        Assert.assertNotNull(listener.headers);
        String message = "hello";
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(message);
        frameHandler().data(false, startId, buffer, ((int) (buffer.size())));
        getStream(startId).cancel(CANCELLED);
        // Receives the second message after be cancelled.
        buffer = OkHttpClientTransportTest.createMessageFrame(message);
        frameHandler().data(false, startId, buffer, ((int) (buffer.size())));
        listener.waitUntilStreamClosed();
        // Should only have the first message delivered.
        Assert.assertEquals(message, listener.messages.get(0));
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(startId), ArgumentMatchers.eq(CANCEL));
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void pendingStreamSucceed() throws Exception {
        initTransport();
        setMaxConcurrentStreams(1);
        final OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        final OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        // The second stream should be pending.
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        String sentMessage = "hello";
        InputStream input = new ByteArrayInputStream(sentMessage.getBytes(Charsets.UTF_8));
        Assert.assertEquals(5, input.available());
        stream2.writeMessage(input);
        stream2.flush();
        stream2.halfClose();
        waitForStreamPending(1);
        Assert.assertEquals(1, activeStreamCount());
        // Finish the first stream
        stream1.cancel(CANCELLED);
        listener1.waitUntilStreamClosed();
        // The second stream should be active now, and the pending data should be sent out.
        Assert.assertEquals(1, activeStreamCount());
        Assert.assertEquals(0, clientTransport.getPendingStreamSize());
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(true), ArgumentMatchers.eq(5), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq((5 + (OkHttpClientTransportTest.HEADER_LENGTH))));
        Buffer sentFrame = capturedBuffer.poll();
        Assert.assertEquals(OkHttpClientTransportTest.createMessageFrame(sentMessage), sentFrame);
        stream2.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void pendingStreamCancelled() throws Exception {
        initTransport();
        setMaxConcurrentStreams(0);
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        waitForStreamPending(1);
        stream.cancel(CANCELLED);
        // The second cancel should be an no-op.
        stream.cancel(UNKNOWN);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(0, clientTransport.getPendingStreamSize());
        Assert.assertEquals(CANCELLED.getCode(), listener.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void pendingStreamFailedByGoAway() throws Exception {
        initTransport();
        setMaxConcurrentStreams(1);
        final OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        final OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        // The second stream should be pending.
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        waitForStreamPending(1);
        Assert.assertEquals(1, activeStreamCount());
        // Receives GO_AWAY.
        frameHandler().goAway(99, CANCEL, EMPTY);
        listener2.waitUntilStreamClosed();
        Assert.assertEquals(CANCELLED.getCode(), listener2.status.getCode());
        Assert.assertEquals(0, clientTransport.getPendingStreamSize());
        // active stream should not be affected.
        Assert.assertEquals(1, activeStreamCount());
        getStream(3).cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void pendingStreamSucceedAfterShutdown() throws Exception {
        initTransport();
        setMaxConcurrentStreams(0);
        final OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        // The second stream should be pending.
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        waitForStreamPending(1);
        clientTransport.shutdown(OkHttpClientTransportTest.SHUTDOWN_REASON);
        setMaxConcurrentStreams(1);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).synStream(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(3), ArgumentMatchers.anyInt(), OkHttpClientTransportTest.anyListHeader());
        Assert.assertEquals(1, activeStreamCount());
        stream.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void pendingStreamFailedByIdExhausted() throws Exception {
        int startId = (Integer.MAX_VALUE) - 4;
        initTransport(startId);
        setMaxConcurrentStreams(1);
        final OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        final OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        final OkHttpClientTransportTest.MockStreamListener listener3 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        // The second and third stream should be pending.
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        OkHttpClientStream stream3 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream3.start(listener3);
        waitForStreamPending(2);
        Assert.assertEquals(1, activeStreamCount());
        // Now finish stream1, stream2 should be started and exhaust the id,
        // so stream3 should be failed.
        stream1.cancel(CANCELLED);
        listener1.waitUntilStreamClosed();
        listener3.waitUntilStreamClosed();
        Assert.assertEquals(UNAVAILABLE.getCode(), listener3.status.getCode());
        Assert.assertEquals(0, clientTransport.getPendingStreamSize());
        Assert.assertEquals(1, activeStreamCount());
        stream2 = getStream((startId + 2));
        stream2.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void receivingWindowExceeded() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        frameHandler().headers(false, false, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        int messageLength = (OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) + 1;
        byte[] fakeMessage = new byte[messageLength];
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(fakeMessage);
        int messageFrameLength = ((int) (buffer.size()));
        frameHandler().data(false, 3, buffer, messageFrameLength);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertEquals("Received data size exceeded our receiving window size", listener.status.getDescription());
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(FLOW_CONTROL_ERROR));
        shutdownAndVerify();
    }

    @Test
    public void unaryHeadersShouldNotBeFlushed() throws Exception {
        // By default the method is a Unary call
        shouldHeadersBeFlushed(false);
        shutdownAndVerify();
    }

    @Test
    public void serverStreamingHeadersShouldNotBeFlushed() throws Exception {
        method = method.toBuilder().setType(SERVER_STREAMING).build();
        shouldHeadersBeFlushed(false);
        shutdownAndVerify();
    }

    @Test
    public void clientStreamingHeadersShouldBeFlushed() throws Exception {
        method = method.toBuilder().setType(CLIENT_STREAMING).build();
        shouldHeadersBeFlushed(true);
        shutdownAndVerify();
    }

    @Test
    public void duplexStreamingHeadersShouldNotBeFlushed() throws Exception {
        method = method.toBuilder().setType(BIDI_STREAMING).build();
        shouldHeadersBeFlushed(true);
        shutdownAndVerify();
    }

    @Test
    public void receiveDataWithoutHeader() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(new byte[1]);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        // Trigger the failure by a trailer.
        frameHandler().headers(true, true, 3, 0, grpcResponseHeaders(), HTTP_20_HEADERS);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertTrue(listener.status.getDescription().startsWith("headers not received before payload"));
        Assert.assertEquals(0, listener.messages.size());
        shutdownAndVerify();
    }

    @Test
    public void receiveDataWithoutHeaderAndTrailer() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(new byte[1]);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        // Trigger the failure by a data frame.
        buffer = OkHttpClientTransportTest.createMessageFrame(new byte[1]);
        frameHandler().data(true, 3, buffer, ((int) (buffer.size())));
        listener.waitUntilStreamClosed();
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertTrue(listener.status.getDescription().startsWith("headers not received before payload"));
        Assert.assertEquals(0, listener.messages.size());
        shutdownAndVerify();
    }

    @Test
    public void receiveLongEnoughDataWithoutHeaderAndTrailer() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.request(1);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(new byte[1000]);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        // Once we receive enough detail, we cancel the stream. so we should have sent cancel.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).rstStream(ArgumentMatchers.eq(3), ArgumentMatchers.eq(CANCEL));
        listener.waitUntilStreamClosed();
        Assert.assertEquals(INTERNAL.getCode(), listener.status.getCode());
        Assert.assertTrue(listener.status.getDescription().startsWith("headers not received before payload"));
        Assert.assertEquals(0, listener.messages.size());
        shutdownAndVerify();
    }

    @Test
    public void receiveDataForUnknownStreamUpdateConnectionWindow() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.cancel(CANCELLED);
        Buffer buffer = OkHttpClientTransportTest.createMessageFrame(new byte[((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) / 2) + 1]);
        frameHandler().data(false, 3, buffer, ((int) (buffer.size())));
        // Should still update the connection window even stream 3 is gone.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).windowUpdate(0, (((OkHttpClientTransportTest.HEADER_LENGTH) + ((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) / 2)) + 1));
        buffer = OkHttpClientTransportTest.createMessageFrame(new byte[((OkHttpClientTransportTest.INITIAL_WINDOW_SIZE) / 2) + 1]);
        // This should kill the connection, since we never created stream 5.
        frameHandler().data(false, 5, buffer, ((int) (buffer.size())));
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).goAway(ArgumentMatchers.eq(0), ArgumentMatchers.eq(PROTOCOL_ERROR), ArgumentMatchers.any(byte[].class));
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void receiveWindowUpdateForUnknownStream() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        stream.cancel(CANCELLED);
        // This should be ignored.
        frameHandler().windowUpdate(3, 73);
        listener.waitUntilStreamClosed();
        // This should kill the connection, since we never created stream 5.
        frameHandler().windowUpdate(5, 73);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).goAway(ArgumentMatchers.eq(0), ArgumentMatchers.eq(PROTOCOL_ERROR), ArgumentMatchers.any(byte[].class));
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.isA(Status.class));
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        shutdownAndVerify();
    }

    @Test
    public void shouldBeInitiallyReady() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        Assert.assertTrue(stream.isReady());
        Assert.assertTrue(listener.isOnReadyCalled());
        stream.cancel(CANCELLED);
        Assert.assertFalse(stream.isReady());
        shutdownAndVerify();
    }

    @Test
    public void notifyOnReady() throws Exception {
        initTransport();
        // exactly one byte below the threshold
        int messageLength = ((TransportState.DEFAULT_ONREADY_THRESHOLD) - (OkHttpClientTransportTest.HEADER_LENGTH)) - 1;
        setInitialWindowSize(0);
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        Assert.assertTrue(stream.isReady());
        // Be notified at the beginning.
        Assert.assertTrue(listener.isOnReadyCalled());
        // Write a message that will not exceed the notification threshold and queue it.
        InputStream input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        Assert.assertTrue(stream.isReady());
        // Write another two messages, still be queued.
        input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        Assert.assertFalse(stream.isReady());
        input = new ByteArrayInputStream(new byte[messageLength]);
        stream.writeMessage(input);
        stream.flush();
        Assert.assertFalse(stream.isReady());
        // Let the first message out.
        frameHandler().windowUpdate(0, ((OkHttpClientTransportTest.HEADER_LENGTH) + messageLength));
        frameHandler().windowUpdate(3, ((OkHttpClientTransportTest.HEADER_LENGTH) + messageLength));
        Assert.assertFalse(stream.isReady());
        Assert.assertFalse(listener.isOnReadyCalled());
        // Let the second message out.
        frameHandler().windowUpdate(0, ((OkHttpClientTransportTest.HEADER_LENGTH) + messageLength));
        frameHandler().windowUpdate(3, ((OkHttpClientTransportTest.HEADER_LENGTH) + messageLength));
        Assert.assertTrue(stream.isReady());
        Assert.assertTrue(listener.isOnReadyCalled());
        stream.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void transportReady() throws Exception {
        initTransport();
        Mockito.verifyZeroInteractions(transportListener);
        frameHandler().settings(false, new Settings());
        Mockito.verify(transportListener).transportReady();
        shutdownAndVerify();
    }

    @Test
    public void ping() throws Exception {
        initTransport();
        OkHttpClientTransportTest.PingCallbackImpl callback1 = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback1, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        // add'l ping will be added as listener to outstanding operation
        OkHttpClientTransportTest.PingCallbackImpl callback2 = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback2, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        ArgumentCaptor<Integer> captor1 = ArgumentCaptor.forClass(int.class);
        ArgumentCaptor<Integer> captor2 = ArgumentCaptor.forClass(int.class);
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).ping(ArgumentMatchers.eq(false), captor1.capture(), captor2.capture());
        // callback not invoked until we see acknowledgement
        Assert.assertEquals(0, callback1.invocationCount);
        Assert.assertEquals(0, callback2.invocationCount);
        int payload1 = captor1.getValue();
        int payload2 = captor2.getValue();
        // getting a bad ack won't complete the future
        // to make the ack "bad", we modify the payload so it doesn't match
        frameHandler().ping(true, payload1, (payload2 - 1));
        // operation not complete because ack was wrong
        Assert.assertEquals(0, callback1.invocationCount);
        Assert.assertEquals(0, callback2.invocationCount);
        nanoTime += 10101;
        // reading the proper response should complete the future
        frameHandler().ping(true, payload1, payload2);
        Assert.assertEquals(1, callback1.invocationCount);
        Assert.assertEquals(10101, callback1.roundTripTime);
        Assert.assertNull(callback1.failureCause);
        // callback2 piggy-backed on same operation
        Assert.assertEquals(1, callback2.invocationCount);
        Assert.assertEquals(10101, callback2.roundTripTime);
        Assert.assertNull(callback2.failureCause);
        // now that previous ping is done, next request returns a different future
        callback1 = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback1, MoreExecutors.directExecutor());
        Assert.assertEquals(2, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        Assert.assertEquals(0, callback1.invocationCount);
        shutdownAndVerify();
    }

    @Test
    public void ping_failsWhenTransportShutdown() throws Exception {
        initTransport();
        OkHttpClientTransportTest.PingCallbackImpl callback = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        Assert.assertEquals(0, callback.invocationCount);
        clientTransport.shutdown(OkHttpClientTransportTest.SHUTDOWN_REASON);
        // ping failed on channel shutdown
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertTrue(((callback.failureCause) instanceof StatusException));
        Assert.assertSame(OkHttpClientTransportTest.SHUTDOWN_REASON, getStatus());
        // now that handler is in terminal state, all future pings fail immediately
        callback = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertTrue(((callback.failureCause) instanceof StatusException));
        Assert.assertSame(OkHttpClientTransportTest.SHUTDOWN_REASON, getStatus());
        shutdownAndVerify();
    }

    @Test
    public void ping_failsIfTransportFails() throws Exception {
        initTransport();
        OkHttpClientTransportTest.PingCallbackImpl callback = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        Assert.assertEquals(0, callback.invocationCount);
        clientTransport.onException(new IOException());
        // ping failed on error
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertTrue(((callback.failureCause) instanceof StatusException));
        Assert.assertEquals(Status.Code.UNAVAILABLE, getStatus().getCode());
        // now that handler is in terminal state, all future pings fail immediately
        callback = new OkHttpClientTransportTest.PingCallbackImpl();
        clientTransport.ping(callback, MoreExecutors.directExecutor());
        Assert.assertEquals(1, OkHttpClientTransportTest.getTransportStats(clientTransport).keepAlivesSent);
        Assert.assertEquals(1, callback.invocationCount);
        Assert.assertTrue(((callback.failureCause) instanceof StatusException));
        Assert.assertEquals(Status.Code.UNAVAILABLE, getStatus().getCode());
        shutdownAndVerify();
    }

    @Test
    public void writeBeforeConnected() throws Exception {
        initTransportAndDelayConnected();
        final String message = "Hello Server";
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        InputStream input = new ByteArrayInputStream(message.getBytes(Charsets.UTF_8));
        stream.writeMessage(input);
        stream.flush();
        // The message should be queued.
        Mockito.verifyNoMoreInteractions(frameWriter);
        allowTransportConnected();
        // The queued message should be sent out.
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).data(ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.any(Buffer.class), ArgumentMatchers.eq((12 + (OkHttpClientTransportTest.HEADER_LENGTH))));
        Buffer sentFrame = capturedBuffer.poll();
        Assert.assertEquals(OkHttpClientTransportTest.createMessageFrame(message), sentFrame);
        stream.cancel(CANCELLED);
        shutdownAndVerify();
    }

    @Test
    public void cancelBeforeConnected() throws Exception {
        initTransportAndDelayConnected();
        final String message = "Hello Server";
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        InputStream input = new ByteArrayInputStream(message.getBytes(Charsets.UTF_8));
        stream.writeMessage(input);
        stream.flush();
        stream.cancel(CANCELLED);
        Mockito.verifyNoMoreInteractions(frameWriter);
        allowTransportConnected();
        Mockito.verifyNoMoreInteractions(frameWriter);
        shutdownAndVerify();
    }

    @Test
    public void shutdownDuringConnecting() throws Exception {
        initTransportAndDelayConnected();
        OkHttpClientTransportTest.MockStreamListener listener = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(listener);
        clientTransport.shutdown(OkHttpClientTransportTest.SHUTDOWN_REASON);
        allowTransportConnected();
        // The new stream should be failed, but not the pending stream.
        assertNewStreamFail();
        Mockito.verify(frameWriter, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).synStream(ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(3), ArgumentMatchers.anyInt(), OkHttpClientTransportTest.anyListHeader());
        Assert.assertEquals(1, activeStreamCount());
        stream.cancel(CANCELLED);
        listener.waitUntilStreamClosed();
        Assert.assertEquals(CANCELLED.getCode(), listener.status.getCode());
        shutdownAndVerify();
    }

    @Test
    public void invalidAuthorityPropagates() {
        clientTransport = new OkHttpClientTransport(new InetSocketAddress("host", 1234), "invalid_authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, OkHttpClientTransportTest.NO_PROXY, tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, transportTracer);
        String host = clientTransport.getOverridenHost();
        int port = clientTransport.getOverridenPort();
        Assert.assertEquals("invalid_authority", host);
        Assert.assertEquals(1234, port);
    }

    @Test
    public void unreachableServer() throws Exception {
        clientTransport = new OkHttpClientTransport(new InetSocketAddress("localhost", 0), "authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, OkHttpClientTransportTest.NO_PROXY, tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, new TransportTracer());
        ManagedClientTransport.Listener listener = Mockito.mock(Listener.class);
        clientTransport.start(listener);
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(captor.capture());
        Status status = captor.getValue();
        Assert.assertEquals(UNAVAILABLE.getCode(), status.getCode());
        Assert.assertTrue(status.getCause().toString(), ((status.getCause()) instanceof IOException));
        OkHttpClientTransportTest.MockStreamListener streamListener = new OkHttpClientTransportTest.MockStreamListener();
        clientTransport.newStream(method, new Metadata(), DEFAULT).start(streamListener);
        streamListener.waitUntilStreamClosed();
        Assert.assertEquals(UNAVAILABLE.getCode(), streamListener.status.getCode());
    }

    @Test
    public void customSocketFactory() throws Exception {
        RuntimeException exception = new RuntimeException("thrown by socket factory");
        SocketFactory socketFactory = new OkHttpClientTransportTest.RuntimeExceptionThrowingSocketFactory(exception);
        clientTransport = new OkHttpClientTransport(new InetSocketAddress("localhost", 0), "authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, OkHttpClientTransportTest.NO_PROXY, tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, new TransportTracer());
        ManagedClientTransport.Listener listener = Mockito.mock(Listener.class);
        clientTransport.start(listener);
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(captor.capture());
        Status status = captor.getValue();
        Assert.assertEquals(UNAVAILABLE.getCode(), status.getCode());
        Assert.assertSame(exception, status.getCause());
    }

    @Test
    public void proxy_200() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        InetSocketAddress targetAddress = InetSocketAddress.createUnresolved("theservice", 80);
        clientTransport = new OkHttpClientTransport(targetAddress, "authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(targetAddress).setProxyAddress(serverSocket.getLocalSocketAddress()).build(), tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, transportTracer);
        clientTransport.start(transportListener);
        Socket sock = serverSocket.accept();
        serverSocket.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), Charsets.UTF_8));
        Assert.assertEquals("CONNECT theservice:80 HTTP/1.1", reader.readLine());
        Assert.assertEquals("Host: theservice:80", reader.readLine());
        while (!("".equals(reader.readLine()))) {
        } 
        sock.getOutputStream().write("HTTP/1.1 200 OK\r\nServer: test\r\n\r\n".getBytes(Charsets.UTF_8));
        sock.getOutputStream().flush();
        Assert.assertEquals("PRI * HTTP/2.0", reader.readLine());
        Assert.assertEquals("", reader.readLine());
        Assert.assertEquals("SM", reader.readLine());
        Assert.assertEquals("", reader.readLine());
        // Empty SETTINGS
        sock.getOutputStream().write(new byte[]{ 0, 0, 0, 0, 4, 0 });
        // GOAWAY
        sock.getOutputStream().write(new byte[]{ 0, 0, 0, 8, 7, 0, 0, 0, 0, 0// last stream id
        , 0, 0, 0, 0// error code
         });
        sock.getOutputStream().flush();
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(ArgumentMatchers.isA(Status.class));
        while ((sock.getInputStream().read()) != (-1)) {
        } 
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
        sock.close();
    }

    @Test
    public void proxy_500() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        InetSocketAddress targetAddress = InetSocketAddress.createUnresolved("theservice", 80);
        clientTransport = new OkHttpClientTransport(targetAddress, "authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(targetAddress).setProxyAddress(serverSocket.getLocalSocketAddress()).build(), tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, transportTracer);
        clientTransport.start(transportListener);
        Socket sock = serverSocket.accept();
        serverSocket.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), Charsets.UTF_8));
        Assert.assertEquals("CONNECT theservice:80 HTTP/1.1", reader.readLine());
        Assert.assertEquals("Host: theservice:80", reader.readLine());
        while (!("".equals(reader.readLine()))) {
        } 
        final String errorText = "text describing error";
        sock.getOutputStream().write("HTTP/1.1 500 OH NO\r\n\r\n".getBytes(Charsets.UTF_8));
        sock.getOutputStream().write(errorText.getBytes(Charsets.UTF_8));
        sock.getOutputStream().flush();
        sock.shutdownOutput();
        Assert.assertEquals((-1), sock.getInputStream().read());
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(captor.capture());
        Status error = captor.getValue();
        Assert.assertTrue(("Status didn't contain error code: " + (captor.getValue())), error.getDescription().contains("500"));
        Assert.assertTrue(("Status didn't contain error description: " + (captor.getValue())), error.getDescription().contains("OH NO"));
        Assert.assertTrue(("Status didn't contain error text: " + (captor.getValue())), error.getDescription().contains(errorText));
        Assert.assertEquals(("Not UNAVAILABLE: " + (captor.getValue())), UNAVAILABLE.getCode(), error.getCode());
        sock.close();
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
    }

    @Test
    public void proxy_immediateServerClose() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        InetSocketAddress targetAddress = InetSocketAddress.createUnresolved("theservice", 80);
        clientTransport = new OkHttpClientTransport(targetAddress, "authority", "userAgent", OkHttpClientTransportTest.EAG_ATTRS, executor, socketFactory, sslSocketFactory, hostnameVerifier, ConnectionSpec.CLEARTEXT, GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, OkHttpClientTransportTest.INITIAL_WINDOW_SIZE, HttpConnectProxiedSocketAddress.newBuilder().setTargetAddress(targetAddress).setProxyAddress(serverSocket.getLocalSocketAddress()).build(), tooManyPingsRunnable, OkHttpClientTransportTest.DEFAULT_MAX_INBOUND_METADATA_SIZE, transportTracer);
        clientTransport.start(transportListener);
        Socket sock = serverSocket.accept();
        serverSocket.close();
        sock.close();
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportShutdown(captor.capture());
        Status error = captor.getValue();
        Assert.assertTrue(("Status didn't contain proxy: " + (captor.getValue())), error.getDescription().contains("proxy"));
        Assert.assertEquals(("Not UNAVAILABLE: " + (captor.getValue())), UNAVAILABLE.getCode(), error.getCode());
        Mockito.verify(transportListener, Mockito.timeout(OkHttpClientTransportTest.TIME_OUT_MS)).transportTerminated();
    }

    @Test
    public void goAway_notUtf8() throws Exception {
        initTransport();
        // 0xFF is never permitted in UTF-8. 0xF0 should have 3 continuations following, and 0x0a isn't
        // a continuation.
        frameHandler().goAway(0, ENHANCE_YOUR_CALM, ByteString.of(((byte) (255)), ((byte) (240)), ((byte) (10))));
        shutdownAndVerify();
    }

    @Test
    public void goAway_notTooManyPings() throws Exception {
        final AtomicBoolean run = new AtomicBoolean();
        tooManyPingsRunnable = new Runnable() {
            @Override
            public void run() {
                run.set(true);
            }
        };
        initTransport();
        frameHandler().goAway(0, ENHANCE_YOUR_CALM, ByteString.encodeUtf8("not_many_pings"));
        Assert.assertFalse(run.get());
        shutdownAndVerify();
    }

    @Test
    public void goAway_tooManyPings() throws Exception {
        final AtomicBoolean run = new AtomicBoolean();
        tooManyPingsRunnable = new Runnable() {
            @Override
            public void run() {
                run.set(true);
            }
        };
        initTransport();
        frameHandler().goAway(0, ENHANCE_YOUR_CALM, ByteString.encodeUtf8("too_many_pings"));
        Assert.assertTrue(run.get());
        shutdownAndVerify();
    }

    @Test
    public void goAway_streamListenerRpcProgress() throws Exception {
        initTransport();
        setMaxConcurrentStreams(2);
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener3 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        OkHttpClientStream stream3 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream3.start(listener3);
        waitForStreamPending(1);
        Assert.assertEquals(2, activeStreamCount());
        assertContainStream(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID);
        assertContainStream(((OkHttpClientTransportTest.DEFAULT_START_STREAM_ID) + 2));
        frameHandler().goAway(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID, CANCEL, ByteString.encodeUtf8("blablabla"));
        listener2.waitUntilStreamClosed();
        listener3.waitUntilStreamClosed();
        Assert.assertNull(listener1.rpcProgress);
        Assert.assertEquals(REFUSED, listener2.rpcProgress);
        Assert.assertEquals(REFUSED, listener3.rpcProgress);
        Assert.assertEquals(1, activeStreamCount());
        assertContainStream(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID);
        getStream(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID).cancel(CANCELLED);
        listener1.waitUntilStreamClosed();
        Assert.assertEquals(PROCESSED, listener1.rpcProgress);
        shutdownAndVerify();
    }

    @Test
    public void reset_streamListenerRpcProgress() throws Exception {
        initTransport();
        OkHttpClientTransportTest.MockStreamListener listener1 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener2 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientTransportTest.MockStreamListener listener3 = new OkHttpClientTransportTest.MockStreamListener();
        OkHttpClientStream stream1 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream1.start(listener1);
        OkHttpClientStream stream2 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream2.start(listener2);
        OkHttpClientStream stream3 = clientTransport.newStream(method, new Metadata(), DEFAULT);
        stream3.start(listener3);
        Assert.assertEquals(3, activeStreamCount());
        assertContainStream(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID);
        assertContainStream(((OkHttpClientTransportTest.DEFAULT_START_STREAM_ID) + 2));
        assertContainStream(((OkHttpClientTransportTest.DEFAULT_START_STREAM_ID) + 4));
        frameHandler().rstStream(((OkHttpClientTransportTest.DEFAULT_START_STREAM_ID) + 2), REFUSED_STREAM);
        listener2.waitUntilStreamClosed();
        Assert.assertNull(listener1.rpcProgress);
        Assert.assertEquals(REFUSED, listener2.rpcProgress);
        Assert.assertNull(listener3.rpcProgress);
        frameHandler().rstStream(OkHttpClientTransportTest.DEFAULT_START_STREAM_ID, CANCEL);
        listener1.waitUntilStreamClosed();
        Assert.assertEquals(PROCESSED, listener1.rpcProgress);
        Assert.assertNull(listener3.rpcProgress);
        getStream(((OkHttpClientTransportTest.DEFAULT_START_STREAM_ID) + 4)).cancel(CANCELLED);
        listener3.waitUntilStreamClosed();
        Assert.assertEquals(PROCESSED, listener3.rpcProgress);
        shutdownAndVerify();
    }

    private static class MockFrameReader implements FrameReader {
        final CountDownLatch closed = new CountDownLatch(1);

        enum Result {

            THROW_EXCEPTION,
            RETURN_FALSE,
            THROW_ERROR;}

        final LinkedBlockingQueue<OkHttpClientTransportTest.MockFrameReader.Result> nextResults = new LinkedBlockingQueue<>();

        @Override
        public void close() throws IOException {
            closed.countDown();
        }

        void assertClosed() {
            try {
                if (!(closed.await(OkHttpClientTransportTest.TIME_OUT_MS, TimeUnit.MILLISECONDS))) {
                    Assert.fail("Failed waiting frame reader to be closed.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Assert.fail("Interrupted while waiting for frame reader to be closed.");
            }
        }

        // The wait is safe; nextFrame is called in a loop and can have spurious wakeups
        @SuppressWarnings("WaitNotInLoop")
        @Override
        public boolean nextFrame(Handler handler) throws IOException {
            OkHttpClientTransportTest.MockFrameReader.Result result;
            try {
                result = nextResults.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
            switch (result) {
                case THROW_EXCEPTION :
                    throw new IOException(OkHttpClientTransportTest.NETWORK_ISSUE_MESSAGE);
                case RETURN_FALSE :
                    return false;
                case THROW_ERROR :
                    throw new Error(OkHttpClientTransportTest.ERROR_MESSAGE);
                default :
                    throw new UnsupportedOperationException(("unimplemented: " + result));
            }
        }

        void throwIoExceptionForNextFrame() {
            nextResults.add(OkHttpClientTransportTest.MockFrameReader.Result.THROW_EXCEPTION);
        }

        void throwErrorForNextFrame() {
            nextResults.add(OkHttpClientTransportTest.MockFrameReader.Result.THROW_ERROR);
        }

        void nextFrameAtEndOfStream() {
            nextResults.add(OkHttpClientTransportTest.MockFrameReader.Result.RETURN_FALSE);
        }

        @Override
        public void readConnectionPreface() throws IOException {
            // not used.
        }
    }

    private static class MockStreamListener implements ClientStreamListener {
        Status status;

        Metadata headers;

        Metadata trailers;

        RpcProgress rpcProgress;

        CountDownLatch closed = new CountDownLatch(1);

        ArrayList<String> messages = new ArrayList<>();

        boolean onReadyCalled;

        MockStreamListener() {
        }

        @Override
        public void headersRead(Metadata headers) {
            this.headers = headers;
        }

        @Override
        public void messagesAvailable(MessageProducer producer) {
            InputStream inputStream;
            while ((inputStream = producer.next()) != null) {
                String msg = OkHttpClientTransportTest.MockStreamListener.getContent(inputStream);
                if (msg != null) {
                    messages.add(msg);
                }
            } 
        }

        @Override
        public void closed(Status status, Metadata trailers) {
            closed(status, PROCESSED, trailers);
        }

        @Override
        public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
            this.status = status;
            this.trailers = trailers;
            this.rpcProgress = rpcProgress;
            closed.countDown();
        }

        @Override
        public void onReady() {
            onReadyCalled = true;
        }

        boolean isOnReadyCalled() {
            boolean value = onReadyCalled;
            onReadyCalled = false;
            return value;
        }

        void waitUntilStreamClosed() throws InterruptedException {
            if (!(closed.await(OkHttpClientTransportTest.TIME_OUT_MS, TimeUnit.MILLISECONDS))) {
                Assert.fail("Failed waiting stream to be closed.");
            }
        }

        // We don't care about suppressed exceptions in the test
        @SuppressWarnings("Finally")
        static String getContent(InputStream message) {
            BufferedReader br = new BufferedReader(new InputStreamReader(message, Charsets.UTF_8));
            try {
                // Only one line message is used in this test.
                return br.readLine();
            } catch (IOException e) {
                return null;
            } finally {
                try {
                    message.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static class MockSocket extends Socket {
        OkHttpClientTransportTest.MockFrameReader frameReader;

        MockSocket(OkHttpClientTransportTest.MockFrameReader frameReader) {
            this.frameReader = frameReader;
        }

        @Override
        public synchronized void close() {
            frameReader.nextFrameAtEndOfStream();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return InetSocketAddress.createUnresolved("localhost", 4000);
        }
    }

    static class PingCallbackImpl implements ClientTransport.PingCallback {
        int invocationCount;

        long roundTripTime;

        Throwable failureCause;

        @Override
        public void onSuccess(long roundTripTimeNanos) {
            (invocationCount)++;
            this.roundTripTime = roundTripTimeNanos;
        }

        @Override
        public void onFailure(Throwable cause) {
            (invocationCount)++;
            this.failureCause = cause;
        }
    }

    private static class DelayConnectedCallback implements Runnable {
        SettableFuture<Void> delayed = SettableFuture.create();

        @Override
        public void run() {
            Futures.getUnchecked(delayed);
        }

        void allowConnected() {
            delayed.set(null);
        }
    }

    /**
     * A FrameWriter to mock with CALL_REAL_METHODS option.
     */
    private static class MockFrameWriter implements FrameWriter {
        private Socket socket;

        private final Queue<Buffer> capturedBuffer;

        public MockFrameWriter(Socket socket, Queue<Buffer> capturedBuffer) {
            // Sets a socket to close. Some tests assumes that FrameWriter will close underlying sink
            // which will eventually close the socket.
            this.socket = socket;
            this.capturedBuffer = capturedBuffer;
        }

        void setSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public int maxDataLength() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void data(boolean outFinished, int streamId, Buffer source, int byteCount) throws IOException {
            // simulate the side effect, and captures to internal queue.
            Buffer capture = new Buffer();
            capture.write(source, byteCount);
            capturedBuffer.add(capture);
        }

        // rest of methods are unimplemented
        @Override
        public void connectionPreface() throws IOException {
        }

        @Override
        public void ackSettings(Settings peerSettings) throws IOException {
        }

        @Override
        public void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void synStream(boolean outFinished, boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) throws IOException {
        }

        @Override
        public void synReply(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException {
        }

        @Override
        public void headers(int streamId, List<Header> headerBlock) throws IOException {
        }

        @Override
        public void rstStream(int streamId, ErrorCode errorCode) throws IOException {
        }

        @Override
        public void settings(Settings okHttpSettings) throws IOException {
        }

        @Override
        public void ping(boolean ack, int payload1, int payload2) throws IOException {
        }

        @Override
        public void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] debugData) throws IOException {
        }

        @Override
        public void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
        }
    }

    private static class RuntimeExceptionThrowingSocketFactory extends SocketFactory {
        RuntimeException exception;

        private RuntimeExceptionThrowingSocketFactory(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public Socket createSocket(String s, int i) {
            throw exception;
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) {
            throw exception;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) {
            throw exception;
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) {
            throw exception;
        }
    }
}

