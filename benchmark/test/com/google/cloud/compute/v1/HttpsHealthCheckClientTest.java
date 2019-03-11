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
import com.google.cloud.compute.v1.stub.HttpsHealthCheckStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class HttpsHealthCheckClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteHttpsHealthCheckMethodDescriptor, getHttpsHealthCheckMethodDescriptor, insertHttpsHealthCheckMethodDescriptor, listHttpsHealthChecksMethodDescriptor, patchHttpsHealthCheckMethodDescriptor, updateHttpsHealthCheckMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(HttpsHealthCheckClientTest.METHOD_DESCRIPTORS, HttpsHealthCheckStubSettings.getDefaultEndpoint());

    private static HttpsHealthCheckClient client;

    private static HttpsHealthCheckSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteHttpsHealthCheckTest() {
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
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
        Operation actualResponse = HttpsHealthCheckClientTest.client.deleteHttpsHealthCheck(httpsHealthCheck);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteHttpsHealthCheckExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
            HttpsHealthCheckClientTest.client.deleteHttpsHealthCheck(httpsHealthCheck);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getHttpsHealthCheckTest() {
        Integer checkIntervalSec = 345561006;
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        Integer healthyThreshold = 133658551;
        String host = "host3208616";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        Integer port = 3446913;
        String requestPath = "requestPath1303145429";
        String selfLink = "selfLink-1691268851";
        Integer timeoutSec = 2067488653;
        Integer unhealthyThreshold = 1838571216;
        HttpsHealthCheck2 expectedResponse = HttpsHealthCheck2.newBuilder().setCheckIntervalSec(checkIntervalSec).setCreationTimestamp(creationTimestamp).setDescription(description).setHealthyThreshold(healthyThreshold).setHost(host).setId(id).setKind(kind).setName(name).setPort(port).setRequestPath(requestPath).setSelfLink(selfLink).setTimeoutSec(timeoutSec).setUnhealthyThreshold(unhealthyThreshold).build();
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
        HttpsHealthCheck2 actualResponse = HttpsHealthCheckClientTest.client.getHttpsHealthCheck(httpsHealthCheck);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getHttpsHealthCheckExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
            HttpsHealthCheckClientTest.client.getHttpsHealthCheck(httpsHealthCheck);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertHttpsHealthCheckTest() {
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
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
        Operation actualResponse = HttpsHealthCheckClientTest.client.insertHttpsHealthCheck(project, httpsHealthCheckResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertHttpsHealthCheckExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
            HttpsHealthCheckClientTest.client.insertHttpsHealthCheck(project, httpsHealthCheckResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listHttpsHealthChecksTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        HttpsHealthCheck2 itemsElement = HttpsHealthCheck2.newBuilder().build();
        List<HttpsHealthCheck2> items = Arrays.asList(itemsElement);
        HttpsHealthCheckList expectedResponse = HttpsHealthCheckList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        HttpsHealthCheckClient.ListHttpsHealthChecksPagedResponse pagedListResponse = HttpsHealthCheckClientTest.client.listHttpsHealthChecks(project);
        List<HttpsHealthCheck2> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listHttpsHealthChecksExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            HttpsHealthCheckClientTest.client.listHttpsHealthChecks(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void patchHttpsHealthCheckTest() {
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
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
        HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = HttpsHealthCheckClientTest.client.patchHttpsHealthCheck(httpsHealthCheck, httpsHealthCheckResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void patchHttpsHealthCheckExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
            HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            HttpsHealthCheckClientTest.client.patchHttpsHealthCheck(httpsHealthCheck, httpsHealthCheckResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateHttpsHealthCheckTest() {
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
        HttpsHealthCheckClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
        HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = HttpsHealthCheckClientTest.client.updateHttpsHealthCheck(httpsHealthCheck, httpsHealthCheckResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = HttpsHealthCheckClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = HttpsHealthCheckClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateHttpsHealthCheckExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        HttpsHealthCheckClientTest.mockService.addException(exception);
        try {
            ProjectGlobalHttpsHealthCheckName httpsHealthCheck = ProjectGlobalHttpsHealthCheckName.of("[PROJECT]", "[HTTPS_HEALTH_CHECK]");
            HttpsHealthCheck2 httpsHealthCheckResource = HttpsHealthCheck2.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            HttpsHealthCheckClientTest.client.updateHttpsHealthCheck(httpsHealthCheck, httpsHealthCheckResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

