package org.altbeacon.beacon;


import android.content.Context;
import android.util.Log;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


/**
 * Created by dyoung on 7/22/14.
 */
@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class BeaconTransmitterTest {
    private static final String TAG = "BeaconTransmitterTest";

    @Test
    public void testBeaconAdvertisingBytes() {
        ShadowLog.stream = System.err;
        Context context = RuntimeEnvironment.application;
        Beacon beacon = new Beacon.Builder().setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6").setId2("1").setId3("2").setManufacturer(280).setTxPower((-59)).setDataFields(Arrays.asList(0L)).build();
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        byte[] data = beaconParser.getBeaconAdvertisementData(beacon);
        // BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        // TODO: can't actually start transmitter here because Robolectric does not support API 21
        Assert.assertEquals("Data should be 24 bytes long", 24, data.length);
        String byteString = "";
        for (int i = 0; i < (data.length); i++) {
            byteString += String.format("%02X", data[i]);
            byteString += " ";
        }
        Assert.assertEquals("Advertisement bytes should be as expected", "BE AC 2F 23 44 54 CF 6D 4A 0F AD F2 F4 91 1B A9 FF A6 00 01 00 02 C5 00 ", byteString);
    }

    @Test
    public void testBeaconAdvertisingBytesForEddystone() {
        ShadowLog.stream = System.err;
        Context context = RuntimeEnvironment.application;
        Beacon beacon = new Beacon.Builder().setId1("0x2f234454f4911ba9ffa6").setId2("0x000000000001").setManufacturer(280).setTxPower((-59)).build();
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19");
        byte[] data = beaconParser.getBeaconAdvertisementData(beacon);
        // BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        // TODO: can't actually start transmitter here because Robolectric does not support API 21
        String byteString = "";
        for (int i = 0; i < (data.length); i++) {
            byteString += String.format("%02X", data[i]);
            byteString += " ";
        }
        Log.d(BeaconTransmitterTest.TAG, ("Advertising bytes are " + byteString));
        Assert.assertEquals("Data should be 24 bytes long", 18, data.length);
        Assert.assertEquals("Advertisement bytes should be as expected", "00 C5 2F 23 44 54 F4 91 1B A9 FF A6 00 00 00 00 00 01 ", byteString);
    }
}

