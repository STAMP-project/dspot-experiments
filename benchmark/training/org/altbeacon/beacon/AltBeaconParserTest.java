package org.altbeacon.beacon;


import org.altbeacon.beacon.logging.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


/* HOW TO SEE DEBUG LINES FROM YOUR UNIT TESTS:
1. set a line like this at the start of your test:
org.robolectric.shadows.ShadowLog.stream = System.err;
2. run the tests from the command line
3. Look at the test report file in your web browser, e.g.
file:///Users/dyoung/workspace/AndroidProximityLibrary/build/reports/tests/index.html
4. Expand the System.err section
 */
@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class AltBeaconParserTest {
    @Test
    public void testRecognizeBeacon() {
        BeaconManager.setDebug(true);
        byte[] bytes = AltBeaconParserTest.hexStringToByteArray("02011a1bff1801beac2f234454cf6d4a0fadf2f4911ba9ffa600010002c50900");
        AltBeaconParser parser = new AltBeaconParser();
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertEquals("Beacon should have one data field", 1, beacon.getDataFields().size());
        Assert.assertEquals("manData should be parsed", 9, getMfgReserved());
    }

    @Test
    public void testDetectsDaveMHardwareBeacon() {
        ShadowLog.stream = System.err;
        byte[] bytes = AltBeaconParserTest.hexStringToByteArray("02011a1bff1801beac2f234454cf6d4a0fadf2f4911ba9ffa600050003be020e09526164426561636f6e20555342020a0300000000000000000000000000");
        AltBeaconParser parser = new AltBeaconParser();
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("Beacon should be not null if parsed successfully", beacon);
    }

    @Test
    public void testDetectsAlternateBeconType() {
        ShadowLog.stream = System.err;
        byte[] bytes = AltBeaconParserTest.hexStringToByteArray("02011a1bff1801aabb2f234454cf6d4a0fadf2f4911ba9ffa600010002c50900");
        AltBeaconParser parser = new AltBeaconParser();
        parser.setMatchingBeaconTypeCode(43707L);
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("Beacon should be not null if parsed successfully", beacon);
    }

    @Test
    public void testParseWrongFormatReturnsNothing() {
        BeaconManager.setDebug(true);
        ShadowLog.stream = System.err;
        LogManager.d("XXX", "testParseWrongFormatReturnsNothing start");
        byte[] bytes = AltBeaconParserTest.hexStringToByteArray("02011a1aff1801ffff2f234454cf6d4a0fadf2f4911ba9ffa600010002c509");
        AltBeaconParser parser = new AltBeaconParser();
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        LogManager.d("XXX", "testParseWrongFormatReturnsNothing end");
        Assert.assertNull("Beacon should be null if not parsed successfully", beacon);
    }

    @Test
    public void testParsesBeaconMissingDataField() {
        BeaconManager.setDebug(true);
        ShadowLog.stream = System.err;
        byte[] bytes = AltBeaconParserTest.hexStringToByteArray("02011a1aff1801beac2f234454cf6d4a0fadf2f4911ba9ffa600010002c5000000");
        AltBeaconParser parser = new AltBeaconParser();
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertEquals("mRssi should be as passed in", (-55), beacon.getRssi());
        Assert.assertEquals("uuid should be parsed", "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6", beacon.getIdentifier(0).toString());
        Assert.assertEquals("id2 should be parsed", "1", beacon.getIdentifier(1).toString());
        Assert.assertEquals("id3 should be parsed", "2", beacon.getIdentifier(2).toString());
        Assert.assertEquals("txPower should be parsed", (-59), beacon.getTxPower());
        Assert.assertEquals("manufacturer should be parsed", 280, beacon.getManufacturer());
        Assert.assertEquals("missing data field zero should be zero", new Long(0L), beacon.getDataFields().get(0));
    }
}

