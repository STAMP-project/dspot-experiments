package org.robolectric.shadows;


import BluetoothDevice.PHY_LE_1M_MASK;
import BluetoothDevice.TRANSPORT_LE;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.ParcelUuid;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothDeviceTest {
    private static final String MOCK_MAC_ADDRESS = "00:11:22:33:AA:BB";

    @Test
    public void canCreateBluetoothDeviceViaNewInstance() throws Exception {
        // This test passes as long as no Exception is thrown. It tests if the constructor can be
        // executed without throwing an Exception when getService() is called inside.
        BluetoothDevice bluetoothDevice = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(bluetoothDevice).isNotNull();
    }

    @Test
    public void canSetAndGetUuids() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        ParcelUuid[] uuids = new ParcelUuid[]{ ParcelUuid.fromString("00000000-1111-2222-3333-000000000011"), ParcelUuid.fromString("00000000-1111-2222-3333-0000000000aa") };
        Shadows.shadowOf(device).setUuids(uuids);
        assertThat(device.getUuids()).isEqualTo(uuids);
    }

    @Test
    public void getUuids_setUuidsNotCalled_shouldReturnNull() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(device.getUuids()).isNull();
    }

    @Test
    public void canSetAndGetBondState() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(device.getBondState()).isEqualTo(BOND_NONE);
        Shadows.shadowOf(device).setBondState(BOND_BONDED);
        assertThat(device.getBondState()).isEqualTo(BOND_BONDED);
    }

    @Test
    public void canSetAndGetCreatedBond() {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(device.createBond()).isFalse();
        Shadows.shadowOf(device).setCreatedBond(true);
        assertThat(device.createBond()).isTrue();
    }

    @Test
    public void canSetAndGetFetchUuidsWithSdpResult() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(device.fetchUuidsWithSdp()).isFalse();
        Shadows.shadowOf(device).setFetchUuidsWithSdpResult(true);
        assertThat(device.fetchUuidsWithSdp()).isTrue();
    }

    @Test
    public void getCorrectFetchUuidsWithSdpCount() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(Shadows.shadowOf(device).getFetchUuidsWithSdpCount()).isEqualTo(0);
        device.fetchUuidsWithSdp();
        assertThat(Shadows.shadowOf(device).getFetchUuidsWithSdpCount()).isEqualTo(1);
        device.fetchUuidsWithSdp();
        assertThat(Shadows.shadowOf(device).getFetchUuidsWithSdpCount()).isEqualTo(2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void connectGatt_doesntCrash() throws Exception {
        BluetoothDevice bluetoothDevice = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(bluetoothDevice.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {})).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void connectGatt_withTransport_doesntCrash() throws Exception {
        BluetoothDevice bluetoothDevice = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(bluetoothDevice.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {}, TRANSPORT_LE)).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void connectGatt_withTransportPhy_doesntCrash() throws Exception {
        BluetoothDevice bluetoothDevice = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(bluetoothDevice.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {}, TRANSPORT_LE, PHY_LE_1M_MASK)).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void connectGatt_withTransportPhyHandler_doesntCrash() throws Exception {
        BluetoothDevice bluetoothDevice = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        assertThat(bluetoothDevice.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {}, TRANSPORT_LE, PHY_LE_1M_MASK, new Handler())).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void canSetAndGetType() throws Exception {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        Shadows.shadowOf(device).setType(DEVICE_TYPE_CLASSIC);
        assertThat(device.getType()).isEqualTo(DEVICE_TYPE_CLASSIC);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void canGetBluetoothGatts() throws Exception {
        BluetoothDevice device = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        List<BluetoothGatt> createdGatts = new ArrayList<>();
        createdGatts.add(device.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {}));
        createdGatts.add(device.connectGatt(ApplicationProvider.getApplicationContext(), false, new BluetoothGattCallback() {}));
        assertThat(Shadows.shadowOf(device).getBluetoothGatts()).isEqualTo(createdGatts);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void connectGatt_setsBluetoothGattCallback() throws Exception {
        BluetoothDevice device = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        BluetoothGattCallback callback = new BluetoothGattCallback() {};
        BluetoothGatt bluetoothGatt = device.connectGatt(ApplicationProvider.getApplicationContext(), false, callback);
        assertThat(Shadows.shadowOf(bluetoothGatt).getGattCallback()).isEqualTo(callback);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void canSimulateGattConnectionChange() throws Exception {
        BluetoothDevice device = ShadowBluetoothDevice.newInstance(ShadowBluetoothDeviceTest.MOCK_MAC_ADDRESS);
        BluetoothGattCallback callback = Mockito.mock(BluetoothGattCallback.class);
        BluetoothGatt bluetoothGatt = device.connectGatt(ApplicationProvider.getApplicationContext(), false, callback);
        int status = 4;
        int newState = 2;
        Shadows.shadowOf(device).simulateGattConnectionChange(status, newState);
        Mockito.verify(callback).onConnectionStateChange(bluetoothGatt, status, newState);
    }
}

