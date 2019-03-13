/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.grpc;


import Status.ABORTED;
import Status.OK;
import com.linecorp.armeria.common.ClosedSessionException;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponseWriter;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.grpc.testing.Messages.SimpleRequest;
import com.linecorp.armeria.grpc.testing.Messages.SimpleResponse;
import com.linecorp.armeria.grpc.testing.TestServiceGrpc;
import com.linecorp.armeria.internal.grpc.ArmeriaMessageDeframer.ByteBufOrStream;
import com.linecorp.armeria.internal.grpc.GrpcTestUtil;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.common.EventLoopRule;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.util.IdentityHashMap;
import java.util.concurrent.CompletableFuture;
import org.curioswitch.common.protobuf.json.MessageMarshaller;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.reactivestreams.Subscription;


// TODO(anuraag): Currently only grpc-protobuf has been published so we only test proto here.
// Once grpc-thrift is published, add tests for thrift stubs which will not go through the
// optimized protobuf marshalling paths.
public class ArmeriaServerCallTest {
    private static final int MAX_MESSAGE_BYTES = 1024;

    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private HttpResponseWriter res;

    @Mock
    private ServerCall.Listener<SimpleRequest> listener;

    @Mock
    private Subscription subscription;

    private ServiceRequestContext ctx;

    @Mock
    private IdentityHashMap<Object, ByteBuf> buffersAttr;

    private ArmeriaServerCall<SimpleRequest, SimpleResponse> call;

    private CompletableFuture<Void> completionFuture;

    @Test
    public void messageReadAfterClose_byteBuf() {
        call.close(ABORTED, new Metadata());
        // messageRead is always called from the event loop.
        ArmeriaServerCallTest.eventLoop.get().submit(() -> {
            call.messageRead(new ByteBufOrStream(GrpcTestUtil.requestByteBuf()));
            verify(listener, never()).onMessage(any());
        }).syncUninterruptibly();
    }

    @Test
    public void messageRead_notWrappedByteBuf() {
        final ByteBuf buf = GrpcTestUtil.requestByteBuf();
        call.messageRead(new ByteBufOrStream(buf));
        Mockito.verifyZeroInteractions(buffersAttr);
    }

    @Test
    public void messageRead_wrappedByteBuf() {
        tearDown();
        call = new ArmeriaServerCall(HttpHeaders.of(), TestServiceGrpc.getUnaryCallMethod(), CompressorRegistry.getDefaultInstance(), DecompressorRegistry.getDefaultInstance(), res, ArmeriaServerCallTest.MAX_MESSAGE_BYTES, ArmeriaServerCallTest.MAX_MESSAGE_BYTES, ctx, GrpcSerializationFormats.PROTO, MessageMarshaller.builder().build(), true, false, "gzip");
        final ByteBuf buf = GrpcTestUtil.requestByteBuf();
        call.messageRead(new ByteBufOrStream(buf));
        Mockito.verify(buffersAttr).put(ArgumentMatchers.any(), ArgumentMatchers.same(buf));
    }

    @Test
    public void messageReadAfterClose_stream() {
        call.close(ABORTED, new Metadata());
        // messageRead is always called from the event loop.
        ArmeriaServerCallTest.eventLoop.get().submit(() -> {
            call.messageRead(new ByteBufOrStream(new ByteBufInputStream(GrpcTestUtil.requestByteBuf(), true)));
            verify(listener, never()).onMessage(any());
        }).syncUninterruptibly();
    }

    @Test
    public void readyOnStart() {
        assertThat(call.isReady()).isTrue();
        call.messageReader().cancel();
    }

    @Test
    public void notReadyAfterClose() {
        assertThat(call.isReady()).isTrue();
        call.close(OK, new Metadata());
        await().untilAsserted(() -> assertThat(call.isReady()).isFalse());
    }

    @Test
    public void closedIfCancelled() {
        assertThat(call.isCancelled()).isFalse();
        completionFuture.completeExceptionally(ClosedSessionException.get());
        await().untilAsserted(() -> assertThat(call.isCancelled()).isTrue());
    }
}

