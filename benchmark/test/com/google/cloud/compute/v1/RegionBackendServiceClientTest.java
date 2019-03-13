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
import com.google.cloud.compute.v1.stub.RegionBackendServiceStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class RegionBackendServiceClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteRegionBackendServiceMethodDescriptor, getRegionBackendServiceMethodDescriptor, getHealthRegionBackendServiceMethodDescriptor, insertRegionBackendServiceMethodDescriptor, listRegionBackendServicesMethodDescriptor, patchRegionBackendServiceMethodDescriptor, updateRegionBackendServiceMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(RegionBackendServiceClientTest.METHOD_DESCRIPTORS, RegionBackendServiceStubSettings.getDefaultEndpoint());

    private static RegionBackendServiceClient client;

    private static RegionBackendServiceSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteRegionBackendServiceTest() {
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
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
        Operation actualResponse = RegionBackendServiceClientTest.client.deleteRegionBackendService(backendService);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
            RegionBackendServiceClientTest.client.deleteRegionBackendService(backendService);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionBackendServiceTest() {
        Integer affinityCookieTtlSec = 1777486694;
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        Boolean enableCDN = false;
        String fingerprint = "fingerprint-1375934236";
        String id = "id3355";
        String kind = "kind3292052";
        String loadBalancingScheme = "loadBalancingScheme1974502980";
        String name = "name3373707";
        Integer port = 3446913;
        String portName = "portName1115276169";
        String protocol = "protocol-989163880";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        ProjectGlobalSecurityPolicyName securityPolicy = ProjectGlobalSecurityPolicyName.of("[PROJECT]", "[SECURITY_POLICY]");
        String selfLink = "selfLink-1691268851";
        String sessionAffinity = "sessionAffinity1000759473";
        Integer timeoutSec = 2067488653;
        BackendService expectedResponse = BackendService.newBuilder().setAffinityCookieTtlSec(affinityCookieTtlSec).setCreationTimestamp(creationTimestamp).setDescription(description).setEnableCDN(enableCDN).setFingerprint(fingerprint).setId(id).setKind(kind).setLoadBalancingScheme(loadBalancingScheme).setName(name).setPort(port).setPortName(portName).setProtocol(protocol).setRegion(region.toString()).setSecurityPolicy(securityPolicy.toString()).setSelfLink(selfLink).setSessionAffinity(sessionAffinity).setTimeoutSec(timeoutSec).build();
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
        BackendService actualResponse = RegionBackendServiceClientTest.client.getRegionBackendService(backendService);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
            RegionBackendServiceClientTest.client.getRegionBackendService(backendService);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getHealthRegionBackendServiceTest() {
        String kind = "kind3292052";
        BackendServiceGroupHealth expectedResponse = BackendServiceGroupHealth.newBuilder().setKind(kind).build();
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
        ResourceGroupReference resourceGroupReferenceResource = ResourceGroupReference.newBuilder().build();
        BackendServiceGroupHealth actualResponse = RegionBackendServiceClientTest.client.getHealthRegionBackendService(backendService, resourceGroupReferenceResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getHealthRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
            ResourceGroupReference resourceGroupReferenceResource = ResourceGroupReference.newBuilder().build();
            RegionBackendServiceClientTest.client.getHealthRegionBackendService(backendService, resourceGroupReferenceResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionBackendServiceTest() {
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
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        BackendService backendServiceResource = BackendService.newBuilder().build();
        Operation actualResponse = RegionBackendServiceClientTest.client.insertRegionBackendService(region, backendServiceResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            BackendService backendServiceResource = BackendService.newBuilder().build();
            RegionBackendServiceClientTest.client.insertRegionBackendService(region, backendServiceResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionBackendServicesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        BackendService itemsElement = BackendService.newBuilder().build();
        List<BackendService> items = Arrays.asList(itemsElement);
        BackendServiceList expectedResponse = BackendServiceList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        RegionBackendServiceClient.ListRegionBackendServicesPagedResponse pagedListResponse = RegionBackendServiceClientTest.client.listRegionBackendServices(region);
        List<BackendService> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionBackendServicesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            RegionBackendServiceClientTest.client.listRegionBackendServices(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void patchRegionBackendServiceTest() {
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
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
        BackendService backendServiceResource = BackendService.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = RegionBackendServiceClientTest.client.patchRegionBackendService(backendService, backendServiceResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void patchRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
            BackendService backendServiceResource = BackendService.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            RegionBackendServiceClientTest.client.patchRegionBackendService(backendService, backendServiceResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateRegionBackendServiceTest() {
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
        RegionBackendServiceClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
        BackendService backendServiceResource = BackendService.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = RegionBackendServiceClientTest.client.updateRegionBackendService(backendService, backendServiceResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionBackendServiceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionBackendServiceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateRegionBackendServiceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionBackendServiceClientTest.mockService.addException(exception);
        try {
            ProjectRegionBackendServiceName backendService = ProjectRegionBackendServiceName.of("[PROJECT]", "[REGION]", "[BACKEND_SERVICE]");
            BackendService backendServiceResource = BackendService.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            RegionBackendServiceClientTest.client.updateRegionBackendService(backendService, backendServiceResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

