package io.hawt.util;


import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class HostsTest {
    @Test
    public void getNetworkInterfaceAddresses() throws Exception {
        Map<String, Set<InetAddress>> includeLoopback = Hosts.getNetworkInterfaceAddresses(true);
        Assert.assertFalse(includeLoopback.isEmpty());
        HostsTest.assertLoopback("Should include loopback", true, includeLoopback);
        Map<String, Set<InetAddress>> noLoopback = Hosts.getNetworkInterfaceAddresses(false);
        Assert.assertFalse(noLoopback.isEmpty());
        HostsTest.assertLoopback("Should not include loopback", false, noLoopback);
    }

    @Test
    public void getAddresses() throws Exception {
        Assert.assertFalse(Hosts.getAddresses().isEmpty());
    }

    @Test
    public void getLocalHostName() throws Exception {
        Assert.assertNotNull(Hosts.getLocalHostName());
    }

    @Test
    public void getLocalIp() throws Exception {
        Assert.assertNotNull(Hosts.getLocalIp());
    }
}

