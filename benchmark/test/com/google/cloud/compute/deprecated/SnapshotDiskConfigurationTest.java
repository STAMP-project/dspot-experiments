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


import Type.SNAPSHOT;
import org.junit.Assert;
import org.junit.Test;


public class SnapshotDiskConfigurationTest {
    private static final Long SIZE = 42L;

    private static final DiskTypeId DISK_TYPE = DiskTypeId.of("project", "zone", "type");

    private static final SnapshotId SNAPSHOT = SnapshotId.of("project", "snapshot");

    private static final String SNAPSHOT_ID = "snapshotId";

    private static final SnapshotDiskConfiguration DISK_CONFIGURATION = SnapshotDiskConfiguration.newBuilder(SnapshotDiskConfigurationTest.SNAPSHOT).setSizeGb(SnapshotDiskConfigurationTest.SIZE).setDiskType(SnapshotDiskConfigurationTest.DISK_TYPE).setSourceSnapshotId(SnapshotDiskConfigurationTest.SNAPSHOT_ID).build();

    @Test
    public void testToBuilder() {
        compareSnapshotDiskConfiguration(SnapshotDiskConfigurationTest.DISK_CONFIGURATION, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().build());
        SnapshotId newSnapshot = SnapshotId.of("newProject", "newSnapshot");
        SnapshotDiskConfiguration diskConfiguration = SnapshotDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setSizeGb(24L).setSourceSnapshot(newSnapshot).setSourceSnapshotId("newSnapshotId").build();
        Assert.assertEquals(24L, diskConfiguration.getSizeGb().longValue());
        Assert.assertEquals(newSnapshot, diskConfiguration.getSourceSnapshot());
        Assert.assertEquals("newSnapshotId", diskConfiguration.getSourceSnapshotId());
        diskConfiguration = diskConfiguration.toBuilder().setSizeGb(SnapshotDiskConfigurationTest.SIZE).setSourceSnapshot(SnapshotDiskConfigurationTest.SNAPSHOT).setSourceSnapshotId(SnapshotDiskConfigurationTest.SNAPSHOT_ID).build();
        compareSnapshotDiskConfiguration(SnapshotDiskConfigurationTest.DISK_CONFIGURATION, diskConfiguration);
    }

    @Test
    public void testToBuilderIncomplete() {
        SnapshotDiskConfiguration diskConfiguration = SnapshotDiskConfiguration.of(SnapshotDiskConfigurationTest.SNAPSHOT);
        compareSnapshotDiskConfiguration(diskConfiguration, diskConfiguration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(SnapshotDiskConfigurationTest.DISK_TYPE, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.getDiskType());
        Assert.assertEquals(SnapshotDiskConfigurationTest.SIZE, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.getSizeGb());
        Assert.assertEquals(SnapshotDiskConfigurationTest.SNAPSHOT, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.getSourceSnapshot());
        Assert.assertEquals(SnapshotDiskConfigurationTest.SNAPSHOT_ID, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.getSourceSnapshotId());
        Assert.assertEquals(Type.SNAPSHOT, SnapshotDiskConfigurationTest.DISK_CONFIGURATION.getType());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((DiskConfiguration.fromPb(SnapshotDiskConfigurationTest.DISK_CONFIGURATION.toPb())) instanceof SnapshotDiskConfiguration));
        compareSnapshotDiskConfiguration(SnapshotDiskConfigurationTest.DISK_CONFIGURATION, DiskConfiguration.<SnapshotDiskConfiguration>fromPb(SnapshotDiskConfigurationTest.DISK_CONFIGURATION.toPb()));
    }

    @Test
    public void testOf() {
        SnapshotDiskConfiguration configuration = SnapshotDiskConfiguration.of(SnapshotDiskConfigurationTest.SNAPSHOT);
        Assert.assertNull(configuration.getDiskType());
        Assert.assertNull(configuration.getSizeGb());
        Assert.assertNull(configuration.getSourceSnapshotId());
        Assert.assertEquals(SnapshotDiskConfigurationTest.SNAPSHOT, configuration.getSourceSnapshot());
        Assert.assertEquals(Type.SNAPSHOT, configuration.getType());
    }

    @Test
    public void testSetProjectId() {
        SnapshotDiskConfiguration configuration = SnapshotDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(SnapshotDiskConfigurationTest.DISK_TYPE.getZone(), SnapshotDiskConfigurationTest.DISK_TYPE.getType())).setSourceSnapshot(SnapshotId.of(SnapshotDiskConfigurationTest.SNAPSHOT.getSnapshot())).build();
        compareSnapshotDiskConfiguration(SnapshotDiskConfigurationTest.DISK_CONFIGURATION, configuration.setProjectId("project"));
    }
}

