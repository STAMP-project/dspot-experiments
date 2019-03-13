package org.altbeacon.beacon.service.scanner;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class DistinctPacketDetectorTest {
    @Test
    public void testSecondDuplicatePacketIsNotDistinct() throws Exception {
        DistinctPacketDetector dpd = new DistinctPacketDetector();
        dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        boolean secondResult = dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        Assert.assertFalse("second call with same packet should not be distinct", secondResult);
    }

    @Test
    public void testSecondNonDuplicatePacketIsDistinct() throws Exception {
        DistinctPacketDetector dpd = new DistinctPacketDetector();
        dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        boolean secondResult = dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 3, 4 });
        Assert.assertTrue("second call with different packet should be distinct", secondResult);
    }

    @Test
    public void testSamePacketForDifferentMacIsDistinct() throws Exception {
        DistinctPacketDetector dpd = new DistinctPacketDetector();
        dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        boolean secondResult = dpd.isPacketDistinct("01:01:01:01:01:01", new byte[]{ 1, 2 });
        Assert.assertTrue("second packet with different mac should be distinct", secondResult);
    }

    @Test
    public void clearingDetectionsPreventsDistinctDetection() throws Exception {
        DistinctPacketDetector dpd = new DistinctPacketDetector();
        dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        dpd.clearDetections();
        boolean secondResult = dpd.isPacketDistinct("01:02:03:04:05:06", new byte[]{ 1, 2 });
        Assert.assertTrue("second call with same packet after clear should be distinct", secondResult);
    }
}

