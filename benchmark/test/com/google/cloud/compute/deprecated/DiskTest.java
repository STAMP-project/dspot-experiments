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


import Compute.DiskOption;
import Compute.OperationOption;
import DiskInfo.CreationStatus;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DiskTest {
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

    private static final String IMAGE_ID = "imageId";

    private static final Long LAST_ATTACH_TIMESTAMP = 1453293600000L;

    private static final Long LAST_DETACH_TIMESTAMP = 1453293660000L;

    private static final StandardDiskConfiguration DISK_CONFIGURATION = StandardDiskConfiguration.newBuilder().setSizeGb(DiskTest.SIZE_GB).setDiskType(DiskTest.TYPE).build();

    private static final SnapshotDiskConfiguration SNAPSHOT_DISK_CONFIGURATION = SnapshotDiskConfiguration.newBuilder(DiskTest.SNAPSHOT).setSizeGb(DiskTest.SIZE_GB).setDiskType(DiskTest.TYPE).setSourceSnapshotId(DiskTest.SNAPSHOT_ID).build();

    private static final ImageDiskConfiguration IMAGE_DISK_CONFIGURATION = ImageDiskConfiguration.newBuilder(DiskTest.IMAGE).setSizeGb(DiskTest.SIZE_GB).setDiskType(DiskTest.TYPE).setSourceImageId(DiskTest.IMAGE_ID).build();

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Disk disk;

    private Disk standardDisk;

    private Disk snapshotDisk;

    private Disk imageDisk;

    @Test
    public void testToBuilder() {
        initializeExpectedDisk(16);
        compareDisk(standardDisk, standardDisk.toBuilder().build());
        compareDisk(imageDisk, imageDisk.toBuilder().build());
        compareDisk(snapshotDisk, snapshotDisk.toBuilder().build());
        Disk newDisk = build();
        Assert.assertEquals("newDescription", newDisk.getDescription());
        newDisk = newDisk.toBuilder().setDescription("description").build();
        compareDisk(standardDisk, newDisk);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedDisk(18);
        DiskInfo diskInfo = DiskInfo.of(DiskTest.DISK_ID, DiskTest.DISK_CONFIGURATION);
        Disk disk = new Disk(serviceMockReturnsOptions, new DiskInfo.BuilderImpl(diskInfo));
        compareDisk(disk, disk.toBuilder().build());
        diskInfo = DiskInfo.of(DiskTest.DISK_ID, DiskTest.SNAPSHOT_DISK_CONFIGURATION);
        disk = new Disk(serviceMockReturnsOptions, new DiskInfo.BuilderImpl(diskInfo));
        compareDisk(disk, disk.toBuilder().build());
        diskInfo = DiskInfo.of(DiskTest.DISK_ID, DiskTest.IMAGE_DISK_CONFIGURATION);
        disk = new Disk(serviceMockReturnsOptions, new DiskInfo.BuilderImpl(diskInfo));
        compareDisk(disk, disk.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedDisk(4);
        Assert.assertEquals(DiskTest.DISK_ID, standardDisk.getDiskId());
        Assert.assertEquals(DiskTest.GENERATED_ID, standardDisk.getGeneratedId());
        Assert.assertEquals(DiskTest.DISK_CONFIGURATION, standardDisk.getConfiguration());
        Assert.assertEquals(DiskTest.CREATION_TIMESTAMP, standardDisk.getCreationTimestamp());
        Assert.assertEquals(DiskTest.CREATION_STATUS, standardDisk.getCreationStatus());
        Assert.assertEquals(DiskTest.DESCRIPTION, standardDisk.getDescription());
        Assert.assertEquals(DiskTest.LICENSES, standardDisk.getLicenses());
        Assert.assertEquals(DiskTest.ATTACHED_INSTANCES, standardDisk.getAttachedInstances());
        Assert.assertEquals(DiskTest.LAST_ATTACH_TIMESTAMP, standardDisk.getLastAttachTimestamp());
        Assert.assertEquals(DiskTest.LAST_DETACH_TIMESTAMP, standardDisk.getLastDetachTimestamp());
        Assert.assertSame(serviceMockReturnsOptions, standardDisk.getCompute());
        Assert.assertEquals(DiskTest.DISK_ID, imageDisk.getDiskId());
        Assert.assertEquals(DiskTest.GENERATED_ID, imageDisk.getGeneratedId());
        Assert.assertEquals(DiskTest.IMAGE_DISK_CONFIGURATION, imageDisk.getConfiguration());
        Assert.assertEquals(DiskTest.CREATION_TIMESTAMP, imageDisk.getCreationTimestamp());
        Assert.assertEquals(DiskTest.CREATION_STATUS, imageDisk.getCreationStatus());
        Assert.assertEquals(DiskTest.DESCRIPTION, imageDisk.getDescription());
        Assert.assertEquals(DiskTest.LICENSES, imageDisk.getLicenses());
        Assert.assertEquals(DiskTest.ATTACHED_INSTANCES, imageDisk.getAttachedInstances());
        Assert.assertEquals(DiskTest.LAST_ATTACH_TIMESTAMP, imageDisk.getLastAttachTimestamp());
        Assert.assertEquals(DiskTest.LAST_DETACH_TIMESTAMP, imageDisk.getLastDetachTimestamp());
        Assert.assertSame(serviceMockReturnsOptions, imageDisk.getCompute());
        Assert.assertEquals(DiskTest.DISK_ID, snapshotDisk.getDiskId());
        Assert.assertEquals(DiskTest.GENERATED_ID, snapshotDisk.getGeneratedId());
        Assert.assertEquals(DiskTest.SNAPSHOT_DISK_CONFIGURATION, snapshotDisk.getConfiguration());
        Assert.assertEquals(DiskTest.CREATION_TIMESTAMP, snapshotDisk.getCreationTimestamp());
        Assert.assertEquals(DiskTest.CREATION_STATUS, snapshotDisk.getCreationStatus());
        Assert.assertEquals(DiskTest.DESCRIPTION, snapshotDisk.getDescription());
        Assert.assertEquals(DiskTest.LICENSES, snapshotDisk.getLicenses());
        Assert.assertEquals(DiskTest.ATTACHED_INSTANCES, snapshotDisk.getAttachedInstances());
        Assert.assertEquals(DiskTest.LAST_ATTACH_TIMESTAMP, snapshotDisk.getLastAttachTimestamp());
        Assert.assertEquals(DiskTest.LAST_DETACH_TIMESTAMP, snapshotDisk.getLastDetachTimestamp());
        Assert.assertSame(serviceMockReturnsOptions, snapshotDisk.getCompute());
        Disk disk = build();
        Assert.assertEquals(DiskId.of("newProject", "newZone"), disk.getDiskId());
        Assert.assertNull(disk.getGeneratedId());
        Assert.assertEquals(DiskTest.SNAPSHOT_DISK_CONFIGURATION, disk.getConfiguration());
        Assert.assertNull(disk.getCreationTimestamp());
        Assert.assertNull(disk.getCreationStatus());
        Assert.assertNull(disk.getDescription());
        Assert.assertNull(disk.getLicenses());
        Assert.assertNull(disk.getAttachedInstances());
        Assert.assertNull(disk.getLastAttachTimestamp());
        Assert.assertNull(disk.getLastDetachTimestamp());
        Assert.assertSame(serviceMockReturnsOptions, disk.getCompute());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedDisk(24);
        compareDisk(standardDisk, Disk.fromPb(serviceMockReturnsOptions, standardDisk.toPb()));
        compareDisk(imageDisk, Disk.fromPb(serviceMockReturnsOptions, imageDisk.toPb()));
        compareDisk(snapshotDisk, Disk.fromPb(serviceMockReturnsOptions, snapshotDisk.toPb()));
        Disk disk = build();
        compareDisk(disk, Disk.fromPb(serviceMockReturnsOptions, disk.toPb()));
        disk = new Disk.Builder(serviceMockReturnsOptions, DiskTest.DISK_ID, DiskTest.SNAPSHOT_DISK_CONFIGURATION).build();
        compareDisk(disk, Disk.fromPb(serviceMockReturnsOptions, disk.toPb()));
        disk = new Disk.Builder(serviceMockReturnsOptions, DiskTest.DISK_ID, DiskTest.IMAGE_DISK_CONFIGURATION).build();
        compareDisk(disk, Disk.fromPb(serviceMockReturnsOptions, disk.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        expect(compute.deleteDisk(DiskTest.DISK_ID)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedDisk(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteDisk(DiskTest.DISK_ID)).andReturn(null);
        replay(compute);
        initializeDisk();
        Assert.assertNull(disk.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedDisk(3);
        Compute[] expectedOptions = new DiskOption[]{ DiskOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getDisk(DiskTest.DISK_ID, expectedOptions)).andReturn(imageDisk);
        replay(compute);
        initializeDisk();
        Assert.assertTrue(disk.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedDisk(3);
        Compute[] expectedOptions = new DiskOption[]{ DiskOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getDisk(DiskTest.DISK_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeDisk();
        Assert.assertFalse(disk.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedDisk(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getDisk(DiskTest.DISK_ID)).andReturn(imageDisk);
        replay(compute);
        initializeDisk();
        Disk updatedDisk = disk.reload();
        compareDisk(imageDisk, updatedDisk);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedDisk(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getDisk(DiskTest.DISK_ID)).andReturn(null);
        replay(compute);
        initializeDisk();
        Assert.assertNull(disk.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedDisk(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getDisk(DiskTest.DISK_ID, DiskOption.fields())).andReturn(imageDisk);
        replay(compute);
        initializeDisk();
        Disk updatedDisk = disk.reload(DiskOption.fields());
        compareDisk(imageDisk, updatedDisk);
        verify(compute);
    }

    @Test
    public void testCreateSnapshot() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        SnapshotId snapshotId = SnapshotId.of(DiskTest.SNAPSHOT.getSnapshot());
        SnapshotInfo snapshot = SnapshotInfo.newBuilder(snapshotId, DiskTest.DISK_ID).build();
        expect(compute.create(snapshot)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createSnapshot(DiskTest.SNAPSHOT.getSnapshot()));
    }

    @Test
    public void testCreateSnapshotWithDescription() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        SnapshotId snapshotId = SnapshotId.of(DiskTest.SNAPSHOT.getSnapshot());
        SnapshotInfo snapshot = SnapshotInfo.newBuilder(snapshotId, DiskTest.DISK_ID).setDescription("description").build();
        expect(compute.create(snapshot)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createSnapshot(DiskTest.SNAPSHOT.getSnapshot(), "description"));
    }

    @Test
    public void testCreateSnapshotWithOptions() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        SnapshotId snapshotId = SnapshotId.of(DiskTest.SNAPSHOT.getSnapshot());
        SnapshotInfo snapshot = SnapshotInfo.newBuilder(snapshotId, DiskTest.DISK_ID).build();
        expect(compute.create(snapshot, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createSnapshot(DiskTest.SNAPSHOT.getSnapshot(), OperationOption.fields()));
    }

    @Test
    public void testCreateSnapshotWithDescriptionAndOptions() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        SnapshotId snapshotId = SnapshotId.of(DiskTest.SNAPSHOT.getSnapshot());
        SnapshotInfo snapshot = SnapshotInfo.newBuilder(snapshotId, DiskTest.DISK_ID).setDescription("description").build();
        expect(compute.create(snapshot, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createSnapshot(DiskTest.SNAPSHOT.getSnapshot(), "description", OperationOption.fields()));
    }

    @Test
    public void testCreateImage() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        ImageId imageId = ImageId.of(DiskTest.IMAGE.getImage());
        ImageInfo image = ImageInfo.of(imageId, DiskImageConfiguration.of(DiskTest.DISK_ID));
        expect(compute.create(image)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createImage(DiskTest.IMAGE.getImage()));
    }

    @Test
    public void testCreateImageWithDescription() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        ImageId imageId = ImageId.of(DiskTest.IMAGE.getImage());
        ImageInfo image = ImageInfo.newBuilder(imageId, DiskImageConfiguration.of(DiskTest.DISK_ID)).setDescription("description").build();
        expect(compute.create(image)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createImage(DiskTest.IMAGE.getImage(), "description"));
    }

    @Test
    public void testCreateImageWithOptions() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        ImageId imageId = ImageId.of(DiskTest.IMAGE.getImage());
        ImageInfo image = ImageInfo.of(imageId, DiskImageConfiguration.of(DiskTest.DISK_ID));
        expect(compute.create(image, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createImage(DiskTest.IMAGE.getImage(), OperationOption.fields()));
    }

    @Test
    public void testCreateImageWithDescriptionAndOptions() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        ImageId imageId = ImageId.of(DiskTest.IMAGE.getImage());
        ImageInfo image = ImageInfo.newBuilder(imageId, DiskImageConfiguration.of(DiskTest.DISK_ID)).setDescription("description").build();
        expect(compute.create(image, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.createImage(DiskTest.IMAGE.getImage(), "description", OperationOption.fields()));
    }

    @Test
    public void testResizeOperation() {
        initializeExpectedDisk(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "zone", "op")).build();
        expect(compute.resize(DiskTest.DISK_ID, 42L)).andReturn(operation);
        replay(compute);
        initializeDisk();
        Assert.assertSame(operation, disk.resize(42L));
    }

    @Test
    public void testResizeNull() {
        initializeExpectedDisk(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.resize(DiskTest.DISK_ID, 42L)).andReturn(null);
        replay(compute);
        initializeDisk();
        Assert.assertNull(disk.resize(42L));
    }
}

