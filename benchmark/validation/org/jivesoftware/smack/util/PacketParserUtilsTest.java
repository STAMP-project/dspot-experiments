/**
 * Copyright (C) 2007 Jive Software.
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
package org.jivesoftware.smack.util;


import Presence.Type.unsubscribed;
import SASLError.account_disabled;
import SASLFailure.ELEMENT;
import SaslStreamElements.NAMESPACE;
import StanzaError.Condition.internal_server_error;
import StanzaError.ERROR;
import StanzaError.ERROR_CONDITION_AND_TEXT_NAMESPACE;
import StreamOpen.CLIENT_NAMESPACE;
import com.jamesmurty.utils.XMLBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.SASLFailure;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smack.test.util.XmlUnitUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class PacketParserUtilsTest {
    private static Properties outputProperties = new Properties();

    {
        PacketParserUtilsTest.outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    @Test
    public void singleMessageBodyTest() throws Exception {
        String defaultLanguage = Stanza.getDefaultLanguage();
        String otherLanguage = PacketParserUtilsTest.determineNonDefaultLanguage();
        String control;
        // message has default language, body has no language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("body").t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        Message message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getBody());
        Assert.assertTrue(message.getBodyLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getBody(defaultLanguage));
        Assert.assertNull(message.getBody(otherLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has non-default language, body has no language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("body").t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(otherLanguage, message.getBody());
        Assert.assertTrue(message.getBodyLanguages().isEmpty());
        Assert.assertEquals(otherLanguage, message.getBody(otherLanguage));
        Assert.assertNull(message.getBody(defaultLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has no language, body has no language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("body").t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getBody());
        Assert.assertTrue(message.getBodyLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getBody(null));
        Assert.assertNull(message.getBody(otherLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has no language, body has default language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("body").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getBody());
        Assert.assertFalse(message.getBodyLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getBody(defaultLanguage));
        Assert.assertNull(message.getBody(otherLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has no language, body has non-default language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("body").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getBody());
        Assert.assertFalse(message.getBodyLanguages().isEmpty());
        Assert.assertTrue(message.getBodyLanguages().contains(otherLanguage));
        Assert.assertEquals(otherLanguage, message.getBody(otherLanguage));
        Assert.assertNull(message.getBody(defaultLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has default language, body has non-default language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("body").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getBody());
        Assert.assertFalse(message.getBodyLanguages().isEmpty());
        Assert.assertTrue(message.getBodyLanguages().contains(otherLanguage));
        Assert.assertEquals(otherLanguage, message.getBody(otherLanguage));
        Assert.assertNull(message.getBody(defaultLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has non-default language, body has default language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("body").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getBody());
        Assert.assertFalse(message.getBodyLanguages().isEmpty());
        Assert.assertTrue(message.getBodyLanguages().contains(defaultLanguage));
        Assert.assertEquals(defaultLanguage, message.getBody(defaultLanguage));
        Assert.assertNull(message.getBody(otherLanguage));
        assertXMLEqual(control, message.toXML().toString());
    }

    @Test
    public void singleMessageSubjectTest() throws Exception {
        String defaultLanguage = Stanza.getDefaultLanguage();
        String otherLanguage = PacketParserUtilsTest.determineNonDefaultLanguage();
        String control;
        // message has default language, subject has no language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("subject").t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        Message message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getSubject());
        Assert.assertTrue(message.getSubjectLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        Assert.assertNull(message.getSubject(otherLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has non-default language, subject has no language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("subject").t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(otherLanguage, message.getSubject());
        Assert.assertTrue(message.getSubjectLanguages().isEmpty());
        Assert.assertEquals(otherLanguage, message.getSubject(otherLanguage));
        Assert.assertNull(message.getSubject(defaultLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has no language, subject has no language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("subject").t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getSubject());
        Assert.assertTrue(message.getSubjectLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getSubject(null));
        Assert.assertNull(message.getSubject(otherLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has no language, subject has default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("subject").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertFalse(message.getSubjectLanguages().isEmpty());
        Assert.assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        Assert.assertNull(message.getSubject(otherLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has no language, subject has non-default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("subject").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getSubject());
        Assert.assertFalse(message.getSubjectLanguages().isEmpty());
        Assert.assertTrue(message.getSubjectLanguages().contains(otherLanguage));
        Assert.assertEquals(otherLanguage, message.getSubject(otherLanguage));
        Assert.assertNull(message.getSubject(defaultLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has default language, subject has non-default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("subject").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getSubject());
        Assert.assertFalse(message.getSubjectLanguages().isEmpty());
        Assert.assertTrue(message.getSubjectLanguages().contains(otherLanguage));
        Assert.assertEquals(otherLanguage, message.getSubject(otherLanguage));
        Assert.assertNull(message.getSubject(defaultLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has non-default language, subject has default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("subject").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertNull(message.getSubject());
        Assert.assertFalse(message.getSubjectLanguages().isEmpty());
        Assert.assertTrue(message.getSubjectLanguages().contains(defaultLanguage));
        Assert.assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        Assert.assertNull(message.getSubject(otherLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
    }

    @Test
    public void multipleMessageBodiesTest() throws Exception {
        String defaultLanguage = Stanza.getDefaultLanguage();
        String otherLanguage = PacketParserUtilsTest.determineNonDefaultLanguage();
        String control;
        Message message;
        // message has default language, first body no language, second body other language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("body").t(defaultLanguage).up().e("body").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getBody());
        Assert.assertEquals(otherLanguage, message.getBody(otherLanguage));
        Assert.assertEquals(2, message.getBodies().size());
        Assert.assertEquals(1, message.getBodyLanguages().size());
        Assert.assertTrue(message.getBodyLanguages().contains(otherLanguage));
        assertXMLEqual(control, message.toXML().toString());
        // message has non-default language, first body no language, second body default language
        control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("body").t(otherLanguage).up().e("body").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(otherLanguage, message.getBody());
        Assert.assertEquals(defaultLanguage, message.getBody(defaultLanguage));
        Assert.assertEquals(2, message.getBodies().size());
        Assert.assertEquals(1, message.getBodyLanguages().size());
        Assert.assertTrue(message.getBodyLanguages().contains(defaultLanguage));
        assertXMLEqual(control, message.toXML().toString());
    }

    @Test
    public void messageNoLanguageFirstBodyNoLanguageSecondBodyOtherTest() throws IOException, Exception, FactoryConfigurationError, XmlPullParserException {
        String defaultLanguage = Stanza.getDefaultLanguage();
        String otherLanguage = PacketParserUtilsTest.determineNonDefaultLanguage();
        // message has no language, first body no language, second body other language
        String control = // TODO change default language into something else
        XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").e("body").t(defaultLanguage).up().e("body").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        Message message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getBody());
        Assert.assertEquals(defaultLanguage, message.getBody(null));
        Assert.assertEquals(otherLanguage, message.getBody(otherLanguage));
        Assert.assertEquals(2, message.getBodies().size());
        Assert.assertEquals(1, message.getBodyLanguages().size());
        assertXMLEqual(control, message.toXML().toString());
    }

    @Test
    public void multipleMessageSubjectsTest() throws Exception {
        String defaultLanguage = Stanza.getDefaultLanguage();
        String otherLanguage = PacketParserUtilsTest.determineNonDefaultLanguage();
        String control;
        Message message;
        // message has default language, first subject no language, second subject other language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("subject").t(defaultLanguage).up().e("subject").a("xml:lang", otherLanguage).t(otherLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getSubject());
        Assert.assertEquals(otherLanguage, message.getSubject(otherLanguage));
        Assert.assertEquals(2, message.getSubjects().size());
        Assert.assertEquals(1, message.getSubjectLanguages().size());
        Assert.assertTrue(message.getSubjectLanguages().contains(otherLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has default language, first subject no language, second subject default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", defaultLanguage).e("subject").t(defaultLanguage).up().e("subject").a("xml:lang", defaultLanguage).t((defaultLanguage + "2")).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(defaultLanguage, message.getSubject());
        Assert.assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        Assert.assertEquals(1, message.getSubjects().size());
        Assert.assertEquals(0, message.getSubjectLanguages().size());
        assertXMLNotEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        // message has non-default language, first subject no language, second subject default language
        control = XMLBuilder.create("message").a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", otherLanguage).e("subject").t(otherLanguage).up().e("subject").a("xml:lang", defaultLanguage).t(defaultLanguage).asString(PacketParserUtilsTest.outputProperties);
        message = PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(control));
        Assert.assertEquals(otherLanguage, message.getSubject());
        Assert.assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        Assert.assertEquals(2, message.getSubjects().size());
        Assert.assertEquals(1, message.getSubjectLanguages().size());
        Assert.assertTrue(message.getSubjectLanguages().contains(defaultLanguage));
        assertXMLEqual(control, message.toXML(CLIENT_NAMESPACE).toString());
        /* // message has no language, first subject no language, second subject default language
        control = XMLBuilder.create("message")
        .a("from", "romeo@montague.lit/orchard")
        .a("to", "juliet@capulet.lit/balcony")
        .a("id", "zid615d9")
        .a("type", "chat")
        .e("subject")
        .t(defaultLanguage)
        .up()
        .e("subject")
        .a("xml:lang", defaultLanguage)
        .t(defaultLanguage + "2")
        .asString(outputProperties);

        message = PacketParserUtils
        .parseMessage(PacketParserUtils.getParserFor(control));

        assertEquals(defaultLanguage, message.getSubject());
        assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        assertEquals(1, message.getSubjects().size());
        assertEquals(0, message.getSubjectLanguages().size());
        assertXMLNotEqual(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());

        // message has no language, first subject no language, second subject other language
        control = XMLBuilder.create("message")
        .a("from", "romeo@montague.lit/orchard")
        .a("to", "juliet@capulet.lit/balcony")
        .a("id", "zid615d9")
        .a("type", "chat")
        .e("subject")
        .t(defaultLanguage)
        .up()
        .e("subject")
        .a("xml:lang", otherLanguage)
        .t(otherLanguage)
        .asString(outputProperties);

        message = PacketParserUtils
        .parseMessage(PacketParserUtils.getParserFor(control));

        assertEquals(defaultLanguage, message.getSubject());
        assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        assertEquals(otherLanguage, message.getSubject(otherLanguage));
        assertEquals(2, message.getSubjects().size());
        assertEquals(1, message.getSubjectLanguages().size());
        assertXMLEqual(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());

        // message has no language, first subject no language, second subject no language
        control = XMLBuilder.create("message")
        .a("from", "romeo@montague.lit/orchard")
        .a("to", "juliet@capulet.lit/balcony")
        .a("id", "zid615d9")
        .a("type", "chat")
        .e("subject")
        .t(defaultLanguage)
        .up()
        .e("subject")
        .t(otherLanguage)
        .asString(outputProperties);

        message = PacketParserUtils
        .parseMessage(PacketParserUtils.getParserFor(control));

        assertEquals(defaultLanguage, message.getSubject());
        assertEquals(defaultLanguage, message.getSubject(defaultLanguage));
        assertEquals(1, message.getSubjects().size());
        assertEquals(0, message.getSubjectLanguages().size());
        assertXMLNotEqual(control, message.toXML(StreamOpen.CLIENT_NAMESPACE).toString());
         */
    }

    /**
     * RFC6121 5.2.3 explicitly disallows mixed content in <body/> elements. Make sure that we throw
     * an exception if we encounter such an element.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = XmlPullParserException.class)
    public void invalidMessageBodyContainingTagTest() throws Exception {
        String control = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", "en").e("body").a("xmlns", "http://www.w3.org/1999/xhtml").e("span").a("style", "font-weight: bold;").t("Bad Message Body").asString(PacketParserUtilsTest.outputProperties);
        Message message = PacketParserUtils.parseMessage(TestUtils.getMessageParser(control));
        Assert.fail(("Should throw exception. Instead got message: " + (message.toXML().toString())));
    }

    @Test
    public void invalidXMLInMessageBody() throws Exception {
        String validControl = XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", "en").e("body").t("Good Message Body").asString(PacketParserUtilsTest.outputProperties);
        String invalidControl = validControl.replace("Good Message Body", "Bad </span> Body");
        try {
            PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(invalidControl));
            Assert.fail("Exception should be thrown");
        } catch (XmlPullParserException e) {
            Assert.assertTrue(e.getMessage().contains("end tag name </span>"));
        }
        invalidControl = validControl.replace("Good Message Body", "Bad </body> Body");
        try {
            PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(invalidControl));
            Assert.fail("Exception should be thrown");
        } catch (XmlPullParserException e) {
            Assert.assertTrue(e.getMessage().contains("end tag name </body>"));
        }
        invalidControl = validControl.replace("Good Message Body", "Bad </message> Body");
        try {
            PacketParserUtils.parseMessage(PacketParserUtils.getParserFor(invalidControl));
            Assert.fail("Exception should be thrown");
        } catch (XmlPullParserException e) {
            Assert.assertTrue(e.getMessage().contains("end tag name </message>"));
        }
    }

    @Test
    public void multipleMessageBodiesParsingTest() throws Exception {
        String control = // TODO: Remove the following xml:lang once Smack's serialization toXml() API is aware of a potential
        // scoping xml:lang value. The out message stanza already declares an xml:lang with the exact same
        // value, hence this statement is redundant.
        XMLBuilder.create("message").namespace(CLIENT_NAMESPACE).a("from", "romeo@montague.lit/orchard").a("to", "juliet@capulet.lit/balcony").a("id", "zid615d9").a("type", "chat").a("xml:lang", "en").e("body").a("xml:lang", "en").t("This is a test of the emergency broadcast system, 1.").up().e("body").a("xml:lang", "ru").t("This is a test of the emergency broadcast system, 2.").up().e("body").a("xml:lang", "sp").t("This is a test of the emergency broadcast system, 3.").asString(PacketParserUtilsTest.outputProperties);
        Stanza message = PacketParserUtils.parseStanza(control);
        XmlUnitUtils.assertSimilar(control, message.toXML());
    }

    @Test
    public void validateSimplePresence() throws Exception {
        // CHECKSTYLE:OFF
        String stanza = "<presence from='juliet@example.com/balcony' to='romeo@example.net'/>";
        Presence presence = PacketParserUtils.parsePresence(PacketParserUtils.getParserFor(stanza));
        assertXMLEqual(stanza, presence.toXML(CLIENT_NAMESPACE).toString());
        // CHECKSTYLE:ON
    }

    @Test
    public void validatePresenceProbe() throws Exception {
        // CHECKSTYLE:OFF
        String stanza = "<presence from='mercutio@example.com' id='xv291f38' to='juliet@example.com' type='unsubscribed'/>";
        Presence presence = PacketParserUtils.parsePresence(PacketParserUtils.getParserFor(stanza));
        assertXMLEqual(stanza, presence.toXML(CLIENT_NAMESPACE).toString());
        Assert.assertEquals(unsubscribed, presence.getType());
        // CHECKSTYLE:ON
    }

    @Test
    public void validatePresenceOptionalElements() throws Exception {
        // CHECKSTYLE:OFF
        String stanza = "<presence xml:lang='en' type='unsubscribed'>" + ((("<show>dnd</show>" + "<status>Wooing Juliet</status>") + "<priority>1</priority>") + "</presence>");
        Presence presence = PacketParserUtils.parsePresence(PacketParserUtils.getParserFor(stanza));
        assertXMLEqual(stanza, presence.toXML(CLIENT_NAMESPACE).toString());
        Assert.assertEquals(unsubscribed, presence.getType());
        Assert.assertEquals("dnd", presence.getMode().name());
        Assert.assertEquals("en", presence.getLanguage());
        Assert.assertEquals("Wooing Juliet", presence.getStatus());
        Assert.assertEquals(1, presence.getPriority());
        // CHECKSTYLE:ON
    }

    @Test
    public void parseContentDepthTest() throws Exception {
        final String stanza = "<iq type='result' to='foo@bar.com' from='baz.com' id='42'/>";
        XmlPullParser parser = TestUtils.getParser(stanza, "iq");
        CharSequence content = PacketParserUtils.parseContent(parser);
        Assert.assertEquals("", content.toString());
    }

    @Test
    public void parseElementMultipleNamespace() throws IOException, FactoryConfigurationError, ParserConfigurationException, TransformerException, SAXException, XmlPullParserException {
        // @formatter:off
        final String stanza = XMLBuilder.create("outer", "outerNamespace").a("outerAttribute", "outerValue").element("inner", "innerNamespace").a("innerAttribute", "innerValue").element("innermost").t("some text").asString();
        // @formatter:on
        XmlPullParser parser = TestUtils.getParser(stanza, "outer");
        CharSequence result = PacketParserUtils.parseElement(parser, true);
        assertXMLEqual(stanza, result.toString());
    }

    @Test
    public void parseSASLFailureSimple() throws IOException, FactoryConfigurationError, ParserConfigurationException, TransformerException, SAXException, XmlPullParserException {
        // @formatter:off
        final String saslFailureString = XMLBuilder.create(ELEMENT, NAMESPACE).e(account_disabled.toString()).asString();
        // @formatter:on
        XmlPullParser parser = TestUtils.getParser(saslFailureString, ELEMENT);
        SASLFailure saslFailure = PacketParserUtils.parseSASLFailure(parser);
        assertXMLEqual(saslFailureString, saslFailure.toString());
    }

    @Test
    public void parseSASLFailureExtended() throws IOException, FactoryConfigurationError, ParserConfigurationException, TransformerException, SAXException, XmlPullParserException {
        // @formatter:off
        final String saslFailureString = XMLBuilder.create(ELEMENT, NAMESPACE).e(account_disabled.toString()).up().e("text").a("xml:lang", "en").t("Call 212-555-1212 for assistance.").up().e("text").a("xml:lang", "de").t("Bitte wenden sie sich an (04321) 123-4444").up().e("text").t("Wusel dusel").asString();
        // @formatter:on
        XmlPullParser parser = TestUtils.getParser(saslFailureString, ELEMENT);
        SASLFailure saslFailure = PacketParserUtils.parseSASLFailure(parser);
        XmlUnitUtils.assertSimilar(saslFailureString, saslFailure.toXML(CLIENT_NAMESPACE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void descriptiveTextNullLangPassedMap() throws Exception {
        final String text = "Dummy descriptive text";
        Map<String, String> texts = new HashMap<>();
        texts.put(null, text);
        StanzaError.getBuilder(internal_server_error).setDescriptiveTexts(texts).build();
    }

    @Test
    public void ensureNoEmptyLangInDescriptiveText() throws Exception {
        final String text = "Dummy descriptive text";
        Map<String, String> texts = new HashMap<>();
        texts.put("", text);
        StanzaError error = StanzaError.getBuilder(internal_server_error).setDescriptiveTexts(texts).build();
        final String errorXml = XMLBuilder.create(ERROR).a("type", "cancel").up().element("internal-server-error", ERROR_CONDITION_AND_TEXT_NAMESPACE).up().element("text", ERROR_CONDITION_AND_TEXT_NAMESPACE).t(text).up().asString();
        XmlUnitUtils.assertSimilar(errorXml, error.toXML(CLIENT_NAMESPACE));
    }

    @Test
    public void ensureNoNullLangInParsedDescriptiveTexts() throws Exception {
        final String text = "Dummy descriptive text";
        final String errorXml = XMLBuilder.create(ERROR).a("type", "cancel").up().element("internal-server-error", ERROR_CONDITION_AND_TEXT_NAMESPACE).up().element("text", ERROR_CONDITION_AND_TEXT_NAMESPACE).t(text).up().asString();
        XmlPullParser parser = TestUtils.getParser(errorXml);
        StanzaError error = PacketParserUtils.parseError(parser).build();
        Assert.assertEquals(text, error.getDescriptiveText());
    }
}

