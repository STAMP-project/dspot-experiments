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


import Type.STANDARD;
import org.junit.Assert;
import org.junit.Test;


public class StandardNetworkConfigurationTest {
    private static final String IP_RANGE = "192.168.0.0/16";

    private static final String GATEWAY_ADDRESS = "192.168.1.1";

    private static final StandardNetworkConfiguration NETWORK_CONFIGURATION = new StandardNetworkConfiguration(StandardNetworkConfigurationTest.IP_RANGE, StandardNetworkConfigurationTest.GATEWAY_ADDRESS);

    @Test
    public void testConstructor() {
        Assert.assertEquals(STANDARD, StandardNetworkConfigurationTest.NETWORK_CONFIGURATION.getType());
        Assert.assertEquals(StandardNetworkConfigurationTest.IP_RANGE, StandardNetworkConfigurationTest.NETWORK_CONFIGURATION.getIpRange());
        Assert.assertEquals(StandardNetworkConfigurationTest.GATEWAY_ADDRESS, StandardNetworkConfigurationTest.NETWORK_CONFIGURATION.getGatewayAddress());
        StandardNetworkConfiguration networkConfiguration = new StandardNetworkConfiguration(StandardNetworkConfigurationTest.IP_RANGE, null);
        Assert.assertEquals(STANDARD, networkConfiguration.getType());
        Assert.assertEquals(StandardNetworkConfigurationTest.IP_RANGE, networkConfiguration.getIpRange());
        Assert.assertNull(networkConfiguration.getGatewayAddress());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((NetworkConfiguration.fromPb(StandardNetworkConfigurationTest.NETWORK_CONFIGURATION.toPb())) instanceof StandardNetworkConfiguration));
        compareNetworkConfiguration(StandardNetworkConfigurationTest.NETWORK_CONFIGURATION, NetworkConfiguration.<StandardNetworkConfiguration>fromPb(StandardNetworkConfigurationTest.NETWORK_CONFIGURATION.toPb()));
        StandardNetworkConfiguration networkConfiguration = new StandardNetworkConfiguration(StandardNetworkConfigurationTest.IP_RANGE, null);
        Assert.assertTrue(((NetworkConfiguration.fromPb(networkConfiguration.toPb())) instanceof StandardNetworkConfiguration));
        compareNetworkConfiguration(networkConfiguration, NetworkConfiguration.<StandardNetworkConfiguration>fromPb(networkConfiguration.toPb()));
    }

    @Test
    public void testOf() {
        StandardNetworkConfiguration configuration = StandardNetworkConfiguration.of(StandardNetworkConfigurationTest.IP_RANGE);
        Assert.assertEquals(STANDARD, configuration.getType());
        Assert.assertEquals(StandardNetworkConfigurationTest.IP_RANGE, configuration.getIpRange());
        Assert.assertNull(configuration.getGatewayAddress());
    }
}

