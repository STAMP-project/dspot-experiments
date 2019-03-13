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
import com.google.cloud.compute.v1.stub.InstanceTemplateStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class InstanceTemplateClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteInstanceTemplateMethodDescriptor, getInstanceTemplateMethodDescriptor, getIamPolicyInstanceTemplateMethodDescriptor, insertInstanceTemplateMethodDescriptor, listInstanceTemplatesMethodDescriptor, setIamPolicyInstanceTemplateMethodDescriptor, testIamPermissionsInstanceTemplateMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(InstanceTemplateClientTest.METHOD_DESCRIPTORS, InstanceTemplateStubSettings.getDefaultEndpoint());

    private static InstanceTemplateClient client;

    private static InstanceTemplateSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteInstanceTemplateTest() {
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
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInstanceTemplateName instanceTemplate = ProjectGlobalInstanceTemplateName.of("[PROJECT]", "[INSTANCE_TEMPLATE]");
        Operation actualResponse = InstanceTemplateClientTest.client.deleteInstanceTemplate(instanceTemplate);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInstanceTemplateName instanceTemplate = ProjectGlobalInstanceTemplateName.of("[PROJECT]", "[INSTANCE_TEMPLATE]");
            InstanceTemplateClientTest.client.deleteInstanceTemplate(instanceTemplate);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getInstanceTemplateTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        String sourceInstance = "sourceInstance-677426119";
        InstanceTemplate expectedResponse = InstanceTemplate.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setSelfLink(selfLink).setSourceInstance(sourceInstance).build();
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInstanceTemplateName instanceTemplate = ProjectGlobalInstanceTemplateName.of("[PROJECT]", "[INSTANCE_TEMPLATE]");
        InstanceTemplate actualResponse = InstanceTemplateClientTest.client.getInstanceTemplate(instanceTemplate);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInstanceTemplateName instanceTemplate = ProjectGlobalInstanceTemplateName.of("[PROJECT]", "[INSTANCE_TEMPLATE]");
            InstanceTemplateClientTest.client.getInstanceTemplate(instanceTemplate);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyInstanceTemplateTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
        Policy actualResponse = InstanceTemplateClientTest.client.getIamPolicyInstanceTemplate(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
            InstanceTemplateClientTest.client.getIamPolicyInstanceTemplate(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertInstanceTemplateTest() {
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
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        InstanceTemplate instanceTemplateResource = InstanceTemplate.newBuilder().build();
        Operation actualResponse = InstanceTemplateClientTest.client.insertInstanceTemplate(project, instanceTemplateResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            InstanceTemplate instanceTemplateResource = InstanceTemplate.newBuilder().build();
            InstanceTemplateClientTest.client.insertInstanceTemplate(project, instanceTemplateResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listInstanceTemplatesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        InstanceTemplate itemsElement = InstanceTemplate.newBuilder().build();
        List<InstanceTemplate> items = Arrays.asList(itemsElement);
        InstanceTemplateList expectedResponse = InstanceTemplateList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        InstanceTemplateClient.ListInstanceTemplatesPagedResponse pagedListResponse = InstanceTemplateClientTest.client.listInstanceTemplates(project);
        List<InstanceTemplate> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listInstanceTemplatesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            InstanceTemplateClientTest.client.listInstanceTemplates(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyInstanceTemplateTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
        GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
        Policy actualResponse = InstanceTemplateClientTest.client.setIamPolicyInstanceTemplate(resource, globalSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
            GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
            InstanceTemplateClientTest.client.setIamPolicyInstanceTemplate(resource, globalSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsInstanceTemplateTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        InstanceTemplateClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = InstanceTemplateClientTest.client.testIamPermissionsInstanceTemplate(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceTemplateClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceTemplateClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsInstanceTemplateExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceTemplateClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInstanceTemplateResourceName resource = ProjectGlobalInstanceTemplateResourceName.of("[PROJECT]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            InstanceTemplateClientTest.client.testIamPermissionsInstanceTemplate(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

