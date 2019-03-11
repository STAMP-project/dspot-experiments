package org.altbeacon.beacon;


import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;


/**
 * Created by dyoung on 7/22/14.
 */
@Config(sdk = 28)
public class SBeaconTest {
    @Test
    public void testDetectsSBeacon() {
        ShadowLog.stream = System.err;
        byte[] bytes = SBeaconTest.hexStringToByteArray("02011a1bff1801031501000100c502000000000000000003");
        SBeaconTest.SBeaconParser parser = new SBeaconTest.SBeaconParser();
        SBeaconTest.SBeacon sBeacon = ((SBeaconTest.SBeacon) (parser.fromScanData(bytes, (-55), null)));
        Assert.assertNotNull("SBeacon should be not null if parsed successfully", sBeacon);
        Assert.assertEquals("id should be parsed", "0x000000000003", sBeacon.getId());
        Assert.assertEquals("group should be parsed", 1, sBeacon.getGroup());
        Assert.assertEquals("time should be parsed", 2, sBeacon.getTime());
        Assert.assertEquals("txPower should be parsed", (-59), getTxPower());
    }

    class SBeacon extends Beacon {
        private static final String TAG = "SBeacon";

        private int mTime;

        protected SBeacon(int group, String id, int time, int txPower, int rssi, int beaconTypeCode, String bluetoothAddress) {
            super();
            mTxPower = txPower;
            mRssi = rssi;
            mBeaconTypeCode = beaconTypeCode;
            mBluetoothAddress = bluetoothAddress;
            mIdentifiers = new ArrayList<Identifier>(2);
            mIdentifiers.add(Identifier.fromInt(group));
            mIdentifiers.add(Identifier.parse(id));
            mTime = time;
            // BeaconManager.logDebug(TAG, "constructed a new sbeacon with id2: " + getIdentifier(2));
        }

        public int getGroup() {
            return mIdentifiers.get(0).toInt();
        }

        public int getTime() {
            return mTime;
        }

        public String getId() {
            return mIdentifiers.get(1).toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            // TODO: Implement me
        }

        protected SBeacon(Parcel in) {
            // TODO: Implement me
        }
    }

    class SBeaconParser extends BeaconParser {
        private static final String TAG = "SBeaconParser";

        @Override
        public Beacon fromScanData(byte[] scanData, int rssi, BluetoothDevice device) {
            int startByte = 2;
            while (startByte <= 5) {
                // "m:2-3=0203,i:2-2,i:7-8,i:14-19,d:10-13,p:9-9"
                if (((((int) (scanData[(startByte + 3)])) & 255) == 3) && ((((int) (scanData[(startByte + 4)])) & 255) == 21)) {
                    // BeaconManager.logDebug(TAG, "This is a SBeacon beacon advertisement");
                    // startByte+0 company id (2 bytes)
                    // startByte+2 = 02 (1) byte header
                    // startByte+3 = 0315 (2 bytes) header
                    // startByte+5 = Beacon Type 0x01
                    // startByte+6 = Reserved (1 bytes)
                    // startByte+7 = Security Code (2 bytes) => Major little endian
                    // startByte+9 = Tx Power => Tx Power
                    // startByte+10 = Timestamp (4 bytes) => Minor (2 LSBs) little endian
                    // startByte+14 = Beacon ID (6 bytes) -> UUID little endian
                    int group = (((scanData[(startByte + 8)]) & 255) * 256) + ((scanData[(startByte + 7)]) & 255);
                    int clock = (((((scanData[(startByte + 13)]) & 255) * 16777216) + (((scanData[(startByte + 12)]) & 255) * 65536)) + (((scanData[(startByte + 11)]) & 255) * 256)) + ((scanData[(startByte + 10)]) & 255);
                    int txPower = ((int) (scanData[(startByte + 9)]));// this one is signed

                    byte[] beaconId = new byte[6];
                    System.arraycopy(scanData, (startByte + 14), beaconId, 0, 6);
                    String hexString = bytesToHex(beaconId);
                    StringBuilder sb = new StringBuilder();
                    sb.append(hexString.substring(0, 12));
                    String id = "0x" + (sb.toString());
                    int beaconTypeCode = (((scanData[(startByte + 3)]) & 255) * 256) + ((scanData[(startByte + 2)]) & 255);
                    String mac = null;
                    if (device != null) {
                        mac = device.getAddress();
                    }
                    Beacon beacon = new SBeaconTest.SBeacon(group, id, clock, txPower, rssi, beaconTypeCode, mac);
                    return beacon;
                }
                startByte++;
            } 
            return null;
        }
    }
}

