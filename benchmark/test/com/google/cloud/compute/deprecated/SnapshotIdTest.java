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


public class SnapshotIdTest {
    private static final String PROJECT = "project";

    private static final String NAME = "snapshot";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/global/snapshots/snapshot";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        SnapshotId snapshotId = SnapshotId.of(SnapshotIdTest.PROJECT, SnapshotIdTest.NAME);
        Assert.assertEquals(SnapshotIdTest.PROJECT, snapshotId.getProject());
        Assert.assertEquals(SnapshotIdTest.NAME, snapshotId.getSnapshot());
        Assert.assertEquals(SnapshotIdTest.URL, snapshotId.getSelfLink());
        snapshotId = SnapshotId.of(SnapshotIdTest.NAME);
        Assert.assertNull(snapshotId.getProject());
        Assert.assertEquals(SnapshotIdTest.NAME, snapshotId.getSnapshot());
    }

    @Test
    public void testToAndFromUrl() {
        SnapshotId snapshotId = SnapshotId.of(SnapshotIdTest.PROJECT, SnapshotIdTest.NAME);
        compareSnapshotId(snapshotId, SnapshotId.fromUrl(snapshotId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid snapshot URL");
        SnapshotId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        SnapshotId snapshotId = SnapshotId.of(SnapshotIdTest.PROJECT, SnapshotIdTest.NAME);
        Assert.assertSame(snapshotId, snapshotId.setProjectId(SnapshotIdTest.PROJECT));
        compareSnapshotId(snapshotId, SnapshotId.of(SnapshotIdTest.NAME).setProjectId(SnapshotIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(SnapshotId.matchesUrl(SnapshotId.of(SnapshotIdTest.PROJECT, SnapshotIdTest.NAME).getSelfLink()));
        Assert.assertFalse(SnapshotId.matchesUrl("notMatchingUrl"));
    }
}

