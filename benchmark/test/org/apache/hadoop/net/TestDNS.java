/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.net;


import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test host name and IP resolution and caching.
 */
public class TestDNS {
    private static final Logger LOG = LoggerFactory.getLogger(TestDNS.class);

    private static final String DEFAULT = "default";

    // This is not a legal hostname (starts with a hyphen). It will never
    // be returned on any test machine.
    private static final String DUMMY_HOSTNAME = "-DUMMY_HOSTNAME";

    private static final String INVALID_DNS_SERVER = "0.0.0.0";

    /**
     * Test that asking for the default hostname works
     *
     * @throws Exception
     * 		if hostname lookups fail
     */
    @Test
    public void testGetLocalHost() throws Exception {
        String hostname = DNS.getDefaultHost(TestDNS.DEFAULT);
        Assert.assertNotNull(hostname);
    }

    /**
     * Test that repeated calls to getting the local host are fairly fast, and
     * hence that caching is being used
     *
     * @throws Exception
     * 		if hostname lookups fail
     */
    @Test
    public void testGetLocalHostIsFast() throws Exception {
        String hostname1 = DNS.getDefaultHost(TestDNS.DEFAULT);
        Assert.assertNotNull(hostname1);
        String hostname2 = DNS.getDefaultHost(TestDNS.DEFAULT);
        long t1 = Time.now();
        String hostname3 = DNS.getDefaultHost(TestDNS.DEFAULT);
        long t2 = Time.now();
        Assert.assertEquals(hostname3, hostname2);
        Assert.assertEquals(hostname2, hostname1);
        long interval = t2 - t1;
        Assert.assertTrue("Took too long to determine local host - caching is not working", (interval < 20000));
    }

    /**
     * Test that our local IP address is not null
     *
     * @throws Exception
     * 		if something went wrong
     */
    @Test
    public void testLocalHostHasAnAddress() throws Exception {
        Assert.assertNotNull(getLocalIPAddr());
    }

    /**
     * Test null interface name
     */
    @Test
    public void testNullInterface() throws Exception {
        String host = DNS.getDefaultHost(null);// should work.

        Assert.assertThat(host, Is.is(DNS.getDefaultHost(TestDNS.DEFAULT)));
        try {
            String ip = DNS.getDefaultIP(null);
            Assert.fail(("Expected a NullPointerException, got " + ip));
        } catch (NullPointerException npe) {
            // Expected
        }
    }

    /**
     * Test that 'null' DNS server gives the same result as if no DNS
     * server was passed.
     */
    @Test
    public void testNullDnsServer() throws Exception {
        String host = DNS.getDefaultHost(getLoopbackInterface(), null);
        Assert.assertThat(host, Is.is(DNS.getDefaultHost(getLoopbackInterface())));
    }

    /**
     * Test that "default" DNS server gives the same result as if no DNS
     * server was passed.
     */
    @Test
    public void testDefaultDnsServer() throws Exception {
        String host = DNS.getDefaultHost(getLoopbackInterface(), TestDNS.DEFAULT);
        Assert.assertThat(host, Is.is(DNS.getDefaultHost(getLoopbackInterface())));
    }

    /**
     * Get the IP addresses of an unknown interface
     */
    @Test
    public void testIPsOfUnknownInterface() throws Exception {
        try {
            DNS.getIPs("name-of-an-unknown-interface");
            Assert.fail("Got an IP for a bogus interface");
        } catch (UnknownHostException e) {
            Assert.assertEquals("No such interface name-of-an-unknown-interface", e.getMessage());
        }
    }

    /**
     * Test the "default" IP addresses is the local IP addr
     */
    @Test
    public void testGetIPWithDefault() throws Exception {
        String[] ips = DNS.getIPs(TestDNS.DEFAULT);
        Assert.assertEquals("Should only return 1 default IP", 1, ips.length);
        Assert.assertEquals(getLocalIPAddr().getHostAddress(), ips[0].toString());
        String ip = DNS.getDefaultIP(TestDNS.DEFAULT);
        Assert.assertEquals(ip, ips[0].toString());
    }

    /**
     * TestCase: get our local address and reverse look it up
     */
    @Test
    public void testRDNS() throws Exception {
        InetAddress localhost = getLocalIPAddr();
        try {
            String s = DNS.reverseDns(localhost, null);
            TestDNS.LOG.info(("Local reverse DNS hostname is " + s));
        } catch (NameNotFoundException | CommunicationException e) {
            if ((!(localhost.isLinkLocalAddress())) || (localhost.isLoopbackAddress())) {
                // these addresses probably won't work with rDNS anyway, unless someone
                // has unusual entries in their DNS server mapping 1.0.0.127 to localhost
                TestDNS.LOG.info("Reverse DNS failing as due to incomplete networking", e);
                TestDNS.LOG.info(((((("Address is " + localhost) + " Loopback=") + (localhost.isLoopbackAddress())) + " Linklocal=") + (localhost.isLinkLocalAddress())));
            }
        }
    }

    /**
     * Test that when using an invalid DNS server with hosts file fallback,
     * we are able to get the hostname from the hosts file.
     *
     * This test may fail on some misconfigured test machines that don't have
     * an entry for "localhost" in their hosts file. This entry is correctly
     * configured out of the box on common Linux distributions and OS X.
     *
     * Windows refuses to resolve 127.0.0.1 to "localhost" despite the presence of
     * this entry in the hosts file.  We skip the test on Windows to avoid
     * reporting a spurious failure.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLookupWithHostsFallback() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        final String oldHostname = changeDnsCachedHostname(TestDNS.DUMMY_HOSTNAME);
        try {
            String hostname = DNS.getDefaultHost(getLoopbackInterface(), TestDNS.INVALID_DNS_SERVER, true);
            // Expect to get back something other than the cached host name.
            Assert.assertThat(hostname, CoreMatchers.not(TestDNS.DUMMY_HOSTNAME));
        } finally {
            // Restore DNS#cachedHostname for subsequent tests.
            changeDnsCachedHostname(oldHostname);
        }
    }

    /**
     * Test that when using an invalid DNS server without hosts file
     * fallback, we get back the cached host name.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLookupWithoutHostsFallback() throws Exception {
        final String oldHostname = changeDnsCachedHostname(TestDNS.DUMMY_HOSTNAME);
        try {
            String hostname = DNS.getDefaultHost(getLoopbackInterface(), TestDNS.INVALID_DNS_SERVER, false);
            // Expect to get back the cached host name since there was no hosts
            // file lookup.
            Assert.assertThat(hostname, Is.is(TestDNS.DUMMY_HOSTNAME));
        } finally {
            // Restore DNS#cachedHostname for subsequent tests.
            changeDnsCachedHostname(oldHostname);
        }
    }

    /**
     * Test that the name "localhost" resolves to something.
     *
     * If this fails, your machine's network is in a mess, go edit /etc/hosts
     */
    @Test
    public void testLocalhostResolves() throws Exception {
        InetAddress localhost = InetAddress.getByName("localhost");
        Assert.assertNotNull("localhost is null", localhost);
        TestDNS.LOG.info(("Localhost IPAddr is " + (localhost.toString())));
    }
}

