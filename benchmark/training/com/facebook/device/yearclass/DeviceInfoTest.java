/**
 * Copyright (c) 2015, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.device.yearclass;


import org.junit.Assert;
import org.junit.Test;


public class DeviceInfoTest {
    @Test
    public void testFileStringValid() {
        Assert.assertEquals(DeviceInfo.getCoresFromFileString("0-3"), 4);
        Assert.assertEquals(DeviceInfo.getCoresFromFileString("0-11"), 12);
    }

    @Test
    public void testFileStringInvalid() {
        Assert.assertEquals(DeviceInfo.getCoresFromFileString("INVALIDSTRING"), (-1));
        Assert.assertEquals(DeviceInfo.getCoresFromFileString("0-2a"), (-1));
        Assert.assertEquals(DeviceInfo.getCoresFromFileString("035"), (-1));
    }
}

