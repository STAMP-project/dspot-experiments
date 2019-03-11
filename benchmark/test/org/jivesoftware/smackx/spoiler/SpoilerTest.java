/**
 * Copyright 2018 Paul Schaub
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
package org.jivesoftware.smackx.spoiler;


import SpoilerElement.ELEMENT;
import SpoilerManager.NAMESPACE_0;
import SpoilerProvider.INSTANCE;
import java.util.Map;
import junit.framework.TestCase;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.spoiler.element.SpoilerElement;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;


public class SpoilerTest extends SmackTestSuite {
    @Test
    public void emptySpoilerTest() throws Exception {
        final String xml = "<spoiler xmlns='urn:xmpp:spoiler:0'/>";
        Message message = new Message();
        SpoilerElement.addSpoiler(message);
        SpoilerElement empty = message.getExtension(ELEMENT, NAMESPACE_0);
        TestCase.assertNull(empty.getHint());
        TestCase.assertNull(empty.getLanguage());
        assertXMLEqual(xml, empty.toXML().toString());
        XmlPullParser parser = TestUtils.getParser(xml);
        SpoilerElement parsed = INSTANCE.parse(parser);
        assertXMLEqual(xml, parsed.toXML().toString());
    }

    @Test
    public void hintSpoilerTest() throws Exception {
        final String xml = "<spoiler xmlns='urn:xmpp:spoiler:0'>Love story end</spoiler>";
        Message message = new Message();
        SpoilerElement.addSpoiler(message, "Love story end");
        SpoilerElement withHint = message.getExtension(ELEMENT, NAMESPACE_0);
        TestCase.assertEquals("Love story end", withHint.getHint());
        TestCase.assertNull(withHint.getLanguage());
        assertXMLEqual(xml, withHint.toXML().toString());
        XmlPullParser parser = TestUtils.getParser(xml);
        SpoilerElement parsed = INSTANCE.parse(parser);
        assertXMLEqual(xml, parsed.toXML().toString());
    }

    @Test
    public void i18nHintSpoilerTest() throws Exception {
        final String xml = "<spoiler xml:lang='de' xmlns='urn:xmpp:spoiler:0'>Der Kuchen ist eine L?ge!</spoiler>";
        Message message = new Message();
        SpoilerElement.addSpoiler(message, "de", "Der Kuchen ist eine L?ge!");
        SpoilerElement i18nHint = message.getExtension(ELEMENT, NAMESPACE_0);
        TestCase.assertEquals("Der Kuchen ist eine L?ge!", i18nHint.getHint());
        TestCase.assertEquals("de", i18nHint.getLanguage());
        assertXMLEqual(xml, i18nHint.toXML().toString());
        XmlPullParser parser = TestUtils.getParser(xml);
        SpoilerElement parsed = INSTANCE.parse(parser);
        TestCase.assertEquals(i18nHint.getLanguage(), parsed.getLanguage());
        assertXMLEqual(xml, parsed.toXML().toString());
    }

    @Test
    public void getSpoilersTest() {
        Message m = new Message();
        TestCase.assertTrue(SpoilerElement.getSpoilers(m).isEmpty());
        SpoilerElement.addSpoiler(m);
        TestCase.assertTrue(SpoilerElement.containsSpoiler(m));
        Map<String, String> spoilers = SpoilerElement.getSpoilers(m);
        TestCase.assertEquals(1, spoilers.size());
        TestCase.assertEquals(null, spoilers.get(""));
        final String spoilerText = "Spoiler Text";
        SpoilerElement.addSpoiler(m, "de", spoilerText);
        spoilers = SpoilerElement.getSpoilers(m);
        TestCase.assertEquals(2, spoilers.size());
        TestCase.assertEquals(spoilerText, spoilers.get("de"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void spoilerCheckArgumentsNullTest() {
        @SuppressWarnings("unused")
        SpoilerElement spoilerElement = new SpoilerElement("de", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void spoilerCheckArgumentsEmptyTest() {
        @SuppressWarnings("unused")
        SpoilerElement spoilerElement = new SpoilerElement("de", "");
    }
}

