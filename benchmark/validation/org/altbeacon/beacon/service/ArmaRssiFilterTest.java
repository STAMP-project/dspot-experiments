package org.altbeacon.beacon.service;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ArmaRssiFilterTest {
    @Test
    public void initTest1() {
        ArmaRssiFilter filter = new ArmaRssiFilter();
        filter.addMeasurement((-50));
        Assert.assertEquals("First measurement should be -50", String.valueOf(filter.calculateRssi()), "-50.0");
    }
}

