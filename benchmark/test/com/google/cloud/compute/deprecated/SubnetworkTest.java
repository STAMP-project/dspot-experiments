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


import Compute.SubnetworkOption;
import org.junit.Assert;
import org.junit.Test;


public class SubnetworkTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final SubnetworkId SUBNETWORK_ID = SubnetworkId.of("project", "region", "network");

    private static final String GATEWAY_ADDRESS = "192.168.1.1";

    private static final NetworkId NETWORK_ID = NetworkId.of("project", "network");

    private static final String IP_RANGE = "192.168.0.0/16";

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Subnetwork subnetwork;

    private Subnetwork expectedSubnetwork;

    @Test
    public void testToBuilder() {
        initializeExpectedSubnetwork(8);
        compareSubnetwork(expectedSubnetwork, expectedSubnetwork.toBuilder().build());
        Subnetwork newSubnetwork = build();
        Assert.assertEquals("newDescription", newSubnetwork.getDescription());
        newSubnetwork = newSubnetwork.toBuilder().setDescription("description").build();
        compareSubnetwork(expectedSubnetwork, newSubnetwork);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedSubnetwork(5);
        SubnetworkInfo subnetworkInfo = SubnetworkInfo.of(SubnetworkTest.SUBNETWORK_ID, SubnetworkTest.NETWORK_ID, SubnetworkTest.IP_RANGE);
        Subnetwork subnetwork = new Subnetwork(serviceMockReturnsOptions, new SubnetworkInfo.BuilderImpl(subnetworkInfo));
        compareSubnetwork(subnetwork, subnetwork.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedSubnetwork(1);
        Assert.assertEquals(SubnetworkTest.GENERATED_ID, expectedSubnetwork.getGeneratedId());
        Assert.assertEquals(SubnetworkTest.SUBNETWORK_ID, expectedSubnetwork.getSubnetworkId());
        Assert.assertEquals(SubnetworkTest.CREATION_TIMESTAMP, expectedSubnetwork.getCreationTimestamp());
        Assert.assertEquals(SubnetworkTest.DESCRIPTION, expectedSubnetwork.getDescription());
        Assert.assertEquals(SubnetworkTest.GATEWAY_ADDRESS, expectedSubnetwork.getGatewayAddress());
        Assert.assertEquals(SubnetworkTest.NETWORK_ID, expectedSubnetwork.getNetwork());
        Assert.assertEquals(SubnetworkTest.IP_RANGE, expectedSubnetwork.getIpRange());
        Assert.assertSame(serviceMockReturnsOptions, expectedSubnetwork.getCompute());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedSubnetwork(8);
        compareSubnetwork(expectedSubnetwork, Subnetwork.fromPb(serviceMockReturnsOptions, expectedSubnetwork.toPb()));
        Subnetwork subnetwork = build();
        compareSubnetwork(subnetwork, Subnetwork.fromPb(serviceMockReturnsOptions, subnetwork.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedSubnetwork(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        expect(compute.deleteSubnetwork(SubnetworkTest.SUBNETWORK_ID)).andReturn(operation);
        replay(compute);
        initializeSubnetwork();
        Assert.assertSame(operation, subnetwork.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedSubnetwork(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteSubnetwork(SubnetworkTest.SUBNETWORK_ID)).andReturn(null);
        replay(compute);
        initializeSubnetwork();
        Assert.assertNull(subnetwork.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedSubnetwork(1);
        Compute[] expectedOptions = new SubnetworkOption[]{ SubnetworkOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSubnetwork(SubnetworkTest.SUBNETWORK_ID, expectedOptions)).andReturn(expectedSubnetwork);
        replay(compute);
        initializeSubnetwork();
        Assert.assertTrue(subnetwork.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedSubnetwork(1);
        Compute[] expectedOptions = new SubnetworkOption[]{ SubnetworkOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSubnetwork(SubnetworkTest.SUBNETWORK_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeSubnetwork();
        Assert.assertFalse(subnetwork.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedSubnetwork(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSubnetwork(SubnetworkTest.SUBNETWORK_ID)).andReturn(expectedSubnetwork);
        replay(compute);
        initializeSubnetwork();
        Subnetwork updatedSubnetwork = subnetwork.reload();
        compareSubnetwork(expectedSubnetwork, updatedSubnetwork);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedSubnetwork(1);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSubnetwork(SubnetworkTest.SUBNETWORK_ID)).andReturn(null);
        replay(compute);
        initializeSubnetwork();
        Assert.assertNull(subnetwork.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedSubnetwork(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getSubnetwork(SubnetworkTest.SUBNETWORK_ID, SubnetworkOption.fields())).andReturn(expectedSubnetwork);
        replay(compute);
        initializeSubnetwork();
        Subnetwork updatedSubnetwork = subnetwork.reload(SubnetworkOption.fields());
        compareSubnetwork(expectedSubnetwork, updatedSubnetwork);
        verify(compute);
    }
}

