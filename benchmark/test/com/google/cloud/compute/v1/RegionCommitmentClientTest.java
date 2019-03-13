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
package com.google.cloud.compute.v1;


import Code.INVALID_ARGUMENT;
import com.google.api.gax.httpjson.ApiMethodDescriptor;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.compute.v1.stub.RegionCommitmentStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class RegionCommitmentClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListRegionCommitmentsMethodDescriptor, getRegionCommitmentMethodDescriptor, insertRegionCommitmentMethodDescriptor, listRegionCommitmentsMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(RegionCommitmentClientTest.METHOD_DESCRIPTORS, RegionCommitmentStubSettings.getDefaultEndpoint());

    private static RegionCommitmentClient client;

    private static RegionCommitmentSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListRegionCommitmentsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        CommitmentsScopedList itemsItem = CommitmentsScopedList.newBuilder().build();
        Map<String, CommitmentsScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        CommitmentAggregatedList expectedResponse = CommitmentAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        RegionCommitmentClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        RegionCommitmentClient.AggregatedListRegionCommitmentsPagedResponse pagedListResponse = RegionCommitmentClientTest.client.aggregatedListRegionCommitments(project);
        List<CommitmentsScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = RegionCommitmentClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionCommitmentClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListRegionCommitmentsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionCommitmentClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            RegionCommitmentClientTest.client.aggregatedListRegionCommitments(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionCommitmentTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String endTimestamp = "endTimestamp1004967602";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        String plan = "plan3443497";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTimestamp = "startTimestamp-1526966919";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        Commitment expectedResponse = Commitment.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setEndTimestamp(endTimestamp).setId(id).setKind(kind).setName(name).setPlan(plan).setRegion(region.toString()).setSelfLink(selfLink).setStartTimestamp(startTimestamp).setStatus(status).setStatusMessage(statusMessage).build();
        RegionCommitmentClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionCommitmentName commitment = ProjectRegionCommitmentName.of("[PROJECT]", "[REGION]", "[COMMITMENT]");
        Commitment actualResponse = RegionCommitmentClientTest.client.getRegionCommitment(commitment);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionCommitmentClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionCommitmentClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionCommitmentExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionCommitmentClientTest.mockService.addException(exception);
        try {
            ProjectRegionCommitmentName commitment = ProjectRegionCommitmentName.of("[PROJECT]", "[REGION]", "[COMMITMENT]");
            RegionCommitmentClientTest.client.getRegionCommitment(commitment);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionCommitmentTest() {
        String clientOperationId = "clientOperationId-239630617";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String endTime = "endTime1725551537";
        String httpErrorMessage = "httpErrorMessage1276263769";
        Integer httpErrorStatusCode = 1386087020;
        String id = "id3355";
        String insertTime = "insertTime-103148397";
        String kind = "kind3292052";
        String name = "name3373707";
        String operationType = "operationType-1432962286";
        Integer progress = 1001078227;
        ProjectRegionName region2 = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region2.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone.toString()).build();
        RegionCommitmentClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        Commitment commitmentResource = Commitment.newBuilder().build();
        Operation actualResponse = RegionCommitmentClientTest.client.insertRegionCommitment(region, commitmentResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionCommitmentClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionCommitmentClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionCommitmentExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionCommitmentClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            Commitment commitmentResource = Commitment.newBuilder().build();
            RegionCommitmentClientTest.client.insertRegionCommitment(region, commitmentResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionCommitmentsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Commitment itemsElement = Commitment.newBuilder().build();
        List<Commitment> items = Arrays.asList(itemsElement);
        CommitmentList expectedResponse = CommitmentList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        RegionCommitmentClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        RegionCommitmentClient.ListRegionCommitmentsPagedResponse pagedListResponse = RegionCommitmentClientTest.client.listRegionCommitments(region);
        List<Commitment> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = RegionCommitmentClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionCommitmentClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionCommitmentsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionCommitmentClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            RegionCommitmentClientTest.client.listRegionCommitments(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

