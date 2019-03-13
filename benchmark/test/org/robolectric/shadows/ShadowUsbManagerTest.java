package org.robolectric.shadows;


import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


/**
 * Unit tests for {@link ShadowUsbManager}.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowUsbManagerTest {
    private static final String DEVICE_NAME_1 = "usb1";

    private static final String DEVICE_NAME_2 = "usb2";

    private UsbManager usbManager;

    @Mock
    UsbDevice usbDevice1;

    @Mock
    UsbDevice usbDevice2;

    @Mock
    UsbAccessory usbAccessory;

    @Test
    public void getDeviceList() {
        assertThat(usbManager.getDeviceList()).isEmpty();
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice1, true);
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice2, true);
        assertThat(usbManager.getDeviceList().values()).containsExactly(usbDevice1, usbDevice2);
    }

    @Test
    public void hasPermission() {
        assertThat(usbManager.hasPermission(usbDevice1)).isFalse();
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice1, false);
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice2, false);
        assertThat(usbManager.hasPermission(usbDevice1)).isFalse();
        assertThat(usbManager.hasPermission(usbDevice2)).isFalse();
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice1, true);
        assertThat(usbManager.hasPermission(usbDevice1)).isTrue();
        assertThat(usbManager.hasPermission(usbDevice2)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void grantPermission_selfPackage_shouldHavePermission() {
        usbManager.grantPermission(usbDevice1);
        assertThat(usbManager.hasPermission(usbDevice1)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N_MR1)
    public void grantPermission_differentPackage_shouldHavePermission() {
        usbManager.grantPermission(usbDevice1, "foo.bar");
        assertThat(Shadows.shadowOf(usbManager).hasPermissionForPackage(usbDevice1, "foo.bar")).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N_MR1)
    public void revokePermission_shouldNotHavePermission() {
        usbManager.grantPermission(usbDevice1, "foo.bar");
        assertThat(Shadows.shadowOf(usbManager).hasPermissionForPackage(usbDevice1, "foo.bar")).isTrue();
        Shadows.shadowOf(usbManager).revokePermission(usbDevice1, "foo.bar");
        assertThat(Shadows.shadowOf(usbManager).hasPermissionForPackage(usbDevice1, "foo.bar")).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M, maxSdk = VERSION_CODES.P)
    public void getPorts_shouldReturnAddedPorts() {
        Shadows.shadowOf(usbManager).addPort("port1");
        Shadows.shadowOf(usbManager).addPort("port2");
        Shadows.shadowOf(usbManager).addPort("port3");
        List<UsbPort> usbPorts = getUsbPorts();
        assertThat(usbPorts).hasSize(3);
        assertThat(usbPorts.stream().map(UsbPort::getId).collect(Collectors.toList())).containsExactly("port1", "port2", "port3");
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void clearPorts_shouldRemoveAllPorts() {
        Shadows.shadowOf(usbManager).addPort("port1");
        Shadows.shadowOf(usbManager).clearPorts();
        List<UsbPort> usbPorts = getUsbPorts();
        assertThat(usbPorts).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M, maxSdk = VERSION_CODES.P)
    public void setPortRoles_sinkHost_shouldSetPortStatus() {
        final int powerRoleSink = ReflectionHelpers.getStaticField(UsbPort.class, "POWER_ROLE_SINK");
        final int dataRoleHost = ReflectionHelpers.getStaticField(UsbPort.class, "DATA_ROLE_HOST");
        Shadows.shadowOf(usbManager).addPort("port1");
        List<UsbPort> usbPorts = getUsbPorts();
        _usbManager_().setPortRoles(usbPorts.get(0), powerRoleSink, dataRoleHost);
        UsbPortStatus usbPortStatus = _usbManager_().getPortStatus(usbPorts.get(0));
        assertThat(usbPortStatus.getCurrentPowerRole()).isEqualTo(powerRoleSink);
        assertThat(usbPortStatus.getCurrentDataRole()).isEqualTo(dataRoleHost);
    }

    @Test
    public void removeDevice() {
        assertThat(usbManager.getDeviceList()).isEmpty();
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice1, false);
        Shadows.shadowOf(usbManager).addOrUpdateUsbDevice(usbDevice2, false);
        assertThat(usbManager.getDeviceList().values()).containsExactly(usbDevice1, usbDevice2);
        Shadows.shadowOf(usbManager).removeUsbDevice(usbDevice1);
        assertThat(usbManager.getDeviceList().values()).containsExactly(usbDevice2);
    }

    @Test
    public void openAccessory() {
        assertThat(usbManager.openAccessory(usbAccessory)).isNotNull();
    }

    @Test
    public void setAccessory() {
        assertThat(usbManager.getAccessoryList()).isNull();
        Shadows.shadowOf(usbManager).setAttachedUsbAccessory(usbAccessory);
        assertThat(usbManager.getAccessoryList()).hasLength(1);
        assertThat(usbManager.getAccessoryList()[0]).isEqualTo(usbAccessory);
    }
}

