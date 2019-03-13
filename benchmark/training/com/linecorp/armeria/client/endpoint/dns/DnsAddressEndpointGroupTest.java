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


import ResolvedAddressTypes.IPV4_ONLY;
import ResolvedAddressTypes.IPV4_PREFERRED;
import ResolvedAddressTypes.IPV6_ONLY;
import com.google.common.collect.ImmutableMap;
import com.linecorp.armeria.client.Endpoint;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class DnsAddressEndpointGroupTest {
    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void ipV4Only() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("foo.com.", "1.1.1.1")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("unrelated.com", "1.2.3.4"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("foo.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV4_ONLY).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("foo.com", 8080).withIpAddr("1.1.1.1"));
            }
        }
    }

    @Test
    public void ipV6Only() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("bar.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("bar.com.", "::1")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("bar.com.", "::1234:5678:90ab")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("bar.com.", "2404:6800:4004:806::2013"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("bar.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV6_ONLY).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("bar.com", 8080).withIpAddr("2404:6800:4004:806::2013"), Endpoint.of("bar.com", 8080).withIpAddr("::1"), Endpoint.of("bar.com", 8080).withIpAddr("::1234:5678:90ab"));
            }
        }
    }

    @Test
    public void ipV4AndIpV6() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("baz.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("baz.com.", "1.1.1.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("baz.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("baz.com.", "::1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("baz.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("baz.com", 8080).withIpAddr("1.1.1.1"), Endpoint.of("baz.com", 8080).withIpAddr("::1"));
            }
        }
    }

    @Test
    public void platformDefault() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("baz.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("baz.com.", "1.1.1.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("baz.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("baz.com.", "::1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("baz.com").port(8080).serverAddresses(server.addr()).build()) {
                assertThat(group.awaitInitialEndpoints()).contains(Endpoint.of("baz.com", 8080).withIpAddr("1.1.1.1"));
            }
        }
    }

    @Test
    public void cname() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("a.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newBadAddressRecord("a.com.", true)).addRecord(ANSWER, DnsAddressEndpointGroupTest.newCnameRecord("a.com.", "b.com.")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("b.com.", "1.1.1.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("a.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newBadAddressRecord("a.com.", false)).addRecord(ANSWER, DnsAddressEndpointGroupTest.newCnameRecord("a.com.", "b.com.")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("b.com.", "::1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("a.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("a.com", 8080).withIpAddr("1.1.1.1"), Endpoint.of("a.com", 8080).withIpAddr("::1"));
            }
        }
    }

    @Test
    public void mixedLoopbackAddresses() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("foo.com.", "127.0.0.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("foo.com.", "::1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("foo.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("foo.com", 8080).withIpAddr("127.0.0.1"));
            }
        }
    }

    @Test
    public void ipV4MappedOrCompatibleAddresses() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("bar.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newCompatibleAddressRecord("bar.com.", "1.1.1.1")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newCompatibleAddressRecord("bar.com.", "1.1.1.2")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newMappedAddressRecord("bar.com.", "1.1.1.1")).addRecord(ANSWER, DnsAddressEndpointGroupTest.newMappedAddressRecord("bar.com.", "1.1.1.3"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("bar.com").port(8080).serverAddresses(server.addr()).resolvedAddressTypes(IPV6_ONLY).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("bar.com", 8080).withIpAddr("1.1.1.1"), Endpoint.of("bar.com", 8080).withIpAddr("1.1.1.2"), Endpoint.of("bar.com", 8080).withIpAddr("1.1.1.3"));
            }
        }
    }

    @Test
    public void noPort() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("no-port.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("no-port.com", "1.1.1.1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("no-port.com").serverAddresses(server.addr()).resolvedAddressTypes(IPV4_ONLY).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("no-port.com").withIpAddr("1.1.1.1"));
            }
        }
    }

    @Test
    public void backoff() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of())) {
            // Respond nothing.
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("backoff.com").serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).backoff(com.linecorp.armeria.client.retry.Backoff.fixed(500)).build()) {
                await().untilAsserted(() -> assertThat(group.attemptsSoFar).isGreaterThan(2));
                assertThat(group.endpoints()).isEmpty();
                // Start to respond correctly.
                server.setResponses(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("backoff.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("backoff.com", "1.1.1.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("backoff.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("backoff.com", "::1"))));
                await().untilAsserted(() -> assertThat(group.endpoints()).containsExactly(Endpoint.of("backoff.com").withIpAddr("1.1.1.1"), Endpoint.of("backoff.com").withIpAddr("::1")));
            }
        }
    }

    @Test
    public void backoffOnEmptyResponse() throws Exception {
        try (TestDnsServer server = new TestDnsServer(// Respond with empty records.
        ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("empty.com.", A), new DefaultDnsResponse(0), new io.netty.handler.codec.dns.DefaultDnsQuestion("empty.com.", AAAA), new DefaultDnsResponse(0)))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("empty.com").serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).backoff(com.linecorp.armeria.client.retry.Backoff.fixed(500)).build()) {
                await().untilAsserted(() -> assertThat(group.attemptsSoFar).isGreaterThan(2));
                assertThat(group.endpoints()).isEmpty();
                // Start to respond correctly.
                server.setResponses(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("empty.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("empty.com", "1.1.1.1")), new io.netty.handler.codec.dns.DefaultDnsQuestion("empty.com.", AAAA), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("empty.com", "::1"))));
                await().untilAsserted(() -> assertThat(group.endpoints()).containsExactly(Endpoint.of("empty.com").withIpAddr("1.1.1.1"), Endpoint.of("empty.com").withIpAddr("::1")));
            }
        }
    }

    @Test
    public void partialResponse() throws Exception {
        try (TestDnsServer server = new TestDnsServer(// Respond A record only.
        // Respond with NXDOMAIN for AAAA.
        ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("partial.com.", A), new DefaultDnsResponse(0).addRecord(ANSWER, DnsAddressEndpointGroupTest.newAddressRecord("partial.com", "1.1.1.1"))))) {
            try (DnsAddressEndpointGroup group = new DnsAddressEndpointGroupBuilder("partial.com").serverAddresses(server.addr()).resolvedAddressTypes(IPV4_PREFERRED).backoff(com.linecorp.armeria.client.retry.Backoff.fixed(500)).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("partial.com").withIpAddr("1.1.1.1"));
            }
        }
    }
}

