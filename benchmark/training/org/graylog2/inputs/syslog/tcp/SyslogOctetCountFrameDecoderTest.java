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
package org.graylog2.inputs.syslog.tcp;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class SyslogOctetCountFrameDecoderTest {
    private EmbeddedChannel channel;

    @Test
    public void testDecode() throws Exception {
        final ByteBuf buf1 = Unpooled.copiedBuffer("123 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", StandardCharsets.US_ASCII);
        final ByteBuf buf2 = Unpooled.copiedBuffer("186 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"2\"] Syslog connection established; fd=\'9\', server=\'AF_INET(172.17.42.1:6666)\', local=\'AF_INET(0.0.0.0:0)\'\n", StandardCharsets.US_ASCII);
        final ByteBuf buf3 = Unpooled.copiedBuffer(buf1, buf2, buf1);
        Assert.assertTrue(channel.writeInbound(buf1, buf2, buf3));
        final ByteBuf actual1 = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual1.toString(StandardCharsets.US_ASCII));
        final ByteBuf actual2 = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"2\"] Syslog connection established; fd=\'9\', server=\'AF_INET(172.17.42.1:6666)\', local=\'AF_INET(0.0.0.0:0)\'\n", actual2.toString(StandardCharsets.US_ASCII));
        final ByteBuf actual3 = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual3.toString(StandardCharsets.US_ASCII));
        final ByteBuf actual4 = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"2\"] Syslog connection established; fd=\'9\', server=\'AF_INET(172.17.42.1:6666)\', local=\'AF_INET(0.0.0.0:0)\'\n", actual4.toString(StandardCharsets.US_ASCII));
        final ByteBuf actual5 = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual5.toString(StandardCharsets.US_ASCII));
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testIncompleteFrameLengthValue() throws Exception {
        final ByteBuf buf1 = Unpooled.copiedBuffer("12", StandardCharsets.US_ASCII);
        final ByteBuf buf2 = Unpooled.copiedBuffer("3 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", StandardCharsets.US_ASCII);
        Assert.assertFalse(channel.writeInbound(buf1));
        Assert.assertNull(channel.readInbound());
        Assert.assertTrue(channel.writeInbound(buf2));
        final ByteBuf actual = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual.toString(StandardCharsets.US_ASCII));
    }

    @Test
    public void testIncompleteFrames() throws Exception {
        final ByteBuf buf1 = Unpooled.copiedBuffer("123 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - ", StandardCharsets.US_ASCII);
        final ByteBuf buf2 = Unpooled.copiedBuffer("[meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", StandardCharsets.US_ASCII);
        Assert.assertFalse(channel.writeInbound(buf1));
        Assert.assertNull(channel.readInbound());
        Assert.assertTrue(channel.writeInbound(buf2));
        final ByteBuf actual = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual.toString(StandardCharsets.US_ASCII));
    }

    @Test
    public void testIncompleteByteBufByteBufFramesAndSmallBuffer() throws Exception {
        /* This test has been added to reproduce this issue: https://github.com/Graylog2/graylog2-server/issues/1105

        It triggers an edge case where the buffer is missing <frame size value length + 1> bytes.
        The SyslogOctetCountFrameDecoder was handling this wrong in previous versions and tried to read more from
        the buffer than there was available after the frame size value bytes have been skipped.
         */
        final ByteBuf messagePart1 = Unpooled.copiedBuffer("123 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.", StandardCharsets.US_ASCII);
        final ByteBuf messagePart2 = Unpooled.copiedBuffer("3\'\n", StandardCharsets.US_ASCII);
        Assert.assertFalse(channel.writeInbound(messagePart1));
        Assert.assertNull(channel.readInbound());
        Assert.assertTrue(channel.writeInbound(messagePart2));
        final ByteBuf actual = channel.readInbound();
        Assert.assertEquals("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - [meta sequenceId=\"1\"] syslog-ng starting up; version=\'3.5.3\'\n", actual.toString(StandardCharsets.US_ASCII));
    }

    @Test
    public void testBrokenFrames() throws Exception {
        final ByteBuf buf = Unpooled.copiedBuffer("1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - ", StandardCharsets.US_ASCII);
        try {
            channel.writeInbound(buf);
            Assert.fail("Expected DecoderException");
        } catch (DecoderException e) {
            Assert.assertTrue(((e.getCause()) instanceof NumberFormatException));
            channel.close().sync().await();
        }
    }

    @Test
    public void testDecodeSupportsMessagesLongerThan1024Bytes() throws Exception {
        // All transport receiver
        // implementations SHOULD be able to accept messages of up to and
        // including 2048 octets in length.  Transport receivers MAY receive
        // messages larger than 2048 octets in length.
        // -- https://tools.ietf.org/html/rfc5424#section-6.1
        final byte[] bytes = new byte[2048];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) ('A' + (i % 26)));
        }
        final String longString = new String(bytes, StandardCharsets.US_ASCII);
        final ByteBuf buffer = Unpooled.copiedBuffer((("2111 <45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - " + longString) + "\n"), StandardCharsets.US_ASCII);
        Assert.assertTrue(channel.writeInbound(buffer));
        channel.finish();
        final ByteBuf actual = channel.readInbound();
        Assert.assertEquals((("<45>1 2014-10-21T10:21:09+00:00 c4dc57ba1ebb syslog-ng 7120 - " + longString) + "\n"), actual.toString(StandardCharsets.US_ASCII));
        Assert.assertNull(channel.readInbound());
    }
}

