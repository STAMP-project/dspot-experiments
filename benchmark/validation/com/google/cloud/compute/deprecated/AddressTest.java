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


import AddressInfo.GlobalForwardingUsage;
import AddressInfo.InstanceUsage;
import AddressInfo.RegionForwardingUsage;
import AddressInfo.Status;
import Compute.AddressOption;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AddressTest {
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

    private static final GlobalForwardingUsage GLOBAL_FORWARDING_USAGE = new AddressInfo.GlobalForwardingUsage(AddressTest.GLOBAL_FORWARDING_RULES);

    private static final RegionForwardingUsage REGION_FORWARDING_USAGE = new AddressInfo.RegionForwardingUsage(AddressTest.REGION_FORWARDING_RULES);

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Address globalForwardingAddress;

    private Address instanceAddress;

    private Address regionForwardingAddress;

    private Address address;

    @Test
    public void testBuilder() {
        initializeExpectedAddress(6);
        Assert.assertEquals(AddressTest.ADDRESS, instanceAddress.getAddress());
        Assert.assertEquals(AddressTest.CREATION_TIMESTAMP, instanceAddress.getCreationTimestamp());
        Assert.assertEquals(AddressTest.DESCRIPTION, instanceAddress.getDescription());
        Assert.assertEquals(AddressTest.GENERATED_ID, instanceAddress.getGeneratedId());
        Assert.assertEquals(AddressTest.REGION_ADDRESS_ID, instanceAddress.getAddressId());
        Assert.assertEquals(AddressTest.STATUS, instanceAddress.getStatus());
        Assert.assertEquals(AddressTest.INSTANCE_USAGE, instanceAddress.getUsage());
        Assert.assertSame(serviceMockReturnsOptions, instanceAddress.getCompute());
        Assert.assertEquals(AddressTest.ADDRESS, regionForwardingAddress.getAddress());
        Assert.assertEquals(AddressTest.CREATION_TIMESTAMP, regionForwardingAddress.getCreationTimestamp());
        Assert.assertEquals(AddressTest.DESCRIPTION, regionForwardingAddress.getDescription());
        Assert.assertEquals(AddressTest.GENERATED_ID, regionForwardingAddress.getGeneratedId());
        Assert.assertEquals(AddressTest.REGION_ADDRESS_ID, regionForwardingAddress.getAddressId());
        Assert.assertEquals(AddressTest.STATUS, regionForwardingAddress.getStatus());
        Assert.assertEquals(AddressTest.REGION_FORWARDING_USAGE, regionForwardingAddress.getUsage());
        Assert.assertSame(serviceMockReturnsOptions, regionForwardingAddress.getCompute());
        Assert.assertEquals(AddressTest.ADDRESS, globalForwardingAddress.getAddress());
        Assert.assertEquals(AddressTest.CREATION_TIMESTAMP, globalForwardingAddress.getCreationTimestamp());
        Assert.assertEquals(AddressTest.DESCRIPTION, globalForwardingAddress.getDescription());
        Assert.assertEquals(AddressTest.GENERATED_ID, globalForwardingAddress.getGeneratedId());
        Assert.assertEquals(AddressTest.GLOBAL_ADDRESS_ID, globalForwardingAddress.getAddressId());
        Assert.assertEquals(AddressTest.STATUS, globalForwardingAddress.getStatus());
        Assert.assertEquals(AddressTest.GLOBAL_FORWARDING_USAGE, globalForwardingAddress.getUsage());
        Assert.assertSame(serviceMockReturnsOptions, globalForwardingAddress.getCompute());
        Address address = build();
        Assert.assertEquals(AddressTest.GLOBAL_ADDRESS_ID, address.getAddressId());
        Assert.assertSame(serviceMockReturnsOptions, address.getCompute());
        Assert.assertNull(address.getAddress());
        Assert.assertNull(address.getCreationTimestamp());
        Assert.assertNull(address.getDescription());
        Assert.assertNull(address.getGeneratedId());
        Assert.assertNull(address.getStatus());
        Assert.assertNull(address.getUsage());
        address = new Address.Builder(serviceMockReturnsOptions, AddressTest.REGION_ADDRESS_ID).build();
        Assert.assertEquals(AddressTest.REGION_ADDRESS_ID, address.getAddressId());
        Assert.assertSame(serviceMockReturnsOptions, address.getCompute());
        Assert.assertNull(address.getAddress());
        Assert.assertNull(address.getCreationTimestamp());
        Assert.assertNull(address.getDescription());
        Assert.assertNull(address.getGeneratedId());
        Assert.assertNull(address.getStatus());
        Assert.assertNull(address.getUsage());
        address = new Address.Builder(serviceMockReturnsOptions, AddressTest.REGION_ADDRESS_ID).setAddressId(AddressTest.GLOBAL_ADDRESS_ID).build();
        Assert.assertEquals(AddressTest.GLOBAL_ADDRESS_ID, address.getAddressId());
        Assert.assertSame(serviceMockReturnsOptions, address.getCompute());
        Assert.assertNull(address.getAddress());
        Assert.assertNull(address.getCreationTimestamp());
        Assert.assertNull(address.getDescription());
        Assert.assertNull(address.getGeneratedId());
        Assert.assertNull(address.getStatus());
        Assert.assertNull(address.getUsage());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedAddress(16);
        compareAddress(instanceAddress, instanceAddress.toBuilder().build());
        compareAddress(globalForwardingAddress, globalForwardingAddress.toBuilder().build());
        compareAddress(regionForwardingAddress, regionForwardingAddress.toBuilder().build());
        Address newAddress = build();
        Assert.assertEquals("newDescription", newAddress.getDescription());
        newAddress = newAddress.toBuilder().setDescription("description").build();
        compareAddress(instanceAddress, newAddress);
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedAddress(20);
        compareAddress(globalForwardingAddress, Address.fromPb(serviceMockReturnsOptions, globalForwardingAddress.toPb()));
        compareAddress(regionForwardingAddress, Address.fromPb(serviceMockReturnsOptions, regionForwardingAddress.toPb()));
        compareAddress(instanceAddress, Address.fromPb(serviceMockReturnsOptions, instanceAddress.toPb()));
        Address address = build();
        compareAddress(address, Address.fromPb(serviceMockReturnsOptions, address.toPb()));
        address = new Address.Builder(serviceMockReturnsOptions, AddressTest.REGION_ADDRESS_ID).build();
        compareAddress(address, Address.fromPb(serviceMockReturnsOptions, address.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedAddress(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        expect(compute.deleteAddress(AddressTest.REGION_ADDRESS_ID)).andReturn(operation);
        replay(compute);
        initializeAddress();
        Assert.assertSame(operation, address.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedAddress(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteAddress(AddressTest.REGION_ADDRESS_ID)).andReturn(null);
        replay(compute);
        initializeAddress();
        Assert.assertNull(address.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedAddress(3);
        Compute[] expectedOptions = new AddressOption[]{ AddressOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getAddress(AddressTest.REGION_ADDRESS_ID, expectedOptions)).andReturn(regionForwardingAddress);
        replay(compute);
        initializeAddress();
        Assert.assertTrue(address.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedAddress(3);
        Compute[] expectedOptions = new AddressOption[]{ AddressOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getAddress(AddressTest.REGION_ADDRESS_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeAddress();
        Assert.assertFalse(address.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedAddress(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getAddress(AddressTest.REGION_ADDRESS_ID)).andReturn(regionForwardingAddress);
        replay(compute);
        initializeAddress();
        Address updatedAddress = address.reload();
        compareAddress(regionForwardingAddress, updatedAddress);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedAddress(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getAddress(AddressTest.REGION_ADDRESS_ID)).andReturn(null);
        replay(compute);
        initializeAddress();
        Assert.assertNull(address.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedAddress(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getAddress(AddressTest.REGION_ADDRESS_ID, AddressOption.fields())).andReturn(regionForwardingAddress);
        replay(compute);
        initializeAddress();
        Address updatedAddress = address.reload(AddressOption.fields());
        compareAddress(regionForwardingAddress, updatedAddress);
        verify(compute);
    }
}

