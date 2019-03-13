package org.robolectric.shadows;


import Context.BLUETOOTH_SERVICE;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
public class ShadowBluetoothManagerTest {
    private final BluetoothManager manager = ((BluetoothManager) (ApplicationProvider.getApplicationContext().getSystemService(BLUETOOTH_SERVICE)));

    @Test
    public void getAdapter_shouldReturnBluetoothAdapter() {
        assertThat(manager.getAdapter()).isSameAs(BluetoothAdapter.getDefaultAdapter());
    }
}

