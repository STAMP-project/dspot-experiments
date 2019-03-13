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


import DeprecationStatus.Status.DELETED;
import ImageInfo.Status;
import StorageImageConfiguration.ContainerType.TAR;
import com.google.cloud.compute.deprecated.ImageConfiguration.SourceType;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ImageInfoTest {
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

    private static final StorageImageConfiguration STORAGE_CONFIGURATION = StorageImageConfiguration.newBuilder(ImageInfoTest.STORAGE_SOURCE).setArchiveSizeBytes(ImageInfoTest.ARCHIVE_SIZE_BYTES).setContainerType(TAR).setSha1(ImageInfoTest.SHA1_CHECKSUM).setSourceType(ImageInfoTest.SOURCE_TYPE).build();

    private static final DiskImageConfiguration DISK_CONFIGURATION = DiskImageConfiguration.newBuilder(ImageInfoTest.SOURCE_DISK).setArchiveSizeBytes(ImageInfoTest.ARCHIVE_SIZE_BYTES).setSourceDiskId(ImageInfoTest.SOURCE_DISK_ID).setSourceType(ImageInfoTest.SOURCE_TYPE).build();

    private static final DeprecationStatus<ImageId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, ImageInfoTest.IMAGE_ID);

    private static final ImageInfo STORAGE_IMAGE = ImageInfo.newBuilder(ImageInfoTest.IMAGE_ID, ImageInfoTest.STORAGE_CONFIGURATION).setGeneratedId(ImageInfoTest.GENERATED_ID).getCreationTimestamp(ImageInfoTest.CREATION_TIMESTAMP).setDescription(ImageInfoTest.DESCRIPTION).setStatus(ImageInfoTest.STATUS).setDiskSizeGb(ImageInfoTest.DISK_SIZE_GB).setLicenses(ImageInfoTest.LICENSES).setDeprecationStatus(ImageInfoTest.DEPRECATION_STATUS).build();

    private static final ImageInfo DISK_IMAGE = ImageInfo.newBuilder(ImageInfoTest.IMAGE_ID, ImageInfoTest.DISK_CONFIGURATION).setGeneratedId(ImageInfoTest.GENERATED_ID).getCreationTimestamp(ImageInfoTest.CREATION_TIMESTAMP).setDescription(ImageInfoTest.DESCRIPTION).setStatus(ImageInfoTest.STATUS).setDiskSizeGb(ImageInfoTest.DISK_SIZE_GB).setLicenses(ImageInfoTest.LICENSES).setDeprecationStatus(ImageInfoTest.DEPRECATION_STATUS).build();

    @Test
    public void testToBuilder() {
        compareImageInfo(ImageInfoTest.STORAGE_IMAGE, ImageInfoTest.STORAGE_IMAGE.toBuilder().build());
        compareImageInfo(ImageInfoTest.DISK_IMAGE, ImageInfoTest.DISK_IMAGE.toBuilder().build());
        ImageInfo imageInfo = ImageInfoTest.STORAGE_IMAGE.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", imageInfo.getDescription());
        imageInfo = imageInfo.toBuilder().setDescription("description").build();
        compareImageInfo(ImageInfoTest.STORAGE_IMAGE, imageInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        ImageInfo imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, ImageInfoTest.STORAGE_CONFIGURATION);
        Assert.assertEquals(imageInfo, imageInfo.toBuilder().build());
        imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, ImageInfoTest.DISK_CONFIGURATION);
        Assert.assertEquals(imageInfo, imageInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ImageInfoTest.GENERATED_ID, ImageInfoTest.STORAGE_IMAGE.getGeneratedId());
        Assert.assertEquals(ImageInfoTest.IMAGE_ID, ImageInfoTest.STORAGE_IMAGE.getImageId());
        Assert.assertEquals(ImageInfoTest.CREATION_TIMESTAMP, ImageInfoTest.STORAGE_IMAGE.getCreationTimestamp());
        Assert.assertEquals(ImageInfoTest.DESCRIPTION, ImageInfoTest.STORAGE_IMAGE.getDescription());
        Assert.assertEquals(ImageInfoTest.STORAGE_CONFIGURATION, ImageInfoTest.STORAGE_IMAGE.getConfiguration());
        Assert.assertEquals(ImageInfoTest.STATUS, ImageInfoTest.STORAGE_IMAGE.getStatus());
        Assert.assertEquals(ImageInfoTest.DISK_SIZE_GB, ImageInfoTest.STORAGE_IMAGE.getDiskSizeGb());
        Assert.assertEquals(ImageInfoTest.LICENSES, ImageInfoTest.STORAGE_IMAGE.getLicenses());
        Assert.assertEquals(ImageInfoTest.DEPRECATION_STATUS, ImageInfoTest.STORAGE_IMAGE.getDeprecationStatus());
        Assert.assertEquals(ImageInfoTest.GENERATED_ID, ImageInfoTest.DISK_IMAGE.getGeneratedId());
        Assert.assertEquals(ImageInfoTest.IMAGE_ID, ImageInfoTest.DISK_IMAGE.getImageId());
        Assert.assertEquals(ImageInfoTest.CREATION_TIMESTAMP, ImageInfoTest.DISK_IMAGE.getCreationTimestamp());
        Assert.assertEquals(ImageInfoTest.DESCRIPTION, ImageInfoTest.DISK_IMAGE.getDescription());
        Assert.assertEquals(ImageInfoTest.DISK_CONFIGURATION, ImageInfoTest.DISK_IMAGE.getConfiguration());
        Assert.assertEquals(ImageInfoTest.STATUS, ImageInfoTest.DISK_IMAGE.getStatus());
        Assert.assertEquals(ImageInfoTest.DISK_SIZE_GB, ImageInfoTest.DISK_IMAGE.getDiskSizeGb());
        Assert.assertEquals(ImageInfoTest.LICENSES, ImageInfoTest.DISK_IMAGE.getLicenses());
        Assert.assertEquals(ImageInfoTest.DEPRECATION_STATUS, ImageInfoTest.DISK_IMAGE.getDeprecationStatus());
    }

    @Test
    public void testOf() {
        ImageInfo imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, ImageInfoTest.STORAGE_CONFIGURATION);
        Assert.assertEquals(ImageInfoTest.IMAGE_ID, imageInfo.getImageId());
        Assert.assertEquals(ImageInfoTest.STORAGE_CONFIGURATION, imageInfo.getConfiguration());
        Assert.assertNull(imageInfo.getGeneratedId());
        Assert.assertNull(imageInfo.getCreationTimestamp());
        Assert.assertNull(imageInfo.getDescription());
        Assert.assertNull(imageInfo.getStatus());
        Assert.assertNull(imageInfo.getDiskSizeGb());
        Assert.assertNull(imageInfo.getLicenses());
        Assert.assertNull(imageInfo.getDeprecationStatus());
        imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, ImageInfoTest.DISK_CONFIGURATION);
        Assert.assertEquals(ImageInfoTest.IMAGE_ID, imageInfo.getImageId());
        Assert.assertEquals(ImageInfoTest.DISK_CONFIGURATION, imageInfo.getConfiguration());
        Assert.assertNull(imageInfo.getGeneratedId());
        Assert.assertNull(imageInfo.getCreationTimestamp());
        Assert.assertNull(imageInfo.getDescription());
        Assert.assertNull(imageInfo.getStatus());
        Assert.assertNull(imageInfo.getDiskSizeGb());
        Assert.assertNull(imageInfo.getLicenses());
        Assert.assertNull(imageInfo.getDeprecationStatus());
    }

    @Test
    public void testToAndFromPb() {
        compareImageInfo(ImageInfoTest.STORAGE_IMAGE, ImageInfo.fromPb(ImageInfoTest.STORAGE_IMAGE.toPb()));
        compareImageInfo(ImageInfoTest.DISK_IMAGE, ImageInfo.fromPb(ImageInfoTest.DISK_IMAGE.toPb()));
        ImageInfo imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, StorageImageConfiguration.of(ImageInfoTest.STORAGE_SOURCE));
        compareImageInfo(imageInfo, ImageInfo.fromPb(imageInfo.toPb()));
        imageInfo = ImageInfo.of(ImageInfoTest.IMAGE_ID, DiskImageConfiguration.of(ImageInfoTest.SOURCE_DISK));
        compareImageInfo(imageInfo, ImageInfo.fromPb(imageInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        ImageInfo imageInfo = ImageInfoTest.DISK_IMAGE.toBuilder().setImageId(ImageId.of("image")).setConfiguration(ImageInfoTest.DISK_CONFIGURATION.toBuilder().setSourceDisk(DiskId.of("zone", "disk")).build()).build();
        compareImageInfo(ImageInfoTest.DISK_IMAGE, imageInfo.setProjectId("project"));
    }
}

