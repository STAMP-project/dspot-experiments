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
package org.apache.beam.runners.fnexecution.state;


import BeamFnApi.StateGetResponse;
import BeamFnApi.StateRequest;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.sdk.fn.test.TestStreams;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link org.apache.beam.runners.fnexecution.state.GrpcStateService}.
 */
@RunWith(JUnit4.class)
public class GrpcStateServiceTest {
    private static final long TIMEOUT_MS = 30 * 1000;

    private GrpcStateService stateService;

    @Mock
    private StreamObserver<BeamFnApi.StateResponse> responseObserver;

    @Mock
    private StateRequestHandler handler;

    /**
     * After a handler has been registered with {@link GrpcStateService#registerForProcessBundleInstructionId(String, StateRequestHandler)}, the
     * {@link GrpcStateService} should delegate requests through {@link GrpcStateService#state(StreamObserver)} to the registered handler.
     */
    @Test
    public void testStateRequestsHandledByRegisteredHandlers() throws Exception {
        // register handler
        String bundleInstructionId = "bundle_instruction";
        stateService.registerForProcessBundleInstructionId(bundleInstructionId, handler);
        // open state stream
        StreamObserver requestObserver = stateService.state(responseObserver);
        // send state request
        BeamFnApi.StateRequest request = StateRequest.newBuilder().setInstructionReference(bundleInstructionId).build();
        requestObserver.onNext(request);
        // assert behavior
        Mockito.verify(handler).handle(request);
    }

    @Test
    public void testHandlerResponseSentToStateStream() throws Exception {
        // define handler behavior
        ByteString expectedResponseData = ByteString.copyFrom("EXPECTED_RESPONSE_DATA", StandardCharsets.UTF_8);
        String bundleInstructionId = "EXPECTED_BUNDLE_INSTRUCTION_ID";
        BeamFnApi.StateResponse.Builder expectedBuilder = BeamFnApi.StateResponse.newBuilder().setGet(StateGetResponse.newBuilder().setData(expectedResponseData));
        StateRequestHandler dummyHandler = ( request) -> {
            CompletableFuture<BeamFnApi.StateResponse.Builder> response = new CompletableFuture<>();
            response.complete(expectedBuilder);
            return response;
        };
        // define observer behavior
        BlockingDeque<BeamFnApi.StateResponse> responses = new LinkedBlockingDeque<>();
        StreamObserver<BeamFnApi.StateResponse> recordingResponseObserver = TestStreams.withOnNext(responses::add).build();
        recordingResponseObserver = Mockito.spy(recordingResponseObserver);
        // register handler
        stateService.registerForProcessBundleInstructionId(bundleInstructionId, dummyHandler);
        // open state stream
        StreamObserver<BeamFnApi.StateRequest> requestObserver = stateService.state(recordingResponseObserver);
        // send state request
        BeamFnApi.StateRequest request = StateRequest.newBuilder().setInstructionReference(bundleInstructionId).build();
        requestObserver.onNext(request);
        // wait for response
        BeamFnApi.StateResponse response = responses.poll(GrpcStateServiceTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // assert that responses contain a built state response
        Mockito.verify(recordingResponseObserver, Mockito.times(1)).onNext(ArgumentMatchers.any());
        Mockito.verify(recordingResponseObserver, Mockito.never()).onCompleted();
        Mockito.verify(recordingResponseObserver, Mockito.never()).onError(ArgumentMatchers.any());
        Assert.assertThat(response.getGet().getData(), CoreMatchers.equalTo(expectedResponseData));
    }
}

