package org.robolectric.shadows;


import BluetoothAdapter.LeScanCallback;
import BluetoothAdapter.SCAN_MODE_CONNECTABLE;
import BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import BluetoothAdapter.SCAN_MODE_NONE;
import BluetoothProfile.A2DP;
import BluetoothProfile.HEADSET;
import BluetoothProfile.STATE_CONNECTED;
import BluetoothProfile.STATE_DISCONNECTED;
import BluetoothProfile.ServiceListener;
import RuntimeEnvironment.application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ShadowBluetoothAdapter}
 */
@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothAdapterTest {
    private static final int MOCK_PROFILE1 = 17;

    private static final int MOCK_PROFILE2 = 21;

    private BluetoothAdapter bluetoothAdapter;

    private ShadowBluetoothAdapter shadowBluetoothAdapter;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAdapterDefaultsDisabled() {
        assertThat(bluetoothAdapter.isEnabled()).isFalse();
    }

    @Test
    public void testAdapterCanBeEnabled_forTesting() {
        shadowBluetoothAdapter.setEnabled(true);
        assertThat(bluetoothAdapter.isEnabled()).isTrue();
    }

    @Test
    public void canGetAndSetAddress() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Shadows.shadowOf(adapter).setAddress("expected");
        assertThat(adapter.getAddress()).isEqualTo("expected");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void canGetAndSetMultipleAdvertisementSupport() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // By default, multiple advertising is supported.
        assertThat(adapter.isMultipleAdvertisementSupported()).isTrue();
        // Flipping it off should update state accordingly.
        Shadows.shadowOf(adapter).setIsMultipleAdvertisementSupported(false);
        assertThat(adapter.isMultipleAdvertisementSupported()).isFalse();
    }

    @Test
    public void canEnable_withAndroidApi() throws Exception {
        bluetoothAdapter.enable();
        assertThat(bluetoothAdapter.isEnabled()).isTrue();
    }

    @Test
    public void canDisable_withAndroidApi() throws Exception {
        shadowBluetoothAdapter.setEnabled(true);
        bluetoothAdapter.disable();
        assertThat(bluetoothAdapter.isEnabled()).isFalse();
    }

    @Test
    public void name_getAndSet() throws Exception {
        // The name shouldn't be null, even before we set anything.
        assertThat(bluetoothAdapter.getName()).isNotNull();
        bluetoothAdapter.setName("Foo");
        assertThat(bluetoothAdapter.getName()).isEqualTo("Foo");
    }

    @Test
    public void scanMode_getAndSet_connectable() throws Exception {
        assertThat(bluetoothAdapter.setScanMode(SCAN_MODE_CONNECTABLE)).isTrue();
        assertThat(bluetoothAdapter.getScanMode()).isEqualTo(SCAN_MODE_CONNECTABLE);
    }

    @Test
    public void scanMode_getAndSet_discoverable() throws Exception {
        assertThat(bluetoothAdapter.setScanMode(SCAN_MODE_CONNECTABLE_DISCOVERABLE)).isTrue();
        assertThat(bluetoothAdapter.getScanMode()).isEqualTo(SCAN_MODE_CONNECTABLE_DISCOVERABLE);
    }

    @Test
    public void scanMode_getAndSet_none() throws Exception {
        assertThat(bluetoothAdapter.setScanMode(SCAN_MODE_NONE)).isTrue();
        assertThat(bluetoothAdapter.getScanMode()).isEqualTo(SCAN_MODE_NONE);
    }

    @Test
    public void scanMode_getAndSet_invalid() throws Exception {
        assertThat(bluetoothAdapter.setScanMode(9999)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void testLeScan() {
        BluetoothAdapter.LeScanCallback callback1 = newLeScanCallback();
        BluetoothAdapter.LeScanCallback callback2 = newLeScanCallback();
        bluetoothAdapter.startLeScan(callback1);
        assertThat(shadowBluetoothAdapter.getLeScanCallbacks()).containsExactly(callback1);
        bluetoothAdapter.startLeScan(callback2);
        assertThat(shadowBluetoothAdapter.getLeScanCallbacks()).containsExactly(callback1, callback2);
        bluetoothAdapter.stopLeScan(callback1);
        assertThat(shadowBluetoothAdapter.getLeScanCallbacks()).containsExactly(callback2);
        bluetoothAdapter.stopLeScan(callback2);
        assertThat(shadowBluetoothAdapter.getLeScanCallbacks()).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void testGetSingleLeScanCallback() {
        BluetoothAdapter.LeScanCallback callback1 = newLeScanCallback();
        BluetoothAdapter.LeScanCallback callback2 = newLeScanCallback();
        bluetoothAdapter.startLeScan(callback1);
        assertThat(shadowBluetoothAdapter.getSingleLeScanCallback()).isEqualTo(callback1);
        bluetoothAdapter.startLeScan(callback2);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("There are 2 callbacks");
        shadowBluetoothAdapter.getSingleLeScanCallback();
    }

    @Test
    public void insecureRfcomm_notNull() throws Exception {
        assertThat(bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("serviceName", UUID.randomUUID())).isNotNull();
    }

    @Test
    public void secureRfcomm_notNull() throws Exception {
        assertThat(bluetoothAdapter.listenUsingRfcommWithServiceRecord("serviceName", UUID.randomUUID())).isNotNull();
    }

    @Test
    public void canGetProfileConnectionState() throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        assertThat(adapter.getProfileConnectionState(HEADSET)).isEqualTo(STATE_DISCONNECTED);
        Shadows.shadowOf(adapter).setProfileConnectionState(HEADSET, STATE_CONNECTED);
        assertThat(adapter.getProfileConnectionState(HEADSET)).isEqualTo(STATE_CONNECTED);
        assertThat(adapter.getProfileConnectionState(A2DP)).isEqualTo(STATE_DISCONNECTED);
    }

    @Test
    public void getProfileProxy_afterSetProfileProxy_callsServiceListener() {
        BluetoothProfile mockProxy = Mockito.mock(BluetoothProfile.class);
        BluetoothProfile.ServiceListener mockServiceListener = Mockito.mock(ServiceListener.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
        boolean result = bluetoothAdapter.getProfileProxy(application, mockServiceListener, ShadowBluetoothAdapterTest.MOCK_PROFILE1);
        assertThat(result).isTrue();
        Mockito.verify(mockServiceListener).onServiceConnected(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
    }

    @Test
    public void getProfileProxy_afterSetProfileProxyWithNullArgument_doesNotCallServiceListener() {
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, null);
        BluetoothProfile.ServiceListener mockServiceListener = Mockito.mock(ServiceListener.class);
        boolean result = bluetoothAdapter.getProfileProxy(application, mockServiceListener, ShadowBluetoothAdapterTest.MOCK_PROFILE1);
        assertThat(result).isFalse();
        Mockito.verifyZeroInteractions(mockServiceListener);
    }

    @Test
    public void getProfileProxy_afterSetProfileProxy_forMultipleProfiles() {
        BluetoothProfile mockProxy1 = Mockito.mock(BluetoothProfile.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy1);
        BluetoothProfile mockProxy2 = Mockito.mock(BluetoothProfile.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE2, mockProxy2);
        BluetoothProfile.ServiceListener mockServiceListener = Mockito.mock(ServiceListener.class);
        boolean result1 = bluetoothAdapter.getProfileProxy(application, mockServiceListener, ShadowBluetoothAdapterTest.MOCK_PROFILE1);
        boolean result2 = bluetoothAdapter.getProfileProxy(application, mockServiceListener, ShadowBluetoothAdapterTest.MOCK_PROFILE2);
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        Mockito.verify(mockServiceListener).onServiceConnected(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy1);
        Mockito.verify(mockServiceListener).onServiceConnected(ShadowBluetoothAdapterTest.MOCK_PROFILE2, mockProxy2);
    }

    @Test
    public void hasActiveProfileProxy_reflectsSetProfileProxy() {
        BluetoothProfile mockProxy = Mockito.mock(BluetoothProfile.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1)).isTrue();
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE2)).isFalse();
    }

    @Test
    public void hasActiveProfileProxy_afterSetProfileProxyWithNullArgument_returnsFalse() {
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, null);
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1)).isFalse();
    }

    @Test
    public void closeProfileProxy_reversesSetProfileProxy() {
        BluetoothProfile mockProxy = Mockito.mock(BluetoothProfile.class);
        BluetoothProfile.ServiceListener mockServiceListener = Mockito.mock(ServiceListener.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
        bluetoothAdapter.closeProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
        boolean result = bluetoothAdapter.getProfileProxy(application, mockServiceListener, ShadowBluetoothAdapterTest.MOCK_PROFILE1);
        assertThat(result).isFalse();
        Mockito.verifyZeroInteractions(mockServiceListener);
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1)).isFalse();
    }

    @Test
    public void closeProfileProxy_afterSetProfileProxy_mismatchedProxy_noOp() {
        BluetoothProfile mockProxy1 = Mockito.mock(BluetoothProfile.class);
        BluetoothProfile mockProxy2 = Mockito.mock(BluetoothProfile.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy1);
        bluetoothAdapter.closeProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy2);
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1)).isTrue();
    }

    @Test
    public void closeProfileProxy_afterSetProfileProxyWithNullArgument_noOp() {
        BluetoothProfile mockProxy = Mockito.mock(BluetoothProfile.class);
        shadowBluetoothAdapter.setProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, null);
        bluetoothAdapter.closeProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1, mockProxy);
        assertThat(shadowBluetoothAdapter.hasActiveProfileProxy(ShadowBluetoothAdapterTest.MOCK_PROFILE1)).isFalse();
    }
}

