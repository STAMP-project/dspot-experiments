package com.danikula.videocache;


import com.danikula.videocache.support.ProxyCacheTestUtils;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;


/**
 * Tests {@link Pinger}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class PingerTest extends BaseTest {
    @Test
    public void testPingSuccess() throws Exception {
        HttpProxyCacheServer server = new HttpProxyCacheServer(RuntimeEnvironment.application);
        Pinger pinger = new Pinger("127.0.0.1", ProxyCacheTestUtils.getPort(server));
        boolean pinged = pinger.ping(1, 100);
        assertThat(pinged).isTrue();
        server.shutdown();
    }

    @Test
    public void testPingFail() throws Exception {
        Pinger pinger = new Pinger("127.0.0.1", 33);
        boolean pinged = pinger.ping(3, 70);
        assertThat(pinged).isFalse();
    }

    @Test
    public void testIsPingRequest() throws Exception {
        Pinger pinger = new Pinger("127.0.0.1", 1);
        assertThat(pinger.isPingRequest("ping")).isTrue();
        assertThat(pinger.isPingRequest("notPing")).isFalse();
    }

    @Test
    public void testResponseToPing() throws Exception {
        Pinger pinger = new Pinger("127.0.0.1", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        pinger.responseToPing(socket);
        assertThat(out.toString()).isEqualTo("HTTP/1.1 200 OK\n\nping ok");
    }

    // https://github.com/danikula/AndroidVideoCache/issues/28
    @Test
    public void testPingedWithExternalProxy() throws Exception {
        ProxyCacheTestUtils.installExternalSystemProxy();
        HttpProxyCacheServer server = new HttpProxyCacheServer(RuntimeEnvironment.application);
        Pinger pinger = new Pinger("127.0.0.1", ProxyCacheTestUtils.getPortWithoutPing(server));
        assertThat(pinger.ping(1, 100)).isTrue();
    }

    // https://github.com/danikula/AndroidVideoCache/issues/28
    @Test
    public void testIsNotPingedWithoutCustomProxySelector() throws Exception {
        HttpProxyCacheServer server = new HttpProxyCacheServer(RuntimeEnvironment.application);
        // IgnoreHostProxySelector is set in HttpProxyCacheServer constructor. So let reset it by custom.
        ProxyCacheTestUtils.installExternalSystemProxy();
        Pinger pinger = new Pinger("127.0.0.1", ProxyCacheTestUtils.getPortWithoutPing(server));
        assertThat(pinger.ping(1, 100)).isFalse();
    }
}

