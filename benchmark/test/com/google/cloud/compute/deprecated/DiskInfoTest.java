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


import DiskInfo.CreationStatus;
import com.google.api.services.compute.model.Disk;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DiskInfoTest {
    private static final String GENERATED_ID = "42";

    private static final DiskId DISK_ID = DiskId.of("project", "zone", "disk");

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final CreationStatus CREATION_STATUS = CreationStatus.READY;

    private static final String DESCRIPTION = "description";

    private static final Long SIZE_GB = 500L;

    private static final DiskTypeId TYPE = DiskTypeId.of("project", "zone", "disk");

    private static final List<LicenseId> LICENSES = ImmutableList.of(LicenseId.of("project", "license1"), LicenseId.of("project", "license2"));

    private static final List<InstanceId> ATTACHED_INSTANCES = ImmutableList.of(InstanceId.of("project", "zone", "instance1"), InstanceId.of("project", "zone", "instance2"));

    private static final SnapshotId SNAPSHOT = SnapshotId.of("project", "snapshot");

    private static final ImageId IMAGE = ImageId.of("project", "image");

    private static final String SNAPSHOT_ID = "snapshotId";

    private static final String IMAGE_ID = "snapshotId";

    private static final Long LAST_ATTACH_TIMESTAMP = 1453293600000L;

    private static final Long LAST_DETACH_TIMESTAMP = 1453293660000L;

    private static final StandardDiskConfiguration DISK_CONFIGURATION = StandardDiskConfiguration.newBuilder().setSizeGb(DiskInfoTest.SIZE_GB).setDiskType(DiskInfoTest.TYPE).build();

    private static final SnapshotDiskConfiguration SNAPSHOT_DISK_CONFIGURATION = SnapshotDiskConfiguration.newBuilder(DiskInfoTest.SNAPSHOT).setSizeGb(DiskInfoTest.SIZE_GB).setDiskType(DiskInfoTest.TYPE).setSourceSnapshotId(DiskInfoTest.SNAPSHOT_ID).build();

    private static final ImageDiskConfiguration IMAGE_DISK_CONFIGURATION = ImageDiskConfiguration.newBuilder(DiskInfoTest.IMAGE).setSizeGb(DiskInfoTest.SIZE_GB).setDiskType(DiskInfoTest.TYPE).setSourceImageId(DiskInfoTest.IMAGE_ID).build();

    private static final DiskInfo DISK_INFO = DiskInfo.newBuilder(DiskInfoTest.DISK_ID, DiskInfoTest.DISK_CONFIGURATION).setGeneratedId(DiskInfoTest.GENERATED_ID).setCreationTimestamp(DiskInfoTest.CREATION_TIMESTAMP).setCreationStatus(DiskInfoTest.CREATION_STATUS).setDescription(DiskInfoTest.DESCRIPTION).setLicenses(DiskInfoTest.LICENSES).setAttachedInstances(DiskInfoTest.ATTACHED_INSTANCES).setLastAttachTimestamp(DiskInfoTest.LAST_ATTACH_TIMESTAMP).setLastDetachTimestamp(DiskInfoTest.LAST_DETACH_TIMESTAMP).build();

    private static final DiskInfo SNAPSHOT_DISK_INFO = DiskInfo.newBuilder(DiskInfoTest.DISK_ID, DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION).setGeneratedId(DiskInfoTest.GENERATED_ID).setCreationTimestamp(DiskInfoTest.CREATION_TIMESTAMP).setCreationStatus(DiskInfoTest.CREATION_STATUS).setDescription(DiskInfoTest.DESCRIPTION).setLicenses(DiskInfoTest.LICENSES).setAttachedInstances(DiskInfoTest.ATTACHED_INSTANCES).setLastAttachTimestamp(DiskInfoTest.LAST_ATTACH_TIMESTAMP).setLastDetachTimestamp(DiskInfoTest.LAST_DETACH_TIMESTAMP).build();

    private static final DiskInfo IMAGE_DISK_INFO = DiskInfo.newBuilder(DiskInfoTest.DISK_ID, DiskInfoTest.IMAGE_DISK_CONFIGURATION).setGeneratedId(DiskInfoTest.GENERATED_ID).setCreationTimestamp(DiskInfoTest.CREATION_TIMESTAMP).setCreationStatus(DiskInfoTest.CREATION_STATUS).setDescription(DiskInfoTest.DESCRIPTION).setLicenses(DiskInfoTest.LICENSES).setAttachedInstances(DiskInfoTest.ATTACHED_INSTANCES).setLastAttachTimestamp(DiskInfoTest.LAST_ATTACH_TIMESTAMP).setLastDetachTimestamp(DiskInfoTest.LAST_DETACH_TIMESTAMP).build();

    @Test
    public void testToBuilder() {
        compareDiskInfo(DiskInfoTest.DISK_INFO, DiskInfoTest.DISK_INFO.toBuilder().build());
        compareDiskInfo(DiskInfoTest.IMAGE_DISK_INFO, DiskInfoTest.IMAGE_DISK_INFO.toBuilder().build());
        compareDiskInfo(DiskInfoTest.SNAPSHOT_DISK_INFO, DiskInfoTest.SNAPSHOT_DISK_INFO.toBuilder().build());
        DiskInfo diskInfo = DiskInfoTest.DISK_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", diskInfo.getDescription());
        diskInfo = diskInfo.toBuilder().setDescription("description").build();
        compareDiskInfo(DiskInfoTest.DISK_INFO, diskInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        DiskInfo diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.DISK_CONFIGURATION);
        Assert.assertEquals(diskInfo, diskInfo.toBuilder().build());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION);
        Assert.assertEquals(diskInfo, diskInfo.toBuilder().build());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.IMAGE_DISK_CONFIGURATION);
        Assert.assertEquals(diskInfo, diskInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(DiskInfoTest.GENERATED_ID, DiskInfoTest.DISK_INFO.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, DiskInfoTest.DISK_INFO.getDiskId());
        Assert.assertEquals(DiskInfoTest.DISK_CONFIGURATION, DiskInfoTest.DISK_INFO.getConfiguration());
        Assert.assertEquals(DiskInfoTest.CREATION_TIMESTAMP, DiskInfoTest.DISK_INFO.getCreationTimestamp());
        Assert.assertEquals(DiskInfoTest.CREATION_STATUS, DiskInfoTest.DISK_INFO.getCreationStatus());
        Assert.assertEquals(DiskInfoTest.DESCRIPTION, DiskInfoTest.DISK_INFO.getDescription());
        Assert.assertEquals(DiskInfoTest.LICENSES, DiskInfoTest.DISK_INFO.getLicenses());
        Assert.assertEquals(DiskInfoTest.ATTACHED_INSTANCES, DiskInfoTest.DISK_INFO.getAttachedInstances());
        Assert.assertEquals(DiskInfoTest.LAST_ATTACH_TIMESTAMP, DiskInfoTest.DISK_INFO.getLastAttachTimestamp());
        Assert.assertEquals(DiskInfoTest.LAST_DETACH_TIMESTAMP, DiskInfoTest.DISK_INFO.getLastDetachTimestamp());
        Assert.assertEquals(DiskInfoTest.GENERATED_ID, DiskInfoTest.IMAGE_DISK_INFO.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, DiskInfoTest.IMAGE_DISK_INFO.getDiskId());
        Assert.assertEquals(DiskInfoTest.IMAGE_DISK_CONFIGURATION, DiskInfoTest.IMAGE_DISK_INFO.getConfiguration());
        Assert.assertEquals(DiskInfoTest.CREATION_TIMESTAMP, DiskInfoTest.IMAGE_DISK_INFO.getCreationTimestamp());
        Assert.assertEquals(DiskInfoTest.CREATION_STATUS, DiskInfoTest.IMAGE_DISK_INFO.getCreationStatus());
        Assert.assertEquals(DiskInfoTest.DESCRIPTION, DiskInfoTest.IMAGE_DISK_INFO.getDescription());
        Assert.assertEquals(DiskInfoTest.LICENSES, DiskInfoTest.IMAGE_DISK_INFO.getLicenses());
        Assert.assertEquals(DiskInfoTest.ATTACHED_INSTANCES, DiskInfoTest.IMAGE_DISK_INFO.getAttachedInstances());
        Assert.assertEquals(DiskInfoTest.LAST_ATTACH_TIMESTAMP, DiskInfoTest.IMAGE_DISK_INFO.getLastAttachTimestamp());
        Assert.assertEquals(DiskInfoTest.LAST_DETACH_TIMESTAMP, DiskInfoTest.IMAGE_DISK_INFO.getLastDetachTimestamp());
        Assert.assertEquals(DiskInfoTest.GENERATED_ID, DiskInfoTest.SNAPSHOT_DISK_INFO.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, DiskInfoTest.SNAPSHOT_DISK_INFO.getDiskId());
        Assert.assertEquals(DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION, DiskInfoTest.SNAPSHOT_DISK_INFO.getConfiguration());
        Assert.assertEquals(DiskInfoTest.CREATION_TIMESTAMP, DiskInfoTest.SNAPSHOT_DISK_INFO.getCreationTimestamp());
        Assert.assertEquals(DiskInfoTest.CREATION_STATUS, DiskInfoTest.SNAPSHOT_DISK_INFO.getCreationStatus());
        Assert.assertEquals(DiskInfoTest.DESCRIPTION, DiskInfoTest.SNAPSHOT_DISK_INFO.getDescription());
        Assert.assertEquals(DiskInfoTest.LICENSES, DiskInfoTest.SNAPSHOT_DISK_INFO.getLicenses());
        Assert.assertEquals(DiskInfoTest.ATTACHED_INSTANCES, DiskInfoTest.SNAPSHOT_DISK_INFO.getAttachedInstances());
        Assert.assertEquals(DiskInfoTest.LAST_ATTACH_TIMESTAMP, DiskInfoTest.SNAPSHOT_DISK_INFO.getLastAttachTimestamp());
        Assert.assertEquals(DiskInfoTest.LAST_DETACH_TIMESTAMP, DiskInfoTest.SNAPSHOT_DISK_INFO.getLastDetachTimestamp());
    }

    @Test
    public void testOf() {
        DiskInfo diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.DISK_CONFIGURATION);
        Assert.assertNull(diskInfo.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, diskInfo.getDiskId());
        Assert.assertEquals(DiskInfoTest.DISK_CONFIGURATION, diskInfo.getConfiguration());
        Assert.assertNull(diskInfo.getCreationTimestamp());
        Assert.assertNull(diskInfo.getCreationStatus());
        Assert.assertNull(diskInfo.getDescription());
        Assert.assertNull(diskInfo.getLicenses());
        Assert.assertNull(diskInfo.getAttachedInstances());
        Assert.assertNull(diskInfo.getLastAttachTimestamp());
        Assert.assertNull(diskInfo.getLastDetachTimestamp());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.IMAGE_DISK_CONFIGURATION);
        Assert.assertNull(diskInfo.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, diskInfo.getDiskId());
        Assert.assertEquals(DiskInfoTest.IMAGE_DISK_CONFIGURATION, diskInfo.getConfiguration());
        Assert.assertNull(diskInfo.getCreationTimestamp());
        Assert.assertNull(diskInfo.getCreationStatus());
        Assert.assertNull(diskInfo.getDescription());
        Assert.assertNull(diskInfo.getLicenses());
        Assert.assertNull(diskInfo.getAttachedInstances());
        Assert.assertNull(diskInfo.getLastAttachTimestamp());
        Assert.assertNull(diskInfo.getLastDetachTimestamp());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION);
        Assert.assertNull(diskInfo.getGeneratedId());
        Assert.assertEquals(DiskInfoTest.DISK_ID, diskInfo.getDiskId());
        Assert.assertEquals(DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION, diskInfo.getConfiguration());
        Assert.assertNull(diskInfo.getCreationTimestamp());
        Assert.assertNull(diskInfo.getCreationStatus());
        Assert.assertNull(diskInfo.getDescription());
        Assert.assertNull(diskInfo.getLicenses());
        Assert.assertNull(diskInfo.getAttachedInstances());
        Assert.assertNull(diskInfo.getLastAttachTimestamp());
        Assert.assertNull(diskInfo.getLastDetachTimestamp());
    }

    @Test
    public void testToAndFromPb() {
        DiskInfo diskInfo = DiskInfo.fromPb(DiskInfoTest.DISK_INFO.toPb());
        compareDiskInfo(DiskInfoTest.DISK_INFO, diskInfo);
        diskInfo = DiskInfo.fromPb(DiskInfoTest.SNAPSHOT_DISK_INFO.toPb());
        compareDiskInfo(DiskInfoTest.SNAPSHOT_DISK_INFO, diskInfo);
        diskInfo = DiskInfo.fromPb(DiskInfoTest.IMAGE_DISK_INFO.toPb());
        compareDiskInfo(DiskInfoTest.IMAGE_DISK_INFO, diskInfo);
        Disk disk = new Disk().setSelfLink(DiskInfoTest.DISK_ID.getSelfLink()).setType(DiskInfoTest.TYPE.getSelfLink()).setSizeGb(DiskInfoTest.SIZE_GB);
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.DISK_CONFIGURATION);
        compareDiskInfo(diskInfo, DiskInfo.fromPb(disk));
        disk = new Disk().setType(DiskInfoTest.TYPE.getSelfLink()).setSizeGb(DiskInfoTest.SIZE_GB).setSelfLink(DiskInfoTest.DISK_ID.getSelfLink()).setSourceSnapshotId(DiskInfoTest.SNAPSHOT_ID).setSourceSnapshot(DiskInfoTest.SNAPSHOT.getSelfLink());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION);
        compareDiskInfo(diskInfo, DiskInfo.fromPb(disk));
        disk = new Disk().setType(DiskInfoTest.TYPE.getSelfLink()).setSizeGb(DiskInfoTest.SIZE_GB).setSelfLink(DiskInfoTest.DISK_ID.getSelfLink()).setSourceImageId(DiskInfoTest.IMAGE_ID).setSourceImage(DiskInfoTest.IMAGE.getSelfLink());
        diskInfo = DiskInfo.of(DiskInfoTest.DISK_ID, DiskInfoTest.IMAGE_DISK_CONFIGURATION);
        compareDiskInfo(diskInfo, DiskInfo.fromPb(disk));
    }

    @Test
    public void testSetProjectId() {
        StandardDiskConfiguration standardDiskConfiguration = DiskInfoTest.DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(DiskInfoTest.TYPE.getZone(), DiskInfoTest.TYPE.getType())).build();
        DiskInfo diskInfo = DiskInfoTest.DISK_INFO.toBuilder().setDiskId(DiskId.of(DiskInfoTest.DISK_ID.getZone(), DiskInfoTest.DISK_ID.getDisk())).setConfiguration(standardDiskConfiguration).build();
        compareDiskInfo(DiskInfoTest.DISK_INFO, diskInfo.setProjectId("project"));
        SnapshotDiskConfiguration snapshotDiskConfiguration = DiskInfoTest.SNAPSHOT_DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(DiskInfoTest.TYPE.getZone(), DiskInfoTest.TYPE.getType())).setSourceSnapshot(SnapshotId.of(DiskInfoTest.SNAPSHOT.getSnapshot())).build();
        diskInfo = DiskInfoTest.SNAPSHOT_DISK_INFO.toBuilder().setDiskId(DiskId.of(DiskInfoTest.DISK_ID.getZone(), DiskInfoTest.DISK_ID.getDisk())).setConfiguration(snapshotDiskConfiguration).build();
        compareDiskInfo(DiskInfoTest.SNAPSHOT_DISK_INFO, diskInfo.setProjectId("project"));
        ImageDiskConfiguration imageDiskConfiguration = DiskInfoTest.IMAGE_DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(DiskInfoTest.TYPE.getZone(), DiskInfoTest.TYPE.getType())).setSourceImage(ImageId.of(DiskInfoTest.IMAGE.getImage())).build();
        diskInfo = DiskInfoTest.IMAGE_DISK_INFO.toBuilder().setDiskId(DiskId.of(DiskInfoTest.DISK_ID.getZone(), DiskInfoTest.DISK_ID.getDisk())).setConfiguration(imageDiskConfiguration).build();
        compareDiskInfo(DiskInfoTest.IMAGE_DISK_INFO, diskInfo.setProjectId("project"));
    }
}

