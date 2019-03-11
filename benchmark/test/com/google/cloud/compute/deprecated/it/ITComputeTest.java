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
package com.google.cloud.compute.deprecated.it;


import AttachedDisk.AttachedDiskConfiguration.Type.PERSISTENT;
import AttachedDisk.CreateDiskConfiguration;
import AttachedDisk.PersistentDiskConfiguration;
import AttachedDisk.ScratchDiskConfiguration;
import Compute.AddressAggregatedListOption;
import Compute.AddressField.ADDRESS;
import Compute.AddressFilter;
import Compute.AddressListOption;
import Compute.AddressOption;
import Compute.DiskAggregatedListOption;
import Compute.DiskField.SIZE_GB;
import Compute.DiskFilter;
import Compute.DiskListOption;
import Compute.DiskOption;
import Compute.DiskTypeField.CREATION_TIMESTAMP;
import Compute.DiskTypeField.DEFAULT_DISK_SIZE_GB;
import Compute.DiskTypeFilter;
import Compute.DiskTypeListOption;
import Compute.DiskTypeOption;
import Compute.ImageField.ARCHIVE_SIZE_BYTES;
import Compute.ImageFilter;
import Compute.ImageListOption;
import Compute.ImageOption;
import Compute.InstanceOption;
import Compute.LicenseOption;
import Compute.MachineTypeAggregatedListOption;
import Compute.MachineTypeField.GUEST_CPUS;
import Compute.MachineTypeField.ID;
import Compute.MachineTypeFilter;
import Compute.MachineTypeListOption;
import Compute.MachineTypeOption;
import Compute.OperationField.STATUS;
import Compute.OperationFilter;
import Compute.OperationListOption;
import Compute.RegionField.NAME;
import Compute.RegionFilter;
import Compute.RegionListOption;
import Compute.RegionOption;
import Compute.SnapshotFilter;
import Compute.SnapshotListOption;
import Compute.SnapshotOption;
import Compute.ZoneFilter;
import Compute.ZoneListOption;
import Compute.ZoneOption;
import DeprecationStatus.Status.DEPRECATED;
import DiskConfiguration.Type.IMAGE;
import DiskConfiguration.Type.SNAPSHOT;
import DiskConfiguration.Type.STANDARD;
import DiskInfo.CreationStatus.READY;
import ImageConfiguration.Type.DISK;
import InstanceInfo.Status.RUNNING;
import InstanceInfo.Status.TERMINATED;
import NetworkInterface.AccessConfig;
import Operation.Status.DONE;
import SchedulingOptions.Maintenance.TERMINATE;
import com.google.api.gax.paging.Page;
import com.google.cloud.compute.deprecated.Address;
import com.google.cloud.compute.deprecated.AddressId;
import com.google.cloud.compute.deprecated.AddressInfo;
import com.google.cloud.compute.deprecated.AttachedDisk;
import com.google.cloud.compute.deprecated.Compute;
import com.google.cloud.compute.deprecated.DeprecationStatus;
import com.google.cloud.compute.deprecated.Disk;
import com.google.cloud.compute.deprecated.DiskConfiguration;
import com.google.cloud.compute.deprecated.DiskId;
import com.google.cloud.compute.deprecated.DiskImageConfiguration;
import com.google.cloud.compute.deprecated.DiskInfo;
import com.google.cloud.compute.deprecated.DiskType;
import com.google.cloud.compute.deprecated.DiskTypeId;
import com.google.cloud.compute.deprecated.GlobalAddressId;
import com.google.cloud.compute.deprecated.Image;
import com.google.cloud.compute.deprecated.ImageDiskConfiguration;
import com.google.cloud.compute.deprecated.ImageId;
import com.google.cloud.compute.deprecated.ImageInfo;
import com.google.cloud.compute.deprecated.Instance;
import com.google.cloud.compute.deprecated.InstanceId;
import com.google.cloud.compute.deprecated.InstanceInfo;
import com.google.cloud.compute.deprecated.License;
import com.google.cloud.compute.deprecated.LicenseId;
import com.google.cloud.compute.deprecated.MachineType;
import com.google.cloud.compute.deprecated.MachineTypeId;
import com.google.cloud.compute.deprecated.NetworkId;
import com.google.cloud.compute.deprecated.NetworkInterface;
import com.google.cloud.compute.deprecated.Operation;
import com.google.cloud.compute.deprecated.Region;
import com.google.cloud.compute.deprecated.RegionAddressId;
import com.google.cloud.compute.deprecated.RegionOperationId;
import com.google.cloud.compute.deprecated.SchedulingOptions;
import com.google.cloud.compute.deprecated.Snapshot;
import com.google.cloud.compute.deprecated.SnapshotDiskConfiguration;
import com.google.cloud.compute.deprecated.SnapshotId;
import com.google.cloud.compute.deprecated.SnapshotInfo;
import com.google.cloud.compute.deprecated.StandardDiskConfiguration;
import com.google.cloud.compute.deprecated.StorageImageConfiguration;
import com.google.cloud.compute.deprecated.Zone;
import com.google.cloud.compute.deprecated.ZoneOperationId;
import com.google.cloud.compute.deprecated.testing.RemoteComputeHelper;
import com.google.cloud.compute.deprecated.testing.ResourceCleaner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


@Ignore("Intermittent errors in this deprecated integration test.")
public class ITComputeTest {
    private static final String REGION = "us-central1";

    private static final String ZONE = "us-central1-a";

    private static final String DISK_TYPE = "local-ssd";

    private static final String MACHINE_TYPE = "f1-micro";

    private static final LicenseId LICENSE_ID = LicenseId.of("ubuntu-os-cloud", "ubuntu-1404-trusty");

    private static final String BASE_RESOURCE_NAME = RemoteComputeHelper.baseResourceName();

    private static final ImageId IMAGE_ID = ImageId.of("debian-cloud", "debian-8-jessie-v20160219");

    private static final String IMAGE_PROJECT = "debian-cloud";

    private static Compute compute;

    private static ResourceCleaner resourceCleaner;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testGetDiskType() {
        DiskType diskType = ITComputeTest.compute.getDiskType(ITComputeTest.ZONE, ITComputeTest.DISK_TYPE);
        // assertNotNull(diskType.getGeneratedId());
        Assert.assertEquals(ITComputeTest.ZONE, diskType.getDiskTypeId().getZone());
        Assert.assertEquals(ITComputeTest.DISK_TYPE, diskType.getDiskTypeId().getType());
        Assert.assertNotNull(diskType.getCreationTimestamp());
        Assert.assertNotNull(diskType.getDescription());
        Assert.assertNotNull(diskType.getValidDiskSize());
        Assert.assertNotNull(diskType.getDefaultDiskSizeGb());
    }

    @Test
    public void testGetDiskTypeWithSelectedFields() {
        DiskType diskType = ITComputeTest.compute.getDiskType(ITComputeTest.ZONE, ITComputeTest.DISK_TYPE, DiskTypeOption.fields(CREATION_TIMESTAMP));
        // assertNotNull(diskType.getGeneratedId());
        Assert.assertEquals(ITComputeTest.ZONE, diskType.getDiskTypeId().getZone());
        Assert.assertEquals(ITComputeTest.DISK_TYPE, diskType.getDiskTypeId().getType());
        Assert.assertNotNull(diskType.getCreationTimestamp());
        Assert.assertNull(diskType.getDescription());
        Assert.assertNull(diskType.getValidDiskSize());
        Assert.assertNull(diskType.getDefaultDiskSizeGb());
    }

    @Test
    public void testListDiskTypes() {
        Page<DiskType> diskPage = ITComputeTest.compute.listDiskTypes(ITComputeTest.ZONE);
        Iterator<DiskType> diskTypeIterator = diskPage.iterateAll().iterator();
        Assert.assertTrue(diskTypeIterator.hasNext());
        while (diskTypeIterator.hasNext()) {
            DiskType diskType = diskTypeIterator.next();
            // assertNotNull(diskType.getGeneratedId());
            Assert.assertNotNull(diskType.getDiskTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, diskType.getDiskTypeId().getZone());
            Assert.assertNotNull(diskType.getCreationTimestamp());
            Assert.assertNotNull(diskType.getDescription());
            Assert.assertNotNull(diskType.getValidDiskSize());
            Assert.assertNotNull(diskType.getDefaultDiskSizeGb());
        } 
    }

    @Test
    public void testListDiskTypesWithSelectedFields() {
        Page<DiskType> diskPage = ITComputeTest.compute.listDiskTypes(ITComputeTest.ZONE, DiskTypeListOption.fields(CREATION_TIMESTAMP));
        Iterator<DiskType> diskTypeIterator = diskPage.iterateAll().iterator();
        Assert.assertTrue(diskTypeIterator.hasNext());
        while (diskTypeIterator.hasNext()) {
            DiskType diskType = diskTypeIterator.next();
            Assert.assertNull(diskType.getGeneratedId());
            Assert.assertNotNull(diskType.getDiskTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, diskType.getDiskTypeId().getZone());
            Assert.assertNotNull(diskType.getCreationTimestamp());
            Assert.assertNull(diskType.getDescription());
            Assert.assertNull(diskType.getValidDiskSize());
            Assert.assertNull(diskType.getDefaultDiskSizeGb());
        } 
    }

    @Test
    public void testListDiskTypesWithFilter() {
        Page<DiskType> diskPage = ITComputeTest.compute.listDiskTypes(ITComputeTest.ZONE, DiskTypeListOption.filter(DiskTypeFilter.equals(DEFAULT_DISK_SIZE_GB, 375)));
        Iterator<DiskType> diskTypeIterator = diskPage.iterateAll().iterator();
        Assert.assertTrue(diskTypeIterator.hasNext());
        while (diskTypeIterator.hasNext()) {
            DiskType diskType = diskTypeIterator.next();
            // todo(mziccard): uncomment or remove once #695 is closed
            // assertNotNull(diskType.getGeneratedId());
            Assert.assertNotNull(diskType.getDiskTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, diskType.getDiskTypeId().getZone());
            Assert.assertNotNull(diskType.getCreationTimestamp());
            Assert.assertNotNull(diskType.getDescription());
            Assert.assertNotNull(diskType.getValidDiskSize());
            Assert.assertEquals(375, ((long) (diskType.getDefaultDiskSizeGb())));
        } 
    }

    @Test
    public void testGetMachineType() {
        MachineType machineType = ITComputeTest.compute.getMachineType(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE);
        Assert.assertEquals(ITComputeTest.ZONE, machineType.getMachineTypeId().getZone());
        Assert.assertEquals(ITComputeTest.MACHINE_TYPE, machineType.getMachineTypeId().getType());
        Assert.assertNotNull(machineType.getGeneratedId());
        Assert.assertNotNull(machineType.getCreationTimestamp());
        Assert.assertNotNull(machineType.getDescription());
        Assert.assertNotNull(machineType.getCpus());
        Assert.assertNotNull(machineType.getMemoryMb());
        Assert.assertNotNull(machineType.getMaximumPersistentDisks());
        Assert.assertNotNull(machineType.getMaximumPersistentDisksSizeGb());
    }

    @Test
    public void testGetMachineTypeWithSelectedFields() {
        MachineType machineType = ITComputeTest.compute.getMachineType(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE, MachineTypeOption.fields(ID));
        Assert.assertEquals(ITComputeTest.ZONE, machineType.getMachineTypeId().getZone());
        Assert.assertEquals(ITComputeTest.MACHINE_TYPE, machineType.getMachineTypeId().getType());
        Assert.assertNotNull(machineType.getGeneratedId());
        Assert.assertNull(machineType.getCreationTimestamp());
        Assert.assertNull(machineType.getDescription());
        Assert.assertNull(machineType.getCpus());
        Assert.assertNull(machineType.getMemoryMb());
        Assert.assertNull(machineType.getMaximumPersistentDisks());
        Assert.assertNull(machineType.getMaximumPersistentDisksSizeGb());
    }

    @Test
    public void testListMachineTypes() {
        Page<MachineType> machinePage = ITComputeTest.compute.listMachineTypes(ITComputeTest.ZONE);
        Iterator<MachineType> machineTypeIterator = machinePage.iterateAll().iterator();
        Assert.assertTrue(machineTypeIterator.hasNext());
        while (machineTypeIterator.hasNext()) {
            MachineType machineType = machineTypeIterator.next();
            Assert.assertNotNull(machineType.getMachineTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, machineType.getMachineTypeId().getZone());
            Assert.assertNotNull(machineType.getGeneratedId());
            Assert.assertNotNull(machineType.getCreationTimestamp());
            Assert.assertNotNull(machineType.getDescription());
            Assert.assertNotNull(machineType.getCpus());
            Assert.assertNotNull(machineType.getMemoryMb());
            Assert.assertNotNull(machineType.getMaximumPersistentDisks());
            Assert.assertNotNull(machineType.getMaximumPersistentDisksSizeGb());
        } 
    }

    @Test
    public void testListMachineTypesWithSelectedFields() {
        Page<MachineType> machinePage = ITComputeTest.compute.listMachineTypes(ITComputeTest.ZONE, MachineTypeListOption.fields(Compute.MachineTypeField.CREATION_TIMESTAMP));
        Iterator<MachineType> machineTypeIterator = machinePage.iterateAll().iterator();
        Assert.assertTrue(machineTypeIterator.hasNext());
        while (machineTypeIterator.hasNext()) {
            MachineType machineType = machineTypeIterator.next();
            Assert.assertNotNull(machineType.getMachineTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, machineType.getMachineTypeId().getZone());
            Assert.assertNull(machineType.getGeneratedId());
            Assert.assertNotNull(machineType.getCreationTimestamp());
            Assert.assertNull(machineType.getDescription());
            Assert.assertNull(machineType.getCpus());
            Assert.assertNull(machineType.getMemoryMb());
            Assert.assertNull(machineType.getMaximumPersistentDisks());
            Assert.assertNull(machineType.getMaximumPersistentDisksSizeGb());
        } 
    }

    @Test
    public void testListMachineTypesWithFilter() {
        Page<MachineType> machinePage = ITComputeTest.compute.listMachineTypes(ITComputeTest.ZONE, MachineTypeListOption.filter(MachineTypeFilter.equals(GUEST_CPUS, 2)));
        Iterator<MachineType> machineTypeIterator = machinePage.iterateAll().iterator();
        Assert.assertTrue(machineTypeIterator.hasNext());
        while (machineTypeIterator.hasNext()) {
            MachineType machineType = machineTypeIterator.next();
            Assert.assertNotNull(machineType.getMachineTypeId());
            Assert.assertEquals(ITComputeTest.ZONE, machineType.getMachineTypeId().getZone());
            Assert.assertNotNull(machineType.getGeneratedId());
            Assert.assertNotNull(machineType.getCreationTimestamp());
            Assert.assertNotNull(machineType.getDescription());
            Assert.assertNotNull(machineType.getCpus());
            Assert.assertEquals(2, ((long) (machineType.getCpus())));
            Assert.assertNotNull(machineType.getMemoryMb());
            Assert.assertNotNull(machineType.getMaximumPersistentDisks());
            Assert.assertNotNull(machineType.getMaximumPersistentDisksSizeGb());
        } 
    }

    @Test
    public void testAggregatedListMachineTypes() {
        Page<MachineType> machinePage = ITComputeTest.compute.listMachineTypes();
        Iterator<MachineType> machineTypeIterator = machinePage.iterateAll().iterator();
        Assert.assertTrue(machineTypeIterator.hasNext());
        while (machineTypeIterator.hasNext()) {
            MachineType machineType = machineTypeIterator.next();
            Assert.assertNotNull(machineType.getMachineTypeId());
            Assert.assertNotNull(machineType.getGeneratedId());
            Assert.assertNotNull(machineType.getCreationTimestamp());
            Assert.assertNotNull(machineType.getDescription());
            Assert.assertNotNull(machineType.getCpus());
            Assert.assertNotNull(machineType.getMemoryMb());
            Assert.assertNotNull(machineType.getMaximumPersistentDisks());
            Assert.assertNotNull(machineType.getMaximumPersistentDisksSizeGb());
        } 
    }

    @Test
    public void testAggregatedListMachineTypesWithFilter() {
        Page<MachineType> machinePage = ITComputeTest.compute.listMachineTypes(MachineTypeAggregatedListOption.filter(MachineTypeFilter.notEquals(GUEST_CPUS, 2)));
        Iterator<MachineType> machineTypeIterator = machinePage.iterateAll().iterator();
        Assert.assertTrue(machineTypeIterator.hasNext());
        while (machineTypeIterator.hasNext()) {
            MachineType machineType = machineTypeIterator.next();
            Assert.assertNotNull(machineType.getMachineTypeId());
            Assert.assertNotNull(machineType.getGeneratedId());
            Assert.assertNotNull(machineType.getCreationTimestamp());
            Assert.assertNotNull(machineType.getDescription());
            Assert.assertNotNull(machineType.getCpus());
            Assert.assertNotEquals(2, ((long) (machineType.getCpus())));
            Assert.assertNotNull(machineType.getMemoryMb());
            Assert.assertNotNull(machineType.getMaximumPersistentDisks());
            Assert.assertNotNull(machineType.getMaximumPersistentDisksSizeGb());
        } 
    }

    @Test
    public void testGetLicense() {
        License license = ITComputeTest.compute.getLicense(ITComputeTest.LICENSE_ID);
        Assert.assertEquals(ITComputeTest.LICENSE_ID, license.getLicenseId());
        Assert.assertNotNull(license.chargesUseFee());
    }

    @Test
    public void testGetLicenseWithSelectedFields() {
        License license = ITComputeTest.compute.getLicense(ITComputeTest.LICENSE_ID, LicenseOption.fields());
        Assert.assertEquals(ITComputeTest.LICENSE_ID, license.getLicenseId());
        Assert.assertNull(license.chargesUseFee());
    }

    @Test
    public void testGetRegion() {
        Region region = ITComputeTest.compute.getRegion(ITComputeTest.REGION);
        Assert.assertEquals(ITComputeTest.REGION, region.getRegionId().getRegion());
        Assert.assertNotNull(region.getDescription());
        Assert.assertNotNull(region.getCreationTimestamp());
        Assert.assertNotNull(region.getGeneratedId());
        Assert.assertNotNull(region.getQuotas());
        Assert.assertNotNull(region.getStatus());
        Assert.assertNotNull(region.getZones());
    }

    @Test
    public void testGetRegionWithSelectedFields() {
        Region region = ITComputeTest.compute.getRegion(ITComputeTest.REGION, RegionOption.fields(Compute.RegionField.ID));
        Assert.assertEquals(ITComputeTest.REGION, region.getRegionId().getRegion());
        Assert.assertNotNull(region.getGeneratedId());
        Assert.assertNull(region.getDescription());
        Assert.assertNull(region.getCreationTimestamp());
        Assert.assertNull(region.getQuotas());
        Assert.assertNull(region.getStatus());
        Assert.assertNull(region.getZones());
    }

    @Test
    public void testListRegions() {
        Page<Region> regionPage = ITComputeTest.compute.listRegions();
        Iterator<Region> regionIterator = regionPage.iterateAll().iterator();
        while (regionIterator.hasNext()) {
            Region region = regionIterator.next();
            Assert.assertNotNull(region.getRegionId());
            Assert.assertNotNull(region.getDescription());
            Assert.assertNotNull(region.getCreationTimestamp());
            Assert.assertNotNull(region.getGeneratedId());
            Assert.assertNotNull(region.getQuotas());
            Assert.assertNotNull(region.getStatus());
            Assert.assertNotNull(region.getZones());
        } 
    }

    @Test
    public void testListRegionsWithSelectedFields() {
        Page<Region> regionPage = ITComputeTest.compute.listRegions(RegionListOption.fields(Compute.RegionField.ID));
        Iterator<Region> regionIterator = regionPage.iterateAll().iterator();
        while (regionIterator.hasNext()) {
            Region region = regionIterator.next();
            Assert.assertNotNull(region.getRegionId());
            Assert.assertNull(region.getDescription());
            Assert.assertNull(region.getCreationTimestamp());
            Assert.assertNotNull(region.getGeneratedId());
            Assert.assertNull(region.getQuotas());
            Assert.assertNull(region.getStatus());
            Assert.assertNull(region.getZones());
        } 
    }

    @Test
    public void testListRegionsWithFilter() {
        Page<Region> regionPage = ITComputeTest.compute.listRegions(RegionListOption.filter(RegionFilter.equals(NAME, ITComputeTest.REGION)));
        Iterator<Region> regionIterator = regionPage.iterateAll().iterator();
        Assert.assertEquals(ITComputeTest.REGION, regionIterator.next().getRegionId().getRegion());
        Assert.assertFalse(regionIterator.hasNext());
    }

    @Test
    public void testGetZone() {
        Zone zone = ITComputeTest.compute.getZone(ITComputeTest.ZONE);
        Assert.assertEquals(ITComputeTest.ZONE, zone.getZoneId().getZone());
        Assert.assertNotNull(zone.getGeneratedId());
        Assert.assertNotNull(zone.getCreationTimestamp());
        Assert.assertNotNull(zone.getDescription());
        Assert.assertNotNull(zone.getStatus());
        Assert.assertNotNull(zone.getRegion());
    }

    @Test
    public void testGetZoneWithSelectedFields() {
        Zone zone = ITComputeTest.compute.getZone(ITComputeTest.ZONE, ZoneOption.fields(Compute.ZoneField.ID));
        Assert.assertEquals(ITComputeTest.ZONE, zone.getZoneId().getZone());
        Assert.assertNotNull(zone.getGeneratedId());
        Assert.assertNull(zone.getCreationTimestamp());
        Assert.assertNull(zone.getDescription());
        Assert.assertNull(zone.getStatus());
        Assert.assertNull(zone.getRegion());
    }

    @Test
    public void testListZones() {
        Page<Zone> zonePage = ITComputeTest.compute.listZones();
        Iterator<Zone> zoneIterator = zonePage.iterateAll().iterator();
        while (zoneIterator.hasNext()) {
            Zone zone = zoneIterator.next();
            Assert.assertNotNull(zone.getZoneId());
            Assert.assertNotNull(zone.getGeneratedId());
            Assert.assertNotNull(zone.getCreationTimestamp());
            Assert.assertNotNull(zone.getDescription());
            Assert.assertNotNull(zone.getStatus());
            Assert.assertNotNull(zone.getRegion());
        } 
    }

    @Test
    public void testListZonesWithSelectedFields() {
        Page<Zone> zonePage = ITComputeTest.compute.listZones(ZoneListOption.fields(Compute.ZoneField.CREATION_TIMESTAMP));
        Iterator<Zone> zoneIterator = zonePage.iterateAll().iterator();
        while (zoneIterator.hasNext()) {
            Zone zone = zoneIterator.next();
            Assert.assertNotNull(zone.getZoneId());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertNotNull(zone.getCreationTimestamp());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getStatus());
            Assert.assertNull(zone.getRegion());
        } 
    }

    @Test
    public void testListZonesWithFilter() {
        Page<Zone> zonePage = ITComputeTest.compute.listZones(ZoneListOption.filter(ZoneFilter.equals(Compute.ZoneField.NAME, ITComputeTest.ZONE)));
        Iterator<Zone> zoneIterator = zonePage.iterateAll().iterator();
        Assert.assertEquals(ITComputeTest.ZONE, zoneIterator.next().getZoneId().getZone());
        Assert.assertFalse(zoneIterator.hasNext());
    }

    @Test
    public void testListGlobalOperations() {
        Page<Operation> operationPage = ITComputeTest.compute.listGlobalOperations();
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertNotNull(operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testListGlobalOperationsWithSelectedFields() {
        Page<Operation> operationPage = ITComputeTest.compute.listGlobalOperations(OperationListOption.fields(Compute.OperationField.ID));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getTargetLink());
            Assert.assertNull(operation.getTargetId());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getStatus());
            Assert.assertNull(operation.getStatusMessage());
            Assert.assertNull(operation.getUser());
            Assert.assertNull(operation.getProgress());
            Assert.assertNull(operation.getDescription());
            Assert.assertNull(operation.getInsertTime());
            Assert.assertNull(operation.getStartTime());
            Assert.assertNull(operation.getEndTime());
            Assert.assertNull(operation.getWarnings());
            Assert.assertNull(operation.getHttpErrorMessage());
        } 
    }

    @Test
    public void testListGlobalOperationsWithFilter() {
        Page<Operation> operationPage = ITComputeTest.compute.listGlobalOperations(OperationListOption.filter(OperationFilter.equals(STATUS, "DONE")));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertEquals(DONE, operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testListRegionOperations() {
        Page<Operation> operationPage = ITComputeTest.compute.listRegionOperations(ITComputeTest.REGION);
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.REGION, operation.<RegionOperationId>getOperationId().getRegion());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertNotNull(operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testListRegionOperationsWithSelectedFields() {
        Page<Operation> operationPage = ITComputeTest.compute.listRegionOperations(ITComputeTest.REGION, OperationListOption.fields(Compute.OperationField.ID));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.REGION, operation.<RegionOperationId>getOperationId().getRegion());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getTargetLink());
            Assert.assertNull(operation.getTargetId());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getStatus());
            Assert.assertNull(operation.getStatusMessage());
            Assert.assertNull(operation.getUser());
            Assert.assertNull(operation.getProgress());
            Assert.assertNull(operation.getDescription());
            Assert.assertNull(operation.getInsertTime());
            Assert.assertNull(operation.getStartTime());
            Assert.assertNull(operation.getEndTime());
            Assert.assertNull(operation.getWarnings());
            Assert.assertNull(operation.getHttpErrorMessage());
        } 
    }

    @Test
    public void testListRegionOperationsWithFilter() {
        Page<Operation> operationPage = ITComputeTest.compute.listRegionOperations(ITComputeTest.REGION, OperationListOption.filter(OperationFilter.equals(STATUS, "DONE")));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.REGION, operation.<RegionOperationId>getOperationId().getRegion());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertEquals(DONE, operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testListZoneOperations() {
        Page<Operation> operationPage = ITComputeTest.compute.listZoneOperations(ITComputeTest.ZONE);
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.ZONE, operation.<ZoneOperationId>getOperationId().getZone());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertNotNull(operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testListZoneOperationsWithSelectedFields() {
        Page<Operation> operationPage = ITComputeTest.compute.listZoneOperations(ITComputeTest.ZONE, OperationListOption.fields(Compute.OperationField.ID));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.ZONE, operation.<ZoneOperationId>getOperationId().getZone());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getTargetLink());
            Assert.assertNull(operation.getTargetId());
            Assert.assertNull(operation.getOperationType());
            Assert.assertNull(operation.getStatus());
            Assert.assertNull(operation.getStatusMessage());
            Assert.assertNull(operation.getUser());
            Assert.assertNull(operation.getProgress());
            Assert.assertNull(operation.getDescription());
            Assert.assertNull(operation.getInsertTime());
            Assert.assertNull(operation.getStartTime());
            Assert.assertNull(operation.getEndTime());
            Assert.assertNull(operation.getWarnings());
            Assert.assertNull(operation.getHttpErrorMessage());
        } 
    }

    @Test
    public void testListZoneOperationsWithFilter() {
        Page<Operation> operationPage = ITComputeTest.compute.listZoneOperations(ITComputeTest.ZONE, OperationListOption.filter(OperationFilter.equals(STATUS, "DONE")));
        Iterator<Operation> operationIterator = operationPage.iterateAll().iterator();
        while (operationIterator.hasNext()) {
            Operation operation = operationIterator.next();
            Assert.assertNotNull(operation.getGeneratedId());
            Assert.assertNotNull(operation.getOperationId());
            Assert.assertEquals(ITComputeTest.ZONE, operation.<ZoneOperationId>getOperationId().getZone());
            // todo(mziccard): uncomment or remove once #727 is closed
            // assertNotNull(operation.getCreationTimestamp());
            Assert.assertNotNull(operation.getOperationType());
            Assert.assertEquals(DONE, operation.getStatus());
            Assert.assertNotNull(operation.getUser());
        } 
    }

    @Test
    public void testCreateGetAndDeleteRegionAddress() throws InterruptedException, TimeoutException {
        String name = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-region-address";
        AddressId addressId = RegionAddressId.of(ITComputeTest.REGION, name);
        AddressInfo addressInfo = AddressInfo.of(addressId);
        Operation operation = ITComputeTest.compute.create(addressInfo);
        operation.waitFor();
        // test get
        Address remoteAddress = ITComputeTest.compute.getAddress(addressId);
        ITComputeTest.resourceCleaner.add(addressId);
        Assert.assertNotNull(remoteAddress);
        Assert.assertTrue(((remoteAddress.getAddressId()) instanceof RegionAddressId));
        Assert.assertEquals(ITComputeTest.REGION, remoteAddress.<RegionAddressId>getAddressId().getRegion());
        Assert.assertEquals(addressId.getAddress(), remoteAddress.getAddressId().getAddress());
        Assert.assertNotNull(remoteAddress.getAddress());
        Assert.assertNotNull(remoteAddress.getCreationTimestamp());
        Assert.assertNotNull(remoteAddress.getGeneratedId());
        Assert.assertNotNull(remoteAddress.getStatus());
        // test get with selected fields
        remoteAddress = ITComputeTest.compute.getAddress(addressId, AddressOption.fields());
        Assert.assertNotNull(remoteAddress);
        Assert.assertTrue(((remoteAddress.getAddressId()) instanceof RegionAddressId));
        Assert.assertEquals(ITComputeTest.REGION, remoteAddress.<RegionAddressId>getAddressId().getRegion());
        Assert.assertEquals(addressId.getAddress(), remoteAddress.getAddressId().getAddress());
        Assert.assertNull(remoteAddress.getAddress());
        Assert.assertNull(remoteAddress.getCreationTimestamp());
        Assert.assertNull(remoteAddress.getGeneratedId());
        operation = remoteAddress.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(addressId);
        Assert.assertNull(ITComputeTest.compute.getAddress(addressId));
    }

    @Test
    public void testListRegionAddresses() throws InterruptedException, TimeoutException {
        String prefix = (ITComputeTest.BASE_RESOURCE_NAME) + "list-region-address";
        String[] addressNames = new String[]{ prefix + "1", prefix + "2" };
        AddressId firstAddressId = RegionAddressId.of(ITComputeTest.REGION, addressNames[0]);
        AddressId secondAddressId = RegionAddressId.of(ITComputeTest.REGION, addressNames[1]);
        Operation firstOperation = ITComputeTest.compute.create(AddressInfo.of(firstAddressId));
        Operation secondOperation = ITComputeTest.compute.create(AddressInfo.of(secondAddressId));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstAddressId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondAddressId);
        Set<String> addressSet = ImmutableSet.copyOf(addressNames);
        // test list
        Compute.AddressFilter filter = AddressFilter.equals(Compute.AddressField.NAME, (prefix + "\\d"));
        Page<Address> addressPage = ITComputeTest.compute.listRegionAddresses(ITComputeTest.REGION, AddressListOption.filter(filter));
        Iterator<Address> addressIterator = addressPage.iterateAll().iterator();
        int count = 0;
        while (addressIterator.hasNext()) {
            Address address = addressIterator.next();
            Assert.assertNotNull(address.getAddressId());
            Assert.assertTrue(((address.getAddressId()) instanceof RegionAddressId));
            Assert.assertEquals(ITComputeTest.REGION, address.<RegionAddressId>getAddressId().getRegion());
            Assert.assertTrue(addressSet.contains(address.getAddressId().getAddress()));
            Assert.assertNotNull(address.getAddress());
            Assert.assertNotNull(address.getCreationTimestamp());
            Assert.assertNotNull(address.getGeneratedId());
            count++;
        } 
        Assert.assertEquals(2, count);
        // test list with selected fields
        count = 0;
        addressPage = ITComputeTest.compute.listRegionAddresses(ITComputeTest.REGION, AddressListOption.filter(filter), AddressListOption.fields(ADDRESS));
        addressIterator = addressPage.iterateAll().iterator();
        while (addressIterator.hasNext()) {
            Address address = addressIterator.next();
            Assert.assertTrue(((address.getAddressId()) instanceof RegionAddressId));
            Assert.assertEquals(ITComputeTest.REGION, address.<RegionAddressId>getAddressId().getRegion());
            Assert.assertTrue(addressSet.contains(address.getAddressId().getAddress()));
            Assert.assertNotNull(address.getAddress());
            Assert.assertNull(address.getCreationTimestamp());
            Assert.assertNull(address.getGeneratedId());
            Assert.assertNull(address.getStatus());
            Assert.assertNull(address.getUsage());
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void testAggregatedListAddresses() throws InterruptedException, TimeoutException {
        String prefix = (ITComputeTest.BASE_RESOURCE_NAME) + "aggregated-list-address";
        String[] addressNames = new String[]{ prefix + "1", prefix + "2" };
        AddressId firstAddressId = RegionAddressId.of(ITComputeTest.REGION, addressNames[0]);
        AddressId secondAddressId = GlobalAddressId.of(ITComputeTest.REGION, addressNames[1]);
        Operation firstOperation = ITComputeTest.compute.create(AddressInfo.of(firstAddressId));
        Operation secondOperation = ITComputeTest.compute.create(AddressInfo.of(secondAddressId));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstAddressId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondAddressId);
        Set<String> addressSet = ImmutableSet.copyOf(addressNames);
        Compute.AddressFilter filter = AddressFilter.equals(Compute.AddressField.NAME, (prefix + "\\d"));
        Page<Address> addressPage = ITComputeTest.compute.listAddresses(AddressAggregatedListOption.filter(filter));
        Iterator<Address> addressIterator = addressPage.iterateAll().iterator();
        int count = 0;
        while (addressIterator.hasNext()) {
            Address address = addressIterator.next();
            Assert.assertNotNull(address.getAddressId());
            Assert.assertTrue(addressSet.contains(address.getAddressId().getAddress()));
            Assert.assertNotNull(address.getAddress());
            Assert.assertNotNull(address.getCreationTimestamp());
            Assert.assertNotNull(address.getGeneratedId());
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCreateGetAndDeleteGlobalAddress() throws InterruptedException, TimeoutException {
        String name = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-global-address";
        AddressId addressId = GlobalAddressId.of(name);
        AddressInfo addressInfo = AddressInfo.of(addressId);
        Operation operation = ITComputeTest.compute.create(addressInfo);
        operation.waitFor();
        // test get
        Address remoteAddress = ITComputeTest.compute.getAddress(addressId);
        ITComputeTest.resourceCleaner.add(addressId);
        Assert.assertNotNull(remoteAddress);
        Assert.assertTrue(((remoteAddress.getAddressId()) instanceof GlobalAddressId));
        Assert.assertEquals(addressId.getAddress(), remoteAddress.getAddressId().getAddress());
        Assert.assertNotNull(remoteAddress.getAddress());
        Assert.assertNotNull(remoteAddress.getCreationTimestamp());
        Assert.assertNotNull(remoteAddress.getGeneratedId());
        Assert.assertNotNull(remoteAddress.getStatus());
        // test get with selected fields
        remoteAddress = ITComputeTest.compute.getAddress(addressId, AddressOption.fields());
        Assert.assertNotNull(remoteAddress);
        Assert.assertTrue(((remoteAddress.getAddressId()) instanceof GlobalAddressId));
        Assert.assertEquals(addressId.getAddress(), remoteAddress.getAddressId().getAddress());
        Assert.assertNull(remoteAddress.getAddress());
        Assert.assertNull(remoteAddress.getCreationTimestamp());
        Assert.assertNull(remoteAddress.getGeneratedId());
        operation = remoteAddress.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(addressId);
        Assert.assertNull(ITComputeTest.compute.getAddress(addressId));
    }

    @Test
    public void testListGlobalAddresses() throws InterruptedException, TimeoutException {
        String prefix = (ITComputeTest.BASE_RESOURCE_NAME) + "list-global-address";
        String[] addressNames = new String[]{ prefix + "1", prefix + "2" };
        AddressId firstAddressId = GlobalAddressId.of(addressNames[0]);
        AddressId secondAddressId = GlobalAddressId.of(addressNames[1]);
        Operation firstOperation = ITComputeTest.compute.create(AddressInfo.of(firstAddressId));
        Operation secondOperation = ITComputeTest.compute.create(AddressInfo.of(secondAddressId));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstAddressId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondAddressId);
        Set<String> addressSet = ImmutableSet.copyOf(addressNames);
        // test list
        Compute.AddressFilter filter = AddressFilter.equals(Compute.AddressField.NAME, (prefix + "\\d"));
        Page<Address> addressPage = ITComputeTest.compute.listGlobalAddresses(AddressListOption.filter(filter));
        Iterator<Address> addressIterator = addressPage.iterateAll().iterator();
        int count = 0;
        while (addressIterator.hasNext()) {
            Address address = addressIterator.next();
            Assert.assertNotNull(address.getAddressId());
            Assert.assertTrue(((address.getAddressId()) instanceof GlobalAddressId));
            Assert.assertTrue(addressSet.contains(address.getAddressId().getAddress()));
            Assert.assertNotNull(address.getAddress());
            Assert.assertNotNull(address.getCreationTimestamp());
            Assert.assertNotNull(address.getGeneratedId());
            count++;
        } 
        Assert.assertEquals(2, count);
        // test list with selected fields
        count = 0;
        addressPage = ITComputeTest.compute.listGlobalAddresses(AddressListOption.filter(filter), AddressListOption.fields(ADDRESS));
        addressIterator = addressPage.iterateAll().iterator();
        while (addressIterator.hasNext()) {
            Address address = addressIterator.next();
            Assert.assertTrue(((address.getAddressId()) instanceof GlobalAddressId));
            Assert.assertTrue(addressSet.contains(address.getAddressId().getAddress()));
            Assert.assertNotNull(address.getAddress());
            Assert.assertNull(address.getCreationTimestamp());
            Assert.assertNull(address.getGeneratedId());
            Assert.assertNull(address.getStatus());
            Assert.assertNull(address.getUsage());
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCreateGetResizeAndDeleteStandardDisk() throws InterruptedException, TimeoutException {
        String name = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-standard-disk";
        DiskId diskId = DiskId.of(ITComputeTest.ZONE, name);
        DiskInfo diskInfo = DiskInfo.of(diskId, StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"), 100L));
        Operation operation = ITComputeTest.compute.create(diskInfo);
        operation.waitFor();
        // test get
        Disk remoteDisk = ITComputeTest.compute.getDisk(diskId);
        ITComputeTest.resourceCleaner.add(diskId);
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(diskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertNotNull(remoteDisk.getCreationTimestamp());
        Assert.assertNotNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof StandardDiskConfiguration));
        StandardDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(100L, ((long) (remoteConfiguration.getSizeGb())));
        Assert.assertEquals("pd-ssd", remoteConfiguration.getDiskType().getType());
        Assert.assertEquals(STANDARD, remoteConfiguration.getType());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        operation = remoteDisk.resize(200L);
        operation.waitFor();
        // test resize and get with selected fields
        remoteDisk = ITComputeTest.compute.getDisk(diskId, DiskOption.fields(SIZE_GB));
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(diskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertNull(remoteDisk.getCreationTimestamp());
        Assert.assertNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof StandardDiskConfiguration));
        remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(200L, ((long) (remoteConfiguration.getSizeGb())));
        Assert.assertEquals("pd-ssd", remoteConfiguration.getDiskType().getType());
        Assert.assertEquals(STANDARD, remoteConfiguration.getType());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        operation = remoteDisk.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(diskId);
        Assert.assertNull(ITComputeTest.compute.getDisk(diskId));
    }

    @Test
    public void testCreateGetAndDeleteImageDisk() throws InterruptedException, TimeoutException {
        String name = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-image-disk";
        DiskId diskId = DiskId.of(ITComputeTest.ZONE, name);
        DiskInfo diskInfo = DiskInfo.of(diskId, ImageDiskConfiguration.of(ITComputeTest.IMAGE_ID));
        Operation operation = ITComputeTest.compute.create(diskInfo);
        operation.waitFor();
        // test get
        Disk remoteDisk = ITComputeTest.compute.getDisk(diskId);
        ITComputeTest.resourceCleaner.add(diskId);
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(diskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertEquals(READY, remoteDisk.getCreationStatus());
        Assert.assertNotNull(remoteDisk.getCreationTimestamp());
        Assert.assertNotNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof ImageDiskConfiguration));
        ImageDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(ITComputeTest.IMAGE_ID, remoteConfiguration.getSourceImage());
        Assert.assertNotNull(remoteConfiguration.getSourceImageId());
        Assert.assertEquals(IMAGE, remoteConfiguration.getType());
        Assert.assertNotNull(remoteConfiguration.getSizeGb());
        Assert.assertEquals("pd-standard", remoteConfiguration.getDiskType().getType());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        // test get with selected fields
        remoteDisk = ITComputeTest.compute.getDisk(diskId, DiskOption.fields());
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(diskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertNull(remoteDisk.getCreationTimestamp());
        Assert.assertNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof ImageDiskConfiguration));
        remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(ITComputeTest.IMAGE_ID, remoteConfiguration.getSourceImage());
        Assert.assertNull(remoteConfiguration.getSourceImageId());
        Assert.assertEquals(IMAGE, remoteConfiguration.getType());
        Assert.assertNull(remoteConfiguration.getSizeGb());
        Assert.assertEquals("pd-standard", remoteConfiguration.getDiskType().getType());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        operation = remoteDisk.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(diskId);
        Assert.assertNull(ITComputeTest.compute.getDisk(diskId));
    }

    @Test
    public void testCreateGetAndDeleteSnapshotAndSnapshotDisk() throws InterruptedException, TimeoutException {
        String diskName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-snapshot-disk1";
        String snapshotDiskName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-snapshot-disk2";
        DiskId diskId = DiskId.of(ITComputeTest.ZONE, diskName);
        DiskId snapshotDiskId = DiskId.of(ITComputeTest.ZONE, snapshotDiskName);
        String snapshotName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-snapshot";
        DiskInfo diskInfo = DiskInfo.of(diskId, StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"), 100L));
        Operation operation = ITComputeTest.compute.create(diskInfo);
        operation.waitFor();
        Disk remoteDisk = ITComputeTest.compute.getDisk(diskId);
        ITComputeTest.resourceCleaner.add(diskId);
        operation = remoteDisk.createSnapshot(snapshotName);
        operation.waitFor();
        // test get snapshot with selected fields
        Snapshot snapshot = ITComputeTest.compute.getSnapshot(snapshotName, SnapshotOption.fields(Compute.SnapshotField.CREATION_TIMESTAMP));
        ITComputeTest.resourceCleaner.add(snapshot.getSnapshotId());
        Assert.assertNull(snapshot.getGeneratedId());
        Assert.assertNotNull(snapshot.getSnapshotId());
        Assert.assertNotNull(snapshot.getCreationTimestamp());
        Assert.assertNull(snapshot.getDescription());
        Assert.assertNull(snapshot.getStatus());
        Assert.assertNull(snapshot.getDiskSizeGb());
        Assert.assertNull(snapshot.getLicenses());
        Assert.assertNull(snapshot.getSourceDisk());
        Assert.assertNull(snapshot.getSourceDiskId());
        Assert.assertNull(snapshot.getStorageBytes());
        Assert.assertNull(snapshot.getStorageBytesStatus());
        // test get snapshot
        snapshot = ITComputeTest.compute.getSnapshot(snapshotName);
        Assert.assertNotNull(snapshot.getGeneratedId());
        Assert.assertNotNull(snapshot.getSnapshotId());
        Assert.assertNotNull(snapshot.getCreationTimestamp());
        Assert.assertNotNull(snapshot.getStatus());
        Assert.assertEquals(100L, ((long) (snapshot.getDiskSizeGb())));
        Assert.assertEquals(diskName, snapshot.getSourceDisk().getDisk());
        Assert.assertNotNull(snapshot.getSourceDiskId());
        Assert.assertNotNull(snapshot.getStorageBytes());
        Assert.assertNotNull(snapshot.getStorageBytesStatus());
        remoteDisk.delete();
        ITComputeTest.resourceCleaner.remove(diskId);
        diskInfo = DiskInfo.of(snapshotDiskId, SnapshotDiskConfiguration.of(SnapshotId.of(snapshotName)));
        operation = ITComputeTest.compute.create(diskInfo);
        operation.waitFor();
        // test get disk
        remoteDisk = ITComputeTest.compute.getDisk(snapshotDiskId);
        ITComputeTest.resourceCleaner.add(snapshotDiskId);
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(snapshotDiskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertEquals(READY, remoteDisk.getCreationStatus());
        Assert.assertNotNull(remoteDisk.getCreationTimestamp());
        Assert.assertNotNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof SnapshotDiskConfiguration));
        SnapshotDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(SNAPSHOT, remoteConfiguration.getType());
        Assert.assertEquals(snapshotName, remoteConfiguration.getSourceSnapshot().getSnapshot());
        Assert.assertEquals(100L, ((long) (remoteConfiguration.getSizeGb())));
        Assert.assertEquals("pd-standard", remoteConfiguration.getDiskType().getType());
        Assert.assertNotNull(remoteConfiguration.getSourceSnapshotId());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        // test get disk with selected fields
        remoteDisk = ITComputeTest.compute.getDisk(snapshotDiskId, DiskOption.fields());
        Assert.assertNotNull(remoteDisk);
        Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
        Assert.assertEquals(snapshotDiskId.getDisk(), remoteDisk.getDiskId().getDisk());
        Assert.assertNull(remoteDisk.getCreationStatus());
        Assert.assertNull(remoteDisk.getCreationTimestamp());
        Assert.assertNull(remoteDisk.getGeneratedId());
        Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof SnapshotDiskConfiguration));
        remoteConfiguration = remoteDisk.getConfiguration();
        Assert.assertEquals(SNAPSHOT, remoteConfiguration.getType());
        Assert.assertEquals(snapshotName, remoteConfiguration.getSourceSnapshot().getSnapshot());
        Assert.assertNull(remoteConfiguration.getSizeGb());
        Assert.assertEquals("pd-standard", remoteConfiguration.getDiskType().getType());
        Assert.assertNull(remoteDisk.<SnapshotDiskConfiguration>getConfiguration().getSourceSnapshotId());
        Assert.assertNull(remoteDisk.getLastAttachTimestamp());
        Assert.assertNull(remoteDisk.getLastDetachTimestamp());
        operation = remoteDisk.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(snapshotDiskId);
        Assert.assertNull(ITComputeTest.compute.getDisk(snapshotDiskId));
        operation = snapshot.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(snapshot.getSnapshotId());
        Assert.assertNull(ITComputeTest.compute.getSnapshot(snapshotName));
    }

    @Test
    public void testListDisksAndSnapshots() throws InterruptedException, TimeoutException {
        String prefix = (ITComputeTest.BASE_RESOURCE_NAME) + "list-disks-and-snapshots-disk";
        String[] diskNames = new String[]{ prefix + "1", prefix + "2" };
        DiskId firstDiskId = DiskId.of(ITComputeTest.ZONE, diskNames[0]);
        DiskId secondDiskId = DiskId.of(ITComputeTest.ZONE, diskNames[1]);
        DiskConfiguration configuration = StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"), 100L);
        Operation firstOperation = ITComputeTest.compute.create(DiskInfo.of(firstDiskId, configuration));
        Operation secondOperation = ITComputeTest.compute.create(DiskInfo.of(secondDiskId, configuration));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstDiskId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondDiskId);
        Set<String> diskSet = ImmutableSet.copyOf(diskNames);
        // test list disks
        Compute.DiskFilter diskFilter = DiskFilter.equals(Compute.DiskField.NAME, (prefix + "\\d"));
        Page<Disk> diskPage = ITComputeTest.compute.listDisks(ITComputeTest.ZONE, DiskListOption.filter(diskFilter));
        Iterator<Disk> diskIterator = diskPage.iterateAll().iterator();
        int count = 0;
        while (diskIterator.hasNext()) {
            Disk remoteDisk = diskIterator.next();
            Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
            Assert.assertTrue(diskSet.contains(remoteDisk.getDiskId().getDisk()));
            Assert.assertEquals(READY, remoteDisk.getCreationStatus());
            Assert.assertNotNull(remoteDisk.getCreationTimestamp());
            Assert.assertNotNull(remoteDisk.getGeneratedId());
            Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof StandardDiskConfiguration));
            StandardDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
            Assert.assertEquals(100L, ((long) (remoteConfiguration.getSizeGb())));
            Assert.assertEquals("pd-ssd", remoteConfiguration.getDiskType().getType());
            Assert.assertEquals(STANDARD, remoteConfiguration.getType());
            Assert.assertNull(remoteDisk.getLastAttachTimestamp());
            Assert.assertNull(remoteDisk.getLastDetachTimestamp());
            count++;
        } 
        Assert.assertEquals(2, count);
        // test list disks with selected fields
        count = 0;
        diskPage = ITComputeTest.compute.listDisks(ITComputeTest.ZONE, DiskListOption.filter(diskFilter), DiskListOption.fields(Compute.DiskField.STATUS));
        diskIterator = diskPage.iterateAll().iterator();
        while (diskIterator.hasNext()) {
            Disk remoteDisk = diskIterator.next();
            Assert.assertEquals(ITComputeTest.ZONE, remoteDisk.getDiskId().getZone());
            Assert.assertTrue(diskSet.contains(remoteDisk.getDiskId().getDisk()));
            Assert.assertEquals(READY, remoteDisk.getCreationStatus());
            Assert.assertNull(remoteDisk.getCreationTimestamp());
            Assert.assertNull(remoteDisk.getGeneratedId());
            Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof StandardDiskConfiguration));
            StandardDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
            Assert.assertNull(remoteConfiguration.getSizeGb());
            Assert.assertEquals("pd-ssd", remoteConfiguration.getDiskType().getType());
            Assert.assertEquals(STANDARD, remoteConfiguration.getType());
            Assert.assertNull(remoteDisk.getLastAttachTimestamp());
            Assert.assertNull(remoteDisk.getLastDetachTimestamp());
            count++;
        } 
        Assert.assertEquals(2, count);
        // test snapshots
        SnapshotId firstSnapshotId = SnapshotId.of(diskNames[0]);
        SnapshotId secondSnapshotId = SnapshotId.of(diskNames[1]);
        firstOperation = ITComputeTest.compute.create(SnapshotInfo.of(firstSnapshotId, firstDiskId));
        secondOperation = ITComputeTest.compute.create(SnapshotInfo.of(secondSnapshotId, secondDiskId));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstSnapshotId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondSnapshotId);
        // test list snapshots
        Compute.SnapshotFilter snapshotFilter = SnapshotFilter.equals(Compute.SnapshotField.NAME, (prefix + "\\d"));
        Page<Snapshot> snapshotPage = ITComputeTest.compute.listSnapshots(SnapshotListOption.filter(snapshotFilter));
        Iterator<Snapshot> snapshotIterator = snapshotPage.iterateAll().iterator();
        count = 0;
        while (snapshotIterator.hasNext()) {
            Snapshot remoteSnapshot = snapshotIterator.next();
            Assert.assertNotNull(remoteSnapshot.getGeneratedId());
            Assert.assertTrue(diskSet.contains(remoteSnapshot.getSnapshotId().getSnapshot()));
            Assert.assertNotNull(remoteSnapshot.getCreationTimestamp());
            Assert.assertNotNull(remoteSnapshot.getStatus());
            Assert.assertEquals(100L, ((long) (remoteSnapshot.getDiskSizeGb())));
            Assert.assertTrue(diskSet.contains(remoteSnapshot.getSourceDisk().getDisk()));
            Assert.assertNotNull(remoteSnapshot.getSourceDiskId());
            Assert.assertNotNull(remoteSnapshot.getStorageBytes());
            Assert.assertNotNull(remoteSnapshot.getStorageBytesStatus());
            count++;
        } 
        Assert.assertEquals(2, count);
        // test list snapshots with selected fields
        snapshotPage = ITComputeTest.compute.listSnapshots(SnapshotListOption.filter(snapshotFilter), SnapshotListOption.fields(Compute.SnapshotField.CREATION_TIMESTAMP));
        snapshotIterator = snapshotPage.iterateAll().iterator();
        count = 0;
        while (snapshotIterator.hasNext()) {
            Snapshot remoteSnapshot = snapshotIterator.next();
            Assert.assertNull(remoteSnapshot.getGeneratedId());
            Assert.assertTrue(diskSet.contains(remoteSnapshot.getSnapshotId().getSnapshot()));
            Assert.assertNotNull(remoteSnapshot.getCreationTimestamp());
            Assert.assertNull(remoteSnapshot.getStatus());
            Assert.assertNull(remoteSnapshot.getDiskSizeGb());
            Assert.assertNull(remoteSnapshot.getSourceDisk());
            Assert.assertNull(remoteSnapshot.getSourceDiskId());
            Assert.assertNull(remoteSnapshot.getStorageBytes());
            Assert.assertNull(remoteSnapshot.getStorageBytesStatus());
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void testAggregatedListDisks() throws InterruptedException, TimeoutException {
        String prefix = (ITComputeTest.BASE_RESOURCE_NAME) + "list-aggregated-disk";
        String[] diskZones = new String[]{ "us-central1-a", "us-east1-c" };
        String[] diskNames = new String[]{ prefix + "1", prefix + "2" };
        DiskId firstDiskId = DiskId.of(diskZones[0], diskNames[0]);
        DiskId secondDiskId = DiskId.of(diskZones[1], diskNames[1]);
        DiskConfiguration configuration = StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"), 100L);
        Operation firstOperation = ITComputeTest.compute.create(DiskInfo.of(firstDiskId, configuration));
        Operation secondOperation = ITComputeTest.compute.create(DiskInfo.of(secondDiskId, configuration));
        firstOperation.waitFor();
        ITComputeTest.resourceCleaner.add(firstDiskId);
        secondOperation.waitFor();
        ITComputeTest.resourceCleaner.add(secondDiskId);
        Set<String> zoneSet = ImmutableSet.copyOf(diskZones);
        Set<String> diskSet = ImmutableSet.copyOf(diskNames);
        Compute.DiskFilter diskFilter = DiskFilter.equals(Compute.DiskField.NAME, (prefix + "\\d"));
        Page<Disk> diskPage = ITComputeTest.compute.listDisks(DiskAggregatedListOption.filter(diskFilter));
        Iterator<Disk> diskIterator = diskPage.iterateAll().iterator();
        int count = 0;
        while (diskIterator.hasNext()) {
            Disk remoteDisk = diskIterator.next();
            Assert.assertTrue(zoneSet.contains(remoteDisk.getDiskId().getZone()));
            Assert.assertTrue(diskSet.contains(remoteDisk.getDiskId().getDisk()));
            Assert.assertEquals(READY, remoteDisk.getCreationStatus());
            Assert.assertNotNull(remoteDisk.getCreationTimestamp());
            Assert.assertNotNull(remoteDisk.getGeneratedId());
            Assert.assertTrue(((remoteDisk.getConfiguration()) instanceof StandardDiskConfiguration));
            StandardDiskConfiguration remoteConfiguration = remoteDisk.getConfiguration();
            Assert.assertEquals(100L, ((long) (remoteConfiguration.getSizeGb())));
            Assert.assertEquals("pd-ssd", remoteConfiguration.getDiskType().getType());
            Assert.assertEquals(STANDARD, remoteConfiguration.getType());
            count++;
        } 
        Assert.assertEquals(2, count);
    }

    @Test
    public void testCreateGetAndDeprecateImage() throws InterruptedException, TimeoutException {
        String diskName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-image-disk";
        String imageName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-image";
        DiskId diskId = DiskId.of(ITComputeTest.ZONE, diskName);
        ImageId imageId = ImageId.of(imageName);
        DiskInfo diskInfo = DiskInfo.of(diskId, StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"), 100L));
        Operation operation = ITComputeTest.compute.create(diskInfo);
        operation.waitFor();
        Disk remoteDisk = ITComputeTest.compute.getDisk(diskId);
        ImageInfo imageInfo = ImageInfo.of(imageId, DiskImageConfiguration.of(diskId));
        operation = ITComputeTest.compute.create(imageInfo);
        operation.waitFor();
        ITComputeTest.resourceCleaner.add(diskId);
        // test get image with selected fields
        Image image = ITComputeTest.compute.getImage(imageId, ImageOption.fields(Compute.ImageField.CREATION_TIMESTAMP));
        ITComputeTest.resourceCleaner.add(imageId);
        Assert.assertNull(image.getGeneratedId());
        Assert.assertNotNull(image.getImageId());
        Assert.assertNotNull(image.getCreationTimestamp());
        Assert.assertNull(image.getDescription());
        Assert.assertNotNull(image.getConfiguration());
        Assert.assertTrue(((image.getConfiguration()) instanceof DiskImageConfiguration));
        DiskImageConfiguration remoteConfiguration = image.getConfiguration();
        Assert.assertEquals(DISK, remoteConfiguration.getType());
        Assert.assertEquals(diskName, remoteConfiguration.getSourceDisk().getDisk());
        Assert.assertNull(image.getStatus());
        Assert.assertNull(image.getDiskSizeGb());
        Assert.assertNull(image.getLicenses());
        Assert.assertNull(image.getDeprecationStatus());
        // test get image
        image = ITComputeTest.compute.getImage(imageId);
        Assert.assertNotNull(image.getGeneratedId());
        Assert.assertNotNull(image.getImageId());
        Assert.assertNotNull(image.getCreationTimestamp());
        Assert.assertNotNull(image.getConfiguration());
        Assert.assertTrue(((image.getConfiguration()) instanceof DiskImageConfiguration));
        remoteConfiguration = image.getConfiguration();
        Assert.assertEquals(DISK, remoteConfiguration.getType());
        Assert.assertEquals(diskName, remoteConfiguration.getSourceDisk().getDisk());
        Assert.assertEquals(100L, ((long) (image.getDiskSizeGb())));
        Assert.assertNotNull(image.getStatus());
        Assert.assertNull(image.getDeprecationStatus());
        // test deprecate image
        DeprecationStatus<ImageId> deprecationStatus = DeprecationStatus.newBuilder(DEPRECATED, imageId).setDeprecated(System.currentTimeMillis()).build();
        operation = image.deprecate(deprecationStatus);
        operation.waitFor();
        image = ITComputeTest.compute.getImage(imageId);
        Assert.assertEquals(deprecationStatus, image.getDeprecationStatus());
        remoteDisk.delete();
        ITComputeTest.resourceCleaner.remove(diskId);
        operation = image.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(imageId);
        Assert.assertNull(ITComputeTest.compute.getImage(imageId));
    }

    @Test
    public void testListImages() {
        Page<Image> imagePage = ITComputeTest.compute.listImages(ITComputeTest.IMAGE_PROJECT);
        Iterator<Image> imageIterator = imagePage.iterateAll().iterator();
        int count = 0;
        while (imageIterator.hasNext()) {
            count++;
            Image image = imageIterator.next();
            Assert.assertNotNull(image.getGeneratedId());
            Assert.assertNotNull(image.getImageId());
            Assert.assertNotNull(image.getCreationTimestamp());
            Assert.assertNotNull(image.getConfiguration());
            Assert.assertNotNull(image.getStatus());
            Assert.assertNotNull(image.getDiskSizeGb());
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testListImagesWithSelectedFields() {
        Page<Image> imagePage = ITComputeTest.compute.listImages(ITComputeTest.IMAGE_PROJECT, ImageListOption.fields(Compute.ImageField.ID));
        Iterator<Image> imageIterator = imagePage.iterateAll().iterator();
        int count = 0;
        while (imageIterator.hasNext()) {
            count++;
            Image image = imageIterator.next();
            Assert.assertNotNull(image.getGeneratedId());
            Assert.assertNotNull(image.getImageId());
            Assert.assertNull(image.getCreationTimestamp());
            Assert.assertNotNull(image.getConfiguration());
            Assert.assertNull(image.getStatus());
            Assert.assertNull(image.getDiskSizeGb());
            Assert.assertNull(image.getLicenses());
            Assert.assertNull(image.getDeprecationStatus());
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testListImagesWithFilter() {
        Page<Image> imagePage = ITComputeTest.compute.listImages(ITComputeTest.IMAGE_PROJECT, ImageListOption.filter(ImageFilter.equals(ARCHIVE_SIZE_BYTES, 365056004L)));
        Iterator<Image> imageIterator = imagePage.iterateAll().iterator();
        int count = 0;
        while (imageIterator.hasNext()) {
            count++;
            Image image = imageIterator.next();
            Assert.assertNotNull(image.getGeneratedId());
            Assert.assertNotNull(image.getImageId());
            Assert.assertNotNull(image.getCreationTimestamp());
            Assert.assertNotNull(image.getConfiguration());
            Assert.assertNotNull(image.getStatus());
            Assert.assertNotNull(image.getDiskSizeGb());
            Assert.assertEquals(365056004L, ((long) (image.<StorageImageConfiguration>getConfiguration().getArchiveSizeBytes())));
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testCreateGetAndDeleteInstance() throws InterruptedException, TimeoutException {
        String instanceName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-instance";
        String addressName = (ITComputeTest.BASE_RESOURCE_NAME) + "create-and-get-instance-address";
        // Create an address to assign to the instance
        AddressId addressId = RegionAddressId.of(ITComputeTest.REGION, addressName);
        AddressInfo addressInfo = AddressInfo.of(addressId);
        Operation operation = ITComputeTest.compute.create(addressInfo);
        operation.waitFor();
        Address address = ITComputeTest.compute.getAddress(addressId);
        ITComputeTest.resourceCleaner.add(addressId);
        // Create an instance
        InstanceId instanceId = InstanceId.of(ITComputeTest.ZONE, instanceName);
        NetworkId networkId = NetworkId.of("default");
        NetworkInterface networkInterface = NetworkInterface.newBuilder(networkId).setAccessConfigurations(AccessConfig.newBuilder().setName("NAT").setNatIp(address.getAddress()).build()).build();
        AttachedDisk disk1 = AttachedDisk.of("dev0", CreateDiskConfiguration.newBuilder(ITComputeTest.IMAGE_ID).setAutoDelete(true).build());
        AttachedDisk disk2 = AttachedDisk.of("dev1", ScratchDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, ITComputeTest.DISK_TYPE)));
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(instanceId, MachineTypeId.of(ITComputeTest.ZONE, "n1-standard-1")).setAttachedDisks(disk1, disk2).setNetworkInterfaces(networkInterface).build();
        operation = ITComputeTest.compute.create(instanceInfo);
        operation.waitFor();
        // test get
        Instance remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        ITComputeTest.resourceCleaner.add(instanceId);
        Assert.assertEquals(instanceName, remoteInstance.getInstanceId().getInstance());
        Assert.assertEquals(ITComputeTest.ZONE, remoteInstance.getInstanceId().getZone());
        Assert.assertEquals(RUNNING, remoteInstance.getStatus());
        Assert.assertEquals("n1-standard-1", remoteInstance.getMachineType().getType());
        Assert.assertEquals(ITComputeTest.ZONE, remoteInstance.getMachineType().getZone());
        Assert.assertNotNull(remoteInstance.getCreationTimestamp());
        Set<String> deviceSet = ImmutableSet.of("dev0", "dev1");
        Assert.assertEquals(2, remoteInstance.getAttachedDisks().size());
        for (AttachedDisk remoteAttachedDisk : remoteInstance.getAttachedDisks()) {
            Assert.assertTrue(deviceSet.contains(remoteAttachedDisk.getDeviceName()));
        }
        Assert.assertEquals(PERSISTENT, remoteInstance.getAttachedDisks().get(0).getConfiguration().getType());
        AttachedDisk.PersistentDiskConfiguration remoteConfiguration = remoteInstance.getAttachedDisks().get(0).getConfiguration();
        Assert.assertEquals(instanceName, remoteConfiguration.getSourceDisk().getDisk());
        Assert.assertEquals(ITComputeTest.ZONE, remoteConfiguration.getSourceDisk().getZone());
        Assert.assertTrue(remoteConfiguration.boot());
        Assert.assertTrue(remoteConfiguration.autoDelete());
        Assert.assertEquals(1, remoteInstance.getNetworkInterfaces().size());
        NetworkInterface remoteNetworkInterface = remoteInstance.getNetworkInterfaces().get(0);
        Assert.assertNotNull(remoteNetworkInterface.getName());
        Assert.assertEquals("default", remoteNetworkInterface.getNetwork().getNetwork());
        List<NetworkInterface.AccessConfig> remoteAccessConfigurations = remoteNetworkInterface.getAccessConfigurations();
        Assert.assertNotNull(remoteAccessConfigurations);
        Assert.assertEquals(1, remoteAccessConfigurations.size());
        NetworkInterface.AccessConfig remoteAccessConfig = remoteAccessConfigurations.get(0);
        Assert.assertEquals(address.getAddress(), remoteAccessConfig.getNatIp());
        Assert.assertEquals("NAT", remoteAccessConfig.getName());
        Assert.assertNotNull(remoteInstance.getMetadata());
        Assert.assertNotNull(remoteInstance.getTags());
        // test get with selected fields
        remoteInstance = ITComputeTest.compute.getInstance(instanceId, InstanceOption.fields(Compute.InstanceField.CREATION_TIMESTAMP));
        Assert.assertEquals(instanceName, remoteInstance.getInstanceId().getInstance());
        Assert.assertEquals(ITComputeTest.ZONE, remoteInstance.getInstanceId().getZone());
        Assert.assertNull(remoteInstance.getMachineType());
        Assert.assertNotNull(remoteInstance.getCreationTimestamp());
        Assert.assertNull(remoteInstance.getAttachedDisks());
        Assert.assertNull(remoteInstance.getNetworkInterfaces());
        Assert.assertNull(remoteInstance.getMetadata());
        Assert.assertNull(remoteInstance.getTags());
        // test get default serial port output
        String serialPortOutput = remoteInstance.getSerialPortOutput();
        Assert.assertNotNull(serialPortOutput);
        // test get serial port output by number
        String newSerialPortOutput = remoteInstance.getSerialPortOutput(1);
        Assert.assertTrue(newSerialPortOutput.contains(serialPortOutput));
        operation = remoteInstance.delete();
        operation.waitFor();
        ITComputeTest.resourceCleaner.remove(instanceId);
        Assert.assertNull(ITComputeTest.compute.getInstance(instanceId));
    }

    @Test
    public void testStartStopAndResetInstance() throws InterruptedException, TimeoutException {
        String instanceName = (ITComputeTest.BASE_RESOURCE_NAME) + "start-stop-reset-instance";
        InstanceId instanceId = InstanceId.of(ITComputeTest.ZONE, instanceName);
        NetworkId networkId = NetworkId.of("default");
        NetworkInterface networkInterface = NetworkInterface.newBuilder(networkId).build();
        AttachedDisk disk = AttachedDisk.of("dev0", CreateDiskConfiguration.newBuilder(ITComputeTest.IMAGE_ID).setAutoDelete(true).build());
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(instanceId, MachineTypeId.of(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE)).setAttachedDisks(disk).setNetworkInterfaces(networkInterface).build();
        Operation operation = ITComputeTest.compute.create(instanceInfo);
        operation.waitFor();
        Instance remoteInstance = ITComputeTest.compute.getInstance(instanceId, InstanceOption.fields(Compute.InstanceField.STATUS));
        ITComputeTest.resourceCleaner.add(instanceId);
        Assert.assertEquals(RUNNING, remoteInstance.getStatus());
        operation = remoteInstance.stop();
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId, InstanceOption.fields(Compute.InstanceField.STATUS));
        Assert.assertEquals(TERMINATED, remoteInstance.getStatus());
        operation = remoteInstance.start();
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId, InstanceOption.fields(Compute.InstanceField.STATUS));
        Assert.assertEquals(RUNNING, remoteInstance.getStatus());
        operation = remoteInstance.reset();
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId, InstanceOption.fields(Compute.InstanceField.STATUS));
        Assert.assertEquals(RUNNING, remoteInstance.getStatus());
    }

    @Test
    public void testSetInstanceProperties() throws InterruptedException, TimeoutException {
        String instanceName = (ITComputeTest.BASE_RESOURCE_NAME) + "set-properties-instance";
        InstanceId instanceId = InstanceId.of(ITComputeTest.ZONE, instanceName);
        NetworkId networkId = NetworkId.of("default");
        NetworkInterface networkInterface = NetworkInterface.newBuilder(networkId).build();
        AttachedDisk disk = AttachedDisk.of("dev0", CreateDiskConfiguration.newBuilder(ITComputeTest.IMAGE_ID).setAutoDelete(true).build());
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(instanceId, MachineTypeId.of(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE)).setAttachedDisks(disk).setNetworkInterfaces(networkInterface).build();
        Operation operation = ITComputeTest.compute.create(instanceInfo);
        operation.waitFor();
        Instance remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        ITComputeTest.resourceCleaner.add(instanceId);
        // test set tags
        List<String> tags = ImmutableList.of("tag1", "tag2");
        operation = remoteInstance.setTags(tags);
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals(tags, remoteInstance.getTags().getValues());
        // test set metadata
        Map<String, String> metadata = ImmutableMap.of("key", "value");
        operation = remoteInstance.setMetadata(metadata);
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals(metadata, remoteInstance.getMetadata().getValues());
        // test set machine type
        operation = remoteInstance.stop();
        operation.waitFor();
        operation = remoteInstance.setMachineType(MachineTypeId.of(ITComputeTest.ZONE, "n1-standard-1"));
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals("n1-standard-1", remoteInstance.getMachineType().getType());
        Assert.assertEquals(ITComputeTest.ZONE, remoteInstance.getMachineType().getZone());
        // test set scheduling options
        SchedulingOptions options = SchedulingOptions.standard(false, TERMINATE);
        operation = remoteInstance.setSchedulingOptions(options);
        operation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals(options, remoteInstance.getSchedulingOptions());
    }

    @Test
    public void testAttachAndDetachDisk() throws InterruptedException, TimeoutException {
        String instanceName = (ITComputeTest.BASE_RESOURCE_NAME) + "attach-and-detach-disk-instance";
        String diskName = (ITComputeTest.BASE_RESOURCE_NAME) + "attach-and-detach-disk";
        InstanceId instanceId = InstanceId.of(ITComputeTest.ZONE, instanceName);
        NetworkId networkId = NetworkId.of("default");
        NetworkInterface networkInterface = NetworkInterface.newBuilder(networkId).build();
        AttachedDisk disk = AttachedDisk.of("dev0", CreateDiskConfiguration.newBuilder(ITComputeTest.IMAGE_ID).setAutoDelete(true).build());
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(instanceId, MachineTypeId.of(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE)).setAttachedDisks(disk).setNetworkInterfaces(networkInterface).build();
        Operation instanceOperation = ITComputeTest.compute.create(instanceInfo);
        DiskId diskId = DiskId.of(ITComputeTest.ZONE, diskName);
        Operation diskOperation = ITComputeTest.compute.create(DiskInfo.of(diskId, StandardDiskConfiguration.of(DiskTypeId.of(ITComputeTest.ZONE, "pd-ssd"))));
        instanceOperation.waitFor();
        diskOperation.waitFor();
        ITComputeTest.resourceCleaner.add(diskId);
        Instance remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        // test attach disk
        instanceOperation = remoteInstance.attachDisk("dev1", PersistentDiskConfiguration.newBuilder(diskId).build());
        instanceOperation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        ITComputeTest.resourceCleaner.add(instanceId);
        Set<String> deviceSet = ImmutableSet.of("dev0", "dev1");
        Assert.assertEquals(2, remoteInstance.getAttachedDisks().size());
        for (AttachedDisk remoteAttachedDisk : remoteInstance.getAttachedDisks()) {
            Assert.assertTrue(deviceSet.contains(remoteAttachedDisk.getDeviceName()));
        }
        // test set disk auto-delete
        instanceOperation = remoteInstance.setDiskAutoDelete("dev1", true);
        instanceOperation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals(2, remoteInstance.getAttachedDisks().size());
        for (AttachedDisk remoteAttachedDisk : remoteInstance.getAttachedDisks()) {
            Assert.assertTrue(deviceSet.contains(remoteAttachedDisk.getDeviceName()));
            Assert.assertTrue(remoteAttachedDisk.getConfiguration().autoDelete());
        }
        // test detach disk
        instanceOperation = remoteInstance.detachDisk("dev1");
        instanceOperation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertEquals(1, remoteInstance.getAttachedDisks().size());
        Assert.assertEquals("dev0", remoteInstance.getAttachedDisks().get(0).getDeviceName());
    }

    @Test
    public void testAddAndRemoveAccessConfig() throws InterruptedException, TimeoutException {
        String instanceName = (ITComputeTest.BASE_RESOURCE_NAME) + "add-and-remove-access-instance";
        String addressName = (ITComputeTest.BASE_RESOURCE_NAME) + "add-and-remove-access-address";
        InstanceId instanceId = InstanceId.of(ITComputeTest.ZONE, instanceName);
        NetworkId networkId = NetworkId.of("default");
        NetworkInterface networkInterface = NetworkInterface.newBuilder(networkId).build();
        AttachedDisk disk = AttachedDisk.of("dev0", CreateDiskConfiguration.newBuilder(ITComputeTest.IMAGE_ID).setAutoDelete(true).build());
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(instanceId, MachineTypeId.of(ITComputeTest.ZONE, ITComputeTest.MACHINE_TYPE)).setAttachedDisks(disk).setNetworkInterfaces(networkInterface).build();
        Operation instanceOperation = ITComputeTest.compute.create(instanceInfo);
        AddressId addressId = RegionAddressId.of(ITComputeTest.REGION, addressName);
        AddressInfo addressInfo = AddressInfo.of(addressId);
        Operation addressOperation = ITComputeTest.compute.create(addressInfo);
        addressOperation.waitFor();
        instanceOperation.waitFor();
        Address remoteAddress = ITComputeTest.compute.getAddress(addressId);
        ITComputeTest.resourceCleaner.add(addressId);
        Instance remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        ITComputeTest.resourceCleaner.add(instanceId);
        String networkInterfaceName = remoteInstance.getNetworkInterfaces().get(0).getName();
        // test add access config
        NetworkInterface.AccessConfig accessConfig = AccessConfig.newBuilder().setNatIp(remoteAddress.getAddress()).setName("NAT").build();
        instanceOperation = remoteInstance.addAccessConfig(networkInterfaceName, accessConfig);
        instanceOperation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        List<NetworkInterface.AccessConfig> accessConfigurations = remoteInstance.getNetworkInterfaces().get(0).getAccessConfigurations();
        Assert.assertEquals(1, accessConfigurations.size());
        Assert.assertEquals("NAT", accessConfigurations.get(0).getName());
        // test delete access config
        instanceOperation = remoteInstance.deleteAccessConfig(networkInterfaceName, "NAT");
        instanceOperation.waitFor();
        remoteInstance = ITComputeTest.compute.getInstance(instanceId);
        Assert.assertTrue(remoteInstance.getNetworkInterfaces().get(0).getAccessConfigurations().isEmpty());
    }
}

