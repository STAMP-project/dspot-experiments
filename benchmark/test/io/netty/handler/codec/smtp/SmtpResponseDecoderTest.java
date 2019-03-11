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


import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SmtpResponseDecoderTest {
    @Test
    public void testDecodeOneLineResponse() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("200 Ok\r\n")));
        Assert.assertTrue(channel.finish());
        SmtpResponse response = channel.readInbound();
        Assert.assertEquals(200, response.code());
        List<CharSequence> sequences = response.details();
        Assert.assertEquals(1, sequences.size());
        Assert.assertEquals("Ok", sequences.get(0).toString());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testDecodeOneLineResponseNoDetails() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("250 \r\n")));
        Assert.assertTrue(channel.finish());
        SmtpResponse response = channel.readInbound();
        Assert.assertEquals(250, response.code());
        List<CharSequence> sequences = response.details();
        Assert.assertEquals(0, sequences.size());
    }

    @Test
    public void testDecodeOneLineResponseChunked() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertFalse(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("200 Ok")));
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("\r\n")));
        Assert.assertTrue(channel.finish());
        SmtpResponse response = channel.readInbound();
        Assert.assertEquals(200, response.code());
        List<CharSequence> sequences = response.details();
        Assert.assertEquals(1, sequences.size());
        Assert.assertEquals("Ok", sequences.get(0).toString());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testDecodeTwoLineResponse() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("200-Hello\r\n200 Ok\r\n")));
        Assert.assertTrue(channel.finish());
        SmtpResponse response = channel.readInbound();
        Assert.assertEquals(200, response.code());
        List<CharSequence> sequences = response.details();
        Assert.assertEquals(2, sequences.size());
        Assert.assertEquals("Hello", sequences.get(0).toString());
        Assert.assertEquals("Ok", sequences.get(1).toString());
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testDecodeTwoLineResponseChunked() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertFalse(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("200-")));
        Assert.assertFalse(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("Hello\r\n2")));
        Assert.assertFalse(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("00 Ok")));
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("\r\n")));
        Assert.assertTrue(channel.finish());
        SmtpResponse response = channel.readInbound();
        Assert.assertEquals(200, response.code());
        List<CharSequence> sequences = response.details();
        Assert.assertEquals(2, sequences.size());
        Assert.assertEquals("Hello", sequences.get(0).toString());
        Assert.assertEquals("Ok", sequences.get(1).toString());
        Assert.assertNull(channel.readInbound());
    }

    @Test(expected = DecoderException.class)
    public void testDecodeInvalidSeparator() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("200:Ok\r\n")));
    }

    @Test(expected = DecoderException.class)
    public void testDecodeInvalidCode() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("xyz Ok\r\n")));
    }

    @Test(expected = DecoderException.class)
    public void testDecodeInvalidLine() {
        EmbeddedChannel channel = SmtpResponseDecoderTest.newChannel();
        Assert.assertTrue(channel.writeInbound(SmtpResponseDecoderTest.newBuffer("Ok\r\n")));
    }
}

