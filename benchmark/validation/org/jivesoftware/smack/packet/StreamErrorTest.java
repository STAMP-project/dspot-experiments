/**
 * Copyright the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.packet;


import Condition.conflict;
import Condition.not_well_formed;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;


public class StreamErrorTest {
    @Test
    public void testParsingOfSimpleStreamError() {
        StreamError error = null;
        // Usually the stream:stream element has more attributes (to, version, ...)
        // We omit those, since they are not relevant for testing
        final String xml = "<stream:stream from='im.example.com' id='++TR84Sm6A3hnt3Q065SnAbbk3Y=' xmlns:stream='http://etherx.jabber.org/streams'>" + ((("<stream:error>" + "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-streams' /> +") + "</stream:error>") + "</stream:stream>");
        try {
            XmlPullParser parser = PacketParserUtils.getParserFor(xml, "error");
            error = PacketParserUtils.parseStreamError(parser);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(conflict, error.getCondition());
    }

    @Test
    public void testParsingOfStreamErrorWithText() {
        StreamError error = null;
        // Usually the stream:stream element has more attributes (to, version, ...)
        // We omit those, since they are not relevant for testing
        final String xml = "<stream:stream from='im.example.com' id='++TR84Sm6A3hnt3Q065SnAbbk3Y=' xmlns:stream='http://etherx.jabber.org/streams'>" + (((((("<stream:error>" + "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-streams' />") + "<text xml:lang='' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>") + "Replaced by new connection") + "</text>") + "</stream:error>") + "</stream:stream>");
        try {
            XmlPullParser parser = PacketParserUtils.getParserFor(xml, "error");
            error = PacketParserUtils.parseStreamError(parser);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(conflict, error.getCondition());
        Assert.assertEquals("Replaced by new connection", error.getDescriptiveText());
    }

    @Test
    public void testParsingOfStreamErrorWithTextAndOptionalElement() {
        StreamError error = null;
        // Usually the stream:stream element has more attributes (to, version, ...)
        // We omit those, since they are not relevant for testing
        final String xml = "<stream:stream from='im.example.com' id='++TR84Sm6A3hnt3Q065SnAbbk3Y=' xmlns:stream='http://etherx.jabber.org/streams'>" + ((((((((("<stream:error>" + "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-streams' />") + "<text xml:lang='' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>") + "Replaced by new connection") + "</text>") + "<appSpecificElement xmlns='myns'>") + "Text contents of application-specific condition element: Foo Bar") + "</appSpecificElement>") + "</stream:error>") + "</stream:stream>");
        try {
            XmlPullParser parser = PacketParserUtils.getParserFor(xml, "error");
            error = PacketParserUtils.parseStreamError(parser);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(conflict, error.getCondition());
        Assert.assertEquals("Replaced by new connection", error.getDescriptiveText());
        ExtensionElement appSpecificElement = error.getExtension("appSpecificElement", "myns");
        Assert.assertNotNull(appSpecificElement);
    }

    @Test
    public void testStreamErrorXmlNotWellFormed() {
        StreamError error = null;
        // Usually the stream:stream element has more attributes (to, version, ...)
        // We omit those, since they are not relevant for testing
        final String xml = "<stream:stream from='im.example.com' id='++TR84Sm6A3hnt3Q065SnAbbk3Y=' xmlns:stream='http://etherx.jabber.org/streams'>" + ("<stream:error><xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>" + "</stream:stream>");
        try {
            XmlPullParser parser = PacketParserUtils.getParserFor(xml, "error");
            error = PacketParserUtils.parseStreamError(parser);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(not_well_formed, error.getCondition());
    }
}

