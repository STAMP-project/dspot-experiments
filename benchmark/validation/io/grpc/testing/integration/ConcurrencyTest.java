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
package io.grpc.testing.integration;


import StreamingOutputCallRequest.Builder;
import TestServiceGrpc.TestServiceStub;
import com.google.common.base.Preconditions;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.integration.Messages.ResponseParameters;
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest;
import io.grpc.testing.integration.Messages.StreamingOutputCallResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that gRPC clients and servers can handle concurrent RPCs.
 *
 * <p>These tests use TLS to make them more realistic, and because we'd like to test the thread
 * safety of the TLS-related code paths as well.
 */
// TODO: Consider augmenting this class to perform non-streaming, client streaming, and
// bidirectional streaming requests also.
@RunWith(JUnit4.class)
public class ConcurrencyTest {
    @Rule
    public final Timeout globalTimeout = Timeout.seconds(10);

    /**
     * A response observer that signals a {@code CountDownLatch} when the proper number of responses
     * arrives and the server signals that the RPC is complete.
     */
    private static class SignalingResponseObserver implements StreamObserver<StreamingOutputCallResponse> {
        public SignalingResponseObserver(CountDownLatch responsesDoneSignal) {
            this.responsesDoneSignal = responsesDoneSignal;
        }

        @Override
        public void onCompleted() {
            Preconditions.checkState(((numResponsesReceived) == (ConcurrencyTest.NUM_RESPONSES_PER_REQUEST)));
            responsesDoneSignal.countDown();
        }

        @Override
        public void onError(Throwable error) {
            // This should never happen. If it does happen, ensure that the error is visible.
            error.printStackTrace();
        }

        @Override
        public void onNext(StreamingOutputCallResponse response) {
            (numResponsesReceived)++;
        }

        private final CountDownLatch responsesDoneSignal;

        private int numResponsesReceived = 0;
    }

    /**
     * A client worker task that waits until all client workers are ready, then sends a request for a
     * server-streaming RPC and arranges for a {@code CountDownLatch} to be signaled when the RPC is
     * complete.
     */
    private class ClientWorker implements Runnable {
        public ClientWorker(CyclicBarrier startBarrier, CountDownLatch responsesDoneSignal) {
            this.startBarrier = startBarrier;
            this.responsesDoneSignal = responsesDoneSignal;
        }

        @Override
        public void run() {
            try {
                // Prepare the request.
                StreamingOutputCallRequest.Builder requestBuilder = StreamingOutputCallRequest.newBuilder();
                for (int i = 0; i < (ConcurrencyTest.NUM_RESPONSES_PER_REQUEST); i++) {
                    requestBuilder.addResponseParameters(ResponseParameters.newBuilder().setSize(1000).setIntervalUs(0));// No delay between responses, for maximum concurrency.

                }
                StreamingOutputCallRequest request = requestBuilder.build();
                // Wait until all client worker threads are poised & ready, then send the request. This way
                // all clients send their requests at approximately the same time.
                startBarrier.await();
                clientStub.streamingOutputCall(request, new ConcurrencyTest.SignalingResponseObserver(responsesDoneSignal));
            } catch (Exception e) {
                throw e instanceof RuntimeException ? ((RuntimeException) (e)) : new RuntimeException(e);
            }
        }

        private final CyclicBarrier startBarrier;

        private final CountDownLatch responsesDoneSignal;
    }

    private static final int NUM_SERVER_THREADS = 10;

    private static final int NUM_CONCURRENT_REQUESTS = 100;

    private static final int NUM_RESPONSES_PER_REQUEST = 100;

    private Server server;

    private ManagedChannel clientChannel;

    private TestServiceStub clientStub;

    private ScheduledExecutorService serverExecutor;

    private ExecutorService clientExecutor;

    /**
     * Tests that gRPC can handle concurrent server-streaming RPCs.
     */
    @Test
    public void serverStreamingTest() throws Exception {
        CyclicBarrier startBarrier = new CyclicBarrier(ConcurrencyTest.NUM_CONCURRENT_REQUESTS);
        CountDownLatch responsesDoneSignal = new CountDownLatch(ConcurrencyTest.NUM_CONCURRENT_REQUESTS);
        for (int i = 0; i < (ConcurrencyTest.NUM_CONCURRENT_REQUESTS); i++) {
            clientExecutor.execute(new ConcurrencyTest.ClientWorker(startBarrier, responsesDoneSignal));
        }
        // Wait until the clients all receive their complete RPC response streams.
        responsesDoneSignal.await();
    }
}

