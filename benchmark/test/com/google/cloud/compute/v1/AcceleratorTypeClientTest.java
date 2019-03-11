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
import com.google.cloud.compute.v1.stub.AcceleratorTypeStubSettings;
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
public class AcceleratorTypeClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListAcceleratorTypesMethodDescriptor, getAcceleratorTypeMethodDescriptor, listAcceleratorTypesMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(AcceleratorTypeClientTest.METHOD_DESCRIPTORS, AcceleratorTypeStubSettings.getDefaultEndpoint());

    private static AcceleratorTypeClient client;

    private static AcceleratorTypeSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListAcceleratorTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        AcceleratorTypesScopedList itemsItem = AcceleratorTypesScopedList.newBuilder().build();
        Map<String, AcceleratorTypesScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        AcceleratorTypeAggregatedList expectedResponse = AcceleratorTypeAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        AcceleratorTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        AcceleratorTypeClient.AggregatedListAcceleratorTypesPagedResponse pagedListResponse = AcceleratorTypeClientTest.client.aggregatedListAcceleratorTypes(project);
        List<AcceleratorTypesScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = AcceleratorTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AcceleratorTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListAcceleratorTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AcceleratorTypeClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            AcceleratorTypeClientTest.client.aggregatedListAcceleratorTypes(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getAcceleratorTypeTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        Integer maximumCardsPerInstance = 1883669166;
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        AcceleratorType expectedResponse = AcceleratorType.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setMaximumCardsPerInstance(maximumCardsPerInstance).setName(name).setSelfLink(selfLink).setZone(zone.toString()).build();
        AcceleratorTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneAcceleratorTypeName acceleratorType = ProjectZoneAcceleratorTypeName.of("[PROJECT]", "[ZONE]", "[ACCELERATOR_TYPE]");
        AcceleratorType actualResponse = AcceleratorTypeClientTest.client.getAcceleratorType(acceleratorType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = AcceleratorTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AcceleratorTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getAcceleratorTypeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AcceleratorTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneAcceleratorTypeName acceleratorType = ProjectZoneAcceleratorTypeName.of("[PROJECT]", "[ZONE]", "[ACCELERATOR_TYPE]");
            AcceleratorTypeClientTest.client.getAcceleratorType(acceleratorType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listAcceleratorTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        AcceleratorType itemsElement = AcceleratorType.newBuilder().build();
        List<AcceleratorType> items = Arrays.asList(itemsElement);
        AcceleratorTypeList expectedResponse = AcceleratorTypeList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        AcceleratorTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        AcceleratorTypeClient.ListAcceleratorTypesPagedResponse pagedListResponse = AcceleratorTypeClientTest.client.listAcceleratorTypes(zone);
        List<AcceleratorType> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = AcceleratorTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = AcceleratorTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listAcceleratorTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        AcceleratorTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            AcceleratorTypeClientTest.client.listAcceleratorTypes(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

