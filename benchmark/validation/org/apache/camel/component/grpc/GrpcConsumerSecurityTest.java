/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.grpc;


import GrpcConstants.GRPC_EVENT_TYPE_HEADER;
import GrpcConstants.GRPC_EVENT_TYPE_ON_NEXT;
import GrpcConstants.GRPC_METHOD_NAME_HEADER;
import PingPongGrpc.PingPongStub;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrpcConsumerSecurityTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcConsumerSecurityTest.class);

    private static final int GRPC_TLS_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_JWT_CORRECT_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_JWT_INCORRECT_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_TEST_PING_ID = 1;

    private static final String GRPC_TEST_PING_VALUE = "PING";

    private static final String GRPC_TEST_PONG_VALUE = "PONG";

    private static final String GRPC_JWT_CORRECT_SECRET = "correctsecret";

    private static final String GRPC_JWT_INCORRECT_SECRET = "incorrectsecret";

    private ManagedChannel tlsChannel;

    private ManagedChannel jwtCorrectChannel;

    private ManagedChannel jwtIncorrectChannel;

    private PingPongStub tlsAsyncStub;

    private PingPongStub jwtCorrectAsyncStub;

    private PingPongStub jwtIncorrectAsyncStub;

    @Test
    public void testWithEnableTLS() throws Exception {
        GrpcConsumerSecurityTest.LOG.info("gRPC pingAsyncSync method aync test with TLS enable start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerSecurityTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerSecurityTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerSecurityTest.PongResponseStreamObserver responseObserver = new GrpcConsumerSecurityTest.PongResponseStreamObserver(latch);
        StreamObserver<PingRequest> requestObserver = tlsAsyncStub.pingAsyncSync(responseObserver);
        requestObserver.onNext(pingRequest);
        latch.await(5, TimeUnit.SECONDS);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:tls-enable");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(GRPC_EVENT_TYPE_HEADER, GRPC_EVENT_TYPE_ON_NEXT);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(GRPC_METHOD_NAME_HEADER, "pingAsyncSync");
        mockEndpoint.assertIsSatisfied();
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerSecurityTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerSecurityTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerSecurityTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testWithCorrectJWT() throws Exception {
        GrpcConsumerSecurityTest.LOG.info("gRPC pingAsyncSync method aync test with correct JWT start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerSecurityTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerSecurityTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerSecurityTest.PongResponseStreamObserver responseObserver = new GrpcConsumerSecurityTest.PongResponseStreamObserver(latch);
        StreamObserver<PingRequest> requestObserver = jwtCorrectAsyncStub.pingAsyncSync(responseObserver);
        requestObserver.onNext(pingRequest);
        latch.await(5, TimeUnit.SECONDS);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:jwt-correct-secret");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(GRPC_EVENT_TYPE_HEADER, GRPC_EVENT_TYPE_ON_NEXT);
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder(GRPC_METHOD_NAME_HEADER, "pingAsyncSync");
        mockEndpoint.assertIsSatisfied();
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerSecurityTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerSecurityTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerSecurityTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testWithIncorrectJWT() throws Exception {
        GrpcConsumerSecurityTest.LOG.info("gRPC pingAsyncSync method aync test with correct JWT start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerSecurityTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerSecurityTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerSecurityTest.PongResponseStreamObserver responseObserver = new GrpcConsumerSecurityTest.PongResponseStreamObserver(latch);
        StreamObserver<PingRequest> requestObserver = jwtIncorrectAsyncStub.pingAsyncSync(responseObserver);
        requestObserver.onNext(pingRequest);
        latch.await(5, TimeUnit.SECONDS);
        MockEndpoint mockEndpoint = getMockEndpoint("mock:jwt-incorrect-secret");
        mockEndpoint.expectedMessageCount(0);
        mockEndpoint.assertIsSatisfied();
    }

    public class PongResponseStreamObserver implements StreamObserver<PongResponse> {
        private PongResponse pongResponse;

        private final CountDownLatch latch;

        public PongResponseStreamObserver(CountDownLatch latch) {
            this.latch = latch;
        }

        public PongResponse getPongResponse() {
            return pongResponse;
        }

        @Override
        public void onNext(PongResponse value) {
            pongResponse = value;
            latch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            GrpcConsumerSecurityTest.LOG.info("Exception", t);
            latch.countDown();
        }

        @Override
        public void onCompleted() {
            latch.countDown();
        }
    }

    public class GrpcMessageBuilder {
        public PongResponse buildAsyncPongResponse(PingRequest pingRequests) {
            return PongResponse.newBuilder().setPongName(((pingRequests.getPingName()) + (GrpcConsumerSecurityTest.GRPC_TEST_PONG_VALUE))).setPongId(pingRequests.getPingId()).build();
        }
    }
}

