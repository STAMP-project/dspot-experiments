/**
 * Copyright 2015 The gRPC Authors
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


import Context.CancellableContext;
import MethodType.UNARY;
import Status.CANCELLED;
import Status.OK;
import Status.RESOURCE_EXHAUSTED;
import Status.UNKNOWN;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.grpc.internal.ServerCallImpl.ServerStreamListenerImpl;
import io.grpc.internal.testing.SingleMessageProducer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


@RunWith(JUnit4.class)
public class ServerCallImplTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private ServerStream stream;

    @Mock
    private ServerCall.Listener<Long> callListener;

    private final CallTracer serverCallTracer = CallTracer.getDefaultFactory().create();

    private ServerCallImpl<Long, Long> call;

    private CancellableContext context;

    private static final MethodDescriptor<Long, Long> UNARY_METHOD = MethodDescriptor.<Long, Long>newBuilder().setType(UNARY).setFullMethodName("service/method").setRequestMarshaller(new ServerCallImplTest.LongMarshaller()).setResponseMarshaller(new ServerCallImplTest.LongMarshaller()).build();

    private static final MethodDescriptor<Long, Long> CLIENT_STREAMING_METHOD = MethodDescriptor.<Long, Long>newBuilder().setType(UNARY).setFullMethodName("service/method").setRequestMarshaller(new ServerCallImplTest.LongMarshaller()).setResponseMarshaller(new ServerCallImplTest.LongMarshaller()).build();

    private final Metadata requestHeaders = new Metadata();

    @Test
    public void callTracer_success() {
        callTracer0(OK);
    }

    @Test
    public void callTracer_failure() {
        callTracer0(UNKNOWN);
    }

    @Test
    public void request() {
        call.request(10);
        verify(stream).request(10);
    }

    @Test
    public void sendHeader_firstCall() {
        Metadata headers = new Metadata();
        call.sendHeaders(headers);
        verify(stream).writeHeaders(headers);
    }

    @Test
    public void sendHeader_failsOnSecondCall() {
        call.sendHeaders(new Metadata());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("sendHeaders has already been called");
        call.sendHeaders(new Metadata());
    }

    @Test
    public void sendHeader_failsOnClosed() {
        call.close(CANCELLED, new Metadata());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("call is closed");
        call.sendHeaders(new Metadata());
    }

    @Test
    public void sendMessage() {
        call.sendHeaders(new Metadata());
        call.sendMessage(1234L);
        verify(stream).writeMessage(ArgumentMatchers.isA(InputStream.class));
        verify(stream).flush();
    }

    @Test
    public void sendMessage_failsOnClosed() {
        call.sendHeaders(new Metadata());
        call.close(CANCELLED, new Metadata());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("call is closed");
        call.sendMessage(1234L);
    }

    @Test
    public void sendMessage_failsIfheadersUnsent() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("sendHeaders has not been called");
        call.sendMessage(1234L);
    }

    @Test
    public void sendMessage_closesOnFailure() {
        call.sendHeaders(new Metadata());
        Mockito.doThrow(new RuntimeException("bad")).when(stream).writeMessage(ArgumentMatchers.isA(InputStream.class));
        call.sendMessage(1234L);
        verify(stream).close(ArgumentMatchers.isA(Status.class), ArgumentMatchers.isA(Metadata.class));
    }

    @Test
    public void sendMessage_serverSendsOne_closeOnSecondCall_unary() {
        sendMessage_serverSendsOne_closeOnSecondCall(ServerCallImplTest.UNARY_METHOD);
    }

    @Test
    public void sendMessage_serverSendsOne_closeOnSecondCall_clientStreaming() {
        sendMessage_serverSendsOne_closeOnSecondCall(ServerCallImplTest.CLIENT_STREAMING_METHOD);
    }

    @Test
    public void sendMessage_serverSendsOne_closeOnSecondCall_appRunToCompletion_unary() {
        sendMessage_serverSendsOne_closeOnSecondCall_appRunToCompletion(ServerCallImplTest.UNARY_METHOD);
    }

    @Test
    public void sendMessage_serverSendsOne_closeOnSecondCall_appRunToCompletion_clientStreaming() {
        sendMessage_serverSendsOne_closeOnSecondCall_appRunToCompletion(ServerCallImplTest.CLIENT_STREAMING_METHOD);
    }

    @Test
    public void serverSendsOne_okFailsOnMissingResponse_unary() {
        serverSendsOne_okFailsOnMissingResponse(ServerCallImplTest.UNARY_METHOD);
    }

    @Test
    public void serverSendsOne_okFailsOnMissingResponse_clientStreaming() {
        serverSendsOne_okFailsOnMissingResponse(ServerCallImplTest.CLIENT_STREAMING_METHOD);
    }

    @Test
    public void serverSendsOne_canErrorWithoutResponse() {
        final String description = "test description";
        final Status status = RESOURCE_EXHAUSTED.withDescription(description);
        final Metadata metadata = new Metadata();
        call.close(status, metadata);
        Mockito.verify(stream, Mockito.times(1)).close(ArgumentMatchers.same(status), ArgumentMatchers.same(metadata));
    }

    @Test
    public void isReady() {
        Mockito.when(stream.isReady()).thenReturn(true);
        Assert.assertTrue(call.isReady());
    }

    @Test
    public void getAuthority() {
        Mockito.when(stream.getAuthority()).thenReturn("fooapi.googleapis.com");
        Assert.assertEquals("fooapi.googleapis.com", call.getAuthority());
        verify(stream).getAuthority();
    }

    @Test
    public void getNullAuthority() {
        Mockito.when(stream.getAuthority()).thenReturn(null);
        Assert.assertNull(call.getAuthority());
        verify(stream).getAuthority();
    }

    @Test
    public void setMessageCompression() {
        call.setMessageCompression(true);
        verify(stream).setMessageCompression(true);
    }

    @Test
    public void streamListener_halfClosed() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.halfClosed();
        verify(callListener).onHalfClose();
    }

    @Test
    public void streamListener_halfClosed_onlyOnce() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.halfClosed();
        // canceling the call should short circuit future halfClosed() calls.
        streamListener.closed(CANCELLED);
        streamListener.halfClosed();
        verify(callListener).onHalfClose();
    }

    @Test
    public void streamListener_closedOk() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.closed(OK);
        verify(callListener).onComplete();
        Assert.assertTrue(context.isCancelled());
        Assert.assertNull(context.cancellationCause());
    }

    @Test
    public void streamListener_closedCancelled() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.closed(CANCELLED);
        verify(callListener).onCancel();
        Assert.assertTrue(context.isCancelled());
        Assert.assertNull(context.cancellationCause());
    }

    @Test
    public void streamListener_onReady() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.onReady();
        verify(callListener).onReady();
    }

    @Test
    public void streamListener_onReady_onlyOnce() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.onReady();
        // canceling the call should short circuit future halfClosed() calls.
        streamListener.closed(CANCELLED);
        streamListener.onReady();
        verify(callListener).onReady();
    }

    @Test
    public void streamListener_messageRead() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.messagesAvailable(new SingleMessageProducer(ServerCallImplTest.UNARY_METHOD.streamRequest(1234L)));
        verify(callListener).onMessage(1234L);
    }

    @Test
    public void streamListener_messageRead_onlyOnce() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        streamListener.messagesAvailable(new SingleMessageProducer(ServerCallImplTest.UNARY_METHOD.streamRequest(1234L)));
        // canceling the call should short circuit future halfClosed() calls.
        streamListener.closed(CANCELLED);
        streamListener.messagesAvailable(new SingleMessageProducer(ServerCallImplTest.UNARY_METHOD.streamRequest(1234L)));
        verify(callListener).onMessage(1234L);
    }

    @Test
    public void streamListener_unexpectedRuntimeException() {
        ServerStreamListenerImpl<Long> streamListener = new ServerCallImpl.ServerStreamListenerImpl<>(call, callListener, context);
        Mockito.doThrow(new RuntimeException("unexpected exception")).when(callListener).onMessage(ArgumentMatchers.any(Long.class));
        InputStream inputStream = ServerCallImplTest.UNARY_METHOD.streamRequest(1234L);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("unexpected exception");
        streamListener.messagesAvailable(new SingleMessageProducer(inputStream));
    }

    private static class LongMarshaller implements Marshaller<Long> {
        @Override
        public InputStream stream(Long value) {
            return new ByteArrayInputStream(value.toString().getBytes(Charsets.UTF_8));
        }

        @Override
        public Long parse(InputStream stream) {
            try {
                return Long.parseLong(CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

