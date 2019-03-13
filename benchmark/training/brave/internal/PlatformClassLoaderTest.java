package brave.internal;


import java.net.InetSocketAddress;
import org.junit.Test;


public class PlatformClassLoaderTest {
    @Test
    public void unloadable_afterGet() {
        assertRunIsUnloadable(PlatformClassLoaderTest.GetPlatform.class);
    }

    static class GetPlatform implements Runnable {
        @Override
        public void run() {
            Platform platform = Platform.get();
            assertThat(platform).isNotNull();
        }
    }

    @Test
    public void unloadable_afterGetLinkLocalIp() {
        assertRunIsUnloadable(PlatformClassLoaderTest.GetPlatformLinkLocalIp.class);
    }

    static class GetPlatformLinkLocalIp implements Runnable {
        @Override
        public void run() {
            Platform platform = Platform.get();
            platform.linkLocalIp();
        }
    }

    @Test
    public void unloadable_afterGetNextTraceIdHigh() {
        assertRunIsUnloadable(PlatformClassLoaderTest.GetPlatformNextTraceIdHigh.class);
    }

    static class GetPlatformNextTraceIdHigh implements Runnable {
        @Override
        public void run() {
            Platform platform = Platform.get();
            assertThat(platform.nextTraceIdHigh()).isNotZero();
        }
    }

    @Test
    public void unloadable_afterGetHostString() {
        assertRunIsUnloadable(PlatformClassLoaderTest.GetPlatformHostString.class);
    }

    static class GetPlatformHostString implements Runnable {
        @Override
        public void run() {
            Platform platform = Platform.get();
            assertThat(platform.getHostString(InetSocketAddress.createUnresolved("1.2.3.4", 0))).isNotNull();
        }
    }

    @Test
    public void unloadable_afterGetClock() {
        assertRunIsUnloadable(PlatformClassLoaderTest.GetPlatformClock.class);
    }

    static class GetPlatformClock implements Runnable {
        @Override
        public void run() {
            Platform platform = Platform.get();
            assertThat(platform.clock().currentTimeMicroseconds()).isPositive();
        }
    }
}

