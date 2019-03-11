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


import Type.IMAGE;
import org.junit.Assert;
import org.junit.Test;


public class ImageDiskConfigurationTest {
    private static final Long SIZE = 42L;

    private static final DiskTypeId DISK_TYPE = DiskTypeId.of("project", "zone", "type");

    private static final ImageId IMAGE = ImageId.of("project", "image");

    private static final String IMAGE_ID = "imageId";

    private static final ImageDiskConfiguration DISK_CONFIGURATION = ImageDiskConfiguration.newBuilder(ImageDiskConfigurationTest.IMAGE).setSizeGb(ImageDiskConfigurationTest.SIZE).setDiskType(ImageDiskConfigurationTest.DISK_TYPE).setSourceImageId(ImageDiskConfigurationTest.IMAGE_ID).build();

    @Test
    public void testToBuilder() {
        compareImageDiskConfiguration(ImageDiskConfigurationTest.DISK_CONFIGURATION, ImageDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().build());
        ImageId newImageId = ImageId.of("newProject", "newImage");
        ImageDiskConfiguration diskConfiguration = ImageDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setSizeGb(24L).setSourceImage(newImageId).setSourceImageId("newImageId").build();
        Assert.assertEquals(24L, diskConfiguration.getSizeGb().longValue());
        Assert.assertEquals(newImageId, diskConfiguration.getSourceImage());
        Assert.assertEquals("newImageId", diskConfiguration.getSourceImageId());
        diskConfiguration = diskConfiguration.toBuilder().setSizeGb(ImageDiskConfigurationTest.SIZE).setSourceImage(ImageDiskConfigurationTest.IMAGE).setSourceImageId(ImageDiskConfigurationTest.IMAGE_ID).build();
        compareImageDiskConfiguration(ImageDiskConfigurationTest.DISK_CONFIGURATION, diskConfiguration);
    }

    @Test
    public void testToBuilderIncomplete() {
        ImageDiskConfiguration diskConfiguration = ImageDiskConfiguration.of(ImageDiskConfigurationTest.IMAGE);
        compareImageDiskConfiguration(diskConfiguration, diskConfiguration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ImageDiskConfigurationTest.DISK_TYPE, ImageDiskConfigurationTest.DISK_CONFIGURATION.getDiskType());
        Assert.assertEquals(ImageDiskConfigurationTest.SIZE, ImageDiskConfigurationTest.DISK_CONFIGURATION.getSizeGb());
        Assert.assertEquals(ImageDiskConfigurationTest.IMAGE, ImageDiskConfigurationTest.DISK_CONFIGURATION.getSourceImage());
        Assert.assertEquals(ImageDiskConfigurationTest.IMAGE_ID, ImageDiskConfigurationTest.DISK_CONFIGURATION.getSourceImageId());
        Assert.assertEquals(Type.IMAGE, ImageDiskConfigurationTest.DISK_CONFIGURATION.getType());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((DiskConfiguration.fromPb(ImageDiskConfigurationTest.DISK_CONFIGURATION.toPb())) instanceof ImageDiskConfiguration));
        compareImageDiskConfiguration(ImageDiskConfigurationTest.DISK_CONFIGURATION, DiskConfiguration.<ImageDiskConfiguration>fromPb(ImageDiskConfigurationTest.DISK_CONFIGURATION.toPb()));
    }

    @Test
    public void testOf() {
        ImageDiskConfiguration configuration = ImageDiskConfiguration.of(ImageDiskConfigurationTest.IMAGE);
        Assert.assertNull(configuration.getDiskType());
        Assert.assertNull(configuration.getSizeGb());
        Assert.assertNull(configuration.getSourceImageId());
        Assert.assertEquals(ImageDiskConfigurationTest.IMAGE, configuration.getSourceImage());
        Assert.assertEquals(Type.IMAGE, configuration.getType());
    }

    @Test
    public void testSetProjectId() {
        ImageDiskConfiguration diskConfiguration = ImageDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(ImageDiskConfigurationTest.DISK_TYPE.getZone(), ImageDiskConfigurationTest.DISK_TYPE.getType())).setSourceImage(ImageId.of(ImageDiskConfigurationTest.IMAGE.getImage())).build();
        compareImageDiskConfiguration(ImageDiskConfigurationTest.DISK_CONFIGURATION, diskConfiguration.setProjectId("project"));
    }
}

