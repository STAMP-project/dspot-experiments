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
import com.google.cloud.compute.v1.stub.GlobalForwardingRuleStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class GlobalForwardingRuleClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteGlobalForwardingRuleMethodDescriptor, getGlobalForwardingRuleMethodDescriptor, insertGlobalForwardingRuleMethodDescriptor, listGlobalForwardingRulesMethodDescriptor, setTargetGlobalForwardingRuleMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(GlobalForwardingRuleClientTest.METHOD_DESCRIPTORS, GlobalForwardingRuleStubSettings.getDefaultEndpoint());

    private static GlobalForwardingRuleClient client;

    private static GlobalForwardingRuleSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteGlobalForwardingRuleTest() {
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
        GlobalForwardingRuleClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
        Operation actualResponse = GlobalForwardingRuleClientTest.client.deleteGlobalForwardingRule(forwardingRule);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = GlobalForwardingRuleClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = GlobalForwardingRuleClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteGlobalForwardingRuleExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        GlobalForwardingRuleClientTest.mockService.addException(exception);
        try {
            ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
            GlobalForwardingRuleClientTest.client.deleteGlobalForwardingRule(forwardingRule);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getGlobalForwardingRuleTest() {
        String iPAddress = "iPAddress-551149873";
        String iPProtocol = "iPProtocol323774237";
        Boolean allPorts = true;
        ProjectGlobalBackendServiceName backendService = ProjectGlobalBackendServiceName.of("[PROJECT]", "[BACKEND_SERVICE]");
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String ipVersion = "ipVersion-1315653184";
        String kind = "kind3292052";
        String loadBalancingScheme = "loadBalancingScheme1974502980";
        String name = "name3373707";
        ProjectGlobalNetworkName network = ProjectGlobalNetworkName.of("[PROJECT]", "[NETWORK]");
        String networkTier = "networkTier-19473069";
        String portRange = "portRange217518079";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String serviceLabel = "serviceLabel-1730474774";
        String serviceName = "serviceName359880149";
        ProjectRegionSubnetworkName subnetwork = ProjectRegionSubnetworkName.of("[PROJECT]", "[REGION]", "[SUBNETWORK]");
        String target = "target-880905839";
        ForwardingRule expectedResponse = ForwardingRule.newBuilder().setIPAddress(iPAddress).setIPProtocol(iPProtocol).setAllPorts(allPorts).setBackendService(backendService.toString()).setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setIpVersion(ipVersion).setKind(kind).setLoadBalancingScheme(loadBalancingScheme).setName(name).setNetwork(network.toString()).setNetworkTier(networkTier).setPortRange(portRange).setRegion(region.toString()).setSelfLink(selfLink).setServiceLabel(serviceLabel).setServiceName(serviceName).setSubnetwork(subnetwork.toString()).setTarget(target).build();
        GlobalForwardingRuleClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
        ForwardingRule actualResponse = GlobalForwardingRuleClientTest.client.getGlobalForwardingRule(forwardingRule);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = GlobalForwardingRuleClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = GlobalForwardingRuleClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getGlobalForwardingRuleExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        GlobalForwardingRuleClientTest.mockService.addException(exception);
        try {
            ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
            GlobalForwardingRuleClientTest.client.getGlobalForwardingRule(forwardingRule);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertGlobalForwardingRuleTest() {
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
        GlobalForwardingRuleClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        ForwardingRule forwardingRuleResource = ForwardingRule.newBuilder().build();
        Operation actualResponse = GlobalForwardingRuleClientTest.client.insertGlobalForwardingRule(project, forwardingRuleResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = GlobalForwardingRuleClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = GlobalForwardingRuleClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertGlobalForwardingRuleExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        GlobalForwardingRuleClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            ForwardingRule forwardingRuleResource = ForwardingRule.newBuilder().build();
            GlobalForwardingRuleClientTest.client.insertGlobalForwardingRule(project, forwardingRuleResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listGlobalForwardingRulesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        ForwardingRule itemsElement = ForwardingRule.newBuilder().build();
        List<ForwardingRule> items = Arrays.asList(itemsElement);
        ForwardingRuleList expectedResponse = ForwardingRuleList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        GlobalForwardingRuleClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        GlobalForwardingRuleClient.ListGlobalForwardingRulesPagedResponse pagedListResponse = GlobalForwardingRuleClientTest.client.listGlobalForwardingRules(project);
        List<ForwardingRule> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = GlobalForwardingRuleClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = GlobalForwardingRuleClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listGlobalForwardingRulesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        GlobalForwardingRuleClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            GlobalForwardingRuleClientTest.client.listGlobalForwardingRules(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setTargetGlobalForwardingRuleTest() {
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
        GlobalForwardingRuleClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
        TargetReference targetReferenceResource = TargetReference.newBuilder().build();
        Operation actualResponse = GlobalForwardingRuleClientTest.client.setTargetGlobalForwardingRule(forwardingRule, targetReferenceResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = GlobalForwardingRuleClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = GlobalForwardingRuleClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setTargetGlobalForwardingRuleExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        GlobalForwardingRuleClientTest.mockService.addException(exception);
        try {
            ProjectGlobalForwardingRuleName forwardingRule = ProjectGlobalForwardingRuleName.of("[PROJECT]", "[FORWARDING_RULE]");
            TargetReference targetReferenceResource = TargetReference.newBuilder().build();
            GlobalForwardingRuleClientTest.client.setTargetGlobalForwardingRule(forwardingRule, targetReferenceResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

