package org.altbeacon.beacon;


import BeaconParser.EDDYSTONE_UID_LAYOUT;
import android.content.Context;
import java.util.Arrays;
import junit.framework.Assert;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


/**
 * Created by dyoung on 2/6/15.
 * This test verifies that a beacon can be parsed that uses a Gatt UUID
 */
@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class GattBeaconTest {
    @Test
    public void testDetectsGattBeacon() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        System.err.println(("verbose logging:" + (LogManager.isVerboseLoggingEnabled())));
        byte[] bytes = GattBeaconTest.hexStringToByteArray("020106030334121516341200e72f234454f4911ba9ffa6000000000001000000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=1234,m:2-2=00,p:3-3:-41,i:4-13,i:14-19");
        Assert.assertNotNull("Service uuid parsed should not be null", parser.getServiceUuid());
        Beacon gattBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("GattBeacon should be not null if parsed successfully", gattBeacon);
        Assert.assertEquals("id1 should be parsed", "0x2f234454f4911ba9ffa6", gattBeacon.getId1().toString());
        Assert.assertEquals("id2 should be parsed", "0x000000000001", gattBeacon.getId2().toString());
        Assert.assertEquals("serviceUuid should be parsed", 4660, gattBeacon.getServiceUuid());
        Assert.assertEquals("txPower should be parsed", (-66), gattBeacon.getTxPower());
    }

    @Test
    public void testDetectsGattBeacon2MaxLength() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        byte[] bytes = GattBeaconTest.hexStringToByteArray("020106030334121616341210ec007261646975736e6574776f726b7373070000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=1234,m:2-2=10,p:3-3:-41,i:4-20v");
        Beacon gattBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("GattBeacon should be not null if parsed successfully", gattBeacon);
        Assert.assertEquals("GattBeacon identifier length should be proper length", 17, gattBeacon.getId1().toByteArray().length);
    }

    @Test
    public void testDetectsGattBeacon2WithShortIdentifier() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        LogManager.d("GattBeaconTest", "Parsing short packet");
        byte[] bytes = GattBeaconTest.hexStringToByteArray("020106030334121516341210ec007261646975736e6574776f726b7307000000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=1234,m:2-2=10,p:3-3:-41,i:4-20v");
        Beacon gattBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("GattBeacon should be not null if parsed successfully", gattBeacon);
        Assert.assertEquals("GattBeacon identifier length should be adjusted smaller if packet is short", 16, gattBeacon.getId1().toByteArray().length);
        Assert.assertEquals("GattBeacon identifier should have proper first byte", ((byte) (0)), gattBeacon.getId1().toByteArray()[0]);
        Assert.assertEquals("GattBeacon identifier should have proper second to last byte", ((byte) (115)), gattBeacon.getId1().toByteArray()[14]);
        Assert.assertEquals("GattBeacon identifier should have proper last byte", ((byte) (7)), gattBeacon.getId1().toByteArray()[15]);
    }

    @Test
    public void testDetectsEddystoneUID() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        byte[] bytes = GattBeaconTest.hexStringToByteArray("0201060303aafe1516aafe00e700010203040506070809010203040506000000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout(EDDYSTONE_UID_LAYOUT);
        Beacon eddystoneUidBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("Eddystone-UID should be not null if parsed successfully", eddystoneUidBeacon);
    }

    @Test
    public void testDetectsGattBeaconWithCnn() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        LogManager.d("GattBeaconTest", "Parsing short packet");
        byte[] bytes = GattBeaconTest.hexStringToByteArray("020106030334120a16341210ed00636e6e070000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=1234,m:2-2=10,p:3-3:-41,i:4-20v");
        LogManager.d("xxx", "------");
        Beacon gattBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("GattBeacon should be not null if parsed successfully", gattBeacon);
        Assert.assertEquals("GattBeacon identifier length should be adjusted smaller if packet is short", 5, gattBeacon.getId1().toByteArray().length);
    }

    @Test
    public void testBeaconAdvertisingBytes() {
        ShadowLog.stream = System.err;
        Context context = RuntimeEnvironment.application;
        Beacon beacon = new Beacon.Builder().setId1("0x454452e29735323d81c0").setId2("0x060504030201").setDataFields(Arrays.asList(37L)).setTxPower((-59)).build();
        // TODO: need to use something other than the d: prefix here for an internally generated field
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("s:0-1=0123,m:2-2=00,d:3-3,p:4-4,i:5-14,i:15-20");
        byte[] data = beaconParser.getBeaconAdvertisementData(beacon);
        // BeaconTransmitter beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        // TODO: can't actually start transmitter here because Robolectric does not support API 21
        Assert.assertEquals("Data should be 19 bytes long", 19, data.length);
        String byteString = "";
        for (int i = 0; i < (data.length); i++) {
            byteString += String.format("%02X", data[i]);
            byteString += " ";
        }
        Assert.assertEquals("Advertisement bytes should be as expected", "00 25 C5 45 44 52 E2 97 35 32 3D 81 C0 06 05 04 03 02 01 ", byteString);
    }

    @Test
    public void testDetectsUriBeacon() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        // "https://goo.gl/hqBXE1"
        byte[] bytes = new byte[]{ 2, 1, 4, 3, 3, ((byte) (216)), ((byte) (254)), 19, 22, ((byte) (216)), ((byte) (254)), 0, ((byte) (242)), 3, 103, 111, 111, 46, 103, 108, 47, 104, 113, 66, 88, 69, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=fed8,m:2-2=00,p:3-3:-41,i:4-21v");
        LogManager.d("xxx", "------");
        Beacon uriBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNotNull("UriBeacon should be not null if parsed successfully", uriBeacon);
        Assert.assertEquals("UriBeacon identifier length should be correct", 14, uriBeacon.getId1().toByteArray().length);
        String urlString = UrlBeaconUrlCompressor.uncompress(uriBeacon.getId1().toByteArray());
        Assert.assertEquals("URL should be decompressed successfully", "https://goo.gl/hqBXE1", urlString);
    }

    @Test
    public void doesNotCrashOnMalformedEddystoneBeacon() {
        ShadowLog.stream = System.err;
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        LogManager.d("GattBeaconTest", "Parsing malformed packet");
        byte[] bytes = GattBeaconTest.hexStringToByteArray("0201060303aafe0416aafe100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        BeaconParser parser = new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v");
        LogManager.d("xxx", "------");
        Beacon gattBeacon = parser.fromScanData(bytes, (-55), null);
        Assert.assertNull("GattBeacon should be null when not parsed successfully", gattBeacon);
    }
}

