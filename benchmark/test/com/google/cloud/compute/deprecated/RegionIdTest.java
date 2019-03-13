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


public class RegionIdTest {
    private static final String PROJECT = "project";

    private static final String REGION = "region";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/regions/region";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        RegionId regionId = RegionId.of(RegionIdTest.PROJECT, RegionIdTest.REGION);
        Assert.assertEquals(RegionIdTest.PROJECT, regionId.getProject());
        Assert.assertEquals(RegionIdTest.REGION, regionId.getRegion());
        Assert.assertEquals(RegionIdTest.URL, regionId.getSelfLink());
        regionId = RegionId.of(RegionIdTest.REGION);
        Assert.assertNull(regionId.getProject());
        Assert.assertEquals(RegionIdTest.REGION, regionId.getRegion());
    }

    @Test
    public void testToAndFromUrl() {
        RegionId regionId = RegionId.of(RegionIdTest.PROJECT, RegionIdTest.REGION);
        compareRegionId(regionId, RegionId.fromUrl(regionId.getSelfLink()));
    }

    @Test
    public void testSetProjectId() {
        RegionId regionId = RegionId.of(RegionIdTest.PROJECT, RegionIdTest.REGION);
        Assert.assertSame(regionId, regionId.setProjectId(RegionIdTest.PROJECT));
        compareRegionId(regionId, RegionId.of(RegionIdTest.REGION).setProjectId(RegionIdTest.PROJECT));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid region URL");
        RegionId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(RegionId.matchesUrl(RegionId.of(RegionIdTest.PROJECT, RegionIdTest.REGION).getSelfLink()));
        Assert.assertFalse(RegionId.matchesUrl("notMatchingUrl"));
    }
}

