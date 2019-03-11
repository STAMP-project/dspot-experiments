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


import NetworkConfiguration.Type.SUBNET;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SubnetNetworkConfigurationTest {
    private static final Boolean AUTO_CREATE_SUBNETWORKS = true;

    private static final List<SubnetworkId> SUBNETWORKS = ImmutableList.of(SubnetworkId.of("project", "region", "subnetwork1"), SubnetworkId.of("project", "region", "subnetwork2"));

    private static final SubnetNetworkConfiguration NETWORK_CONFIGURATION = new SubnetNetworkConfiguration(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, SubnetNetworkConfigurationTest.SUBNETWORKS);

    @Test
    public void testConstructor() {
        Assert.assertEquals(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.autoCreateSubnetworks());
        Assert.assertEquals(SUBNET, SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.getType());
        Assert.assertEquals(SubnetNetworkConfigurationTest.SUBNETWORKS, SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.getSubnetworks());
        Assert.assertEquals(SUBNET, SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.getType());
        SubnetNetworkConfiguration networkConfiguration = new SubnetNetworkConfiguration(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, null);
        Assert.assertEquals(SUBNET, networkConfiguration.getType());
        Assert.assertEquals(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, networkConfiguration.autoCreateSubnetworks());
        Assert.assertNull(networkConfiguration.getSubnetworks());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((NetworkConfiguration.fromPb(SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.toPb())) instanceof SubnetNetworkConfiguration));
        compareNetworkConfiguration(SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION, NetworkConfiguration.<SubnetNetworkConfiguration>fromPb(SubnetNetworkConfigurationTest.NETWORK_CONFIGURATION.toPb()));
        SubnetNetworkConfiguration networkConfiguration = new SubnetNetworkConfiguration(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, null);
        Assert.assertTrue(((NetworkConfiguration.fromPb(networkConfiguration.toPb())) instanceof SubnetNetworkConfiguration));
        compareNetworkConfiguration(networkConfiguration, NetworkConfiguration.<SubnetNetworkConfiguration>fromPb(networkConfiguration.toPb()));
    }

    @Test
    public void testOf() {
        SubnetNetworkConfiguration configuration = SubnetNetworkConfiguration.of(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS);
        Assert.assertEquals(SubnetNetworkConfigurationTest.AUTO_CREATE_SUBNETWORKS, configuration.autoCreateSubnetworks());
        Assert.assertNull(configuration.getSubnetworks());
        Assert.assertEquals(SUBNET, configuration.getType());
    }
}

