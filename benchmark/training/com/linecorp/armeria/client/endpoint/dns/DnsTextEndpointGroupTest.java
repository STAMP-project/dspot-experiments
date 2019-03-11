/**
 * Copyright 2018    LINE Corporation
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


public class DnsTextEndpointGroupTest {
    @Rule
    public final TestRule globalTimeout = new DisableOnDebug(new Timeout(30, TimeUnit.SECONDS));

    @Test
    public void txt() throws Exception {
        try (TestDnsServer server = new TestDnsServer(ImmutableMap.of(new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", TXT), new DefaultDnsResponse(0).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("foo.com.", "endpoint=a.foo.com")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("foo.com.", "endpoint=b.foo.com")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("unrelated.com.", "endpoint=c.com")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTooShortTxtRecord("foo.com.")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTooLongTxtRecord("foo.com.")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("foo.com.", "unrelated_txt")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("foo.com.", "endpoint=group:foo")).addRecord(ANSWER, DnsTextEndpointGroupTest.newTxtRecord("foo.com.", "endpoint=b:a:d"))))) {
            try (DnsTextEndpointGroup group = new DnsTextEndpointGroupBuilder("foo.com", ( txt) -> {
                final String txtStr = new String(txt, StandardCharsets.US_ASCII);
                if (txtStr.startsWith("endpoint=")) {
                    return Endpoint.parse(txtStr.substring(9));
                } else {
                    return null;
                }
            }).serverAddresses(server.addr()).build()) {
                assertThat(group.awaitInitialEndpoints()).containsExactly(Endpoint.of("a.foo.com"), Endpoint.of("b.foo.com"));
            }
        }
    }
}

