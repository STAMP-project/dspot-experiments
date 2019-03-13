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
package org.graylog.plugins.netflow.codecs;


import NetFlowV9FieldType.ValueType.IPV4;
import NetFlowV9FieldType.ValueType.UINT16;
import NetFlowV9FieldType.ValueType.UINT32;
import NetFlowV9FieldType.ValueType.UINT64;
import NetFlowV9FieldType.ValueType.UINT8;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.graylog.plugins.netflow.v9.NetFlowV9BaseRecord;
import org.graylog.plugins.netflow.v9.NetFlowV9FieldDef;
import org.graylog.plugins.netflow.v9.NetFlowV9FieldType;
import org.graylog.plugins.netflow.v9.NetFlowV9Packet;
import org.graylog.plugins.netflow.v9.NetFlowV9Record;
import org.graylog.plugins.netflow.v9.NetFlowV9Template;
import org.graylog2.plugin.Message;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class NetflowV9CodecAggregatorTest {
    private NetFlowCodec codec;

    private NetflowV9CodecAggregator codecAggregator;

    private InetSocketAddress source;

    public NetflowV9CodecAggregatorTest() throws IOException {
        source = new InetSocketAddress(InetAddress.getLocalHost(), 12345);
    }

    @Test
    public void pcap_netgraph_NetFlowV5() throws Exception {
        final Collection<Message> allMessages = decodePcapStream("netflow-data/netgraph-netflow5.pcap");
        assertThat(allMessages).hasSize(120).allSatisfy(( message) -> assertThat(message.getField("nf_version")).isEqualTo(5));
    }

    @Test
    public void pcap_nprobe_NetFlowV9_mixed() throws Exception {
        final Collection<Message> allMessages = decodePcapStream("netflow-data/nprobe-netflow9.pcap");
        assertThat(allMessages).hasSize(152);
    }

    @Test
    public void pcap_softflowd_NetFlowV5() throws Exception {
        final Collection<Message> allMessages = decodePcapStream("netflow-data/netflow5.pcap");
        assertThat(allMessages).hasSize(4).allSatisfy(( message) -> assertThat(message.getField("nf_version")).isEqualTo(5));
    }

    @Test
    public void pcap_softflowd_NetFlowV9() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/netflow9.pcap");
        packets.forEach(( packet) -> {
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allTemplates).contains(NetFlowV9Template.create(1024, 13, ImmutableList.<NetFlowV9FieldDef>builder().add(NetFlowV9FieldDef.create(NetFlowV9FieldType.create(8, IPV4, "ipv4_src_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(12, IPV4, "ipv4_dst_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(21, UINT32, "last_switched"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(22, UINT32, "first_switched"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(1, UINT32, "in_bytes"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(2, UINT32, "in_pkts"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(10, UINT16, "input_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(14, UINT16, "output_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(7, UINT16, "l4_src_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(11, UINT16, "l4_dst_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(4, UINT8, "protocol"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(6, UINT8, "tcp_flags"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(60, UINT8, "ip_protocol_version"), 1)).build()));
        assertThat(allRecords).hasSize(22).contains(NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("ipv4_src_addr", "8.8.8.8").put("ipv4_dst_addr", "192.168.1.20").put("last_switched", 208442L).put("first_switched", 208442L).put("in_bytes", 76L).put("in_pkts", 1L).put("input_snmp", 0L).put("output_snmp", 0L).put("l4_src_port", 53).put("l4_dst_port", 34865).put("protocol", ((short) (17))).put("tcp_flags", ((short) (0))).put("ip_protocol_version", ((short) (4L))).build()));
    }

    @Test
    public void pcap_pmacctd_NetFlowV5() throws Exception {
        final Collection<Message> allMessages = decodePcapStream("netflow-data/pmacctd-netflow5.pcap");
        assertThat(allMessages).hasSize(42).allSatisfy(( message) -> assertThat(message.getField("nf_version")).isEqualTo(5));
    }

    @Test
    public void pcap_pmacctd_NetFlowV9() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/pmacctd-netflow9.pcap");
        packets.forEach(( packet) -> {
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allTemplates).contains(NetFlowV9Template.create(1024, 10, ImmutableList.<NetFlowV9FieldDef>builder().add(NetFlowV9FieldDef.create(NetFlowV9FieldType.create(153, UINT64, "flow_end_msec"), 8), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(152, UINT64, "flow_start_msec"), 8), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(1, UINT32, "in_bytes"), 8), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(2, UINT32, "in_pkts"), 8), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(60, UINT8, "ip_protocol_version"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(10, UINT16, "input_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(14, UINT16, "output_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(61, UINT8, "direction"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(8, IPV4, "ipv4_src_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(12, IPV4, "ipv4_dst_addr"), 4)).build()));
        assertThat(allRecords).hasSize(6).contains(NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("flow_end_msec", 1501508283491L).put("flow_start_msec", 1501508283473L).put("in_bytes", 68L).put("in_pkts", 1L).put("ip_protocol_version", ((short) (4))).put("input_snmp", 0L).put("output_snmp", 0L).put("direction", ((short) (0))).put("ipv4_src_addr", "172.17.0.2").put("ipv4_dst_addr", "8.8.4.4").build()));
    }

    @Test
    public void pcap_nprobe_NetFlowV9_2() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/nprobe-netflow9-2.pcap");
        packets.forEach(( packet) -> {
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allTemplates).contains(NetFlowV9Template.create(257, 18, ImmutableList.<NetFlowV9FieldDef>builder().add(NetFlowV9FieldDef.create(NetFlowV9FieldType.create(1, UINT32, "in_bytes"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(2, UINT32, "in_pkts"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(4, UINT8, "protocol"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(5, UINT8, "src_tos"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(6, UINT8, "tcp_flags"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(7, UINT16, "l4_src_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(8, IPV4, "ipv4_src_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(9, UINT8, "src_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(10, UINT16, "input_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(11, UINT16, "l4_dst_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(12, IPV4, "ipv4_dst_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(13, UINT8, "dst_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(14, UINT16, "output_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(15, IPV4, "ipv4_next_hop"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(16, UINT16, "src_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(17, UINT16, "dst_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(21, UINT32, "last_switched"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(22, UINT32, "first_switched"), 4)).build()));
        assertThat(allRecords).hasSize(7).contains(NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("in_bytes", 375L).put("in_pkts", 7L).put("ipv4_src_addr", "172.17.0.2").put("ipv4_dst_addr", "93.184.216.34").put("ipv4_next_hop", "0.0.0.0").put("l4_src_port", 43296).put("l4_dst_port", 80).put("protocol", ((short) (6))).put("src_tos", ((short) (0))).put("tcp_flags", ((short) (27))).put("src_mask", ((short) (0))).put("dst_mask", ((short) (0))).put("input_snmp", 0L).put("output_snmp", 0L).put("src_as", 0L).put("dst_as", 15133L).put("first_switched", 3L).put("last_switched", 413L).build()), NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("in_bytes", 1829L).put("in_pkts", 6L).put("ipv4_src_addr", "93.184.216.34").put("ipv4_dst_addr", "172.17.0.2").put("ipv4_next_hop", "0.0.0.0").put("l4_src_port", 80).put("l4_dst_port", 43296).put("protocol", ((short) (6))).put("src_tos", ((short) (0))).put("tcp_flags", ((short) (27))).put("src_mask", ((short) (0))).put("dst_mask", ((short) (0))).put("input_snmp", 0L).put("output_snmp", 0L).put("src_as", 15133L).put("dst_as", 0L).put("first_switched", 138L).put("last_switched", 413L).build()), NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("in_bytes", 68L).put("in_pkts", 1L).put("ipv4_src_addr", "172.17.0.2").put("ipv4_dst_addr", "8.8.4.4").put("ipv4_next_hop", "0.0.0.0").put("l4_src_port", 60546).put("l4_dst_port", 53).put("protocol", ((short) (17))).put("src_tos", ((short) (0))).put("tcp_flags", ((short) (0))).put("src_mask", ((short) (0))).put("dst_mask", ((short) (0))).put("input_snmp", 0L).put("output_snmp", 0L).put("src_as", 0L).put("dst_as", 15169L).put("first_switched", 284L).put("last_switched", 284L).build()), NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("in_bytes", 84L).put("in_pkts", 1L).put("ipv4_src_addr", "8.8.4.4").put("ipv4_dst_addr", "172.17.0.2").put("ipv4_next_hop", "0.0.0.0").put("l4_src_port", 53).put("l4_dst_port", 60546).put("protocol", ((short) (17))).put("src_tos", ((short) (0))).put("tcp_flags", ((short) (0))).put("src_mask", ((short) (0))).put("dst_mask", ((short) (0))).put("input_snmp", 0L).put("output_snmp", 0L).put("src_as", 15169L).put("dst_as", 0L).put("first_switched", 321L).put("last_switched", 321L).build()));
    }

    @Test
    public void pcap_nprobe_NetFlowV9_3() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/nprobe-netflow9-3.pcap");
        packets.forEach(( packet) -> {
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allTemplates).contains(NetFlowV9Template.create(257, 18, ImmutableList.<NetFlowV9FieldDef>builder().add(NetFlowV9FieldDef.create(NetFlowV9FieldType.create(1, UINT32, "in_bytes"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(2, UINT32, "in_pkts"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(4, UINT8, "protocol"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(5, UINT8, "src_tos"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(6, UINT8, "tcp_flags"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(7, UINT16, "l4_src_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(8, IPV4, "ipv4_src_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(9, UINT8, "src_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(10, UINT16, "input_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(11, UINT16, "l4_dst_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(12, IPV4, "ipv4_dst_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(13, UINT8, "dst_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(14, UINT16, "output_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(15, IPV4, "ipv4_next_hop"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(16, UINT16, "src_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(17, UINT16, "dst_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(21, UINT32, "last_switched"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(22, UINT32, "first_switched"), 4)).build()));
        assertThat(allRecords).hasSize(898);
    }

    @Test
    public void pcap_nprobe_NetFlowV9_4() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/nprobe-netflow9-4.pcap");
        packets.forEach(( packet) -> {
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allTemplates).contains(NetFlowV9Template.create(257, 18, ImmutableList.<NetFlowV9FieldDef>builder().add(NetFlowV9FieldDef.create(NetFlowV9FieldType.create(1, UINT32, "in_bytes"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(2, UINT32, "in_pkts"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(4, UINT8, "protocol"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(5, UINT8, "src_tos"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(6, UINT8, "tcp_flags"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(7, UINT16, "l4_src_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(8, IPV4, "ipv4_src_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(9, UINT8, "src_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(10, UINT16, "input_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(11, UINT16, "l4_dst_port"), 2), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(12, IPV4, "ipv4_dst_addr"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(13, UINT8, "dst_mask"), 1), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(14, UINT16, "output_snmp"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(15, IPV4, "ipv4_next_hop"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(16, UINT16, "src_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(17, UINT16, "dst_as"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(21, UINT32, "last_switched"), 4), NetFlowV9FieldDef.create(NetFlowV9FieldType.create(22, UINT32, "first_switched"), 4)).build()));
        assertThat(allRecords).hasSize(16);
    }

    @Test
    public void pcap_fortinet_NetFlowV9() throws Exception {
        final List<NetFlowV9BaseRecord> allRecords = new ArrayList<>();
        final List<NetFlowV9Template> allTemplates = new ArrayList<>();
        final Collection<NetFlowV9Packet> packets = parseNetflowPcapStream("netflow-data/fgt300d-netflow9.pcap");
        packets.forEach(( packet) -> {
            List<NetFlowV9BaseRecord> recs = packet.records();
            allRecords.addAll(packet.records());
            allTemplates.addAll(packet.templates());
        });
        assertThat(allRecords).hasSize(146);
        assertThat(allTemplates).hasSize(12);
        NetFlowV9BaseRecord foo = allRecords.iterator().next();
        assertThat(allRecords).contains(NetFlowV9Record.create(ImmutableMap.<String, Object>builder().put("in_bytes", 371L).put("out_bytes", 371L).put("in_pkts", 2L).put("out_pkts", 2L).put("ipv4_src_addr", "98.158.128.103").put("ipv4_dst_addr", "172.30.1.154").put("l4_src_port", 32161).put("l4_dst_port", 38461).put("protocol", ((short) (17))).put("field_65", 3141).put("forwarding_status", ((short) (64))).put("flow_end_reason", ((short) (2))).put("input_snmp", 5).put("output_snmp", 15).put("first_switched", 2056606986L).put("last_switched", 2056787066L).put("xlate_src_addr_ipv4", "0.0.0.0").put("xlate_dst_addr_ipv4", "139.60.168.65").put("xlate_src_port", 0).put("xlate_dst_port", 38461).build()));
    }

    @Test
    public void decodeMessagesSuccessfullyDecodesNetFlowV5() throws Exception {
        final Collection<Message> messages = decodeResult(aggregateRawPacket("netflow-data/netflow-v5-1.dat"));
        assertThat(messages).isNotNull().hasSize(2);
        final Message message = Iterables.get(messages, 0);
        assertThat(message).isNotNull();
        assertThat(message.getMessage()).isEqualTo("NetFlowV5 [10.0.2.2]:54435 <> [10.0.2.15]:22 proto:6 pkts:5 bytes:230");
        assertThat(message.getTimestamp()).isEqualTo(DateTime.parse("2015-05-02T18:38:08.280Z"));
        assertThat(message.getSource()).isEqualTo(source.getAddress().getHostAddress());
        assertThat(message.getFields()).containsEntry("nf_src_address", "10.0.2.2").containsEntry("nf_dst_address", "10.0.2.15").containsEntry("nf_proto_name", "TCP").containsEntry("nf_src_as", 0).containsEntry("nf_dst_as", 0).containsEntry("nf_snmp_input", 0).containsEntry("nf_snmp_output", 0);
    }

    @Test
    public void decodeMessagesSuccessfullyDecodesNetFlowV9() throws Exception {
        final Collection<Message> messages1 = decodeResult(aggregateRawPacket("netflow-data/netflow-v9-2-1.dat"));
        final Collection<Message> messages2 = decodeResult(aggregateRawPacket("netflow-data/netflow-v9-2-2.dat"));
        final Collection<Message> messages3 = decodeResult(aggregateRawPacket("netflow-data/netflow-v9-2-3.dat"));
        assertThat(messages1).isEmpty();
        assertThat(messages2).isNotNull().hasSize(1);
        final Message message2 = Iterables.getFirst(messages2, null);
        assertThat(message2).isNotNull();
        assertThat(message2.getMessage()).isEqualTo("NetFlowV9 [192.168.124.1]:3072 <> [239.255.255.250]:1900 proto:17 pkts:8 bytes:2818");
        assertThat(message2.getTimestamp()).isEqualTo(DateTime.parse("2013-05-21T07:51:49.000Z"));
        assertThat(message2.getSource()).isEqualTo(source.getAddress().getHostAddress());
        assertThat(message2.getFields()).containsEntry("nf_src_address", "192.168.124.1").containsEntry("nf_dst_address", "239.255.255.250").containsEntry("nf_proto_name", "UDP").containsEntry("nf_src_as", 0L).containsEntry("nf_dst_as", 0L).containsEntry("nf_snmp_input", 0).containsEntry("nf_snmp_output", 0);
        assertThat(messages3).isNotNull().hasSize(1);
        final Message message3 = Iterables.getFirst(messages3, null);
        assertThat(message3).isNotNull();
        assertThat(message3.getMessage()).isEqualTo("NetFlowV9 [192.168.124.20]:42444 <> [121.161.231.32]:9090 proto:17 pkts:2 bytes:348");
        assertThat(message3.getTimestamp()).isEqualTo(DateTime.parse("2013-05-21T07:52:43.000Z"));
        assertThat(message3.getSource()).isEqualTo(source.getAddress().getHostAddress());
        assertThat(message3.getFields()).containsEntry("nf_src_address", "192.168.124.20").containsEntry("nf_dst_address", "121.161.231.32").containsEntry("nf_proto_name", "UDP").containsEntry("nf_src_as", 0L).containsEntry("nf_dst_as", 0L).containsEntry("nf_snmp_input", 0).containsEntry("nf_snmp_output", 0);
    }
}

