package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.view.InputDevice;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowInputDeviceTest {
    @Test
    public void canConstructInputDeviceWithName() throws Exception {
        InputDevice inputDevice = ShadowInputDevice.makeInputDeviceNamed("foo");
        assertThat(inputDevice.getName()).isEqualTo("foo");
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void canChangeProductId() throws Exception {
        InputDevice inputDevice = ShadowInputDevice.makeInputDeviceNamed("foo");
        ShadowInputDevice shadowInputDevice = Shadow.extract(inputDevice);
        shadowInputDevice.setProductId(1337);
        assertThat(inputDevice.getProductId()).isEqualTo(1337);
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void canChangeVendorId() throws Exception {
        InputDevice inputDevice = ShadowInputDevice.makeInputDeviceNamed("foo");
        ShadowInputDevice shadowInputDevice = Shadow.extract(inputDevice);
        shadowInputDevice.setVendorId(1337);
        assertThat(inputDevice.getVendorId()).isEqualTo(1337);
    }
}

