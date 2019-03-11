package org.altbeacon.beacon;


import AltBeacon.CREATOR;
import android.os.Parcel;
import java.util.Arrays;
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
public class AltBeaconTest {
    private Parcel aParcel = null;

    @Test
    public void testRecognizeBeacon() {
        byte[] bytes = AltBeaconTest.hexStringToByteArray("02011a1bff1801beac2f234454cf6d4a0fadf2f4911ba9ffa600010002c509");
        AltBeaconParser parser = new AltBeaconParser();
        Beacon beacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertEquals("manData should be parsed", 9, getMfgReserved());
    }

    @Test
    public void testCanSerializeParcelable() {
        ShadowLog.stream = System.err;
        final Beacon original = new AltBeacon.Builder().setMfgReserved(2).setBluetoothAddress("aa:bb:cc:dd:ee:ff").setBluetoothName("Any Bluetooth").setBeaconTypeCode(1).setExtraDataFields(Arrays.asList(4L, 5L)).setId1("6").setId2("7").setId3("8").setManufacturer(10).setMultiFrameBeacon(true).setParserIdentifier("Any Parser ID").setRssi((-11)).setRunningAverageRssi((-12.3)).setServiceUuid(13).setTxPower(14).build();
        original.setPacketCount(15);
        original.setRssiMeasurementCount(16);
        aParcel = Parcel.obtain();
        original.writeToParcel(aParcel, 0);
        aParcel.setDataPosition(0);
        final AltBeacon parceled = CREATOR.createFromParcel(aParcel);
        MatcherAssert.assertThat(parceled, Matchers.allOf(Matchers.hasProperty("bluetoothAddress", Matchers.equalTo("aa:bb:cc:dd:ee:ff")), Matchers.hasProperty("bluetoothName", Matchers.equalTo("Any Bluetooth")), Matchers.hasProperty("beaconTypeCode", Matchers.equalTo(1)), Matchers.hasProperty("dataFields", Matchers.equalTo(Arrays.asList(2L))), Matchers.hasProperty("extraDataFields", Matchers.equalTo(Arrays.asList(4L, 5L))), Matchers.hasProperty("id1", Matchers.equalTo(Identifier.fromInt(6))), Matchers.hasProperty("id2", Matchers.equalTo(Identifier.fromInt(7))), Matchers.hasProperty("id3", Matchers.equalTo(Identifier.fromInt(8))), Matchers.hasProperty("manufacturer", Matchers.equalTo(10)), Matchers.hasProperty("multiFrameBeacon", Matchers.equalTo(true)), Matchers.hasProperty("parserIdentifier", Matchers.equalTo("Any Parser ID")), Matchers.hasProperty("rssi", Matchers.equalTo((-11))), Matchers.hasProperty("runningAverageRssi", Matchers.equalTo((-12.3))), Matchers.hasProperty("serviceUuid", Matchers.equalTo(13)), Matchers.hasProperty("txPower", Matchers.equalTo(14)), Matchers.hasProperty("mfgReserved", Matchers.equalTo(2)), Matchers.hasProperty("packetCount", Matchers.equalTo(15)), Matchers.hasProperty("measurementCount", Matchers.equalTo(16))));
    }

    @Test
    public void copyingBeaconTransfersAllFields() {
        final Beacon original = new AltBeacon.Builder().setMfgReserved(2).setBluetoothAddress("aa:bb:cc:dd:ee:ff").setBluetoothName("Any Bluetooth").setBeaconTypeCode(1).setExtraDataFields(Arrays.asList(4L, 5L)).setId1("6").setId2("7").setId3("8").setManufacturer(10).setMultiFrameBeacon(true).setParserIdentifier("Any Parser ID").setRssi((-11)).setRunningAverageRssi((-12.3)).setServiceUuid(13).setTxPower(14).build();
        original.setPacketCount(15);
        original.setRssiMeasurementCount(16);
        final AltBeacon copied = new AltBeacon(original);
        MatcherAssert.assertThat(copied, Matchers.allOf(Matchers.hasProperty("bluetoothAddress", Matchers.equalTo("aa:bb:cc:dd:ee:ff")), Matchers.hasProperty("bluetoothName", Matchers.equalTo("Any Bluetooth")), Matchers.hasProperty("beaconTypeCode", Matchers.equalTo(1)), Matchers.hasProperty("dataFields", Matchers.equalTo(Arrays.asList(2L))), Matchers.hasProperty("extraDataFields", Matchers.equalTo(Arrays.asList(4L, 5L))), Matchers.hasProperty("id1", Matchers.equalTo(Identifier.fromInt(6))), Matchers.hasProperty("id2", Matchers.equalTo(Identifier.fromInt(7))), Matchers.hasProperty("id3", Matchers.equalTo(Identifier.fromInt(8))), Matchers.hasProperty("manufacturer", Matchers.equalTo(10)), Matchers.hasProperty("multiFrameBeacon", Matchers.equalTo(true)), Matchers.hasProperty("parserIdentifier", Matchers.equalTo("Any Parser ID")), Matchers.hasProperty("rssi", Matchers.equalTo((-11))), Matchers.hasProperty("runningAverageRssi", Matchers.equalTo((-12.3))), Matchers.hasProperty("serviceUuid", Matchers.equalTo(13)), Matchers.hasProperty("txPower", Matchers.equalTo(14)), Matchers.hasProperty("packetCount", Matchers.equalTo(15)), Matchers.hasProperty("measurementCount", Matchers.equalTo(16))));
    }
}

