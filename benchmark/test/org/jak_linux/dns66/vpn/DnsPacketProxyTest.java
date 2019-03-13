package org.jak_linux.dns66.vpn;


import AdVpnThread.VpnNetworkException;
import IpNumber.UDP;
import IpVersion.IPV4;
import IpVersion.IPV6;
import Section.ANSWER;
import Section.AUTHORITY;
import TcpPacket.Builder;
import TcpPort.HTTP;
import UdpPort.DOMAIN;
import android.content.Context;
import android.util.Log;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import org.jak_linux.dns66.db.RuleDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.IpV6SimpleFlowLabel;
import org.pcap4j.packet.IpV6SimpleTrafficClass;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.UnknownPacket;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;


/**
 * Various tests for the core DNS packet proxying code.
 */
// TODO: 19/03/17 Check for correct point of error
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class DnsPacketProxyTest {
    private DnsPacketProxyTest.MockEventLoop mockEventLoop;

    private DnsPacketProxy dnsPacketProxy;

    private RuleDatabase ruleDatabase;

    @Test
    public void testInitialize() throws Exception {
        ArrayList<InetAddress> dnsServers = new ArrayList<>();
        dnsPacketProxy = new DnsPacketProxy(mockEventLoop, Mockito.mock(RuleDatabase.class));
        dnsPacketProxy.initialize(Mockito.mock(Context.class), dnsServers);
        Assert.assertSame(dnsServers, dnsPacketProxy.upstreamDnsServers);
    }

    @Test
    public void testHandleDnsRequestNotIpPacket() throws Exception {
        dnsPacketProxy.handleDnsRequest(new byte[]{ 'f', 'o', 'o' });
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertNull(mockEventLoop.lastResponse);
    }

    @Test
    public void testHandleDnsRequestNotUdpPacket() throws Exception {
        TcpPacket.Builder payLoadBuilder = new TcpPacket.Builder().srcPort(HTTP).dstPort(HTTP).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(new byte[0]));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertNull(mockEventLoop.lastResponse);
    }

    @Test
    public void testHandleDnsRequestNotDnsPacket() throws Exception {
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(UdpPort.HTTP).dstPort(UdpPort.HTTP).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(new byte[]{ 1, 2, 3, 4, 5 }));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertNull(mockEventLoop.lastResponse);
    }

    @Test
    public void testHandleDnsRequestEmptyPacket() throws Exception {
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(DOMAIN).dstPort(DOMAIN).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(new byte[0]));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNotNull(mockEventLoop.lastOutgoing);
        Assert.assertEquals(0, mockEventLoop.lastOutgoing.getLength());
        Assert.assertEquals(Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 }), mockEventLoop.lastOutgoing.getAddress());
        Assert.assertNull(mockEventLoop.lastResponse);
        // Check the same thing with one upstream DNS server configured.
        tinySetUp();
        dnsPacketProxy.upstreamDnsServers.add(Inet4Address.getByAddress(new byte[]{ 1, 1, 1, 2 }));
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertNull(mockEventLoop.lastResponse);
        // Check the same thing with enough upstream DNS servers configured.
        tinySetUp();
        for (byte i = 2; i < 9; i++)
            dnsPacketProxy.upstreamDnsServers.add(Inet4Address.getByAddress(new byte[]{ 1, 1, 1, i }));

        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNotNull(mockEventLoop.lastOutgoing);
        Assert.assertEquals(0, mockEventLoop.lastOutgoing.getLength());
        Assert.assertEquals(Inet4Address.getByAddress(new byte[]{ 1, 1, 1, 8 }), mockEventLoop.lastOutgoing.getAddress());
        Assert.assertNull(mockEventLoop.lastResponse);
    }

    @Test
    public void testDnsQuery() throws Exception {
        Message message = Message.newQuery(new org.xbill.DNS.ARecord(new Name("notblocked.example.com."), 1, 3600, Inet4Address.getByAddress(new byte[]{ 0, 0, 0, 0 })));
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(DOMAIN).dstPort(DOMAIN).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(message.toWire()));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNull(mockEventLoop.lastResponse);
        Assert.assertNotNull(mockEventLoop.lastOutgoing);
        Assert.assertEquals(Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 }), mockEventLoop.lastOutgoing.getAddress());
    }

    @Test
    public void testNoQueryDnsQuery() throws Exception {
        Message message = new Message();
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(DOMAIN).dstPort(DOMAIN).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(message.toWire()));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNull(mockEventLoop.lastResponse);
        Assert.assertNull(mockEventLoop.lastOutgoing);
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
    }

    @Test
    public void testBlockedDnsQuery() throws Exception {
        Message message = Message.newQuery(new org.xbill.DNS.ARecord(new Name("blocked.example.com."), 1, 3600, Inet4Address.getByAddress(new byte[]{ 0, 0, 0, 0 })));
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(DOMAIN).dstPort(DOMAIN).srcAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 4, 4 })).dstAddr(InetAddress.getByAddress(new byte[]{ 8, 8, 8, 8 })).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(message.toWire()));
        IpPacket ipOutPacket = new IpV4Packet.Builder().version(IPV4).tos(IpV4Rfc791Tos.newInstance(((byte) (0)))).protocol(UDP).srcAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 4, 4 })))).dstAddr(((Inet4Address) (Inet4Address.getByAddress(new byte[]{ 8, 8, 8, 8 })))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNotNull(mockEventLoop.lastResponse);
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertTrue(((mockEventLoop.lastResponse) instanceof IpPacket));
        Assert.assertTrue(((mockEventLoop.lastResponse.getPayload()) instanceof UdpPacket));
        Message responseMsg = new Message(mockEventLoop.lastResponse.getPayload().getPayload().getRawData());
        Assert.assertEquals(NOERROR, responseMsg.getHeader().getRcode());
        Assert.assertArrayEquals(new Record[]{  }, responseMsg.getSectionArray(ANSWER));
        Assert.assertNotEquals(0, responseMsg.getSectionArray(AUTHORITY).length);
        Assert.assertTrue(((responseMsg.getSectionArray(AUTHORITY)[0]) instanceof SOARecord));
        Assert.assertTrue(((responseMsg.getSectionArray(AUTHORITY)[0].getTTL()) > 0));
    }

    @Test
    public void testBlockedInet6DnsQuery() throws Exception {
        Message message = Message.newQuery(new org.xbill.DNS.ARecord(new Name("blocked.example.com."), 1, 3600, Inet4Address.getByAddress(new byte[]{ 0, 0, 0, 0 })));
        UdpPacket.Builder payLoadBuilder = new UdpPacket.Builder().srcPort(DOMAIN).dstPort(DOMAIN).srcAddr(((Inet6Address) (Inet6Address.getByName("::0")))).dstAddr(((Inet6Address) (Inet6Address.getByName("::1")))).correctChecksumAtBuild(true).correctLengthAtBuild(true).payloadBuilder(new UnknownPacket.Builder().rawData(message.toWire()));
        IpPacket ipOutPacket = new IpV6Packet.Builder().version(IPV6).trafficClass(IpV6SimpleTrafficClass.newInstance(((byte) (0)))).flowLabel(IpV6SimpleFlowLabel.newInstance(0)).nextHeader(UDP).srcAddr(((Inet6Address) (Inet6Address.getByName("::0")))).dstAddr(((Inet6Address) (Inet6Address.getByName("::1")))).correctLengthAtBuild(true).payloadBuilder(payLoadBuilder).build();
        dnsPacketProxy.handleDnsRequest(ipOutPacket.getRawData());
        Assert.assertNotNull(mockEventLoop.lastResponse);
        Assert.assertNull(mockEventLoop.lastOutgoing);
        Assert.assertTrue(((mockEventLoop.lastResponse) instanceof IpPacket));
        Assert.assertTrue(((mockEventLoop.lastResponse.getPayload()) instanceof UdpPacket));
        Message responseMsg = new Message(mockEventLoop.lastResponse.getPayload().getPayload().getRawData());
        Assert.assertEquals(NOERROR, responseMsg.getHeader().getRcode());
        Assert.assertArrayEquals(new Record[]{  }, responseMsg.getSectionArray(ANSWER));
        Assert.assertNotEquals(0, responseMsg.getSectionArray(AUTHORITY).length);
        Assert.assertTrue(((responseMsg.getSectionArray(AUTHORITY)[0]) instanceof SOARecord));
        Assert.assertTrue(((responseMsg.getSectionArray(AUTHORITY)[0].getTTL()) > 0));
    }

    private static class MockEventLoop implements DnsPacketProxy.EventLoop {
        DatagramPacket lastOutgoing;

        IpPacket lastResponse;

        @Override
        public void forwardPacket(DatagramPacket packet, IpPacket requestPacket) throws VpnNetworkException {
            lastOutgoing = packet;
        }

        @Override
        public void queueDeviceWrite(IpPacket packet) {
            lastResponse = packet;
        }
    }
}

