package org.stagemonitor.core.util;


import java.net.Inet4Address;
import java.net.InetAddress;
import org.junit.Test;


public class InetAddressesTest {
    @Test
    public void setClientIp() {
        final InetAddress inetAddress = InetAddresses.forString("192.168.58.1");
        final int ipAsInt = InetAddresses.inetAddressToInt(((Inet4Address) (inetAddress)));
        final String ip = InetAddresses.fromInteger(ipAsInt).getHostAddress();
        assertThat(ip).isEqualTo("192.168.58.1");
    }
}

