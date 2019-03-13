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


import PingPongGrpc.PingPongImplBase;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("TODO: investigate for Camel 3.0")
public class GrpcProducerAsyncTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcProducerAsyncTest.class);

    private static final int GRPC_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_TEST_PING_ID = 1;

    private static final int GRPC_TEST_PONG_ID01 = 1;

    private static final int GRPC_TEST_PONG_ID02 = 2;

    private static final String GRPC_TEST_PING_VALUE = "PING";

    private static final String GRPC_TEST_PONG_VALUE = "PONG";

    private static Server grpcServer;

    private Object asyncPongResponse;

    @Test
    public void testPingSyncSyncMethodInvocation() throws Exception {
        GrpcProducerAsyncTest.LOG.info("gRPC PingSyncSync method test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE).setPingId(GrpcProducerAsyncTest.GRPC_TEST_PING_ID).build();
        // Testing sync service call with async style invocation
        template.asyncCallbackSendBody("direct:grpc-sync-sync", pingRequest, new SynchronizationAdapter() {
            @Override
            public void onComplete(Exchange exchange) {
                asyncPongResponse = exchange.getOut().getBody();
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertNotNull(asyncPongResponse);
        assertTrue(((asyncPongResponse) instanceof List));
        @SuppressWarnings("unchecked")
        List<PongResponse> asyncPongResponseList = ((List<PongResponse>) (asyncPongResponse));
        assertEquals(1, asyncPongResponseList.size());
        assertEquals(asyncPongResponseList.get(0).getPongId(), GrpcProducerAsyncTest.GRPC_TEST_PING_ID);
        assertEquals(asyncPongResponseList.get(0).getPongName(), ((GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE)));
    }

    @Test
    public void testPingSyncAsyncMethodInvocation() throws Exception {
        GrpcProducerAsyncTest.LOG.info("gRPC PingSyncAsync method test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE).setPingId(GrpcProducerAsyncTest.GRPC_TEST_PING_ID).build();
        // Testing async service call
        template.asyncCallbackSendBody("direct:grpc-sync-async", pingRequest, new SynchronizationAdapter() {
            @Override
            public void onComplete(Exchange exchange) {
                asyncPongResponse = exchange.getOut().getBody();
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertNotNull(asyncPongResponse);
        assertTrue(((asyncPongResponse) instanceof List));
        @SuppressWarnings("unchecked")
        List<PongResponse> asyncPongResponseList = ((List<PongResponse>) (asyncPongResponse));
        assertEquals(2, asyncPongResponseList.size());
        assertEquals(asyncPongResponseList.get(0).getPongId(), GrpcProducerAsyncTest.GRPC_TEST_PONG_ID01);
        assertEquals(asyncPongResponseList.get(1).getPongId(), GrpcProducerAsyncTest.GRPC_TEST_PONG_ID02);
        assertEquals(asyncPongResponseList.get(0).getPongName(), ((GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE)));
    }

    @Test
    public void testPingAsyncSyncMethodInvocation() throws Exception {
        GrpcProducerAsyncTest.LOG.info("gRPC PingAsyncSync method test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE).setPingId(GrpcProducerAsyncTest.GRPC_TEST_PING_ID).build();
        // Testing async service call with async style invocation
        template.asyncCallbackSendBody("direct:grpc-async-sync", pingRequest, new SynchronizationAdapter() {
            @Override
            public void onComplete(Exchange exchange) {
                asyncPongResponse = exchange.getOut().getBody();
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertNotNull(asyncPongResponse);
        assertTrue(((asyncPongResponse) instanceof List));
        @SuppressWarnings("unchecked")
        List<PongResponse> asyncPongResponseList = ((List<PongResponse>) (asyncPongResponse));
        assertEquals(1, asyncPongResponseList.size());
        assertEquals(asyncPongResponseList.get(0).getPongId(), GrpcProducerAsyncTest.GRPC_TEST_PING_ID);
        assertEquals(asyncPongResponseList.get(0).getPongName(), ((GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE)));
    }

    @Test
    public void testPingAsyncAsyncMethodInvocation() throws Exception {
        GrpcProducerAsyncTest.LOG.info("gRPC PingAsyncAsync method test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE).setPingId(GrpcProducerAsyncTest.GRPC_TEST_PING_ID).build();
        // Testing async service call with async style invocation
        template.asyncCallbackSendBody("direct:grpc-async-async", pingRequest, new SynchronizationAdapter() {
            @Override
            public void onComplete(Exchange exchange) {
                asyncPongResponse = exchange.getOut().getBody();
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertNotNull(asyncPongResponse);
        assertTrue(((asyncPongResponse) instanceof List));
        @SuppressWarnings("unchecked")
        List<PongResponse> asyncPongResponseList = ((List<PongResponse>) (asyncPongResponse));
        assertEquals(1, asyncPongResponseList.size());
        assertEquals(asyncPongResponseList.get(0).getPongId(), GrpcProducerAsyncTest.GRPC_TEST_PING_ID);
        assertEquals(asyncPongResponseList.get(0).getPongName(), ((GrpcProducerAsyncTest.GRPC_TEST_PING_VALUE) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE)));
    }

    /**
     * Test gRPC PingPong server implementation
     */
    static class PingPongImpl extends PingPongGrpc.PingPongImplBase {
        @Override
        public void pingSyncSync(PingRequest request, StreamObserver<PongResponse> responseObserver) {
            GrpcProducerAsyncTest.LOG.info("gRPC server received data from PingPong service PingId={} PingName={}", request.getPingId(), request.getPingName());
            PongResponse response = PongResponse.newBuilder().setPongName(((request.getPingName()) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE))).setPongId(request.getPingId()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void pingSyncAsync(PingRequest request, StreamObserver<PongResponse> responseObserver) {
            GrpcProducerAsyncTest.LOG.info("gRPC server received data from PingAsyncResponse service PingId={} PingName={}", request.getPingId(), request.getPingName());
            PongResponse response01 = PongResponse.newBuilder().setPongName(((request.getPingName()) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE))).setPongId(GrpcProducerAsyncTest.GRPC_TEST_PONG_ID01).build();
            PongResponse response02 = PongResponse.newBuilder().setPongName(((request.getPingName()) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE))).setPongId(GrpcProducerAsyncTest.GRPC_TEST_PONG_ID02).build();
            responseObserver.onNext(response01);
            responseObserver.onNext(response02);
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<PingRequest> pingAsyncSync(StreamObserver<PongResponse> responseObserver) {
            StreamObserver<PingRequest> requestObserver = new StreamObserver<PingRequest>() {
                @Override
                public void onNext(PingRequest request) {
                    PongResponse response = PongResponse.newBuilder().setPongName(((request.getPingName()) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE))).setPongId(request.getPingId()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    GrpcProducerAsyncTest.LOG.info(("Error in pingAsyncSync() " + (t.getMessage())));
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
            return requestObserver;
        }

        @Override
        public StreamObserver<PingRequest> pingAsyncAsync(StreamObserver<PongResponse> responseObserver) {
            StreamObserver<PingRequest> requestObserver = new StreamObserver<PingRequest>() {
                @Override
                public void onNext(PingRequest request) {
                    PongResponse response = PongResponse.newBuilder().setPongName(((request.getPingName()) + (GrpcProducerAsyncTest.GRPC_TEST_PONG_VALUE))).setPongId(request.getPingId()).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable t) {
                    GrpcProducerAsyncTest.LOG.info(("Error in pingAsyncAsync() " + (t.getMessage())));
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
            return requestObserver;
        }
    }
}

