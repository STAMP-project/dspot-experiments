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
package org.graylog.plugins.netflow.v9;


import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.graylog.plugins.netflow.flows.EmptyTemplateException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class NetFlowV9ParserTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private NetFlowV9FieldTypeRegistry typeRegistry;

    @Test
    public void testParse() throws IOException {
        final byte[] b1 = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v9-2-1.dat"));
        final byte[] b2 = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v9-2-2.dat"));
        final byte[] b3 = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v9-2-3.dat"));
        Map<Integer, NetFlowV9Template> cache = Maps.newHashMap();
        // check header
        NetFlowV9Packet p1 = NetFlowV9Parser.parsePacket(Unpooled.wrappedBuffer(b1), typeRegistry, cache, null);
        Assert.assertEquals(9, p1.header().version());
        Assert.assertEquals(3, p1.header().count());
        Assert.assertEquals(0, p1.header().sequence());
        Assert.assertEquals(42212, p1.header().sysUptime());
        Assert.assertEquals(1369122709, p1.header().unixSecs());
        Assert.assertEquals(106, p1.header().sourceId());
        // check templates
        Assert.assertEquals(2, p1.templates().size());
        Assert.assertNotNull(p1.optionTemplate());
        NetFlowV9Template t1 = p1.templates().get(0);
        Assert.assertEquals(257, t1.templateId());
        Assert.assertEquals(18, t1.fieldCount());
        List<NetFlowV9FieldDef> d1 = t1.definitions();
        Assert.assertEquals("in_bytes", name(d1.get(0)));
        Assert.assertEquals("in_pkts", name(d1.get(1)));
        Assert.assertEquals("protocol", name(d1.get(2)));
        Assert.assertEquals("src_tos", name(d1.get(3)));
        Assert.assertEquals("tcp_flags", name(d1.get(4)));
        Assert.assertEquals("l4_src_port", name(d1.get(5)));
        Assert.assertEquals("ipv4_src_addr", name(d1.get(6)));
        Assert.assertEquals("src_mask", name(d1.get(7)));
        Assert.assertEquals("input_snmp", name(d1.get(8)));
        Assert.assertEquals("l4_dst_port", name(d1.get(9)));
        Assert.assertEquals("ipv4_dst_addr", name(d1.get(10)));
        Assert.assertEquals("dst_mask", name(d1.get(11)));
        Assert.assertEquals("output_snmp", name(d1.get(12)));
        Assert.assertEquals("ipv4_next_hop", name(d1.get(13)));
        Assert.assertEquals("src_as", name(d1.get(14)));
        Assert.assertEquals("dst_as", name(d1.get(15)));
        Assert.assertEquals("last_switched", name(d1.get(16)));
        Assert.assertEquals("first_switched", name(d1.get(17)));
        NetFlowV9Template t2 = p1.templates().get(1);
        Assert.assertEquals(258, t2.templateId());
        Assert.assertEquals(18, t2.fieldCount());
        NetFlowV9Packet p2 = NetFlowV9Parser.parsePacket(Unpooled.wrappedBuffer(b2), typeRegistry, cache, null);
        NetFlowV9BaseRecord r2 = p2.records().get(0);
        Map<String, Object> f2 = r2.fields();
        Assert.assertEquals(2818L, f2.get("in_bytes"));
        Assert.assertEquals(8L, f2.get("in_pkts"));
        Assert.assertEquals("192.168.124.1", f2.get("ipv4_src_addr"));
        Assert.assertEquals("239.255.255.250", f2.get("ipv4_dst_addr"));
        Assert.assertEquals(3072, f2.get("l4_src_port"));
        Assert.assertEquals(1900, f2.get("l4_dst_port"));
        Assert.assertEquals(((short) (17)), f2.get("protocol"));
        NetFlowV9Packet p3 = NetFlowV9Parser.parsePacket(Unpooled.wrappedBuffer(b3), typeRegistry, cache, null);
        Assert.assertEquals(1, p3.records().size());
    }

    @Test
    public void testParseIncomplete() throws Exception {
        final byte[] b = Resources.toByteArray(Resources.getResource("netflow-data/netflow-v9-3_incomplete.dat"));
        assertThatExceptionOfType(EmptyTemplateException.class).isThrownBy(() -> NetFlowV9Parser.parsePacket(Unpooled.wrappedBuffer(b), typeRegistry));
    }
}

