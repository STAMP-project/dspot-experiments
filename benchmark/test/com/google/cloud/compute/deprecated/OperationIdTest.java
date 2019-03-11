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


import OperationId.Type.GLOBAL;
import OperationId.Type.REGION;
import OperationId.Type.ZONE;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class OperationIdTest {
    private static final String PROJECT = "project";

    private static final String ZONE = "zone";

    private static final String REGION = "region";

    private static final String NAME = "op";

    private static final String GLOBAL_URL = "https://www.googleapis.com/compute/v1/projects/project/global/operations/op";

    private static final String ZONE_URL = "https://www.googleapis.com/compute/v1/projects/project/zones/zone/operations/op";

    private static final String REGION_URL = "https://www.googleapis.com/compute/v1/projects/project/regions/region/operations/op";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        GlobalOperationId operationId = GlobalOperationId.of(OperationIdTest.PROJECT, OperationIdTest.NAME);
        Assert.assertEquals(GLOBAL, operationId.getType());
        Assert.assertEquals(OperationIdTest.PROJECT, operationId.getProject());
        Assert.assertEquals(OperationIdTest.NAME, operationId.getOperation());
        Assert.assertEquals(OperationIdTest.GLOBAL_URL, operationId.getSelfLink());
        operationId = GlobalOperationId.of(OperationIdTest.NAME);
        Assert.assertEquals(GLOBAL, operationId.getType());
        Assert.assertNull(operationId.getProject());
        Assert.assertEquals(OperationIdTest.NAME, operationId.getOperation());
        ZoneOperationId zoneOperationId = ZoneOperationId.of(OperationIdTest.PROJECT, OperationIdTest.ZONE, OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.ZONE, zoneOperationId.getType());
        Assert.assertEquals(OperationIdTest.PROJECT, zoneOperationId.getProject());
        Assert.assertEquals(OperationIdTest.ZONE, zoneOperationId.getZone());
        Assert.assertEquals(OperationIdTest.NAME, zoneOperationId.getOperation());
        Assert.assertEquals(OperationIdTest.ZONE_URL, zoneOperationId.getSelfLink());
        zoneOperationId = ZoneOperationId.of(OperationIdTest.ZONE, OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.ZONE, zoneOperationId.getType());
        Assert.assertNull(zoneOperationId.getProject());
        Assert.assertEquals(OperationIdTest.ZONE, zoneOperationId.getZone());
        Assert.assertEquals(OperationIdTest.NAME, zoneOperationId.getOperation());
        zoneOperationId = ZoneOperationId.of(ZoneId.of(OperationIdTest.PROJECT, OperationIdTest.ZONE), OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.ZONE, zoneOperationId.getType());
        Assert.assertEquals(OperationIdTest.PROJECT, zoneOperationId.getProject());
        Assert.assertEquals(OperationIdTest.ZONE, zoneOperationId.getZone());
        Assert.assertEquals(OperationIdTest.NAME, zoneOperationId.getOperation());
        RegionOperationId regionOperationId = RegionOperationId.of(OperationIdTest.PROJECT, OperationIdTest.REGION, OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.REGION, regionOperationId.getType());
        Assert.assertEquals(OperationIdTest.PROJECT, regionOperationId.getProject());
        Assert.assertEquals(OperationIdTest.REGION, regionOperationId.getRegion());
        Assert.assertEquals(OperationIdTest.NAME, regionOperationId.getOperation());
        Assert.assertEquals(OperationIdTest.REGION_URL, regionOperationId.getSelfLink());
        regionOperationId = RegionOperationId.of(OperationIdTest.REGION, OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.REGION, regionOperationId.getType());
        Assert.assertNull(regionOperationId.getProject());
        Assert.assertEquals(OperationIdTest.REGION, regionOperationId.getRegion());
        Assert.assertEquals(OperationIdTest.NAME, regionOperationId.getOperation());
        regionOperationId = RegionOperationId.of(RegionId.of(OperationIdTest.PROJECT, OperationIdTest.REGION), OperationIdTest.NAME);
        Assert.assertEquals(OperationId.Type.REGION, regionOperationId.getType());
        Assert.assertEquals(OperationIdTest.PROJECT, regionOperationId.getProject());
        Assert.assertEquals(OperationIdTest.REGION, regionOperationId.getRegion());
        Assert.assertEquals(OperationIdTest.NAME, regionOperationId.getOperation());
    }

    @Test
    public void testToAndFromUrlGlobal() {
        GlobalOperationId operationId = GlobalOperationId.of(OperationIdTest.PROJECT, OperationIdTest.NAME);
        compareOperationId(operationId, GlobalOperationId.fromUrl(operationId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid global operation URL");
        GlobalOperationId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testToAndFromUrlRegion() {
        RegionOperationId regionOperationId = RegionOperationId.of(OperationIdTest.PROJECT, OperationIdTest.REGION, OperationIdTest.NAME);
        compareRegionOperationId(regionOperationId, RegionOperationId.fromUrl(regionOperationId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid region operation URL");
        RegionOperationId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testToAndFromUrlZone() {
        ZoneOperationId zoneOperationId = ZoneOperationId.of(OperationIdTest.PROJECT, OperationIdTest.ZONE, OperationIdTest.NAME);
        compareZoneOperationId(zoneOperationId, ZoneOperationId.fromUrl(zoneOperationId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid zone operation URL");
        ZoneOperationId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        GlobalOperationId operationId = GlobalOperationId.of(OperationIdTest.PROJECT, OperationIdTest.NAME);
        Assert.assertSame(operationId, operationId.setProjectId(OperationIdTest.PROJECT));
        compareOperationId(operationId, GlobalOperationId.of(OperationIdTest.NAME).setProjectId(OperationIdTest.PROJECT));
        ZoneOperationId zoneOperationId = ZoneOperationId.of(OperationIdTest.PROJECT, OperationIdTest.ZONE, OperationIdTest.NAME);
        Assert.assertSame(zoneOperationId, zoneOperationId.setProjectId(OperationIdTest.PROJECT));
        compareZoneOperationId(zoneOperationId, ZoneOperationId.of(OperationIdTest.ZONE, OperationIdTest.NAME).setProjectId(OperationIdTest.PROJECT));
        RegionOperationId regionOperationId = RegionOperationId.of(OperationIdTest.PROJECT, OperationIdTest.REGION, OperationIdTest.NAME);
        Assert.assertSame(regionOperationId, regionOperationId.setProjectId(OperationIdTest.PROJECT));
        compareRegionOperationId(regionOperationId, RegionOperationId.of(OperationIdTest.REGION, OperationIdTest.NAME).setProjectId(OperationIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(GlobalOperationId.matchesUrl(GlobalOperationId.of(OperationIdTest.PROJECT, OperationIdTest.NAME).getSelfLink()));
        Assert.assertFalse(GlobalOperationId.matchesUrl("notMatchingUrl"));
        Assert.assertTrue(RegionOperationId.matchesUrl(RegionOperationId.of(OperationIdTest.PROJECT, OperationIdTest.REGION, OperationIdTest.NAME).getSelfLink()));
        Assert.assertFalse(RegionOperationId.matchesUrl("notMatchingUrl"));
        Assert.assertTrue(ZoneOperationId.matchesUrl(ZoneOperationId.of(OperationIdTest.PROJECT, OperationIdTest.REGION, OperationIdTest.NAME).getSelfLink()));
        Assert.assertFalse(ZoneOperationId.matchesUrl("notMatchingUrl"));
    }
}

