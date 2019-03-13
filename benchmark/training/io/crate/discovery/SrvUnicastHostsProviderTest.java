/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.discovery;


import SrvUnicastHostsProvider.DISCOVERY_SRV_RESOLVER;
import io.crate.test.integration.CrateUnitTest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.threadpool.ThreadPool;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.junit.Test;


public class SrvUnicastHostsProviderTest extends CrateUnitTest {
    private ThreadPool threadPool;

    private SrvUnicastHostsProvider srvUnicastHostsProvider;

    private AnyOf<String> isLocalHost;

    @Test
    public void testParseAddressNoPort() {
        Settings settings = Settings.builder().put(DISCOVERY_SRV_RESOLVER.getKey(), "127.0.0.1").build();
        InetSocketAddress address = srvUnicastHostsProvider.parseResolverAddress(settings);
        assertThat(address.getHostName(), isLocalHost);
        assertThat(address.getPort(), Matchers.is(53));
    }

    @Test
    public void testParseAddressValidPort() {
        Settings settings = Settings.builder().put(DISCOVERY_SRV_RESOLVER.getKey(), "127.0.0.1:1234").build();
        InetSocketAddress address = srvUnicastHostsProvider.parseResolverAddress(settings);
        assertThat(address.getHostName(), isLocalHost);
        assertThat(address.getPort(), Matchers.is(1234));
    }

    @Test
    public void testParseAddressPortOutOfRange() {
        Settings settings = Settings.builder().put(DISCOVERY_SRV_RESOLVER.getKey(), "127.0.0.1:1234567").build();
        InetSocketAddress address = srvUnicastHostsProvider.parseResolverAddress(settings);
        assertThat(address.getHostName(), isLocalHost);
        assertThat(address.getPort(), Matchers.is(53));
    }

    @Test
    public void testParseAddressPortNoInteger() {
        Settings settings = Settings.builder().put(DISCOVERY_SRV_RESOLVER.getKey(), "127.0.0.1:foo").build();
        InetSocketAddress address = srvUnicastHostsProvider.parseResolverAddress(settings);
        assertThat(address.getHostName(), isLocalHost);
        assertThat(address.getPort(), Matchers.is(53));
    }

    @Test
    public void testParseRecords() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(0);// priority

        buf.writeShort(0);// weight

        buf.writeShort(993);// port

        encodeName("localhost.", buf);
        DnsRecord record = new io.netty.handler.codec.dns.DefaultDnsRawRecord("_myprotocol._tcp.crate.io.", DnsRecordType.SRV, 30, buf);
        List<TransportAddress> addresses = srvUnicastHostsProvider.parseRecords(Collections.singletonList(record));
        assertThat(addresses.get(0).getAddress(), Matchers.is("127.0.0.1"));
        assertThat(addresses.get(0).getPort(), Matchers.is(993));
    }
}

