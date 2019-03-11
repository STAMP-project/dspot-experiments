/**
 * Copyright 2018 Paul Schaub.
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
package org.jivesoftware.smackx.mood;


import Mood.happy;
import MoodProvider.INSTANCE;
import junit.framework.TestCase;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.mood.element.MoodElement;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import static Mood.happy;


public class MoodElementTest extends SmackTestSuite {
    @Test
    public void toXmlTest() throws Exception {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>" + (("<happy/>" + "<text>Yay, the mood spec has been approved!</text>") + "</mood>");
        MoodElement moodElement = new MoodElement(new MoodElement.MoodSubjectElement(happy, null), "Yay, the mood spec has been approved!");
        assertXMLEqual(xml, moodElement.toXML().toString());
        TestCase.assertFalse(moodElement.hasConcretisation());
        TestCase.assertEquals(happy, moodElement.getMood());
        XmlPullParser parser = TestUtils.getParser(xml);
        MoodElement parsed = INSTANCE.parse(parser);
        TestCase.assertEquals(xml, parsed.toXML().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentsTest() {
        MoodElement element = new MoodElement(null, "Text alone is not allowed.");
    }

    @Test
    public void emptyMoodTest() throws Exception {
        MoodElement empty = new MoodElement(null, null);
        TestCase.assertNull(empty.getText());
        TestCase.assertNull(empty.getMood());
        TestCase.assertNull(empty.getMoodConcretisation());
        TestCase.assertFalse(empty.hasText());
        TestCase.assertFalse(empty.hasConcretisation());
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'/>";
        XmlPullParser parser = TestUtils.getParser(xml);
        MoodElement emptyParsed = INSTANCE.parse(parser);
        TestCase.assertEquals(empty.toXML().toString(), emptyParsed.toXML().toString());
    }

    @Test(expected = XmlPullParserException.class)
    public void unknownMoodValueExceptionTest() throws Exception {
        String xml = "<mood xmlns='http://jabber.org/protocol/mood'>" + ("<unknown/>" + "</mood>");
        XmlPullParser parser = TestUtils.getParser(xml);
        MoodElement element = INSTANCE.parse(parser);
    }
}

