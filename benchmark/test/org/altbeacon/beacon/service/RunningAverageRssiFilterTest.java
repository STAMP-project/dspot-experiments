package org.altbeacon.beacon.service;


import org.altbeacon.beacon.Beacon;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class RunningAverageRssiFilterTest {
    @Test
    public void initTest1() {
        RunningAverageRssiFilter filter = new RunningAverageRssiFilter();
        filter.addMeasurement((-50));
        Assert.assertEquals("First measurement should be -50", String.valueOf(filter.calculateRssi()), "-50.0");
    }

    @Test
    public void rangedBeaconDoesNotOverrideSampleExpirationMillisecondsText() {
        RangedBeacon.setSampleExpirationMilliseconds(20000);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(20000);
        Beacon beacon = new Beacon.Builder().setId1("1").build();
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(33L);
        RangedBeacon rb = new RangedBeacon(beacon);
        Assert.assertEquals("RunningAverageRssiFilter sampleExprirationMilliseconds should not be altered by constructing RangedBeacon", 33L, RunningAverageRssiFilter.getSampleExpirationMilliseconds());
    }

    @Test
    public void regressionCheckRangedBeaconCommitDoesNotOverrideSampleExpirationMilliseconds() {
        RangedBeacon.setSampleExpirationMilliseconds(20000);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(20000);
        Beacon beacon = new Beacon.Builder().setId1("1").build();
        RangedBeacon rb = new RangedBeacon(beacon);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(33L);
        rb.commitMeasurements();
        Assert.assertEquals("RunningAverageRssiFilter sampleExprirationMilliseconds should not be altered by committing RangedBeacon", 33L, RunningAverageRssiFilter.getSampleExpirationMilliseconds());
    }

    @Test
    public void legacySetSampleExpirationMillisecondsWorksText() {
        RangedBeacon.setSampleExpirationMilliseconds(20000);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(20000);
        Beacon beacon = new Beacon.Builder().setId1("1").build();
        RangedBeacon.setSampleExpirationMilliseconds(33L);
        RangedBeacon rb = new RangedBeacon(beacon);
        Assert.assertEquals("RunningAverageRssiFilter sampleExprirationMilliseconds should not be altered by constructing RangedBeacon", 33L, RunningAverageRssiFilter.getSampleExpirationMilliseconds());
    }
}

