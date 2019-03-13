/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.pcap;


import org.apache.drill.exec.store.pcap.decoder.Packet;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Test;


public class TestPcapRecordReader extends BaseTestQuery {
    @Test
    public void testStarQuery() throws Exception {
        runSQLVerifyCount("select * from dfs.`store/pcap/tcp-1.pcap`", 16);
        runSQLVerifyCount("select distinct DST_IP from dfs.`store/pcap/tcp-1.pcap`", 1);
        runSQLVerifyCount("select distinct DsT_IP from dfs.`store/pcap/tcp-1.pcap`", 1);
        runSQLVerifyCount("select distinct dst_ip from dfs.`store/pcap/tcp-1.pcap`", 1);
    }

    @Test
    public void testCountQuery() throws Exception {
        runSQLVerifyCount("select count(*) from dfs.`store/pcap/tcp-1.pcap`", 1);
        runSQLVerifyCount("select count(*) from dfs.`store/pcap/tcp-2.pcap`", 1);
    }

    @Test
    public void testDistinctQuery() throws Exception {
        // omit data field from distinct count for now
        runSQLVerifyCount("select distinct type, network, `timestamp`, src_ip, dst_ip, src_port, dst_port, src_mac_address, dst_mac_address, tcp_session, packet_length from dfs.`store/pcap/tcp-1.pcap`", 1);
    }

    @Test
    public void testFlagFormatting() {
        Assert.assertEquals("NS", Packet.formatFlags(256));
        Assert.assertEquals("CWR", Packet.formatFlags(128));
        Assert.assertEquals("ECE", Packet.formatFlags(64).substring(0, 3));
        Assert.assertEquals("ECE", Packet.formatFlags(66).substring(0, 3));
        Assert.assertEquals("URG", Packet.formatFlags(32));
        Assert.assertEquals("ACK", Packet.formatFlags(16));
        Assert.assertEquals("PSH", Packet.formatFlags(8));
        Assert.assertEquals("RST", Packet.formatFlags(4));
        Assert.assertEquals("SYN", Packet.formatFlags(2));
        Assert.assertEquals("FIN", Packet.formatFlags(1));
        Assert.assertEquals("RST|SYN|FIN", Packet.formatFlags(7));
    }

    @Test
    public void checkFlags() throws Exception {
        runSQLVerifyCount("select tcp_session, tcp_ack, tcp_flags from dfs.`store/pcap/synscan.pcap`", 2011);
    }
}

