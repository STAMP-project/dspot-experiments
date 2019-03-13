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
import com.google.cloud.compute.v1.stub.LicenseStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class LicenseClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteLicenseMethodDescriptor, getLicenseMethodDescriptor, getIamPolicyLicenseMethodDescriptor, insertLicenseMethodDescriptor, listLicensesMethodDescriptor, setIamPolicyLicenseMethodDescriptor, testIamPermissionsLicenseMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(LicenseClientTest.METHOD_DESCRIPTORS, LicenseStubSettings.getDefaultEndpoint());

    private static LicenseClient client;

    private static LicenseSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteLicenseTest() {
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
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseName license = ProjectGlobalLicenseName.of("[PROJECT]", "[LICENSE]");
        Operation actualResponse = LicenseClientTest.client.deleteLicense(license);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseName license = ProjectGlobalLicenseName.of("[PROJECT]", "[LICENSE]");
            LicenseClientTest.client.deleteLicense(license);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getLicenseTest() {
        Boolean chargesUseFee = true;
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        ProjectGlobalLicenseCodeName licenseCode = ProjectGlobalLicenseCodeName.of("[PROJECT]", "[LICENSE_CODE]");
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        Boolean transferable = false;
        License expectedResponse = License.newBuilder().setChargesUseFee(chargesUseFee).setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setLicenseCode(licenseCode.toString()).setName(name).setSelfLink(selfLink).setTransferable(transferable).build();
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseName license = ProjectGlobalLicenseName.of("[PROJECT]", "[LICENSE]");
        License actualResponse = LicenseClientTest.client.getLicense(license);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseName license = ProjectGlobalLicenseName.of("[PROJECT]", "[LICENSE]");
            LicenseClientTest.client.getLicense(license);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyLicenseTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
        Policy actualResponse = LicenseClientTest.client.getIamPolicyLicense(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
            LicenseClientTest.client.getIamPolicyLicense(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertLicenseTest() {
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
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        License licenseResource = License.newBuilder().build();
        Operation actualResponse = LicenseClientTest.client.insertLicense(project, licenseResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            License licenseResource = License.newBuilder().build();
            LicenseClientTest.client.insertLicense(project, licenseResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listLicensesTest() {
        String id = "id3355";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        License itemsElement = License.newBuilder().build();
        List<License> items = Arrays.asList(itemsElement);
        LicensesListResponse expectedResponse = LicensesListResponse.newBuilder().setId(id).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        LicenseClient.ListLicensesPagedResponse pagedListResponse = LicenseClientTest.client.listLicenses(project);
        List<License> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listLicensesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            LicenseClientTest.client.listLicenses(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyLicenseTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
        GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
        Policy actualResponse = LicenseClientTest.client.setIamPolicyLicense(resource, globalSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
            GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
            LicenseClientTest.client.setIamPolicyLicense(resource, globalSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsLicenseTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        LicenseClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = LicenseClientTest.client.testIamPermissionsLicense(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsLicenseExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseResourceName resource = ProjectGlobalLicenseResourceName.of("[PROJECT]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            LicenseClientTest.client.testIamPermissionsLicense(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

