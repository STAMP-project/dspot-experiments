package org.robolectric.shadows;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.IOException;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


/**
 * Unit tests for {@link ShadowBluetoothServerSocket}.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowBluetoothServerSocketTest {
    private static final UUID DUMMY_UUID = UUID.fromString("00000000-1111-2222-3333-444444444444");

    private BluetoothServerSocket serverSocket;

    @Test
    public void accept() throws Exception {
        BluetoothDevice btDevice = ShadowBluetoothDevice.newInstance("DE:AD:BE:EE:EE:EF");
        Shadows.shadowOf(serverSocket).deviceConnected(btDevice);
        BluetoothSocket clientSocket = serverSocket.accept();
        assertThat(clientSocket.getRemoteDevice()).isSameAs(btDevice);
    }

    @Test
    public void accept_timeout() {
        try {
            serverSocket.accept(200);
            Assert.fail();
        } catch (IOException expected) {
            // Expected
        }
    }

    @Test
    public void close() throws Exception {
        serverSocket.close();
        try {
            serverSocket.accept();
            Assert.fail();
        } catch (IOException expected) {
            // Expected.
        }
    }
}

