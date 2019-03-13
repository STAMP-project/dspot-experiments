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
import com.google.cloud.compute.v1.stub.RegionDiskTypeStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class RegionDiskTypeClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(getRegionDiskTypeMethodDescriptor, listRegionDiskTypesMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(RegionDiskTypeClientTest.METHOD_DESCRIPTORS, RegionDiskTypeStubSettings.getDefaultEndpoint());

    private static RegionDiskTypeClient client;

    private static RegionDiskTypeSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void getRegionDiskTypeTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String defaultDiskSizeGb = "defaultDiskSizeGb807490165";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String validDiskSize = "validDiskSize-1653521184";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        DiskType expectedResponse = DiskType.newBuilder().setCreationTimestamp(creationTimestamp).setDefaultDiskSizeGb(defaultDiskSizeGb).setDescription(description).setId(id).setKind(kind).setName(name).setRegion(region.toString()).setSelfLink(selfLink).setValidDiskSize(validDiskSize).setZone(zone.toString()).build();
        RegionDiskTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskTypeName diskType = ProjectRegionDiskTypeName.of("[PROJECT]", "[REGION]", "[DISK_TYPE]");
        DiskType actualResponse = RegionDiskTypeClientTest.client.getRegionDiskType(diskType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionDiskTypeExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskTypeClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskTypeName diskType = ProjectRegionDiskTypeName.of("[PROJECT]", "[REGION]", "[DISK_TYPE]");
            RegionDiskTypeClientTest.client.getRegionDiskType(diskType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionDiskTypesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        DiskType itemsElement = DiskType.newBuilder().build();
        List<DiskType> items = Arrays.asList(itemsElement);
        RegionDiskTypeList expectedResponse = RegionDiskTypeList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        RegionDiskTypeClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        RegionDiskTypeClient.ListRegionDiskTypesPagedResponse pagedListResponse = RegionDiskTypeClientTest.client.listRegionDiskTypes(region);
        List<DiskType> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = RegionDiskTypeClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskTypeClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionDiskTypesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskTypeClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            RegionDiskTypeClientTest.client.listRegionDiskTypes(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

