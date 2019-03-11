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


import com.amazonaws.SDKGlobalConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for RegionUtils class.
 */
public class RegionUtilsIntegrationTest {
    /**
     * Tests that region file override could be properly loaded, and the
     * endpoint verification is also disabled so that invalid (not owned by AWS)
     * endpoints don't trigger RuntimeException.
     */
    @Test
    public void testRegionFileOverride() {
        String fakeRegionFilePath = RegionUtilsIntegrationTest.class.getResource("fake-regions.xml").getPath();
        System.setProperty(SDKGlobalConfiguration.REGIONS_FILE_OVERRIDE_SYSTEM_PROPERTY, fakeRegionFilePath);
        RegionUtils.initialize();
        Assert.assertEquals(2, RegionUtils.getRegions().size());
        Assert.assertEquals("hostname.com", RegionUtils.getRegion("us-east-1").getDomain());
        Assert.assertEquals("fake.hostname.com", RegionUtils.getRegion("us-east-1").getServiceEndpoint("cloudformation"));
        Assert.assertEquals("amazonaws.com", RegionUtils.getRegion("us-west-1").getDomain());
    }
}

