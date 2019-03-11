package org.zeromq;


import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZBeacon.Listener;


public class ZBeaconTest {
    @Test
    public void testReceiveOwnBeacons() throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        byte[] beacon = new byte[]{ 'H', 'Y', 'D', 'R', 'A', 1, 18, 52 };
        byte[] prefix = new byte[]{ 'H', 'Y', 'D', 'R', 'A', 1 };
        int port = Utils.findOpenPort();
        ZBeacon zbeacon = new ZBeacon("255.255.255.255", port, beacon, false);
        zbeacon.setPrefix(prefix);
        zbeacon.setListener(new Listener() {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                latch.countDown();
            }
        });
        zbeacon.start();
        latch.await(20, TimeUnit.SECONDS);
        Assert.assertThat(latch.getCount(), CoreMatchers.is(0L));
        zbeacon.stop();
    }

    @Test
    public void testReceiveOwnBeaconsBlocking() throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        byte[] beacon = new byte[]{ 'H', 'Y', 'D', 'R', 'A', 1, 18, 52 };
        byte[] prefix = new byte[]{ 'H', 'Y', 'D', 'R', 'A', 1 };
        int port = Utils.findOpenPort();
        ZBeacon zbeacon = new ZBeacon("255.255.255.255", port, beacon, false, true);
        zbeacon.setPrefix(prefix);
        zbeacon.setListener(new Listener() {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                latch.countDown();
            }
        });
        zbeacon.start();
        latch.await(20, TimeUnit.SECONDS);
        Assert.assertThat(latch.getCount(), CoreMatchers.is(0L));
        zbeacon.stop();
    }
}

