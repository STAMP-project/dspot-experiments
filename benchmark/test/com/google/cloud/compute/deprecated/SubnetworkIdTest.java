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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SubnetworkIdTest {
    private static final String PROJECT = "project";

    private static final String REGION = "region";

    private static final String NAME = "subnet";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/regions/region/subnetworks/subnet";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        SubnetworkId subnetworkId = SubnetworkId.of(SubnetworkIdTest.PROJECT, SubnetworkIdTest.REGION, SubnetworkIdTest.NAME);
        Assert.assertEquals(SubnetworkIdTest.PROJECT, subnetworkId.getProject());
        Assert.assertEquals(SubnetworkIdTest.REGION, subnetworkId.getRegion());
        Assert.assertEquals(SubnetworkIdTest.NAME, subnetworkId.getSubnetwork());
        Assert.assertEquals(SubnetworkIdTest.URL, subnetworkId.getSelfLink());
        subnetworkId = SubnetworkId.of(SubnetworkIdTest.REGION, SubnetworkIdTest.NAME);
        Assert.assertNull(subnetworkId.getProject());
        Assert.assertEquals(SubnetworkIdTest.REGION, subnetworkId.getRegion());
        Assert.assertEquals(SubnetworkIdTest.NAME, subnetworkId.getSubnetwork());
        subnetworkId = SubnetworkId.of(RegionId.of(SubnetworkIdTest.PROJECT, SubnetworkIdTest.REGION), SubnetworkIdTest.NAME);
        Assert.assertEquals(SubnetworkIdTest.PROJECT, subnetworkId.getProject());
        Assert.assertEquals(SubnetworkIdTest.REGION, subnetworkId.getRegion());
        Assert.assertEquals(SubnetworkIdTest.NAME, subnetworkId.getSubnetwork());
    }

    @Test
    public void testToAndFromUrl() {
        SubnetworkId subnetworkId = SubnetworkId.of(SubnetworkIdTest.PROJECT, SubnetworkIdTest.REGION, SubnetworkIdTest.NAME);
        compareSubnetworkId(subnetworkId, SubnetworkId.fromUrl(subnetworkId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid subnetwork URL");
        SubnetworkId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        SubnetworkId subnetworkId = SubnetworkId.of(SubnetworkIdTest.PROJECT, SubnetworkIdTest.REGION, SubnetworkIdTest.NAME);
        Assert.assertSame(subnetworkId, subnetworkId.setProjectId(SubnetworkIdTest.PROJECT));
        compareSubnetworkId(subnetworkId, SubnetworkId.of(SubnetworkIdTest.REGION, SubnetworkIdTest.NAME).setProjectId(SubnetworkIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(SubnetworkId.matchesUrl(SubnetworkId.of(SubnetworkIdTest.PROJECT, SubnetworkIdTest.REGION, SubnetworkIdTest.NAME).getSelfLink()));
        Assert.assertFalse(SubnetworkId.matchesUrl("notMatchingUrl"));
    }
}

