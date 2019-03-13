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
package io.grpc.okhttp;


import ErrorCode.CANCEL;
import GrpcUtil.USER_AGENT_KEY;
import Headers.CONTENT_TYPE_HEADER;
import Headers.METHOD_GET_HEADER;
import Headers.METHOD_HEADER;
import Headers.SCHEME_HEADER;
import Headers.TE_HEADER;
import MethodType.UNARY;
import Status.CANCELLED;
import com.google.common.io.BaseEncoding;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.NoopClientStreamListener;
import io.grpc.internal.StatsTraceContext;
import io.grpc.internal.TransportTracer;
import io.grpc.okhttp.internal.framed.FrameWriter;
import io.grpc.okhttp.internal.framed.Header;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(JUnit4.class)
public class OkHttpClientStreamTest {
    private static final int MAX_MESSAGE_SIZE = 100;

    private static final int INITIAL_WINDOW_SIZE = 65535;

    @Mock
    private MethodDescriptor.Marshaller<Void> marshaller;

    @Mock
    private FrameWriter mockedFrameWriter;

    private ExceptionHandlingFrameWriter frameWriter;

    @Mock
    private OkHttpClientTransport transport;

    @Mock
    private OutboundFlowController flowController;

    @Captor
    private ArgumentCaptor<List<Header>> headersCaptor;

    private final Object lock = new Object();

    private final TransportTracer transportTracer = new TransportTracer();

    private MethodDescriptor<?, ?> methodDescriptor;

    private OkHttpClientStream stream;

    @Test
    public void getType() {
        Assert.assertEquals(UNARY, stream.getType());
    }

    @Test
    public void cancel_notStarted() {
        final AtomicReference<Status> statusRef = new AtomicReference<>();
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener() {
            @Override
            public void closed(Status status, RpcProgress rpcProgress, Metadata trailers) {
                statusRef.set(status);
                Assert.assertTrue(Thread.holdsLock(lock));
            }
        });
        stream.cancel(CANCELLED);
        Assert.assertEquals(Status.Code.CANCELLED, statusRef.get().getCode());
    }

    @Test
    public void cancel_started() {
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener());
        stream.transportState().start(1234);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertTrue(Thread.holdsLock(lock));
                return null;
            }
        }).when(transport).finishStream(1234, CANCELLED, PROCESSED, true, CANCEL, null);
        stream.cancel(CANCELLED);
        Mockito.verify(transport).finishStream(1234, CANCELLED, PROCESSED, true, CANCEL, null);
    }

    @Test
    public void start_alreadyCancelled() {
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener());
        stream.cancel(CANCELLED);
        stream.transportState().start(1234);
        Mockito.verifyNoMoreInteractions(mockedFrameWriter);
    }

    @Test
    public void start_userAgentRemoved() throws IOException {
        Metadata metaData = new Metadata();
        metaData.put(USER_AGENT_KEY, "misbehaving-application");
        stream = new OkHttpClientStream(methodDescriptor, metaData, frameWriter, transport, flowController, lock, OkHttpClientStreamTest.MAX_MESSAGE_SIZE, OkHttpClientStreamTest.INITIAL_WINDOW_SIZE, "localhost", "good-application", StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener());
        stream.transportState().start(3);
        Mockito.verify(mockedFrameWriter).synStream(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), headersCaptor.capture());
        assertThat(headersCaptor.getValue()).contains(new Header(USER_AGENT_KEY.name(), "good-application"));
    }

    @Test
    public void start_headerFieldOrder() throws IOException {
        Metadata metaData = new Metadata();
        metaData.put(USER_AGENT_KEY, "misbehaving-application");
        stream = new OkHttpClientStream(methodDescriptor, metaData, frameWriter, transport, flowController, lock, OkHttpClientStreamTest.MAX_MESSAGE_SIZE, OkHttpClientStreamTest.INITIAL_WINDOW_SIZE, "localhost", "good-application", StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener());
        stream.transportState().start(3);
        Mockito.verify(mockedFrameWriter).synStream(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), headersCaptor.capture());
        assertThat(headersCaptor.getValue()).containsExactly(SCHEME_HEADER, METHOD_HEADER, new Header(Header.TARGET_AUTHORITY, "localhost"), new Header(Header.TARGET_PATH, ("/" + (methodDescriptor.getFullMethodName()))), new Header(USER_AGENT_KEY.name(), "good-application"), CONTENT_TYPE_HEADER, TE_HEADER).inOrder();
    }

    @Test
    public void getUnaryRequest() throws IOException {
        MethodDescriptor<?, ?> getMethod = MethodDescriptor.<Void, Void>newBuilder().setType(MethodDescriptor.MethodType.UNARY).setFullMethodName("service/method").setIdempotent(true).setSafe(true).setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).build();
        stream = new OkHttpClientStream(getMethod, new Metadata(), frameWriter, transport, flowController, lock, OkHttpClientStreamTest.MAX_MESSAGE_SIZE, OkHttpClientStreamTest.INITIAL_WINDOW_SIZE, "localhost", "good-application", StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(new OkHttpClientStreamTest.BaseClientStreamListener());
        // GET streams send headers after halfClose is called.
        Mockito.verify(mockedFrameWriter, Mockito.times(0)).synStream(ArgumentMatchers.eq(false), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), headersCaptor.capture());
        Mockito.verify(transport, Mockito.times(0)).streamReadyToStart(ArgumentMatchers.isA(OkHttpClientStream.class));
        byte[] msg = "request".getBytes(Charset.forName("UTF-8"));
        stream.writeMessage(new ByteArrayInputStream(msg));
        stream.halfClose();
        Mockito.verify(transport).streamReadyToStart(ArgumentMatchers.eq(stream));
        stream.transportState().start(3);
        Mockito.verify(mockedFrameWriter).synStream(ArgumentMatchers.eq(true), ArgumentMatchers.eq(false), ArgumentMatchers.eq(3), ArgumentMatchers.eq(0), headersCaptor.capture());
        assertThat(headersCaptor.getValue()).contains(METHOD_GET_HEADER);
        assertThat(headersCaptor.getValue()).contains(new Header(Header.TARGET_PATH, ((("/" + (getMethod.getFullMethodName())) + "?") + (BaseEncoding.base64().encode(msg)))));
    }

    // TODO(carl-mastrangelo): extract this out into a testing/ directory and remove other definitions
    // of it.
    private static class BaseClientStreamListener extends NoopClientStreamListener {
        @Override
        public void messagesAvailable(MessageProducer producer) {
            while ((producer.next()) != null) {
            } 
        }
    }
}

