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
package com.google.cloud.errorreporting.v1beta1;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.devtools.clouderrorreporting.v1beta1.ProjectName;
import com.google.devtools.clouderrorreporting.v1beta1.ReportErrorEventRequest;
import com.google.devtools.clouderrorreporting.v1beta1.ReportErrorEventResponse;
import com.google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class ReportErrorsServiceClientTest {
    private static MockErrorGroupService mockErrorGroupService;

    private static MockErrorStatsService mockErrorStatsService;

    private static MockReportErrorsService mockReportErrorsService;

    private static MockServiceHelper serviceHelper;

    private ReportErrorsServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void reportErrorEventTest() {
        ReportErrorEventResponse expectedResponse = ReportErrorEventResponse.newBuilder().build();
        ReportErrorsServiceClientTest.mockReportErrorsService.addResponse(expectedResponse);
        ProjectName projectName = ProjectName.of("[PROJECT]");
        ReportedErrorEvent event = ReportedErrorEvent.newBuilder().build();
        ReportErrorEventResponse actualResponse = client.reportErrorEvent(projectName, event);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = ReportErrorsServiceClientTest.mockReportErrorsService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ReportErrorEventRequest actualRequest = ((ReportErrorEventRequest) (actualRequests.get(0)));
        Assert.assertEquals(projectName, ProjectName.parse(actualRequest.getProjectName()));
        Assert.assertEquals(event, actualRequest.getEvent());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void reportErrorEventExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ReportErrorsServiceClientTest.mockReportErrorsService.addException(exception);
        try {
            ProjectName projectName = ProjectName.of("[PROJECT]");
            ReportedErrorEvent event = ReportedErrorEvent.newBuilder().build();
            client.reportErrorEvent(projectName, event);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

