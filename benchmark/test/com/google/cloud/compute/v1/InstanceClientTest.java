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
import com.google.cloud.compute.v1.stub.InstanceStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class InstanceClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(addAccessConfigInstanceMethodDescriptor, aggregatedListInstancesMethodDescriptor, attachDiskInstanceMethodDescriptor, deleteInstanceMethodDescriptor, deleteAccessConfigInstanceMethodDescriptor, detachDiskInstanceMethodDescriptor, getInstanceMethodDescriptor, getIamPolicyInstanceMethodDescriptor, getSerialPortOutputInstanceMethodDescriptor, insertInstanceMethodDescriptor, listInstancesMethodDescriptor, listReferrersInstancesMethodDescriptor, resetInstanceMethodDescriptor, setDeletionProtectionInstanceMethodDescriptor, setDiskAutoDeleteInstanceMethodDescriptor, setIamPolicyInstanceMethodDescriptor, setLabelsInstanceMethodDescriptor, setMachineResourcesInstanceMethodDescriptor, setMachineTypeInstanceMethodDescriptor, setMetadataInstanceMethodDescriptor, setMinCpuPlatformInstanceMethodDescriptor, setSchedulingInstanceMethodDescriptor, setServiceAccountInstanceMethodDescriptor, setTagsInstanceMethodDescriptor, simulateMaintenanceEventInstanceMethodDescriptor, startInstanceMethodDescriptor, startWithEncryptionKeyInstanceMethodDescriptor, stopInstanceMethodDescriptor, testIamPermissionsInstanceMethodDescriptor, updateAccessConfigInstanceMethodDescriptor, updateNetworkInterfaceInstanceMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(InstanceClientTest.METHOD_DESCRIPTORS, InstanceStubSettings.getDefaultEndpoint());

    private static InstanceClient client;

    private static InstanceSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void addAccessConfigInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        String networkInterface = "networkInterface902258792";
        AccessConfig accessConfigResource = AccessConfig.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.addAccessConfigInstance(instance, networkInterface, accessConfigResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void addAccessConfigInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            String networkInterface = "networkInterface902258792";
            AccessConfig accessConfigResource = AccessConfig.newBuilder().build();
            InstanceClientTest.client.addAccessConfigInstance(instance, networkInterface, accessConfigResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListInstancesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        InstancesScopedList itemsItem = InstancesScopedList.newBuilder().build();
        Map<String, InstancesScopedList> items = new HashMap<>();
        items.put("items", itemsItem);
        InstanceAggregatedList expectedResponse = InstanceAggregatedList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).putAllItems(items).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        InstanceClient.AggregatedListInstancesPagedResponse pagedListResponse = InstanceClientTest.client.aggregatedListInstances(project);
        List<InstancesScopedList> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsMap().values().iterator().next(), resources.get(0));
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void aggregatedListInstancesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            InstanceClientTest.client.aggregatedListInstances(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void attachDiskInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Boolean forceAttach = false;
        AttachedDisk attachedDiskResource = AttachedDisk.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.attachDiskInstance(instance, forceAttach, attachedDiskResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void attachDiskInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Boolean forceAttach = false;
            AttachedDisk attachedDiskResource = AttachedDisk.newBuilder().build();
            InstanceClientTest.client.attachDiskInstance(instance, forceAttach, attachedDiskResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Operation actualResponse = InstanceClientTest.client.deleteInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.deleteInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAccessConfigInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        String networkInterface = "networkInterface902258792";
        String accessConfig = "accessConfig-464014723";
        Operation actualResponse = InstanceClientTest.client.deleteAccessConfigInstance(instance, networkInterface, accessConfig);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAccessConfigInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            String networkInterface = "networkInterface902258792";
            String accessConfig = "accessConfig-464014723";
            InstanceClientTest.client.deleteAccessConfigInstance(instance, networkInterface, accessConfig);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void detachDiskInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        String deviceName = "deviceName-1543071020";
        Operation actualResponse = InstanceClientTest.client.detachDiskInstance(instance, deviceName);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void detachDiskInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            String deviceName = "deviceName-1543071020";
            InstanceClientTest.client.detachDiskInstance(instance, deviceName);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getInstanceTest() {
        Boolean canIpForward = true;
        String cpuPlatform = "cpuPlatform947156266";
        String creationTimestamp = "creationTimestamp567396278";
        Boolean deletionProtection = true;
        String description = "description-1724546052";
        String hostname = "hostname-299803597";
        String id = "id3355";
        String kind = "kind3292052";
        String labelFingerprint = "labelFingerprint714995737";
        ProjectZoneMachineTypeName machineType = ProjectZoneMachineTypeName.of("[PROJECT]", "[ZONE]", "[MACHINE_TYPE]");
        String minCpuPlatform = "minCpuPlatform-1367699977";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        Boolean startRestricted = true;
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Instance expectedResponse = Instance.newBuilder().setCanIpForward(canIpForward).setCpuPlatform(cpuPlatform).setCreationTimestamp(creationTimestamp).setDeletionProtection(deletionProtection).setDescription(description).setHostname(hostname).setId(id).setKind(kind).setLabelFingerprint(labelFingerprint).setMachineType(machineType.toString()).setMinCpuPlatform(minCpuPlatform).setName(name).setSelfLink(selfLink).setStartRestricted(startRestricted).setStatus(status).setStatusMessage(statusMessage).setZone(zone.toString()).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Instance actualResponse = InstanceClientTest.client.getInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.getInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyInstanceTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        Policy actualResponse = InstanceClientTest.client.getIamPolicyInstance(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            InstanceClientTest.client.getIamPolicyInstance(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getSerialPortOutputInstanceTest() {
        String contents = "contents-567321830";
        String kind = "kind3292052";
        String next = "next3377907";
        String selfLink = "selfLink-1691268851";
        String start2 = "start2-1897185387";
        SerialPortOutput expectedResponse = SerialPortOutput.newBuilder().setContents(contents).setKind(kind).setNext(next).setSelfLink(selfLink).setStart(start2).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Integer port = 3446913;
        String start = "start109757538";
        SerialPortOutput actualResponse = InstanceClientTest.client.getSerialPortOutputInstance(instance, port, start);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getSerialPortOutputInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Integer port = 3446913;
            String start = "start109757538";
            InstanceClientTest.client.getSerialPortOutputInstance(instance, port, start);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Instance instanceResource = Instance.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.insertInstance(zone, instanceResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            Instance instanceResource = Instance.newBuilder().build();
            InstanceClientTest.client.insertInstance(zone, instanceResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listInstancesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Instance itemsElement = Instance.newBuilder().build();
        List<Instance> items = Arrays.asList(itemsElement);
        InstanceList expectedResponse = InstanceList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        InstanceClient.ListInstancesPagedResponse pagedListResponse = InstanceClientTest.client.listInstances(zone);
        List<Instance> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listInstancesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
            InstanceClientTest.client.listInstances(zone);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listReferrersInstancesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Reference itemsElement = Reference.newBuilder().build();
        List<Reference> items = Arrays.asList(itemsElement);
        InstanceListReferrers expectedResponse = InstanceListReferrers.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstanceClient.ListReferrersInstancesPagedResponse pagedListResponse = InstanceClientTest.client.listReferrersInstances(instance);
        List<Reference> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listReferrersInstancesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.listReferrersInstances(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void resetInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Operation actualResponse = InstanceClientTest.client.resetInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void resetInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.resetInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setDeletionProtectionInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        Boolean deletionProtection = true;
        Operation actualResponse = InstanceClientTest.client.setDeletionProtectionInstance(resource, deletionProtection);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setDeletionProtectionInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            Boolean deletionProtection = true;
            InstanceClientTest.client.setDeletionProtectionInstance(resource, deletionProtection);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setDiskAutoDeleteInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Boolean autoDelete = false;
        String deviceName = "deviceName-1543071020";
        Operation actualResponse = InstanceClientTest.client.setDiskAutoDeleteInstance(instance, autoDelete, deviceName);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setDiskAutoDeleteInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Boolean autoDelete = false;
            String deviceName = "deviceName-1543071020";
            InstanceClientTest.client.setDiskAutoDeleteInstance(instance, autoDelete, deviceName);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyInstanceTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        ZoneSetPolicyRequest zoneSetPolicyRequestResource = ZoneSetPolicyRequest.newBuilder().build();
        Policy actualResponse = InstanceClientTest.client.setIamPolicyInstance(resource, zoneSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            ZoneSetPolicyRequest zoneSetPolicyRequestResource = ZoneSetPolicyRequest.newBuilder().build();
            InstanceClientTest.client.setIamPolicyInstance(resource, zoneSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesSetLabelsRequest instancesSetLabelsRequestResource = InstancesSetLabelsRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setLabelsInstance(instance, instancesSetLabelsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesSetLabelsRequest instancesSetLabelsRequestResource = InstancesSetLabelsRequest.newBuilder().build();
            InstanceClientTest.client.setLabelsInstance(instance, instancesSetLabelsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setMachineResourcesInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesSetMachineResourcesRequest instancesSetMachineResourcesRequestResource = InstancesSetMachineResourcesRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setMachineResourcesInstance(instance, instancesSetMachineResourcesRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setMachineResourcesInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesSetMachineResourcesRequest instancesSetMachineResourcesRequestResource = InstancesSetMachineResourcesRequest.newBuilder().build();
            InstanceClientTest.client.setMachineResourcesInstance(instance, instancesSetMachineResourcesRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setMachineTypeInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesSetMachineTypeRequest instancesSetMachineTypeRequestResource = InstancesSetMachineTypeRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setMachineTypeInstance(instance, instancesSetMachineTypeRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setMachineTypeInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesSetMachineTypeRequest instancesSetMachineTypeRequestResource = InstancesSetMachineTypeRequest.newBuilder().build();
            InstanceClientTest.client.setMachineTypeInstance(instance, instancesSetMachineTypeRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setMetadataInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Metadata metadataResource = Metadata.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setMetadataInstance(instance, metadataResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setMetadataInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Metadata metadataResource = Metadata.newBuilder().build();
            InstanceClientTest.client.setMetadataInstance(instance, metadataResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setMinCpuPlatformInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesSetMinCpuPlatformRequest instancesSetMinCpuPlatformRequestResource = InstancesSetMinCpuPlatformRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setMinCpuPlatformInstance(instance, instancesSetMinCpuPlatformRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setMinCpuPlatformInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesSetMinCpuPlatformRequest instancesSetMinCpuPlatformRequestResource = InstancesSetMinCpuPlatformRequest.newBuilder().build();
            InstanceClientTest.client.setMinCpuPlatformInstance(instance, instancesSetMinCpuPlatformRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setSchedulingInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Scheduling schedulingResource = Scheduling.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setSchedulingInstance(instance, schedulingResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setSchedulingInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Scheduling schedulingResource = Scheduling.newBuilder().build();
            InstanceClientTest.client.setSchedulingInstance(instance, schedulingResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setServiceAccountInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesSetServiceAccountRequest instancesSetServiceAccountRequestResource = InstancesSetServiceAccountRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setServiceAccountInstance(instance, instancesSetServiceAccountRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setServiceAccountInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesSetServiceAccountRequest instancesSetServiceAccountRequestResource = InstancesSetServiceAccountRequest.newBuilder().build();
            InstanceClientTest.client.setServiceAccountInstance(instance, instancesSetServiceAccountRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setTagsInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Tags tagsResource = Tags.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.setTagsInstance(instance, tagsResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setTagsInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            Tags tagsResource = Tags.newBuilder().build();
            InstanceClientTest.client.setTagsInstance(instance, tagsResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void simulateMaintenanceEventInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Operation actualResponse = InstanceClientTest.client.simulateMaintenanceEventInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void simulateMaintenanceEventInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.simulateMaintenanceEventInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void startInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Operation actualResponse = InstanceClientTest.client.startInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void startInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.startInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void startWithEncryptionKeyInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        InstancesStartWithEncryptionKeyRequest instancesStartWithEncryptionKeyRequestResource = InstancesStartWithEncryptionKeyRequest.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.startWithEncryptionKeyInstance(instance, instancesStartWithEncryptionKeyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void startWithEncryptionKeyInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstancesStartWithEncryptionKeyRequest instancesStartWithEncryptionKeyRequestResource = InstancesStartWithEncryptionKeyRequest.newBuilder().build();
            InstanceClientTest.client.startWithEncryptionKeyInstance(instance, instancesStartWithEncryptionKeyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void stopInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        Operation actualResponse = InstanceClientTest.client.stopInstance(instance);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void stopInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            InstanceClientTest.client.stopInstance(instance);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsInstanceTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = InstanceClientTest.client.testIamPermissionsInstance(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceResourceName resource = ProjectZoneInstanceResourceName.of("[PROJECT]", "[ZONE]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            InstanceClientTest.client.testIamPermissionsInstance(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateAccessConfigInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        String networkInterface = "networkInterface902258792";
        AccessConfig accessConfigResource = AccessConfig.newBuilder().build();
        Operation actualResponse = InstanceClientTest.client.updateAccessConfigInstance(instance, networkInterface, accessConfigResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateAccessConfigInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            String networkInterface = "networkInterface902258792";
            AccessConfig accessConfigResource = AccessConfig.newBuilder().build();
            InstanceClientTest.client.updateAccessConfigInstance(instance, networkInterface, accessConfigResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void updateNetworkInterfaceInstanceTest() {
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
        InstanceClientTest.mockService.addResponse(expectedResponse);
        ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
        String networkInterface = "networkInterface902258792";
        NetworkInterface networkInterfaceResource = NetworkInterface.newBuilder().build();
        List<String> fieldMask = new ArrayList<>();
        Operation actualResponse = InstanceClientTest.client.updateNetworkInterfaceInstance(instance, networkInterface, networkInterfaceResource, fieldMask);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = InstanceClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = InstanceClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void updateNetworkInterfaceInstanceExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        InstanceClientTest.mockService.addException(exception);
        try {
            ProjectZoneInstanceName instance = ProjectZoneInstanceName.of("[PROJECT]", "[ZONE]", "[INSTANCE]");
            String networkInterface = "networkInterface902258792";
            NetworkInterface networkInterfaceResource = NetworkInterface.newBuilder().build();
            List<String> fieldMask = new ArrayList<>();
            InstanceClientTest.client.updateNetworkInterfaceInstance(instance, networkInterface, networkInterfaceResource, fieldMask);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

