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


import AddressInfo.InstanceUsage;
import AddressInfo.Status;
import com.google.cloud.compute.deprecated.AddressInfo.GlobalForwardingUsage;
import com.google.cloud.compute.deprecated.AddressInfo.RegionForwardingUsage;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AddressInfoTest {
    private static final String ADDRESS = "192.168.1.1";

    private static final Long CREATION_TIMESTAMP = 1452602400000L;

    private static final String DESCRIPTION = "description";

    private static final String GENERATED_ID = "42";

    private static final GlobalAddressId GLOBAL_ADDRESS_ID = GlobalAddressId.of("project", "address");

    private static final RegionAddressId REGION_ADDRESS_ID = RegionAddressId.of("project", "region", "address");

    private static final Status STATUS = Status.RESERVED;

    private static final List<GlobalForwardingRuleId> GLOBAL_FORWARDING_RULES = ImmutableList.of(GlobalForwardingRuleId.of("project", "forwardingRule1"), GlobalForwardingRuleId.of("project", "forwardingRule2"));

    private static final List<RegionForwardingRuleId> REGION_FORWARDING_RULES = ImmutableList.of(RegionForwardingRuleId.of("project", "region", "forwardingRule1"), RegionForwardingRuleId.of("project", "region", "forwardingRule2"));

    private static final InstanceUsage INSTANCE_USAGE = new AddressInfo.InstanceUsage(InstanceId.of("project", "zone", "instance1"));

    private static final GlobalForwardingUsage GLOBAL_FORWARDING_USAGE = new GlobalForwardingUsage(AddressInfoTest.GLOBAL_FORWARDING_RULES);

    private static final RegionForwardingUsage REGION_FORWARDING_USAGE = new RegionForwardingUsage(AddressInfoTest.REGION_FORWARDING_RULES);

    private static final AddressInfo INSTANCE_ADDRESS_INFO = AddressInfo.newBuilder(AddressInfoTest.REGION_ADDRESS_ID).setAddress(AddressInfoTest.ADDRESS).setCreationTimestamp(AddressInfoTest.CREATION_TIMESTAMP).setDescription(AddressInfoTest.DESCRIPTION).setGeneratedId(AddressInfoTest.GENERATED_ID).setStatus(AddressInfoTest.STATUS).setUsage(AddressInfoTest.INSTANCE_USAGE).build();

    private static final AddressInfo GLOBAL_FORWARDING_ADDRESS_INFO = AddressInfo.newBuilder(AddressInfoTest.GLOBAL_ADDRESS_ID).setAddress(AddressInfoTest.ADDRESS).setCreationTimestamp(AddressInfoTest.CREATION_TIMESTAMP).setDescription(AddressInfoTest.DESCRIPTION).setGeneratedId(AddressInfoTest.GENERATED_ID).setStatus(AddressInfoTest.STATUS).setUsage(AddressInfoTest.GLOBAL_FORWARDING_USAGE).build();

    private static final AddressInfo REGION_FORWARDING_ADDRESS_INFO = AddressInfo.newBuilder(AddressInfoTest.REGION_ADDRESS_ID).setAddress(AddressInfoTest.ADDRESS).setCreationTimestamp(AddressInfoTest.CREATION_TIMESTAMP).setDescription(AddressInfoTest.DESCRIPTION).setGeneratedId(AddressInfoTest.GENERATED_ID).setStatus(AddressInfoTest.STATUS).setUsage(AddressInfoTest.REGION_FORWARDING_USAGE).build();

    @Test
    public void testToBuilder() {
        compareAddressInfo(AddressInfoTest.INSTANCE_ADDRESS_INFO, AddressInfoTest.INSTANCE_ADDRESS_INFO.toBuilder().build());
        AddressInfo addressInfo = AddressInfoTest.INSTANCE_ADDRESS_INFO.toBuilder().setAddress("192.168.1.2").setDescription("description2").build();
        Assert.assertEquals("description2", addressInfo.getDescription());
        Assert.assertEquals("192.168.1.2", addressInfo.getAddress());
        addressInfo = addressInfo.toBuilder().setAddress("192.168.1.1").setDescription("description").build();
        compareAddressInfo(AddressInfoTest.INSTANCE_ADDRESS_INFO, addressInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        AddressInfo addressInfo = AddressInfo.newBuilder(AddressInfoTest.GLOBAL_ADDRESS_ID).build();
        Assert.assertEquals(addressInfo, addressInfo.toBuilder().build());
        addressInfo = AddressInfo.newBuilder(AddressInfoTest.REGION_ADDRESS_ID).build();
        Assert.assertEquals(addressInfo, addressInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(AddressInfoTest.ADDRESS, AddressInfoTest.INSTANCE_ADDRESS_INFO.getAddress());
        Assert.assertEquals(AddressInfoTest.CREATION_TIMESTAMP, AddressInfoTest.INSTANCE_ADDRESS_INFO.getCreationTimestamp());
        Assert.assertEquals(AddressInfoTest.DESCRIPTION, AddressInfoTest.INSTANCE_ADDRESS_INFO.getDescription());
        Assert.assertEquals(AddressInfoTest.GENERATED_ID, AddressInfoTest.INSTANCE_ADDRESS_INFO.getGeneratedId());
        Assert.assertEquals(AddressInfoTest.REGION_ADDRESS_ID, AddressInfoTest.INSTANCE_ADDRESS_INFO.getAddressId());
        Assert.assertEquals(AddressInfoTest.STATUS, AddressInfoTest.INSTANCE_ADDRESS_INFO.getStatus());
        Assert.assertEquals(AddressInfoTest.INSTANCE_USAGE, AddressInfoTest.INSTANCE_ADDRESS_INFO.getUsage());
        Assert.assertEquals(AddressInfoTest.INSTANCE_USAGE.getInstance(), AddressInfoTest.INSTANCE_ADDRESS_INFO.<AddressInfo.InstanceUsage>getUsage().getInstance());
        Assert.assertEquals(AddressInfoTest.ADDRESS, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getAddress());
        Assert.assertEquals(AddressInfoTest.CREATION_TIMESTAMP, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getCreationTimestamp());
        Assert.assertEquals(AddressInfoTest.DESCRIPTION, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getDescription());
        Assert.assertEquals(AddressInfoTest.GENERATED_ID, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getGeneratedId());
        Assert.assertEquals(AddressInfoTest.REGION_ADDRESS_ID, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getAddressId());
        Assert.assertEquals(AddressInfoTest.STATUS, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getStatus());
        Assert.assertEquals(AddressInfoTest.REGION_FORWARDING_USAGE, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.getUsage());
        Assert.assertEquals(AddressInfoTest.REGION_FORWARDING_RULES, AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.<RegionForwardingUsage>getUsage().getForwardingRules());
        Assert.assertEquals(AddressInfoTest.ADDRESS, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getAddress());
        Assert.assertEquals(AddressInfoTest.CREATION_TIMESTAMP, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getCreationTimestamp());
        Assert.assertEquals(AddressInfoTest.DESCRIPTION, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getDescription());
        Assert.assertEquals(AddressInfoTest.GENERATED_ID, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getGeneratedId());
        Assert.assertEquals(AddressInfoTest.GLOBAL_ADDRESS_ID, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getAddressId());
        Assert.assertEquals(AddressInfoTest.STATUS, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getStatus());
        Assert.assertEquals(AddressInfoTest.GLOBAL_FORWARDING_USAGE, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.getUsage());
        Assert.assertEquals(AddressInfoTest.GLOBAL_FORWARDING_RULES, AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.<GlobalForwardingUsage>getUsage().getForwardingRules());
    }

    @Test
    public void testOf() {
        AddressInfo addressInfo = AddressInfo.of("address");
        Assert.assertEquals(GlobalAddressId.of("address"), addressInfo.getAddressId());
        Assert.assertNull(addressInfo.getAddress());
        Assert.assertNull(addressInfo.getCreationTimestamp());
        Assert.assertNull(addressInfo.getDescription());
        Assert.assertNull(addressInfo.getGeneratedId());
        Assert.assertNull(addressInfo.getStatus());
        Assert.assertNull(addressInfo.getUsage());
        addressInfo = AddressInfo.of(AddressInfoTest.GLOBAL_ADDRESS_ID);
        Assert.assertEquals(AddressInfoTest.GLOBAL_ADDRESS_ID, addressInfo.getAddressId());
        Assert.assertNull(addressInfo.getAddress());
        Assert.assertNull(addressInfo.getCreationTimestamp());
        Assert.assertNull(addressInfo.getDescription());
        Assert.assertNull(addressInfo.getGeneratedId());
        Assert.assertNull(addressInfo.getStatus());
        Assert.assertNull(addressInfo.getUsage());
        addressInfo = AddressInfo.of("region", "address");
        Assert.assertEquals(RegionAddressId.of("region", "address"), addressInfo.getAddressId());
        Assert.assertNull(addressInfo.getAddress());
        Assert.assertNull(addressInfo.getCreationTimestamp());
        Assert.assertNull(addressInfo.getDescription());
        Assert.assertNull(addressInfo.getGeneratedId());
        Assert.assertNull(addressInfo.getStatus());
        Assert.assertNull(addressInfo.getUsage());
        addressInfo = AddressInfo.of(RegionId.of("region"), "address");
        Assert.assertEquals(RegionAddressId.of("region", "address"), addressInfo.getAddressId());
        Assert.assertNull(addressInfo.getAddress());
        Assert.assertNull(addressInfo.getCreationTimestamp());
        Assert.assertNull(addressInfo.getDescription());
        Assert.assertNull(addressInfo.getGeneratedId());
        Assert.assertNull(addressInfo.getStatus());
        Assert.assertNull(addressInfo.getUsage());
    }

    @Test
    public void testToPbAndFromPb() {
        compareAddressInfo(AddressInfoTest.INSTANCE_ADDRESS_INFO, AddressInfo.fromPb(AddressInfoTest.INSTANCE_ADDRESS_INFO.toPb()));
        compareAddressInfo(AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO, AddressInfo.fromPb(AddressInfoTest.REGION_FORWARDING_ADDRESS_INFO.toPb()));
        compareAddressInfo(AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO, AddressInfo.fromPb(AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.toPb()));
        AddressInfo addressInfo = AddressInfo.newBuilder(AddressInfoTest.GLOBAL_ADDRESS_ID).build();
        compareAddressInfo(addressInfo, AddressInfo.fromPb(addressInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        AddressInfo addressInfo = AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO.toBuilder().setAddressId(GlobalAddressId.of(AddressInfoTest.GLOBAL_ADDRESS_ID.getAddress())).build();
        compareAddressInfo(AddressInfoTest.GLOBAL_FORWARDING_ADDRESS_INFO, addressInfo.setProjectId("project"));
    }
}

