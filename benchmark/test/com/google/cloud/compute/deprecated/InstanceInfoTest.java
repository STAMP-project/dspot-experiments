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
import InstanceInfo.Status;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class InstanceInfoTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final InstanceId INSTANCE_ID = InstanceId.of("project", "zone", "instance");

    private static final Status STATUS = Status.RUNNING;

    private static final String STATUS_MESSAGE = "statusMessage";

    private static final Tags TAGS = Tags.of("tag1", "tag2");

    private static final MachineTypeId MACHINE_TYPE = MachineTypeId.of("project", "zone", "type");

    private static final Boolean CAN_IP_FORWARD = true;

    private static final NetworkInterface NETWORK_INTERFACE = NetworkInterface.of(NetworkId.of("project", "network"));

    private static final List<NetworkInterface> NETWORK_INTERFACES = ImmutableList.of(InstanceInfoTest.NETWORK_INTERFACE);

    private static final DiskId DISK_ID = DiskId.of("project", "zone", "disk");

    private static final AttachedDisk ATTACHED_DISK = AttachedDisk.of(PersistentDiskConfiguration.of(InstanceInfoTest.DISK_ID));

    private static final List<AttachedDisk> ATTACHED_DISKS = ImmutableList.of(InstanceInfoTest.ATTACHED_DISK);

    private static final Metadata METADATA = Metadata.newBuilder().add("key1", "value1").add("key2", "value2").build();

    private static final ServiceAccount SERVICE_ACCOUNT = ServiceAccount.of("email", ImmutableList.of("scope1"));

    private static final List<ServiceAccount> SERVICE_ACCOUNTS = ImmutableList.of(InstanceInfoTest.SERVICE_ACCOUNT);

    private static final SchedulingOptions SCHEDULING_OPTIONS = SchedulingOptions.preemptible();

    private static final String CPU_PLATFORM = "cpuPlatform";

    private static final InstanceInfo INSTANCE_INFO = InstanceInfo.newBuilder(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE).setGeneratedId(InstanceInfoTest.GENERATED_ID).setCreationTimestamp(InstanceInfoTest.CREATION_TIMESTAMP).setDescription(InstanceInfoTest.DESCRIPTION).setStatus(InstanceInfoTest.STATUS).setStatusMessage(InstanceInfoTest.STATUS_MESSAGE).setTags(InstanceInfoTest.TAGS).setCanIpForward(InstanceInfoTest.CAN_IP_FORWARD).setNetworkInterfaces(InstanceInfoTest.NETWORK_INTERFACES).setAttachedDisks(InstanceInfoTest.ATTACHED_DISKS).setMetadata(InstanceInfoTest.METADATA).setServiceAccounts(InstanceInfoTest.SERVICE_ACCOUNTS).setSchedulingOptions(InstanceInfoTest.SCHEDULING_OPTIONS).setCpuPlatform(InstanceInfoTest.CPU_PLATFORM).build();

    @Test
    public void testToBuilder() {
        compareInstanceInfo(InstanceInfoTest.INSTANCE_INFO, InstanceInfoTest.INSTANCE_INFO.toBuilder().build());
        InstanceInfo instance = InstanceInfoTest.INSTANCE_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", instance.getDescription());
        instance = instance.toBuilder().setDescription(InstanceInfoTest.DESCRIPTION).build();
        compareInstanceInfo(InstanceInfoTest.INSTANCE_INFO, instance);
    }

    @Test
    public void testToBuilderIncomplete() {
        InstanceInfo instanceInfo = InstanceInfo.of(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE, InstanceInfoTest.ATTACHED_DISK, InstanceInfoTest.NETWORK_INTERFACE);
        Assert.assertEquals(instanceInfo, instanceInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(InstanceInfoTest.GENERATED_ID, InstanceInfoTest.INSTANCE_INFO.getGeneratedId());
        Assert.assertEquals(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.INSTANCE_INFO.getInstanceId());
        Assert.assertEquals(InstanceInfoTest.CREATION_TIMESTAMP, InstanceInfoTest.INSTANCE_INFO.getCreationTimestamp());
        Assert.assertEquals(InstanceInfoTest.DESCRIPTION, InstanceInfoTest.INSTANCE_INFO.getDescription());
        Assert.assertEquals(InstanceInfoTest.STATUS, InstanceInfoTest.INSTANCE_INFO.getStatus());
        Assert.assertEquals(InstanceInfoTest.STATUS_MESSAGE, InstanceInfoTest.INSTANCE_INFO.getStatusMessage());
        Assert.assertEquals(InstanceInfoTest.TAGS, InstanceInfoTest.INSTANCE_INFO.getTags());
        Assert.assertEquals(InstanceInfoTest.MACHINE_TYPE, InstanceInfoTest.INSTANCE_INFO.getMachineType());
        Assert.assertEquals(InstanceInfoTest.CAN_IP_FORWARD, InstanceInfoTest.INSTANCE_INFO.canIpForward());
        Assert.assertEquals(InstanceInfoTest.NETWORK_INTERFACES, InstanceInfoTest.INSTANCE_INFO.getNetworkInterfaces());
        Assert.assertEquals(InstanceInfoTest.ATTACHED_DISKS, InstanceInfoTest.INSTANCE_INFO.getAttachedDisks());
        Assert.assertEquals(InstanceInfoTest.METADATA, InstanceInfoTest.INSTANCE_INFO.getMetadata());
        Assert.assertEquals(InstanceInfoTest.SERVICE_ACCOUNTS, InstanceInfoTest.INSTANCE_INFO.getServiceAccounts());
        Assert.assertEquals(InstanceInfoTest.SCHEDULING_OPTIONS, InstanceInfoTest.INSTANCE_INFO.getSchedulingOptions());
        Assert.assertEquals(InstanceInfoTest.CPU_PLATFORM, InstanceInfoTest.INSTANCE_INFO.getCpuPlatform());
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE).setGeneratedId(InstanceInfoTest.GENERATED_ID).setCreationTimestamp(InstanceInfoTest.CREATION_TIMESTAMP).setDescription(InstanceInfoTest.DESCRIPTION).setStatus(InstanceInfoTest.STATUS).setStatusMessage(InstanceInfoTest.STATUS_MESSAGE).setTags(InstanceInfoTest.TAGS).setCanIpForward(InstanceInfoTest.CAN_IP_FORWARD).setNetworkInterfaces(InstanceInfoTest.NETWORK_INTERFACE).setAttachedDisks(InstanceInfoTest.ATTACHED_DISK).setMetadata(InstanceInfoTest.METADATA).setServiceAccounts(InstanceInfoTest.SERVICE_ACCOUNTS).setSchedulingOptions(InstanceInfoTest.SCHEDULING_OPTIONS).setCpuPlatform(InstanceInfoTest.CPU_PLATFORM).build();
        compareInstanceInfo(InstanceInfoTest.INSTANCE_INFO, instanceInfo);
    }

    @Test
    public void testOf() {
        InstanceInfo instance = InstanceInfo.of(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE, InstanceInfoTest.ATTACHED_DISK, InstanceInfoTest.NETWORK_INTERFACE);
        Assert.assertNull(instance.getGeneratedId());
        Assert.assertEquals(InstanceInfoTest.INSTANCE_ID, instance.getInstanceId());
        Assert.assertNull(instance.getCreationTimestamp());
        Assert.assertNull(instance.getDescription());
        Assert.assertNull(instance.getStatus());
        Assert.assertNull(instance.getStatusMessage());
        Assert.assertNull(instance.getTags());
        Assert.assertEquals(InstanceInfoTest.MACHINE_TYPE, instance.getMachineType());
        Assert.assertNull(instance.canIpForward());
        Assert.assertEquals(InstanceInfoTest.NETWORK_INTERFACES, instance.getNetworkInterfaces());
        Assert.assertEquals(InstanceInfoTest.ATTACHED_DISKS, instance.getAttachedDisks());
        Assert.assertNull(instance.getMetadata());
        Assert.assertNull(instance.getServiceAccounts());
        Assert.assertNull(instance.getSchedulingOptions());
        Assert.assertNull(instance.getCpuPlatform());
    }

    @Test
    public void testToAndFromPb() {
        compareInstanceInfo(InstanceInfoTest.INSTANCE_INFO, InstanceInfo.fromPb(InstanceInfoTest.INSTANCE_INFO.toPb()));
        InstanceInfo instance = InstanceInfo.of(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE, InstanceInfoTest.ATTACHED_DISK, InstanceInfoTest.NETWORK_INTERFACE);
        compareInstanceInfo(instance, InstanceInfo.fromPb(instance.toPb()));
    }

    @Test
    public void testSetProjectId() {
        InstanceInfo instance = InstanceInfo.of(InstanceId.of("zone", "instance"), MachineTypeId.of("zone", "type"), AttachedDisk.of(PersistentDiskConfiguration.of(DiskId.of("zone", "disk"))), NetworkInterface.of(NetworkId.of("project", "network")));
        InstanceInfo instanceWithProject = InstanceInfo.of(InstanceInfoTest.INSTANCE_ID, InstanceInfoTest.MACHINE_TYPE, InstanceInfoTest.ATTACHED_DISK, InstanceInfoTest.NETWORK_INTERFACE);
        compareInstanceInfo(instanceWithProject, instance.setProjectId("project"));
    }
}

