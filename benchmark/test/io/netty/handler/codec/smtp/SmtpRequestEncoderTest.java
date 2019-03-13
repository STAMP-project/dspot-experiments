/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.codec.smtp;


import CharsetUtil.US_ASCII;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import org.junit.Assert;
import org.junit.Test;


public class SmtpRequestEncoderTest {
    @Test
    public void testEncodeEhlo() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.ehlo("localhost"), "EHLO localhost\r\n");
    }

    @Test
    public void testEncodeHelo() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.helo("localhost"), "HELO localhost\r\n");
    }

    @Test
    public void testEncodeMail() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.mail("me@netty.io"), "MAIL FROM:<me@netty.io>\r\n");
    }

    @Test
    public void testEncodeMailNullSender() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.mail(null), "MAIL FROM:<>\r\n");
    }

    @Test
    public void testEncodeRcpt() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.rcpt("me@netty.io"), "RCPT TO:<me@netty.io>\r\n");
    }

    @Test
    public void testEncodeNoop() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.noop(), "NOOP\r\n");
    }

    @Test
    public void testEncodeRset() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.rset(), "RSET\r\n");
    }

    @Test
    public void testEncodeHelp() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.help(null), "HELP\r\n");
    }

    @Test
    public void testEncodeHelpWithArg() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.help("MAIL"), "HELP MAIL\r\n");
    }

    @Test
    public void testEncodeData() {
        SmtpRequestEncoderTest.testEncode(SmtpRequests.data(), "DATA\r\n");
    }

    @Test
    public void testEncodeDataAndContent() {
        EmbeddedChannel channel = new EmbeddedChannel(new SmtpRequestEncoder());
        Assert.assertTrue(channel.writeOutbound(SmtpRequests.data()));
        Assert.assertTrue(channel.writeOutbound(new DefaultSmtpContent(Unpooled.copiedBuffer("Subject: Test\r\n\r\n", US_ASCII))));
        Assert.assertTrue(channel.writeOutbound(new DefaultLastSmtpContent(Unpooled.copiedBuffer("Test\r\n", US_ASCII))));
        Assert.assertTrue(channel.finish());
        Assert.assertEquals("DATA\r\nSubject: Test\r\n\r\nTest\r\n.\r\n", SmtpRequestEncoderTest.getWrittenString(channel));
    }

    @Test(expected = EncoderException.class)
    public void testThrowsIfContentExpected() {
        EmbeddedChannel channel = new EmbeddedChannel(new SmtpRequestEncoder());
        Assert.assertTrue(channel.writeOutbound(SmtpRequests.data()));
        channel.writeOutbound(SmtpRequests.noop());
    }

    @Test
    public void testRsetClearsContentExpectedFlag() {
        EmbeddedChannel channel = new EmbeddedChannel(new SmtpRequestEncoder());
        Assert.assertTrue(channel.writeOutbound(SmtpRequests.data()));
        Assert.assertTrue(channel.writeOutbound(SmtpRequests.rset()));
        Assert.assertTrue(channel.writeOutbound(SmtpRequests.noop()));
        Assert.assertTrue(channel.finish());
        Assert.assertEquals("DATA\r\nRSET\r\nNOOP\r\n", SmtpRequestEncoderTest.getWrittenString(channel));
    }
}

