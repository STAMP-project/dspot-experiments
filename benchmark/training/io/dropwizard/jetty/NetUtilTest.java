package io.dropwizard.jetty;


import NetUtil.DEFAULT_TCP_BACKLOG_LINUX;
import NetUtil.DEFAULT_TCP_BACKLOG_WINDOWS;
import java.net.InetAddress;
import java.util.Collection;
import org.junit.jupiter.api.Test;


public class NetUtilTest {
    private static final String OS_NAME_PROPERTY = "os.name";

    /**
     * Assuming Windows
     */
    @Test
    public void testDefaultTcpBacklogForWindows() {
        assumeThat(System.getProperty(NetUtilTest.OS_NAME_PROPERTY)).contains("win");
        assumeThat(isTcpBacklogSettingReadable()).isFalse();
        assertThat(NetUtil.getTcpBacklog()).isEqualTo(DEFAULT_TCP_BACKLOG_WINDOWS);
    }

    /**
     * Assuming Mac (which does not have /proc)
     */
    @Test
    public void testNonWindowsDefaultTcpBacklog() {
        assumeThat(System.getProperty(NetUtilTest.OS_NAME_PROPERTY)).contains("Mac OS X");
        assumeThat(isTcpBacklogSettingReadable()).isFalse();
        assertThat(NetUtil.getTcpBacklog()).isEqualTo(DEFAULT_TCP_BACKLOG_LINUX);
    }

    /**
     * Assuming Mac (which does not have /proc)
     */
    @Test
    public void testNonWindowsSpecifiedTcpBacklog() {
        assumeThat(System.getProperty(NetUtilTest.OS_NAME_PROPERTY)).contains("Mac OS X");
        assumeThat(isTcpBacklogSettingReadable()).isFalse();
        assertThat(NetUtil.getTcpBacklog(100)).isEqualTo(100);
    }

    /**
     * Assuming Linux (which has /proc)
     */
    @Test
    public void testOsSetting() {
        assumeThat(System.getProperty(NetUtilTest.OS_NAME_PROPERTY)).contains("Linux");
        assumeThat(isTcpBacklogSettingReadable()).isTrue();
        assertThat(NetUtil.getTcpBacklog((-1))).isNotEqualTo((-1));
        assertThat(NetUtil.getTcpBacklog()).as("NetUtil should read more than the first character of somaxconn").isGreaterThan(2);
    }

    @Test
    public void testAllLocalIps() throws Exception {
        NetUtil.setLocalIpFilter(( nif, adr) -> ((adr != null) && (!(adr.isLoopbackAddress()))) && ((nif.isPointToPoint()) || (!(adr.isLinkLocalAddress()))));
        final Collection<InetAddress> addresses = NetUtil.getAllLocalIPs();
        assertThat(addresses.size()).isGreaterThan(0);
        assertThat(addresses).doesNotContain(InetAddress.getLoopbackAddress());
    }

    @Test
    public void testLocalIpsWithLocalFilter() throws Exception {
        NetUtil.setLocalIpFilter(( inf, adr) -> adr != null);
        final Collection<InetAddress> addresses = NetUtil.getAllLocalIPs();
        assertThat(addresses.size()).isGreaterThan(0);
        assertThat(addresses).contains(InetAddress.getLoopbackAddress());
    }
}

