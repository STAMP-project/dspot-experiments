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


import AttachedDisk.PersistentDiskConfiguration;
import Compute.AddressAggregatedListOption;
import Compute.AddressField.REGION;
import Compute.AddressFilter;
import Compute.AddressListOption;
import Compute.AddressOption;
import Compute.DiskAggregatedListOption;
import Compute.DiskField.SIZE_GB;
import Compute.DiskFilter;
import Compute.DiskListOption;
import Compute.DiskOption;
import Compute.DiskTypeAggregatedListOption;
import Compute.DiskTypeField.DESCRIPTION;
import Compute.DiskTypeField.ID;
import Compute.DiskTypeFilter;
import Compute.DiskTypeListOption;
import Compute.DiskTypeOption;
import Compute.ImageFilter;
import Compute.ImageListOption;
import Compute.ImageOption;
import Compute.InstanceAggregatedListOption;
import Compute.InstanceField.CAN_IP_FORWARD;
import Compute.InstanceFilter;
import Compute.InstanceListOption;
import Compute.InstanceOption;
import Compute.LicenseField.CHARGES_USE_FEE;
import Compute.LicenseOption;
import Compute.MachineTypeAggregatedListOption;
import Compute.MachineTypeField.MAXIMUM_PERSISTENT_DISKS;
import Compute.MachineTypeFilter;
import Compute.MachineTypeListOption;
import Compute.MachineTypeOption;
import Compute.NetworkField.IPV4_RANGE;
import Compute.NetworkFilter;
import Compute.NetworkListOption;
import Compute.NetworkOption;
import Compute.OperationField.PROGRESS;
import Compute.OperationFilter;
import Compute.OperationListOption;
import Compute.OperationOption;
import Compute.RegionFilter;
import Compute.RegionListOption;
import Compute.RegionOption;
import Compute.SnapshotField.DISK_SIZE_GB;
import Compute.SnapshotFilter;
import Compute.SnapshotListOption;
import Compute.SnapshotOption;
import Compute.SubnetworkAggregatedListOption;
import Compute.SubnetworkField.IP_CIDR_RANGE;
import Compute.SubnetworkFilter;
import Compute.SubnetworkListOption;
import Compute.SubnetworkOption;
import Compute.ZoneField.NAME;
import Compute.ZoneFilter;
import Compute.ZoneListOption;
import Compute.ZoneOption;
import ComputeRpc.Option;
import ComputeRpc.Option.FILTER;
import ComputeRpc.Option.MAX_RESULTS;
import ComputeRpc.Option.PAGE_TOKEN;
import DeprecationStatus.Status.DEPRECATED;
import DiskType.TO_PB_FUNCTION;
import Operation.OperationError;
import Operation.OperationWarning;
import Region.Quota;
import Region.Status;
import SchedulingOptions.Maintenance.MIGRATE;
import com.google.api.gax.paging.Page;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Tuple;
import com.google.cloud.compute.deprecated.NetworkInterface.AccessConfig;
import com.google.cloud.compute.deprecated.spi.ComputeRpcFactory;
import com.google.cloud.compute.deprecated.spi.v1.ComputeRpc;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static Operation.Status.DONE;
import static Zone.Status.DOWN;


public class ComputeImplTest {
    private static final String PROJECT = "project";

    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final String VALID_DISK_SIZE = "10GB-10TB";

    private static final Long DEFAULT_DISK_SIZE_GB = 10L;

    private static final DiskTypeId DISK_TYPE_ID = DiskTypeId.of("project", "zone", "diskType");

    private static final DiskType DISK_TYPE = DiskType.newBuilder().setGeneratedId(ComputeImplTest.GENERATED_ID).setDiskTypeId(ComputeImplTest.DISK_TYPE_ID).setCreationTimestamp(ComputeImplTest.CREATION_TIMESTAMP).setDescription(ComputeImplTest.DESCRIPTION).setValidDiskSize(ComputeImplTest.VALID_DISK_SIZE).setDefaultDiskSizeGb(ComputeImplTest.DEFAULT_DISK_SIZE_GB).build();

    private static final MachineTypeId MACHINE_TYPE_ID = MachineTypeId.of("project", "zone", "type");

    private static final Integer GUEST_CPUS = 1;

    private static final Integer MEMORY_MB = 2;

    private static final List<Integer> SCRATCH_DISKS = ImmutableList.of(3);

    private static final Integer MAXIMUM_PERSISTENT_DISKS = 4;

    private static final Long MAXIMUM_PERSISTENT_DISKS_SIZE_GB = 5L;

    private static final MachineType MACHINE_TYPE = MachineType.newBuilder().setGeneratedId(ComputeImplTest.GENERATED_ID).setMachineTypeId(ComputeImplTest.MACHINE_TYPE_ID).setCreationTimestamp(ComputeImplTest.CREATION_TIMESTAMP).setDescription(ComputeImplTest.DESCRIPTION).setCpus(ComputeImplTest.GUEST_CPUS).setMemoryMb(ComputeImplTest.MEMORY_MB).setScratchDisksSizeGb(ComputeImplTest.SCRATCH_DISKS).setMaximumPersistentDisks(ComputeImplTest.MAXIMUM_PERSISTENT_DISKS).setMaximumPersistentDisksSizeGb(ComputeImplTest.MAXIMUM_PERSISTENT_DISKS_SIZE_GB).build();

    private static final RegionId REGION_ID = RegionId.of("project", "region");

    private static final Status REGION_STATUS = Status.DOWN;

    private static final ZoneId ZONE_ID1 = ZoneId.of("project", "zone1");

    private static final ZoneId ZONE_ID2 = ZoneId.of("project", "zone2");

    private static final List<ZoneId> ZONES = ImmutableList.of(ComputeImplTest.ZONE_ID1, ComputeImplTest.ZONE_ID2);

    private static final Quota QUOTA1 = new Region.Quota("METRIC1", 2, 1);

    private static final Quota QUOTA2 = new Region.Quota("METRIC2", 4, 3);

    private static final List<Region.Quota> QUOTAS = ImmutableList.of(ComputeImplTest.QUOTA1, ComputeImplTest.QUOTA2);

    private static final Region REGION = Region.builder().setRegionId(ComputeImplTest.REGION_ID).setGeneratedId(ComputeImplTest.GENERATED_ID).setCreationTimestamp(ComputeImplTest.CREATION_TIMESTAMP).setDescription(ComputeImplTest.DESCRIPTION).setStatus(ComputeImplTest.REGION_STATUS).setZones(ComputeImplTest.ZONES).setQuotas(ComputeImplTest.QUOTAS).build();

    private static final ZoneId ZONE_ID = ZoneId.of("project", "zone");

    private static final Zone.Status ZONE_STATUS = DOWN;

    private static final Zone ZONE = Zone.builder().setZoneId(ComputeImplTest.ZONE_ID).setGeneratedId(ComputeImplTest.GENERATED_ID).setCreationTimestamp(ComputeImplTest.CREATION_TIMESTAMP).setDescription(ComputeImplTest.DESCRIPTION).setStatus(ComputeImplTest.ZONE_STATUS).setRegion(ComputeImplTest.REGION_ID).build();

    private static final LicenseId LICENSE_ID = LicenseId.of("project", "license");

    private static final Boolean CHARGES_USE_FEE = true;

    private static final License LICENSE = new License(ComputeImplTest.LICENSE_ID, ComputeImplTest.CHARGES_USE_FEE);

    private static final OperationError OPERATION_ERROR1 = new Operation.OperationError("code1", "location1", "message1");

    private static final OperationError OPERATION_ERROR2 = new Operation.OperationError("code2", "location2", "message2");

    private static final OperationWarning OPERATION_WARNING1 = new Operation.OperationWarning("code1", "message1", ImmutableMap.of("k1", "v1"));

    private static final OperationWarning OPERATION_WARNING2 = new Operation.OperationWarning("code2", "location2", ImmutableMap.of("k2", "v2"));

    private static final String CLIENT_OPERATION_ID = "clientOperationId";

    private static final String OPERATION_TYPE = "delete";

    private static final String TARGET_LINK = "targetLink";

    private static final String TARGET_ID = "42";

    private static final Operation.Status STATUS = DONE;

    private static final String STATUS_MESSAGE = "statusMessage";

    private static final String USER = "user";

    private static final Integer PROGRESS = 100;

    private static final Long INSERT_TIME = 1453293540000L;

    private static final Long START_TIME = 1453293420000L;

    private static final Long END_TIME = 1453293480000L;

    private static final List<Operation.OperationError> ERRORS = ImmutableList.of(ComputeImplTest.OPERATION_ERROR1, ComputeImplTest.OPERATION_ERROR2);

    private static final List<Operation.OperationWarning> WARNINGS = ImmutableList.of(ComputeImplTest.OPERATION_WARNING1, ComputeImplTest.OPERATION_WARNING2);

    private static final Integer HTTP_ERROR_STATUS_CODE = 404;

    private static final String HTTP_ERROR_MESSAGE = "NOT FOUND";

    private static final GlobalOperationId GLOBAL_OPERATION_ID = GlobalOperationId.of("project", "op");

    private static final ZoneOperationId ZONE_OPERATION_ID = ZoneOperationId.of("project", "zone", "op");

    private static final RegionOperationId REGION_OPERATION_ID = RegionOperationId.of("project", "region", "op");

    private static final RegionAddressId REGION_ADDRESS_ID = RegionAddressId.of("project", "region", "address");

    private static final GlobalAddressId GLOBAL_ADDRESS_ID = GlobalAddressId.of("project", "address");

    private static final AddressInfo REGION_ADDRESS = AddressInfo.newBuilder(ComputeImplTest.REGION_ADDRESS_ID).build();

    private static final AddressInfo GLOBAL_ADDRESS = AddressInfo.newBuilder(ComputeImplTest.GLOBAL_ADDRESS_ID).build();

    private static final DiskId DISK_ID = DiskId.of("project", "zone", "disk");

    private static final SnapshotId SNAPSHOT_ID = SnapshotId.of("project", "snapshot");

    private static final SnapshotInfo SNAPSHOT = SnapshotInfo.of(ComputeImplTest.SNAPSHOT_ID, ComputeImplTest.DISK_ID);

    private static final ImageId IMAGE_ID = ImageId.of("project", "image");

    private static final ImageInfo IMAGE = ImageInfo.of(ComputeImplTest.IMAGE_ID, DiskImageConfiguration.of(ComputeImplTest.DISK_ID));

    private static final DeprecationStatus<ImageId> DEPRECATION_STATUS = DeprecationStatus.newBuilder(DEPRECATED, ComputeImplTest.IMAGE_ID).build();

    private static final DiskInfo DISK = DiskInfo.of(ComputeImplTest.DISK_ID, StandardDiskConfiguration.of(ComputeImplTest.DISK_TYPE_ID));

    private static final NetworkId NETWORK_ID = NetworkId.of("project", "network");

    private static final SubnetworkId SUBNETWORK_ID = SubnetworkId.of("project", "region", "network");

    private static final SubnetworkInfo SUBNETWORK = SubnetworkInfo.of(ComputeImplTest.SUBNETWORK_ID, ComputeImplTest.NETWORK_ID, "192.168.0.0/16");

    private static final NetworkInfo NETWORK = NetworkInfo.of(ComputeImplTest.NETWORK_ID, StandardNetworkConfiguration.of("192.168.0.0/16"));

    private static final InstanceId INSTANCE_ID = InstanceId.of("project", "zone", "instance");

    private static final PersistentDiskConfiguration PERSISTENT_DISK_CONFIGURATION = PersistentDiskConfiguration.of(ComputeImplTest.DISK_ID);

    private static final AttachedDisk ATTACHED_DISK = AttachedDisk.of("device", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);

    private static final NetworkInterface NETWORK_INTERFACE = NetworkInterface.of(ComputeImplTest.NETWORK_ID);

    private static final InstanceInfo INSTANCE = InstanceInfo.of(ComputeImplTest.INSTANCE_ID, ComputeImplTest.MACHINE_TYPE_ID, ComputeImplTest.ATTACHED_DISK, ComputeImplTest.NETWORK_INTERFACE);

    // Empty ComputeRpc options
    private static final Map<ComputeRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    // DiskType options
    private static final DiskTypeOption DISK_TYPE_OPTION_FIELDS = DiskTypeOption.fields(ID, Compute.DiskTypeField.DESCRIPTION);

    // DiskType list options
    private static final DiskTypeFilter DISK_TYPE_FILTER = DiskTypeFilter.equals(Compute.DiskTypeField.DESCRIPTION, "someDescription");

    private static final DiskTypeListOption DISK_TYPE_LIST_PAGE_TOKEN = DiskTypeListOption.pageToken("cursor");

    private static final DiskTypeListOption DISK_TYPE_LIST_PAGE_SIZE = DiskTypeListOption.pageSize(42L);

    private static final DiskTypeListOption DISK_TYPE_LIST_FILTER = DiskTypeListOption.filter(ComputeImplTest.DISK_TYPE_FILTER);

    private static final Map<ComputeRpc.Option, ?> DISK_TYPE_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "description eq someDescription");

    // DiskType aggregated list options
    private static final DiskTypeAggregatedListOption DISK_TYPE_AGGREGATED_LIST_PAGE_TOKEN = DiskTypeAggregatedListOption.pageToken("cursor");

    private static final DiskTypeAggregatedListOption DISK_TYPE_AGGREGATED_LIST_PAGE_SIZE = DiskTypeAggregatedListOption.pageSize(42L);

    private static final DiskTypeAggregatedListOption DISK_TYPE_AGGREGATED_LIST_FILTER = DiskTypeAggregatedListOption.filter(ComputeImplTest.DISK_TYPE_FILTER);

    // MachineType options
    private static final MachineTypeOption MACHINE_TYPE_OPTION_FIELDS = MachineTypeOption.fields(Compute.MachineTypeField.ID, Compute.MachineTypeField.DESCRIPTION);

    // MachineType list options
    private static final MachineTypeFilter MACHINE_TYPE_FILTER = MachineTypeFilter.notEquals(Compute.MachineTypeField.MAXIMUM_PERSISTENT_DISKS, 42L);

    private static final MachineTypeListOption MACHINE_TYPE_LIST_PAGE_TOKEN = MachineTypeListOption.pageToken("cursor");

    private static final MachineTypeListOption MACHINE_TYPE_LIST_PAGE_SIZE = MachineTypeListOption.pageSize(42L);

    private static final MachineTypeListOption MACHINE_TYPE_LIST_FILTER = MachineTypeListOption.filter(ComputeImplTest.MACHINE_TYPE_FILTER);

    private static final Map<ComputeRpc.Option, ?> MACHINE_TYPE_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "maximumPersistentDisks ne 42");

    // MachineType aggregated list options
    private static final MachineTypeAggregatedListOption MACHINE_TYPE_AGGREGATED_LIST_PAGE_TOKEN = MachineTypeAggregatedListOption.pageToken("cursor");

    private static final MachineTypeAggregatedListOption MACHINE_TYPE_AGGREGATED_LIST_PAGE_SIZE = MachineTypeAggregatedListOption.pageSize(42L);

    private static final MachineTypeAggregatedListOption MACHINE_TYPE_AGGREGATED_LIST_FILTER = MachineTypeAggregatedListOption.filter(ComputeImplTest.MACHINE_TYPE_FILTER);

    // Region options
    private static final RegionOption REGION_OPTION_FIELDS = RegionOption.fields(Compute.RegionField.ID, Compute.RegionField.DESCRIPTION);

    // Region list options
    private static final RegionFilter REGION_FILTER = RegionFilter.equals(Compute.RegionField.ID, "someId");

    private static final RegionListOption REGION_LIST_PAGE_TOKEN = RegionListOption.pageToken("cursor");

    private static final RegionListOption REGION_LIST_PAGE_SIZE = RegionListOption.pageSize(42L);

    private static final RegionListOption REGION_LIST_FILTER = RegionListOption.filter(ComputeImplTest.REGION_FILTER);

    private static final Map<ComputeRpc.Option, ?> REGION_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "id eq someId");

    // Zone options
    private static final ZoneOption ZONE_OPTION_FIELDS = ZoneOption.fields(Compute.ZoneField.ID, Compute.ZoneField.DESCRIPTION);

    // Zone list options
    private static final ZoneFilter ZONE_FILTER = ZoneFilter.notEquals(NAME, "someName");

    private static final ZoneListOption ZONE_LIST_PAGE_TOKEN = ZoneListOption.pageToken("cursor");

    private static final ZoneListOption ZONE_LIST_PAGE_SIZE = ZoneListOption.pageSize(42L);

    private static final ZoneListOption ZONE_LIST_FILTER = ZoneListOption.filter(ComputeImplTest.ZONE_FILTER);

    private static final Map<ComputeRpc.Option, ?> ZONE_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "name ne someName");

    // License options
    private static final LicenseOption LICENSE_OPTION_FIELDS = LicenseOption.fields(Compute.LicenseField.CHARGES_USE_FEE);

    // Operation options
    private static final OperationOption OPERATION_OPTION_FIELDS = OperationOption.fields(Compute.OperationField.ID, Compute.OperationField.DESCRIPTION);

    // Operation list options
    private static final OperationFilter OPERATION_FILTER = OperationFilter.notEquals(Compute.OperationField.PROGRESS, 0);

    private static final OperationListOption OPERATION_LIST_PAGE_TOKEN = OperationListOption.pageToken("cursor");

    private static final OperationListOption OPERATION_LIST_PAGE_SIZE = OperationListOption.pageSize(42L);

    private static final OperationListOption OPERATION_LIST_FILTER = OperationListOption.filter(ComputeImplTest.OPERATION_FILTER);

    private static final Map<ComputeRpc.Option, ?> OPERATION_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "progress ne 0");

    // Address options
    private static final AddressOption ADDRESS_OPTION_FIELDS = AddressOption.fields(Compute.AddressField.ID, Compute.AddressField.DESCRIPTION);

    // Address list options
    private static final AddressFilter ADDRESS_FILTER = AddressFilter.notEquals(Compute.AddressField.REGION, "someRegion");

    private static final AddressListOption ADDRESS_LIST_PAGE_TOKEN = AddressListOption.pageToken("cursor");

    private static final AddressListOption ADDRESS_LIST_PAGE_SIZE = AddressListOption.pageSize(42L);

    private static final AddressListOption ADDRESS_LIST_FILTER = AddressListOption.filter(ComputeImplTest.ADDRESS_FILTER);

    private static final Map<ComputeRpc.Option, ?> ADDRESS_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "region ne someRegion");

    // Address aggregated list options
    private static final AddressAggregatedListOption ADDRESS_AGGREGATED_LIST_PAGE_TOKEN = AddressAggregatedListOption.pageToken("cursor");

    private static final AddressAggregatedListOption ADDRESS_AGGREGATED_LIST_PAGE_SIZE = AddressAggregatedListOption.pageSize(42L);

    private static final AddressAggregatedListOption ADDRESS_AGGREGATED_LIST_FILTER = AddressAggregatedListOption.filter(ComputeImplTest.ADDRESS_FILTER);

    // Snapshot options
    private static final SnapshotOption SNAPSHOT_OPTION_FIELDS = SnapshotOption.fields(Compute.SnapshotField.ID, Compute.SnapshotField.DESCRIPTION);

    // Snapshot list options
    private static final SnapshotFilter SNAPSHOT_FILTER = SnapshotFilter.equals(DISK_SIZE_GB, 500L);

    private static final SnapshotListOption SNAPSHOT_LIST_PAGE_TOKEN = SnapshotListOption.pageToken("cursor");

    private static final SnapshotListOption SNAPSHOT_LIST_PAGE_SIZE = SnapshotListOption.pageSize(42L);

    private static final SnapshotListOption SNAPSHOT_LIST_FILTER = SnapshotListOption.filter(ComputeImplTest.SNAPSHOT_FILTER);

    private static final Map<ComputeRpc.Option, ?> SNAPSHOT_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "diskSizeGb eq 500");

    // Image options
    private static final ImageOption IMAGE_OPTION_FIELDS = ImageOption.fields(Compute.ImageField.ID, Compute.ImageField.DESCRIPTION);

    // Image list options
    private static final ImageFilter IMAGE_FILTER = ImageFilter.notEquals(Compute.ImageField.DISK_SIZE_GB, 500L);

    private static final ImageListOption IMAGE_LIST_PAGE_TOKEN = ImageListOption.pageToken("cursor");

    private static final ImageListOption IMAGE_LIST_PAGE_SIZE = ImageListOption.pageSize(42L);

    private static final ImageListOption IMAGE_LIST_FILTER = ImageListOption.filter(ComputeImplTest.IMAGE_FILTER);

    private static final Map<ComputeRpc.Option, ?> IMAGE_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "diskSizeGb ne 500");

    // Disk options
    private static final DiskOption DISK_OPTION_FIELDS = DiskOption.fields(Compute.DiskField.ID, Compute.DiskField.DESCRIPTION);

    // Disk list options
    private static final DiskFilter DISK_FILTER = DiskFilter.notEquals(SIZE_GB, 500L);

    private static final DiskListOption DISK_LIST_PAGE_TOKEN = DiskListOption.pageToken("cursor");

    private static final DiskListOption DISK_LIST_PAGE_SIZE = DiskListOption.pageSize(42L);

    private static final DiskListOption DISK_LIST_FILTER = DiskListOption.filter(ComputeImplTest.DISK_FILTER);

    private static final Map<ComputeRpc.Option, ?> DISK_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "sizeGb ne 500");

    // Disk aggregated list options
    private static final DiskAggregatedListOption DISK_AGGREGATED_LIST_PAGE_TOKEN = DiskAggregatedListOption.pageToken("cursor");

    private static final DiskAggregatedListOption DISK_AGGREGATED_LIST_PAGE_SIZE = DiskAggregatedListOption.pageSize(42L);

    private static final DiskAggregatedListOption DISK_AGGREGATED_LIST_FILTER = DiskAggregatedListOption.filter(ComputeImplTest.DISK_FILTER);

    // Subnetwork options
    private static final SubnetworkOption SUBNETWORK_OPTION_FIELDS = SubnetworkOption.fields(Compute.SubnetworkField.ID, Compute.SubnetworkField.DESCRIPTION);

    // Subnetwork list options
    private static final SubnetworkFilter SUBNETWORK_FILTER = SubnetworkFilter.equals(IP_CIDR_RANGE, "192.168.0.0/16");

    private static final SubnetworkListOption SUBNETWORK_LIST_PAGE_TOKEN = SubnetworkListOption.pageToken("cursor");

    private static final SubnetworkListOption SUBNETWORK_LIST_PAGE_SIZE = SubnetworkListOption.pageSize(42L);

    private static final SubnetworkListOption SUBNETWORK_LIST_FILTER = SubnetworkListOption.filter(ComputeImplTest.SUBNETWORK_FILTER);

    private static final Map<ComputeRpc.Option, ?> SUBNETWORK_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "ipCidrRange eq 192.168.0.0/16");

    // Subnetwork aggregated list options
    private static final SubnetworkAggregatedListOption SUBNETWORK_AGGREGATED_LIST_PAGE_TOKEN = SubnetworkAggregatedListOption.pageToken("cursor");

    private static final SubnetworkAggregatedListOption SUBNETWORK_AGGREGATED_LIST_PAGE_SIZE = SubnetworkAggregatedListOption.pageSize(42L);

    private static final SubnetworkAggregatedListOption SUBNETWORK_AGGREGATED_LIST_FILTER = SubnetworkAggregatedListOption.filter(ComputeImplTest.SUBNETWORK_FILTER);

    // Network options
    private static final NetworkOption NETWORK_OPTION_FIELDS = NetworkOption.fields(Compute.NetworkField.ID, Compute.NetworkField.DESCRIPTION);

    // Network list options
    private static final NetworkFilter NETWORK_FILTER = NetworkFilter.equals(IPV4_RANGE, "192.168.0.0/16");

    private static final NetworkListOption NETWORK_LIST_PAGE_TOKEN = NetworkListOption.pageToken("cursor");

    private static final NetworkListOption NETWORK_LIST_PAGE_SIZE = NetworkListOption.pageSize(42L);

    private static final NetworkListOption NETWORK_LIST_FILTER = NetworkListOption.filter(ComputeImplTest.NETWORK_FILTER);

    private static final Map<ComputeRpc.Option, ?> NETWORK_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "IPv4Range eq 192.168.0.0/16");

    // Instance options
    private static final InstanceOption INSTANCE_OPTION_FIELDS = InstanceOption.fields(Compute.InstanceField.ID, Compute.InstanceField.DESCRIPTION);

    // Instance list options
    private static final InstanceFilter INSTANCE_FILTER = InstanceFilter.equals(CAN_IP_FORWARD, true);

    private static final InstanceListOption INSTANCE_LIST_PAGE_TOKEN = InstanceListOption.pageToken("cursor");

    private static final InstanceListOption INSTANCE_LIST_PAGE_SIZE = InstanceListOption.pageSize(42L);

    private static final InstanceListOption INSTANCE_LIST_FILTER = InstanceListOption.filter(ComputeImplTest.INSTANCE_FILTER);

    private static final Map<ComputeRpc.Option, ?> INSTANCE_LIST_OPTIONS = ImmutableMap.of(PAGE_TOKEN, "cursor", MAX_RESULTS, 42L, FILTER, "canIpForward eq true");

    // Instance aggregated list options
    private static final InstanceAggregatedListOption INSTANCE_AGGREGATED_LIST_PAGE_TOKEN = InstanceAggregatedListOption.pageToken("cursor");

    private static final InstanceAggregatedListOption INSTANCE_AGGREGATED_LIST_PAGE_SIZE = InstanceAggregatedListOption.pageSize(42L);

    private static final InstanceAggregatedListOption INSTANCE_AGGREGATED_LIST_FILTER = InstanceAggregatedListOption.filter(ComputeImplTest.INSTANCE_FILTER);

    private static final Function<Operation, com.google.api.services.compute.model.Operation> OPERATION_TO_PB_FUNCTION = new Function<Operation, com.google.api.services.compute.model.Operation>() {
        @Override
        public com.google.api.services.compute.model.Operation apply(Operation operation) {
            return operation.toPb();
        }
    };

    private ComputeOptions options;

    private ComputeRpcFactory rpcFactoryMock;

    private ComputeRpc computeRpcMock;

    private Compute compute;

    private Operation globalOperation;

    private Operation zoneOperation;

    private Operation regionOperation;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertSame(options, compute.getOptions());
    }

    @Test
    public void testGetDiskType() {
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.DISK_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        DiskType diskType = compute.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType());
        Assert.assertEquals(ComputeImplTest.DISK_TYPE, diskType);
    }

    @Test
    public void testGetDiskType_Null() {
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType()));
    }

    @Test
    public void testGetDiskTypeFromDiskTypeId() {
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.DISK_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        DiskType diskType = compute.getDiskType(ComputeImplTest.DISK_TYPE_ID);
        Assert.assertEquals(ComputeImplTest.DISK_TYPE, diskType);
    }

    @Test
    public void testGetDiskTypeWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getDiskType(eq(ComputeImplTest.DISK_TYPE_ID.getZone()), eq(ComputeImplTest.DISK_TYPE_ID.getType()), capture(capturedOptions))).andReturn(ComputeImplTest.DISK_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        DiskType diskType = compute.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.DISK_TYPE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.DISK_TYPE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(ComputeImplTest.DISK_TYPE, diskType);
    }

    @Test
    public void testListDiskTypes() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testListDiskTypesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        ImmutableList<DiskType> nextDiskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextDiskTypeList, TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextDiskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testListEmptyDiskTypes() {
        ImmutableList<com.google.api.services.compute.model.DiskType> diskTypes = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.DiskType>>of(null, diskTypes);
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<DiskType> page = compute.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(diskTypes.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testListDiskTypesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_LIST_PAGE_SIZE, ComputeImplTest.DISK_TYPE_LIST_PAGE_TOKEN, ComputeImplTest.DISK_TYPE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testAggregatedListDiskTypes() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testAggregatedListDiskTypesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        ImmutableList<DiskType> nextDiskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextDiskTypeList, TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listDiskTypes(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextDiskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testAggregatedListEmptyDiskTypes() {
        ImmutableList<com.google.api.services.compute.model.DiskType> diskTypes = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.DiskType>>of(null, diskTypes);
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<DiskType> page = compute.listDiskTypes();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(diskTypes.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testAggregatedListDiskTypesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<DiskType> diskTypeList = ImmutableList.of(ComputeImplTest.DISK_TYPE, ComputeImplTest.DISK_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result = Tuple.of(cursor, Iterables.transform(diskTypeList, TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDiskTypes(ComputeImplTest.DISK_TYPE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<DiskType> page = compute.listDiskTypes(ComputeImplTest.DISK_TYPE_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.DISK_TYPE_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.DISK_TYPE_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskTypeList.toArray(), Iterables.toArray(page.getValues(), DiskType.class));
    }

    @Test
    public void testGetMachineType() {
        EasyMock.expect(computeRpcMock.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.MACHINE_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        MachineType machineType = compute.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType());
        Assert.assertEquals(ComputeImplTest.MACHINE_TYPE, machineType);
    }

    @Test
    public void testGetMachineType_Null() {
        EasyMock.expect(computeRpcMock.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType()));
    }

    @Test
    public void testGetMachineTypeFromMachineTypeId() {
        EasyMock.expect(computeRpcMock.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.MACHINE_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        MachineType machineType = compute.getMachineType(ComputeImplTest.MACHINE_TYPE_ID);
        Assert.assertEquals(ComputeImplTest.MACHINE_TYPE, machineType);
    }

    @Test
    public void testGetMachineTypeWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getMachineType(eq(ComputeImplTest.MACHINE_TYPE_ID.getZone()), eq(ComputeImplTest.MACHINE_TYPE_ID.getType()), capture(capturedOptions))).andReturn(ComputeImplTest.MACHINE_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        MachineType machineType = compute.getMachineType(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_ID.getType(), ComputeImplTest.MACHINE_TYPE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.DISK_TYPE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(ComputeImplTest.MACHINE_TYPE, machineType);
    }

    @Test
    public void testListMachineTypes() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testListMachineTypesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        ImmutableList<MachineType> nextMachineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextMachineTypeList, MachineType.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextMachineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testListEmptyMachineTypes() {
        ImmutableList<com.google.api.services.compute.model.MachineType> machineTypes = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.MachineType>>of(null, machineTypes);
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<MachineType> page = compute.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(machineTypes.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testListMachineTypesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes(ComputeImplTest.MACHINE_TYPE_ID.getZone(), ComputeImplTest.MACHINE_TYPE_LIST_PAGE_SIZE, ComputeImplTest.MACHINE_TYPE_LIST_PAGE_TOKEN, ComputeImplTest.MACHINE_TYPE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testAggregatedListMachineTypes() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testAggregatedListMachineTypesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        ImmutableList<MachineType> nextMachineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextMachineTypeList, MachineType.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listMachineTypes(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextMachineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testAggregatedListEmptyMachineTypes() {
        ImmutableList<com.google.api.services.compute.model.MachineType> machineTypes = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.MachineType>>of(null, machineTypes);
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<MachineType> page = compute.listMachineTypes();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(machineTypes.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testAggregatedListMachineTypesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<MachineType> machineTypeList = ImmutableList.of(ComputeImplTest.MACHINE_TYPE, ComputeImplTest.MACHINE_TYPE);
        Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result = Tuple.of(cursor, Iterables.transform(machineTypeList, MachineType.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listMachineTypes(ComputeImplTest.MACHINE_TYPE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<MachineType> page = compute.listMachineTypes(ComputeImplTest.MACHINE_TYPE_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.MACHINE_TYPE_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.MACHINE_TYPE_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(machineTypeList.toArray(), Iterables.toArray(page.getValues(), MachineType.class));
    }

    @Test
    public void testGetRegion() {
        EasyMock.expect(computeRpcMock.getRegion(ComputeImplTest.REGION_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.REGION.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Region region = compute.getRegion(ComputeImplTest.REGION_ID.getRegion());
        Assert.assertEquals(ComputeImplTest.REGION, region);
    }

    @Test
    public void testGetRegion_Null() {
        EasyMock.expect(computeRpcMock.getRegion(ComputeImplTest.REGION_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getRegion(ComputeImplTest.REGION_ID.getRegion()));
    }

    @Test
    public void testGetRegionWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getRegion(eq(ComputeImplTest.REGION_ID.getRegion()), capture(capturedOptions))).andReturn(ComputeImplTest.REGION.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Region region = compute.getRegion(ComputeImplTest.REGION_ID.getRegion(), ComputeImplTest.REGION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.REGION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(ComputeImplTest.REGION, region);
    }

    @Test
    public void testListRegions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Region> regionList = ImmutableList.of(ComputeImplTest.REGION, ComputeImplTest.REGION);
        Tuple<String, Iterable<com.google.api.services.compute.model.Region>> result = Tuple.of(cursor, Iterables.transform(regionList, Region.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegions(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Region> page = compute.listRegions();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(regionList.toArray(), Iterables.toArray(page.getValues(), Region.class));
    }

    @Test
    public void testListRegionsNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Region> regionList = ImmutableList.of(ComputeImplTest.REGION, ComputeImplTest.REGION);
        ImmutableList<Region> nextRegionList = ImmutableList.of(ComputeImplTest.REGION);
        Tuple<String, Iterable<com.google.api.services.compute.model.Region>> result = Tuple.of(cursor, Iterables.transform(regionList, Region.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Region>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextRegionList, Region.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listRegions(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listRegions(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Region> page = compute.listRegions();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(regionList.toArray(), Iterables.toArray(page.getValues(), Region.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextRegionList.toArray(), Iterables.toArray(page.getValues(), Region.class));
    }

    @Test
    public void testListEmptyRegions() {
        ImmutableList<com.google.api.services.compute.model.Region> regions = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Region>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Region>>of(null, regions);
        EasyMock.expect(computeRpcMock.listRegions(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Region> page = compute.listRegions();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(regions.toArray(), Iterables.toArray(page.getValues(), Region.class));
    }

    @Test
    public void testListRegionsWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Region> regionList = ImmutableList.of(ComputeImplTest.REGION, ComputeImplTest.REGION);
        Tuple<String, Iterable<com.google.api.services.compute.model.Region>> result = Tuple.of(cursor, Iterables.transform(regionList, Region.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegions(ComputeImplTest.REGION_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Region> page = compute.listRegions(ComputeImplTest.REGION_LIST_PAGE_SIZE, ComputeImplTest.REGION_LIST_PAGE_TOKEN, ComputeImplTest.REGION_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(regionList.toArray(), Iterables.toArray(page.getValues(), Region.class));
    }

    @Test
    public void testGetZone() {
        EasyMock.expect(computeRpcMock.getZone(ComputeImplTest.ZONE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.ZONE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Zone zone = compute.getZone(ComputeImplTest.ZONE_ID.getZone());
        Assert.assertEquals(ComputeImplTest.ZONE, zone);
    }

    @Test
    public void testGetZone_Null() {
        EasyMock.expect(computeRpcMock.getZone(ComputeImplTest.ZONE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getZone(ComputeImplTest.ZONE_ID.getZone()));
    }

    @Test
    public void testGetZoneWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getZone(eq(ComputeImplTest.ZONE_ID.getZone()), capture(capturedOptions))).andReturn(ComputeImplTest.ZONE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Zone zone = compute.getZone(ComputeImplTest.ZONE_ID.getZone(), ComputeImplTest.ZONE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.ZONE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(ComputeImplTest.ZONE, zone);
    }

    @Test
    public void testListZones() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Zone> zoneList = ImmutableList.of(ComputeImplTest.ZONE, ComputeImplTest.ZONE);
        Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> result = Tuple.of(cursor, Iterables.transform(zoneList, Zone.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listZones(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Zone> page = compute.listZones();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(zoneList.toArray(), Iterables.toArray(page.getValues(), Zone.class));
    }

    @Test
    public void testListZonesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Zone> zoneList = ImmutableList.of(ComputeImplTest.ZONE, ComputeImplTest.ZONE);
        ImmutableList<Zone> nextZoneList = ImmutableList.of(ComputeImplTest.ZONE);
        Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> result = Tuple.of(cursor, Iterables.transform(zoneList, Zone.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextZoneList, Zone.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listZones(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listZones(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Zone> page = compute.listZones();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(zoneList.toArray(), Iterables.toArray(page.getValues(), Zone.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextZoneList.toArray(), Iterables.toArray(page.getValues(), Zone.class));
    }

    @Test
    public void testListEmptyZones() {
        ImmutableList<com.google.api.services.compute.model.Zone> zones = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Zone>>of(null, zones);
        EasyMock.expect(computeRpcMock.listZones(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Zone> page = compute.listZones();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(zones.toArray(), Iterables.toArray(page.getValues(), Zone.class));
    }

    @Test
    public void testListZonesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Zone> zoneList = ImmutableList.of(ComputeImplTest.ZONE, ComputeImplTest.ZONE);
        Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> result = Tuple.of(cursor, Iterables.transform(zoneList, Zone.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listZones(ComputeImplTest.ZONE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Zone> page = compute.listZones(ComputeImplTest.ZONE_LIST_PAGE_SIZE, ComputeImplTest.ZONE_LIST_PAGE_TOKEN, ComputeImplTest.ZONE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(zoneList.toArray(), Iterables.toArray(page.getValues(), Zone.class));
    }

    @Test
    public void testGetLicenseFromString() {
        EasyMock.expect(computeRpcMock.getLicense(ComputeImplTest.PROJECT, ComputeImplTest.LICENSE_ID.getLicense(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.LICENSE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        License license = compute.getLicense(ComputeImplTest.LICENSE_ID.getLicense());
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
    }

    @Test
    public void testGetLicenseFromString_Null() {
        EasyMock.expect(computeRpcMock.getLicense(ComputeImplTest.PROJECT, ComputeImplTest.LICENSE_ID.getLicense(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getLicense(ComputeImplTest.LICENSE_ID.getLicense()));
    }

    @Test
    public void testGetLicenseFromStringWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getLicense(eq(ComputeImplTest.PROJECT), eq(ComputeImplTest.LICENSE_ID.getLicense()), capture(capturedOptions))).andReturn(ComputeImplTest.LICENSE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        License license = compute.getLicense(ComputeImplTest.LICENSE_ID.getLicense(), ComputeImplTest.LICENSE_OPTION_FIELDS);
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.LICENSE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("chargesUseFee"));
        Assert.assertEquals(22, selector.length());
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
    }

    @Test
    public void testGetLicenseFromIdWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        LicenseId licenseId = LicenseId.of("project2", "license2");
        EasyMock.expect(computeRpcMock.getLicense(eq(licenseId.getProject()), eq(licenseId.getLicense()), capture(capturedOptions))).andReturn(ComputeImplTest.LICENSE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        License license = compute.getLicense(licenseId, ComputeImplTest.LICENSE_OPTION_FIELDS);
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.LICENSE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("chargesUseFee"));
        Assert.assertEquals(22, selector.length());
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
    }

    @Test
    public void testGetLicenseFromId() {
        LicenseId licenseId = LicenseId.of("project2", "license2");
        EasyMock.expect(computeRpcMock.getLicense(licenseId.getProject(), licenseId.getLicense(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.LICENSE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        License license = compute.getLicense(licenseId);
        Assert.assertEquals(ComputeImplTest.LICENSE, license);
    }

    @Test
    public void testGetLicenseFromId_Null() {
        LicenseId licenseId = LicenseId.of("project2", "license2");
        EasyMock.expect(computeRpcMock.getLicense(licenseId.getProject(), licenseId.getLicense(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getLicense(licenseId));
    }

    @Test
    public void testGetGlobalOperation() {
        EasyMock.expect(computeRpcMock.getGlobalOperation(ComputeImplTest.GLOBAL_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.getOperation(ComputeImplTest.GLOBAL_OPERATION_ID));
    }

    @Test
    public void testGetGlobalOperation_Null() {
        EasyMock.expect(computeRpcMock.getGlobalOperation(ComputeImplTest.GLOBAL_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getOperation(ComputeImplTest.GLOBAL_OPERATION_ID));
    }

    @Test
    public void testGetGlobalOperationWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getGlobalOperation(eq(ComputeImplTest.GLOBAL_OPERATION_ID.getOperation()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.GLOBAL_OPERATION_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testListGlobalOperations() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(globalOperation, globalOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listGlobalOperations(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listGlobalOperations();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListGlobalOperationsNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(globalOperation, globalOperation);
        ImmutableList<Operation> nextOperationList = ImmutableList.of(globalOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextOperationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listGlobalOperations(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listGlobalOperations(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listGlobalOperations();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextOperationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListEmptyGlobalOperations() {
        ImmutableList<com.google.api.services.compute.model.Operation> operations = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Operation>>of(null, operations);
        EasyMock.expect(computeRpcMock.listGlobalOperations(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Operation> page = compute.listGlobalOperations();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(operations.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListGlobalOperationsWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(globalOperation, globalOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listGlobalOperations(ComputeImplTest.OPERATION_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listGlobalOperations(ComputeImplTest.OPERATION_LIST_PAGE_SIZE, ComputeImplTest.OPERATION_LIST_PAGE_TOKEN, ComputeImplTest.OPERATION_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testDeleteGlobalOperation_True() {
        EasyMock.expect(computeRpcMock.deleteGlobalOperation(ComputeImplTest.GLOBAL_OPERATION_ID.getOperation())).andReturn(true);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertTrue(compute.deleteOperation(ComputeImplTest.GLOBAL_OPERATION_ID));
    }

    @Test
    public void testDeleteGlobalOperation_False() {
        EasyMock.expect(computeRpcMock.deleteGlobalOperation(ComputeImplTest.GLOBAL_OPERATION_ID.getOperation())).andReturn(false);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertFalse(compute.deleteOperation(ComputeImplTest.GLOBAL_OPERATION_ID));
    }

    @Test
    public void testGetRegionOperation() {
        EasyMock.expect(computeRpcMock.getRegionOperation(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.REGION_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.REGION_OPERATION_ID);
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testGetRegionOperation_Null() {
        EasyMock.expect(computeRpcMock.getRegionOperation(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.REGION_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.REGION_OPERATION_ID);
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testGetRegionOperationWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getRegionOperation(eq(ComputeImplTest.REGION_OPERATION_ID.getRegion()), eq(ComputeImplTest.REGION_OPERATION_ID.getOperation()), capture(capturedOptions))).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.REGION_OPERATION_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testListRegionOperations() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(regionOperation, regionOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListRegionOperationsNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(regionOperation, regionOperation);
        ImmutableList<Operation> nextOperationList = ImmutableList.of(regionOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextOperationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextOperationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListEmptyRegionOperations() {
        ImmutableList<com.google.api.services.compute.model.Operation> operations = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Operation>>of(null, operations);
        EasyMock.expect(computeRpcMock.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Operation> page = compute.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(operations.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListRegionOperationsWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(regionOperation, regionOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.OPERATION_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listRegionOperations(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.OPERATION_LIST_PAGE_SIZE, ComputeImplTest.OPERATION_LIST_PAGE_TOKEN, ComputeImplTest.OPERATION_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testDeleteRegionOperation_True() {
        EasyMock.expect(computeRpcMock.deleteRegionOperation(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.REGION_OPERATION_ID.getOperation())).andReturn(true);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertTrue(compute.deleteOperation(ComputeImplTest.REGION_OPERATION_ID));
    }

    @Test
    public void testDeleteRegionOperation_False() {
        EasyMock.expect(computeRpcMock.deleteRegionOperation(ComputeImplTest.REGION_OPERATION_ID.getRegion(), ComputeImplTest.REGION_OPERATION_ID.getOperation())).andReturn(false);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertFalse(compute.deleteOperation(ComputeImplTest.REGION_OPERATION_ID));
    }

    @Test
    public void testGetZoneOperation() {
        EasyMock.expect(computeRpcMock.getZoneOperation(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.ZONE_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.ZONE_OPERATION_ID);
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testGetZoneOperation_Null() {
        EasyMock.expect(computeRpcMock.getZoneOperation(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.ZONE_OPERATION_ID.getOperation(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getOperation(ComputeImplTest.ZONE_OPERATION_ID));
    }

    @Test
    public void testGetZoneOperationWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getZoneOperation(eq(ComputeImplTest.ZONE_OPERATION_ID.getZone()), eq(ComputeImplTest.ZONE_OPERATION_ID.getOperation()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.getOperation(ComputeImplTest.ZONE_OPERATION_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testListZoneOperations() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(zoneOperation, zoneOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListZoneOperationsNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(zoneOperation, zoneOperation);
        ImmutableList<Operation> nextOperationList = ImmutableList.of(zoneOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextOperationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextOperationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListEmptyZoneOperations() {
        ImmutableList<com.google.api.services.compute.model.Operation> operations = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Operation>>of(null, operations);
        EasyMock.expect(computeRpcMock.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Operation> page = compute.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(operations.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testListZoneOperationsWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Operation> operationList = ImmutableList.of(zoneOperation, zoneOperation);
        Tuple<String, Iterable<com.google.api.services.compute.model.Operation>> result = Tuple.of(cursor, Iterables.transform(operationList, ComputeImplTest.OPERATION_TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.OPERATION_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Operation> page = compute.listZoneOperations(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.OPERATION_LIST_PAGE_SIZE, ComputeImplTest.OPERATION_LIST_PAGE_TOKEN, ComputeImplTest.OPERATION_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(operationList.toArray(), Iterables.toArray(page.getValues(), Operation.class));
    }

    @Test
    public void testDeleteZoneOperation_True() {
        EasyMock.expect(computeRpcMock.deleteZoneOperation(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.ZONE_OPERATION_ID.getOperation())).andReturn(true);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertTrue(compute.deleteOperation(ComputeImplTest.ZONE_OPERATION_ID));
    }

    @Test
    public void testDeleteZoneOperation_False() {
        EasyMock.expect(computeRpcMock.deleteZoneOperation(ComputeImplTest.ZONE_OPERATION_ID.getZone(), ComputeImplTest.ZONE_OPERATION_ID.getOperation())).andReturn(false);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertFalse(compute.deleteOperation(ComputeImplTest.ZONE_OPERATION_ID));
    }

    @Test
    public void testGetGlobalAddress() {
        EasyMock.expect(computeRpcMock.getGlobalAddress(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.GLOBAL_ADDRESS.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Address address = compute.getAddress(ComputeImplTest.GLOBAL_ADDRESS_ID);
        Assert.assertEquals(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)), address);
    }

    @Test
    public void testGetGlobalAddress_Null() {
        EasyMock.expect(computeRpcMock.getGlobalAddress(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getAddress(ComputeImplTest.GLOBAL_ADDRESS_ID));
    }

    @Test
    public void testGetGlobalAddressWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getGlobalAddress(eq(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress()), capture(capturedOptions))).andReturn(ComputeImplTest.GLOBAL_ADDRESS.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Address address = compute.getAddress(ComputeImplTest.GLOBAL_ADDRESS_ID, ComputeImplTest.ADDRESS_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.ADDRESS_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)), address);
    }

    @Test
    public void testGetRegionAddress() {
        EasyMock.expect(computeRpcMock.getRegionAddress(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.REGION_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.REGION_ADDRESS.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Address address = compute.getAddress(ComputeImplTest.REGION_ADDRESS_ID);
        Assert.assertEquals(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), address);
    }

    @Test
    public void testGetRegionAddress_Null() {
        EasyMock.expect(computeRpcMock.getRegionAddress(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.REGION_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.REGION_ADDRESS.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Address address = compute.getAddress(ComputeImplTest.REGION_ADDRESS_ID);
        Assert.assertEquals(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), address);
    }

    @Test
    public void testGetRegionAddressWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getRegionAddress(eq(ComputeImplTest.REGION_ADDRESS_ID.getRegion()), eq(ComputeImplTest.REGION_ADDRESS_ID.getAddress()), capture(capturedOptions))).andReturn(ComputeImplTest.REGION_ADDRESS.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Address address = compute.getAddress(ComputeImplTest.REGION_ADDRESS_ID, ComputeImplTest.ADDRESS_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.ADDRESS_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), address);
    }

    @Test
    public void testDeleteGlobalAddress_Operation() {
        EasyMock.expect(computeRpcMock.deleteGlobalAddress(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.deleteAddress(ComputeImplTest.GLOBAL_ADDRESS_ID));
    }

    @Test
    public void testDeleteGlobalAddressWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteGlobalAddress(eq(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteAddress(ComputeImplTest.GLOBAL_ADDRESS_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeleteGlobalAddress_Null() {
        EasyMock.expect(computeRpcMock.deleteGlobalAddress(ComputeImplTest.GLOBAL_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteAddress(ComputeImplTest.GLOBAL_ADDRESS_ID));
    }

    @Test
    public void testDeleteRegionAddress_Operation() {
        EasyMock.expect(computeRpcMock.deleteRegionAddress(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.REGION_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(regionOperation, compute.deleteAddress(ComputeImplTest.REGION_ADDRESS_ID));
    }

    @Test
    public void testDeleteRegionAddressWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteRegionAddress(eq(ComputeImplTest.REGION_ADDRESS_ID.getRegion()), eq(ComputeImplTest.REGION_ADDRESS_ID.getAddress()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteAddress(ComputeImplTest.REGION_ADDRESS_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeleteRegionAddress_Null() {
        EasyMock.expect(computeRpcMock.deleteRegionAddress(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.REGION_ADDRESS_ID.getAddress(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteAddress(ComputeImplTest.REGION_ADDRESS_ID));
    }

    @Test
    public void testListGlobalAddresses() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listGlobalAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listGlobalAddresses();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListGlobalAddressesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)));
        ImmutableList<Address> nextAddressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextAddressList, AddressInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listGlobalAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listGlobalAddresses(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listGlobalAddresses();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextAddressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListEmptyGlobalAddresses() {
        ImmutableList<com.google.api.services.compute.model.Address> addresses = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Address>>of(null, addresses);
        EasyMock.expect(computeRpcMock.listGlobalAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Address> page = compute.listGlobalAddresses();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(addresses.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListGlobalAddressesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.GLOBAL_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listGlobalAddresses(ComputeImplTest.ADDRESS_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listGlobalAddresses(ComputeImplTest.ADDRESS_LIST_PAGE_SIZE, ComputeImplTest.ADDRESS_LIST_PAGE_TOKEN, ComputeImplTest.ADDRESS_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListRegionAddresses() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListRegionAddressesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        ImmutableList<Address> nextAddressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextAddressList, AddressInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextAddressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListEmptyRegionAddresses() {
        ImmutableList<com.google.api.services.compute.model.Address> addresses = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Address>>of(null, addresses);
        EasyMock.expect(computeRpcMock.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Address> page = compute.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(addresses.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testListRegionAddressesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.ADDRESS_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listRegionAddresses(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.ADDRESS_LIST_PAGE_SIZE, ComputeImplTest.ADDRESS_LIST_PAGE_TOKEN, ComputeImplTest.ADDRESS_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testAggregatedListAddresses() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listAddresses();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testAggregatedListAddressesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        ImmutableList<Address> nextAddressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextAddressList, AddressInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listAddresses(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listAddresses();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextAddressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testAggregatedListEmptyAddresses() {
        ImmutableList<com.google.api.services.compute.model.Address> addresses = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Address>>of(null, addresses);
        EasyMock.expect(computeRpcMock.listAddresses(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Page<Address> page = compute.listAddresses();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(addresses.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testAggregatedListAddressesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Address> addressList = ImmutableList.of(new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)), new Address(compute, new AddressInfo.BuilderImpl(ComputeImplTest.REGION_ADDRESS)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Address>> result = Tuple.of(cursor, Iterables.transform(addressList, AddressInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listAddresses(ComputeImplTest.ADDRESS_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Address> page = compute.listAddresses(ComputeImplTest.ADDRESS_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.ADDRESS_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.ADDRESS_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(addressList.toArray(), Iterables.toArray(page.getValues(), Address.class));
    }

    @Test
    public void testCreateGlobalAddress() {
        EasyMock.expect(computeRpcMock.createGlobalAddress(ComputeImplTest.GLOBAL_ADDRESS.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        AddressId incompleteId = GlobalAddressId.of("address");
        Operation operation = compute.create(ComputeImplTest.GLOBAL_ADDRESS.toBuilder().setAddressId(incompleteId).build());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testCreateGlobalAddressWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createGlobalAddress(eq(ComputeImplTest.GLOBAL_ADDRESS.toPb()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.GLOBAL_ADDRESS, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testCreateRegionAddress() {
        EasyMock.expect(computeRpcMock.createRegionAddress(ComputeImplTest.REGION_ADDRESS_ID.getRegion(), ComputeImplTest.REGION_ADDRESS.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        AddressId incompleteId = RegionAddressId.of("region", "address");
        Operation operation = compute.create(ComputeImplTest.REGION_ADDRESS.toBuilder().setAddressId(incompleteId).build());
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testCreateRegionAddressWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createRegionAddress(eq(ComputeImplTest.REGION_ADDRESS_ID.getRegion()), eq(ComputeImplTest.REGION_ADDRESS.toPb()), capture(capturedOptions))).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.REGION_ADDRESS, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testCreateSnapshot() {
        EasyMock.expect(computeRpcMock.createSnapshot(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), ComputeImplTest.SNAPSHOT_ID.getSnapshot(), null, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.SNAPSHOT);
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testCreateSnapshotWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createSnapshot(eq(ComputeImplTest.DISK_ID.getZone()), eq(ComputeImplTest.DISK_ID.getDisk()), eq(ComputeImplTest.SNAPSHOT_ID.getSnapshot()), EasyMock.<String>isNull(), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.SNAPSHOT, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testGetSnapshot() {
        EasyMock.expect(computeRpcMock.getSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.SNAPSHOT.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Snapshot snapshot = compute.getSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot());
        Assert.assertEquals(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)), snapshot);
    }

    @Test
    public void testGetSnapshot_Null() {
        EasyMock.expect(computeRpcMock.getSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot()));
    }

    @Test
    public void testGetSnapshotWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getSnapshot(eq(ComputeImplTest.SNAPSHOT_ID.getSnapshot()), capture(capturedOptions))).andReturn(ComputeImplTest.SNAPSHOT.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Snapshot snapshot = compute.getSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot(), ComputeImplTest.SNAPSHOT_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.SNAPSHOT_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)), snapshot);
    }

    @Test
    public void testDeleteSnapshot_Operation() {
        EasyMock.expect(computeRpcMock.deleteSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.deleteSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot()));
    }

    @Test
    public void testDeleteSnapshotWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteSnapshot(eq(ComputeImplTest.SNAPSHOT_ID.getSnapshot()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteSnapshot(ComputeImplTest.SNAPSHOT_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeleteSnapshot_Null() {
        EasyMock.expect(computeRpcMock.deleteSnapshot(ComputeImplTest.SNAPSHOT_ID.getSnapshot(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteSnapshot(ComputeImplTest.SNAPSHOT_ID));
    }

    @Test
    public void testListSnapshots() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Snapshot> snapshotList = ImmutableList.of(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)), new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Snapshot>> result = Tuple.of(cursor, Iterables.transform(snapshotList, SnapshotInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSnapshots(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Snapshot> page = compute.listSnapshots();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(snapshotList.toArray(), Iterables.toArray(page.getValues(), Snapshot.class));
    }

    @Test
    public void testListSnapshotsNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Snapshot> snapshotList = ImmutableList.of(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)), new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)));
        ImmutableList<Snapshot> nextSnapshotList = ImmutableList.of(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Snapshot>> result = Tuple.of(cursor, Iterables.transform(snapshotList, SnapshotInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Snapshot>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextSnapshotList, SnapshotInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listSnapshots(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listSnapshots(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Snapshot> page = compute.listSnapshots();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(snapshotList.toArray(), Iterables.toArray(page.getValues(), Snapshot.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextSnapshotList.toArray(), Iterables.toArray(page.getValues(), Snapshot.class));
    }

    @Test
    public void testListEmptySnapshots() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Snapshot> snapshots = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Snapshot>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Snapshot>>of(null, snapshots);
        EasyMock.expect(computeRpcMock.listSnapshots(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Snapshot> page = compute.listSnapshots();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(snapshots.toArray(), Iterables.toArray(page.getValues(), Snapshot.class));
    }

    @Test
    public void testListSnapshotsWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Snapshot> snapshotList = ImmutableList.of(new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)), new Snapshot(compute, new SnapshotInfo.BuilderImpl(ComputeImplTest.SNAPSHOT)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Snapshot>> result = Tuple.of(cursor, Iterables.transform(snapshotList, SnapshotInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSnapshots(ComputeImplTest.SNAPSHOT_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Snapshot> page = compute.listSnapshots(ComputeImplTest.SNAPSHOT_LIST_PAGE_SIZE, ComputeImplTest.SNAPSHOT_LIST_PAGE_TOKEN, ComputeImplTest.SNAPSHOT_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(snapshotList.toArray(), Iterables.toArray(page.getValues(), Snapshot.class));
    }

    @Test
    public void testCreateImage() {
        EasyMock.expect(computeRpcMock.createImage(ComputeImplTest.IMAGE.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.IMAGE);
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testCreateImageWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createImage(eq(ComputeImplTest.IMAGE.toPb()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.IMAGE, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testGetImage() {
        EasyMock.expect(computeRpcMock.getImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.IMAGE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Image image = compute.getImage(ComputeImplTest.IMAGE_ID);
        Assert.assertEquals(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), image);
    }

    @Test
    public void testGetImage_Null() {
        EasyMock.expect(computeRpcMock.getImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getImage(ComputeImplTest.IMAGE_ID));
    }

    @Test
    public void testGetImageWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getImage(eq(ComputeImplTest.IMAGE_ID.getProject()), eq(ComputeImplTest.IMAGE_ID.getImage()), capture(capturedOptions))).andReturn(ComputeImplTest.IMAGE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Image image = compute.getImage(ComputeImplTest.IMAGE_ID, ComputeImplTest.IMAGE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.IMAGE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("sourceDisk"));
        Assert.assertTrue(selector.contains("rawDisk"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(42, selector.length());
        Assert.assertEquals(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), image);
    }

    @Test
    public void testDeleteImage_Operation() {
        EasyMock.expect(computeRpcMock.deleteImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.deleteImage(ComputeImplTest.IMAGE_ID));
    }

    @Test
    public void testDeleteImageWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteImage(eq(ComputeImplTest.PROJECT), eq(ComputeImplTest.IMAGE_ID.getImage()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteImage(ImageId.of("image"), ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeleteImage_Null() {
        EasyMock.expect(computeRpcMock.deleteImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteImage(ComputeImplTest.IMAGE_ID));
    }

    @Test
    public void testDeprecateImage_Operation() {
        EasyMock.expect(computeRpcMock.deprecateImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.DEPRECATION_STATUS.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.deprecate(ComputeImplTest.IMAGE_ID, ComputeImplTest.DEPRECATION_STATUS));
    }

    @Test
    public void testDeprecateImageWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deprecateImage(eq(ComputeImplTest.PROJECT), eq(ComputeImplTest.IMAGE_ID.getImage()), eq(ComputeImplTest.DEPRECATION_STATUS.toPb()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deprecate(ImageId.of("image"), ComputeImplTest.DEPRECATION_STATUS, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeprecateImage_Null() {
        EasyMock.expect(computeRpcMock.deprecateImage(ComputeImplTest.IMAGE_ID.getProject(), ComputeImplTest.IMAGE_ID.getImage(), ComputeImplTest.DEPRECATION_STATUS.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deprecate(ComputeImplTest.IMAGE_ID, ComputeImplTest.DEPRECATION_STATUS));
    }

    @Test
    public void testListImages() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Image> imageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.of(cursor, Iterables.transform(imageList, ImageInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listImages(ComputeImplTest.PROJECT, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(imageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListImagesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Image> imageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        ImmutableList<Image> nextImageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.of(cursor, Iterables.transform(imageList, ImageInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextImageList, ImageInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listImages(ComputeImplTest.PROJECT, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listImages(ComputeImplTest.PROJECT, nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(imageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextImageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListImagesForProject() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Image> imageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.of(cursor, Iterables.transform(imageList, ImageInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listImages("otherProject", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages("otherProject");
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(imageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListEmptyImages() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Image> images = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Image>>of(null, images);
        EasyMock.expect(computeRpcMock.listImages(ComputeImplTest.PROJECT, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(images.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListEmptyImagesForProject() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Image> images = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Image>>of(null, images);
        EasyMock.expect(computeRpcMock.listImages("otherProject", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages("otherProject");
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(images.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListImagesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Image> imageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.of(cursor, Iterables.transform(imageList, ImageInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listImages(ComputeImplTest.PROJECT, ComputeImplTest.IMAGE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages(ComputeImplTest.IMAGE_LIST_PAGE_SIZE, ComputeImplTest.IMAGE_LIST_PAGE_TOKEN, ComputeImplTest.IMAGE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(imageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testListImagesForProjectWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Image> imageList = ImmutableList.of(new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)), new Image(compute, new ImageInfo.BuilderImpl(ComputeImplTest.IMAGE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Image>> result = Tuple.of(cursor, Iterables.transform(imageList, ImageInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listImages("other", ComputeImplTest.IMAGE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Image> page = compute.listImages("other", ComputeImplTest.IMAGE_LIST_PAGE_SIZE, ComputeImplTest.IMAGE_LIST_PAGE_TOKEN, ComputeImplTest.IMAGE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(imageList.toArray(), Iterables.toArray(page.getValues(), Image.class));
    }

    @Test
    public void testGetDisk() {
        EasyMock.expect(computeRpcMock.getDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.DISK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Disk disk = compute.getDisk(ComputeImplTest.DISK_ID);
        Assert.assertEquals(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), disk);
    }

    @Test
    public void testGetDisk_Null() {
        EasyMock.expect(computeRpcMock.getDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getDisk(ComputeImplTest.DISK_ID));
    }

    @Test
    public void testGetDiskWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getDisk(eq(ComputeImplTest.DISK_ID.getZone()), eq(ComputeImplTest.DISK_ID.getDisk()), capture(capturedOptions))).andReturn(ComputeImplTest.DISK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Disk disk = compute.getDisk(ComputeImplTest.DISK_ID, ComputeImplTest.DISK_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.DISK_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("type"));
        Assert.assertTrue(selector.contains("sourceImage"));
        Assert.assertTrue(selector.contains("sourceSnapshot"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(55, selector.length());
        Assert.assertEquals(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), disk);
    }

    @Test
    public void testDeleteDisk_Operation() {
        EasyMock.expect(computeRpcMock.deleteDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.deleteDisk(ComputeImplTest.DISK_ID));
    }

    @Test
    public void testDeleteDiskWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteDisk(eq(ComputeImplTest.DISK_ID.getZone()), eq(ComputeImplTest.DISK_ID.getDisk()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteDisk(ComputeImplTest.DISK_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testDeleteDisk_Null() {
        EasyMock.expect(computeRpcMock.deleteDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteDisk(ComputeImplTest.DISK_ID));
    }

    @Test
    public void testListDisks() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks(ComputeImplTest.DISK_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testListDisksNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        ImmutableList<Disk> nextDiskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextDiskList, DiskInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_ID.getZone(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks(ComputeImplTest.DISK_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextDiskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testListEmptyDisks() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Disk> disks = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Disk>>of(null, disks);
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks(ComputeImplTest.DISK_ID.getZone());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(disks.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testListDisksWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_LIST_PAGE_SIZE, ComputeImplTest.DISK_LIST_PAGE_TOKEN, ComputeImplTest.DISK_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testAggregatedListDisks() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testAggregatedListDisksNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        ImmutableList<Disk> nextDiskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextDiskList, DiskInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listDisks(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextDiskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testAggregatedListEmptyDisks() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Disk> diskList = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Disk>>of(null, diskList);
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testAggregatedListDisksWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Disk> diskList = ImmutableList.of(new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)), new Disk(compute, new DiskInfo.BuilderImpl(ComputeImplTest.DISK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Disk>> result = Tuple.of(cursor, Iterables.transform(diskList, DiskInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listDisks(ComputeImplTest.DISK_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Disk> page = compute.listDisks(ComputeImplTest.DISK_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.DISK_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.DISK_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(diskList.toArray(), Iterables.toArray(page.getValues(), Disk.class));
    }

    @Test
    public void testCreateDisk() {
        EasyMock.expect(computeRpcMock.createDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        DiskId diskId = DiskId.of("zone", "disk");
        DiskTypeId diskTypeId = DiskTypeId.of("zone", "diskType");
        DiskInfo disk = ComputeImplTest.DISK.toBuilder().setDiskId(diskId).setConfiguration(StandardDiskConfiguration.of(diskTypeId)).build();
        Operation operation = compute.create(disk);
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testCreateDiskWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createDisk(eq(ComputeImplTest.DISK_ID.getZone()), eq(ComputeImplTest.DISK.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.DISK, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testResizeDisk_Operation() {
        EasyMock.expect(computeRpcMock.resizeDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), 42L, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.resize(ComputeImplTest.DISK_ID, 42L));
    }

    @Test
    public void testResizeDiskWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.resizeDisk(eq(ComputeImplTest.DISK_ID.getZone()), eq(ComputeImplTest.DISK_ID.getDisk()), eq(42L), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.resize(ComputeImplTest.DISK_ID, 42L, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testResizeDisk_Null() {
        EasyMock.expect(computeRpcMock.resizeDisk(ComputeImplTest.DISK_ID.getZone(), ComputeImplTest.DISK_ID.getDisk(), 42L, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.resize(ComputeImplTest.DISK_ID, 42L));
    }

    @Test
    public void testGetSubnetwork() {
        EasyMock.expect(computeRpcMock.getSubnetwork(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_ID.getSubnetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.SUBNETWORK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Subnetwork subnetwork = compute.getSubnetwork(ComputeImplTest.SUBNETWORK_ID);
        Assert.assertEquals(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), subnetwork);
    }

    @Test
    public void testGetSubnetwork_Null() {
        EasyMock.expect(computeRpcMock.getSubnetwork(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_ID.getSubnetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getSubnetwork(ComputeImplTest.SUBNETWORK_ID));
    }

    @Test
    public void testGetSubnetworkWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getSubnetwork(eq(ComputeImplTest.SUBNETWORK_ID.getRegion()), eq(ComputeImplTest.SUBNETWORK_ID.getSubnetwork()), capture(capturedOptions))).andReturn(ComputeImplTest.SUBNETWORK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Subnetwork subnetwork = compute.getSubnetwork(ComputeImplTest.SUBNETWORK_ID, ComputeImplTest.SUBNETWORK_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.SUBNETWORK_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), subnetwork);
    }

    @Test
    public void testDeleteSubnetwork_Operation() {
        EasyMock.expect(computeRpcMock.deleteSubnetwork(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_ID.getSubnetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(regionOperation, compute.deleteSubnetwork(ComputeImplTest.SUBNETWORK_ID));
    }

    @Test
    public void testDeleteSubnetworkWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteSubnetwork(eq(ComputeImplTest.SUBNETWORK_ID.getRegion()), eq(ComputeImplTest.SUBNETWORK_ID.getSubnetwork()), capture(capturedOptions))).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteSubnetwork(ComputeImplTest.SUBNETWORK_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testDeleteSubnetwork_Null() {
        EasyMock.expect(computeRpcMock.deleteSubnetwork(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_ID.getSubnetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteSubnetwork(ComputeImplTest.SUBNETWORK_ID));
    }

    @Test
    public void testListSubnetworks() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testListSubnetworksNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        ImmutableList<Subnetwork> nextSubnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextSubnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextSubnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testListEmptySubnetworks() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Subnetwork> subnetworks = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Subnetwork>>of(null, subnetworks);
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(subnetworks.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testListSubnetworksWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK_LIST_PAGE_SIZE, ComputeImplTest.SUBNETWORK_LIST_PAGE_TOKEN, ComputeImplTest.SUBNETWORK_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testAggregatedListSubnetworks() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testAggregatedListSubnetworksNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        ImmutableList<Subnetwork> nextSubnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextSubnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listSubnetworks(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextSubnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testAggregatedListEmptySubnetworks() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Subnetwork> subnetworks = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Subnetwork>>of(null, subnetworks);
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(subnetworks.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testAggregatedListSubnetworksWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Subnetwork> subnetworkList = ImmutableList.of(new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)), new Subnetwork(compute, new SubnetworkInfo.BuilderImpl(ComputeImplTest.SUBNETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Subnetwork>> result = Tuple.of(cursor, Iterables.transform(subnetworkList, SubnetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listSubnetworks(ComputeImplTest.SUBNETWORK_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Subnetwork> page = compute.listSubnetworks(ComputeImplTest.SUBNETWORK_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.SUBNETWORK_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.SUBNETWORK_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(subnetworkList.toArray(), Iterables.toArray(page.getValues(), Subnetwork.class));
    }

    @Test
    public void testCreateSubnetwork() {
        EasyMock.expect(computeRpcMock.createSubnetwork(ComputeImplTest.SUBNETWORK_ID.getRegion(), ComputeImplTest.SUBNETWORK.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        SubnetworkId subnetworkId = SubnetworkId.of("region", "network");
        NetworkId networkId = NetworkId.of("network");
        SubnetworkInfo subnetwork = SubnetworkInfo.of(subnetworkId, networkId, "192.168.0.0/16");
        Operation operation = compute.create(subnetwork);
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testCreateSubnetworkWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createSubnetwork(eq(ComputeImplTest.SUBNETWORK_ID.getRegion()), eq(ComputeImplTest.SUBNETWORK.toPb()), capture(capturedOptions))).andReturn(regionOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.SUBNETWORK, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(regionOperation, operation);
    }

    @Test
    public void testGetNetwork() {
        EasyMock.expect(computeRpcMock.getNetwork(ComputeImplTest.NETWORK_ID.getNetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.NETWORK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Network network = compute.getNetwork(ComputeImplTest.NETWORK_ID.getNetwork());
        Assert.assertEquals(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)), network);
    }

    @Test
    public void testGetNetwork_Null() {
        EasyMock.expect(computeRpcMock.getNetwork(ComputeImplTest.NETWORK_ID.getNetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getNetwork(ComputeImplTest.NETWORK_ID.getNetwork()));
    }

    @Test
    public void testGetNetworkWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getNetwork(eq(ComputeImplTest.NETWORK_ID.getNetwork()), capture(capturedOptions))).andReturn(ComputeImplTest.NETWORK.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Network network = compute.getNetwork(ComputeImplTest.NETWORK_ID.getNetwork(), ComputeImplTest.NETWORK_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.NETWORK_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertTrue(selector.contains("IPv4Range"));
        Assert.assertTrue(selector.contains("autoCreateSubnetworks"));
        Assert.assertEquals(55, selector.length());
        Assert.assertEquals(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)), network);
    }

    @Test
    public void testDeleteNetwork_Operation() {
        EasyMock.expect(computeRpcMock.deleteNetwork(ComputeImplTest.NETWORK_ID.getNetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(globalOperation, compute.deleteNetwork(ComputeImplTest.NETWORK_ID));
    }

    @Test
    public void testDeleteNetworkWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteNetwork(eq(ComputeImplTest.NETWORK_ID.getNetwork()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteNetwork(ComputeImplTest.NETWORK_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testDeleteNetwork_Null() {
        EasyMock.expect(computeRpcMock.deleteNetwork(ComputeImplTest.NETWORK_ID.getNetwork(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteNetwork(ComputeImplTest.NETWORK_ID));
    }

    @Test
    public void testListNetworks() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Network> networkList = ImmutableList.of(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)), new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Network>> result = Tuple.of(cursor, Iterables.transform(networkList, NetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listNetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Network> page = compute.listNetworks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(networkList.toArray(), Iterables.toArray(page.getValues(), Network.class));
    }

    @Test
    public void testListNetworksNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Network> networkList = ImmutableList.of(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)), new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)));
        ImmutableList<Network> nextNetworkList = ImmutableList.of(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Network>> result = Tuple.of(cursor, Iterables.transform(networkList, NetworkInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Network>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextNetworkList, NetworkInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listNetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listNetworks(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Network> page = compute.listNetworks();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(networkList.toArray(), Iterables.toArray(page.getValues(), Network.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextNetworkList.toArray(), Iterables.toArray(page.getValues(), Network.class));
    }

    @Test
    public void testListEmptyNetworks() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Network> networks = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Network>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Network>>of(null, networks);
        EasyMock.expect(computeRpcMock.listNetworks(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Network> page = compute.listNetworks();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(networks.toArray(), Iterables.toArray(page.getValues(), Network.class));
    }

    @Test
    public void testListNetworksWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Network> networkList = ImmutableList.of(new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)), new Network(compute, new NetworkInfo.BuilderImpl(ComputeImplTest.NETWORK)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Network>> result = Tuple.of(cursor, Iterables.transform(networkList, NetworkInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listNetworks(ComputeImplTest.NETWORK_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Network> page = compute.listNetworks(ComputeImplTest.NETWORK_LIST_PAGE_SIZE, ComputeImplTest.NETWORK_LIST_PAGE_TOKEN, ComputeImplTest.NETWORK_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(networkList.toArray(), Iterables.toArray(page.getValues(), Network.class));
    }

    @Test
    public void testCreateNetwork() {
        EasyMock.expect(computeRpcMock.createNetwork(ComputeImplTest.NETWORK.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        NetworkInfo network = NetworkInfo.of(NetworkId.of("network"), StandardNetworkConfiguration.of("192.168.0.0/16"));
        Operation operation = compute.create(network);
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testCreateNetworkWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createNetwork(eq(ComputeImplTest.NETWORK.toPb()), capture(capturedOptions))).andReturn(globalOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.NETWORK, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(globalOperation, operation);
    }

    @Test
    public void testGetInstance() {
        EasyMock.expect(computeRpcMock.getInstance(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(ComputeImplTest.INSTANCE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Instance instance = compute.getInstance(ComputeImplTest.INSTANCE_ID);
        Assert.assertEquals(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), instance);
    }

    @Test
    public void testGetInstance_Null() {
        EasyMock.expect(computeRpcMock.getInstance(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getInstance(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testGetInstanceWithSelectedFields() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.getInstance(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), capture(capturedOptions))).andReturn(ComputeImplTest.INSTANCE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Instance instance = compute.getInstance(ComputeImplTest.INSTANCE_ID, ComputeImplTest.INSTANCE_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.INSTANCE_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), instance);
    }

    @Test
    public void testDeleteInstance_Operation() {
        EasyMock.expect(computeRpcMock.deleteInstance(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.deleteInstance(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testDeleteInstanceWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteInstance(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteInstance(ComputeImplTest.INSTANCE_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testDeleteInstance_Null() {
        EasyMock.expect(computeRpcMock.deleteInstance(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteInstance(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testListInstances() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances(ComputeImplTest.INSTANCE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testListInstancesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        ImmutableList<Instance> nextInstanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextInstanceList, InstanceInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances(ComputeImplTest.INSTANCE_ID.getZone());
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextInstanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testListEmptyInstances() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Instance> instances = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Instance>>of(null, instances);
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances(ComputeImplTest.INSTANCE_ID.getZone());
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(instances.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testListInstancesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_LIST_PAGE_SIZE, ComputeImplTest.INSTANCE_LIST_PAGE_TOKEN, ComputeImplTest.INSTANCE_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testAggregatedListInstances() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testAggregatedListInstancesNextPage() {
        String cursor = "cursor";
        String nextCursor = "nextCursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        ImmutableList<Instance> nextInstanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> nextResult = Tuple.of(nextCursor, Iterables.transform(nextInstanceList, InstanceInfo.TO_PB_FUNCTION));
        Map<ComputeRpc.Option, ?> nextOptions = ImmutableMap.of(PAGE_TOKEN, cursor);
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.expect(computeRpcMock.listInstances(nextOptions)).andReturn(nextResult);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances();
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
        page = page.getNextPage();
        Assert.assertEquals(nextCursor, page.getNextPageToken());
        Assert.assertArrayEquals(nextInstanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testAggregatedListEmptyInstances() {
        compute = options.getService();
        ImmutableList<com.google.api.services.compute.model.Instance> instanceList = ImmutableList.of();
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.<String, Iterable<com.google.api.services.compute.model.Instance>>of(null, instanceList);
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances();
        Assert.assertNull(page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testAggregatedListInstancesWithOptions() {
        String cursor = "cursor";
        compute = options.getService();
        ImmutableList<Instance> instanceList = ImmutableList.of(new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)), new Instance(compute, new InstanceInfo.BuilderImpl(ComputeImplTest.INSTANCE)));
        Tuple<String, Iterable<com.google.api.services.compute.model.Instance>> result = Tuple.of(cursor, Iterables.transform(instanceList, InstanceInfo.TO_PB_FUNCTION));
        EasyMock.expect(computeRpcMock.listInstances(ComputeImplTest.INSTANCE_LIST_OPTIONS)).andReturn(result);
        EasyMock.replay(computeRpcMock);
        Page<Instance> page = compute.listInstances(ComputeImplTest.INSTANCE_AGGREGATED_LIST_PAGE_SIZE, ComputeImplTest.INSTANCE_AGGREGATED_LIST_PAGE_TOKEN, ComputeImplTest.INSTANCE_AGGREGATED_LIST_FILTER);
        Assert.assertEquals(cursor, page.getNextPageToken());
        Assert.assertArrayEquals(instanceList.toArray(), Iterables.toArray(page.getValues(), Instance.class));
    }

    @Test
    public void testCreateInstance() {
        EasyMock.expect(computeRpcMock.createInstance(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        InstanceInfo instance = InstanceInfo.of(InstanceId.of("zone", "instance"), MachineTypeId.of("zone", "type"), ComputeImplTest.ATTACHED_DISK, NetworkInterface.of(NetworkId.of("network")));
        Operation operation = compute.create(instance);
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testCreateInstanceWithOptions() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.createInstance(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.create(ComputeImplTest.INSTANCE, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testAddAccessConfig_Operation() {
        AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        EasyMock.expect(computeRpcMock.addAccessConfig(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "networkInterface", accessConfig.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.addAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", accessConfig));
    }

    @Test
    public void testAddAccessConfigWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        EasyMock.expect(computeRpcMock.addAccessConfig(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq("networkInterface"), eq(accessConfig.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.addAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", accessConfig, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testAddAccessConfig_Null() {
        AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        EasyMock.expect(computeRpcMock.addAccessConfig(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "networkInterface", accessConfig.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.addAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", accessConfig));
    }

    @Test
    public void testAttachDisk_Operation() {
        AttachedDisk attachedDisk = AttachedDisk.of(ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        EasyMock.expect(computeRpcMock.attachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), attachedDisk.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.attachDisk(ComputeImplTest.INSTANCE_ID, ComputeImplTest.PERSISTENT_DISK_CONFIGURATION));
    }

    @Test
    public void testAttachDiskWithSelectedFields_Operation() {
        AttachedDisk attachedDisk = AttachedDisk.of(ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.attachDisk(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(attachedDisk.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.attachDisk(ComputeImplTest.INSTANCE_ID, ComputeImplTest.PERSISTENT_DISK_CONFIGURATION, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testAttachDisk_Null() {
        AttachedDisk attachedDisk = AttachedDisk.of(ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        EasyMock.expect(computeRpcMock.attachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), attachedDisk.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.attachDisk(ComputeImplTest.INSTANCE_ID, ComputeImplTest.PERSISTENT_DISK_CONFIGURATION));
    }

    @Test
    public void testAttachDiskName_Operation() {
        AttachedDisk attachedDisk = AttachedDisk.of("dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        EasyMock.expect(computeRpcMock.attachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), attachedDisk.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.attachDisk(ComputeImplTest.INSTANCE_ID, "dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION));
    }

    @Test
    public void testAttachDiskNameWithSelectedFields_Operation() {
        AttachedDisk attachedDisk = AttachedDisk.of("dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.attachDisk(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(attachedDisk.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.attachDisk(ComputeImplTest.INSTANCE_ID, "dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testAttachDiskName_Null() {
        AttachedDisk attachedDisk = AttachedDisk.of("dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION);
        EasyMock.expect(computeRpcMock.attachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), attachedDisk.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.attachDisk(ComputeImplTest.INSTANCE_ID, "dev0", ComputeImplTest.PERSISTENT_DISK_CONFIGURATION));
    }

    @Test
    public void testDeleteAccessConfig_Operation() {
        EasyMock.expect(computeRpcMock.deleteAccessConfig(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "networkInterface", "accessConfig", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.deleteAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", "accessConfig"));
    }

    @Test
    public void testDeleteAccessConfigWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.deleteAccessConfig(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq("networkInterface"), eq("accessConfig"), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.deleteAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", "accessConfig", ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testDeleteAccessConfig_Null() {
        EasyMock.expect(computeRpcMock.deleteAccessConfig(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "networkInterface", "accessConfig", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.deleteAccessConfig(ComputeImplTest.INSTANCE_ID, "networkInterface", "accessConfig"));
    }

    @Test
    public void testDetachDisk_Operation() {
        EasyMock.expect(computeRpcMock.detachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "device", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.detachDisk(ComputeImplTest.INSTANCE_ID, "device"));
    }

    @Test
    public void testDetachDiskWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.detachDisk(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq("device"), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.detachDisk(ComputeImplTest.INSTANCE_ID, "device", ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testDetachDisk_Null() {
        EasyMock.expect(computeRpcMock.detachDisk(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "device", ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.detachDisk(ComputeImplTest.INSTANCE_ID, "device"));
    }

    @Test
    public void testSerialPortOutputFromPort() {
        String output = "output";
        EasyMock.expect(computeRpcMock.getSerialPortOutput(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), 2, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(output);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(output, compute.getSerialPortOutput(ComputeImplTest.INSTANCE_ID, 2));
    }

    @Test
    public void testSerialPortOutputDefault() {
        String output = "output";
        EasyMock.expect(computeRpcMock.getSerialPortOutput(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), null, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(output);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(output, compute.getSerialPortOutput(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testSerialPortOutputFromPort_Null() {
        EasyMock.expect(computeRpcMock.getSerialPortOutput(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), 2, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getSerialPortOutput(ComputeImplTest.INSTANCE_ID, 2));
    }

    @Test
    public void testSerialPortOutputDefault_Null() {
        EasyMock.expect(computeRpcMock.getSerialPortOutput(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), null, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.getSerialPortOutput(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testResetInstance_Operation() {
        EasyMock.expect(computeRpcMock.reset(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.reset(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testResetInstanceWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.reset(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.reset(ComputeImplTest.INSTANCE_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testResetInstance_Null() {
        EasyMock.expect(computeRpcMock.reset(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.reset(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testSetDiskAutodelete_Operation() {
        EasyMock.expect(computeRpcMock.setDiskAutoDelete(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "device", true, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.setDiskAutoDelete(ComputeImplTest.INSTANCE_ID, "device", true));
    }

    @Test
    public void testSetDiskAutodeleteWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.setDiskAutoDelete(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq("device"), eq(true), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.setDiskAutoDelete(ComputeImplTest.INSTANCE_ID, "device", true, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testSetDiskAutodelete_Null() {
        EasyMock.expect(computeRpcMock.setDiskAutoDelete(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), "device", false, ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.setDiskAutoDelete(ComputeImplTest.INSTANCE_ID, "device", false));
    }

    @Test
    public void testSetMachineType_Operation() {
        EasyMock.expect(computeRpcMock.setMachineType(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.MACHINE_TYPE_ID.getSelfLink(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.setMachineType(ComputeImplTest.INSTANCE_ID, MachineTypeId.of("zone", "type")));
    }

    @Test
    public void testSetMachineTypeWithOptions_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.setMachineType(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(ComputeImplTest.MACHINE_TYPE_ID.getSelfLink()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.setMachineType(ComputeImplTest.INSTANCE_ID, MachineTypeId.of("zone", "type"), ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testSetMachineType_Null() {
        EasyMock.expect(computeRpcMock.setMachineType(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.MACHINE_TYPE_ID.getSelfLink(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.setMachineType(ComputeImplTest.INSTANCE_ID, MachineTypeId.of("zone", "type")));
    }

    @Test
    public void testSetMetadata_Operation() {
        Metadata metadata = Metadata.newBuilder().add("key", "value").setFingerprint("fingerprint").build();
        EasyMock.expect(computeRpcMock.setMetadata(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), metadata.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.setMetadata(ComputeImplTest.INSTANCE_ID, metadata));
    }

    @Test
    public void testSetMetadataWithOptions_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        Metadata metadata = Metadata.newBuilder().add("key", "value").setFingerprint("fingerprint").build();
        EasyMock.expect(computeRpcMock.setMetadata(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(metadata.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.setMetadata(ComputeImplTest.INSTANCE_ID, metadata, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testSetMetadata_Null() {
        Metadata metadata = Metadata.newBuilder().add("key", "value").setFingerprint("fingerprint").build();
        EasyMock.expect(computeRpcMock.setMetadata(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), metadata.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.setMetadata(ComputeImplTest.INSTANCE_ID, metadata));
    }

    @Test
    public void testSetSchedulingOptions_Operation() {
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        EasyMock.expect(computeRpcMock.setScheduling(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), schedulingOptions.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.setSchedulingOptions(ComputeImplTest.INSTANCE_ID, schedulingOptions));
    }

    @Test
    public void testSetSchedulingOptionsWithOptions_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        EasyMock.expect(computeRpcMock.setScheduling(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(schedulingOptions.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.setSchedulingOptions(ComputeImplTest.INSTANCE_ID, schedulingOptions, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testSetSchedulingOptions_Null() {
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        EasyMock.expect(computeRpcMock.setScheduling(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), schedulingOptions.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.setSchedulingOptions(ComputeImplTest.INSTANCE_ID, schedulingOptions));
    }

    @Test
    public void testTags_Operation() {
        Tags tags = Tags.of("tag1", "tag2");
        EasyMock.expect(computeRpcMock.setTags(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), tags.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.setTags(ComputeImplTest.INSTANCE_ID, tags));
    }

    @Test
    public void testSetTagsWithOptions_Operation() {
        Tags tags = Tags.of("tag1", "tag2");
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.setTags(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), eq(tags.toPb()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.setTags(ComputeImplTest.INSTANCE_ID, tags, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testSetTags_Null() {
        Tags tags = Tags.of("tag1", "tag2");
        EasyMock.expect(computeRpcMock.setTags(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), tags.toPb(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.setTags(ComputeImplTest.INSTANCE_ID, tags));
    }

    @Test
    public void testStartInstance_Operation() {
        EasyMock.expect(computeRpcMock.start(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.start(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testStartInstanceWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.start(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.start(ComputeImplTest.INSTANCE_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testStartInstance_Null() {
        EasyMock.expect(computeRpcMock.start(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.start(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testStopInstance_Operation() {
        EasyMock.expect(computeRpcMock.stop(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertEquals(zoneOperation, compute.stop(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testStopInstanceWithSelectedFields_Operation() {
        Capture<Map<ComputeRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(computeRpcMock.stop(eq(ComputeImplTest.INSTANCE_ID.getZone()), eq(ComputeImplTest.INSTANCE_ID.getInstance()), capture(capturedOptions))).andReturn(zoneOperation.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Operation operation = compute.stop(ComputeImplTest.INSTANCE_ID, ComputeImplTest.OPERATION_OPTION_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(ComputeImplTest.OPERATION_OPTION_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains("selfLink"));
        Assert.assertTrue(selector.contains("id"));
        Assert.assertTrue(selector.contains("description"));
        Assert.assertEquals(23, selector.length());
        Assert.assertEquals(zoneOperation, operation);
    }

    @Test
    public void testStopInstance_Null() {
        EasyMock.expect(computeRpcMock.stop(ComputeImplTest.INSTANCE_ID.getZone(), ComputeImplTest.INSTANCE_ID.getInstance(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andReturn(null);
        EasyMock.replay(computeRpcMock);
        compute = options.getService();
        Assert.assertNull(compute.stop(ComputeImplTest.INSTANCE_ID));
    }

    @Test
    public void testRetryableException() {
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andThrow(new ComputeException(500, "InternalError")).andReturn(ComputeImplTest.DISK_TYPE.toPb());
        EasyMock.replay(computeRpcMock);
        compute = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        DiskType diskType = compute.getDiskType(ComputeImplTest.DISK_TYPE_ID);
        Assert.assertEquals(ComputeImplTest.DISK_TYPE, diskType);
    }

    @Test
    public void testNonRetryableException() {
        String exceptionMessage = "Not Implemented";
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andThrow(new ComputeException(501, exceptionMessage));
        EasyMock.replay(computeRpcMock);
        compute = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(ComputeException.class);
        thrown.expectMessage(exceptionMessage);
        compute.getDiskType(ComputeImplTest.DISK_TYPE_ID);
    }

    @Test
    public void testRuntimeException() {
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(computeRpcMock.getDiskType(ComputeImplTest.DISK_TYPE_ID.getZone(), ComputeImplTest.DISK_TYPE_ID.getType(), ComputeImplTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(computeRpcMock);
        compute = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(ComputeException.class);
        thrown.expectMessage(exceptionMessage);
        compute.getDiskType(ComputeImplTest.DISK_TYPE_ID);
    }
}

