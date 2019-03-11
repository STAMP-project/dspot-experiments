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


import DeprecationStatus.Status.DELETED;
import Zone.Status;
import org.junit.Assert;
import org.junit.Test;


public class ZoneTest {
    private static final ZoneId ZONE_ID = ZoneId.of("project", "zone");

    private static final RegionId REGION_ID = RegionId.of("project", "region");

    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final Status STATUS = Status.DOWN;

    private static final DeprecationStatus<ZoneId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, ZoneTest.ZONE_ID);

    private static final Zone ZONE = Zone.builder().setZoneId(ZoneTest.ZONE_ID).setGeneratedId(ZoneTest.GENERATED_ID).setCreationTimestamp(ZoneTest.CREATION_TIMESTAMP).setDescription(ZoneTest.DESCRIPTION).setStatus(ZoneTest.STATUS).setDeprecationStatus(ZoneTest.DEPRECATION_STATUS).setRegion(ZoneTest.REGION_ID).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(ZoneTest.REGION_ID, ZoneTest.ZONE.getRegion());
        Assert.assertEquals(ZoneTest.GENERATED_ID, ZoneTest.ZONE.getGeneratedId());
        Assert.assertEquals(ZoneTest.CREATION_TIMESTAMP, ZoneTest.ZONE.getCreationTimestamp());
        Assert.assertEquals(ZoneTest.DESCRIPTION, ZoneTest.ZONE.getDescription());
        Assert.assertEquals(ZoneTest.STATUS, ZoneTest.ZONE.getStatus());
        Assert.assertEquals(ZoneTest.REGION_ID, ZoneTest.ZONE.getRegion());
        Assert.assertEquals(ZoneTest.DEPRECATION_STATUS, ZoneTest.ZONE.getDeprecationStatus());
    }

    @Test
    public void testToAndFromPb() {
        com.google.api.services.compute.model.Zone zonePb = ZoneTest.ZONE.toPb();
        Assert.assertEquals(ZoneTest.REGION_ID.getSelfLink(), zonePb.getRegion());
        Zone zone = Zone.fromPb(zonePb);
        compareZones(ZoneTest.ZONE, zone);
        Assert.assertEquals(ZoneTest.ZONE_ID.getProject(), zone.getZoneId().getProject());
        Assert.assertEquals(ZoneTest.ZONE_ID.getZone(), zone.getZoneId().getZone());
        zone = Zone.builder().setZoneId(ZoneTest.ZONE_ID).build();
        compareZones(zone, Zone.fromPb(zone.toPb()));
    }
}

