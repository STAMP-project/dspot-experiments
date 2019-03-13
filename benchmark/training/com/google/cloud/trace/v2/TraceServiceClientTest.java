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
package com.google.cloud.trace.v2;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.devtools.cloudtrace.v2.BatchWriteSpansRequest;
import com.google.devtools.cloudtrace.v2.ProjectName;
import com.google.devtools.cloudtrace.v2.Span;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class TraceServiceClientTest {
    private static MockTraceService mockTraceService;

    private static MockServiceHelper serviceHelper;

    private TraceServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void batchWriteSpansTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        TraceServiceClientTest.mockTraceService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        List<Span> spans = new ArrayList<>();
        client.batchWriteSpans(name, spans);
        List<GeneratedMessageV3> actualRequests = TraceServiceClientTest.mockTraceService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        BatchWriteSpansRequest actualRequest = ((BatchWriteSpansRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertEquals(spans, actualRequest.getSpansList());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void batchWriteSpansExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        TraceServiceClientTest.mockTraceService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            List<Span> spans = new ArrayList<>();
            client.batchWriteSpans(name, spans);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

