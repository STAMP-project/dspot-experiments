package brave.internal;


import Platform.Jre7;
import com.google.common.collect.Sets;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest({ Platform.class, NetworkInterface.class })
public class PlatformTest {
    Platform platform = Jre7.buildIfSupported();

    @Test
    public void clock_hasNiceToString_jre7() {
        assertThat(platform.clock()).hasToString("System.currentTimeMillis()");
    }

    @Test
    public void clock_hasNiceToString_jre9() {
        Platform platform = new Platform.Jre9();
        assertThat(platform.clock()).hasToString("Clock.systemUTC().instant()");
    }

    // example from X-Amzn-Trace-Id: Root=1-5759e988-bd862e3fe1be46a994272793;Sampled=1
    @Test
    public void randomLong_epochSecondsPlusRandom() {
        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(1465510280000L);// Thursday, June 9, 2016 10:11:20 PM

        long traceIdHigh = Jre7.buildIfSupported().nextTraceIdHigh();
        assertThat(HexCodec.toLowerHex(traceIdHigh)).startsWith("5759e988");
    }

    @Test
    public void randomLong_whenRandomIsMostNegative() {
        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(1465510280000L);
        long traceIdHigh = Platform.nextTraceIdHigh(-1);
        assertThat(HexCodec.toLowerHex(traceIdHigh)).isEqualTo("5759e988ffffffff");
    }

    @Test
    public void linkLocalIp_lazySet() {
        assertThat(platform.linkLocalIp).isNull();// sanity check setup

        // cannot test as the there is no link local IP
        if ((platform.produceLinkLocalIp()) == null)
            return;

        assertThat(platform.linkLocalIp()).isNotNull();
    }

    @Test
    public void linkLocalIp_sameInstance() {
        assertThat(platform.linkLocalIp()).isSameAs(platform.linkLocalIp());
    }

    @Test
    public void produceLinkLocalIp_exceptionReadingNics() throws Exception {
        mockStatic(NetworkInterface.class);
        when(NetworkInterface.getNetworkInterfaces()).thenThrow(SocketException.class);
        assertThat(platform.produceLinkLocalIp()).isNull();
    }

    /**
     * possible albeit very unlikely
     */
    @Test
    public void produceLinkLocalIp_noNics() throws Exception {
        mockStatic(NetworkInterface.class);
        when(NetworkInterface.getNetworkInterfaces()).thenReturn(null);
        assertThat(platform.linkLocalIp()).isNull();
        when(NetworkInterface.getNetworkInterfaces()).thenReturn(new Vector<NetworkInterface>().elements());
        assertThat(platform.produceLinkLocalIp()).isNull();
    }

    /**
     * also possible albeit unlikely
     */
    @Test
    public void produceLinkLocalIp_noAddresses() throws Exception {
        PlatformTest.nicWithAddress(null);
        assertThat(platform.produceLinkLocalIp()).isNull();
    }

    @Test
    public void produceLinkLocalIp_siteLocal_ipv4() throws Exception {
        PlatformTest.nicWithAddress(InetAddress.getByAddress("local", new byte[]{ ((byte) (192)), ((byte) (168)), 0, 1 }));
        assertThat(platform.produceLinkLocalIp()).isEqualTo("192.168.0.1");
    }

    @Test
    public void produceLinkLocalIp_siteLocal_ipv6() throws Exception {
        InetAddress ipv6 = Inet6Address.getByName("fec0:db8::c001");
        PlatformTest.nicWithAddress(ipv6);
        assertThat(platform.produceLinkLocalIp()).isEqualTo(ipv6.getHostAddress());
    }

    @Test
    public void produceLinkLocalIp_notSiteLocal_ipv4() throws Exception {
        PlatformTest.nicWithAddress(InetAddress.getByAddress("external", new byte[]{ 1, 2, 3, 4 }));
        assertThat(platform.produceLinkLocalIp()).isNull();
    }

    @Test
    public void produceLinkLocalIp_notSiteLocal_ipv6() throws Exception {
        PlatformTest.nicWithAddress(Inet6Address.getByName("2001:db8::c001"));
        assertThat(platform.produceLinkLocalIp()).isNull();
    }

    /**
     * Getting an endpoint is expensive. This tests it is provisioned only once.
     *
     * test inspired by dagger.internal.DoubleCheckTest
     */
    @Test
    public void linkLocalIp_provisionsOnce() throws Exception {
        // create all the tasks up front so that they are executed with no delay
        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> platform.linkLocalIp());
        }
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        List<Future<String>> futures = executor.invokeAll(tasks);
        // check there's only a single unique endpoint returned
        Set<Object> results = Sets.newIdentityHashSet();
        for (Future<String> future : futures) {
            results.add(future.get());
        }
        assertThat(results).hasSize(1);
        executor.shutdownNow();
    }
}

