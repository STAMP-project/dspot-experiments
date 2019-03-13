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


import AccessConfig.Type.ONE_TO_ONE_NAT;
import com.google.cloud.compute.deprecated.NetworkInterface.AccessConfig;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NetworkInterfaceTest {
    private static final String NAME = "networkInterface";

    private static final NetworkId NETWORK = NetworkId.of("project", "network");

    private static final String NETWORK_IP = "192.168.1.1";

    private static final SubnetworkId SUBNETWORK = SubnetworkId.of("project", "region", "subnetwork");

    private static final AccessConfig ACCESS_CONFIG = AccessConfig.newBuilder().setName("accessConfig").setNatIp("192.168.1.1").setType(ONE_TO_ONE_NAT).build();

    private static final List<AccessConfig> ACCESS_CONFIGURATIONS = ImmutableList.of(NetworkInterfaceTest.ACCESS_CONFIG);

    private static final NetworkInterface NETWORK_INTERFACE = NetworkInterface.newBuilder(NetworkInterfaceTest.NETWORK).setName(NetworkInterfaceTest.NAME).setNetworkIp(NetworkInterfaceTest.NETWORK_IP).setSubnetwork(NetworkInterfaceTest.SUBNETWORK).setAccessConfigurations(NetworkInterfaceTest.ACCESS_CONFIGURATIONS).build();

    @Test
    public void testAccessConfigToBuilder() {
        AccessConfig accessConfig = NetworkInterfaceTest.ACCESS_CONFIG.toBuilder().setName("newName").build();
        Assert.assertEquals("newName", accessConfig.getName());
        compareAccessConfig(NetworkInterfaceTest.ACCESS_CONFIG, accessConfig.toBuilder().setName("accessConfig").build());
    }

    @Test
    public void testAccessConfigToBuilderIncomplete() {
        AccessConfig accessConfig = AccessConfig.of();
        compareAccessConfig(accessConfig, accessConfig.toBuilder().build());
    }

    @Test
    public void testToBuilder() {
        compareNetworkInterface(NetworkInterfaceTest.NETWORK_INTERFACE, NetworkInterfaceTest.NETWORK_INTERFACE.toBuilder().build());
        NetworkInterface networkInterface = NetworkInterfaceTest.NETWORK_INTERFACE.toBuilder().setName("newInterface").build();
        Assert.assertEquals("newInterface", networkInterface.getName());
        networkInterface = networkInterface.toBuilder().setName(NetworkInterfaceTest.NAME).build();
        compareNetworkInterface(NetworkInterfaceTest.NETWORK_INTERFACE, networkInterface);
    }

    @Test
    public void testToBuilderIncomplete() {
        NetworkInterface networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK);
        Assert.assertEquals(networkInterface, networkInterface.toBuilder().build());
        networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK.getNetwork());
        Assert.assertEquals(networkInterface, networkInterface.toBuilder().build());
    }

    @Test
    public void testAccessConfigBuilder() {
        Assert.assertEquals("accessConfig", NetworkInterfaceTest.ACCESS_CONFIG.getName());
        Assert.assertEquals("192.168.1.1", NetworkInterfaceTest.ACCESS_CONFIG.getNatIp());
        Assert.assertEquals(ONE_TO_ONE_NAT, NetworkInterfaceTest.ACCESS_CONFIG.getType());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(NetworkInterfaceTest.NAME, NetworkInterfaceTest.NETWORK_INTERFACE.getName());
        Assert.assertEquals(NetworkInterfaceTest.NETWORK, NetworkInterfaceTest.NETWORK_INTERFACE.getNetwork());
        Assert.assertEquals(NetworkInterfaceTest.NETWORK_IP, NetworkInterfaceTest.NETWORK_INTERFACE.getNetworkIp());
        Assert.assertEquals(NetworkInterfaceTest.SUBNETWORK, NetworkInterfaceTest.NETWORK_INTERFACE.getSubnetwork());
        Assert.assertEquals(NetworkInterfaceTest.ACCESS_CONFIGURATIONS, NetworkInterfaceTest.NETWORK_INTERFACE.getAccessConfigurations());
        NetworkInterface networkInterface = NetworkInterface.newBuilder("network").setName(NetworkInterfaceTest.NAME).setNetworkIp(NetworkInterfaceTest.NETWORK_IP).setSubnetwork(NetworkInterfaceTest.SUBNETWORK).setAccessConfigurations(NetworkInterfaceTest.ACCESS_CONFIG).build();
        Assert.assertEquals(NetworkInterfaceTest.NAME, networkInterface.getName());
        Assert.assertEquals(NetworkId.of("network"), networkInterface.getNetwork());
        Assert.assertEquals(NetworkInterfaceTest.NETWORK_IP, networkInterface.getNetworkIp());
        Assert.assertEquals(NetworkInterfaceTest.SUBNETWORK, networkInterface.getSubnetwork());
        Assert.assertEquals(NetworkInterfaceTest.ACCESS_CONFIGURATIONS, networkInterface.getAccessConfigurations());
    }

    @Test
    public void testAccessConfigOf() {
        AccessConfig accessConfig = AccessConfig.of("192.168.1.1");
        Assert.assertNull(accessConfig.getName());
        Assert.assertEquals("192.168.1.1", accessConfig.getNatIp());
        Assert.assertNull(accessConfig.getType());
        accessConfig = AccessConfig.of();
        Assert.assertNull(accessConfig.getName());
        Assert.assertNull(accessConfig.getNatIp());
        Assert.assertNull(accessConfig.getType());
    }

    @Test
    public void testOf() {
        NetworkInterface networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK);
        Assert.assertNull(networkInterface.getName());
        Assert.assertEquals(NetworkInterfaceTest.NETWORK, networkInterface.getNetwork());
        Assert.assertNull(networkInterface.getNetworkIp());
        Assert.assertNull(networkInterface.getSubnetwork());
        networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK.getNetwork());
        Assert.assertNull(networkInterface.getName());
        Assert.assertNull(networkInterface.getNetwork().getProject());
        Assert.assertEquals(NetworkInterfaceTest.NETWORK.getNetwork(), networkInterface.getNetwork().getNetwork());
        Assert.assertNull(networkInterface.getNetworkIp());
        Assert.assertNull(networkInterface.getSubnetwork());
    }

    @Test
    public void testAccessConfigToAndFromPb() {
        AccessConfig accessConfig = AccessConfig.fromPb(NetworkInterfaceTest.ACCESS_CONFIG.toPb());
        compareAccessConfig(NetworkInterfaceTest.ACCESS_CONFIG, accessConfig);
        accessConfig = AccessConfig.of();
        compareAccessConfig(accessConfig, AccessConfig.fromPb(accessConfig.toPb()));
    }

    @Test
    public void testToAndFromPb() {
        NetworkInterface networkInterface = NetworkInterface.fromPb(NetworkInterfaceTest.NETWORK_INTERFACE.toPb());
        compareNetworkInterface(NetworkInterfaceTest.NETWORK_INTERFACE, networkInterface);
        networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK);
        compareNetworkInterface(networkInterface, NetworkInterface.fromPb(networkInterface.toPb()));
    }

    @Test
    public void testSetProjectId() {
        NetworkInterface networkInterface = NetworkInterface.of(NetworkInterfaceTest.NETWORK);
        compareNetworkInterface(networkInterface, NetworkInterface.of(NetworkId.of("network")).setProjectId("project"));
        networkInterface = NetworkInterfaceTest.NETWORK_INTERFACE.toBuilder().setNetwork(NetworkId.of("network")).setSubnetwork(SubnetworkId.of("region", "subnetwork")).build();
        compareNetworkInterface(NetworkInterfaceTest.NETWORK_INTERFACE, networkInterface.setProjectId("project"));
    }
}

