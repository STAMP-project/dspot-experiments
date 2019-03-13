/**
 * Copyright 2014 Vyacheslav Blinov
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
package org.jivesoftware.smackx.amp;


import AMPDeliverCondition.Value;
import AMPExtension.Action;
import AMPExtension.ELEMENT;
import AMPExtension.Status.alert;
import java.io.IOException;
import java.io.InputStream;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.amp.packet.AMPExtension;
import org.jivesoftware.smackx.amp.provider.AMPExtensionProvider;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import static AMPMatchResourceCondition.Value.any;
import static AMPMatchResourceCondition.Value.exact;
import static AMPMatchResourceCondition.Value.other;


public class AMPExtensionTest {
    private InputStream CORRECT_SENDING_STANZA_STREAM;

    private InputStream INCORRECT_RECEIVING_STANZA_STREAM;

    @Test
    public void isCorrectToXmlTransform() throws IOException {
        String correctStanza = AMPExtensionTest.toString(CORRECT_SENDING_STANZA_STREAM);
        AMPExtension ext = new AMPExtension();
        ext.addRule(new AMPExtension.Rule(Action.alert, new AMPDeliverCondition(Value.direct)));
        ext.addRule(new AMPExtension.Rule(Action.drop, new AMPDeliverCondition(Value.forward)));
        ext.addRule(new AMPExtension.Rule(Action.error, new AMPDeliverCondition(Value.gateway)));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPDeliverCondition(Value.none)));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPDeliverCondition(Value.stored)));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPExpireAtCondition("2004-09-10T08:33:14Z")));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPMatchResourceCondition(any)));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPMatchResourceCondition(exact)));
        ext.addRule(new AMPExtension.Rule(Action.notify, new AMPMatchResourceCondition(other)));
        Assert.assertEquals(correctStanza, ext.toXML().toString());
    }

    @Test
    public void isCorrectFromXmlErrorHandling() throws Exception {
        AMPExtensionProvider ampProvider = new AMPExtensionProvider();
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(INCORRECT_RECEIVING_STANZA_STREAM, "UTF-8");
        Assert.assertEquals(XmlPullParser.START_TAG, parser.next());
        Assert.assertEquals(ELEMENT, parser.getName());
        ExtensionElement extension = ampProvider.parse(parser);
        Assert.assertTrue((extension instanceof AMPExtension));
        AMPExtension amp = ((AMPExtension) (extension));
        Assert.assertEquals(0, amp.getRulesCount());
        Assert.assertEquals(alert, amp.getStatus());
        Assert.assertEquals("bernardo@hamlet.lit/elsinore", amp.getFrom());
        Assert.assertEquals("francisco@hamlet.lit", amp.getTo());
    }

    @Test
    public void isCorrectFromXmlDeserialization() throws Exception {
        AMPExtensionProvider ampProvider = new AMPExtensionProvider();
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(CORRECT_SENDING_STANZA_STREAM, "UTF-8");
        Assert.assertEquals(XmlPullParser.START_TAG, parser.next());
        Assert.assertEquals(ELEMENT, parser.getName());
        ExtensionElement extension = ampProvider.parse(parser);
        Assert.assertTrue((extension instanceof AMPExtension));
        AMPExtension amp = ((AMPExtension) (extension));
        Assert.assertEquals(9, amp.getRulesCount());
    }
}

