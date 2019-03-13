/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.regions;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RegionMetadataTest {
    private static RegionMetadata metadata;

    @Test
    public void testGetRegion() {
        Region region = RegionMetadataTest.metadata.getRegion("us-east-1");
        Assert.assertNotNull(region);
        Assert.assertEquals("us-east-1", region.getName());
        region = RegionMetadataTest.metadata.getRegion("us-west-1");
        Assert.assertNotNull(region);
        Assert.assertEquals("us-west-1", region.getName());
        region = RegionMetadataTest.metadata.getRegion("cn-north-1");
        Assert.assertNotNull(region);
        Assert.assertEquals("cn-north-1", region.getName());
        region = RegionMetadataTest.metadata.getRegion("bogus-monkeys");
        Assert.assertNull(region);
    }

    @Test
    public void testGetRegionsForService() {
        List<Region> regions = RegionMetadataTest.metadata.getRegionsForService("s3");
        Assert.assertNotNull(regions);
        Assert.assertEquals(2, regions.size());
        Assert.assertEquals("us-east-1", regions.get(0).getName());
        Assert.assertEquals("us-west-1", regions.get(1).getName());
        regions = RegionMetadataTest.metadata.getRegionsForService("bogus-monkeys");
        Assert.assertNotNull(regions);
        Assert.assertTrue(regions.isEmpty());
    }

    @Test
    public void testGetRegionByEndpoint() {
        Region region = RegionMetadataTest.metadata.getRegionByEndpoint("s3-us-west-1.amazonaws.com");
        Assert.assertNotNull(region);
        Assert.assertEquals("us-west-1", region.getName());
        try {
            RegionMetadataTest.metadata.getRegionByEndpoint("bogus-monkeys");
            Assert.fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }
}

