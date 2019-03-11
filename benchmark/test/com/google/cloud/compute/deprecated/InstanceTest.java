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
import Compute.InstanceOption;
import Compute.OperationOption;
import InstanceInfo.Status;
import NetworkInterface.AccessConfig;
import SchedulingOptions.Maintenance.MIGRATE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class InstanceTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final InstanceId INSTANCE_ID = InstanceId.of("project", "zone", "instance");

    private static final Status STATUS = Status.RUNNING;

    private static final String STATUS_MESSAGE = "statusMessage";

    private static final Tags TAGS = Tags.newBuilder().setValues("tag1", "tag2").setFingerprint("fingerprint").build();

    private static final MachineTypeId MACHINE_TYPE = MachineTypeId.of("project", "zone", "type");

    private static final Boolean CAN_IP_FORWARD = true;

    private static final NetworkInterface NETWORK_INTERFACE = NetworkInterface.of(NetworkId.of("project", "network"));

    private static final List<NetworkInterface> NETWORK_INTERFACES = ImmutableList.of(InstanceTest.NETWORK_INTERFACE);

    private static final DiskId DISK_ID = DiskId.of("project", "zone", "disk");

    private static final AttachedDisk ATTACHED_DISK = AttachedDisk.of(PersistentDiskConfiguration.of(InstanceTest.DISK_ID));

    private static final List<AttachedDisk> ATTACHED_DISKS = ImmutableList.of(InstanceTest.ATTACHED_DISK);

    private static final Metadata METADATA = Metadata.newBuilder().add("key1", "value1").add("key2", "value2").setFingerprint("fingerprint").build();

    private static final ServiceAccount SERVICE_ACCOUNT = ServiceAccount.of("email", ImmutableList.of("scope1"));

    private static final List<ServiceAccount> SERVICE_ACCOUNTS = ImmutableList.of(InstanceTest.SERVICE_ACCOUNT);

    private static final SchedulingOptions SCHEDULING_OPTIONS = SchedulingOptions.preemptible();

    private static final String CPU_PLATFORM = "cpuPlatform";

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Instance instance;

    private Instance expectedInstance;

    @Test
    public void testToBuilder() {
        initializeExpectedInstance(8);
        compareInstance(expectedInstance, expectedInstance.toBuilder().build());
        Instance newInstance = build();
        Assert.assertEquals("newDescription", newInstance.getDescription());
        newInstance = newInstance.toBuilder().setDescription("description").build();
        compareInstance(expectedInstance, newInstance);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedInstance(5);
        InstanceInfo instanceInfo = InstanceInfo.of(InstanceTest.INSTANCE_ID, InstanceTest.MACHINE_TYPE, InstanceTest.ATTACHED_DISK, InstanceTest.NETWORK_INTERFACE);
        Instance instance = new Instance(serviceMockReturnsOptions, new InstanceInfo.BuilderImpl(instanceInfo));
        compareInstance(instance, instance.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedInstance(2);
        Assert.assertEquals(InstanceTest.GENERATED_ID, expectedInstance.getGeneratedId());
        Assert.assertEquals(InstanceTest.INSTANCE_ID, expectedInstance.getInstanceId());
        Assert.assertEquals(InstanceTest.CREATION_TIMESTAMP, expectedInstance.getCreationTimestamp());
        Assert.assertEquals(InstanceTest.DESCRIPTION, expectedInstance.getDescription());
        Assert.assertEquals(InstanceTest.STATUS, expectedInstance.getStatus());
        Assert.assertEquals(InstanceTest.STATUS_MESSAGE, expectedInstance.getStatusMessage());
        Assert.assertEquals(InstanceTest.TAGS, expectedInstance.getTags());
        Assert.assertEquals(InstanceTest.MACHINE_TYPE, expectedInstance.getMachineType());
        Assert.assertEquals(InstanceTest.CAN_IP_FORWARD, expectedInstance.canIpForward());
        Assert.assertEquals(InstanceTest.NETWORK_INTERFACES, expectedInstance.getNetworkInterfaces());
        Assert.assertEquals(InstanceTest.ATTACHED_DISKS, expectedInstance.getAttachedDisks());
        Assert.assertEquals(InstanceTest.METADATA, expectedInstance.getMetadata());
        Assert.assertEquals(InstanceTest.SERVICE_ACCOUNTS, expectedInstance.getServiceAccounts());
        Assert.assertEquals(InstanceTest.SCHEDULING_OPTIONS, expectedInstance.getSchedulingOptions());
        Assert.assertEquals(InstanceTest.CPU_PLATFORM, expectedInstance.getCpuPlatform());
        Assert.assertSame(serviceMockReturnsOptions, expectedInstance.getCompute());
        InstanceInfo instanceInfo = InstanceInfo.of(InstanceTest.INSTANCE_ID, InstanceTest.MACHINE_TYPE, InstanceTest.ATTACHED_DISK, InstanceTest.NETWORK_INTERFACE);
        Instance instance = new Instance(serviceMockReturnsOptions, new InstanceInfo.BuilderImpl(instanceInfo));
        Assert.assertNull(instance.getGeneratedId());
        Assert.assertEquals(InstanceTest.INSTANCE_ID, instance.getInstanceId());
        Assert.assertNull(instance.getCreationTimestamp());
        Assert.assertNull(instance.getDescription());
        Assert.assertNull(instance.getStatus());
        Assert.assertNull(instance.getStatusMessage());
        Assert.assertNull(instance.getTags());
        Assert.assertEquals(InstanceTest.MACHINE_TYPE, instance.getMachineType());
        Assert.assertNull(instance.canIpForward());
        Assert.assertEquals(InstanceTest.NETWORK_INTERFACES, instance.getNetworkInterfaces());
        Assert.assertEquals(InstanceTest.ATTACHED_DISKS, instance.getAttachedDisks());
        Assert.assertNull(instance.getMetadata());
        Assert.assertNull(instance.getServiceAccounts());
        Assert.assertNull(instance.getSchedulingOptions());
        Assert.assertNull(instance.getCpuPlatform());
        Assert.assertSame(serviceMockReturnsOptions, instance.getCompute());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedInstance(8);
        compareInstance(expectedInstance, Instance.fromPb(serviceMockReturnsOptions, expectedInstance.toPb()));
        Instance instance = build();
        compareInstance(instance, Instance.fromPb(serviceMockReturnsOptions, instance.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.deleteInstance(InstanceTest.INSTANCE_ID)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteInstance(InstanceTest.INSTANCE_ID)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedInstance(1);
        Compute[] expectedOptions = new InstanceOption[]{ InstanceOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getInstance(InstanceTest.INSTANCE_ID, expectedOptions)).andReturn(expectedInstance);
        replay(compute);
        initializeInstance();
        Assert.assertTrue(instance.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedInstance(1);
        Compute[] expectedOptions = new InstanceOption[]{ InstanceOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getInstance(InstanceTest.INSTANCE_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertFalse(instance.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedInstance(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getInstance(InstanceTest.INSTANCE_ID)).andReturn(expectedInstance);
        replay(compute);
        initializeInstance();
        Instance updatedInstance = instance.reload();
        compareInstance(expectedInstance, updatedInstance);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getInstance(InstanceTest.INSTANCE_ID)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedInstance(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getInstance(InstanceTest.INSTANCE_ID, InstanceOption.fields())).andReturn(expectedInstance);
        replay(compute);
        initializeInstance();
        Instance updateInstance = instance.reload(InstanceOption.fields());
        compareInstance(expectedInstance, updateInstance);
        verify(compute);
    }

    @Test
    public void testAddAccessConfig() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        NetworkInterface.AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.addAccessConfig(InstanceTest.INSTANCE_ID, "nic0", accessConfig)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.addAccessConfig("nic0", accessConfig));
    }

    @Test
    public void testAddAccessConfig_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        NetworkInterface.AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        expect(compute.addAccessConfig(InstanceTest.INSTANCE_ID, "nic0", accessConfig)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.addAccessConfig("nic0", accessConfig));
    }

    @Test
    public void testAddAccessConfigWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        NetworkInterface.AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.addAccessConfig(InstanceTest.INSTANCE_ID, "nic0", accessConfig, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.addAccessConfig("nic0", accessConfig, OperationOption.fields()));
    }

    @Test
    public void testAttachDisk() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, configuration)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk(configuration));
    }

    @Test
    public void testAttachDisk_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, configuration)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.attachDisk(configuration));
    }

    @Test
    public void testAttachDiskWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, configuration, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk(configuration, OperationOption.fields()));
    }

    @Test
    public void testAttachDiskName() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk("dev0", configuration));
    }

    @Test
    public void testAttachDiskName_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.attachDisk("dev0", configuration));
    }

    @Test
    public void testAttachDiskNameWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk("dev0", configuration, OperationOption.fields()));
    }

    @Test
    public void testAttachDiskNameIndex() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration, 1)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk("dev0", configuration, 1));
    }

    @Test
    public void testAttachDiskNameIndex_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration, 1)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.attachDisk("dev0", configuration, 1));
    }

    @Test
    public void testAttachDiskNameIndexWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        AttachedDisk.PersistentDiskConfiguration configuration = PersistentDiskConfiguration.of(InstanceTest.DISK_ID);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.attachDisk(InstanceTest.INSTANCE_ID, "dev0", configuration, 1, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.attachDisk("dev0", configuration, 1, OperationOption.fields()));
    }

    @Test
    public void testDeleteAccessConfig() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.deleteAccessConfig(InstanceTest.INSTANCE_ID, "nic0", "NAT")).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.deleteAccessConfig("nic0", "NAT"));
    }

    @Test
    public void testDeleteAccessConfig_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteAccessConfig(InstanceTest.INSTANCE_ID, "nic0", "NAT")).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.deleteAccessConfig("nic0", "NAT"));
    }

    @Test
    public void testDeleteAccessConfigWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.deleteAccessConfig(InstanceTest.INSTANCE_ID, "nic0", "NAT", OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.deleteAccessConfig("nic0", "NAT", OperationOption.fields()));
    }

    @Test
    public void testDetachDisk() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.detachDisk(InstanceTest.INSTANCE_ID, "dev0")).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.detachDisk("dev0"));
    }

    @Test
    public void testDetachDisk_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.detachDisk(InstanceTest.INSTANCE_ID, "dev0")).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.detachDisk("dev0"));
    }

    @Test
    public void testDetachDiskWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.detachDisk(InstanceTest.INSTANCE_ID, "dev0", OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.detachDisk("dev0", OperationOption.fields()));
    }

    @Test
    public void testGetSerialPortOutputWithNumber() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSerialPortOutput(InstanceTest.INSTANCE_ID, 2)).andReturn("output");
        replay(compute);
        initializeInstance();
        Assert.assertSame("output", instance.getSerialPortOutput(2));
    }

    @Test
    public void testGetSerialPortOutput() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSerialPortOutput(InstanceTest.INSTANCE_ID)).andReturn("output");
        replay(compute);
        initializeInstance();
        Assert.assertSame("output", instance.getSerialPortOutput());
    }

    @Test
    public void testResetOperation() {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.reset(InstanceTest.INSTANCE_ID)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.reset());
    }

    @Test
    public void testResetNull() {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.reset(InstanceTest.INSTANCE_ID)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.reset());
    }

    @Test
    public void testSetDiskAutodelete() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.setDiskAutoDelete(InstanceTest.INSTANCE_ID, "dev0", true)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setDiskAutoDelete("dev0", true));
    }

    @Test
    public void testSetDiskAutodelete_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.setDiskAutoDelete(InstanceTest.INSTANCE_ID, "dev0", false)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setDiskAutoDelete("dev0", false));
    }

    @Test
    public void testSetDiskAutodeleteWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.setDiskAutoDelete(InstanceTest.INSTANCE_ID, "dev0", true, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setDiskAutoDelete("dev0", true, OperationOption.fields()));
    }

    @Test
    public void testSetMachineType() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.setMachineType(InstanceTest.INSTANCE_ID, InstanceTest.MACHINE_TYPE)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMachineType(InstanceTest.MACHINE_TYPE));
    }

    @Test
    public void testSetMachineType_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.setMachineType(InstanceTest.INSTANCE_ID, InstanceTest.MACHINE_TYPE)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setMachineType(InstanceTest.MACHINE_TYPE));
    }

    @Test
    public void testSetMachineTypeWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.setMachineType(InstanceTest.INSTANCE_ID, InstanceTest.MACHINE_TYPE, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMachineType(InstanceTest.MACHINE_TYPE, OperationOption.fields()));
    }

    @Test
    public void testSetMetadata() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Metadata metadata = Metadata.newBuilder().add("k", "v").setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMetadata(metadata));
    }

    @Test
    public void testSetMetadata_Null() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Metadata metadata = Metadata.newBuilder().add("k", "v").setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setMetadata(metadata));
    }

    @Test
    public void testSetMetadataWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Metadata metadata = Metadata.newBuilder().add("k", "v").setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMetadata(metadata, OperationOption.fields()));
    }

    @Test
    public void testSetMetadataFromMap() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Map<String, String> metadataMap = ImmutableMap.of("k", "v");
        Metadata metadata = Metadata.newBuilder().setValues(metadataMap).setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMetadata(metadataMap));
    }

    @Test
    public void testSetMetadataFromMap_Null() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Map<String, String> metadataMap = ImmutableMap.of("k", "v");
        Metadata metadata = Metadata.newBuilder().setValues(metadataMap).setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setMetadata(metadataMap));
    }

    @Test
    public void testSetMetadataFromMapWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Map<String, String> metadataMap = ImmutableMap.of("k", "v");
        Metadata metadata = Metadata.newBuilder().setValues(metadataMap).setFingerprint("fingerprint").build();
        expect(compute.setMetadata(InstanceTest.INSTANCE_ID, metadata, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setMetadata(metadataMap, OperationOption.fields()));
    }

    @Test
    public void testSetSchedulingOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        expect(compute.setSchedulingOptions(InstanceTest.INSTANCE_ID, schedulingOptions)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setSchedulingOptions(schedulingOptions));
    }

    @Test
    public void testSetSchedulingOptions_Null() throws Exception {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        expect(compute.setSchedulingOptions(InstanceTest.INSTANCE_ID, schedulingOptions)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setSchedulingOptions(schedulingOptions));
    }

    @Test
    public void testSetSchedulingOptionsWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        SchedulingOptions schedulingOptions = SchedulingOptions.standard(true, MIGRATE);
        expect(compute.setSchedulingOptions(InstanceTest.INSTANCE_ID, schedulingOptions, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setSchedulingOptions(schedulingOptions, OperationOption.fields()));
    }

    @Test
    public void testSetTags() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Tags tags = Tags.newBuilder().setValues("v1", "v2").setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setTags(tags));
    }

    @Test
    public void testSetTags_Null() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Tags tags = Tags.newBuilder().setValues("v1", "v2").setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setTags(tags));
    }

    @Test
    public void testSetTagsWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        Tags tags = Tags.newBuilder().setValues("v1", "v2").setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setTags(tags, OperationOption.fields()));
    }

    @Test
    public void testSetTagsFromList() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        List<String> tagList = ImmutableList.of("v1", "v2");
        Tags tags = Tags.newBuilder().setValues(tagList).setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setTags(tagList));
    }

    @Test
    public void testSetTagsFromList_Null() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        List<String> tagList = ImmutableList.of("v1", "v2");
        Tags tags = Tags.newBuilder().setValues(tagList).setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.setTags(tagList));
    }

    @Test
    public void testSetTagsFromListWithOptions() throws Exception {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        List<String> tagList = ImmutableList.of("v1", "v2");
        Tags tags = Tags.newBuilder().setValues(tagList).setFingerprint("fingerprint").build();
        expect(compute.setTags(InstanceTest.INSTANCE_ID, tags, OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.setTags(tagList, OperationOption.fields()));
    }

    @Test
    public void testStartOperation() {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.start(InstanceTest.INSTANCE_ID)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.start());
    }

    @Test
    public void testStartNull() {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.start(InstanceTest.INSTANCE_ID)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.start());
    }

    @Test
    public void testStopOperation() {
        initializeExpectedInstance(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(ZoneOperationId.of("project", "op")).build();
        expect(compute.stop(InstanceTest.INSTANCE_ID)).andReturn(operation);
        replay(compute);
        initializeInstance();
        Assert.assertSame(operation, instance.stop());
    }

    @Test
    public void testStopNull() {
        initializeExpectedInstance(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.stop(InstanceTest.INSTANCE_ID)).andReturn(null);
        replay(compute);
        initializeInstance();
        Assert.assertNull(instance.stop());
    }
}

