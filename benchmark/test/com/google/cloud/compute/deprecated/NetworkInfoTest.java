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


import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NetworkInfoTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final SubnetworkId SUBNETWORK1 = SubnetworkId.of("project", "region1", "network1");

    private static final SubnetworkId SUBNETWORK2 = SubnetworkId.of("project", "region2", "network2");

    private static final List<SubnetworkId> SUBNETWORKS = ImmutableList.of(NetworkInfoTest.SUBNETWORK1, NetworkInfoTest.SUBNETWORK2);

    private static final String GATEWAY_ADDRESS = "192.168.1.1";

    private static final NetworkId NETWORK_ID = NetworkId.of("project", "network");

    private static final String IP_RANGE = "192.168.0.0/16";

    private static final Boolean AUTO_CREATE_SUBNETWORKS = true;

    private static final StandardNetworkConfiguration NETWORK_CONFIGURATION = new StandardNetworkConfiguration(NetworkInfoTest.IP_RANGE, NetworkInfoTest.GATEWAY_ADDRESS);

    private static final SubnetNetworkConfiguration SUBNET_NETWORK_CONFIGURATION = new SubnetNetworkConfiguration(NetworkInfoTest.AUTO_CREATE_SUBNETWORKS, NetworkInfoTest.SUBNETWORKS);

    private static final NetworkInfo NETWORK_INFO = NetworkInfo.newBuilder(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_CONFIGURATION).setGeneratedId(NetworkInfoTest.GENERATED_ID).setCreationTimestamp(NetworkInfoTest.CREATION_TIMESTAMP).setDescription(NetworkInfoTest.DESCRIPTION).build();

    private static final NetworkInfo SUBNET_NETWORK_INFO = NetworkInfo.newBuilder(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.SUBNET_NETWORK_CONFIGURATION).setGeneratedId(NetworkInfoTest.GENERATED_ID).setCreationTimestamp(NetworkInfoTest.CREATION_TIMESTAMP).setDescription(NetworkInfoTest.DESCRIPTION).build();

    @Test
    public void testToBuilder() {
        compareNetworkInfo(NetworkInfoTest.NETWORK_INFO, NetworkInfoTest.NETWORK_INFO.toBuilder().build());
        NetworkInfo networkInfo = NetworkInfoTest.NETWORK_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", networkInfo.getDescription());
        networkInfo = networkInfo.toBuilder().setDescription("description").build();
        compareNetworkInfo(NetworkInfoTest.NETWORK_INFO, networkInfo);
        compareNetworkInfo(NetworkInfoTest.SUBNET_NETWORK_INFO, NetworkInfoTest.SUBNET_NETWORK_INFO.toBuilder().build());
        networkInfo = NetworkInfoTest.SUBNET_NETWORK_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", networkInfo.getDescription());
        networkInfo = networkInfo.toBuilder().setDescription("description").build();
        compareNetworkInfo(NetworkInfoTest.SUBNET_NETWORK_INFO, networkInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        NetworkInfo networkInfo = NetworkInfo.of(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_CONFIGURATION);
        Assert.assertEquals(networkInfo, networkInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(NetworkInfoTest.GENERATED_ID, NetworkInfoTest.NETWORK_INFO.getGeneratedId());
        Assert.assertEquals(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_INFO.getNetworkId());
        Assert.assertEquals(NetworkInfoTest.CREATION_TIMESTAMP, NetworkInfoTest.NETWORK_INFO.getCreationTimestamp());
        Assert.assertEquals(NetworkInfoTest.DESCRIPTION, NetworkInfoTest.NETWORK_INFO.getDescription());
        Assert.assertEquals(NetworkInfoTest.NETWORK_CONFIGURATION, NetworkInfoTest.NETWORK_INFO.getConfiguration());
        Assert.assertEquals(NetworkInfoTest.GENERATED_ID, NetworkInfoTest.SUBNET_NETWORK_INFO.getGeneratedId());
        Assert.assertEquals(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.SUBNET_NETWORK_INFO.getNetworkId());
        Assert.assertEquals(NetworkInfoTest.CREATION_TIMESTAMP, NetworkInfoTest.SUBNET_NETWORK_INFO.getCreationTimestamp());
        Assert.assertEquals(NetworkInfoTest.DESCRIPTION, NetworkInfoTest.SUBNET_NETWORK_INFO.getDescription());
        Assert.assertEquals(NetworkInfoTest.SUBNET_NETWORK_CONFIGURATION, NetworkInfoTest.SUBNET_NETWORK_INFO.getConfiguration());
    }

    @Test
    public void testOf() {
        NetworkInfo networkInfo = NetworkInfo.of(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_CONFIGURATION);
        Assert.assertNull(networkInfo.getGeneratedId());
        Assert.assertEquals(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_INFO.getNetworkId());
        Assert.assertEquals(NetworkInfoTest.NETWORK_CONFIGURATION, NetworkInfoTest.NETWORK_INFO.getConfiguration());
        Assert.assertNull(networkInfo.getCreationTimestamp());
        Assert.assertNull(networkInfo.getDescription());
    }

    @Test
    public void testToAndFromPb() {
        compareNetworkInfo(NetworkInfoTest.NETWORK_INFO, NetworkInfo.fromPb(NetworkInfoTest.NETWORK_INFO.toPb()));
        compareNetworkInfo(NetworkInfoTest.SUBNET_NETWORK_INFO, NetworkInfo.fromPb(NetworkInfoTest.SUBNET_NETWORK_INFO.toPb()));
        NetworkInfo networkInfo = NetworkInfo.of(NetworkInfoTest.NETWORK_ID, NetworkInfoTest.NETWORK_CONFIGURATION);
        compareNetworkInfo(networkInfo, NetworkInfo.fromPb(networkInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        NetworkInfo networkInfo = NetworkInfoTest.NETWORK_INFO.toBuilder().setNetworkId(NetworkId.of("network")).build();
        compareNetworkInfo(NetworkInfoTest.NETWORK_INFO, networkInfo.setProjectId("project"));
    }
}

