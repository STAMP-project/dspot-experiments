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
import com.google.common.collect.Lists;
import com.google.devtools.clouderrorreporting.v1beta1.DeleteEventsRequest;
import com.google.devtools.clouderrorreporting.v1beta1.DeleteEventsResponse;
import com.google.devtools.clouderrorreporting.v1beta1.ErrorEvent;
import com.google.devtools.clouderrorreporting.v1beta1.ErrorGroupStats;
import com.google.devtools.clouderrorreporting.v1beta1.ListEventsRequest;
import com.google.devtools.clouderrorreporting.v1beta1.ListEventsResponse;
import com.google.devtools.clouderrorreporting.v1beta1.ListGroupStatsRequest;
import com.google.devtools.clouderrorreporting.v1beta1.ListGroupStatsResponse;
import com.google.devtools.clouderrorreporting.v1beta1.ProjectName;
import com.google.devtools.clouderrorreporting.v1beta1.QueryTimeRange;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class ErrorStatsServiceClientTest {
    private static MockErrorGroupService mockErrorGroupService;

    private static MockErrorStatsService mockErrorStatsService;

    private static MockReportErrorsService mockReportErrorsService;

    private static MockServiceHelper serviceHelper;

    private ErrorStatsServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listGroupStatsTest() {
        String nextPageToken = "";
        ErrorGroupStats errorGroupStatsElement = ErrorGroupStats.newBuilder().build();
        List<ErrorGroupStats> errorGroupStats = Arrays.asList(errorGroupStatsElement);
        ListGroupStatsResponse expectedResponse = ListGroupStatsResponse.newBuilder().setNextPageToken(nextPageToken).addAllErrorGroupStats(errorGroupStats).build();
        ErrorStatsServiceClientTest.mockErrorStatsService.addResponse(expectedResponse);
        ProjectName projectName = ProjectName.of("[PROJECT]");
        QueryTimeRange timeRange = QueryTimeRange.newBuilder().build();
        ErrorStatsServiceClient.ListGroupStatsPagedResponse pagedListResponse = client.listGroupStats(projectName, timeRange);
        List<ErrorGroupStats> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getErrorGroupStatsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = ErrorStatsServiceClientTest.mockErrorStatsService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListGroupStatsRequest actualRequest = ((ListGroupStatsRequest) (actualRequests.get(0)));
        Assert.assertEquals(projectName, ProjectName.parse(actualRequest.getProjectName()));
        Assert.assertEquals(timeRange, actualRequest.getTimeRange());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listGroupStatsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ErrorStatsServiceClientTest.mockErrorStatsService.addException(exception);
        try {
            ProjectName projectName = ProjectName.of("[PROJECT]");
            QueryTimeRange timeRange = QueryTimeRange.newBuilder().build();
            client.listGroupStats(projectName, timeRange);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listEventsTest() {
        String nextPageToken = "";
        ErrorEvent errorEventsElement = ErrorEvent.newBuilder().build();
        List<ErrorEvent> errorEvents = Arrays.asList(errorEventsElement);
        ListEventsResponse expectedResponse = ListEventsResponse.newBuilder().setNextPageToken(nextPageToken).addAllErrorEvents(errorEvents).build();
        ErrorStatsServiceClientTest.mockErrorStatsService.addResponse(expectedResponse);
        ProjectName projectName = ProjectName.of("[PROJECT]");
        String groupId = "groupId506361563";
        ErrorStatsServiceClient.ListEventsPagedResponse pagedListResponse = client.listEvents(projectName, groupId);
        List<ErrorEvent> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getErrorEventsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = ErrorStatsServiceClientTest.mockErrorStatsService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListEventsRequest actualRequest = ((ListEventsRequest) (actualRequests.get(0)));
        Assert.assertEquals(projectName, ProjectName.parse(actualRequest.getProjectName()));
        Assert.assertEquals(groupId, actualRequest.getGroupId());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listEventsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ErrorStatsServiceClientTest.mockErrorStatsService.addException(exception);
        try {
            ProjectName projectName = ProjectName.of("[PROJECT]");
            String groupId = "groupId506361563";
            client.listEvents(projectName, groupId);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteEventsTest() {
        DeleteEventsResponse expectedResponse = DeleteEventsResponse.newBuilder().build();
        ErrorStatsServiceClientTest.mockErrorStatsService.addResponse(expectedResponse);
        ProjectName projectName = ProjectName.of("[PROJECT]");
        DeleteEventsResponse actualResponse = client.deleteEvents(projectName);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = ErrorStatsServiceClientTest.mockErrorStatsService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteEventsRequest actualRequest = ((DeleteEventsRequest) (actualRequests.get(0)));
        Assert.assertEquals(projectName, ProjectName.parse(actualRequest.getProjectName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteEventsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ErrorStatsServiceClientTest.mockErrorStatsService.addException(exception);
        try {
            ProjectName projectName = ProjectName.of("[PROJECT]");
            client.deleteEvents(projectName);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

