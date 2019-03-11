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
import com.google.cloud.compute.v1.stub.AutoscalerStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class AutoscalerClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListAutoscalersMethodDescriptor, deleteAutoscalerMethodDescriptor, getAutoscalerMethodDescriptor, insertAutoscalerMethodDescriptor, listAutoscalersMethodDescriptor, patchAutoscalerMethodDescriptor, updateAutoscalerMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(AutoscalerClientTest.METHOD_DESCRIPTORS, AutoscalerStubSettings.getDefaultEndpoint());

    private static AutoscalerClient client;

    private static AutoscalerSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListAutoscalersTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        AutoscalersScopedList itemsItem = AutoscalersScopedList.newBuilder().build();
        Map<String, AutoscalersScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        AutoscalerAggregatedList expectedResponse = AutoscalerAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        AutoscalerClient.AggregatedListAutoscalersPagedResponse pagedListResponse = AutoscalerClientTest.client.aggregatedListAutoscalers(project);
        List<AutoscalersScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListAutoscalersExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            AutoscalerClientTest.client.aggregatedListAutoscalers(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAutoscalerTest() {
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
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone.toString()).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneAutoscalerName autoscaler = ProjectZoneAutoscalerName.of("[PROJECT]", "[ZONE]", "[AUTOSCALER]");
        Operation actualResponse = AutoscalerClientTest.client.deleteAutoscaler(autoscaler);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAutoscalerExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            ProjectZoneAutoscalerName autoscaler = ProjectZoneAutoscalerName.of("[PROJECT]", "[ZONE]", "[AUTOSCALER]");
            AutoscalerClientTest.client.deleteAutoscaler(autoscaler);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getAutoscalerTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String status = "status-892481550";
        String target = "target-880905839";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Autoscaler expectedResponse = Autoscaler.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setRegion(region.toString()).setSelfLink(selfLink).setStatus(status).setTarget(target).setZone(zone.toString()).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneAutoscalerName autoscaler = ProjectZoneAutoscalerName.of("[PROJECT]", "[ZONE]", "[AUTOSCALER]");
        Autoscaler actualResponse = AutoscalerClientTest.client.getAutoscaler(autoscaler);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getAutoscalerExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            ProjectZoneAutoscalerName autoscaler = ProjectZoneAutoscalerName.of("[PROJECT]", "[ZONE]", "[AUTOSCALER]");
            AutoscalerClientTest.client.getAutoscaler(autoscaler);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertAutoscalerTest() {
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
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone2 = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone2.toString()).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
        Operation actualResponse = AutoscalerClientTest.client.insertAutoscaler(zone, autoscalerResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertAutoscalerExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
            AutoscalerClientTest.client.insertAutoscaler(zone, autoscalerResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listAutoscalersTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Autoscaler itemsElement = Autoscaler.newBuilder().build();
        List<Autoscaler> items = Arrays.asList(itemsElement);
        AutoscalerList expectedResponse = AutoscalerList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        AutoscalerClient.ListAutoscalersPagedResponse pagedListResponse = AutoscalerClientTest.client.listAutoscalers(zone);
        List<Autoscaler> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listAutoscalersExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            AutoscalerClientTest.client.listAutoscalers(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void patchAutoscalerTest() {
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
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone2 = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone2.toString()).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        String autoscaler = "autoscaler517258967";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = AutoscalerClientTest.client.patchAutoscaler(autoscaler, zone, autoscalerResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void patchAutoscalerExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            String autoscaler = "autoscaler517258967";
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            AutoscalerClientTest.client.patchAutoscaler(autoscaler, zone, autoscalerResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateAutoscalerTest() {
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
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone2 = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone2.toString()).build();
        AutoscalerClientTest.mockService.addResponse(expectedResponse);
        String autoscaler = "autoscaler517258967";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = AutoscalerClientTest.client.updateAutoscaler(autoscaler, zone, autoscalerResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AutoscalerClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AutoscalerClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateAutoscalerExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AutoscalerClientTest.mockService.addException(exception);
        try {
            String autoscaler = "autoscaler517258967";
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            Autoscaler autoscalerResource = Autoscaler.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            AutoscalerClientTest.client.updateAutoscaler(autoscaler, zone, autoscalerResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

