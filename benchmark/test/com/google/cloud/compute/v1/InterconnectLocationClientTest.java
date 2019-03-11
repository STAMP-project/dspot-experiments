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
import com.google.cloud.compute.v1.stub.InterconnectLocationStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class InterconnectLocationClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(getInterconnectLocationMethodDescriptor, listInterconnectLocationsMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(InterconnectLocationClientTest.METHOD_DESCRIPTORS, InterconnectLocationStubSettings.getDefaultEndpoint());

    private static InterconnectLocationClient client;

    private static InterconnectLocationSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void getInterconnectLocationTest() {
        ProjectGlobalAddressName address = ProjectGlobalAddressName.of("[PROJECT]", "[ADDRESS]");
        String availabilityZone = "availabilityZone-378410992";
        String city = "city3053931";
        String continent = "continent-403427916";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String facilityProvider = "facilityProvider2143916045";
        String facilityProviderFacilityId = "facilityProviderFacilityId-1523343611";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        String peeringdbFacilityId = "peeringdbFacilityId-303818";
        String selfLink = "selfLink-1691268851";
        String status = "status-892481550";
        InterconnectLocation expectedResponse = InterconnectLocation.newBuilder().setAddress(address.toString()).setAvailabilityZone(availabilityZone).setCity(city).setContinent(continent).setCreationTimestamp(creationTimestamp).setDescription(description).setFacilityProvider(facilityProvider).setFacilityProviderFacilityId(facilityProviderFacilityId).setId(id).setKind(kind).setName(name).setPeeringdbFacilityId(peeringdbFacilityId).setSelfLink(selfLink).setStatus(status).build();
        InterconnectLocationClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalInterconnectLocationName interconnectLocation = ProjectGlobalInterconnectLocationName.of("[PROJECT]", "[INTERCONNECT_LOCATION]");
        InterconnectLocation actualResponse = InterconnectLocationClientTest.client.getInterconnectLocation(interconnectLocation);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InterconnectLocationClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InterconnectLocationClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getInterconnectLocationExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InterconnectLocationClientTest.mockService.addException(exception);
        try {
            ProjectGlobalInterconnectLocationName interconnectLocation = ProjectGlobalInterconnectLocationName.of("[PROJECT]", "[INTERCONNECT_LOCATION]");
            InterconnectLocationClientTest.client.getInterconnectLocation(interconnectLocation);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listInterconnectLocationsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        InterconnectLocation itemsElement = InterconnectLocation.newBuilder().build();
        List<InterconnectLocation> items = Arrays.asList(itemsElement);
        InterconnectLocationList expectedResponse = InterconnectLocationList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        InterconnectLocationClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        InterconnectLocationClient.ListInterconnectLocationsPagedResponse pagedListResponse = InterconnectLocationClientTest.client.listInterconnectLocations(project);
        List<InterconnectLocation> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = InterconnectLocationClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InterconnectLocationClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listInterconnectLocationsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InterconnectLocationClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            InterconnectLocationClientTest.client.listInterconnectLocations(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

