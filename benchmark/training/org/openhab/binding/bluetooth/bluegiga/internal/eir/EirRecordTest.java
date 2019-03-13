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
package org.openhab.binding.bluetooth.bluegiga.internal.eir;


import EirDataType.EIR_MANUFACTURER_SPECIFIC;
import EirDataType.EIR_SVC_DATA_UUID128;
import EirDataType.EIR_SVC_DATA_UUID16;
import EirDataType.EIR_SVC_DATA_UUID32;
import EirDataType.EIR_SVC_UUID128_COMPLETE;
import EirDataType.EIR_SVC_UUID16_COMPLETE;
import EirDataType.EIR_SVC_UUID32_COMPLETE;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class EirRecordTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testProcess16BitUUIDs() {
        int[] data = new int[]{ 3, 15, 24, 0, 24 };
        String batteryService = "0000180f-0000-0000-0000-000000000000";
        String genericAccess = "00001800-0000-0000-0000-000000000000";
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_UUID16_COMPLETE, eirRecord.getType());
        List<UUID> uuids = ((List<UUID>) (eirRecord.getRecord()));
        Assert.assertEquals(2, uuids.size());
        Assert.assertTrue(uuids.contains(UUID.fromString(batteryService)));
        Assert.assertTrue(uuids.contains(UUID.fromString(genericAccess)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcess32BitUUIDs() {
        int[] data = new int[]{ 5, 16, 142, 231, 116, 17, 142, 231, 100 };
        String service1 = "74E78E10-0000-0000-0000-000000000000";
        String service2 = "64E78E11-0000-0000-0000-000000000000";
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_UUID32_COMPLETE, eirRecord.getType());
        List<UUID> uuids = ((List<UUID>) (eirRecord.getRecord()));
        Assert.assertEquals(2, uuids.size());
        Assert.assertTrue(uuids.contains(UUID.fromString(service1)));
        Assert.assertTrue(uuids.contains(UUID.fromString(service2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcess128BitUUIDs() {
        int[] data = new int[]{ 7, 109, 102, 112, 68, 115, 102, 98, 117, 102, 69, 118, 100, 85, 170, 108, 34, 110, 102, 112, 68, 115, 102, 98, 117, 102, 69, 118, 100, 85, 170, 108, 18 };
        String service1 = "226caa55-6476-4566-7562-66734470666d";
        String service2 = "126caa55-6476-4566-7562-66734470666e";
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_UUID128_COMPLETE, eirRecord.getType());
        List<UUID> uuids = ((List<UUID>) (eirRecord.getRecord()));
        Assert.assertEquals(2, uuids.size());
        Assert.assertTrue(uuids.contains(UUID.fromString(service1)));
        Assert.assertTrue(uuids.contains(UUID.fromString(service2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessManufacturerData() {
        int[] data = new int[]{ 255, 255, 2, 0, 255 };
        short siliconLabsID = ((short) (767));
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_MANUFACTURER_SPECIFIC, eirRecord.getType());
        Map<Short, int[]> manufacturerData = ((Map<Short, int[]>) (eirRecord.getRecord()));
        Assert.assertTrue(manufacturerData.containsKey(siliconLabsID));
        Assert.assertArrayEquals(new int[]{ 0, 255 }, manufacturerData.get(siliconLabsID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUUID16ServiceData() {
        int[] data = new int[]{ 22, 15, 24, 69 };
        UUID batteryServiceUUID = UUID.fromString("0000180f-0000-0000-0000-000000000000");
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_DATA_UUID16, eirRecord.getType());
        Map<UUID, int[]> serviceData = ((Map<UUID, int[]>) (eirRecord.getRecord()));
        Assert.assertTrue(serviceData.containsKey(batteryServiceUUID));
        Assert.assertArrayEquals(new int[]{ 69 }, serviceData.get(batteryServiceUUID));
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testUUID32ServiceData() {
        int[] data = new int[]{ 32, 16, 142, 231, 116, 116, 1, 13, 1, ((byte) (236)) };
        UUID dataServiceUUID = UUID.fromString("74E78E10-0000-0000-0000-000000000000");
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_DATA_UUID32, eirRecord.getType());
        Map<UUID, int[]> serviceData = ((Map<UUID, int[]>) (eirRecord.getRecord()));
        Assert.assertTrue(serviceData.containsKey(dataServiceUUID));
        Assert.assertArrayEquals(new int[]{ 116, 1, 13, 1, ((byte) (236)) }, serviceData.get(dataServiceUUID));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUUID128ServiceData() {
        int[] data = new int[]{ 33, 109, 102, 112, 68, 115, 102, 98, 117, 102, 69, 118, 100, 85, 170, 108, 34, 116, 1, 13, 1, ((byte) (236)) };
        UUID dataServiceUUID = UUID.fromString("226caa55-6476-4566-7562-66734470666d");
        EirRecord eirRecord = new EirRecord(data);
        Assert.assertEquals(EIR_SVC_DATA_UUID128, eirRecord.getType());
        Map<UUID, int[]> serviceData = ((Map<UUID, int[]>) (eirRecord.getRecord()));
        Assert.assertTrue(serviceData.containsKey(dataServiceUUID));
        Assert.assertArrayEquals(new int[]{ 116, 1, 13, 1, ((byte) (236)) }, serviceData.get(dataServiceUUID));
    }
}

