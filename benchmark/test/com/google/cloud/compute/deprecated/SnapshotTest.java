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


import Compute.SnapshotOption;
import SnapshotInfo.Status;
import SnapshotInfo.StorageBytesStatus;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SnapshotTest {
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

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Snapshot snapshot;

    private Snapshot expectedSnapshot;

    @Test
    public void testToBuilder() {
        initializeExpectedSnapshot(8);
        compareSnapshot(expectedSnapshot, expectedSnapshot.toBuilder().build());
        Snapshot newSnapshot = build();
        Assert.assertEquals("newDescription", newSnapshot.getDescription());
        newSnapshot = newSnapshot.toBuilder().setDescription("description").build();
        compareSnapshot(expectedSnapshot, newSnapshot);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedSnapshot(5);
        SnapshotInfo snapshotInfo = SnapshotInfo.of(SnapshotTest.SNAPSHOT_ID, SnapshotTest.SOURCE_DISK);
        Snapshot snapshot = new Snapshot(serviceMockReturnsOptions, new SnapshotInfo.BuilderImpl(snapshotInfo));
        compareSnapshot(snapshot, snapshot.toBuilder().build());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedSnapshot(8);
        compareSnapshot(expectedSnapshot, Snapshot.fromPb(serviceMockReturnsOptions, expectedSnapshot.toPb()));
        Snapshot snapshot = build();
        compareSnapshot(snapshot, Snapshot.fromPb(serviceMockReturnsOptions, snapshot.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedSnapshot(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        expect(compute.deleteSnapshot(SnapshotTest.SNAPSHOT_ID)).andReturn(operation);
        replay(compute);
        initializeSnapshot();
        Assert.assertSame(operation, snapshot.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedSnapshot(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteSnapshot(SnapshotTest.SNAPSHOT_ID)).andReturn(null);
        replay(compute);
        initializeSnapshot();
        Assert.assertNull(snapshot.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedSnapshot(1);
        Compute[] expectedOptions = new SnapshotOption[]{ SnapshotOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSnapshot(SnapshotTest.SNAPSHOT_ID.getSnapshot(), expectedOptions)).andReturn(expectedSnapshot);
        replay(compute);
        initializeSnapshot();
        Assert.assertTrue(snapshot.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedSnapshot(1);
        Compute[] expectedOptions = new SnapshotOption[]{ SnapshotOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSnapshot(SnapshotTest.SNAPSHOT_ID.getSnapshot(), expectedOptions)).andReturn(null);
        replay(compute);
        initializeSnapshot();
        Assert.assertFalse(snapshot.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedSnapshot(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSnapshot(SnapshotTest.SNAPSHOT_ID.getSnapshot())).andReturn(expectedSnapshot);
        replay(compute);
        initializeSnapshot();
        Snapshot updatedSnapshot = snapshot.reload();
        compareSnapshot(expectedSnapshot, updatedSnapshot);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedSnapshot(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSnapshot(SnapshotTest.SNAPSHOT_ID.getSnapshot())).andReturn(null);
        replay(compute);
        initializeSnapshot();
        Assert.assertNull(snapshot.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedSnapshot(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSnapshot(SnapshotTest.SNAPSHOT_ID.getSnapshot(), SnapshotOption.fields())).andReturn(expectedSnapshot);
        replay(compute);
        initializeSnapshot();
        Snapshot updatedSnapshot = snapshot.reload(SnapshotOption.fields());
        compareSnapshot(expectedSnapshot, updatedSnapshot);
        verify(compute);
    }
}

