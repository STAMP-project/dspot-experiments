/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.dialogflow.v2;


import StatusCode.Code.INVALID_ARGUMENT;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class SessionsClientTest {
    private static MockAgents mockAgents;

    private static MockContexts mockContexts;

    private static MockEntityTypes mockEntityTypes;

    private static MockIntents mockIntents;

    private static MockSessionEntityTypes mockSessionEntityTypes;

    private static MockSessions mockSessions;

    private static MockServiceHelper serviceHelper;

    private SessionsClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void detectIntentTest() {
        String responseId = "responseId1847552473";
        DetectIntentResponse expectedResponse = DetectIntentResponse.newBuilder().setResponseId(responseId).build();
        SessionsClientTest.mockSessions.addResponse(expectedResponse);
        SessionName session = SessionName.of("[PROJECT]", "[SESSION]");
        QueryInput queryInput = QueryInput.newBuilder().build();
        DetectIntentResponse actualResponse = client.detectIntent(session, queryInput);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = SessionsClientTest.mockSessions.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DetectIntentRequest actualRequest = ((DetectIntentRequest) (actualRequests.get(0)));
        Assert.assertEquals(session, SessionName.parse(actualRequest.getSession()));
        Assert.assertEquals(queryInput, actualRequest.getQueryInput());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void detectIntentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        SessionsClientTest.mockSessions.addException(exception);
        try {
            SessionName session = SessionName.of("[PROJECT]", "[SESSION]");
            QueryInput queryInput = QueryInput.newBuilder().build();
            client.detectIntent(session, queryInput);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void streamingDetectIntentTest() throws Exception {
        String responseId = "responseId1847552473";
        StreamingDetectIntentResponse expectedResponse = StreamingDetectIntentResponse.newBuilder().setResponseId(responseId).build();
        SessionsClientTest.mockSessions.addResponse(expectedResponse);
        String session = "session1984987798";
        QueryInput queryInput = QueryInput.newBuilder().build();
        StreamingDetectIntentRequest request = StreamingDetectIntentRequest.newBuilder().setSession(session).setQueryInput(queryInput).build();
        MockStreamObserver<StreamingDetectIntentResponse> responseObserver = new MockStreamObserver();
        BidiStreamingCallable<StreamingDetectIntentRequest, StreamingDetectIntentResponse> callable = client.streamingDetectIntentCallable();
        ApiStreamObserver<StreamingDetectIntentRequest> requestObserver = callable.bidiStreamingCall(responseObserver);
        requestObserver.onNext(request);
        requestObserver.onCompleted();
        List<StreamingDetectIntentResponse> actualResponses = responseObserver.future().get();
        Assert.assertEquals(1, actualResponses.size());
        Assert.assertEquals(expectedResponse, actualResponses.get(0));
    }

    @Test
    @SuppressWarnings("all")
    public void streamingDetectIntentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        SessionsClientTest.mockSessions.addException(exception);
        String session = "session1984987798";
        QueryInput queryInput = QueryInput.newBuilder().build();
        StreamingDetectIntentRequest request = StreamingDetectIntentRequest.newBuilder().setSession(session).setQueryInput(queryInput).build();
        MockStreamObserver<StreamingDetectIntentResponse> responseObserver = new MockStreamObserver();
        BidiStreamingCallable<StreamingDetectIntentRequest, StreamingDetectIntentResponse> callable = client.streamingDetectIntentCallable();
        ApiStreamObserver<StreamingDetectIntentRequest> requestObserver = callable.bidiStreamingCall(responseObserver);
        requestObserver.onNext(request);
        try {
            List<StreamingDetectIntentResponse> actualResponses = responseObserver.future().get();
            Assert.fail("No exception thrown");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof InvalidArgumentException));
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }
}

