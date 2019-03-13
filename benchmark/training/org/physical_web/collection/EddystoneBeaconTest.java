/**
 * Copyright 2016 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.physical_web.collection;


import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the EddystoneBeacon class.
 */
public class EddystoneBeaconTest {
    @Test
    public void getFatBeaconTitleTest() throws UnsupportedEncodingException {
        // Array length failure
        Assert.assertEquals("", EddystoneBeacon.getFatBeaconTitle(new byte[]{  }));
        Assert.assertEquals("", EddystoneBeacon.getFatBeaconTitle(new byte[]{ 1 }));
        Assert.assertEquals("", EddystoneBeacon.getFatBeaconTitle(new byte[]{ 1, 2 }));
        // Invalid byte sequence
        Assert.assertEquals("", EddystoneBeacon.getFatBeaconTitle(new byte[]{ 1, 2, 0 }));
        // Valid title
        String title = "title";
        byte[] titleBytes = title.getBytes("UTF-8");
        int length = titleBytes.length;
        byte[] serviceData = new byte[length + 3];
        System.arraycopy(titleBytes, 0, serviceData, 3, length);
        serviceData[0] = 16;
        serviceData[1] = 0;
        serviceData[2] = 14;
        Assert.assertEquals(title, EddystoneBeacon.getFatBeaconTitle(serviceData));
    }

    @Test
    public void isFatBeaconTest() {
        // Array length failure
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(null));
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{  }));
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{ 1 }));
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{ 1, 2 }));
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{ 1, 2, 3 }));
        // Not URL Type failure
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{ 1, 2, 14, 4 }));
        // Doesn't start with title type
        Assert.assertFalse(EddystoneBeacon.isFatBeacon(new byte[]{ 16, 2, 3, 4 }));
        // Fat beacon
        Assert.assertTrue(EddystoneBeacon.isFatBeacon(new byte[]{ 16, 2, 14, 4 }));
    }

    @Test
    public void isUrlFrameTest() {
        // Array length failure
        Assert.assertFalse(EddystoneBeacon.isUrlFrame(null));
        Assert.assertFalse(EddystoneBeacon.isUrlFrame(new byte[]{  }));
        // Not URL frame type
        Assert.assertFalse(EddystoneBeacon.isUrlFrame(new byte[]{ 32 }));
        // URL frame type with various flags
        Assert.assertTrue(EddystoneBeacon.isUrlFrame(new byte[]{ 16 }));
        Assert.assertTrue(EddystoneBeacon.isUrlFrame(new byte[]{ 17 }));
        Assert.assertTrue(EddystoneBeacon.isUrlFrame(new byte[]{ 24 }));
        Assert.assertTrue(EddystoneBeacon.isUrlFrame(new byte[]{ 26 }));
        Assert.assertTrue(EddystoneBeacon.isUrlFrame(new byte[]{ 31 }));
    }

    @Test
    public void parseFromServiceDataTest() {
        // Array length failure
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(null, null));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(new byte[]{  }, null));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(new byte[]{ 1 }, null));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(new byte[]{ 1, 2 }, null));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(null, new byte[]{  }));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(null, new byte[]{ 1 }));
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(null, new byte[]{ 1, 2 }));
        // Invalid URL
        Assert.assertNull(EddystoneBeacon.parseFromServiceData(new byte[]{ 16, 0, 79 }, null));
        // Valid URL
        EddystoneBeacon beacon = EddystoneBeacon.parseFromServiceData(new byte[]{ 16, 0, 1 }, null);
        Assert.assertEquals("https://www.", beacon.getUrl());
        Assert.assertEquals(0, beacon.getFlags());
        Assert.assertEquals(0, beacon.getTxPowerLevel());
    }
}

