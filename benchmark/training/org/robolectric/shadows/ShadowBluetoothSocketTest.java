package org.robolectric.shadows;


import android.bluetooth.BluetoothSocket;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.robolectric.Shadows.shadowOf;


@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothSocketTest {
    BluetoothSocket bluetoothSocket;

    private static final byte[] DATA = new byte[]{ 1, 2, 3, 42, 96, 127 };

    @Test
    public void getInputStreamFeeder() throws Exception {
        shadowOf(bluetoothSocket).getInputStreamFeeder().write(ShadowBluetoothSocketTest.DATA);
        InputStream inputStream = bluetoothSocket.getInputStream();
        byte[] b = new byte[1024];
        int len = inputStream.read(b);
        assertThat(Arrays.copyOf(b, len)).isEqualTo(ShadowBluetoothSocketTest.DATA);
    }

    @Test
    public void getOutputStreamSink() throws Exception {
        bluetoothSocket.getOutputStream().write(ShadowBluetoothSocketTest.DATA);
        byte[] b = new byte[1024];
        int len = shadowOf(bluetoothSocket).getOutputStreamSink().read(b);
        assertThat(Arrays.copyOf(b, len)).isEqualTo(ShadowBluetoothSocketTest.DATA);
    }

    @Test
    public void close() throws Exception {
        bluetoothSocket.close();
        try {
            bluetoothSocket.connect();
            Assert.fail();
        } catch (IOException expected) {
            // Expected.
        }
    }

    @Test
    public void connect() throws Exception {
        assertThat(bluetoothSocket.isConnected()).isFalse();
        bluetoothSocket.connect();
        assertThat(bluetoothSocket.isConnected()).isTrue();
        bluetoothSocket.close();
        assertThat(bluetoothSocket.isConnected()).isFalse();
    }
}

