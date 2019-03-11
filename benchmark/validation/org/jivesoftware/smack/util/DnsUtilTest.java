/**
 * Copyright 2018 Florian Schmaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.util;


import DNSUtil.XMPP_CLIENT_DNS_SRV_PREFIX;
import DNSUtil.XMPP_SERVER_DNS_SRV_PREFIX;
import org.jivesoftware.smack.util.DNSUtil.DomainType;
import org.jivesoftware.smack.util.dns.SmackDaneProvider;
import org.jivesoftware.smack.util.dns.SmackDaneVerifier;
import org.junit.Assert;
import org.junit.Test;


public class DnsUtilTest {
    @Test
    public void simpleDomainTypeTest() {
        DomainType client = DomainType.client;
        Assert.assertEquals(XMPP_CLIENT_DNS_SRV_PREFIX, client.srvPrefix.ace);
        DomainType server = DomainType.server;
        Assert.assertEquals(XMPP_SERVER_DNS_SRV_PREFIX, server.srvPrefix.ace);
    }

    private static final SmackDaneProvider DNS_UTIL_TEST_DANE_PROVIDER = new SmackDaneProvider() {
        @Override
        public SmackDaneVerifier newInstance() {
            throw new AssertionError();
        }
    };

    @Test
    public void daneProviderTest() {
        DNSUtil.setDaneProvider(DnsUtilTest.DNS_UTIL_TEST_DANE_PROVIDER);
        SmackDaneProvider currentDaneProvider = DNSUtil.getDaneProvider();
        Assert.assertEquals(DnsUtilTest.DNS_UTIL_TEST_DANE_PROVIDER, currentDaneProvider);
    }
}

