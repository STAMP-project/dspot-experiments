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
import com.google.cloud.compute.v1.stub.RegionDiskStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class RegionDiskClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(createSnapshotRegionDiskMethodDescriptor, deleteRegionDiskMethodDescriptor, getRegionDiskMethodDescriptor, insertRegionDiskMethodDescriptor, listRegionDisksMethodDescriptor, resizeRegionDiskMethodDescriptor, setLabelsRegionDiskMethodDescriptor, testIamPermissionsRegionDiskMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(RegionDiskClientTest.METHOD_DESCRIPTORS, RegionDiskStubSettings.getDefaultEndpoint());

    private static RegionDiskClient client;

    private static RegionDiskSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void createSnapshotRegionDiskTest() {
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
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
        Snapshot snapshotResource = Snapshot.newBuilder().build();
        Operation actualResponse = RegionDiskClientTest.client.createSnapshotRegionDisk(disk, snapshotResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void createSnapshotRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
            Snapshot snapshotResource = Snapshot.newBuilder().build();
            RegionDiskClientTest.client.createSnapshotRegionDisk(disk, snapshotResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteRegionDiskTest() {
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
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
        Operation actualResponse = RegionDiskClientTest.client.deleteRegionDisk(disk);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
            RegionDiskClientTest.client.deleteRegionDisk(disk);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionDiskTest() {
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String id = "id3355";
        String kind = "kind3292052";
        String labelFingerprint = "labelFingerprint714995737";
        String lastAttachTimestamp = "lastAttachTimestamp-2105323995";
        String lastDetachTimestamp = "lastDetachTimestamp-480399885";
        String name = "name3373707";
        String options = "options-1249474914";
        String physicalBlockSizeBytes = "physicalBlockSizeBytes-1190604793";
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String sizeGb = "sizeGb2105542105";
        String sourceImage = "sourceImage1661056055";
        String sourceImageId = "sourceImageId-2092155357";
        String sourceSnapshot = "sourceSnapshot-947679896";
        String sourceSnapshotId = "sourceSnapshotId-1511650478";
        String status = "status-892481550";
        String type = "type3575610";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Disk expectedResponse = Disk.newBuilder().setCreationTimestamp(creationTimestamp).setDescription(description).setId(id).setKind(kind).setLabelFingerprint(labelFingerprint).setLastAttachTimestamp(lastAttachTimestamp).setLastDetachTimestamp(lastDetachTimestamp).setName(name).setOptions(options).setPhysicalBlockSizeBytes(physicalBlockSizeBytes).setRegion(region.toString()).setSelfLink(selfLink).setSizeGb(sizeGb).setSourceImage(sourceImage).setSourceImageId(sourceImageId).setSourceSnapshot(sourceSnapshot).setSourceSnapshotId(sourceSnapshotId).setStatus(status).setType(type).setZone(zone.toString()).build();
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
        Disk actualResponse = RegionDiskClientTest.client.getRegionDisk(disk);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
            RegionDiskClientTest.client.getRegionDisk(disk);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionDiskTest() {
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
        ProjectRegionName region2 = ProjectRegionName.of("[PROJECT]", "[REGION]");
        String selfLink = "selfLink-1691268851";
        String startTime = "startTime-1573145462";
        String status = "status-892481550";
        String statusMessage = "statusMessage-239442758";
        String targetId = "targetId-815576439";
        String targetLink = "targetLink-2084812312";
        String user = "user3599307";
        ProjectZoneName zone = ProjectZoneName.of("[PROJECT]", "[ZONE]");
        Operation expectedResponse = Operation.newBuilder().setClientOperationId(clientOperationId).setCreationTimestamp(creationTimestamp).setDescription(description).setEndTime(endTime).setHttpErrorMessage(httpErrorMessage).setHttpErrorStatusCode(httpErrorStatusCode).setId(id).setInsertTime(insertTime).setKind(kind).setName(name).setOperationType(operationType).setProgress(progress).setRegion(region2.toString()).setSelfLink(selfLink).setStartTime(startTime).setStatus(status).setStatusMessage(statusMessage).setTargetId(targetId).setTargetLink(targetLink).setUser(user).setZone(zone.toString()).build();
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        Disk diskResource = Disk.newBuilder().build();
        Operation actualResponse = RegionDiskClientTest.client.insertRegionDisk(region, diskResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            Disk diskResource = Disk.newBuilder().build();
            RegionDiskClientTest.client.insertRegionDisk(region, diskResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionDisksTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Disk itemsElement = Disk.newBuilder().build();
        List<Disk> items = Arrays.asList(itemsElement);
        DiskList expectedResponse = DiskList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
        RegionDiskClient.ListRegionDisksPagedResponse pagedListResponse = RegionDiskClientTest.client.listRegionDisks(region);
        List<Disk> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listRegionDisksExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionName region = ProjectRegionName.of("[PROJECT]", "[REGION]");
            RegionDiskClientTest.client.listRegionDisks(region);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void resizeRegionDiskTest() {
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
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
        RegionDisksResizeRequest regionDisksResizeRequestResource = RegionDisksResizeRequest.newBuilder().build();
        Operation actualResponse = RegionDiskClientTest.client.resizeRegionDisk(disk, regionDisksResizeRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void resizeRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskName disk = ProjectRegionDiskName.of("[PROJECT]", "[REGION]", "[DISK]");
            RegionDisksResizeRequest regionDisksResizeRequestResource = RegionDisksResizeRequest.newBuilder().build();
            RegionDiskClientTest.client.resizeRegionDisk(disk, regionDisksResizeRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsRegionDiskTest() {
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
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskResourceName resource = ProjectRegionDiskResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
        RegionSetLabelsRequest regionSetLabelsRequestResource = RegionSetLabelsRequest.newBuilder().build();
        Operation actualResponse = RegionDiskClientTest.client.setLabelsRegionDisk(resource, regionSetLabelsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskResourceName resource = ProjectRegionDiskResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
            RegionSetLabelsRequest regionSetLabelsRequestResource = RegionSetLabelsRequest.newBuilder().build();
            RegionDiskClientTest.client.setLabelsRegionDisk(resource, regionSetLabelsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsRegionDiskTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        RegionDiskClientTest.mockService.addResponse(expectedResponse);
        ProjectRegionDiskResourceName resource = ProjectRegionDiskResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = RegionDiskClientTest.client.testIamPermissionsRegionDisk(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = RegionDiskClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = RegionDiskClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsRegionDiskExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        RegionDiskClientTest.mockService.addException(exception);
        try {
            ProjectRegionDiskResourceName resource = ProjectRegionDiskResourceName.of("[PROJECT]", "[REGION]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            RegionDiskClientTest.client.testIamPermissionsRegionDisk(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

