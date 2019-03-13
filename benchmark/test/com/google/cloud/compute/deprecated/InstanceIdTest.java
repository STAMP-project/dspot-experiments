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


public class InstanceIdTest {
    private static final String PROJECT = "project";

    private static final String ZONE = "zone";

    private static final String NAME = "instance";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/zones/zone/instances/instance";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        InstanceId instanceId = InstanceId.of(InstanceIdTest.PROJECT, InstanceIdTest.ZONE, InstanceIdTest.NAME);
        Assert.assertEquals(InstanceIdTest.PROJECT, instanceId.getProject());
        Assert.assertEquals(InstanceIdTest.ZONE, instanceId.getZone());
        Assert.assertEquals(InstanceIdTest.NAME, instanceId.getInstance());
        Assert.assertEquals(InstanceIdTest.URL, instanceId.getSelfLink());
        instanceId = InstanceId.of(ZoneId.of(InstanceIdTest.PROJECT, InstanceIdTest.ZONE), InstanceIdTest.NAME);
        Assert.assertEquals(InstanceIdTest.PROJECT, instanceId.getProject());
        Assert.assertEquals(InstanceIdTest.ZONE, instanceId.getZone());
        Assert.assertEquals(InstanceIdTest.NAME, instanceId.getInstance());
        Assert.assertEquals(InstanceIdTest.URL, instanceId.getSelfLink());
        instanceId = InstanceId.of(InstanceIdTest.ZONE, InstanceIdTest.NAME);
        Assert.assertNull(instanceId.getProject());
        Assert.assertEquals(InstanceIdTest.ZONE, instanceId.getZone());
        Assert.assertEquals(InstanceIdTest.NAME, instanceId.getInstance());
    }

    @Test
    public void testToAndFromUrl() {
        InstanceId instanceId = InstanceId.of(InstanceIdTest.PROJECT, InstanceIdTest.ZONE, InstanceIdTest.NAME);
        compareInstanceId(instanceId, InstanceId.fromUrl(instanceId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid instance URL");
        InstanceId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        InstanceId instanceId = InstanceId.of(InstanceIdTest.PROJECT, InstanceIdTest.ZONE, InstanceIdTest.NAME);
        Assert.assertSame(instanceId, instanceId.setProjectId(InstanceIdTest.PROJECT));
        compareInstanceId(instanceId, InstanceId.of(InstanceIdTest.ZONE, InstanceIdTest.NAME).setProjectId(InstanceIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(InstanceId.matchesUrl(InstanceId.of(InstanceIdTest.PROJECT, InstanceIdTest.ZONE, InstanceIdTest.NAME).getSelfLink()));
        Assert.assertFalse(InstanceId.matchesUrl("notMatchingUrl"));
    }
}

