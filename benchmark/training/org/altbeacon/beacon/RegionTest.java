package org.altbeacon.beacon;


import java.util.ArrayList;
import java.util.Collections;
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
public class RegionTest {
    @Test
    public void testBeaconMatchesRegionWithSameIdentifiers() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), Identifier.parse("3"));
        Assert.assertTrue("Beacon should match region with all identifiers the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithSameIdentifier1() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Identifier.parse("1"), null, null);
        Assert.assertTrue("Beacon should match region with first identifier the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithSameIdentifier1And2() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), null);
        Assert.assertTrue("Beacon should match region with first two identifiers the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithDifferentIdentifier1() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Identifier.parse("22222"), null, null);
        Assert.assertTrue("Beacon should not match region with first identifier different", (!(region.matchesBeacon(beacon))));
    }

    @Test
    public void testBeaconMatchesRegionWithShorterIdentifierList() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Collections.singletonList(Identifier.parse("1")));
        Assert.assertTrue("Beacon should match region with first identifier equal and shorter Identifier list", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithSingleNullIdentifierList() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        ArrayList<Identifier> identifiers = new ArrayList<>();
        identifiers.add(null);
        Region region = new Region("all-beacons-region", identifiers);
        Assert.assertTrue("Beacon should match region with first identifier null and shorter Identifier list", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconDoesntMatchRegionWithLongerIdentifierList() {
        Beacon beacon = new Beacon.Builder().setId1("1").setId2("2").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), Identifier.parse("3"));
        Assert.assertFalse("Beacon should not match region with more identifers than the beacon", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconDoesMatchRegionWithLongerIdentifierListWithSomeNull() {
        Beacon beacon = new Beacon.Builder().setId1("1").setId2("2").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Region region = new Region("myRegion", null, null, null);
        Assert.assertTrue("Beacon should match region with more identifers than the beacon, if the region identifiers are null", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithSameBluetoothMac() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("01:02:03:04:05:06").build();
        Region region = new Region("myRegion", "01:02:03:04:05:06");
        Assert.assertTrue("Beacon should match region with mac the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconDoesNotMatchRegionWithDiffrentBluetoothMac() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("01:02:03:04:05:06").build();
        Region region = new Region("myRegion", "01:02:03:04:05:99");
        Assert.assertFalse("Beacon should match region with mac the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testBeaconMatchesRegionWithSameBluetoothMacAndIdentifiers() {
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("01:02:03:04:05:06").build();
        ArrayList identifiers = new ArrayList<Identifier>();
        identifiers.add(Identifier.parse("1"));
        identifiers.add(Identifier.parse("2"));
        identifiers.add(Identifier.parse("3"));
        Region region = new Region("myRegion", identifiers, "01:02:03:04:05:06");
        Assert.assertTrue("Beacon should match region with mac the same", region.matchesBeacon(beacon));
    }

    @Test
    public void testCanSerialize() throws Exception {
        ShadowLog.stream = System.err;
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), null);
        byte[] serializedRegion = convertToBytes(region);
        Region region2 = ((Region) (convertFromBytes(serializedRegion)));
        Assert.assertEquals("Right number of identifiers after deserialization", 3, region2.mIdentifiers.size());
        Assert.assertEquals("uniqueId is same after deserialization", region.getUniqueId(), region2.getUniqueId());
        Assert.assertEquals("id1 is same after deserialization", region.getIdentifier(0), region2.getIdentifier(0));
        Assert.assertEquals("id2 is same after deserialization", region.getIdentifier(1), region2.getIdentifier(1));
        Assert.assertNull("id3 is null before deserialization", region.getIdentifier(2));
        Assert.assertNull("id3 is null after deserialization", region2.getIdentifier(2));
    }

    @Test
    public void testCanSerializeWithMac() throws Exception {
        ShadowLog.stream = System.err;
        Region region = new Region("myRegion", "1B:2a:03:4C:6E:9F");
        byte[] serializedRegion = convertToBytes(region);
        Region region2 = ((Region) (convertFromBytes(serializedRegion)));
        Assert.assertEquals("Right number of identifiers after deserialization", 0, region2.mIdentifiers.size());
        Assert.assertEquals("ac is same after deserialization", region.getBluetoothAddress(), region2.getBluetoothAddress());
    }

    @Test
    public void rejectsInvalidMac() {
        ShadowLog.stream = System.err;
        try {
            Region region = new Region("myRegion", "this string is not a valid mac address!");
            Assert.assertTrue("IllegalArgumentException should have been thrown", false);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Error message should be as expected", "Invalid mac address: 'this string is not a valid mac address!' Must be 6 hex bytes separated by colons.", e.getMessage());
        }
    }

    @Test
    public void testToString() {
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), null);
        Assert.assertEquals("id1: 1 id2: 2 id3: null", region.toString());
    }

    @Test
    public void testConvenienceIdentifierAccessors() {
        Region region = new Region("myRegion", Identifier.parse("1"), Identifier.parse("2"), Identifier.parse("3"));
        Assert.assertEquals("1", region.getId1().toString());
        Assert.assertEquals("2", region.getId2().toString());
        Assert.assertEquals("3", region.getId3().toString());
    }
}

