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
import com.google.cloud.compute.v1.stub.LicenseCodeStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class LicenseCodeClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(getLicenseCodeMethodDescriptor, testIamPermissionsLicenseCodeMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(LicenseCodeClientTest.METHOD_DESCRIPTORS, LicenseCodeStubSettings.getDefaultEndpoint());

    private static LicenseCodeClient client;

    private static LicenseCodeSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void getLicenseCodeTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        String state = "state109757585";
        Boolean transferable = false;
        LicenseCode expectedResponse = LicenseCode.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setSelfLink(selfLink).setState(state).setTransferable(transferable).build();
        LicenseCodeClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseCodeName licenseCode = ProjectGlobalLicenseCodeName.of("[PROJECT]", "[LICENSE_CODE]");
        LicenseCode actualResponse = LicenseCodeClientTest.client.getLicenseCode(licenseCode);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseCodeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseCodeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getLicenseCodeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseCodeClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseCodeName licenseCode = ProjectGlobalLicenseCodeName.of("[PROJECT]", "[LICENSE_CODE]");
            LicenseCodeClientTest.client.getLicenseCode(licenseCode);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsLicenseCodeTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        LicenseCodeClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalLicenseCodeResourceName resource = ProjectGlobalLicenseCodeResourceName.of("[PROJECT]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = LicenseCodeClientTest.client.testIamPermissionsLicenseCode(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = LicenseCodeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = LicenseCodeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsLicenseCodeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        LicenseCodeClientTest.mockService.addException(exception);
        try {
            ProjectGlobalLicenseCodeResourceName resource = ProjectGlobalLicenseCodeResourceName.of("[PROJECT]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            LicenseCodeClientTest.client.testIamPermissionsLicenseCode(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

