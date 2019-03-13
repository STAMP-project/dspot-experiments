/**
 * Copyright (c) 2015, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.device.yearclass;


import DeviceInfo.DEVICEINFO_UNKNOWN;
import YearClass.CLASS_2010;
import YearClass.CLASS_2012;
import YearClass.CLASS_2013;
import YearClass.CLASS_2014;
import YearClass.CLASS_2016;
import YearClass.CLASS_UNKNOWN;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class YearClassTest {
    // CPU, frequency, RAM, and YearClass values from Samsung Galaxy S7 (global edition).
    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testGetYearCategoryG4() {
        // CPU, frequency, RAM, and YearClass values from LG G4.
        int yearClass = getYearClass(6, 1824000, ((2778L * 1024) * 1024));
        Assert.assertEquals(CLASS_2014, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testGetYearCategoryS5() {
        // CPU, frequency, RAM, and YearClass values from Samsung Galaxy S5.
        int yearClass = getYearClass(4, 2457600, 1946939392L);
        Assert.assertEquals(CLASS_2013, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testGetYearCategoryGalaxyJ1() {
        // CPU, frequency, RAM, and YearClass values from Samsung Galaxy J1.
        int yearClass = getYearClass(2, 1248000, ((716L * 1024) * 1024));
        Assert.assertEquals(CLASS_2010, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testGetYearCategoryP8lite() {
        // CPU, frequency, RAM, and YearClass values from Huawei P8lite.
        int yearClass = getYearClass(8, 1200000, ((1858L * 1024) * 1024));
        Assert.assertEquals(CLASS_2013, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testEmptyCase() {
        int yearClass = getYearClass(DEVICEINFO_UNKNOWN, DEVICEINFO_UNKNOWN, DEVICEINFO_UNKNOWN);
        Assert.assertEquals(CLASS_UNKNOWN, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testCoreNums() {
        // Test with only number of cores information available.
        int yearClass = getYearClass(4, DEVICEINFO_UNKNOWN, DEVICEINFO_UNKNOWN);
        Assert.assertEquals(CLASS_2012, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testClockSpeed() {
        // Test with only clock speed information available.
        int yearClass = getYearClass(DEVICEINFO_UNKNOWN, 2457600, DEVICEINFO_UNKNOWN);
        Assert.assertEquals(CLASS_2014, yearClass);
    }

    // Test with only total RAM information available.
    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testGetYearCategoryS7() {
        // CPU, frequency, RAM, and YearClass values from Samsung Galaxy Note8 (USA edition).
        int yearClass = getYearClass(8, 2350000, ((6144L * 1024) * 1024));
        Assert.assertEquals(CLASS_2016, yearClass);
    }

    @PrepareForTest(DeviceInfo.class)
    @Test
    public void testTotalRAM() {
        // Test with only total RAM information available.
        int yearClass = getYearClass(DEVICEINFO_UNKNOWN, DEVICEINFO_UNKNOWN, ((6144L * 1024) * 1024));
        Assert.assertEquals(CLASS_2016, yearClass);
    }
}

