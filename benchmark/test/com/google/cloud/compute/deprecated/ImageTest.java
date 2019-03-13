/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated;


import Compute.ImageOption;
import DeprecationStatus.Status.DELETED;
import DeprecationStatus.Status.DEPRECATED;
import ImageConfiguration.SourceType;
import ImageInfo.Status;
import StorageImageConfiguration.ContainerType.TAR;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ImageTest {
    private static final ImageId IMAGE_ID = ImageId.of("project", "image");

    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final Status STATUS = Status.READY;

    private static final List<LicenseId> LICENSES = ImmutableList.of(LicenseId.of("project", "license1"), LicenseId.of("project", "license2"));

    private static final Long DISK_SIZE_GB = 42L;

    private static final String STORAGE_SOURCE = "source";

    private static final Long ARCHIVE_SIZE_BYTES = 24L;

    private static final String SHA1_CHECKSUM = "checksum";

    private static final DiskId SOURCE_DISK = DiskId.of("project", "zone", "disk");

    private static final String SOURCE_DISK_ID = "diskId";

    private static final SourceType SOURCE_TYPE = SourceType.RAW;

    private static final StorageImageConfiguration STORAGE_CONFIGURATION = StorageImageConfiguration.newBuilder(ImageTest.STORAGE_SOURCE).setArchiveSizeBytes(ImageTest.ARCHIVE_SIZE_BYTES).setContainerType(TAR).setSha1(ImageTest.SHA1_CHECKSUM).setSourceType(ImageTest.SOURCE_TYPE).build();

    private static final DiskImageConfiguration DISK_CONFIGURATION = DiskImageConfiguration.newBuilder(ImageTest.SOURCE_DISK).setArchiveSizeBytes(ImageTest.ARCHIVE_SIZE_BYTES).setSourceDiskId(ImageTest.SOURCE_DISK_ID).setSourceType(ImageTest.SOURCE_TYPE).build();

    private static final DeprecationStatus<ImageId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, ImageTest.IMAGE_ID);

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Image image;

    private Image diskImage;

    private Image storageImage;

    @Test
    public void testToBuilder() {
        initializeExpectedImage(12);
        compareImage(diskImage, diskImage.toBuilder().build());
        compareImage(storageImage, storageImage.toBuilder().build());
        Image newImage = build();
        Assert.assertEquals("newDescription", newImage.getDescription());
        newImage = newImage.toBuilder().setDescription("description").build();
        compareImage(diskImage, newImage);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedImage(6);
        ImageInfo imageInfo = ImageInfo.of(ImageTest.IMAGE_ID, ImageTest.DISK_CONFIGURATION);
        Image image = new Image(serviceMockReturnsOptions, new ImageInfo.BuilderImpl(imageInfo));
        compareImage(image, image.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedImage(3);
        Assert.assertEquals(ImageTest.GENERATED_ID, diskImage.getGeneratedId());
        Assert.assertEquals(ImageTest.IMAGE_ID, diskImage.getImageId());
        Assert.assertEquals(ImageTest.CREATION_TIMESTAMP, diskImage.getCreationTimestamp());
        Assert.assertEquals(ImageTest.DESCRIPTION, diskImage.getDescription());
        Assert.assertEquals(ImageTest.DISK_CONFIGURATION, diskImage.getConfiguration());
        Assert.assertEquals(ImageTest.STATUS, diskImage.getStatus());
        Assert.assertEquals(ImageTest.DISK_SIZE_GB, diskImage.getDiskSizeGb());
        Assert.assertEquals(ImageTest.LICENSES, diskImage.getLicenses());
        Assert.assertEquals(ImageTest.DEPRECATION_STATUS, diskImage.getDeprecationStatus());
        Assert.assertSame(serviceMockReturnsOptions, diskImage.getCompute());
        Assert.assertEquals(ImageTest.GENERATED_ID, storageImage.getGeneratedId());
        Assert.assertEquals(ImageTest.IMAGE_ID, storageImage.getImageId());
        Assert.assertEquals(ImageTest.CREATION_TIMESTAMP, storageImage.getCreationTimestamp());
        Assert.assertEquals(ImageTest.DESCRIPTION, storageImage.getDescription());
        Assert.assertEquals(ImageTest.STORAGE_CONFIGURATION, storageImage.getConfiguration());
        Assert.assertEquals(ImageTest.STATUS, storageImage.getStatus());
        Assert.assertEquals(ImageTest.DISK_SIZE_GB, storageImage.getDiskSizeGb());
        Assert.assertEquals(ImageTest.LICENSES, storageImage.getLicenses());
        Assert.assertEquals(ImageTest.DEPRECATION_STATUS, storageImage.getDeprecationStatus());
        Assert.assertSame(serviceMockReturnsOptions, storageImage.getCompute());
        ImageId imageId = ImageId.of("otherImage");
        Image image = build();
        Assert.assertNull(image.getGeneratedId());
        Assert.assertEquals(imageId, image.getImageId());
        Assert.assertNull(image.getCreationTimestamp());
        Assert.assertNull(image.getDescription());
        Assert.assertEquals(ImageTest.DISK_CONFIGURATION, image.getConfiguration());
        Assert.assertNull(image.getStatus());
        Assert.assertNull(image.getDiskSizeGb());
        Assert.assertNull(image.getLicenses());
        Assert.assertNull(image.getDeprecationStatus());
        Assert.assertSame(serviceMockReturnsOptions, image.getCompute());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedImage(12);
        compareImage(diskImage, Image.fromPb(serviceMockReturnsOptions, diskImage.toPb()));
        compareImage(storageImage, Image.fromPb(serviceMockReturnsOptions, storageImage.toPb()));
        Image image = build();
        compareImage(image, Image.fromPb(serviceMockReturnsOptions, image.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedImage(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        expect(compute.deleteImage(ImageTest.IMAGE_ID)).andReturn(operation);
        replay(compute);
        initializeImage();
        Assert.assertSame(operation, image.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedImage(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteImage(ImageTest.IMAGE_ID)).andReturn(null);
        replay(compute);
        initializeImage();
        Assert.assertNull(image.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedImage(2);
        Compute[] expectedOptions = new ImageOption[]{ ImageOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getImage(ImageTest.IMAGE_ID, expectedOptions)).andReturn(diskImage);
        replay(compute);
        initializeImage();
        Assert.assertTrue(image.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedImage(2);
        Compute[] expectedOptions = new ImageOption[]{ ImageOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getImage(ImageTest.IMAGE_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeImage();
        Assert.assertFalse(image.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedImage(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getImage(ImageTest.IMAGE_ID)).andReturn(storageImage);
        replay(compute);
        initializeImage();
        Image updateImage = image.reload();
        compareImage(storageImage, updateImage);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedImage(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getImage(ImageTest.IMAGE_ID)).andReturn(null);
        replay(compute);
        initializeImage();
        Assert.assertNull(image.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedImage(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getImage(ImageTest.IMAGE_ID, ImageOption.fields())).andReturn(storageImage);
        replay(compute);
        initializeImage();
        Image updateImage = image.reload(ImageOption.fields());
        compareImage(storageImage, updateImage);
        verify(compute);
    }

    @Test
    public void testDeprecateImage() {
        initializeExpectedImage(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        DeprecationStatus<ImageId> status = DeprecationStatus.of(DEPRECATED, ImageTest.IMAGE_ID);
        expect(compute.deprecate(ImageTest.IMAGE_ID, status)).andReturn(operation);
        replay(compute);
        initializeImage();
        Assert.assertSame(operation, image.deprecate(status));
    }

    @Test
    public void testDeprecateNull() {
        initializeExpectedImage(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        DeprecationStatus<ImageId> status = DeprecationStatus.of(DEPRECATED, ImageTest.IMAGE_ID);
        expect(compute.deprecate(ImageTest.IMAGE_ID, status)).andReturn(null);
        replay(compute);
        initializeImage();
        Assert.assertNull(image.deprecate(status));
    }
}

