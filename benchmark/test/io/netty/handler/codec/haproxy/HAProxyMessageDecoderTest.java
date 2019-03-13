/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.haproxy;


import AddressFamily.AF_IPv4;
import AddressFamily.AF_IPv6;
import AddressFamily.AF_UNIX;
import AddressFamily.AF_UNSPEC;
import CharsetUtil.US_ASCII;
import HAProxyCommand.LOCAL;
import HAProxyCommand.PROXY;
import HAProxyProtocolVersion.V1;
import HAProxyProtocolVersion.V2;
import HAProxyProxiedProtocol.TCP4;
import HAProxyProxiedProtocol.TCP6;
import HAProxyProxiedProtocol.UDP4;
import HAProxyProxiedProtocol.UDP6;
import HAProxyProxiedProtocol.UNIX_DGRAM;
import HAProxyProxiedProtocol.UNIX_STREAM;
import HAProxyProxiedProtocol.UNKNOWN;
import HAProxyTLV.Type.PP2_TYPE_SSL;
import HAProxyTLV.Type.PP2_TYPE_SSL_CN;
import HAProxyTLV.Type.PP2_TYPE_SSL_VERSION;
import ProtocolDetectionState.DETECTED;
import ProtocolDetectionState.INVALID;
import ProtocolDetectionState.NEEDS_MORE_DATA;
import TransportProtocol.DGRAM;
import TransportProtocol.STREAM;
import TransportProtocol.UNSPEC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ProtocolDetectionResult;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol.AddressFamily;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol.TransportProtocol;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class HAProxyMessageDecoderTest {
    private EmbeddedChannel ch;

    @Test
    public void testIPV4Decode() {
        int startChannels = ch.pipeline().names().size();
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V1, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(TCP4, msg.proxiedProtocol());
        Assert.assertEquals("192.168.0.1", msg.sourceAddress());
        Assert.assertEquals("192.168.0.11", msg.destinationAddress());
        Assert.assertEquals(56324, msg.sourcePort());
        Assert.assertEquals(443, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testIPV6Decode() {
        int startChannels = ch.pipeline().names().size();
        String header = "PROXY TCP6 2001:0db8:85a3:0000:0000:8a2e:0370:7334 1050:0:0:0:5:600:300c:326b 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V1, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(TCP6, msg.proxiedProtocol());
        Assert.assertEquals("2001:0db8:85a3:0000:0000:8a2e:0370:7334", msg.sourceAddress());
        Assert.assertEquals("1050:0:0:0:5:600:300c:326b", msg.destinationAddress());
        Assert.assertEquals(56324, msg.sourcePort());
        Assert.assertEquals(443, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUnknownProtocolDecode() {
        int startChannels = ch.pipeline().names().size();
        String header = "PROXY UNKNOWN 192.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V1, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(UNKNOWN, msg.proxiedProtocol());
        Assert.assertNull(msg.sourceAddress());
        Assert.assertNull(msg.destinationAddress());
        Assert.assertEquals(0, msg.sourcePort());
        Assert.assertEquals(0, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV1NoUDP() {
        String header = "PROXY UDP4 192.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidPort() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 80000 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidIPV4Address() {
        String header = "PROXY TCP4 299.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidIPV6Address() {
        String header = "PROXY TCP6 r001:0db8:85a3:0000:0000:8a2e:0370:7334 1050:0:0:0:5:600:300c:326b 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidProtocol() {
        String header = "PROXY TCP7 192.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testMissingParams() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testTooManyParams() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324 443 123\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidCommand() {
        String header = "PING TCP4 192.168.0.1 192.168.0.11 56324 443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testInvalidEOL() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324 443\nGET / HTTP/1.1\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testHeaderTooLong() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324 " + "00000000000000000000000000000000000000000000000000000000000000000443\r\n";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
    }

    @Test
    public void testIncompleteHeader() {
        String header = "PROXY TCP4 192.168.0.1 192.168.0.11 56324";
        ch.writeInbound(copiedBuffer(header, US_ASCII));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testCloseOnInvalid() {
        ChannelFuture closeFuture = ch.closeFuture();
        String header = "GET / HTTP/1.1\r\n";
        try {
            ch.writeInbound(copiedBuffer(header, US_ASCII));
        } catch (HAProxyProtocolException ppex) {
            // swallow this exception since we're just testing to be sure the channel was closed
        }
        boolean isComplete = closeFuture.awaitUninterruptibly(5000);
        if (((!isComplete) || (!(closeFuture.isDone()))) || (!(closeFuture.isSuccess()))) {
            Assert.fail("Expected channel close");
        }
    }

    @Test
    public void testTransportProtocolAndAddressFamily() {
        final byte unknown = UNKNOWN.byteValue();
        final byte tcp4 = TCP4.byteValue();
        final byte tcp6 = TCP6.byteValue();
        final byte udp4 = UDP4.byteValue();
        final byte udp6 = UDP6.byteValue();
        final byte unix_stream = UNIX_STREAM.byteValue();
        final byte unix_dgram = UNIX_DGRAM.byteValue();
        Assert.assertEquals(UNSPEC, TransportProtocol.valueOf(unknown));
        Assert.assertEquals(STREAM, TransportProtocol.valueOf(tcp4));
        Assert.assertEquals(STREAM, TransportProtocol.valueOf(tcp6));
        Assert.assertEquals(STREAM, TransportProtocol.valueOf(unix_stream));
        Assert.assertEquals(DGRAM, TransportProtocol.valueOf(udp4));
        Assert.assertEquals(DGRAM, TransportProtocol.valueOf(udp6));
        Assert.assertEquals(DGRAM, TransportProtocol.valueOf(unix_dgram));
        Assert.assertEquals(AF_UNSPEC, AddressFamily.valueOf(unknown));
        Assert.assertEquals(AF_IPv4, AddressFamily.valueOf(tcp4));
        Assert.assertEquals(AF_IPv4, AddressFamily.valueOf(udp4));
        Assert.assertEquals(AF_IPv6, AddressFamily.valueOf(tcp6));
        Assert.assertEquals(AF_IPv6, AddressFamily.valueOf(udp6));
        Assert.assertEquals(AF_UNIX, AddressFamily.valueOf(unix_stream));
        Assert.assertEquals(AF_UNIX, AddressFamily.valueOf(unix_dgram));
    }

    @Test
    public void testV2IPV4Decode() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 17;// TCP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(TCP4, msg.proxiedProtocol());
        Assert.assertEquals("192.168.0.1", msg.sourceAddress());
        Assert.assertEquals("192.168.0.11", msg.destinationAddress());
        Assert.assertEquals(56324, msg.sourcePort());
        Assert.assertEquals(443, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testV2UDPDecode() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 18;// UDP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(UDP4, msg.proxiedProtocol());
        Assert.assertEquals("192.168.0.1", msg.sourceAddress());
        Assert.assertEquals("192.168.0.11", msg.destinationAddress());
        Assert.assertEquals(56324, msg.sourcePort());
        Assert.assertEquals(443, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testv2IPV6Decode() {
        byte[] header = new byte[52];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 33;// TCP over IPv6

        header[14] = 0;// Remaining Bytes

        header[15] = 36;// -----

        header[16] = 32;// Source Address

        header[17] = 1;// -----

        header[18] = 13;// -----

        header[19] = ((byte) (184));// -----

        header[20] = ((byte) (133));// -----

        header[21] = ((byte) (163));// -----

        header[22] = 0;// -----

        header[23] = 0;// -----

        header[24] = 0;// -----

        header[25] = 0;// -----

        header[26] = ((byte) (138));// -----

        header[27] = 46;// -----

        header[28] = 3;// -----

        header[29] = 112;// -----

        header[30] = 115;// -----

        header[31] = 52;// -----

        header[32] = 16;// Destination Address

        header[33] = 80;// -----

        header[34] = 0;// -----

        header[35] = 0;// -----

        header[36] = 0;// -----

        header[37] = 0;// -----

        header[38] = 0;// -----

        header[39] = 0;// -----

        header[40] = 0;// -----

        header[41] = 5;// -----

        header[42] = 6;// -----

        header[43] = 0;// -----

        header[44] = 48;// -----

        header[45] = 12;// -----

        header[46] = 50;// -----

        header[47] = 107;// -----

        header[48] = ((byte) (220));// Source Port

        header[49] = 4;// -----

        header[50] = 1;// Destination Port

        header[51] = ((byte) (187));// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(TCP6, msg.proxiedProtocol());
        Assert.assertEquals("2001:db8:85a3:0:0:8a2e:370:7334", msg.sourceAddress());
        Assert.assertEquals("1050:0:0:0:5:600:300c:326b", msg.destinationAddress());
        Assert.assertEquals(56324, msg.sourcePort());
        Assert.assertEquals(443, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testv2UnixDecode() {
        byte[] header = new byte[232];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 49;// UNIX_STREAM

        header[14] = 0;// Remaining Bytes

        header[15] = ((byte) (216));// -----

        header[16] = 47;// Source Address

        header[17] = 118;// -----

        header[18] = 97;// -----

        header[19] = 114;// -----

        header[20] = 47;// -----

        header[21] = 114;// -----

        header[22] = 117;// -----

        header[23] = 110;// -----

        header[24] = 47;// -----

        header[25] = 115;// -----

        header[26] = 114;// -----

        header[27] = 99;// -----

        header[28] = 46;// -----

        header[29] = 115;// -----

        header[30] = 111;// -----

        header[31] = 99;// -----

        header[32] = 107;// -----

        header[33] = 0;// -----

        header[124] = 47;// Destination Address

        header[125] = 118;// -----

        header[126] = 97;// -----

        header[127] = 114;// -----

        header[128] = 47;// -----

        header[129] = 114;// -----

        header[130] = 117;// -----

        header[131] = 110;// -----

        header[132] = 47;// -----

        header[133] = 100;// -----

        header[134] = 101;// -----

        header[135] = 115;// -----

        header[136] = 116;// -----

        header[137] = 46;// -----

        header[138] = 115;// -----

        header[139] = 111;// -----

        header[140] = 99;// -----

        header[141] = 107;// -----

        header[142] = 0;// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(UNIX_STREAM, msg.proxiedProtocol());
        Assert.assertEquals("/var/run/src.sock", msg.sourceAddress());
        Assert.assertEquals("/var/run/dest.sock", msg.destinationAddress());
        Assert.assertEquals(0, msg.sourcePort());
        Assert.assertEquals(0, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testV2LocalProtocolDecode() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 32;// v2, cmd=LOCAL

        header[13] = 0;// Unspecified transport protocol and address family

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(LOCAL, msg.command());
        Assert.assertEquals(UNKNOWN, msg.proxiedProtocol());
        Assert.assertNull(msg.sourceAddress());
        Assert.assertNull(msg.destinationAddress());
        Assert.assertEquals(0, msg.sourcePort());
        Assert.assertEquals(0, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testV2UnknownProtocolDecode() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 0;// Unspecified transport protocol and address family

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(UNKNOWN, msg.proxiedProtocol());
        Assert.assertNull(msg.sourceAddress());
        Assert.assertNull(msg.destinationAddress());
        Assert.assertEquals(0, msg.sourcePort());
        Assert.assertEquals(0, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testV2WithSslTLVs() throws Exception {
        ch = new EmbeddedChannel(new HAProxyMessageDecoder());
        final byte[] bytes = new byte[]{ 13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10, 33, 17, 0, 35, 127, 0, 0, 1, 127, 0, 0, 1, -55, -90, 7, 89, 32, 0, 20, 5, 0, 0, 0, 0, 33, 0, 5, 84, 76, 83, 118, 49, 34, 0, 4, 76, 69, 65, 70 };
        int startChannels = ch.pipeline().names().size();
        Assert.assertTrue(ch.writeInbound(copiedBuffer(bytes)));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(TCP4, msg.proxiedProtocol());
        Assert.assertEquals("127.0.0.1", msg.sourceAddress());
        Assert.assertEquals("127.0.0.1", msg.destinationAddress());
        Assert.assertEquals(51622, msg.sourcePort());
        Assert.assertEquals(1881, msg.destinationPort());
        final List<HAProxyTLV> tlvs = msg.tlvs();
        Assert.assertEquals(3, tlvs.size());
        final HAProxyTLV firstTlv = tlvs.get(0);
        Assert.assertEquals(PP2_TYPE_SSL, firstTlv.type());
        final HAProxySSLTLV sslTlv = ((HAProxySSLTLV) (firstTlv));
        Assert.assertEquals(0, sslTlv.verify());
        Assert.assertTrue(sslTlv.isPP2ClientSSL());
        Assert.assertTrue(sslTlv.isPP2ClientCertSess());
        Assert.assertFalse(sslTlv.isPP2ClientCertConn());
        final HAProxyTLV secondTlv = tlvs.get(1);
        Assert.assertEquals(PP2_TYPE_SSL_VERSION, secondTlv.type());
        ByteBuf secondContentBuf = secondTlv.content();
        byte[] secondContent = new byte[secondContentBuf.readableBytes()];
        secondContentBuf.readBytes(secondContent);
        Assert.assertArrayEquals("TLSv1".getBytes(US_ASCII), secondContent);
        final HAProxyTLV thirdTLV = tlvs.get(2);
        Assert.assertEquals(PP2_TYPE_SSL_CN, thirdTLV.type());
        ByteBuf thirdContentBuf = thirdTLV.content();
        byte[] thirdContent = new byte[thirdContentBuf.readableBytes()];
        thirdContentBuf.readBytes(thirdContent);
        Assert.assertArrayEquals("LEAF".getBytes(US_ASCII), thirdContent);
        Assert.assertTrue(sslTlv.encapsulatedTLVs().contains(secondTlv));
        Assert.assertTrue(sslTlv.encapsulatedTLVs().contains(thirdTLV));
        Assert.assertTrue((0 < (firstTlv.refCnt())));
        Assert.assertTrue((0 < (secondTlv.refCnt())));
        Assert.assertTrue((0 < (thirdTLV.refCnt())));
        Assert.assertFalse(thirdTLV.release());
        Assert.assertFalse(secondTlv.release());
        Assert.assertTrue(firstTlv.release());
        Assert.assertEquals(0, firstTlv.refCnt());
        Assert.assertEquals(0, secondTlv.refCnt());
        Assert.assertEquals(0, thirdTLV.refCnt());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testV2WithTLV() {
        ch = new EmbeddedChannel(new HAProxyMessageDecoder(4));
        byte[] header = new byte[236];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 49;// UNIX_STREAM

        header[14] = 0;// Remaining Bytes

        header[15] = ((byte) (220));// -----

        header[16] = 47;// Source Address

        header[17] = 118;// -----

        header[18] = 97;// -----

        header[19] = 114;// -----

        header[20] = 47;// -----

        header[21] = 114;// -----

        header[22] = 117;// -----

        header[23] = 110;// -----

        header[24] = 47;// -----

        header[25] = 115;// -----

        header[26] = 114;// -----

        header[27] = 99;// -----

        header[28] = 46;// -----

        header[29] = 115;// -----

        header[30] = 111;// -----

        header[31] = 99;// -----

        header[32] = 107;// -----

        header[33] = 0;// -----

        header[124] = 47;// Destination Address

        header[125] = 118;// -----

        header[126] = 97;// -----

        header[127] = 114;// -----

        header[128] = 47;// -----

        header[129] = 114;// -----

        header[130] = 117;// -----

        header[131] = 110;// -----

        header[132] = 47;// -----

        header[133] = 100;// -----

        header[134] = 101;// -----

        header[135] = 115;// -----

        header[136] = 116;// -----

        header[137] = 46;// -----

        header[138] = 115;// -----

        header[139] = 111;// -----

        header[140] = 99;// -----

        header[141] = 107;// -----

        header[142] = 0;// -----

        // ---- Additional data (TLV) ---- \\
        header[232] = 1;// Type

        header[233] = 0;// Remaining bytes

        header[234] = 1;// -----

        header[235] = 1;// Payload

        int startChannels = ch.pipeline().names().size();
        ch.writeInbound(copiedBuffer(header));
        Object msgObj = ch.readInbound();
        Assert.assertEquals((startChannels - 1), ch.pipeline().names().size());
        Assert.assertTrue((msgObj instanceof HAProxyMessage));
        HAProxyMessage msg = ((HAProxyMessage) (msgObj));
        Assert.assertEquals(V2, msg.protocolVersion());
        Assert.assertEquals(PROXY, msg.command());
        Assert.assertEquals(UNIX_STREAM, msg.proxiedProtocol());
        Assert.assertEquals("/var/run/src.sock", msg.sourceAddress());
        Assert.assertEquals("/var/run/dest.sock", msg.destinationAddress());
        Assert.assertEquals(0, msg.sourcePort());
        Assert.assertEquals(0, msg.destinationPort());
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV2InvalidProtocol() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 65;// Bogus transport protocol

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        ch.writeInbound(copiedBuffer(header));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV2MissingParams() {
        byte[] header = new byte[26];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 17;// TCP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = 10;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        ch.writeInbound(copiedBuffer(header));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV2InvalidCommand() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 34;// v2, Bogus command

        header[13] = 17;// TCP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        ch.writeInbound(copiedBuffer(header));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV2InvalidVersion() {
        byte[] header = new byte[28];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 49;// Bogus version, cmd=PROXY

        header[13] = 17;// TCP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = 12;// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        ch.writeInbound(copiedBuffer(header));
    }

    @Test(expected = HAProxyProtocolException.class)
    public void testV2HeaderTooLong() {
        ch = new EmbeddedChannel(new HAProxyMessageDecoder(0));
        byte[] header = new byte[248];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        header[13] = 17;// TCP over IPv4

        header[14] = 0;// Remaining Bytes

        header[15] = ((byte) (232));// -----

        header[16] = ((byte) (192));// Source Address

        header[17] = ((byte) (168));// -----

        header[18] = 0;// -----

        header[19] = 1;// -----

        header[20] = ((byte) (192));// Destination Address

        header[21] = ((byte) (168));// -----

        header[22] = 0;// -----

        header[23] = 11;// -----

        header[24] = ((byte) (220));// Source Port

        header[25] = 4;// -----

        header[26] = 1;// Destination Port

        header[27] = ((byte) (187));// -----

        ch.writeInbound(copiedBuffer(header));
    }

    @Test
    public void testV2IncompleteHeader() {
        byte[] header = new byte[13];
        header[0] = 13;// Binary Prefix

        header[1] = 10;// -----

        header[2] = 13;// -----

        header[3] = 10;// -----

        header[4] = 0;// -----

        header[5] = 13;// -----

        header[6] = 10;// -----

        header[7] = 81;// -----

        header[8] = 85;// -----

        header[9] = 73;// -----

        header[10] = 84;// -----

        header[11] = 10;// -----

        header[12] = 33;// v2, cmd=PROXY

        ch.writeInbound(copiedBuffer(header));
        Assert.assertNull(ch.readInbound());
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDetectProtocol() {
        final ByteBuf validHeaderV1 = copiedBuffer("PROXY TCP4 192.168.0.1 192.168.0.11 56324 443\r\n", US_ASCII);
        ProtocolDetectionResult<HAProxyProtocolVersion> result = HAProxyMessageDecoder.detectProtocol(validHeaderV1);
        Assert.assertEquals(DETECTED, result.state());
        Assert.assertEquals(V1, result.detectedProtocol());
        validHeaderV1.release();
        final ByteBuf invalidHeader = copiedBuffer("Invalid header", US_ASCII);
        result = HAProxyMessageDecoder.detectProtocol(invalidHeader);
        Assert.assertEquals(INVALID, result.state());
        Assert.assertNull(result.detectedProtocol());
        invalidHeader.release();
        final ByteBuf validHeaderV2 = buffer();
        validHeaderV2.writeByte(13);
        validHeaderV2.writeByte(10);
        validHeaderV2.writeByte(13);
        validHeaderV2.writeByte(10);
        validHeaderV2.writeByte(0);
        validHeaderV2.writeByte(13);
        validHeaderV2.writeByte(10);
        validHeaderV2.writeByte(81);
        validHeaderV2.writeByte(85);
        validHeaderV2.writeByte(73);
        validHeaderV2.writeByte(84);
        validHeaderV2.writeByte(10);
        result = HAProxyMessageDecoder.detectProtocol(validHeaderV2);
        Assert.assertEquals(DETECTED, result.state());
        Assert.assertEquals(V2, result.detectedProtocol());
        validHeaderV2.release();
        final ByteBuf incompleteHeader = buffer();
        incompleteHeader.writeByte(13);
        incompleteHeader.writeByte(10);
        incompleteHeader.writeByte(13);
        incompleteHeader.writeByte(10);
        incompleteHeader.writeByte(0);
        incompleteHeader.writeByte(13);
        incompleteHeader.writeByte(10);
        result = HAProxyMessageDecoder.detectProtocol(incompleteHeader);
        Assert.assertEquals(NEEDS_MORE_DATA, result.state());
        Assert.assertNull(result.detectedProtocol());
        incompleteHeader.release();
    }
}

