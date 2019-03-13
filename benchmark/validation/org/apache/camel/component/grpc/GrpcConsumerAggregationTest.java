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


import PingPongGrpc.PingPongBlockingStub;
import PingPongGrpc.PingPongStub;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrpcConsumerAggregationTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcConsumerAggregationTest.class);

    private static final int GRPC_SYNC_REQUEST_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_ASYNC_REQUEST_TEST_PORT = AvailablePortFinder.getNextAvailable();

    private static final int GRPC_TEST_PING_ID = 1;

    private static final String GRPC_TEST_PING_VALUE = "PING";

    private static final String GRPC_TEST_PONG_VALUE = "PONG";

    private ManagedChannel syncRequestChannel;

    private ManagedChannel asyncRequestChannel;

    private PingPongBlockingStub blockingStub;

    private PingPongStub nonBlockingStub;

    private PingPongStub asyncNonBlockingStub;

    @Test
    public void testSyncSyncMethodInSync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingSyncSync method blocking test start");
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        PongResponse pongResponse = blockingStub.pingSyncSync(pingRequest);
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testSyncAsyncMethodInSync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingSyncAsync method blocking test start");
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        Iterator<PongResponse> pongResponseIter = blockingStub.pingSyncAsync(pingRequest);
        while (pongResponseIter.hasNext()) {
            PongResponse pongResponse = pongResponseIter.next();
            assertNotNull(pongResponse);
            assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
            assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
        } 
    }

    @Test
    public void testSyncSyncMethodInAsync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingSyncSync method aync test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerAggregationTest.PongResponseStreamObserver responseObserver = new GrpcConsumerAggregationTest.PongResponseStreamObserver(latch);
        nonBlockingStub.pingSyncSync(pingRequest, responseObserver);
        latch.await(5, TimeUnit.SECONDS);
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testSyncAsyncMethodInAsync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingSyncAsync method aync test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerAggregationTest.PongResponseStreamObserver responseObserver = new GrpcConsumerAggregationTest.PongResponseStreamObserver(latch);
        nonBlockingStub.pingSyncAsync(pingRequest, responseObserver);
        latch.await(5, TimeUnit.SECONDS);
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testAsyncSyncMethodInAsync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingAsyncSync method aync test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerAggregationTest.PongResponseStreamObserver responseObserver = new GrpcConsumerAggregationTest.PongResponseStreamObserver(latch);
        StreamObserver<PingRequest> requestObserver = asyncNonBlockingStub.pingAsyncSync(responseObserver);
        requestObserver.onNext(pingRequest);
        requestObserver.onNext(pingRequest);
        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
    }

    @Test
    public void testAsyncAsyncMethodInAsync() throws Exception {
        GrpcConsumerAggregationTest.LOG.info("gRPC pingAsyncAsync method aync test start");
        final CountDownLatch latch = new CountDownLatch(1);
        PingRequest pingRequest = PingRequest.newBuilder().setPingName(GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE).setPingId(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID).build();
        GrpcConsumerAggregationTest.PongResponseStreamObserver responseObserver = new GrpcConsumerAggregationTest.PongResponseStreamObserver(latch);
        StreamObserver<PingRequest> requestObserver = asyncNonBlockingStub.pingAsyncAsync(responseObserver);
        requestObserver.onNext(pingRequest);
        requestObserver.onNext(pingRequest);
        requestObserver.onCompleted();
        latch.await(5, TimeUnit.SECONDS);
        PongResponse pongResponse = responseObserver.getPongResponse();
        assertNotNull(pongResponse);
        assertEquals(GrpcConsumerAggregationTest.GRPC_TEST_PING_ID, pongResponse.getPongId());
        assertEquals(((GrpcConsumerAggregationTest.GRPC_TEST_PING_VALUE) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE)), pongResponse.getPongName());
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
        }

        @Override
        public void onError(Throwable t) {
            GrpcConsumerAggregationTest.LOG.info("Exception", t);
            latch.countDown();
        }

        @Override
        public void onCompleted() {
            latch.countDown();
        }
    }

    public class GrpcMessageBuilder {
        public PongResponse buildPongResponse(PingRequest pingRequest) {
            return PongResponse.newBuilder().setPongName(((pingRequest.getPingName()) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE))).setPongId(pingRequest.getPingId()).build();
        }

        public PongResponse buildAsyncPongResponse(List<PingRequest> pingRequests) {
            return PongResponse.newBuilder().setPongName(((pingRequests.get(0).getPingName()) + (GrpcConsumerAggregationTest.GRPC_TEST_PONG_VALUE))).setPongId(pingRequests.get(0).getPingId()).build();
        }
    }
}

