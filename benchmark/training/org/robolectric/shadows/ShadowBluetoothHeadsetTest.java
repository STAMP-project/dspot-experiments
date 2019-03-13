package org.robolectric.shadows;


import BluetoothProfile.STATE_CONNECTED;
import BluetoothProfile.STATE_DISCONNECTED;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Test for {@link ShadowBluetoothHeadset}
 */
@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothHeadsetTest {
    private BluetoothDevice device1;

    private BluetoothDevice device2;

    private BluetoothHeadset bluetoothHeadset;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getConnectedDevices_defaultsToEmptyList() {
        assertThat(bluetoothHeadset.getConnectedDevices()).isEmpty();
    }

    @Test
    public void getConnectedDevices_canBeSetUpWithAddConnectedDevice() {
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device1);
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device2);
        assertThat(bluetoothHeadset.getConnectedDevices()).containsExactly(device1, device2);
    }

    @Test
    public void getConnectionState_defaultsToDisconnected() {
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device1);
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device2);
        assertThat(bluetoothHeadset.getConnectionState(device1)).isEqualTo(STATE_CONNECTED);
        assertThat(bluetoothHeadset.getConnectionState(device2)).isEqualTo(STATE_CONNECTED);
    }

    @Test
    public void getConnectionState_canBeSetUpWithAddConnectedDevice() {
        assertThat(bluetoothHeadset.getConnectionState(device1)).isEqualTo(STATE_DISCONNECTED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void sendVendorSpecificResultCode_defaultsToTrueForConnectedDevice() {
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device1);
        assertThat(bluetoothHeadset.sendVendorSpecificResultCode(device1, "command", "arg")).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void sendVendorSpecificResultCode_alwaysFalseForDisconnectedDevice() {
        assertThat(bluetoothHeadset.sendVendorSpecificResultCode(device1, "command", "arg")).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void sendVendorSpecificResultCode_canBeForcedToFalseForConnectedDevice() {
        Shadows.shadowOf(bluetoothHeadset).addConnectedDevice(device1);
        Shadows.shadowOf(bluetoothHeadset).setAllowsSendVendorSpecificResultCode(false);
        assertThat(bluetoothHeadset.sendVendorSpecificResultCode(device1, "command", "arg")).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void sendVendorSpecificResultCode_throwsOnNullCommand() {
        try {
            bluetoothHeadset.sendVendorSpecificResultCode(device1, null, "arg");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

