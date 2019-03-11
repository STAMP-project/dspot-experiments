/**
 * Copyright 2014 Vyacheslav Blinov, 2017-2018 Florian Schmaus
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
package org.jivesoftware.smack.sm.provider;


import StanzaError.Condition;
import StanzaError.Condition.item_not_found;
import StanzaError.ERROR_CONDITION_AND_TEXT_NAMESPACE;
import StreamManagement.AckAnswer;
import StreamManagement.Enabled;
import StreamManagement.Failed;
import StreamManagement.Resumed;
import com.jamesmurty.utils.XMLBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.hamcrest.CoreMatchers;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.packet.StanzaErrorTextElement;
import org.jivesoftware.smack.sm.packet.StreamManagement;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class ParseStreamManagementTest {
    private static final Properties outputProperties = ParseStreamManagementTest.initOutputProperties();

    @Test
    public void testParseEnabled() throws Exception {
        String stanzaID = "zid615d9";
        boolean resume = true;
        String location = "test";
        int max = 42;
        String enabledStanza = XMLBuilder.create("enabled").a("xmlns", "urn:xmpp:sm:3").a("id", "zid615d9").a("resume", String.valueOf(resume)).a("location", location).a("max", String.valueOf(max)).asString(ParseStreamManagementTest.outputProperties);
        StreamManagement.Enabled enabledPacket = ParseStreamManagement.enabled(PacketParserUtils.getParserFor(enabledStanza));
        Assert.assertThat(enabledPacket, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(enabledPacket.getId(), CoreMatchers.equalTo(stanzaID));
        Assert.assertThat(enabledPacket.getLocation(), CoreMatchers.equalTo(location));
        Assert.assertThat(enabledPacket.isResumeSet(), CoreMatchers.equalTo(resume));
        Assert.assertThat(enabledPacket.getMaxResumptionTime(), CoreMatchers.equalTo(max));
    }

    @Test
    public void testParseEnabledInvariant() throws IOException, XmlPullParserException {
        String enabledString = new StreamManagement.Enabled("stream-id", false).toXML().toString();
        XmlPullParser parser = PacketParserUtils.getParserFor(enabledString);
        StreamManagement.Enabled enabled = ParseStreamManagement.enabled(parser);
        Assert.assertEquals(enabledString, enabled.toXML().toString());
    }

    @Test
    public void testParseFailed() throws Exception {
        String failedStanza = XMLBuilder.create("failed").a("xmlns", "urn:xmpp:sm:3").asString(ParseStreamManagementTest.outputProperties);
        StreamManagement.Failed failedPacket = ParseStreamManagement.failed(PacketParserUtils.getParserFor(failedStanza));
        Assert.assertThat(failedPacket, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertTrue(((failedPacket.getStanzaErrorCondition()) == null));
    }

    @Test
    public void testParseFailedError() throws Exception {
        StanzaError.Condition errorCondition = Condition.unexpected_request;
        String failedStanza = XMLBuilder.create("failed").a("xmlns", "urn:xmpp:sm:3").element(errorCondition.toString(), ERROR_CONDITION_AND_TEXT_NAMESPACE).asString(ParseStreamManagementTest.outputProperties);
        StreamManagement.Failed failedPacket = ParseStreamManagement.failed(PacketParserUtils.getParserFor(failedStanza));
        Assert.assertThat(failedPacket, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertTrue(((failedPacket.getStanzaErrorCondition()) == errorCondition));
    }

    @Test
    public void testParseFailedWithTExt() throws IOException, XmlPullParserException {
        // @formatter:off
        final String failedNonza = "<failed h='20' xmlns='urn:xmpp:sm:3'>" + (((("<item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>") + "Previous session timed out") + "</text>") + "</failed>");
        // @formatter:on
        XmlPullParser parser = PacketParserUtils.getParserFor(failedNonza);
        StreamManagement.Failed failed = ParseStreamManagement.failed(parser);
        Assert.assertEquals(item_not_found, failed.getStanzaErrorCondition());
        List<StanzaErrorTextElement> textElements = failed.getTextElements();
        Assert.assertEquals(1, textElements.size());
        StanzaErrorTextElement textElement = textElements.get(0);
        Assert.assertEquals("Previous session timed out", textElement.getText());
        Assert.assertEquals("en", textElement.getLang());
    }

    @Test
    public void testParseResumed() throws Exception {
        long handledPackets = 42;
        String previousID = "zid615d9";
        String resumedStanza = XMLBuilder.create("resumed").a("xmlns", "urn:xmpp:sm:3").a("h", String.valueOf(handledPackets)).a("previd", previousID).asString(ParseStreamManagementTest.outputProperties);
        StreamManagement.Resumed resumedPacket = ParseStreamManagement.resumed(PacketParserUtils.getParserFor(resumedStanza));
        Assert.assertThat(resumedPacket, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(resumedPacket.getHandledCount(), CoreMatchers.equalTo(handledPackets));
        Assert.assertThat(resumedPacket.getPrevId(), CoreMatchers.equalTo(previousID));
    }

    @Test
    public void testParseAckAnswer() throws Exception {
        long handledPackets = 42 + 42;
        String ackStanza = XMLBuilder.create("a").a("xmlns", "urn:xmpp:sm:3").a("h", String.valueOf(handledPackets)).asString(ParseStreamManagementTest.outputProperties);
        StreamManagement.AckAnswer acknowledgementPacket = ParseStreamManagement.ackAnswer(PacketParserUtils.getParserFor(ackStanza));
        Assert.assertThat(acknowledgementPacket, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(acknowledgementPacket.getHandledCount(), CoreMatchers.equalTo(handledPackets));
    }
}

