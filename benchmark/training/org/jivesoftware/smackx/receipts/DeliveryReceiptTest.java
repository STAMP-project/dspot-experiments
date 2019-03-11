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
package org.jivesoftware.smackx.receipts;


import AutoReceiptMode.always;
import DeliveryReceipt.NAMESPACE;
import DeliveryReceiptRequest.ELEMENT;
import Message.Type;
import com.jamesmurty.utils.XMLBuilder;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.test.util.WaitForPacketListener;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;


public class DeliveryReceiptTest extends InitExtensions {
    private static Properties outputProperties = new Properties();

    static {
        DeliveryReceiptTest.outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }

    @Test
    public void receiptTest() throws Exception {
        XmlPullParser parser;
        String control;
        control = XMLBuilder.create("message").a("from", "romeo@montague.com").e("request").a("xmlns", "urn:xmpp:receipts").asString(DeliveryReceiptTest.outputProperties);
        parser = PacketParserUtils.getParserFor(control);
        Message p = PacketParserUtils.parseMessage(parser);
        DeliveryReceiptRequest drr = p.getExtension(ELEMENT, NAMESPACE);
        Assert.assertNotNull(drr);
        Assert.assertTrue(DeliveryReceiptManager.hasDeliveryReceiptRequest(p));
        Message m = new Message(JidCreate.from("romeo@montague.com"), Type.normal);
        Assert.assertFalse(DeliveryReceiptManager.hasDeliveryReceiptRequest(m));
        DeliveryReceiptRequest.addTo(m);
        Assert.assertTrue(DeliveryReceiptManager.hasDeliveryReceiptRequest(m));
    }

    @Test
    public void receiptManagerListenerTest() throws Exception {
        DummyConnection c = new DummyConnection();
        c.connect();
        DeliveryReceiptManager drm = DeliveryReceiptManager.getInstanceFor(c);
        DeliveryReceiptTest.TestReceiptReceivedListener rrl = new DeliveryReceiptTest.TestReceiptReceivedListener();
        drm.addReceiptReceivedListener(rrl);
        Message m = new Message(JidCreate.from("romeo@montague.com"), Type.normal);
        m.setFrom(JidCreate.from("julia@capulet.com"));
        m.setStanzaId("reply-id");
        m.addExtension(new DeliveryReceipt("original-test-id"));
        c.processStanza(m);
        waitUntilInvocationOrTimeout();
    }

    private static class TestReceiptReceivedListener extends WaitForPacketListener implements ReceiptReceivedListener {
        @Override
        public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
            Assert.assertThat("julia@capulet.com", equalsCharSequence(fromJid));
            Assert.assertThat("romeo@montague.com", equalsCharSequence(toJid));
            Assert.assertEquals("original-test-id", receiptId);
            reportInvoked();
        }
    }

    @Test
    public void receiptManagerAutoReplyTest() throws Exception {
        DummyConnection c = new DummyConnection();
        c.connect();
        DeliveryReceiptManager drm = DeliveryReceiptManager.getInstanceFor(c);
        drm.setAutoReceiptMode(always);
        Assert.assertEquals(always, drm.getAutoReceiptMode());
        // test auto-receipts
        Message m = new Message(JidCreate.from("julia@capulet.com"), Type.normal);
        m.setFrom(JidCreate.from("romeo@montague.com"));
        m.setStanzaId("test-receipt-request");
        DeliveryReceiptRequest.addTo(m);
        // the DRM will send a reply-packet
        c.processStanza(m);
        Stanza reply = c.getSentPacket();
        DeliveryReceipt r = DeliveryReceipt.from(((Message) (reply)));
        Assert.assertThat("romeo@montague.com", equalsCharSequence(reply.getTo()));
        Assert.assertEquals("test-receipt-request", r.getId());
    }
}

