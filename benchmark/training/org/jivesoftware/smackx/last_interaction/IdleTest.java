/**
 * Copyright ? 2018 Paul Schaub
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
package org.jivesoftware.smackx.last_interaction;


import IdleProvider.TEST_INSTANCE;
import Presence.Type;
import java.util.Date;
import junit.framework.TestCase;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smackx.last_interaction.element.IdleElement;
import org.junit.Test;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;


public class IdleTest extends SmackTestSuite {
    @Test
    public void providerTest() throws Exception {
        String xml = "<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z' />";
        XmlPullParser parser = TestUtils.getParser(xml);
        TestCase.assertNotNull(parser);
        IdleElement parsed = TEST_INSTANCE.parse(parser);
        Date date = XmppDateTime.parseXEP0082Date("1969-07-21T02:56:15Z");
        TestCase.assertEquals(date, parsed.getSince());
        IdleElement element = new IdleElement(date);
        assertXMLEqual("<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15.000+00:00'/>", element.toXML().toString());
    }

    @Test
    public void helperTest() {
        Presence presence = new Presence(Type.available);
        IdleElement.addToPresence(presence);
        IdleElement element = IdleElement.fromPresence(presence);
        TestCase.assertNotNull(element);
    }
}

