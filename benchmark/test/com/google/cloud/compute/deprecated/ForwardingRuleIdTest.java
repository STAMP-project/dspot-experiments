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


import ForwardingRuleId.Type.GLOBAL;
import ForwardingRuleId.Type.REGION;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ForwardingRuleIdTest {
    private static final String PROJECT = "project";

    private static final String REGION = "region";

    private static final String NAME = "rule";

    private static final String GLOBAL_URL = "https://www.googleapis.com/compute/v1/projects/project/global/forwardingRules/rule";

    private static final String REGION_URL = "https://www.googleapis.com/compute/v1/projects/" + "project/regions/region/forwardingRules/rule";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        GlobalForwardingRuleId forwardingRuleId = GlobalForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.NAME);
        Assert.assertEquals(ForwardingRuleIdTest.PROJECT, forwardingRuleId.getProject());
        Assert.assertEquals(ForwardingRuleIdTest.NAME, forwardingRuleId.getRule());
        Assert.assertEquals(ForwardingRuleIdTest.GLOBAL_URL, forwardingRuleId.getSelfLink());
        Assert.assertEquals(GLOBAL, forwardingRuleId.getType());
        forwardingRuleId = GlobalForwardingRuleId.of(ForwardingRuleIdTest.NAME);
        Assert.assertNull(forwardingRuleId.getProject());
        Assert.assertEquals(ForwardingRuleIdTest.NAME, forwardingRuleId.getRule());
        Assert.assertEquals(GLOBAL, forwardingRuleId.getType());
        RegionForwardingRuleId regionForwardingRuleId = RegionForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME);
        Assert.assertEquals(ForwardingRuleIdTest.PROJECT, regionForwardingRuleId.getProject());
        Assert.assertEquals(ForwardingRuleIdTest.REGION, regionForwardingRuleId.getRegion());
        Assert.assertEquals(ForwardingRuleIdTest.NAME, regionForwardingRuleId.getRule());
        Assert.assertEquals(ForwardingRuleIdTest.REGION_URL, regionForwardingRuleId.getSelfLink());
        Assert.assertEquals(ForwardingRuleId.Type.REGION, regionForwardingRuleId.getType());
        regionForwardingRuleId = RegionForwardingRuleId.of(RegionId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION), ForwardingRuleIdTest.NAME);
        Assert.assertEquals(ForwardingRuleIdTest.PROJECT, regionForwardingRuleId.getProject());
        Assert.assertEquals(ForwardingRuleIdTest.REGION, regionForwardingRuleId.getRegion());
        Assert.assertEquals(ForwardingRuleIdTest.NAME, regionForwardingRuleId.getRule());
        Assert.assertEquals(ForwardingRuleIdTest.REGION_URL, regionForwardingRuleId.getSelfLink());
        Assert.assertEquals(ForwardingRuleId.Type.REGION, regionForwardingRuleId.getType());
        regionForwardingRuleId = RegionForwardingRuleId.of(ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME);
        Assert.assertNull(regionForwardingRuleId.getProject());
        Assert.assertEquals(ForwardingRuleIdTest.REGION, regionForwardingRuleId.getRegion());
        Assert.assertEquals(ForwardingRuleIdTest.NAME, regionForwardingRuleId.getRule());
        Assert.assertEquals(ForwardingRuleId.Type.REGION, regionForwardingRuleId.getType());
    }

    @Test
    public void testToAndFromUrlGlobal() {
        GlobalForwardingRuleId forwardingRuleId = GlobalForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.NAME);
        compareGlobalForwardingRuleId(forwardingRuleId, GlobalForwardingRuleId.fromUrl(forwardingRuleId.getSelfLink()));
        RegionForwardingRuleId regionForwardingRuleId = RegionForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME);
        compareRegionForwardingRuleId(regionForwardingRuleId, RegionForwardingRuleId.fromUrl(regionForwardingRuleId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid global forwarding rule URL");
        GlobalForwardingRuleId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testToAndFromUrlRegion() {
        RegionForwardingRuleId regionForwardingRuleId = RegionForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME);
        compareRegionForwardingRuleId(regionForwardingRuleId, RegionForwardingRuleId.fromUrl(regionForwardingRuleId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid region forwarding rule URL");
        RegionForwardingRuleId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        GlobalForwardingRuleId forwardingRuleId = GlobalForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.NAME);
        Assert.assertSame(forwardingRuleId, forwardingRuleId.setProjectId(ForwardingRuleIdTest.PROJECT));
        compareGlobalForwardingRuleId(forwardingRuleId, GlobalForwardingRuleId.of(ForwardingRuleIdTest.NAME).setProjectId(ForwardingRuleIdTest.PROJECT));
        RegionForwardingRuleId regionForwardingRuleId = RegionForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME);
        Assert.assertSame(regionForwardingRuleId, regionForwardingRuleId.setProjectId(ForwardingRuleIdTest.PROJECT));
        compareRegionForwardingRuleId(regionForwardingRuleId, RegionForwardingRuleId.of(ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME).setProjectId(ForwardingRuleIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(GlobalForwardingRuleId.matchesUrl(GlobalForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.NAME).getSelfLink()));
        Assert.assertFalse(GlobalForwardingRuleId.matchesUrl("notMatchingUrl"));
        Assert.assertTrue(RegionForwardingRuleId.matchesUrl(RegionForwardingRuleId.of(ForwardingRuleIdTest.PROJECT, ForwardingRuleIdTest.REGION, ForwardingRuleIdTest.NAME).getSelfLink()));
        Assert.assertFalse(RegionForwardingRuleId.matchesUrl("notMatchingUrl"));
    }
}

