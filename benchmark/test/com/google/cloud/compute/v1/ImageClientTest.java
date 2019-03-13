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
import com.google.cloud.compute.v1.stub.ImageStubSettings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class ImageClientTest {
    private static final List<ApiMethodDescriptor> METHOD_DESCRIPTORS = ImmutableList.copyOf(Lists.<ApiMethodDescriptor>newArrayList(deleteImageMethodDescriptor, deprecateImageMethodDescriptor, getImageMethodDescriptor, getFromFamilyImageMethodDescriptor, getIamPolicyImageMethodDescriptor, insertImageMethodDescriptor, listImagesMethodDescriptor, setIamPolicyImageMethodDescriptor, setLabelsImageMethodDescriptor, testIamPermissionsImageMethodDescriptor));

    private static final MockHttpService mockService = new MockHttpService(ImageClientTest.METHOD_DESCRIPTORS, ImageStubSettings.getDefaultEndpoint());

    private static ImageClient client;

    private static ImageSettings clientSettings;

    @Test
    @SuppressWarnings("all")
    public void deleteImageTest() {
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
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
        Operation actualResponse = ImageClientTest.client.deleteImage(image);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deleteImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
            ImageClientTest.client.deleteImage(image);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deprecateImageTest() {
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
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
        DeprecationStatus deprecationStatusResource = DeprecationStatus.newBuilder().build();
        Operation actualResponse = ImageClientTest.client.deprecateImage(image, deprecationStatusResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void deprecateImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
            DeprecationStatus deprecationStatusResource = DeprecationStatus.newBuilder().build();
            ImageClientTest.client.deprecateImage(image, deprecationStatusResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getImageTest() {
        String archiveSizeBytes = "archiveSizeBytes-1766390198";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String diskSizeGb = "diskSizeGb-757478089";
        ProjectGlobalImageFamilyName family = ProjectGlobalImageFamilyName.of("[PROJECT]", "[FAMILY]");
        String id = "id3355";
        String kind = "kind3292052";
        String labelFingerprint = "labelFingerprint714995737";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        String sourceDisk = "sourceDisk-85117119";
        String sourceDiskId = "sourceDiskId-1693292839";
        String sourceImage = "sourceImage1661056055";
        String sourceImageId = "sourceImageId-2092155357";
        String sourceSnapshot = "sourceSnapshot-947679896";
        String sourceSnapshotId = "sourceSnapshotId-1511650478";
        String sourceType = "sourceType-84625186";
        String status = "status-892481550";
        Image expectedResponse = Image.newBuilder().setArchiveSizeBytes(archiveSizeBytes).setCreationTimestamp(creationTimestamp).setDescription(description).setDiskSizeGb(diskSizeGb).setFamily(family.toString()).setId(id).setKind(kind).setLabelFingerprint(labelFingerprint).setName(name).setSelfLink(selfLink).setSourceDisk(sourceDisk).setSourceDiskId(sourceDiskId).setSourceImage(sourceImage).setSourceImageId(sourceImageId).setSourceSnapshot(sourceSnapshot).setSourceSnapshotId(sourceSnapshotId).setSourceType(sourceType).setStatus(status).build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
        Image actualResponse = ImageClientTest.client.getImage(image);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageName image = ProjectGlobalImageName.of("[PROJECT]", "[IMAGE]");
            ImageClientTest.client.getImage(image);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getFromFamilyImageTest() {
        String archiveSizeBytes = "archiveSizeBytes-1766390198";
        String creationTimestamp = "creationTimestamp567396278";
        String description = "description-1724546052";
        String diskSizeGb = "diskSizeGb-757478089";
        ProjectGlobalImageFamilyName family2 = ProjectGlobalImageFamilyName.of("[PROJECT]", "[FAMILY]");
        String id = "id3355";
        String kind = "kind3292052";
        String labelFingerprint = "labelFingerprint714995737";
        String name = "name3373707";
        String selfLink = "selfLink-1691268851";
        String sourceDisk = "sourceDisk-85117119";
        String sourceDiskId = "sourceDiskId-1693292839";
        String sourceImage = "sourceImage1661056055";
        String sourceImageId = "sourceImageId-2092155357";
        String sourceSnapshot = "sourceSnapshot-947679896";
        String sourceSnapshotId = "sourceSnapshotId-1511650478";
        String sourceType = "sourceType-84625186";
        String status = "status-892481550";
        Image expectedResponse = Image.newBuilder().setArchiveSizeBytes(archiveSizeBytes).setCreationTimestamp(creationTimestamp).setDescription(description).setDiskSizeGb(diskSizeGb).setFamily(family2.toString()).setId(id).setKind(kind).setLabelFingerprint(labelFingerprint).setName(name).setSelfLink(selfLink).setSourceDisk(sourceDisk).setSourceDiskId(sourceDiskId).setSourceImage(sourceImage).setSourceImageId(sourceImageId).setSourceSnapshot(sourceSnapshot).setSourceSnapshotId(sourceSnapshotId).setSourceType(sourceType).setStatus(status).build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageFamilyName family = ProjectGlobalImageFamilyName.of("[PROJECT]", "[FAMILY]");
        Image actualResponse = ImageClientTest.client.getFromFamilyImage(family);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getFromFamilyImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageFamilyName family = ProjectGlobalImageFamilyName.of("[PROJECT]", "[FAMILY]");
            ImageClientTest.client.getFromFamilyImage(family);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyImageTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
        Policy actualResponse = ImageClientTest.client.getIamPolicyImage(resource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void getIamPolicyImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
            ImageClientTest.client.getIamPolicyImage(resource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void insertImageTest() {
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
        ImageClientTest.mockService.addResponse(expectedResponse);
        Boolean forceCreate = true;
        ProjectName project = ProjectName.of("[PROJECT]");
        Image imageResource = Image.newBuilder().build();
        Operation actualResponse = ImageClientTest.client.insertImage(forceCreate, project, imageResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void insertImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            Boolean forceCreate = true;
            ProjectName project = ProjectName.of("[PROJECT]");
            Image imageResource = Image.newBuilder().build();
            ImageClientTest.client.insertImage(forceCreate, project, imageResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void listImagesTest() {
        String id = "id3355";
        String kind = "kind3292052";
        String nextPageToken = "";
        String selfLink = "selfLink-1691268851";
        Image itemsElement = Image.newBuilder().build();
        List<Image> items = Arrays.asList(itemsElement);
        ImageList expectedResponse = ImageList.newBuilder().setId(id).setKind(kind).setNextPageToken(nextPageToken).setSelfLink(selfLink).addAllItems(items).build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectName project = ProjectName.of("[PROJECT]");
        ImageClient.ListImagesPagedResponse pagedListResponse = ImageClientTest.client.listImages(project);
        List<Image> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getItemsList().get(0), resources.get(0));
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void listImagesExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectName project = ProjectName.of("[PROJECT]");
            ImageClientTest.client.listImages(project);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyImageTest() {
        String etag = "etag3123477";
        Boolean iamOwned = false;
        Integer version = 351608024;
        Policy expectedResponse = Policy.newBuilder().setEtag(etag).setIamOwned(iamOwned).setVersion(version).build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
        GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
        Policy actualResponse = ImageClientTest.client.setIamPolicyImage(resource, globalSetPolicyRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setIamPolicyImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
            GlobalSetPolicyRequest globalSetPolicyRequestResource = GlobalSetPolicyRequest.newBuilder().build();
            ImageClientTest.client.setIamPolicyImage(resource, globalSetPolicyRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsImageTest() {
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
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
        GlobalSetLabelsRequest globalSetLabelsRequestResource = GlobalSetLabelsRequest.newBuilder().build();
        Operation actualResponse = ImageClientTest.client.setLabelsImage(resource, globalSetLabelsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void setLabelsImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
            GlobalSetLabelsRequest globalSetLabelsRequestResource = GlobalSetLabelsRequest.newBuilder().build();
            ImageClientTest.client.setLabelsImage(resource, globalSetLabelsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsImageTest() {
        TestPermissionsResponse expectedResponse = TestPermissionsResponse.newBuilder().build();
        ImageClientTest.mockService.addResponse(expectedResponse);
        ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
        TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
        TestPermissionsResponse actualResponse = ImageClientTest.client.testIamPermissionsImage(resource, testPermissionsRequestResource);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<String> actualRequests = ImageClientTest.mockService.getRequestPaths();
        Assert.assertEquals(1, actualRequests.size());
        String apiClientHeaderKey = ImageClientTest.mockService.getRequestHeaders().get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey()).iterator().next();
        Assert.assertTrue(GaxHttpJsonProperties.getDefaultApiClientHeaderPattern().matcher(apiClientHeaderKey).matches());
    }

    @Test
    @SuppressWarnings("all")
    public void testIamPermissionsImageExceptionTest() throws Exception {
        ApiException exception = ApiExceptionFactory.createException(new Exception(), FakeStatusCode.of(INVALID_ARGUMENT), false);
        ImageClientTest.mockService.addException(exception);
        try {
            ProjectGlobalImageResourceName resource = ProjectGlobalImageResourceName.of("[PROJECT]", "[RESOURCE]");
            TestPermissionsRequest testPermissionsRequestResource = TestPermissionsRequest.newBuilder().build();
            ImageClientTest.client.testIamPermissionsImage(resource, testPermissionsRequestResource);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

