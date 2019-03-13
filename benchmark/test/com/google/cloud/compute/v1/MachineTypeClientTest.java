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
import com.google.cloud.compute.v1.stub.MachineTypeStubSettings;
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
public class MachineTypeClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(aggregatedListMachineTypesMethodDescriptor, getMachineTypeMethodDescriptor, listMachineTypesMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(MachineTypeClientTest.METHOD_DESCRIPTORS, MachineTypeStubSettings.getDefaultEndpoint());

    private static MachineTypeClient client;

    private static MachineTypeSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void aggregatedListMachineTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        MachineTypesScopedList itemsItem = MachineTypesScopedList.newBuilder().build();
        Map<String, MachineTypesScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        MachineTypeAggregatedList expectedResponse = MachineTypeAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        MachineTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        MachineTypeClient.AggregatedListMachineTypesPagedResponse pagedListResponse = MachineTypeClientTest.client.aggregatedListMachineTypes(project);
        List<MachineTypesScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = MachineTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = MachineTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListMachineTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        MachineTypeClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            MachineTypeClientTest.client.aggregatedListMachineTypes(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getMachineTypeTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        Integer guestCpus = 1754126894;
        String id = "id3355";
        Integer imageSpaceGb = 461539048;
        Boolean isSharedCpu = false;
        String kind = "kind3292052";
        Integer maximumPersistentDisks = 1033091853;
        String maximumPersistentDisksSizeGb = "maximumPersistentDisksSizeGb-1993209177";
        Integer memoryMb = 1726613907;
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        MachineType expectedResponse = MachineType.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setGuestCpus(guestCpus).setId(id).setImageSpaceGb(imageSpaceGb).setIsSharedCpu(isSharedCpu).setKind(kind).setMaximumPersistentDisks(maximumPersistentDisks).setMaximumPersistentDisksSizeGb(maximumPersistentDisksSizeGb).setMemoryMb(memoryMb).setName(name).setSelfLink(selfLink).setZone(zone.toString()).build();
        MachineTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneMachineTypeName machineType = ProjectZoneMachineTypeName.of("[PROJECT]", "[ZONE]", "[MACHINE_TYPE]");
        MachineType actualResponse = MachineTypeClientTest.client.getMachineType(machineType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = MachineTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = MachineTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getMachineTypeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        MachineTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneMachineTypeName machineType = ProjectZoneMachineTypeName.of("[PROJECT]", "[ZONE]", "[MACHINE_TYPE]");
            MachineTypeClientTest.client.getMachineType(machineType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listMachineTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        MachineType itemsElement = MachineType.newBuilder().build();
        List<MachineType> items = Arrays.asList(itemsElement);
        MachineTypeList expectedResponse = MachineTypeList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        MachineTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        MachineTypeClient.ListMachineTypesPagedResponse pagedListResponse = MachineTypeClientTest.client.listMachineTypes(zone);
        List<MachineType> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = MachineTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = MachineTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listMachineTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        MachineTypeClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            MachineTypeClientTest.client.listMachineTypes(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

