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


public class AddressIdTest {
    private static final String PROJECT = "project";

    private static final String REGION = "region";

    private static final String NAME = "addr";

    private static final String GLOBAL_URL = "https://www.googleapis.com/compute/v1/projects/project/global/addresses/addr";

    private static final String REGION_URL = "https://www.googleapis.com/compute/v1/projects/project/regions/region/addresses/addr";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        GlobalAddressId addressId = GlobalAddressId.of(AddressIdTest.PROJECT, AddressIdTest.NAME);
        Assert.assertEquals(AddressIdTest.PROJECT, addressId.getProject());
        Assert.assertEquals(AddressIdTest.NAME, addressId.getAddress());
        Assert.assertEquals(AddressIdTest.GLOBAL_URL, addressId.getSelfLink());
        addressId = GlobalAddressId.of(AddressIdTest.NAME);
        Assert.assertNull(addressId.getProject());
        Assert.assertEquals(AddressIdTest.NAME, addressId.getAddress());
        RegionAddressId regionAddressId = RegionAddressId.of(AddressIdTest.PROJECT, AddressIdTest.REGION, AddressIdTest.NAME);
        Assert.assertEquals(AddressIdTest.PROJECT, regionAddressId.getProject());
        Assert.assertEquals(AddressIdTest.REGION, regionAddressId.getRegion());
        Assert.assertEquals(AddressIdTest.NAME, regionAddressId.getAddress());
        Assert.assertEquals(AddressIdTest.REGION_URL, regionAddressId.getSelfLink());
        regionAddressId = RegionAddressId.of(RegionId.of(AddressIdTest.PROJECT, AddressIdTest.REGION), AddressIdTest.NAME);
        Assert.assertEquals(AddressIdTest.PROJECT, regionAddressId.getProject());
        Assert.assertEquals(AddressIdTest.REGION, regionAddressId.getRegion());
        Assert.assertEquals(AddressIdTest.NAME, regionAddressId.getAddress());
        Assert.assertEquals(AddressIdTest.REGION_URL, regionAddressId.getSelfLink());
        regionAddressId = RegionAddressId.of(AddressIdTest.REGION, AddressIdTest.NAME);
        Assert.assertNull(regionAddressId.getProject());
        Assert.assertEquals(AddressIdTest.REGION, regionAddressId.getRegion());
        Assert.assertEquals(AddressIdTest.NAME, regionAddressId.getAddress());
    }

    @Test
    public void testToAndFromUrlGlobal() {
        GlobalAddressId addressId = GlobalAddressId.of(AddressIdTest.PROJECT, AddressIdTest.NAME);
        compareAddressId(addressId, GlobalAddressId.fromUrl(addressId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid global address URL");
        GlobalAddressId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testToAndFromUrlRegion() {
        RegionAddressId regionAddressId = RegionAddressId.of(AddressIdTest.PROJECT, AddressIdTest.REGION, AddressIdTest.NAME);
        compareRegionAddressId(regionAddressId, RegionAddressId.fromUrl(regionAddressId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid region address URL");
        RegionAddressId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        GlobalAddressId addressId = GlobalAddressId.of(AddressIdTest.PROJECT, AddressIdTest.NAME);
        Assert.assertSame(addressId, addressId.setProjectId(AddressIdTest.PROJECT));
        compareAddressId(addressId, GlobalAddressId.of(AddressIdTest.NAME).setProjectId(AddressIdTest.PROJECT));
        RegionAddressId regionAddressId = RegionAddressId.of(AddressIdTest.PROJECT, AddressIdTest.REGION, AddressIdTest.NAME);
        Assert.assertSame(regionAddressId, regionAddressId.setProjectId(AddressIdTest.PROJECT));
        compareRegionAddressId(regionAddressId, RegionAddressId.of(AddressIdTest.REGION, AddressIdTest.NAME).setProjectId(AddressIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(GlobalAddressId.matchesUrl(GlobalAddressId.of(AddressIdTest.PROJECT, AddressIdTest.NAME).getSelfLink()));
        Assert.assertFalse(GlobalAddressId.matchesUrl("notMatchingUrl"));
        Assert.assertTrue(RegionAddressId.matchesUrl(RegionAddressId.of(AddressIdTest.PROJECT, AddressIdTest.REGION, AddressIdTest.NAME).getSelfLink()));
        Assert.assertFalse(RegionAddressId.matchesUrl("notMatchingUrl"));
    }
}

