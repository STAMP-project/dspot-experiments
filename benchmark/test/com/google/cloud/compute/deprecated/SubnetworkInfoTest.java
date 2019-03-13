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


import org.junit.Assert;
import org.junit.Test;


public class SubnetworkInfoTest {
    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final SubnetworkId SUBNETWORK_ID = SubnetworkId.of("project", "region", "subnetwork");

    private static final String GATEWAY_ADDRESS = "192.168.1.1";

    private static final NetworkId NETWORK_ID = NetworkId.of("project", "network");

    private static final String IP_RANGE = "192.168.0.0/16";

    private static final SubnetworkInfo SUBNETWORK_INFO = SubnetworkInfo.newBuilder(SubnetworkInfoTest.SUBNETWORK_ID, SubnetworkInfoTest.NETWORK_ID, SubnetworkInfoTest.IP_RANGE).setGeneratedId(SubnetworkInfoTest.GENERATED_ID).setCreationTimestamp(SubnetworkInfoTest.CREATION_TIMESTAMP).setDescription(SubnetworkInfoTest.DESCRIPTION).setGatewayAddress(SubnetworkInfoTest.GATEWAY_ADDRESS).build();

    @Test
    public void testToBuilder() {
        compareSubnetworkInfo(SubnetworkInfoTest.SUBNETWORK_INFO, SubnetworkInfoTest.SUBNETWORK_INFO.toBuilder().build());
        SubnetworkInfo subnetworkInfo = SubnetworkInfoTest.SUBNETWORK_INFO.toBuilder().setDescription("newDescription").build();
        Assert.assertEquals("newDescription", subnetworkInfo.getDescription());
        subnetworkInfo = subnetworkInfo.toBuilder().setDescription("description").build();
        compareSubnetworkInfo(SubnetworkInfoTest.SUBNETWORK_INFO, subnetworkInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        SubnetworkInfo subnetworkInfo = SubnetworkInfo.of(SubnetworkInfoTest.SUBNETWORK_ID, SubnetworkInfoTest.NETWORK_ID, SubnetworkInfoTest.IP_RANGE);
        Assert.assertEquals(subnetworkInfo, subnetworkInfo.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(SubnetworkInfoTest.GENERATED_ID, SubnetworkInfoTest.SUBNETWORK_INFO.getGeneratedId());
        Assert.assertEquals(SubnetworkInfoTest.SUBNETWORK_ID, SubnetworkInfoTest.SUBNETWORK_INFO.getSubnetworkId());
        Assert.assertEquals(SubnetworkInfoTest.CREATION_TIMESTAMP, SubnetworkInfoTest.SUBNETWORK_INFO.getCreationTimestamp());
        Assert.assertEquals(SubnetworkInfoTest.DESCRIPTION, SubnetworkInfoTest.SUBNETWORK_INFO.getDescription());
        Assert.assertEquals(SubnetworkInfoTest.GATEWAY_ADDRESS, SubnetworkInfoTest.SUBNETWORK_INFO.getGatewayAddress());
        Assert.assertEquals(SubnetworkInfoTest.NETWORK_ID, SubnetworkInfoTest.SUBNETWORK_INFO.getNetwork());
        Assert.assertEquals(SubnetworkInfoTest.IP_RANGE, SubnetworkInfoTest.SUBNETWORK_INFO.getIpRange());
    }

    @Test
    public void testOf() {
        SubnetworkInfo subnetworkInfo = SubnetworkInfo.of(SubnetworkInfoTest.SUBNETWORK_ID, SubnetworkInfoTest.NETWORK_ID, SubnetworkInfoTest.IP_RANGE);
        Assert.assertNull(subnetworkInfo.getGeneratedId());
        Assert.assertEquals(SubnetworkInfoTest.SUBNETWORK_ID, subnetworkInfo.getSubnetworkId());
        Assert.assertNull(subnetworkInfo.getCreationTimestamp());
        Assert.assertNull(subnetworkInfo.getDescription());
        Assert.assertNull(subnetworkInfo.getGatewayAddress());
        Assert.assertEquals(SubnetworkInfoTest.NETWORK_ID, subnetworkInfo.getNetwork());
        Assert.assertEquals(SubnetworkInfoTest.IP_RANGE, subnetworkInfo.getIpRange());
    }

    @Test
    public void testToAndFromPb() {
        compareSubnetworkInfo(SubnetworkInfoTest.SUBNETWORK_INFO, SubnetworkInfo.fromPb(SubnetworkInfoTest.SUBNETWORK_INFO.toPb()));
        SubnetworkInfo subnetworkInfo = SubnetworkInfo.of(SubnetworkInfoTest.SUBNETWORK_ID, SubnetworkInfoTest.NETWORK_ID, SubnetworkInfoTest.IP_RANGE);
        compareSubnetworkInfo(subnetworkInfo, SubnetworkInfo.fromPb(subnetworkInfo.toPb()));
    }

    @Test
    public void testSetProjectId() {
        SubnetworkInfo subnetworkInfo = SubnetworkInfoTest.SUBNETWORK_INFO.toBuilder().setSubnetworkId(SubnetworkId.of("region", "subnetwork")).setNetwork(NetworkId.of("network")).build();
        compareSubnetworkInfo(SubnetworkInfoTest.SUBNETWORK_INFO, subnetworkInfo.setProjectId("project"));
    }
}

