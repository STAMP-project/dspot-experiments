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


import com.google.cloud.compute.deprecated.SnapshotInfo.Status;
import com.google.cloud.compute.deprecated.SnapshotInfo.StorageBytesStatus;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SnapshotInfoTest {
    private static final String GENERATED_ID = "42";

    private static final DiskId SOURCE_DISK = DiskId.of("project", "zone", "disk");

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final List<LicenseId> LICENSES = ImmutableList.of(LicenseId.of("project", "license1"), LicenseId.of("project", "license2"));

    private static final SnapshotId SNAPSHOT_ID = SnapshotId.of("project", "snapshot");

    private static final Status STATUS = Status.CREATING;

    private static final Long DISK_SIZE_GB = 42L;

    private static final String SOURCE_DISK_ID = "diskId";

    private static final Long STORAGE_BYTES = 24L;

    private static final StorageBytesStatus STORAGE_BYTES_STATUS = StorageBytesStatus.UP_TO_DATE;

    private static final SnapshotInfo SNAPSHOT_INFO = SnapshotInfo.newBuilder(SnapshotInfoTest.SNAPSHOT_ID, SnapshotInfoTest.SOURCE_DISK).setGeneratedId(SnapshotInfoTest.GENERATED_ID).setCreationTimestamp(SnapshotInfoTest.CREATION_TIMESTAMP).setDescription(SnapshotInfoTest.DESCRIPTION).setStatus(SnapshotInfoTest.STATUS).setDiskSizeGb(SnapshotInfoTest.DISK_SIZE_GB).setLicenses(SnapshotInfoTest.LICENSES).setSourceDiskId(SnapshotInfoTest.SOURCE_DISK_ID).setStorageBytes(SnapshotInfoTest.STORAGE_BYTES).setStorageBytesStatus(SnapshotInfoTest.STORAGE_BYTES_STATUS).build();

    @Test
    public void testToBuilder() {
        compareSnapshotInfo(SnapshotInfoTest.SNAPSHOT_INFO, SnapshotInfoTest.SNAPSHOT_INFO.toBuilder().build());
        SnapshotInfo snapshotInfo = SnapshotInfoTest.SNAPSHOT_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", snapshotInfo.getDescription());
        snapshotInfo = snapshotInfo.toBuilder().setDescription("description").build();
        compareSnapshotInfo(SnapshotInfoTest.SNAPSHOT_INFO, snapshotInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        SnapshotInfo snapshotInfo = SnapshotInfo.of(SnapshotInfoTest.SNAPSHOT_ID, SnapshotInfoTest.SOURCE_DISK);
        Assert.assertEquals(snapshotInfo, snapshotInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(SnapshotInfoTest.GENERATED_ID, SnapshotInfoTest.SNAPSHOT_INFO.getGeneratedId());
        Assert.assertEquals(SnapshotInfoTest.SNAPSHOT_ID, SnapshotInfoTest.SNAPSHOT_INFO.getSnapshotId());
        Assert.assertEquals(SnapshotInfoTest.CREATION_TIMESTAMP, SnapshotInfoTest.SNAPSHOT_INFO.getCreationTimestamp());
        Assert.assertEquals(SnapshotInfoTest.DESCRIPTION, SnapshotInfoTest.SNAPSHOT_INFO.getDescription());
        Assert.assertEquals(SnapshotInfoTest.STATUS, SnapshotInfoTest.SNAPSHOT_INFO.getStatus());
        Assert.assertEquals(SnapshotInfoTest.DISK_SIZE_GB, SnapshotInfoTest.SNAPSHOT_INFO.getDiskSizeGb());
        Assert.assertEquals(SnapshotInfoTest.LICENSES, SnapshotInfoTest.SNAPSHOT_INFO.getLicenses());
        Assert.assertEquals(SnapshotInfoTest.SOURCE_DISK, SnapshotInfoTest.SNAPSHOT_INFO.getSourceDisk());
        Assert.assertEquals(SnapshotInfoTest.SOURCE_DISK_ID, SnapshotInfoTest.SNAPSHOT_INFO.getSourceDiskId());
        Assert.assertEquals(SnapshotInfoTest.STORAGE_BYTES, SnapshotInfoTest.SNAPSHOT_INFO.getStorageBytes());
        Assert.assertEquals(SnapshotInfoTest.STORAGE_BYTES_STATUS, SnapshotInfoTest.SNAPSHOT_INFO.getStorageBytesStatus());
    }

    @Test
    public void testOf() {
        SnapshotInfo snapshotInfo = SnapshotInfo.of(SnapshotInfoTest.SNAPSHOT_ID, SnapshotInfoTest.SOURCE_DISK);
        Assert.assertNull(snapshotInfo.getGeneratedId());
        Assert.assertEquals(SnapshotInfoTest.SNAPSHOT_ID, snapshotInfo.getSnapshotId());
        Assert.assertNull(snapshotInfo.getCreationTimestamp());
        Assert.assertNull(snapshotInfo.getDescription());
        Assert.assertNull(snapshotInfo.getStatus());
        Assert.assertNull(snapshotInfo.getDiskSizeGb());
        Assert.assertNull(snapshotInfo.getLicenses());
        Assert.assertEquals(SnapshotInfoTest.SOURCE_DISK, snapshotInfo.getSourceDisk());
        Assert.assertNull(snapshotInfo.getSourceDiskId());
        Assert.assertNull(snapshotInfo.getStorageBytes());
        Assert.assertNull(snapshotInfo.getStorageBytesStatus());
    }

    @Test
    public void testToAndFromPb() {
        compareSnapshotInfo(SnapshotInfoTest.SNAPSHOT_INFO, SnapshotInfo.fromPb(SnapshotInfoTest.SNAPSHOT_INFO.toPb()));
        SnapshotInfo snapshotInfo = SnapshotInfo.of(SnapshotInfoTest.SNAPSHOT_ID, SnapshotInfoTest.SOURCE_DISK);
        compareSnapshotInfo(snapshotInfo, SnapshotInfo.fromPb(snapshotInfo.toPb()));
        snapshotInfo = new SnapshotInfo.BuilderImpl().setSnapshotId(SnapshotInfoTest.SNAPSHOT_ID).build();
        compareSnapshotInfo(snapshotInfo, SnapshotInfo.fromPb(snapshotInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        SnapshotInfo snapshotInfo = SnapshotInfoTest.SNAPSHOT_INFO.toBuilder().setSnapshotId(SnapshotId.of("snapshot")).setSourceDisk(DiskId.of("zone", "disk")).build();
        compareSnapshotInfo(SnapshotInfoTest.SNAPSHOT_INFO, snapshotInfo.setProjectId("project"));
    }
}

