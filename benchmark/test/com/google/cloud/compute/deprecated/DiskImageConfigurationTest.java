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


import ImageConfiguration.SourceType;
import ImageConfiguration.Type.DISK;
import org.junit.Assert;
import org.junit.Test;


public class DiskImageConfigurationTest {
    private static final DiskId SOURCE_DISK = DiskId.of("project", "zone", "disk");

    private static final String SOURCE_DISK_ID = "diskId";

    private static final Long ARCHIVE_SIZE_BYTES = 42L;

    private static final SourceType SOURCE_TYPE = SourceType.RAW;

    private static final DiskImageConfiguration CONFIGURATION = DiskImageConfiguration.newBuilder(DiskImageConfigurationTest.SOURCE_DISK).setSourceDiskId(DiskImageConfigurationTest.SOURCE_DISK_ID).setSourceType(DiskImageConfigurationTest.SOURCE_TYPE).setArchiveSizeBytes(DiskImageConfigurationTest.ARCHIVE_SIZE_BYTES).build();

    @Test
    public void testToBuilder() {
        compareDiskImageConfiguration(DiskImageConfigurationTest.CONFIGURATION, DiskImageConfigurationTest.CONFIGURATION.toBuilder().build());
        DiskId newDisk = DiskId.of("newProject", "newZone", "newDisk");
        String newDiskId = "newDiskId";
        DiskImageConfiguration configuration = DiskImageConfigurationTest.CONFIGURATION.toBuilder().setSourceDisk(newDisk).setSourceDiskId(newDiskId).build();
        Assert.assertEquals(newDisk, configuration.getSourceDisk());
        Assert.assertEquals(newDiskId, configuration.getSourceDiskId());
        configuration = configuration.toBuilder().setSourceDiskId(DiskImageConfigurationTest.SOURCE_DISK_ID).setSourceDisk(DiskImageConfigurationTest.SOURCE_DISK).build();
        compareDiskImageConfiguration(DiskImageConfigurationTest.CONFIGURATION, configuration);
    }

    @Test
    public void testToBuilderIncomplete() {
        DiskImageConfiguration configuration = DiskImageConfiguration.of(DiskImageConfigurationTest.SOURCE_DISK);
        compareDiskImageConfiguration(configuration, configuration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(DiskImageConfigurationTest.SOURCE_TYPE, DiskImageConfigurationTest.CONFIGURATION.getSourceType());
        Assert.assertEquals(DiskImageConfigurationTest.SOURCE_DISK, DiskImageConfigurationTest.CONFIGURATION.getSourceDisk());
        Assert.assertEquals(DiskImageConfigurationTest.SOURCE_DISK_ID, DiskImageConfigurationTest.CONFIGURATION.getSourceDiskId());
        Assert.assertEquals(DiskImageConfigurationTest.ARCHIVE_SIZE_BYTES, DiskImageConfigurationTest.CONFIGURATION.getArchiveSizeBytes());
        Assert.assertEquals(DISK, DiskImageConfigurationTest.CONFIGURATION.getType());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((ImageConfiguration.fromPb(DiskImageConfigurationTest.CONFIGURATION.toPb())) instanceof DiskImageConfiguration));
        compareDiskImageConfiguration(DiskImageConfigurationTest.CONFIGURATION, ImageConfiguration.<DiskImageConfiguration>fromPb(DiskImageConfigurationTest.CONFIGURATION.toPb()));
        DiskImageConfiguration configuration = DiskImageConfiguration.of(DiskImageConfigurationTest.SOURCE_DISK);
        compareDiskImageConfiguration(configuration, DiskImageConfiguration.fromPb(configuration.toPb()));
    }

    @Test
    public void testOf() {
        DiskImageConfiguration configuration = DiskImageConfiguration.of(DiskImageConfigurationTest.SOURCE_DISK);
        Assert.assertEquals(DISK, configuration.getType());
        Assert.assertNull(configuration.getSourceDiskId());
        Assert.assertNull(configuration.getSourceType());
        Assert.assertNull(configuration.getArchiveSizeBytes());
        Assert.assertEquals(DiskImageConfigurationTest.SOURCE_DISK, configuration.getSourceDisk());
    }

    @Test
    public void testSetProjectId() {
        DiskImageConfiguration configuration = DiskImageConfigurationTest.CONFIGURATION.toBuilder().setSourceDisk(DiskId.of("zone", "disk")).build();
        compareDiskImageConfiguration(DiskImageConfigurationTest.CONFIGURATION, configuration.setProjectId("project"));
    }
}

