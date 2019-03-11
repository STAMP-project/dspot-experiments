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
import Region.Quota;
import Region.Status;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RegionTest {
    private static final RegionId REGION_ID = RegionId.of("project", "region");

    private static final String GENERATED_ID = "42";

    private static final Long CREATION_TIMESTAMP = 1453293540000L;

    private static final String DESCRIPTION = "description";

    private static final Status STATUS = Status.DOWN;

    private static final ZoneId ZONE_ID1 = ZoneId.of("project", "zone1");

    private static final ZoneId ZONE_ID2 = ZoneId.of("project", "zone2");

    private static final List<ZoneId> ZONES = ImmutableList.of(RegionTest.ZONE_ID1, RegionTest.ZONE_ID2);

    private static final Quota QUOTA1 = new Region.Quota("METRIC1", 2, 1);

    private static final Quota QUOTA2 = new Region.Quota("METRIC2", 4, 3);

    private static final List<Region.Quota> QUOTAS = ImmutableList.of(RegionTest.QUOTA1, RegionTest.QUOTA2);

    private static final DeprecationStatus<RegionId> DEPRECATION_STATUS = DeprecationStatus.of(DELETED, RegionTest.REGION_ID);

    private static final Region REGION = Region.builder().setRegionId(RegionTest.REGION_ID).setGeneratedId(RegionTest.GENERATED_ID).setCreationTimestamp(RegionTest.CREATION_TIMESTAMP).setDescription(RegionTest.DESCRIPTION).setStatus(RegionTest.STATUS).setZones(RegionTest.ZONES).setQuotas(RegionTest.QUOTAS).setDeprecationStatus(RegionTest.DEPRECATION_STATUS).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(RegionTest.REGION_ID, RegionTest.REGION.getRegionId());
        Assert.assertEquals(RegionTest.GENERATED_ID, RegionTest.REGION.getGeneratedId());
        Assert.assertEquals(RegionTest.CREATION_TIMESTAMP, RegionTest.REGION.getCreationTimestamp());
        Assert.assertEquals(RegionTest.DESCRIPTION, RegionTest.REGION.getDescription());
        Assert.assertEquals(RegionTest.STATUS, RegionTest.REGION.getStatus());
        Assert.assertEquals(RegionTest.ZONES, RegionTest.REGION.getZones());
        Assert.assertEquals(RegionTest.QUOTAS, RegionTest.REGION.getQuotas());
        Assert.assertEquals(RegionTest.DEPRECATION_STATUS, RegionTest.REGION.getDeprecationStatus());
    }

    @Test
    public void testToAndFromPb() {
        Region region = Region.fromPb(RegionTest.REGION.toPb());
        compareRegions(RegionTest.REGION, region);
        Assert.assertEquals(RegionTest.REGION_ID.getProject(), region.getRegionId().getProject());
        Assert.assertEquals(RegionTest.REGION_ID.getRegion(), region.getRegionId().getRegion());
        region = Region.builder().setRegionId(RegionTest.REGION_ID).build();
        compareRegions(region, Region.fromPb(region.toPb()));
    }
}

