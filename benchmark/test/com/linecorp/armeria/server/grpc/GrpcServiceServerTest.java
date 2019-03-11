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


import Code.CANCELLED;
import Code.RESOURCE_EXHAUSTED;
import Code.UNKNOWN;
import Codec.Identity.NONE;
import GrpcHeaderNames.GRPC_ACCEPT_ENCODING;
import GrpcHeaderNames.GRPC_ENCODING;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.CONTENT_TYPE;
import HttpMethod.POST;
import HttpStatus.BAD_REQUEST;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import SessionProtocol.H1C;
import SessionProtocol.H2C;
import Status.ABORTED;
import Status.DEADLINE_EXCEEDED;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.util.EventLoopGroups;
import com.linecorp.armeria.grpc.testing.Messages.EchoStatus;
import com.linecorp.armeria.grpc.testing.Messages.Payload;
import com.linecorp.armeria.grpc.testing.Messages.SimpleRequest;
import com.linecorp.armeria.grpc.testing.Messages.SimpleResponse;
import com.linecorp.armeria.grpc.testing.Messages.StreamingOutputCallRequest;
import com.linecorp.armeria.grpc.testing.UnitTestServiceGrpc;
import com.linecorp.armeria.grpc.testing.UnitTestServiceGrpc.UnitTestServiceBlockingStub;
import com.linecorp.armeria.grpc.testing.UnitTestServiceGrpc.UnitTestServiceImplBase;
import com.linecorp.armeria.grpc.testing.UnitTestServiceGrpc.UnitTestServiceStub;
import com.linecorp.armeria.internal.PathAndQuery;
import com.linecorp.armeria.internal.grpc.GrpcTestUtil;
import com.linecorp.armeria.internal.grpc.StreamRecorder;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.testing.server.ServerRule;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc.ServerReflectionStub;
import io.grpc.reflection.v1alpha.ServerReflectionRequest;
import io.grpc.reflection.v1alpha.ServerReflectionResponse;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import io.netty.util.AsciiString;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static ArmeriaServerCall.TRAILERS_FRAME_HEADER;


@RunWith(Parameterized.class)
public class GrpcServiceServerTest {
    private static final int MAX_MESSAGE_SIZE = (16 * 1024) * 1024;

    private static AsciiString LARGE_PAYLOAD;

    // Used to communicate completion to a test when it is not possible to return to the client.
    private static final AtomicReference<Boolean> COMPLETED = new AtomicReference<>();

    // Used to communicate that the client has closed to allow the server to continue executing logic when
    // required.
    private static final AtomicReference<Boolean> CLIENT_CLOSED = new AtomicReference<>();

    private static class UnitTestServiceImpl extends UnitTestServiceImplBase {
        private static final AttributeKey<Integer> CHECK_REQUEST_CONTEXT_COUNT = AttributeKey.valueOf(GrpcServiceServerTest.UnitTestServiceImpl.class, "CHECK_REQUEST_CONTEXT_COUNT");

        @Override
        public void staticUnaryCall(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            if (!(request.equals(GrpcTestUtil.REQUEST_MESSAGE))) {
                responseObserver.onError(new IllegalArgumentException(("Unexpected request: " + request)));
                return;
            }
            responseObserver.onNext(GrpcTestUtil.RESPONSE_MESSAGE);
            responseObserver.onCompleted();
        }

        @Override
        public void staticStreamedOutputCall(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            if (!(request.equals(GrpcTestUtil.REQUEST_MESSAGE))) {
                responseObserver.onError(new IllegalArgumentException(("Unexpected request: " + request)));
                return;
            }
            responseObserver.onNext(GrpcTestUtil.RESPONSE_MESSAGE);
            responseObserver.onNext(GrpcTestUtil.RESPONSE_MESSAGE);
            responseObserver.onCompleted();
        }

        @Override
        public void errorNoMessage(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            responseObserver.onError(ABORTED.asException());
        }

        @Override
        public void errorWithMessage(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            responseObserver.onError(ABORTED.withDescription("aborted call").asException());
        }

        @Override
        public void unaryThrowsError(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            throw ABORTED.withDescription("call aborted").asRuntimeException();
        }

        @Override
        public StreamObserver<SimpleRequest> streamThrowsError(StreamObserver<SimpleResponse> responseObserver) {
            return new StreamObserver<SimpleRequest>() {
                @Override
                public void onNext(SimpleRequest value) {
                    throw ABORTED.withDescription("bad streaming message").asRuntimeException();
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onCompleted() {
                }
            };
        }

        @Override
        public StreamObserver<SimpleRequest> streamThrowsErrorInStub(StreamObserver<SimpleResponse> responseObserver) {
            throw ABORTED.withDescription("bad streaming stub").asRuntimeException();
        }

        @Override
        public void staticUnaryCallSetsMessageCompression(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            if (!(request.equals(GrpcTestUtil.REQUEST_MESSAGE))) {
                responseObserver.onError(new IllegalArgumentException(("Unexpected request: " + request)));
                return;
            }
            final ServerCallStreamObserver<SimpleResponse> callObserver = ((ServerCallStreamObserver<SimpleResponse>) (responseObserver));
            callObserver.setCompression("gzip");
            callObserver.setMessageCompression(true);
            responseObserver.onNext(GrpcTestUtil.RESPONSE_MESSAGE);
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<SimpleRequest> checkRequestContext(StreamObserver<SimpleResponse> responseObserver) {
            final RequestContext ctx = RequestContext.current();
            ctx.attr(GrpcServiceServerTest.UnitTestServiceImpl.CHECK_REQUEST_CONTEXT_COUNT).set(0);
            return new StreamObserver<SimpleRequest>() {
                @Override
                public void onNext(SimpleRequest value) {
                    final RequestContext ctx = RequestContext.current();
                    final Attribute<Integer> attr = ctx.attr(GrpcServiceServerTest.UnitTestServiceImpl.CHECK_REQUEST_CONTEXT_COUNT);
                    attr.set(((attr.get()) + 1));
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onCompleted() {
                    final RequestContext ctx = RequestContext.current();
                    final int count = ctx.attr(GrpcServiceServerTest.UnitTestServiceImpl.CHECK_REQUEST_CONTEXT_COUNT).get();
                    responseObserver.onNext(SimpleResponse.newBuilder().setPayload(Payload.newBuilder().setBody(ByteString.copyFromUtf8(Integer.toString(count)))).build());
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<SimpleRequest> streamClientCancels(StreamObserver<SimpleResponse> responseObserver) {
            return new StreamObserver<SimpleRequest>() {
                @Override
                public void onNext(SimpleRequest value) {
                    responseObserver.onNext(SimpleResponse.getDefaultInstance());
                }

                @Override
                public void onError(Throwable t) {
                    GrpcServiceServerTest.COMPLETED.set(true);
                }

                @Override
                public void onCompleted() {
                }
            };
        }

        @Override
        public void streamClientCancelsBeforeResponseClosedCancels(SimpleRequest request, StreamObserver<SimpleResponse> responseObserver) {
            ((ServerCallStreamObserver<?>) (responseObserver)).setOnCancelHandler(() -> com.linecorp.armeria.server.grpc.COMPLETED.set(true));
            responseObserver.onNext(SimpleResponse.getDefaultInstance());
        }
    }

    private static final BlockingQueue<RequestLog> requestLogQueue = new LinkedTransferQueue<>();

    @ClassRule
    public static ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.workerGroup(EventLoopGroups.newEventLoopGroup(1), true);
            sb.defaultMaxRequestLength(0);
            sb.service(new GrpcServiceBuilder().setMaxInboundMessageSizeBytes(GrpcServiceServerTest.MAX_MESSAGE_SIZE).addService(new GrpcServiceServerTest.UnitTestServiceImpl()).enableUnframedRequests(true).supportedSerializationFormats(GrpcSerializationFormats.values()).build(), ( service) -> service.decorate(LoggingService.newDecorator()).decorate(( delegate, ctx, req) -> {
                ctx.log().addListener(GrpcServiceServerTest.requestLogQueue::add, RequestLogAvailability.COMPLETE);
                return delegate.serve(ctx, req);
            }));
            sb.service(new GrpcServiceBuilder().addService(ProtoReflectionService.newInstance()).build(), ( service) -> service.decorate(LoggingService.newDecorator()));
        }
    };

    @ClassRule
    public static ServerRule serverWithBlockingExecutor = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.workerGroup(EventLoopGroups.newEventLoopGroup(1), true);
            sb.defaultMaxRequestLength(0);
            sb.serviceUnder("/", new GrpcServiceBuilder().setMaxInboundMessageSizeBytes(GrpcServiceServerTest.MAX_MESSAGE_SIZE).addService(new GrpcServiceServerTest.UnitTestServiceImpl()).enableUnframedRequests(true).supportedSerializationFormats(GrpcSerializationFormats.values()).useBlockingTaskExecutor(true).build().decorate(LoggingService.newDecorator()).decorate(( delegate, ctx, req) -> {
                ctx.log().addListener(GrpcServiceServerTest.requestLogQueue::add, RequestLogAvailability.COMPLETE);
                return delegate.serve(ctx, req);
            }));
        }
    };

    @ClassRule
    public static ServerRule serverWithNoMaxMessageSize = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.workerGroup(EventLoopGroups.newEventLoopGroup(1), true);
            sb.defaultMaxRequestLength(0);
            sb.serviceUnder("/", new GrpcServiceBuilder().addService(new GrpcServiceServerTest.UnitTestServiceImpl()).build().decorate(LoggingService.newDecorator()).decorate(( delegate, ctx, req) -> {
                ctx.log().addListener(GrpcServiceServerTest.requestLogQueue::add, RequestLogAvailability.COMPLETE);
                return delegate.serve(ctx, req);
            }));
        }
    };

    @ClassRule
    public static ServerRule serverWithLongMaxRequestLimit = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.workerGroup(EventLoopGroups.newEventLoopGroup(1), true);
            sb.defaultMaxRequestLength(Long.MAX_VALUE);
            sb.serviceUnder("/", new GrpcServiceBuilder().addService(new GrpcServiceServerTest.UnitTestServiceImpl()).build().decorate(LoggingService.newDecorator()).decorate(( delegate, ctx, req) -> {
                ctx.log().addListener(GrpcServiceServerTest.requestLogQueue::add, RequestLogAvailability.COMPLETE);
                return delegate.serve(ctx, req);
            }));
        }
    };

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    private static ManagedChannel channel;

    private static ManagedChannel blockingChannel;

    private final boolean useBlockingExecutor;

    public GrpcServiceServerTest(boolean useBlockingExecutor) {
        this.useBlockingExecutor = useBlockingExecutor;
    }

    private UnitTestServiceBlockingStub blockingClient;

    private UnitTestServiceStub streamingClient;

    @Test
    public void unary_normal() throws Exception {
        assertThat(blockingClient.staticUnaryCall(GrpcTestUtil.REQUEST_MESSAGE)).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        // Confirm gRPC paths are cached despite using serviceUnder
        await().untilAsserted(() -> assertThat(PathAndQuery.cachedPaths()).contains("/armeria.grpc.testing.UnitTestService/StaticUnaryCall"));
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void streamedOutput_normal() throws Exception {
        final StreamRecorder<SimpleResponse> recorder = StreamRecorder.create();
        streamingClient.staticStreamedOutputCall(GrpcTestUtil.REQUEST_MESSAGE, recorder);
        recorder.awaitCompletion();
        assertThat(recorder.getValues()).containsExactly(GrpcTestUtil.RESPONSE_MESSAGE, GrpcTestUtil.RESPONSE_MESSAGE);
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticStreamedOutputCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void error_noMessage() throws Exception {
        final StatusRuntimeException t = ((StatusRuntimeException) (catchThrowable(() -> blockingClient.errorNoMessage(REQUEST_MESSAGE))));
        assertThat(t.getStatus().getCode()).isEqualTo(Code.ABORTED);
        assertThat(t.getStatus().getDescription()).isNull();
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/ErrorNoMessage");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(grpcStatus).isNotNull();
            assertThat(grpcStatus.getCode()).isEqualTo(Code.ABORTED);
            assertThat(grpcStatus.getDescription()).isNull();
        });
    }

    @Test
    public void error_withMessage() throws Exception {
        final StatusRuntimeException t = ((StatusRuntimeException) (catchThrowable(() -> blockingClient.errorWithMessage(REQUEST_MESSAGE))));
        assertThat(t.getStatus().getCode()).isEqualTo(Code.ABORTED);
        assertThat(t.getStatus().getDescription()).isEqualTo("aborted call");
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/ErrorWithMessage");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(grpcStatus).isNotNull();
            assertThat(grpcStatus.getCode()).isEqualTo(Code.ABORTED);
            assertThat(grpcStatus.getDescription()).isEqualTo("aborted call");
        });
    }

    @Test
    public void error_thrown_unary() throws Exception {
        final StatusRuntimeException t = ((StatusRuntimeException) (catchThrowable(() -> blockingClient.unaryThrowsError(REQUEST_MESSAGE))));
        assertThat(t.getStatus().getCode()).isEqualTo(Code.ABORTED);
        assertThat(t.getStatus().getDescription()).isEqualTo("call aborted");
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/UnaryThrowsError");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(grpcStatus).isNotNull();
            assertThat(grpcStatus.getCode()).isEqualTo(Code.ABORTED);
            assertThat(grpcStatus.getDescription()).isEqualTo("call aborted");
        });
    }

    @Test
    public void error_thrown_streamMessage() throws Exception {
        final StreamRecorder<SimpleResponse> response = StreamRecorder.create();
        final StreamObserver<SimpleRequest> request = streamingClient.streamThrowsError(response);
        request.onNext(GrpcTestUtil.REQUEST_MESSAGE);
        response.awaitCompletion();
        final StatusRuntimeException t = ((StatusRuntimeException) (response.getError()));
        assertThat(t.getStatus().getCode()).isEqualTo(Code.ABORTED);
        assertThat(t.getStatus().getDescription()).isEqualTo("bad streaming message");
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StreamThrowsError");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(grpcStatus).isNotNull();
            assertThat(grpcStatus.getCode()).isEqualTo(Code.ABORTED);
            assertThat(grpcStatus.getDescription()).isEqualTo("bad streaming message");
        });
    }

    @Test
    public void error_thrown_streamStub() throws Exception {
        final StreamRecorder<SimpleResponse> response = StreamRecorder.create();
        streamingClient.streamThrowsErrorInStub(response);
        response.awaitCompletion();
        final StatusRuntimeException t = ((StatusRuntimeException) (response.getError()));
        assertThat(t.getStatus().getCode()).isEqualTo(Code.ABORTED);
        assertThat(t.getStatus().getDescription()).isEqualTo("bad streaming stub");
        GrpcServiceServerTest.checkRequestLogStatus(( grpcStatus) -> {
            assertThat(grpcStatus.getCode()).isEqualTo(Code.ABORTED);
            assertThat(grpcStatus.getDescription()).isEqualTo("bad streaming stub");
        });
    }

    @Test
    public void requestContextSet() throws Exception {
        final StreamRecorder<SimpleResponse> response = StreamRecorder.create();
        final StreamObserver<SimpleRequest> request = streamingClient.checkRequestContext(response);
        request.onNext(GrpcTestUtil.REQUEST_MESSAGE);
        request.onNext(GrpcTestUtil.REQUEST_MESSAGE);
        request.onNext(GrpcTestUtil.REQUEST_MESSAGE);
        request.onCompleted();
        response.awaitCompletion();
        final SimpleResponse expectedResponse = SimpleResponse.newBuilder().setPayload(Payload.newBuilder().setBody(ByteString.copyFromUtf8("3"))).build();
        assertThat(response.getValues()).containsExactly(expectedResponse);
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/CheckRequestContext");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(expectedResponse);
        });
    }

    @Test
    public void tooLargeRequest_uncompressed() throws Exception {
        final SimpleRequest request = GrpcServiceServerTest.newLargeRequest();
        final StatusRuntimeException t = ((StatusRuntimeException) (catchThrowable(() -> blockingClient.staticUnaryCall(request))));
        assertThat(t.getStatus().getCode()).isEqualTo(CANCELLED);
        GrpcServiceServerTest.checkRequestLogStatus(( grpcStatus) -> {
            assertThat(grpcStatus.getCode()).isEqualTo(RESOURCE_EXHAUSTED);
        });
    }

    @Test
    public void tooLargeRequest_compressed() throws Exception {
        final SimpleRequest request = GrpcServiceServerTest.newLargeRequest();
        final StatusRuntimeException t = ((StatusRuntimeException) (catchThrowable(() -> blockingClient.withCompression("gzip").staticUnaryCall(request))));
        assertThat(t.getStatus().getCode()).isEqualTo(CANCELLED);
        GrpcServiceServerTest.checkRequestLogStatus(( grpcStatus) -> {
            assertThat(grpcStatus.getCode()).isEqualTo(RESOURCE_EXHAUSTED);
        });
    }

    @Test
    public void uncompressedClient_compressedEndpoint() throws Exception {
        final ManagedChannel nonDecompressingChannel = ManagedChannelBuilder.forAddress("127.0.0.1", GrpcServiceServerTest.server.httpPort()).decompressorRegistry(DecompressorRegistry.emptyInstance().with(NONE, false)).usePlaintext().build();
        final UnitTestServiceBlockingStub client = UnitTestServiceGrpc.newBlockingStub(nonDecompressingChannel);
        assertThat(client.staticUnaryCallSetsMessageCompression(GrpcTestUtil.REQUEST_MESSAGE)).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        nonDecompressingChannel.shutdownNow();
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCallSetsMessageCompression");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void compressedClient_compressedEndpoint() throws Exception {
        assertThat(blockingClient.staticUnaryCallSetsMessageCompression(GrpcTestUtil.REQUEST_MESSAGE)).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCallSetsMessageCompression");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void clientSocketClosedBeforeHalfCloseHttp2() throws Exception {
        GrpcServiceServerTest.clientSocketClosedBeforeHalfClose("h2c");
    }

    @Test
    public void clientSocketClosedBeforeHalfCloseHttp1() throws Exception {
        GrpcServiceServerTest.clientSocketClosedBeforeHalfClose("h1c");
    }

    @Test
    public void clientSocketClosedAfterHalfCloseBeforeCloseCancelsHttp2() throws Exception {
        GrpcServiceServerTest.clientSocketClosedAfterHalfCloseBeforeCloseCancels(H2C);
    }

    @Test
    public void clientSocketClosedAfterHalfCloseBeforeCloseCancelsHttp1() throws Exception {
        GrpcServiceServerTest.clientSocketClosedAfterHalfCloseBeforeCloseCancels(H1C);
    }

    @Test
    public void unframed() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/protobuf"), GrpcTestUtil.REQUEST_MESSAGE.toByteArray()).aggregate().get();
        final SimpleResponse message = SimpleResponse.parseFrom(response.content().array());
        assertThat(message).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        assertThat(response.headers().getInt(CONTENT_LENGTH)).isEqualTo(response.content().length());
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void unframed_acceptEncoding() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/protobuf").set(GRPC_ACCEPT_ENCODING, "gzip,none"), GrpcTestUtil.REQUEST_MESSAGE.toByteArray()).aggregate().get();
        final SimpleResponse message = SimpleResponse.parseFrom(response.content().array());
        assertThat(message).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        assertThat(response.headers().getInt(CONTENT_LENGTH)).isEqualTo(response.content().length());
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void unframed_streamingApi() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticStreamedOutputCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/protobuf"), StreamingOutputCallRequest.getDefaultInstance().toByteArray()).aggregate().get();
        assertThat(response.status()).isEqualTo(BAD_REQUEST);
        GrpcServiceServerTest.assertNoRpcContent();
    }

    @Test
    public void unframed_noContentType() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()), GrpcTestUtil.REQUEST_MESSAGE.toByteArray()).aggregate().get();
        assertThat(response.status()).isEqualTo(UNSUPPORTED_MEDIA_TYPE);
        GrpcServiceServerTest.assertNoRpcContent();
    }

    @Test
    public void unframed_grpcEncoding() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/protobuf").set(GRPC_ENCODING, "gzip"), GrpcTestUtil.REQUEST_MESSAGE.toByteArray()).aggregate().get();
        assertThat(response.status()).isEqualTo(UNSUPPORTED_MEDIA_TYPE);
        GrpcServiceServerTest.assertNoRpcContent();
    }

    @Test
    public void unframed_serviceError() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final SimpleRequest request = SimpleRequest.newBuilder().setResponseStatus(EchoStatus.newBuilder().setCode(DEADLINE_EXCEEDED.getCode().value())).build();
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/protobuf"), request.toByteArray()).aggregate().get();
        assertThat(response.status()).isEqualTo(INTERNAL_SERVER_ERROR);
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(request);
            assertThat(grpcStatus).isNotNull();
            assertThat(grpcStatus.getCode()).isEqualTo(UNKNOWN);
        });
    }

    @Test
    public void grpcWeb() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getStaticUnaryCallMethod().getFullMethodName()).set(CONTENT_TYPE, "application/grpc-web"), GrpcTestUtil.uncompressedFrame(GrpcTestUtil.requestByteBuf())).aggregate().get();
        final byte[] serializedStatusHeader = "grpc-status: 0\r\n".getBytes(StandardCharsets.US_ASCII);
        final byte[] serializedTrailers = Bytes.concat(new byte[]{ TRAILERS_FRAME_HEADER }, Ints.toByteArray(serializedStatusHeader.length), serializedStatusHeader);
        assertThat(response.content().array()).containsExactly(Bytes.concat(GrpcTestUtil.uncompressedFrame(GrpcTestUtil.protoByteBuf(GrpcTestUtil.RESPONSE_MESSAGE)), serializedTrailers));
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void grpcWeb_error() throws Exception {
        final HttpClient client = HttpClient.of(GrpcServiceServerTest.server.httpUri("/"));
        final AggregatedHttpMessage response = client.execute(HttpHeaders.of(POST, UnitTestServiceGrpc.getErrorWithMessageMethod().getFullMethodName()).set(CONTENT_TYPE, "application/grpc-web"), GrpcTestUtil.uncompressedFrame(GrpcTestUtil.requestByteBuf())).aggregate().get();
        assertThat(response.headers()).contains(entry(GrpcHeaderNames.GRPC_STATUS, "10"), entry(GrpcHeaderNames.GRPC_MESSAGE, "aborted call"));
    }

    @Test
    public void json() throws Exception {
        final AtomicReference<HttpHeaders> requestHeaders = new AtomicReference<>();
        final UnitTestServiceBlockingStub jsonStub = decorator(( client) -> new SimpleDecoratingClient<HttpRequest, HttpResponse>(client) {
            @Override
            public HttpResponse execute(ClientRequestContext ctx, HttpRequest req) throws Exception {
                requestHeaders.set(req.headers());
                return delegate().execute(ctx, req);
            }
        }).build(UnitTestServiceBlockingStub.class);
        final SimpleResponse response = jsonStub.staticUnaryCall(GrpcTestUtil.REQUEST_MESSAGE);
        assertThat(response).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        assertThat(requestHeaders.get().get(CONTENT_TYPE)).isEqualTo("application/grpc+json");
        GrpcServiceServerTest.checkRequestLog(( rpcReq, rpcRes, grpcStatus) -> {
            assertThat(rpcReq.method()).isEqualTo("armeria.grpc.testing.UnitTestService/StaticUnaryCall");
            assertThat(rpcReq.params()).containsExactly(GrpcTestUtil.REQUEST_MESSAGE);
            assertThat(rpcRes.get()).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        });
    }

    @Test
    public void noMaxMessageSize() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", GrpcServiceServerTest.serverWithNoMaxMessageSize.httpPort()).usePlaintext().build();
        try {
            UnitTestServiceBlockingStub stub = UnitTestServiceGrpc.newBlockingStub(channel);
            assertThat(stub.staticUnaryCall(GrpcTestUtil.REQUEST_MESSAGE)).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        } finally {
            channel.shutdownNow();
        }
    }

    @Test
    public void longMaxRequestLimit() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", GrpcServiceServerTest.serverWithLongMaxRequestLimit.httpPort()).usePlaintext().build();
        try {
            UnitTestServiceBlockingStub stub = UnitTestServiceGrpc.newBlockingStub(channel);
            assertThat(stub.staticUnaryCall(GrpcTestUtil.REQUEST_MESSAGE)).isEqualTo(GrpcTestUtil.RESPONSE_MESSAGE);
        } finally {
            channel.shutdownNow();
        }
    }

    @Test
    public void reflectionService() {
        ServerReflectionStub stub = ServerReflectionGrpc.newStub(GrpcServiceServerTest.channel);
        AtomicReference<ServerReflectionResponse> response = new AtomicReference<>();
        StreamObserver<ServerReflectionRequest> request = stub.serverReflectionInfo(new StreamObserver<ServerReflectionResponse>() {
            @Override
            public void onNext(ServerReflectionResponse value) {
                response.set(value);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
            }
        });
        request.onNext(ServerReflectionRequest.newBuilder().setListServices("").build());
        request.onCompleted();
        await().untilAsserted(() -> {
            assertThat(response).doesNotHaveValue(null);
            // Instead of making this test depend on every other one, just check that there is at least
            // two services returned corresponding to UnitTestService and ProtoReflectionService.
            assertThat(response.get().getListServicesResponse().getServiceList()).hasSizeGreaterThanOrEqualTo(2);
        });
    }

    @FunctionalInterface
    private interface RequestLogChecker {
        void check(RpcRequest rpcReq, RpcResponse rpcRes, @Nullable
        Status grpcStatus) throws Exception;
    }

    @FunctionalInterface
    private interface RequestLogStatusChecker {
        void check(Status grpcStatus) throws Exception;
    }
}

