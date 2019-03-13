/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.dns;


import DnsSection.ADDITIONAL;
import DnsSection.ANSWER;
import DnsSection.AUTHORITY;
import DnsSection.QUESTION;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.SocketUtils;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static DnsRecordType.A;
import static DnsRecordType.AAAA;
import static DnsRecordType.CNAME;
import static DnsRecordType.MX;
import static DnsRecordType.PTR;


public class DnsQueryTest {
    @Test
    public void writeQueryTest() throws Exception {
        InetSocketAddress addr = SocketUtils.socketAddress("8.8.8.8", 53);
        EmbeddedChannel embedder = new EmbeddedChannel(new DatagramDnsQueryEncoder());
        List<DnsQuery> queries = new ArrayList<DnsQuery>(5);
        queries.add(new DatagramDnsQuery(null, addr, 1).setRecord(QUESTION, new DefaultDnsQuestion("1.0.0.127.in-addr.arpa", PTR)));
        queries.add(new DatagramDnsQuery(null, addr, 1).setRecord(QUESTION, new DefaultDnsQuestion("www.example.com", A)));
        queries.add(new DatagramDnsQuery(null, addr, 1).setRecord(QUESTION, new DefaultDnsQuestion("example.com", AAAA)));
        queries.add(new DatagramDnsQuery(null, addr, 1).setRecord(QUESTION, new DefaultDnsQuestion("example.com", MX)));
        queries.add(new DatagramDnsQuery(null, addr, 1).setRecord(QUESTION, new DefaultDnsQuestion("example.com", CNAME)));
        for (DnsQuery query : queries) {
            Assert.assertThat(query.count(QUESTION), Matchers.is(1));
            Assert.assertThat(query.count(ANSWER), Matchers.is(0));
            Assert.assertThat(query.count(AUTHORITY), Matchers.is(0));
            Assert.assertThat(query.count(ADDITIONAL), Matchers.is(0));
            embedder.writeOutbound(query);
            DatagramPacket packet = embedder.readOutbound();
            Assert.assertTrue(packet.content().isReadable());
            packet.release();
            Assert.assertNull(embedder.readOutbound());
        }
    }
}

