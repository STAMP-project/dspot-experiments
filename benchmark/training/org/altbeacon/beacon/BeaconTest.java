package org.altbeacon.beacon;


import Beacon.CREATOR;
import android.os.Parcel;
import java.util.Arrays;
import java.util.List;
import org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
public class BeaconTest {
    private Parcel aParcel = null;

    @Test
    public void testAccessBeaconIdentifiers() {
        Beacon beacon = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertEquals("First beacon id should be 1", beacon.getIdentifier(0).toString(), "1");
        Assert.assertEquals("Second beacon id should be 1", beacon.getIdentifier(1).toString(), "2");
        Assert.assertEquals("Third beacon id should be 1", beacon.getIdentifier(2).toString(), "3");
        Assert.assertEquals("First beacon id should be 1", beacon.getId1().toString(), "1");
        Assert.assertEquals("Second beacon id should be 1", beacon.getId2().toString(), "2");
        Assert.assertEquals("Third beacon id should be 1", beacon.getId3().toString(), "3");
    }

    @Test
    public void testBeaconsWithSameIdentifersAreEqual() {
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertEquals("Beacons with same identifiers are equal", beacon1, beacon2);
    }

    @Test
    public void testBeaconsWithDifferentId1AreNotEqual() {
        ShadowLog.stream = System.err;
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("11").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertTrue("Beacons with different id1 are not equal", (!(beacon1.equals(beacon2))));
    }

    @Test
    public void testBeaconsWithDifferentId2AreNotEqual() {
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("12").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertTrue("Beacons with different id2 are not equal", (!(beacon1.equals(beacon2))));
    }

    @Test
    public void testBeaconsWithDifferentId3AreNotEqual() {
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("13").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertTrue("Beacons with different id3 are not equal", (!(beacon1.equals(beacon2))));
    }

    @Test
    public void testBeaconsWithSameMacsAreEqual() {
        Beacon.setHardwareEqualityEnforced(true);
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Assert.assertTrue("Beacons with same same macs are equal", beacon1.equals(beacon2));
    }

    @Test
    public void testBeaconsWithDifferentMacsAreNotEqual() {
        Beacon.setHardwareEqualityEnforced(true);
        Beacon beacon1 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:6").build();
        Beacon beacon2 = new AltBeacon.Builder().setMfgReserved(7).setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothAddress("1:2:3:4:5:666666").build();
        Assert.assertTrue("Beacons with different same macs are not equal", (!(beacon1.equals(beacon2))));
    }

    @Test
    public void testCalculateAccuracyWithRssiEqualsPower() {
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(null, null));
        double accuracy = Beacon.calculateDistance((-55), (-55));
        Assert.assertEquals("Distance should be one meter if mRssi is the same as power", 1.0, accuracy, 0.1);
    }

    @Test
    public void testCalculateAccuracyWithRssiGreaterThanPower() {
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(null, null));
        double accuracy = Beacon.calculateDistance((-55), (-50));
        Assert.assertTrue(("Distance should be under one meter if mRssi is less negative than power.  Accuracy was " + accuracy), (accuracy < 1.0));
    }

    @Test
    public void testCalculateAccuracyWithRssiLessThanPower() {
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(null, null));
        double accuracy = Beacon.calculateDistance((-55), (-60));
        Assert.assertTrue(("Distance should be over one meter if mRssi is less negative than power. Accuracy was " + accuracy), (accuracy > 1.0));
    }

    @Test
    public void testCalculateAccuracyWithRssiEqualsPowerOnInternalProperties() {
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(null, null));
        Beacon beacon = new Beacon.Builder().setTxPower((-55)).setRssi((-55)).build();
        double distance = beacon.getDistance();
        Assert.assertEquals("Distance should be one meter if mRssi is the same as power", 1.0, distance, 0.1);
    }

    @Test
    public void testCalculateAccuracyWithRssiEqualsPowerOnInternalPropertiesAndRunningAverage() {
        Beacon beacon = new Beacon.Builder().setTxPower((-55)).setRssi(0).build();
        beacon.setRunningAverageRssi((-55));
        double distance = beacon.getDistance();
        Assert.assertEquals("Distance should be one meter if mRssi is the same as power", 1.0, distance, 0.1);
    }

    @Test
    public void testCanSerialize() throws Exception {
        ShadowLog.stream = System.err;
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothName("xx").setBluetoothAddress("1:2:3:4:5:6").setDataFields(Arrays.asList(100L)).build();
        byte[] serializedBeacon = convertToBytes(beacon);
        Beacon beacon2 = ((Beacon) (convertFromBytes(serializedBeacon)));
        Assert.assertEquals("Right number of identifiers after deserialization", 3, beacon2.mIdentifiers.size());
        Assert.assertEquals("id1 is same after deserialization", beacon.getIdentifier(0), beacon2.getIdentifier(0));
        Assert.assertEquals("id2 is same after deserialization", beacon.getIdentifier(1), beacon2.getIdentifier(1));
        Assert.assertEquals("id3 is same after deserialization", beacon.getIdentifier(2), beacon2.getIdentifier(2));
        Assert.assertEquals("txPower is same after deserialization", beacon.getTxPower(), beacon2.getTxPower());
        Assert.assertEquals("rssi is same after deserialization", beacon.getRssi(), beacon2.getRssi());
        Assert.assertEquals("distance is same after deserialization", beacon.getDistance(), beacon2.getDistance(), 0.001);
        Assert.assertEquals("bluetoothAddress is same after deserialization", beacon.getBluetoothAddress(), beacon2.getBluetoothAddress());
        Assert.assertEquals("bluetoothAddress is same after deserialization", beacon.getBluetoothName(), beacon2.getBluetoothName());
        Assert.assertEquals("beaconTypeCode is same after deserialization", beacon.getBeaconTypeCode(), beacon2.getBeaconTypeCode());
        Assert.assertEquals("manufacturer is same after deserialization", beacon.getManufacturer(), beacon2.getManufacturer());
        Assert.assertEquals("data field 0 is the same after deserialization", beacon.getDataFields().get(0), beacon2.getDataFields().get(0));
        Assert.assertEquals("data field 0 is the right value", beacon.getDataFields().get(0), ((Long) (100L)));
    }

    @Test
    public void noDoubleWrappingOfExtraDataFields() {
        ShadowLog.stream = System.err;
        Beacon beacon = new AltBeacon.Builder().setId1("1").setId2("2").setId3("3").setRssi(4).setBeaconTypeCode(5).setTxPower(6).setBluetoothName("xx").setBluetoothAddress("1:2:3:4:5:6").setDataFields(Arrays.asList(100L)).build();
        List<Long> list = beacon.getExtraDataFields();
        beacon.setExtraDataFields(list);
        Assert.assertTrue("getter should return same object after first wrap ", ((beacon.getExtraDataFields()) == list));
    }

    @Test
    public void testHashCodeWithNullIdentifier() {
        Beacon beacon = new AltBeacon.Builder().setIdentifiers(Arrays.asList(Identifier.parse("0x1234"), null)).build();
        Assert.assertTrue("hashCode() should not throw exception", ((beacon.hashCode()) >= (Integer.MIN_VALUE)));
    }

    @Test
    public void parcelingBeaconContainsAllFields() {
        final Beacon original = new Beacon.Builder().setBluetoothAddress("aa:bb:cc:dd:ee:ff").setBluetoothName("Any Bluetooth").setBeaconTypeCode(1).setDataFields(Arrays.asList(2L, 3L)).setExtraDataFields(Arrays.asList(4L, 5L)).setId1("6").setId2("7").setId3("8").setManufacturer(10).setMultiFrameBeacon(true).setParserIdentifier("Any Parser ID").setRssi((-11)).setRunningAverageRssi((-12.3)).setServiceUuid(13).setTxPower(14).build();
        original.setPacketCount(15);
        original.setRssiMeasurementCount(16);
        aParcel = Parcel.obtain();
        original.writeToParcel(aParcel, 0);
        aParcel.setDataPosition(0);
        final Beacon parceled = CREATOR.createFromParcel(aParcel);
        MatcherAssert.assertThat(parceled, Matchers.allOf(Matchers.hasProperty("bluetoothAddress", Matchers.equalTo("aa:bb:cc:dd:ee:ff")), Matchers.hasProperty("bluetoothName", Matchers.equalTo("Any Bluetooth")), Matchers.hasProperty("beaconTypeCode", Matchers.equalTo(1)), Matchers.hasProperty("dataFields", Matchers.equalTo(Arrays.asList(2L, 3L))), Matchers.hasProperty("extraDataFields", Matchers.equalTo(Arrays.asList(4L, 5L))), Matchers.hasProperty("id1", Matchers.equalTo(Identifier.fromInt(6))), Matchers.hasProperty("id2", Matchers.equalTo(Identifier.fromInt(7))), Matchers.hasProperty("id3", Matchers.equalTo(Identifier.fromInt(8))), Matchers.hasProperty("manufacturer", Matchers.equalTo(10)), Matchers.hasProperty("multiFrameBeacon", Matchers.equalTo(true)), Matchers.hasProperty("parserIdentifier", Matchers.equalTo("Any Parser ID")), Matchers.hasProperty("rssi", Matchers.equalTo((-11))), Matchers.hasProperty("runningAverageRssi", Matchers.equalTo((-12.3))), Matchers.hasProperty("serviceUuid", Matchers.equalTo(13)), Matchers.hasProperty("txPower", Matchers.equalTo(14)), Matchers.hasProperty("packetCount", Matchers.equalTo(15)), Matchers.hasProperty("measurementCount", Matchers.equalTo(16))));
    }

    @Test
    public void copyingBeaconContainsAllFields() {
        final Beacon original = new Beacon.Builder().setBluetoothAddress("aa:bb:cc:dd:ee:ff").setBluetoothName("Any Bluetooth").setBeaconTypeCode(1).setDataFields(Arrays.asList(2L, 3L)).setExtraDataFields(Arrays.asList(4L, 5L)).setId1("6").setId2("7").setId3("8").setManufacturer(10).setMultiFrameBeacon(true).setParserIdentifier("Any Parser ID").setRssi((-11)).setRunningAverageRssi((-12.3)).setServiceUuid(13).setTxPower(14).build();
        original.setPacketCount(15);
        original.setRssiMeasurementCount(16);
        final Beacon copied = new Beacon(original);
        MatcherAssert.assertThat(copied, Matchers.allOf(Matchers.hasProperty("bluetoothAddress", Matchers.equalTo("aa:bb:cc:dd:ee:ff")), Matchers.hasProperty("bluetoothName", Matchers.equalTo("Any Bluetooth")), Matchers.hasProperty("beaconTypeCode", Matchers.equalTo(1)), Matchers.hasProperty("dataFields", Matchers.equalTo(Arrays.asList(2L, 3L))), Matchers.hasProperty("extraDataFields", Matchers.equalTo(Arrays.asList(4L, 5L))), Matchers.hasProperty("id1", Matchers.equalTo(Identifier.fromInt(6))), Matchers.hasProperty("id2", Matchers.equalTo(Identifier.fromInt(7))), Matchers.hasProperty("id3", Matchers.equalTo(Identifier.fromInt(8))), Matchers.hasProperty("manufacturer", Matchers.equalTo(10)), Matchers.hasProperty("multiFrameBeacon", Matchers.equalTo(true)), Matchers.hasProperty("parserIdentifier", Matchers.equalTo("Any Parser ID")), Matchers.hasProperty("rssi", Matchers.equalTo((-11))), Matchers.hasProperty("runningAverageRssi", Matchers.equalTo((-12.3))), Matchers.hasProperty("serviceUuid", Matchers.equalTo(13)), Matchers.hasProperty("txPower", Matchers.equalTo(14)), Matchers.hasProperty("packetCount", Matchers.equalTo(15)), Matchers.hasProperty("measurementCount", Matchers.equalTo(16))));
    }
}

