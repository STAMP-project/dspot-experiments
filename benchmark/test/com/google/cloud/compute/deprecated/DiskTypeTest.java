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
import org.junit.Assert;
import org.junit.Test;


public class DiskTypeTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final String VALID_DISK_SIZE = "10GB-10TB";

    private static final Long DEFAULT_DISK_SIZE_GB = 10L;

    private static final DiskTypeId DISK_TYPE_ID = DiskTypeId.of("project", "zone", "diskType");

    private static final DeprecationStatus<DiskTypeId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, DiskTypeTest.DISK_TYPE_ID);

    private static final DiskType DISK_TYPE = DiskType.newBuilder().setGeneratedId(DiskTypeTest.GENERATED_ID).setDiskTypeId(DiskTypeTest.DISK_TYPE_ID).setCreationTimestamp(DiskTypeTest.CREATION_TIMESTAMP).setDescription(DiskTypeTest.DESCRIPTION).setValidDiskSize(DiskTypeTest.VALID_DISK_SIZE).setDefaultDiskSizeGb(DiskTypeTest.DEFAULT_DISK_SIZE_GB).setDeprecationStatus(DiskTypeTest.DEPRECATION_STATUS).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(DiskTypeTest.GENERATED_ID, DiskTypeTest.DISK_TYPE.getGeneratedId());
        Assert.assertEquals(DiskTypeTest.DISK_TYPE_ID, DiskTypeTest.DISK_TYPE.getDiskTypeId());
        Assert.assertEquals(DiskTypeTest.CREATION_TIMESTAMP, DiskTypeTest.DISK_TYPE.getCreationTimestamp());
        Assert.assertEquals(DiskTypeTest.DESCRIPTION, DiskTypeTest.DISK_TYPE.getDescription());
        Assert.assertEquals(DiskTypeTest.VALID_DISK_SIZE, DiskTypeTest.DISK_TYPE.getValidDiskSize());
        Assert.assertEquals(DiskTypeTest.DEFAULT_DISK_SIZE_GB, DiskTypeTest.DISK_TYPE.getDefaultDiskSizeGb());
        Assert.assertEquals(DiskTypeTest.DEPRECATION_STATUS, DiskTypeTest.DISK_TYPE.getDeprecationStatus());
    }

    @Test
    public void testToPbAndFromPb() {
        compareDiskTypes(DiskTypeTest.DISK_TYPE, DiskType.fromPb(DiskTypeTest.DISK_TYPE.toPb()));
        DiskType diskType = DiskType.newBuilder().setDiskTypeId(DiskTypeTest.DISK_TYPE_ID).build();
        compareDiskTypes(diskType, DiskType.fromPb(diskType.toPb()));
    }
}

