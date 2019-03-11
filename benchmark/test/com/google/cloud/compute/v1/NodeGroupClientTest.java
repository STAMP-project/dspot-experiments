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
import com.google.cloud.compute.v1.stub.NodeGroupStubSettings;
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
public class NodeGroupClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(addNodesNodeGroupMethodDescriptor, aggregatedListNodeGroupsMethodDescriptor, deleteNodeGroupMethodDescriptor, deleteNodesNodeGroupMethodDescriptor, getNodeGroupMethodDescriptor, getIamPolicyNodeGroupMethodDescriptor, insertNodeGroupMethodDescriptor, listNodeGroupsMethodDescriptor, listNodesNodeGroupsMethodDescriptor, setIamPolicyNodeGroupMethodDescriptor, setNodeTemplateNodeGroupMethodDescriptor, testIamPermissionsNodeGroupMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(NodeGroupClientTest.METHOD_DESCRIPTORS, NodeGroupStubSettings.getDefaultEndpoint());

    private static NodeGroupClient client;

    private static NodeGroupSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void addNodesNodeGroupTest() {
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
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        NodeGroupsAddNodesRequest nodeGroupsAddNodesRequestResource = NodeGroupsAddNodesRequest.newBuilder().build();
        Operation actualResponse = NodeGroupClientTest.client.addNodesNodeGroup(nodeGroup, nodeGroupsAddNodesRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void addNodesNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupsAddNodesRequest nodeGroupsAddNodesRequestResource = NodeGroupsAddNodesRequest.newBuilder().build();
            NodeGroupClientTest.client.addNodesNodeGroup(nodeGroup, nodeGroupsAddNodesRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeGroupsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeGroupsScopedList itemsItem = NodeGroupsScopedList.newBuilder().build();
        Map<String, NodeGroupsScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        NodeGroupAggregatedList expectedResponse = NodeGroupAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        NodeGroupClient.AggregatedListNodeGroupsPagedResponse pagedListResponse = NodeGroupClientTest.client.aggregatedListNodeGroups(project);
        List<NodeGroupsScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListNodeGroupsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            NodeGroupClientTest.client.aggregatedListNodeGroups(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodeGroupTest() {
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
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        Operation actualResponse = NodeGroupClientTest.client.deleteNodeGroup(nodeGroup);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupClientTest.client.deleteNodeGroup(nodeGroup);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodesNodeGroupTest() {
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
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        NodeGroupsDeleteNodesRequest nodeGroupsDeleteNodesRequestResource = NodeGroupsDeleteNodesRequest.newBuilder().build();
        Operation actualResponse = NodeGroupClientTest.client.deleteNodesNodeGroup(nodeGroup, nodeGroupsDeleteNodesRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteNodesNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupsDeleteNodesRequest nodeGroupsDeleteNodesRequestResource = NodeGroupsDeleteNodesRequest.newBuilder().build();
            NodeGroupClientTest.client.deleteNodesNodeGroup(nodeGroup, nodeGroupsDeleteNodesRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeGroupTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String name = "name3373707";
        ProjectRegionNodeTemplateName nodeTemplate = ProjectRegionNodeTemplateName.of("[PROJECT]", "[REGION]", "[NODE_TEMPLATE]");
        String selfLink = "selfLink-1691268851";
        Integer size = 3530753;
        String status = "status-892481550";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        NodeGroup expectedResponse = NodeGroup.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setName(name).setNodeTemplate(nodeTemplate.toString()).setSelfLink(selfLink).setSize(size).setStatus(status).setZone(zone.toString()).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        NodeGroup actualResponse = NodeGroupClientTest.client.getNodeGroup(nodeGroup);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupClientTest.client.getNodeGroup(nodeGroup);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyNodeGroupTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        Policy actualResponse = NodeGroupClientTest.client.getIamPolicyNodeGroup(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            NodeGroupClientTest.client.getIamPolicyNodeGroup(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertNodeGroupTest() {
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
        ProjectZoneName zone2 = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone2.toString()).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        Integer initialNodeCount = 1682564205;
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        NodeGroup nodeGroupResource = NodeGroup.newBuilder().build();
        Operation actualResponse = NodeGroupClientTest.client.insertNodeGroup(initialNodeCount, zone, nodeGroupResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            Integer initialNodeCount = 1682564205;
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            NodeGroup nodeGroupResource = NodeGroup.newBuilder().build();
            NodeGroupClientTest.client.insertNodeGroup(initialNodeCount, zone, nodeGroupResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeGroupsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeGroup itemsElement = NodeGroup.newBuilder().build();
        List<NodeGroup> items = Arrays.asList(itemsElement);
        NodeGroupList expectedResponse = NodeGroupList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        NodeGroupClient.ListNodeGroupsPagedResponse pagedListResponse = NodeGroupClientTest.client.listNodeGroups(zone);
        List<NodeGroup> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listNodeGroupsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            NodeGroupClientTest.client.listNodeGroups(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listNodesNodeGroupsTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        NodeGroupNode itemsElement = NodeGroupNode.newBuilder().build();
        List<NodeGroupNode> items = Arrays.asList(itemsElement);
        NodeGroupsListNodes expectedResponse = NodeGroupsListNodes.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        NodeGroupClient.ListNodesNodeGroupsPagedResponse pagedListResponse = NodeGroupClientTest.client.listNodesNodeGroups(nodeGroup);
        List<NodeGroupNode> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listNodesNodeGroupsExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupClientTest.client.listNodesNodeGroups(nodeGroup);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyNodeGroupTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        ZoneSetPolicyRequest zoneSetPolicyRequestResource = ZoneSetPolicyRequest.newBuilder().build();
        Policy actualResponse = NodeGroupClientTest.client.setIamPolicyNodeGroup(resource, zoneSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            ZoneSetPolicyRequest zoneSetPolicyRequestResource = ZoneSetPolicyRequest.newBuilder().build();
            NodeGroupClientTest.client.setIamPolicyNodeGroup(resource, zoneSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setNodeTemplateNodeGroupTest() {
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
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
        NodeGroupsSetNodeTemplateRequest nodeGroupsSetNodeTemplateRequestResource = NodeGroupsSetNodeTemplateRequest.newBuilder().build();
        Operation actualResponse = NodeGroupClientTest.client.setNodeTemplateNodeGroup(nodeGroup, nodeGroupsSetNodeTemplateRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setNodeTemplateNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupName nodeGroup = ProjectZoneNodeGroupName.of("[PROJECT]", "[ZONE]", "[NODE_GROUP]");
            NodeGroupsSetNodeTemplateRequest nodeGroupsSetNodeTemplateRequestResource = NodeGroupsSetNodeTemplateRequest.newBuilder().build();
            NodeGroupClientTest.client.setNodeTemplateNodeGroup(nodeGroup, nodeGroupsSetNodeTemplateRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsNodeGroupTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        NodeGroupClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = NodeGroupClientTest.client.testIamPermissionsNodeGroup(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = NodeGroupClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = NodeGroupClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsNodeGroupExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        NodeGroupClientTest.mockService.addException(exception);
        try {
            ProjectZoneNodeGroupResourceName resource = ProjectZoneNodeGroupResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            NodeGroupClientTest.client.testIamPermissionsNodeGroup(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

