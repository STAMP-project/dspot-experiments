/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.fn.harness.control;


import BeamFnApi.InstructionRequest.RequestCase;
import BeamFnApi.InstructionRequest.RequestCase.PROCESS_BUNDLE;
import BeamFnApi.InstructionRequest.RequestCase.REGISTER;
import BeamFnApi.InstructionResponse;
import BeamFnApi.InstructionResponse.Builder;
import BeamFnApi.ProcessBundleRequest;
import BeamFnApi.ProcessBundleResponse;
import BeamFnApi.RegisterRequest;
import Endpoints.ApiServiceDescriptor;
import java.util.EnumMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnControlGrpc;
import org.apache.beam.model.pipeline.v1.Endpoints;
import org.apache.beam.sdk.fn.stream.OutboundObserverFactory;
import org.apache.beam.sdk.fn.test.InProcessManagedChannelFactory;
import org.apache.beam.sdk.fn.test.TestStreams;
import org.apache.beam.sdk.function.ThrowingFunction;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.Server;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.inprocess.InProcessServerBuilder;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.CallStreamObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BeamFnControlClient}.
 */
@RunWith(JUnit4.class)
public class BeamFnControlClientTest {
    private static final InstructionRequest SUCCESSFUL_REQUEST = BeamFnApi.InstructionRequest.newBuilder().setInstructionId("1L").setProcessBundle(ProcessBundleRequest.getDefaultInstance()).build();

    private static final InstructionResponse SUCCESSFUL_RESPONSE = InstructionResponse.newBuilder().setInstructionId("1L").setProcessBundle(ProcessBundleResponse.getDefaultInstance()).build();

    private static final InstructionRequest UNKNOWN_HANDLER_REQUEST = BeamFnApi.InstructionRequest.newBuilder().setInstructionId("2L").build();

    private static final InstructionResponse UNKNOWN_HANDLER_RESPONSE = InstructionResponse.newBuilder().setInstructionId("2L").setError(("Unknown InstructionRequest type " + (RequestCase.REQUEST_NOT_SET))).build();

    private static final RuntimeException FAILURE = new RuntimeException("TestFailure");

    private static final InstructionRequest FAILURE_REQUEST = BeamFnApi.InstructionRequest.newBuilder().setInstructionId("3L").setRegister(RegisterRequest.getDefaultInstance()).build();

    private static final InstructionResponse FAILURE_RESPONSE = InstructionResponse.newBuilder().setInstructionId("3L").setError(getStackTraceAsString(BeamFnControlClientTest.FAILURE)).build();

    @Test
    public void testDelegation() throws Exception {
        AtomicBoolean clientClosedStream = new AtomicBoolean();
        BlockingQueue<BeamFnApi.InstructionResponse> values = new LinkedBlockingQueue<>();
        BlockingQueue<StreamObserver<BeamFnApi.InstructionRequest>> outboundServerObservers = new LinkedBlockingQueue<>();
        CallStreamObserver<BeamFnApi.InstructionResponse> inboundServerObserver = TestStreams.withOnNext(values::add).withOnCompleted(() -> clientClosedStream.set(true)).build();
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = ApiServiceDescriptor.newBuilder().setUrl((((this.getClass().getName()) + "-") + (UUID.randomUUID().toString()))).build();
        Server server = InProcessServerBuilder.forName(apiServiceDescriptor.getUrl()).addService(new BeamFnControlGrpc.BeamFnControlImplBase() {
            @Override
            public StreamObserver<BeamFnApi.InstructionResponse> control(StreamObserver<BeamFnApi.InstructionRequest> outboundObserver) {
                Uninterruptibles.putUninterruptibly(outboundServerObservers, outboundObserver);
                return inboundServerObserver;
            }
        }).build();
        server.start();
        try {
            EnumMap<BeamFnApi.InstructionRequest.RequestCase, ThrowingFunction<BeamFnApi.InstructionRequest, BeamFnApi.InstructionResponse.Builder>> handlers = new EnumMap(RequestCase.class);
            handlers.put(PROCESS_BUNDLE, ( value) -> BeamFnApi.InstructionResponse.newBuilder().setProcessBundle(BeamFnApi.ProcessBundleResponse.getDefaultInstance()));
            handlers.put(REGISTER, ( value) -> {
                throw FAILURE;
            });
            BeamFnControlClient client = new BeamFnControlClient("", apiServiceDescriptor, InProcessManagedChannelFactory.create(), OutboundObserverFactory.trivial(), handlers);
            // Get the connected client and attempt to send and receive an instruction
            StreamObserver<BeamFnApi.InstructionRequest> outboundServerObserver = outboundServerObservers.take();
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<Void> future = executor.submit(() -> {
                client.processInstructionRequests(executor);
                return null;
            });
            outboundServerObserver.onNext(BeamFnControlClientTest.SUCCESSFUL_REQUEST);
            Assert.assertEquals(BeamFnControlClientTest.SUCCESSFUL_RESPONSE, values.take());
            // Ensure that conversion of an unknown request type is properly converted to a
            // failure response.
            outboundServerObserver.onNext(BeamFnControlClientTest.UNKNOWN_HANDLER_REQUEST);
            Assert.assertEquals(BeamFnControlClientTest.UNKNOWN_HANDLER_RESPONSE, values.take());
            // Ensure that all exceptions are caught and translated to failures
            outboundServerObserver.onNext(BeamFnControlClientTest.FAILURE_REQUEST);
            Assert.assertEquals(BeamFnControlClientTest.FAILURE_RESPONSE, values.take());
            // Ensure that the server completing the stream translates to the completable future
            // being completed allowing for a successful shutdown of the client.
            outboundServerObserver.onCompleted();
            future.get();
        } finally {
            server.shutdownNow();
        }
    }

    @Test
    public void testJavaErrorResponse() throws Exception {
        BlockingQueue<StreamObserver<BeamFnApi.InstructionRequest>> outboundServerObservers = new LinkedBlockingQueue<>();
        BlockingQueue<Throwable> error = new LinkedBlockingQueue<>();
        CallStreamObserver<BeamFnApi.InstructionResponse> inboundServerObserver = TestStreams.<BeamFnApi.InstructionResponse>withOnNext(( response) -> fail(String.format("Unexpected Response %s", response))).withOnError(error::add).build();
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = ApiServiceDescriptor.newBuilder().setUrl((((this.getClass().getName()) + "-") + (UUID.randomUUID().toString()))).build();
        Server server = InProcessServerBuilder.forName(apiServiceDescriptor.getUrl()).addService(new BeamFnControlGrpc.BeamFnControlImplBase() {
            @Override
            public StreamObserver<BeamFnApi.InstructionResponse> control(StreamObserver<BeamFnApi.InstructionRequest> outboundObserver) {
                Uninterruptibles.putUninterruptibly(outboundServerObservers, outboundObserver);
                return inboundServerObserver;
            }
        }).build();
        server.start();
        try {
            EnumMap<BeamFnApi.InstructionRequest.RequestCase, ThrowingFunction<BeamFnApi.InstructionRequest, BeamFnApi.InstructionResponse.Builder>> handlers = new EnumMap(RequestCase.class);
            handlers.put(REGISTER, ( value) -> {
                throw new Error("Test Error");
            });
            BeamFnControlClient client = new BeamFnControlClient("", apiServiceDescriptor, InProcessManagedChannelFactory.create(), OutboundObserverFactory.trivial(), handlers);
            // Get the connected client and attempt to send and receive an instruction
            StreamObserver<BeamFnApi.InstructionRequest> outboundServerObserver = outboundServerObservers.take();
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<Void> future = executor.submit(() -> {
                client.processInstructionRequests(executor);
                return null;
            });
            // Ensure that all exceptions are caught and translated to failures
            outboundServerObserver.onNext(InstructionRequest.newBuilder().setInstructionId("0").setRegister(org.apache.beam.model.fnexecution.v1.BeamFnApi.RegisterRequest.getDefaultInstance()).build());
            // There should be an error reported to the StreamObserver.
            Assert.assertThat(error.take(), Matchers.not(Matchers.nullValue()));
            // Ensure that the client shuts down when an Error is thrown from the harness
            try {
                future.get();
                throw new IllegalStateException("The future should have terminated with an error");
            } catch (ExecutionException errorWrapper) {
                Assert.assertThat(errorWrapper.getCause().getMessage(), Matchers.containsString("Test Error"));
            }
        } finally {
            server.shutdownNow();
        }
    }
}

