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
package com.google.cloud.monitoring.v3;


import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.monitoring.v3.AlertPolicy;
import com.google.monitoring.v3.AlertPolicyName;
import com.google.monitoring.v3.CreateAlertPolicyRequest;
import com.google.monitoring.v3.DeleteAlertPolicyRequest;
import com.google.monitoring.v3.GetAlertPolicyRequest;
import com.google.monitoring.v3.ListAlertPoliciesRequest;
import com.google.monitoring.v3.ListAlertPoliciesResponse;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.UpdateAlertPolicyRequest;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class AlertPolicyServiceClientTest {
    private static MockAlertPolicyService mockAlertPolicyService;

    private static MockGroupService mockGroupService;

    private static MockMetricService mockMetricService;

    private static MockNotificationChannelService mockNotificationChannelService;

    private static MockUptimeCheckService mockUptimeCheckService;

    private static MockServiceHelper serviceHelper;

    private AlertPolicyServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listAlertPoliciesTest() {
        String nextPageToken = "";
        AlertPolicy alertPoliciesElement = AlertPolicy.newBuilder().build();
        List<AlertPolicy> alertPolicies = Arrays.asList(alertPoliciesElement);
        ListAlertPoliciesResponse expectedResponse = ListAlertPoliciesResponse.newBuilder().setNextPageToken(nextPageToken).addAllAlertPolicies(alertPolicies).build();
        AlertPolicyServiceClientTest.mockAlertPolicyService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        AlertPolicyServiceClient.ListAlertPoliciesPagedResponse pagedListResponse = client.listAlertPolicies(name);
        List<AlertPolicy> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getAlertPoliciesList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = AlertPolicyServiceClientTest.mockAlertPolicyService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListAlertPoliciesRequest actualRequest = ((ListAlertPoliciesRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listAlertPoliciesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        AlertPolicyServiceClientTest.mockAlertPolicyService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            client.listAlertPolicies(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getAlertPolicyTest() {
        String name2 = "name2-1052831874";
        String displayName = "displayName1615086568";
        AlertPolicy expectedResponse = AlertPolicy.newBuilder().setName(name2).setDisplayName(displayName).build();
        AlertPolicyServiceClientTest.mockAlertPolicyService.addResponse(expectedResponse);
        AlertPolicyName name = AlertPolicyName.of("[PROJECT]", "[ALERT_POLICY]");
        AlertPolicy actualResponse = client.getAlertPolicy(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = AlertPolicyServiceClientTest.mockAlertPolicyService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetAlertPolicyRequest actualRequest = ((GetAlertPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, AlertPolicyName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getAlertPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        AlertPolicyServiceClientTest.mockAlertPolicyService.addException(exception);
        try {
            AlertPolicyName name = AlertPolicyName.of("[PROJECT]", "[ALERT_POLICY]");
            client.getAlertPolicy(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createAlertPolicyTest() {
        String name2 = "name2-1052831874";
        String displayName = "displayName1615086568";
        AlertPolicy expectedResponse = AlertPolicy.newBuilder().setName(name2).setDisplayName(displayName).build();
        AlertPolicyServiceClientTest.mockAlertPolicyService.addResponse(expectedResponse);
        ProjectName name = ProjectName.of("[PROJECT]");
        AlertPolicy alertPolicy = AlertPolicy.newBuilder().build();
        AlertPolicy actualResponse = client.createAlertPolicy(name, alertPolicy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = AlertPolicyServiceClientTest.mockAlertPolicyService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateAlertPolicyRequest actualRequest = ((CreateAlertPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, ProjectName.parse(actualRequest.getName()));
        Assert.assertEquals(alertPolicy, actualRequest.getAlertPolicy());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createAlertPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        AlertPolicyServiceClientTest.mockAlertPolicyService.addException(exception);
        try {
            ProjectName name = ProjectName.of("[PROJECT]");
            AlertPolicy alertPolicy = AlertPolicy.newBuilder().build();
            client.createAlertPolicy(name, alertPolicy);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAlertPolicyTest() {
        Empty expectedResponse = Empty.newBuilder().build();
        AlertPolicyServiceClientTest.mockAlertPolicyService.addResponse(expectedResponse);
        AlertPolicyName name = AlertPolicyName.of("[PROJECT]", "[ALERT_POLICY]");
        client.deleteAlertPolicy(name);
        List<GeneratedMessageV3> actualRequests = AlertPolicyServiceClientTest.mockAlertPolicyService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteAlertPolicyRequest actualRequest = ((DeleteAlertPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, AlertPolicyName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAlertPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        AlertPolicyServiceClientTest.mockAlertPolicyService.addException(exception);
        try {
            AlertPolicyName name = AlertPolicyName.of("[PROJECT]", "[ALERT_POLICY]");
            client.deleteAlertPolicy(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateAlertPolicyTest() {
        String name = "name3373707";
        String displayName = "displayName1615086568";
        AlertPolicy expectedResponse = AlertPolicy.newBuilder().setName(name).setDisplayName(displayName).build();
        AlertPolicyServiceClientTest.mockAlertPolicyService.addResponse(expectedResponse);
        FieldMask updateMask = FieldMask.newBuilder().build();
        AlertPolicy alertPolicy = AlertPolicy.newBuilder().build();
        AlertPolicy actualResponse = client.updateAlertPolicy(updateMask, alertPolicy);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = AlertPolicyServiceClientTest.mockAlertPolicyService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        UpdateAlertPolicyRequest actualRequest = ((UpdateAlertPolicyRequest) (actualRequests.get(0)));
        Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
        Assert.assertEquals(alertPolicy, actualRequest.getAlertPolicy());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void updateAlertPolicyExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        AlertPolicyServiceClientTest.mockAlertPolicyService.addException(exception);
        try {
            FieldMask updateMask = FieldMask.newBuilder().build();
            AlertPolicy alertPolicy = AlertPolicy.newBuilder().build();
            client.updateAlertPolicy(updateMask, alertPolicy);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

