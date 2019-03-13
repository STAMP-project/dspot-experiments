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


import Compute.NetworkOption;
import Compute.OperationOption;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NetworkTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final SubnetworkId SUBNETWORK1 = SubnetworkId.of("project", "region1", "network1");

    private static final SubnetworkId SUBNETWORK2 = SubnetworkId.of("project", "region2", "network2");

    private static final List<SubnetworkId> SUBNETWORKS = ImmutableList.of(NetworkTest.SUBNETWORK1, NetworkTest.SUBNETWORK2);

    private static final String GATEWAY_ADDRESS = "192.168.1.1";

    private static final NetworkId NETWORK_ID = NetworkId.of("project", "network");

    private static final String IP_RANGE = "192.168.0.0/16";

    private static final Boolean AUTO_CREATE_SUBNETWORKS = true;

    private static final StandardNetworkConfiguration NETWORK_CONFIGURATION = new StandardNetworkConfiguration(NetworkTest.IP_RANGE, NetworkTest.GATEWAY_ADDRESS);

    private static final SubnetNetworkConfiguration SUBNET_NETWORK_CONFIGURATION = new SubnetNetworkConfiguration(NetworkTest.AUTO_CREATE_SUBNETWORKS, NetworkTest.SUBNETWORKS);

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Network network;

    private Network standardNetwork;

    private Network subnetNetwork;

    @Test
    public void testToBuilder() {
        initializeExpectedNetwork(9);
        compareNetwork(standardNetwork, standardNetwork.toBuilder().build());
        Network newNetwork = build();
        Assert.assertEquals("newDescription", newNetwork.getDescription());
        newNetwork = newNetwork.toBuilder().setDescription("description").build();
        compareNetwork(standardNetwork, newNetwork);
    }

    @Test
    public void testToBuilderIncomplete() {
        initializeExpectedNetwork(6);
        NetworkInfo networkInfo = NetworkInfo.of(NetworkTest.NETWORK_ID, NetworkTest.NETWORK_CONFIGURATION);
        Network network = new Network(serviceMockReturnsOptions, new NetworkInfo.BuilderImpl(networkInfo));
        compareNetwork(network, network.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        initializeExpectedNetwork(2);
        Assert.assertEquals(NetworkTest.GENERATED_ID, standardNetwork.getGeneratedId());
        Assert.assertEquals(NetworkTest.NETWORK_ID, standardNetwork.getNetworkId());
        Assert.assertEquals(NetworkTest.CREATION_TIMESTAMP, standardNetwork.getCreationTimestamp());
        Assert.assertEquals(NetworkTest.DESCRIPTION, standardNetwork.getDescription());
        Assert.assertEquals(NetworkTest.NETWORK_CONFIGURATION, standardNetwork.getConfiguration());
        Assert.assertSame(serviceMockReturnsOptions, standardNetwork.getCompute());
        Assert.assertEquals(NetworkTest.GENERATED_ID, subnetNetwork.getGeneratedId());
        Assert.assertEquals(NetworkTest.NETWORK_ID, subnetNetwork.getNetworkId());
        Assert.assertEquals(NetworkTest.CREATION_TIMESTAMP, subnetNetwork.getCreationTimestamp());
        Assert.assertEquals(NetworkTest.DESCRIPTION, subnetNetwork.getDescription());
        Assert.assertEquals(NetworkTest.SUBNET_NETWORK_CONFIGURATION, subnetNetwork.getConfiguration());
        Assert.assertSame(serviceMockReturnsOptions, subnetNetwork.getCompute());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedNetwork(12);
        compareNetwork(standardNetwork, Network.fromPb(serviceMockReturnsOptions, standardNetwork.toPb()));
        compareNetwork(subnetNetwork, Network.fromPb(serviceMockReturnsOptions, subnetNetwork.toPb()));
        Network network = build();
        compareNetwork(network, Network.fromPb(serviceMockReturnsOptions, network.toPb()));
    }

    @Test
    public void testDeleteOperation() {
        initializeExpectedNetwork(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(GlobalOperationId.of("project", "op")).build();
        expect(compute.deleteNetwork(NetworkTest.NETWORK_ID.getNetwork())).andReturn(operation);
        replay(compute);
        initializeNetwork();
        Assert.assertSame(operation, network.delete());
    }

    @Test
    public void testDeleteNull() {
        initializeExpectedNetwork(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteNetwork(NetworkTest.NETWORK_ID.getNetwork())).andReturn(null);
        replay(compute);
        initializeNetwork();
        Assert.assertNull(network.delete());
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedNetwork(2);
        Compute[] expectedOptions = new NetworkOption[]{ NetworkOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getNetwork(NetworkTest.NETWORK_ID.getNetwork(), expectedOptions)).andReturn(standardNetwork);
        replay(compute);
        initializeNetwork();
        Assert.assertTrue(network.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedNetwork(2);
        Compute[] expectedOptions = new NetworkOption[]{ NetworkOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getNetwork(NetworkTest.NETWORK_ID.getNetwork(), expectedOptions)).andReturn(null);
        replay(compute);
        initializeNetwork();
        Assert.assertFalse(network.exists());
        verify(compute);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedNetwork(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getNetwork(NetworkTest.NETWORK_ID.getNetwork())).andReturn(standardNetwork);
        replay(compute);
        initializeNetwork();
        Network updatedNetwork = network.reload();
        compareNetwork(standardNetwork, updatedNetwork);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedNetwork(2);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getNetwork(NetworkTest.NETWORK_ID.getNetwork())).andReturn(null);
        replay(compute);
        initializeNetwork();
        Assert.assertNull(network.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedNetwork(4);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getNetwork(NetworkTest.NETWORK_ID.getNetwork(), NetworkOption.fields())).andReturn(standardNetwork);
        replay(compute);
        initializeNetwork();
        Network updatedNetwork = network.reload(NetworkOption.fields());
        compareNetwork(standardNetwork, updatedNetwork);
        verify(compute);
    }

    @Test
    public void testCreateSubnetwork() throws Exception {
        initializeExpectedNetwork(3);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(RegionOperationId.of(NetworkTest.SUBNETWORK1.getRegionId(), "op")).build();
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.create(SubnetworkInfo.of(NetworkTest.SUBNETWORK1, NetworkTest.NETWORK_ID, NetworkTest.IP_RANGE))).andReturn(operation);
        replay(compute);
        initializeNetwork();
        Assert.assertSame(operation, network.createSubnetwork(NetworkTest.SUBNETWORK1, NetworkTest.IP_RANGE));
        verify(compute);
    }

    @Test
    public void testCreateSubnetworkWithOptions() throws Exception {
        initializeExpectedNetwork(3);
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(RegionOperationId.of(NetworkTest.SUBNETWORK1.getRegionId(), "op")).build();
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.create(SubnetworkInfo.of(NetworkTest.SUBNETWORK1, NetworkTest.NETWORK_ID, NetworkTest.IP_RANGE), OperationOption.fields())).andReturn(operation);
        replay(compute);
        initializeNetwork();
        Assert.assertSame(operation, network.createSubnetwork(NetworkTest.SUBNETWORK1, NetworkTest.IP_RANGE, OperationOption.fields()));
        verify(compute);
    }
}

