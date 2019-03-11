package org.jak_linux.dns66.vpn;


import Configuration.Item;
import Configuration.Whitelist;
import VpnService.Builder;
import android.util.Log;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.jak_linux.dns66.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by jak on 19/04/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class AdVpnThreadTest {
    private AdVpnService service;

    private AdVpnThread thread;

    private Configuration config;

    private Builder builder;

    private List<InetAddress> serversAdded;

    @Test
    public void testConfigurePackages() throws Exception {
        final List<String> disallowed = new ArrayList<>();
        final List<String> allowed = new ArrayList<>();
        when(builder.addDisallowedApplication(ArgumentMatchers.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                disallowed.add(getArgumentAt(0, String.class));
                return null;
            }
        });
        when(builder.addAllowedApplication(ArgumentMatchers.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                allowed.add(getArgumentAt(0, String.class));
                return null;
            }
        });
        // We are defaulting to disallow: allow all allowed packages.
        allowed.clear();
        disallowed.clear();
        config.whitelist.defaultMode = Whitelist.DEFAULT_MODE_NOT_ON_VPN;
        thread.configurePackages(builder, config);
        Assert.assertTrue(allowed.contains("onVpn"));
        Assert.assertEquals(new ArrayList<String>(), disallowed);
        // We are defaulting to allow: deny all non-allowed packages.
        allowed.clear();
        disallowed.clear();
        config.whitelist.defaultMode = Whitelist.DEFAULT_MODE_ON_VPN;
        thread.configurePackages(builder, config);
        Assert.assertTrue(disallowed.contains("notOnVpn"));
        Assert.assertEquals(new ArrayList<String>(), allowed);
        // Intelligent is like allow, it only disallows system apps
        allowed.clear();
        disallowed.clear();
        config.whitelist.defaultMode = Whitelist.DEFAULT_MODE_INTELLIGENT;
        thread.configurePackages(builder, config);
        Assert.assertTrue(disallowed.contains("notOnVpn"));
        Assert.assertEquals(new ArrayList<String>(), allowed);
    }

    @Test
    public void testHasIpV6Servers() throws Exception {
        Configuration.Item item0 = new Configuration.Item();
        Configuration.Item item1 = new Configuration.Item();
        config.ipV6Support = true;
        config.dnsServers.enabled = true;
        config.dnsServers.items.add(item0);
        config.dnsServers.items.add(item1);
        item0.location = "::1";
        item0.state = Item.STATE_ALLOW;
        item1.location = "127.0.0.1";
        item1.state = Item.STATE_ALLOW;
        Set<InetAddress> servers = new HashSet<>();
        Assert.assertTrue(thread.hasIpV6Servers(config, servers));
        config.ipV6Support = false;
        Assert.assertFalse(thread.hasIpV6Servers(config, servers));
        config.ipV6Support = true;
        item0.state = Item.STATE_DENY;
        Assert.assertFalse(thread.hasIpV6Servers(config, servers));
        servers.add(Inet6Address.getByName("127.0.0.1"));
        Assert.assertFalse(thread.hasIpV6Servers(config, servers));
        servers.add(Inet6Address.getByName("::1"));
        Assert.assertTrue(thread.hasIpV6Servers(config, servers));
    }

    // Everything works fine, everyone gets through.
    @Test
    public void testNewDNSServer() throws Exception {
        String format = "192.168.0.%d";
        byte[] ipv6Template = new byte[]{ 32, 1, 13, ((byte) (184 & 255)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        InetAddress i6addr = Inet6Address.getByName("::1");
        InetAddress i4addr = Inet4Address.getByName("127.0.0.1");
        thread.newDNSServer(builder, format, ipv6Template, i4addr);
        Assert.assertTrue(thread.upstreamDnsServers.contains(i4addr));
        Assert.assertTrue(serversAdded.contains(InetAddress.getByName("192.168.0.2")));
        thread.newDNSServer(builder, format, ipv6Template, i6addr);
        Assert.assertTrue(thread.upstreamDnsServers.contains(i6addr));
        Assert.assertEquals(3, ipv6Template[((ipv6Template.length) - 1)]);
        Assert.assertTrue(serversAdded.contains(InetAddress.getByAddress(ipv6Template)));
    }

    // IPv6 is disabled: We only get IPv4 servers through
    @Test
    public void testNewDNSServer_ipv6disabled() throws Exception {
        byte[] ipv6Template = new byte[]{ 32, 1, 13, ((byte) (184 & 255)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        InetAddress i6addr = Inet6Address.getByName("::1");
        thread.newDNSServer(builder, "192.168.0.%d", null, i6addr);
        Assert.assertTrue(serversAdded.isEmpty());
        Assert.assertTrue(thread.upstreamDnsServers.isEmpty());
        InetAddress i4addr = Inet4Address.getByName("127.0.0.1");
        thread.newDNSServer(builder, "192.168.0.%d", null, i4addr);
        Assert.assertTrue(serversAdded.contains(InetAddress.getByName("192.168.0.2")));
        Assert.assertTrue(thread.upstreamDnsServers.contains(i4addr));
    }

    // IPv4 is disabled: We only get IPv6 servers through
    @Test
    public void testNewDNSServer_ipv4disabled() throws Exception {
        String format = "192.168.0.%d";
        byte[] ipv6Template = new byte[]{ 32, 1, 13, ((byte) (184 & 255)), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        InetAddress i6addr = Inet6Address.getByName("::1");
        InetAddress i4addr = Inet4Address.getByName("127.0.0.1");
        thread.newDNSServer(builder, null, ipv6Template, i4addr);
        Assert.assertTrue(thread.upstreamDnsServers.isEmpty());
        Assert.assertTrue(serversAdded.isEmpty());
        thread.newDNSServer(builder, format, ipv6Template, i6addr);
        Assert.assertTrue(thread.upstreamDnsServers.contains(i6addr));
        Assert.assertEquals(2, ipv6Template[((ipv6Template.length) - 1)]);
        Assert.assertTrue(serversAdded.contains(InetAddress.getByAddress(ipv6Template)));
    }
}

