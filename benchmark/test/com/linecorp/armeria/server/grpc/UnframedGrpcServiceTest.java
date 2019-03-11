/**
 * Copyright 2018 LINE Corporation
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


import HttpStatus.CLIENT_CLOSED_REQUEST;
import HttpStatus.OK;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.grpc.testing.TestServiceGrpc.TestServiceImplBase;
import com.linecorp.armeria.protobuf.EmptyProtos.Empty;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.common.EventLoopRule;
import io.grpc.stub.StreamObserver;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


public class UnframedGrpcServiceTest {
    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    private static class TestService extends TestServiceImplBase {
        @Override
        public void emptyCall(Empty request, StreamObserver<Empty> responseObserver) {
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    private static final UnframedGrpcServiceTest.TestService testService = new UnframedGrpcServiceTest.TestService();

    private static final int MAX_MESSAGE_BYTES = 1024;

    private ServiceRequestContext ctx;

    private HttpRequest request;

    private UnframedGrpcService unframedGrpcService;

    @Test
    public void statusOk() throws Exception {
        unframedGrpcService = ((UnframedGrpcService) (new GrpcServiceBuilder().addService(UnframedGrpcServiceTest.testService).setMaxInboundMessageSizeBytes(UnframedGrpcServiceTest.MAX_MESSAGE_BYTES).setMaxOutboundMessageSizeBytes(UnframedGrpcServiceTest.MAX_MESSAGE_BYTES).supportedSerializationFormats(GrpcSerializationFormats.values()).enableUnframedRequests(true).build()));
        final HttpResponse response = unframedGrpcService.serve(ctx, request);
        final AggregatedHttpMessage aggregatedHttpMessage = response.aggregate().get();
        assertThat(aggregatedHttpMessage.status()).isEqualTo(OK);
        assertThat(aggregatedHttpMessage.contentUtf8()).isEqualTo("{}");
    }

    @Test
    public void statusCancelled() throws Exception {
        final UnframedGrpcServiceTest.TestService spyTestService = Mockito.spy(UnframedGrpcServiceTest.testService);
        Mockito.doThrow(Status.CANCELLED.withDescription("grpc error message").asRuntimeException()).when(spyTestService).emptyCall(any(), any());
        unframedGrpcService = ((UnframedGrpcService) (new GrpcServiceBuilder().addService(spyTestService).setMaxInboundMessageSizeBytes(UnframedGrpcServiceTest.MAX_MESSAGE_BYTES).setMaxOutboundMessageSizeBytes(UnframedGrpcServiceTest.MAX_MESSAGE_BYTES).supportedSerializationFormats(GrpcSerializationFormats.values()).enableUnframedRequests(true).build()));
        final HttpResponse response = unframedGrpcService.serve(ctx, request);
        final AggregatedHttpMessage aggregatedHttpMessage = response.aggregate().get();
        assertThat(aggregatedHttpMessage.status()).isEqualTo(CLIENT_CLOSED_REQUEST);
        assertThat(aggregatedHttpMessage.contentUtf8()).isEqualTo(("http-status: 499, Client Closed Request\n" + ("Caused by: \n" + "grpc-status: 1, CANCELLED, grpc error message")));
    }
}

