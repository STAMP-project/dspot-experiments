package org.altbeacon.beacon.utils;


import java.net.MalformedURLException;
import java.util.ArrayList;
import org.altbeacon.beacon.Beacon;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class EddystoneTelemetryAccessorTest {
    @Test
    public void testAllowsAccessToTelemetryBytes() throws MalformedURLException {
        ArrayList<Long> telemetryFields = new ArrayList<Long>();
        telemetryFields.add(1L);// version

        telemetryFields.add(530L);// battery level

        telemetryFields.add(787L);// temperature

        telemetryFields.add(68428852L);// pdu count

        telemetryFields.add(85271861L);// uptime

        Beacon beaconWithTelemetry = new Beacon.Builder().setId1("0x0102030405060708090a").setId2("0x01020304050607").setTxPower((-59)).setExtraDataFields(telemetryFields).build();
        byte[] telemetryBytes = new EddystoneTelemetryAccessor().getTelemetryBytes(beaconWithTelemetry);
        byte[] expectedBytes = new byte[]{ 32, 1, 2, 18, 3, 19, 4, 20, 36, 52, 5, 21, 37, 53 };
        Assert.assertEquals(EddystoneTelemetryAccessorTest.byteArrayToHexString(telemetryBytes), EddystoneTelemetryAccessorTest.byteArrayToHexString(expectedBytes));
    }

    @Test
    public void testAllowsAccessToBase64EncodedTelemetryBytes() throws MalformedURLException {
        ArrayList<Long> telemetryFields = new ArrayList<Long>();
        telemetryFields.add(1L);// version

        telemetryFields.add(530L);// battery level

        telemetryFields.add(787L);// temperature

        telemetryFields.add(68428852L);// pdu count

        telemetryFields.add(85271861L);// uptime

        Beacon beaconWithTelemetry = new Beacon.Builder().setId1("0x0102030405060708090a").setId2("0x01020304050607").setTxPower((-59)).setExtraDataFields(telemetryFields).build();
        byte[] telemetryBytes = new EddystoneTelemetryAccessor().getTelemetryBytes(beaconWithTelemetry);
        String encodedTelemetryBytes = new EddystoneTelemetryAccessor().getBase64EncodedTelemetry(beaconWithTelemetry);
        Assert.assertNotNull(telemetryBytes);
    }
}

