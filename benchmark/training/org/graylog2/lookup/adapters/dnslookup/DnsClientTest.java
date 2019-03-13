/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.lookup.adapters.dnslookup;


import com.google.common.net.InternetDomainName;
import org.junit.Assert;
import org.junit.Test;


public class DnsClientTest {
    @Test
    public void testReverseIpFormat() {
        DnsClient dnsClient = new DnsClient();
        // Test IPv4 reverse format.
        Assert.assertEquals("40.30.20.10.in-addr.arpa.", dnsClient.getInverseAddressFormat("10.20.30.40"));
        // Test IPv6 reverse format.
        Assert.assertEquals("1.0.0.b.1.a.7.0.0.0.0.0.0.0.0.0.0.1.0.0.0.0.8.0.0.8.8.a.4.0.6.2.ip6.arpa.", dnsClient.getInverseAddressFormat("2604:a880:800:10::7a1:b001"));
    }

    @Test
    public void testValidIp4Address() {
        Assert.assertTrue(DnsClient.isIp4Address("8.8.8.8"));
        Assert.assertTrue(DnsClient.isIp4Address("127.0.0.1"));
        Assert.assertFalse(DnsClient.isIp4Address("t127.0.0.1"));
        Assert.assertFalse(DnsClient.isIp4Address("google.com"));
    }

    @Test
    public void testValidIp6Address() {
        Assert.assertTrue(DnsClient.isIp6Address("2607:f8b0:4000:812::200e"));
        Assert.assertTrue(DnsClient.isIp6Address("2606:2800:220:1:248:1893:25c8:1946"));
        Assert.assertFalse(DnsClient.isIp6Address("t2606:2800:220:1:248:1893:25c8:1946"));
        Assert.assertFalse(DnsClient.isIp6Address("google.com"));
    }

    @Test
    public void testValidCommaSeparatedIps() {
        Assert.assertTrue(DnsClient.allIpAddressesValid("8.8.4.4:53, 8.8.8.8"));// Custom port.

        Assert.assertTrue(DnsClient.allIpAddressesValid("8.8.4.4, "));// Extra comma

        Assert.assertTrue(DnsClient.allIpAddressesValid("8.8.4.4"));// Pure IP

        Assert.assertFalse(DnsClient.allIpAddressesValid("8.8.4.4dfs:53, 8.8.4.4:59"));// Not an IP address

        Assert.assertFalse(DnsClient.allIpAddressesValid("8.8.4.4 8.8.8.8"));// No comma separator

        Assert.assertFalse(DnsClient.allIpAddressesValid("8.8.4.4, google.com"));// Hostname is not an IP address

    }

    @Test
    public void testValidHostname() {
        Assert.assertTrue(DnsClient.isHostName("google.com"));
        Assert.assertTrue(DnsClient.isHostName("api.graylog.com"));
        Assert.assertFalse(DnsClient.isHostName("http://api.graylog.com"));// Prefix not allowed

        Assert.assertFalse(DnsClient.isHostName("api.graylog.com/10"));// URL params not allowed

        Assert.assertFalse(DnsClient.isHostName("api.graylog.com?name=dano"));// Query strings not allowed.

    }

    /**
     * This test ensures that Google Guava domain parsing is working as expected (helps add protection for if
     * the Guava dependency is upgraded in the future.)
     */
    @Test
    public void testExtractShortDomain() {
        // Verify that Google Guava correctly returns just the top domain for multi-level TLDs.
        // Eg. lb01.store.amazon.co.uk should resolve to just amazon.co.uk
        InternetDomainName internetDomainName = InternetDomainName.from("lb01.store.amazon.co.uk");
        Assert.assertEquals("amazon.co.uk", topDomainUnderRegistrySuffix().toString());
    }

    /**
     * The logic that parses reverse lookup domains is complex, so it needs to be tested.
     */
    @Test
    public void testParseReverseLookupDomain() {
        // Test all of the types of domains that parsing is performed for.
        PtrDnsAnswer result = buildReverseLookupDomainTest("subdomain.test.co.uk");
        Assert.assertEquals("subdomain.test.co.uk", result.fullDomain());
        Assert.assertEquals("test.co.uk", result.domain());
        result = buildReverseLookupDomainTest("subdomain.test.com");
        Assert.assertEquals("subdomain.test.com", result.fullDomain());
        Assert.assertEquals("test.com", result.domain());
        // Test some completely bogus domain to verify that the manual domain parsing is exercised.
        result = buildReverseLookupDomainTest("blah.blahblah.lala.blaaa");
        Assert.assertEquals("blah.blahblah.lala.blaaa", result.fullDomain());
        Assert.assertEquals("lala.blaaa", result.domain());
        // Test a single word domain
        result = buildReverseLookupDomainTest("HahaOneWordDomainTryingToBreakTheSoftware");
        Assert.assertEquals("HahaOneWordDomainTryingToBreakTheSoftware", result.fullDomain());
        Assert.assertEquals("HahaOneWordDomainTryingToBreakTheSoftware", result.domain());
    }
}

