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
package org.graylog.plugins.netflow.v5;


import com.google.common.io.Resources;
import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.pkts.Pcap;
import io.pkts.packet.UDPPacket;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NetFlowV5ParserTest {
    @Test
    public void testParse1() throws IOException {
        final byte[] b = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v5-1.dat"));
        NetFlowV5Packet packet = NetFlowV5Parser.parsePacket(Unpooled.wrappedBuffer(b));
        Assert.assertNotNull(packet);
        NetFlowV5Header h = packet.header();
        Assert.assertEquals(5, h.version());
        Assert.assertEquals(2, h.count());
        Assert.assertEquals(3381L, h.sysUptime());
        Assert.assertEquals(1430591888L, h.unixSecs());
        Assert.assertEquals(280328000, h.unixNsecs());
        final List<NetFlowV5Record> records = packet.records();
        Assert.assertEquals(2, records.size());
        final NetFlowV5Record record1 = records.get(0);
        Assert.assertEquals(InetAddresses.forString("10.0.2.15"), record1.dstAddr());
        Assert.assertEquals(6, record1.protocol());
        Assert.assertEquals(0, record1.srcAs());
        Assert.assertEquals(InetAddresses.forString("10.0.2.2"), record1.srcAddr());
        Assert.assertEquals(2577L, record1.last());
        Assert.assertEquals(22, record1.dstPort());
        Assert.assertEquals(230L, record1.octetCount());
        Assert.assertEquals(54435, record1.srcPort());
        Assert.assertEquals(0, record1.srcMask());
        Assert.assertEquals(0, record1.tos());
        Assert.assertEquals(0, record1.inputIface());
        Assert.assertEquals(InetAddresses.forString("0.0.0.0"), record1.nextHop());
        Assert.assertEquals(16, record1.tcpFlags());
        Assert.assertEquals(0, record1.dstAs());
        Assert.assertEquals(0, record1.outputIface());
        Assert.assertEquals(4294967295L, record1.first());
        Assert.assertEquals(0, record1.dstMask());
        Assert.assertEquals(5L, record1.packetCount());
        final NetFlowV5Record record2 = records.get(1);
        Assert.assertEquals(InetAddresses.forString("10.0.2.2"), record2.dstAddr());
        Assert.assertEquals(6, record2.protocol());
        Assert.assertEquals(0, record2.srcAs());
        Assert.assertEquals(InetAddresses.forString("10.0.2.15"), record2.srcAddr());
        Assert.assertEquals(2577L, record2.last());
        Assert.assertEquals(54435, record2.dstPort());
        Assert.assertEquals(304L, record2.octetCount());
        Assert.assertEquals(22, record2.srcPort());
        Assert.assertEquals(0, record2.srcMask());
        Assert.assertEquals(0, record2.tos());
        Assert.assertEquals(0, record2.inputIface());
        Assert.assertEquals(InetAddresses.forString("0.0.0.0"), record2.nextHop());
        Assert.assertEquals(24, record2.tcpFlags());
        Assert.assertEquals(0, record2.dstAs());
        Assert.assertEquals(0, record2.outputIface());
        Assert.assertEquals(4294967295L, record2.first());
        Assert.assertEquals(0, record2.dstMask());
        Assert.assertEquals(4L, record2.packetCount());
    }

    @Test
    public void testParse2() throws IOException {
        final byte[] b = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v5-2.dat"));
        NetFlowV5Packet packet = NetFlowV5Parser.parsePacket(Unpooled.wrappedBuffer(b));
        Assert.assertNotNull(packet);
        NetFlowV5Header h = packet.header();
        Assert.assertEquals(5, h.version());
        Assert.assertEquals(30, h.count());
        Assert.assertEquals(234994, h.sysUptime());
        Assert.assertEquals(1369017138, h.unixSecs());
        Assert.assertEquals(805, h.unixNsecs());
        Assert.assertEquals(30, packet.records().size());
        final NetFlowV5Record r = packet.records().get(0);
        Assert.assertEquals(InetAddresses.forString("192.168.124.20"), r.dstAddr());
        Assert.assertEquals(6, r.protocol());
        Assert.assertEquals(0, r.srcAs());
        Assert.assertEquals(InetAddresses.forString("14.63.211.15"), r.srcAddr());
        Assert.assertEquals(202992L, r.last());
        Assert.assertEquals(47994, r.dstPort());
        Assert.assertEquals(317221L, r.octetCount());
        Assert.assertEquals(80, r.srcPort());
        Assert.assertEquals(0, r.srcMask());
        Assert.assertEquals(0, r.tos());
        Assert.assertEquals(0, r.inputIface());
        Assert.assertEquals(InetAddresses.forString("0.0.0.0"), r.nextHop());
        Assert.assertEquals(27, r.tcpFlags());
        Assert.assertEquals(0, r.dstAs());
        Assert.assertEquals(0, r.outputIface());
        Assert.assertEquals(202473L, r.first());
        Assert.assertEquals(0, r.dstMask());
        Assert.assertEquals(110L, r.packetCount());
    }

    @Test
    public void pcap_softflowd_NetFlowV5() throws Exception {
        final List<NetFlowV5Record> allRecords = new ArrayList<>();
        try (InputStream inputStream = Resources.getResource("netflow-data/netflow5.pcap").openStream()) {
            final Pcap pcap = Pcap.openStream(inputStream);
            pcap.loop(( packet) -> {
                if (packet.hasProtocol(Protocol.UDP)) {
                    final UDPPacket udp = ((UDPPacket) (packet.getPacket(Protocol.UDP)));
                    final ByteBuf byteBuf = Unpooled.wrappedBuffer(udp.getPayload().getArray());
                    final NetFlowV5Packet netFlowV5Packet = NetFlowV5Parser.parsePacket(byteBuf);
                    assertThat(netFlowV5Packet).isNotNull();
                    allRecords.addAll(netFlowV5Packet.records());
                }
                return true;
            });
        }
        assertThat(allRecords).hasSize(4);
    }

    @Test
    public void pcap_pmacctd_NetFlowV5() throws Exception {
        final List<NetFlowV5Record> allRecords = new ArrayList<>();
        try (InputStream inputStream = Resources.getResource("netflow-data/pmacctd-netflow5.pcap").openStream()) {
            final Pcap pcap = Pcap.openStream(inputStream);
            pcap.loop(( packet) -> {
                if (packet.hasProtocol(Protocol.UDP)) {
                    final UDPPacket udp = ((UDPPacket) (packet.getPacket(Protocol.UDP)));
                    final ByteBuf byteBuf = Unpooled.wrappedBuffer(udp.getPayload().getArray());
                    final NetFlowV5Packet netFlowV5Packet = NetFlowV5Parser.parsePacket(byteBuf);
                    assertThat(netFlowV5Packet).isNotNull();
                    allRecords.addAll(netFlowV5Packet.records());
                }
                return true;
            });
        }
        assertThat(allRecords).hasSize(42);
    }

    @Test
    public void pcap_netgraph_NetFlowV5() throws Exception {
        final List<NetFlowV5Record> allRecords = new ArrayList<>();
        try (InputStream inputStream = Resources.getResource("netflow-data/netgraph-netflow5.pcap").openStream()) {
            final Pcap pcap = Pcap.openStream(inputStream);
            pcap.loop(( packet) -> {
                if (packet.hasProtocol(Protocol.UDP)) {
                    final UDPPacket udp = ((UDPPacket) (packet.getPacket(Protocol.UDP)));
                    final ByteBuf byteBuf = Unpooled.wrappedBuffer(udp.getPayload().getArray());
                    final NetFlowV5Packet netFlowV5Packet = NetFlowV5Parser.parsePacket(byteBuf);
                    assertThat(netFlowV5Packet).isNotNull();
                    allRecords.addAll(netFlowV5Packet.records());
                }
                return true;
            });
        }
        assertThat(allRecords).hasSize(120);
    }
}

