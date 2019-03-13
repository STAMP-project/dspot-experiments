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


public class DiskIdTest {
    private static final String PROJECT = "project";

    private static final String ZONE = "zone";

    private static final String NAME = "disk";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/zones/zone/disks/disk";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        DiskId diskId = DiskId.of(DiskIdTest.PROJECT, DiskIdTest.ZONE, DiskIdTest.NAME);
        Assert.assertEquals(DiskIdTest.PROJECT, diskId.getProject());
        Assert.assertEquals(DiskIdTest.ZONE, diskId.getZone());
        Assert.assertEquals(DiskIdTest.NAME, diskId.getDisk());
        Assert.assertEquals(DiskIdTest.URL, diskId.getSelfLink());
        diskId = DiskId.of(DiskIdTest.ZONE, DiskIdTest.NAME);
        Assert.assertNull(diskId.getProject());
        Assert.assertEquals(DiskIdTest.ZONE, diskId.getZone());
        Assert.assertEquals(DiskIdTest.NAME, diskId.getDisk());
        diskId = DiskId.of(ZoneId.of(DiskIdTest.ZONE), DiskIdTest.NAME);
        Assert.assertNull(diskId.getProject());
        Assert.assertEquals(DiskIdTest.ZONE, diskId.getZone());
        Assert.assertEquals(DiskIdTest.NAME, diskId.getDisk());
    }

    @Test
    public void testToAndFromUrl() {
        DiskId diskId = DiskId.of(DiskIdTest.PROJECT, DiskIdTest.ZONE, DiskIdTest.NAME);
        compareDiskId(diskId, DiskId.fromUrl(diskId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid disk URL");
        DiskId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        DiskId diskId = DiskId.of(DiskIdTest.PROJECT, DiskIdTest.ZONE, DiskIdTest.NAME);
        Assert.assertSame(diskId, diskId.setProjectId(DiskIdTest.PROJECT));
        compareDiskId(diskId, DiskId.of(DiskIdTest.ZONE, DiskIdTest.NAME).setProjectId(DiskIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(DiskId.matchesUrl(DiskId.of(DiskIdTest.PROJECT, DiskIdTest.ZONE, DiskIdTest.NAME).getSelfLink()));
        Assert.assertFalse(DiskId.matchesUrl("notMatchingUrl"));
    }
}

