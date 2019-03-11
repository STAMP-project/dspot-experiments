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


import AttachedDisk.AttachedDiskConfiguration;
import InterfaceType.SCSI;
import PersistentDiskConfiguration.Mode;
import Type.PERSISTENT;
import Type.SCRATCH;
import com.google.cloud.compute.deprecated.AttachedDisk.AttachedDiskConfiguration.InterfaceType;
import com.google.cloud.compute.deprecated.AttachedDisk.CreateDiskConfiguration;
import com.google.cloud.compute.deprecated.AttachedDisk.PersistentDiskConfiguration;
import com.google.cloud.compute.deprecated.AttachedDisk.ScratchDiskConfiguration;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AttachedDiskTest {
    private static final Boolean AUTO_DELETE = true;

    private static final Boolean BOOT = true;

    private static final Integer INDEX = 0;

    private static final String DEVICE_NAME = "deviceName";

    private static final String DISK_NAME = "diskName";

    private static final DiskTypeId DISK_TYPE_ID = DiskTypeId.of("project", "zone", "diskType");

    private static final Long DISK_SIZE_GB = 42L;

    private static final DiskId DISK_ID = DiskId.of("project", "zone", "disk");

    private static final ImageId IMAGE_ID = ImageId.of("project", "image");

    private static final InterfaceType INTERFACE_TYPE = InterfaceType.NVME;

    private static final Mode MODE = Mode.READ_ONLY;

    private static final PersistentDiskConfiguration PERSISTENT_DISK_CONFIGURATION = PersistentDiskConfiguration.newBuilder(AttachedDiskTest.DISK_ID).setBoot(AttachedDiskTest.BOOT).setAutoDelete(AttachedDiskTest.AUTO_DELETE).setMode(AttachedDiskTest.MODE).build();

    private static final ScratchDiskConfiguration SCRATCH_DISK_CONFIGURATION = ScratchDiskConfiguration.newBuilder(AttachedDiskTest.DISK_TYPE_ID).setInterfaceType(AttachedDiskTest.INTERFACE_TYPE).build();

    private static final CreateDiskConfiguration CREATE_DISK_CONFIGURATION = CreateDiskConfiguration.newBuilder(AttachedDiskTest.IMAGE_ID).setAutoDelete(AttachedDiskTest.AUTO_DELETE).setDiskName(AttachedDiskTest.DISK_NAME).setDiskType(AttachedDiskTest.DISK_TYPE_ID).setDiskSizeGb(AttachedDiskTest.DISK_SIZE_GB).setSourceImage(AttachedDiskTest.IMAGE_ID).build();

    private static final List<LicenseId> LICENSES = ImmutableList.of(LicenseId.of("project", "license1"), LicenseId.of("project", "license2"));

    private static final AttachedDisk PERSISTENT_DISK = AttachedDisk.newBuilder(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION).setDeviceName(AttachedDiskTest.DEVICE_NAME).setIndex(AttachedDiskTest.INDEX).setLicenses(AttachedDiskTest.LICENSES).build();

    private static final AttachedDisk SCRATCH_DISK = AttachedDisk.newBuilder(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION).setDeviceName(AttachedDiskTest.DEVICE_NAME).setIndex(AttachedDiskTest.INDEX).setLicenses(AttachedDiskTest.LICENSES).build();

    private static final AttachedDisk CREATED_DISK = AttachedDisk.newBuilder(AttachedDiskTest.CREATE_DISK_CONFIGURATION).setDeviceName(AttachedDiskTest.DEVICE_NAME).setIndex(AttachedDiskTest.INDEX).setLicenses(AttachedDiskTest.LICENSES).build();

    @Test
    public void testConfigurationToBuilder() {
        comparePersistentDiskConfiguration(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.toBuilder().build());
        compareScratchDiskConfiguration(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.toBuilder().build());
        compareCreateDiskConfiguration(AttachedDiskTest.CREATE_DISK_CONFIGURATION, AttachedDiskTest.CREATE_DISK_CONFIGURATION.toBuilder().build());
        PersistentDiskConfiguration persistentDiskConfiguration = AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.toBuilder().setAutoDelete(false).build();
        Assert.assertFalse(persistentDiskConfiguration.autoDelete());
        persistentDiskConfiguration = persistentDiskConfiguration.toBuilder().setAutoDelete(AttachedDiskTest.AUTO_DELETE).build();
        Assert.assertEquals(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, persistentDiskConfiguration);
        ScratchDiskConfiguration scratchDiskConfiguration = AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.toBuilder().setInterfaceType(SCSI).build();
        Assert.assertEquals(SCSI, scratchDiskConfiguration.getInterfaceType());
        scratchDiskConfiguration = scratchDiskConfiguration.toBuilder().setInterfaceType(AttachedDiskTest.INTERFACE_TYPE).build();
        Assert.assertEquals(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, scratchDiskConfiguration);
        CreateDiskConfiguration createDiskConfiguration = AttachedDiskTest.CREATE_DISK_CONFIGURATION.toBuilder().setAutoDelete(false).build();
        Assert.assertFalse(createDiskConfiguration.autoDelete());
        createDiskConfiguration = createDiskConfiguration.toBuilder().setAutoDelete(AttachedDiskTest.AUTO_DELETE).build();
        Assert.assertEquals(AttachedDiskTest.CREATE_DISK_CONFIGURATION, createDiskConfiguration);
    }

    @Test
    public void testConfigurationToBuilderIncomplete() {
        PersistentDiskConfiguration persistentConfiguration = PersistentDiskConfiguration.of(AttachedDiskTest.DISK_ID);
        comparePersistentDiskConfiguration(persistentConfiguration, AttachedDiskConfiguration.<PersistentDiskConfiguration>fromPb(persistentConfiguration.toPb()));
        ScratchDiskConfiguration scratchDiskConfiguration = ScratchDiskConfiguration.of(AttachedDiskTest.DISK_TYPE_ID);
        compareScratchDiskConfiguration(scratchDiskConfiguration, AttachedDiskConfiguration.<ScratchDiskConfiguration>fromPb(scratchDiskConfiguration.toPb()));
        CreateDiskConfiguration createDiskConfiguration = CreateDiskConfiguration.of(AttachedDiskTest.IMAGE_ID);
        compareCreateDiskConfiguration(createDiskConfiguration, AttachedDiskConfiguration.<CreateDiskConfiguration>fromPb(createDiskConfiguration.toPb()));
    }

    @Test
    public void testToBuilder() {
        compareAttachedDisk(AttachedDiskTest.PERSISTENT_DISK, AttachedDiskTest.PERSISTENT_DISK.toBuilder().build());
        compareAttachedDisk(AttachedDiskTest.SCRATCH_DISK, AttachedDiskTest.SCRATCH_DISK.toBuilder().build());
        compareAttachedDisk(AttachedDiskTest.CREATED_DISK, AttachedDiskTest.CREATED_DISK.toBuilder().build());
        AttachedDisk attachedDisk = AttachedDiskTest.PERSISTENT_DISK.toBuilder().setDeviceName("newDeviceName").build();
        Assert.assertEquals("newDeviceName", attachedDisk.getDeviceName());
        attachedDisk = attachedDisk.toBuilder().setDeviceName(AttachedDiskTest.DEVICE_NAME).build();
        compareAttachedDisk(AttachedDiskTest.PERSISTENT_DISK, attachedDisk);
    }

    @Test
    public void testToBuilderIncomplete() {
        AttachedDisk attachedDisk = AttachedDisk.of(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION);
        Assert.assertEquals(attachedDisk, attachedDisk.toBuilder().build());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION);
        Assert.assertEquals(attachedDisk, attachedDisk.toBuilder().build());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.CREATE_DISK_CONFIGURATION);
        Assert.assertEquals(attachedDisk, attachedDisk.toBuilder().build());
    }

    @Test
    public void testConfigurationBuilder() {
        Assert.assertTrue(AttachedDiskTest.CREATE_DISK_CONFIGURATION.boot());
        Assert.assertEquals(AttachedDiskTest.AUTO_DELETE, AttachedDiskTest.CREATE_DISK_CONFIGURATION.autoDelete());
        Assert.assertNull(AttachedDiskTest.CREATE_DISK_CONFIGURATION.getInterfaceType());
        Assert.assertEquals(PERSISTENT, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getType());
        Assert.assertEquals(AttachedDiskTest.IMAGE_ID, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getSourceImage());
        Assert.assertEquals(AttachedDiskTest.DISK_NAME, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getDiskName());
        Assert.assertEquals(AttachedDiskTest.DISK_TYPE_ID, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getDiskType());
        Assert.assertEquals(AttachedDiskTest.DISK_SIZE_GB, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getDiskSizeGb());
        Assert.assertEquals(AttachedDiskTest.IMAGE_ID, AttachedDiskTest.CREATE_DISK_CONFIGURATION.getSourceImage());
        Assert.assertEquals(AttachedDiskTest.BOOT, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.boot());
        Assert.assertEquals(AttachedDiskTest.AUTO_DELETE, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.autoDelete());
        Assert.assertNull(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.getInterfaceType());
        Assert.assertEquals(PERSISTENT, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.getType());
        Assert.assertEquals(AttachedDiskTest.MODE, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.getMode());
        Assert.assertEquals(AttachedDiskTest.DISK_ID, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.getSourceDisk());
        Assert.assertFalse(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.boot());
        Assert.assertTrue(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.autoDelete());
        Assert.assertEquals(AttachedDiskTest.INTERFACE_TYPE, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.getInterfaceType());
        Assert.assertEquals(SCRATCH, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.getType());
        Assert.assertEquals(AttachedDiskTest.DISK_TYPE_ID, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.getDiskType());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, AttachedDiskTest.PERSISTENT_DISK.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.PERSISTENT_DISK.getDeviceName());
        Assert.assertEquals(AttachedDiskTest.INDEX, AttachedDiskTest.PERSISTENT_DISK.getIndex());
        Assert.assertEquals(AttachedDiskTest.LICENSES, AttachedDiskTest.PERSISTENT_DISK.getLicenses());
        Assert.assertEquals(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, AttachedDiskTest.SCRATCH_DISK.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.SCRATCH_DISK.getDeviceName());
        Assert.assertEquals(AttachedDiskTest.INDEX, AttachedDiskTest.SCRATCH_DISK.getIndex());
        Assert.assertEquals(AttachedDiskTest.LICENSES, AttachedDiskTest.SCRATCH_DISK.getLicenses());
        Assert.assertEquals(AttachedDiskTest.CREATE_DISK_CONFIGURATION, AttachedDiskTest.CREATED_DISK.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.CREATED_DISK.getDeviceName());
        Assert.assertEquals(AttachedDiskTest.INDEX, AttachedDiskTest.CREATED_DISK.getIndex());
        Assert.assertEquals(AttachedDiskTest.LICENSES, AttachedDiskTest.CREATED_DISK.getLicenses());
    }

    @Test
    public void testConfigurationOf() {
        PersistentDiskConfiguration persistentConfiguration = PersistentDiskConfiguration.of(AttachedDiskTest.DISK_ID);
        Assert.assertEquals(AttachedDiskTest.DISK_ID, persistentConfiguration.getSourceDisk());
        Assert.assertEquals(PERSISTENT, persistentConfiguration.getType());
        Assert.assertNull(persistentConfiguration.autoDelete());
        Assert.assertNull(persistentConfiguration.boot());
        Assert.assertNull(persistentConfiguration.getInterfaceType());
        ScratchDiskConfiguration scratchDiskConfiguration = ScratchDiskConfiguration.of(AttachedDiskTest.DISK_TYPE_ID);
        Assert.assertEquals(AttachedDiskTest.DISK_TYPE_ID, scratchDiskConfiguration.getDiskType());
        Assert.assertNull(scratchDiskConfiguration.getInterfaceType());
        Assert.assertEquals(SCRATCH, scratchDiskConfiguration.getType());
        Assert.assertTrue(scratchDiskConfiguration.autoDelete());
        Assert.assertFalse(scratchDiskConfiguration.boot());
        Assert.assertNull(scratchDiskConfiguration.getInterfaceType());
        CreateDiskConfiguration createDiskConfiguration = CreateDiskConfiguration.of(AttachedDiskTest.IMAGE_ID);
        Assert.assertEquals(AttachedDiskTest.IMAGE_ID, createDiskConfiguration.getSourceImage());
        Assert.assertNull(createDiskConfiguration.getDiskType());
        Assert.assertNull(createDiskConfiguration.getDiskName());
        Assert.assertNull(createDiskConfiguration.getDiskSizeGb());
        Assert.assertNull(createDiskConfiguration.getInterfaceType());
        Assert.assertEquals(PERSISTENT, createDiskConfiguration.getType());
        Assert.assertNull(createDiskConfiguration.autoDelete());
        Assert.assertTrue(createDiskConfiguration.boot());
        Assert.assertNull(createDiskConfiguration.getInterfaceType());
    }

    @Test
    public void testOf() {
        AttachedDisk attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertNull(attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertNull(attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.CREATE_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.CREATE_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertEquals(AttachedDiskTest.DEVICE_NAME, attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
        attachedDisk = AttachedDisk.of(AttachedDiskTest.CREATE_DISK_CONFIGURATION);
        Assert.assertEquals(AttachedDiskTest.CREATE_DISK_CONFIGURATION, attachedDisk.getConfiguration());
        Assert.assertNull(attachedDisk.getDeviceName());
        Assert.assertNull(attachedDisk.getIndex());
        Assert.assertNull(attachedDisk.getLicenses());
    }

    @Test
    public void testConfigurationToAndFromPb() {
        PersistentDiskConfiguration persistentConfiguration = PersistentDiskConfiguration.of(AttachedDiskTest.DISK_ID);
        comparePersistentDiskConfiguration(persistentConfiguration, AttachedDiskConfiguration.<PersistentDiskConfiguration>fromPb(persistentConfiguration.toPb()));
        comparePersistentDiskConfiguration(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION, AttachedDiskConfiguration.<PersistentDiskConfiguration>fromPb(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION.toPb()));
        ScratchDiskConfiguration scratchDiskConfiguration = ScratchDiskConfiguration.of(AttachedDiskTest.DISK_TYPE_ID);
        compareScratchDiskConfiguration(scratchDiskConfiguration, AttachedDiskConfiguration.<ScratchDiskConfiguration>fromPb(scratchDiskConfiguration.toPb()));
        compareScratchDiskConfiguration(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION, AttachedDiskConfiguration.<ScratchDiskConfiguration>fromPb(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION.toPb()));
        CreateDiskConfiguration createDiskConfiguration = CreateDiskConfiguration.of(AttachedDiskTest.IMAGE_ID);
        compareCreateDiskConfiguration(createDiskConfiguration, AttachedDiskConfiguration.<CreateDiskConfiguration>fromPb(createDiskConfiguration.toPb()));
        compareCreateDiskConfiguration(AttachedDiskTest.CREATE_DISK_CONFIGURATION, AttachedDiskConfiguration.<CreateDiskConfiguration>fromPb(AttachedDiskTest.CREATE_DISK_CONFIGURATION.toPb()));
    }

    @Test
    public void testToAndFromPb() {
        AttachedDisk attachedDisk = AttachedDisk.fromPb(AttachedDiskTest.PERSISTENT_DISK.toPb());
        compareAttachedDisk(AttachedDiskTest.PERSISTENT_DISK, attachedDisk);
        attachedDisk = AttachedDisk.fromPb(AttachedDiskTest.SCRATCH_DISK.toPb());
        compareAttachedDisk(AttachedDiskTest.SCRATCH_DISK, attachedDisk);
        attachedDisk = AttachedDisk.fromPb(AttachedDiskTest.CREATED_DISK.toPb());
        compareAttachedDisk(AttachedDiskTest.CREATED_DISK, attachedDisk);
        attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
        attachedDisk = AttachedDisk.of(AttachedDiskTest.PERSISTENT_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
        attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.SCRATCH_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
        attachedDisk = AttachedDisk.of(AttachedDiskTest.SCRATCH_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
        attachedDisk = AttachedDisk.of(AttachedDiskTest.DEVICE_NAME, AttachedDiskTest.CREATE_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
        attachedDisk = AttachedDisk.of(AttachedDiskTest.CREATE_DISK_CONFIGURATION);
        compareAttachedDisk(attachedDisk, AttachedDisk.fromPb(attachedDisk.toPb()));
    }

    @Test
    public void testConfigurationSetProjectId() {
        PersistentDiskConfiguration persistentConfiguration = PersistentDiskConfiguration.of(DiskId.of("zone", "disk"));
        comparePersistentDiskConfiguration(PersistentDiskConfiguration.of(DiskId.of("project", "zone", "disk")), persistentConfiguration.setProjectId("project"));
        ScratchDiskConfiguration scratchDiskConfiguration = ScratchDiskConfiguration.of(DiskTypeId.of("zone", "diskType"));
        compareScratchDiskConfiguration(ScratchDiskConfiguration.of(DiskTypeId.of("project", "zone", "diskType")), scratchDiskConfiguration.setProjectId("project"));
        CreateDiskConfiguration createDiskConfiguration = AttachedDiskTest.CREATE_DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of("zone", "diskType")).setSourceImage(ImageId.of("image")).build();
        compareCreateDiskConfiguration(AttachedDiskTest.CREATE_DISK_CONFIGURATION, createDiskConfiguration.setProjectId("project"));
    }

    @Test
    public void testSetProjectId() {
        PersistentDiskConfiguration persistentConfiguration = PersistentDiskConfiguration.of(DiskId.of("zone", "disk"));
        PersistentDiskConfiguration persistentConfigurationWithProject = PersistentDiskConfiguration.of(DiskId.of("project", "zone", "disk"));
        AttachedDisk attachedDisk = AttachedDisk.of(persistentConfiguration);
        compareAttachedDisk(AttachedDisk.of(persistentConfigurationWithProject), attachedDisk.setProjectId("project"));
        ScratchDiskConfiguration scratchDiskConfiguration = ScratchDiskConfiguration.of(DiskTypeId.of("zone", "diskType"));
        ScratchDiskConfiguration scratchDiskConfigurationWithProject = ScratchDiskConfiguration.of(DiskTypeId.of("project", "zone", "diskType"));
        compareAttachedDisk(AttachedDisk.of(scratchDiskConfigurationWithProject), AttachedDisk.of(scratchDiskConfiguration).setProjectId("project"));
        CreateDiskConfiguration createDiskConfiguration = CreateDiskConfiguration.of(ImageId.of("image"));
        CreateDiskConfiguration createDiskConfigurationWithProject = CreateDiskConfiguration.of(ImageId.of("project", "image"));
        compareAttachedDisk(AttachedDisk.of(createDiskConfigurationWithProject), AttachedDisk.of(createDiskConfiguration).setProjectId("project"));
        createDiskConfiguration = AttachedDiskTest.CREATE_DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of("zone", "diskType")).setSourceImage(ImageId.of("image")).build();
        compareAttachedDisk(AttachedDisk.of(AttachedDiskTest.CREATE_DISK_CONFIGURATION), AttachedDisk.of(createDiskConfiguration).setProjectId("project"));
    }
}

