package org.robolectric.shadows;


import android.view.InputDevice;
import android.view.KeyEvent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowInputEventTest {
    @Test
    public void canSetInputDeviceOnKeyEvent() throws Exception {
        InputDevice myDevice = ShadowInputDevice.makeInputDeviceNamed("myDevice");
        KeyEvent keyEvent = new KeyEvent(1, 2);
        Shadows.shadowOf(keyEvent).setDevice(myDevice);
        assertThat(keyEvent.getDevice().getName()).isEqualTo("myDevice");
    }
}

