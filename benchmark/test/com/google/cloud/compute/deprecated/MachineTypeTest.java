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
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MachineTypeTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final MachineTypeId MACHINE_TYPE_ID = MachineTypeId.of("project", "zone", "type");

    private static final Integer CPUS = 1;

    private static final Integer MEMORY_MB = 2;

    private static final List<Integer> SCRATCH_DISKS = ImmutableList.of(3);

    private static final Integer MAXIMUM_PERSISTENT_DISKS = 4;

    private static final Long MAXIMUM_PERSISTENT_DISKS_SIZE_GB = 5L;

    private static final DeprecationStatus<MachineTypeId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, MachineTypeTest.MACHINE_TYPE_ID);

    private static final MachineType MACHINE_TYPE = MachineType.newBuilder().setGeneratedId(MachineTypeTest.GENERATED_ID).setMachineTypeId(MachineTypeTest.MACHINE_TYPE_ID).setCreationTimestamp(MachineTypeTest.CREATION_TIMESTAMP).setDescription(MachineTypeTest.DESCRIPTION).setCpus(MachineTypeTest.CPUS).setMemoryMb(MachineTypeTest.MEMORY_MB).setScratchDisksSizeGb(MachineTypeTest.SCRATCH_DISKS).setMaximumPersistentDisks(MachineTypeTest.MAXIMUM_PERSISTENT_DISKS).setMaximumPersistentDisksSizeGb(MachineTypeTest.MAXIMUM_PERSISTENT_DISKS_SIZE_GB).setDeprecationStatus(MachineTypeTest.DEPRECATION_STATUS).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(MachineTypeTest.GENERATED_ID, MachineTypeTest.MACHINE_TYPE.getGeneratedId());
        Assert.assertEquals(MachineTypeTest.MACHINE_TYPE_ID, MachineTypeTest.MACHINE_TYPE.getMachineTypeId());
        Assert.assertEquals(MachineTypeTest.CREATION_TIMESTAMP, MachineTypeTest.MACHINE_TYPE.getCreationTimestamp());
        Assert.assertEquals(MachineTypeTest.DESCRIPTION, MachineTypeTest.MACHINE_TYPE.getDescription());
        Assert.assertEquals(MachineTypeTest.CPUS, MachineTypeTest.MACHINE_TYPE.getCpus());
        Assert.assertEquals(MachineTypeTest.MEMORY_MB, MachineTypeTest.MACHINE_TYPE.getMemoryMb());
        Assert.assertEquals(MachineTypeTest.SCRATCH_DISKS, MachineTypeTest.MACHINE_TYPE.getScratchDisksSizeGb());
        Assert.assertEquals(MachineTypeTest.MAXIMUM_PERSISTENT_DISKS, MachineTypeTest.MACHINE_TYPE.getMaximumPersistentDisks());
        Assert.assertEquals(MachineTypeTest.MAXIMUM_PERSISTENT_DISKS_SIZE_GB, MachineTypeTest.MACHINE_TYPE.getMaximumPersistentDisksSizeGb());
        Assert.assertEquals(MachineTypeTest.DEPRECATION_STATUS, MachineTypeTest.MACHINE_TYPE.getDeprecationStatus());
    }

    @Test
    public void testToPbAndFromPb() {
        compareMachineTypes(MachineTypeTest.MACHINE_TYPE, MachineType.fromPb(MachineTypeTest.MACHINE_TYPE.toPb()));
        MachineType machineType = MachineType.newBuilder().setMachineTypeId(MachineTypeTest.MACHINE_TYPE_ID).build();
        compareMachineTypes(machineType, MachineType.fromPb(machineType.toPb()));
    }
}

