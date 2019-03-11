/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.onewire.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for {@link SensorId}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class SensorIdTest {
    @Test
    public void bareSensorIdConstructionTest() {
        SensorId sensorId = new SensorId("28.0123456789ab");
        Assert.assertEquals("/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
        sensorId = new SensorId("/28.0123456789ab");
        Assert.assertEquals("/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
    }

    @Test
    public void hubMainSensorIdConstructionTest() {
        SensorId sensorId = new SensorId("1F.0123456789ab/main/28.0123456789ab");
        Assert.assertEquals("/1F.0123456789ab/main/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
        sensorId = new SensorId("/1F.0123456789ab/main/28.0123456789ab");
        Assert.assertEquals("/1F.0123456789ab/main/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
    }

    @Test
    public void hubAuxSensorIdConstructionTest() {
        SensorId sensorId = new SensorId("1F.0123456789ab/aux/28.0123456789ab");
        Assert.assertEquals("/1F.0123456789ab/aux/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
        sensorId = new SensorId("/1F.0123456789ab/aux/28.0123456789ab");
        Assert.assertEquals("/1F.0123456789ab/aux/28.0123456789ab", sensorId.getFullPath());
        Assert.assertEquals("28.0123456789ab", sensorId.getId());
        Assert.assertEquals("28", sensorId.getFamilyId());
    }

    @Test
    public void equalsTest() {
        SensorId sensorId1 = new SensorId("1F.0123456789ab/aux/28.0123456789ab");
        SensorId sensorId2 = new SensorId("1F.0123456789ab/aux/28.0123456789ab");
        SensorId sensorId3 = new SensorId("1F.0123456789ab/aux/28.0123456789ac");
        Assert.assertTrue(sensorId1.equals(sensorId2));
        Assert.assertFalse(sensorId1.equals(sensorId3));
    }
}

