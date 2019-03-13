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
import com.google.devtools.clouderrorreporting.v1beta1.ErrorGroup;
import com.google.devtools.clouderrorreporting.v1beta1.GetGroupRequest;
import com.google.devtools.clouderrorreporting.v1beta1.GroupName;
import com.google.devtools.clouderrorreporting.v1beta1.UpdateGroupRequest;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class ErrorGroupServiceClientTest {
    private static MockErrorGroupService mockErrorGroupService;

    private static MockErrorStatsService mockErrorStatsService;

    private static MockReportErrorsService mockReportErrorsService;

    private static MockServiceHelper serviceHelper;

    private ErrorGroupServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void getGroupTest() {
        GroupName name = GroupName.of("[PROJECT]", "[GROUP]");
        String groupId = "groupId506361563";
        ErrorGroup expectedResponse = ErrorGroup.newBuilder().setName(name.toString()).setGroupId(groupId).build();
        ErrorGroupServiceClientTest.mockErrorGroupService.addResponse(expectedResponse);
        GroupName groupName = GroupName.of("[PROJECT]", "[GROUP]");
        ErrorGroup actualResponse = client.getGroup(groupName);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = ErrorGroupServiceClientTest.mockErrorGroupService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetGroupRequest actualRequest = ((GetGroupRequest) (actualRequests.get(0)));
        Assert.assertEquals(groupName, GroupName.parse(actualRequest.getGroupName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getGroupExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ErrorGroupServiceClientTest.mockErrorGroupService.addException(exception);
        try {
            GroupName groupName = GroupName.of("[PROJECT]", "[GROUP]");
            client.getGroup(groupName);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateGroupTest() {
        GroupName name = GroupName.of("[PROJECT]", "[GROUP]");
        String groupId = "groupId506361563";
        ErrorGroup expectedResponse = ErrorGroup.newBuilder().setName(name.toString()).setGroupId(groupId).build();
        ErrorGroupServiceClientTest.mockErrorGroupService.addResponse(expectedResponse);
        ErrorGroup group = ErrorGroup.newBuilder().build();
        ErrorGroup actualResponse = client.updateGroup(group);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = ErrorGroupServiceClientTest.mockErrorGroupService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateGroupRequest actualRequest = ((UpdateGroupRequest) (actualRequests.get(0)));
        Assert.assertEquals(group, actualRequest.getGroup());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateGroupExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        ErrorGroupServiceClientTest.mockErrorGroupService.addException(exception);
        try {
            ErrorGroup group = ErrorGroup.newBuilder().build();
            client.updateGroup(group);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

