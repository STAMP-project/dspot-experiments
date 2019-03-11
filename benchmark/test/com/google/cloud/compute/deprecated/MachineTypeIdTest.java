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


public class MachineTypeIdTest {
    private static final String PROJECT = "project";

    private static final String ZONE = "zone";

    private static final String TYPE = "type";

    private static final String URL = "https://www.googleapis.com/compute/v1/projects/project/zones/zone/machineTypes/type";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testOf() {
        MachineTypeId machineTypeId = MachineTypeId.of(MachineTypeIdTest.PROJECT, MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE);
        Assert.assertEquals(MachineTypeIdTest.PROJECT, machineTypeId.getProject());
        Assert.assertEquals(MachineTypeIdTest.ZONE, machineTypeId.getZone());
        Assert.assertEquals(MachineTypeIdTest.TYPE, machineTypeId.getType());
        Assert.assertEquals(MachineTypeIdTest.URL, machineTypeId.getSelfLink());
        machineTypeId = MachineTypeId.of(MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE);
        Assert.assertNull(machineTypeId.getProject());
        Assert.assertEquals(MachineTypeIdTest.ZONE, machineTypeId.getZone());
        Assert.assertEquals(MachineTypeIdTest.TYPE, machineTypeId.getType());
    }

    @Test
    public void testToAndFromUrl() {
        MachineTypeId machineTypeId = MachineTypeId.of(MachineTypeIdTest.PROJECT, MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE);
        compareMachineTypeId(machineTypeId, MachineTypeId.fromUrl(machineTypeId.getSelfLink()));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("notMatchingUrl is not a valid machine type URL");
        MachineTypeId.fromUrl("notMatchingUrl");
    }

    @Test
    public void testSetProjectId() {
        MachineTypeId machineTypeId = MachineTypeId.of(MachineTypeIdTest.PROJECT, MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE);
        Assert.assertSame(machineTypeId, machineTypeId.setProjectId(MachineTypeIdTest.PROJECT));
        compareMachineTypeId(machineTypeId, MachineTypeId.of(MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE).setProjectId(MachineTypeIdTest.PROJECT));
    }

    @Test
    public void testMatchesUrl() {
        Assert.assertTrue(MachineTypeId.matchesUrl(MachineTypeId.of(MachineTypeIdTest.PROJECT, MachineTypeIdTest.ZONE, MachineTypeIdTest.TYPE).getSelfLink()));
        Assert.assertFalse(MachineTypeId.matchesUrl("notMatchingUrl"));
    }
}

