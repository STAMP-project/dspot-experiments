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
package io.netty.handler.codec.xml;


import CharsetUtil.UTF_8;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class XmlFrameDecoderTest {
    private final List<String> xmlSamples;

    public XmlFrameDecoderTest() throws IOException, URISyntaxException {
        xmlSamples = Arrays.asList(sample("01"), sample("02"), sample("03"), sample("04"), sample("05"), sample("06"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithIllegalArgs01() {
        new XmlFrameDecoder(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithIllegalArgs02() {
        new XmlFrameDecoder((-23));
    }

    @Test(expected = TooLongFrameException.class)
    public void testDecodeWithFrameExceedingMaxLength() {
        XmlFrameDecoder decoder = new XmlFrameDecoder(3);
        EmbeddedChannel ch = new EmbeddedChannel(decoder);
        ch.writeInbound(Unpooled.copiedBuffer("<v/>", UTF_8));
    }

    @Test(expected = CorruptedFrameException.class)
    public void testDecodeWithInvalidInput() {
        XmlFrameDecoder decoder = new XmlFrameDecoder(1048576);
        EmbeddedChannel ch = new EmbeddedChannel(decoder);
        ch.writeInbound(Unpooled.copiedBuffer("invalid XML", UTF_8));
    }

    @Test(expected = CorruptedFrameException.class)
    public void testDecodeWithInvalidContentBeforeXml() {
        XmlFrameDecoder decoder = new XmlFrameDecoder(1048576);
        EmbeddedChannel ch = new EmbeddedChannel(decoder);
        ch.writeInbound(Unpooled.copiedBuffer("invalid XML<foo/>", UTF_8));
    }

    @Test
    public void testDecodeShortValidXml() {
        XmlFrameDecoderTest.testDecodeWithXml("<xxx/>", "<xxx/>");
    }

    @Test
    public void testDecodeShortValidXmlWithLeadingWhitespace01() {
        XmlFrameDecoderTest.testDecodeWithXml("   <xxx/>", "<xxx/>");
    }

    @Test
    public void testDecodeShortValidXmlWithLeadingWhitespace02() {
        XmlFrameDecoderTest.testDecodeWithXml("  \n\r \t<xxx/>\t", "<xxx/>");
    }

    @Test
    public void testDecodeShortValidXmlWithLeadingWhitespace02AndTrailingGarbage() {
        XmlFrameDecoderTest.testDecodeWithXml("  \n\r \t<xxx/>\ttrash", "<xxx/>", CorruptedFrameException.class);
    }

    @Test
    public void testDecodeInvalidXml() {
        XmlFrameDecoderTest.testDecodeWithXml("<a></", new Object[0]);
        XmlFrameDecoderTest.testDecodeWithXml("<a></a", new Object[0]);
    }

    @Test
    public void testDecodeWithCDATABlock() {
        final String xml = "<book>" + ("<![CDATA[K&R, a.k.a. Kernighan & Ritchie]]>" + "</book>");
        XmlFrameDecoderTest.testDecodeWithXml(xml, xml);
    }

    @Test
    public void testDecodeWithCDATABlockContainingNestedUnbalancedXml() {
        // <br> isn't closed, also <a> should have been </a>
        final String xml = "<info>" + ("<![CDATA[Copyright 2012-2013,<br><a href=\"http://www.acme.com\">ACME Inc.<a>]]>" + "</info>");
        XmlFrameDecoderTest.testDecodeWithXml(xml, xml);
    }

    @Test
    public void testDecodeWithMultipleMessages() {
        final String input = "<root xmlns=\"http://www.acme.com/acme\" status=\"loginok\" " + (((("timestamp=\"1362410583776\"/>\n\n" + "<root xmlns=\"http://www.acme.com/acme\" status=\"start\" time=\"0\" ") + "timestamp=\"1362410584794\">\n<child active=\"1\" status=\"started\" id=\"935449\" ") + "msgnr=\"2\"/>\n</root>") + "<root xmlns=\"http://www.acme.com/acme\" status=\"logout\" timestamp=\"1362410584795\"/>");
        final String frame1 = "<root xmlns=\"http://www.acme.com/acme\" status=\"loginok\" " + "timestamp=\"1362410583776\"/>";
        final String frame2 = "<root xmlns=\"http://www.acme.com/acme\" status=\"start\" time=\"0\" " + ("timestamp=\"1362410584794\">\n<child active=\"1\" status=\"started\" id=\"935449\" " + "msgnr=\"2\"/>\n</root>");
        final String frame3 = "<root xmlns=\"http://www.acme.com/acme\" status=\"logout\" " + "timestamp=\"1362410584795\"/>";
        XmlFrameDecoderTest.testDecodeWithXml(input, frame1, frame2, frame3);
    }

    @Test
    public void testFraming() {
        XmlFrameDecoderTest.testDecodeWithXml(Arrays.asList("<abc", ">123</a", "bc>"), "<abc>123</abc>");
    }

    @Test
    public void testDecodeWithSampleXml() {
        for (final String xmlSample : xmlSamples) {
            XmlFrameDecoderTest.testDecodeWithXml(xmlSample, xmlSample);
        }
    }
}

