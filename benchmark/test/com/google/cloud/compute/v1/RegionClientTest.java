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
import com.google.cloud.compute.v1.stub.RegionStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class RegionClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(getRegionMethodDescriptor, listRegionsMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(RegionClientTest.METHOD_DESCRIPTORS, RegionStubSettings.getDefaultEndpoint());

    private static RegionClient client;

    private static RegionSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void getRegionTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        String status = "status-892481550";
        Region expectedResponse = Region.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setSelfLink(selfLink).setStatus(status).build();
        RegionClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        Region actualResponse = RegionClientTest.client.getRegion(region);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            RegionClientTest.client.getRegion(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Region itemsElement = Region.newBuilder().build();
        List<Region> items = Arrays.asList(itemsElement);
        RegionList expectedResponse = RegionList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        RegionClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        RegionClient.ListRegionsPagedResponse pagedListResponse = RegionClientTest.client.listRegions(project);
        List<Region> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = RegionClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            RegionClientTest.client.listRegions(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

