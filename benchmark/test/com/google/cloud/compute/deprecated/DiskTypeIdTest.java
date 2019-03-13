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


public class DiskTypeIdTest {
    private static final String PROJECT = "project";

    private static final String ZONE = "zone";

    private static final String DISK_TYPE = "diskType";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/zones/zone/diskTypes/diskType";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        DiskTypeId diskTypeId = DiskTypeId.of(DiskTypeIdTest.PROJECT, DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE);
        Assert.assertEquals(DiskTypeIdTest.PROJECT, diskTypeId.getProject());
        Assert.assertEquals(DiskTypeIdTest.ZONE, diskTypeId.getZone());
        Assert.assertEquals(DiskTypeIdTest.DISK_TYPE, diskTypeId.getType());
        Assert.assertEquals(DiskTypeIdTest.URL, diskTypeId.getSelfLink());
        diskTypeId = DiskTypeId.of(DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE);
        Assert.assertNull(diskTypeId.getProject());
        Assert.assertEquals(DiskTypeIdTest.ZONE, diskTypeId.getZone());
        Assert.assertEquals(DiskTypeIdTest.DISK_TYPE, diskTypeId.getType());
    }

    @Test
    public void testToAndFromUrl() {
        DiskTypeId diskTypeId = DiskTypeId.of(DiskTypeIdTest.PROJECT, DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE);
        Assert.assertSame(diskTypeId, diskTypeId.setProjectId(DiskTypeIdTest.PROJECT));
        compareDiskTypeId(diskTypeId, DiskTypeId.fromUrl(diskTypeId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid disk type URL");
        DiskTypeId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        DiskTypeId diskTypeId = DiskTypeId.of(DiskTypeIdTest.PROJECT, DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE);
        Assert.assertSame(diskTypeId, diskTypeId.setProjectId(DiskTypeIdTest.PROJECT));
        compareDiskTypeId(diskTypeId, DiskTypeId.of(DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE).setProjectId(DiskTypeIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(DiskTypeId.matchesUrl(DiskTypeId.of(DiskTypeIdTest.PROJECT, DiskTypeIdTest.ZONE, DiskTypeIdTest.DISK_TYPE).getSelfLink()));
        Assert.assertFalse(DiskTypeId.matchesUrl("notMatchingUrl"));
    }
}

