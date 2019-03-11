/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.endpoint.dns;


import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.client.Endpoint;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class DnsServiceEndpointGroupTest {
    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void srv() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", SRV), new DefaultDnsResponse(0).addRecord(ANSWER, DnsServiceEndpointGroupTest.newSrvRecord("foo.com.", 1, 2, "a.foo.com.")).addRecord(ANSWER, DnsServiceEndpointGroupTest.newSrvRecord("foo.com.", 3, 4, "b.foo.com.")).addRecord(ANSWER, DnsServiceEndpointGroupTest.newSrvRecord("unrelated.com.", 0, 0, "asdf.com.")).addRecord(ANSWER, DnsServiceEndpointGroupTest.newTooShortSrvRecord("foo.com.")).addRecord(ANSWER, DnsServiceEndpointGroupTest.newBadNameSrvRecord("foo.com."))))) {
            try (DnsServiceEndpointGroup group = new DnsServiceEndpointGroupBuilder("foo.com").serverAddresses(server.addr()).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("a.foo.com", 2).withWeight(1), Endpoint.of("b.foo.com", 4).withWeight(3));
            }
        }
    }

    @Test
    public void cname() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("bar.com.", SRV), new DefaultDnsResponse(0).addRecord(ANSWER, DnsServiceEndpointGroupTest.newCnameRecord("bar.com.", "baz.com.")).addRecord(ANSWER, DnsServiceEndpointGroupTest.newSrvRecord("baz.com.", 5, 6, "c.baz.com."))))) {
            try (DnsServiceEndpointGroup group = new DnsServiceEndpointGroupBuilder("bar.com").serverAddresses(server.addr()).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("c.baz.com", 6).withWeight(5));
            }
        }
    }

    @Test
    public void noPort() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("no-port.com.", SRV), new DefaultDnsResponse(0).addRecord(ANSWER, DnsServiceEndpointGroupTest.newSrvRecord("no-port.com.", 7, 0, "d.no-port.com"))))) {
            try (DnsServiceEndpointGroup group = new DnsServiceEndpointGroupBuilder("no-port.com").serverAddresses(server.addr()).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("d.no-port.com"));
            }
        }
    }
}

