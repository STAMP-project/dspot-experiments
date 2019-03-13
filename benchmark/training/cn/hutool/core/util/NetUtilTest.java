package cn.hutool.core.util;


import PatternPool.MAC_ADDRESS;
import java.net.InetAddress;
import org.junit.Assert;
import org.junit.Test;


/**
 * NetUtil????
 *
 * @author Looly
 */
public class NetUtilTest {
    @Test
    public void getLocalhostTest() {
        InetAddress localhost = NetUtil.getLocalhost();
        Assert.assertNotNull(localhost);
    }

    @Test
    public void getLocalMacAddressTest() {
        String macAddress = NetUtil.getLocalMacAddress();
        Assert.assertNotNull(macAddress);
        // ??MAC????
        boolean match = ReUtil.isMatch(MAC_ADDRESS, macAddress);
        Assert.assertTrue(match);
    }

    @Test
    public void longToIpTest() {
        String ipv4 = NetUtil.longToIpv4(2130706433L);
        Assert.assertEquals("127.0.0.1", ipv4);
    }

    @Test
    public void ipToLongTest() {
        long ipLong = NetUtil.ipv4ToLong("127.0.0.1");
        Assert.assertEquals(2130706433L, ipLong);
    }
}

