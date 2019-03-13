/**
 * Copyright 2018 Timothy Pitt, Florian Schmaus
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
package org.jivesoftware.smackx.pubsub;


import Affiliation.Type.member;
import Affiliation.Type.publisher;
import JidTestUtil.BARE_JID_1;
import JidTestUtil.BARE_JID_2;
import JidTestUtil.FULL_JID_1_RESOURCE_1;
import PubSubElementType.SUBSCRIPTIONS_OWNER;
import Subscription.State;
import Subscription.State.none;
import Subscription.State.subscribed;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.ThreadedDummyConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.pubsub.Affiliation.AffiliationNamespace;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.util.ConnectionUtils;
import org.jivesoftware.util.Protocol;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.JidTestUtil;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;


public class PubSubNodeTest extends SmackTestSuite {
    @Test
    public void modifySubscriptionsAsOwnerTest() throws IOException, Exception, InterruptedException, SmackException, XMPPException {
        ThreadedDummyConnection con = ThreadedDummyConnection.newInstance();
        PubSubManager mgr = new PubSubManager(con, JidTestUtil.PUBSUB_EXAMPLE_ORG);
        Node testNode = new LeafNode(mgr, "princely_musings");
        List<Subscription> ChangeSubs = Arrays.asList(new Subscription(JidCreate.from("romeo@montague.org"), State.subscribed), new Subscription(JidCreate.from("juliet@capulet.org"), State.none));
        testNode.modifySubscriptionsAsOwner(ChangeSubs);
        PubSub request = con.getSentPacket();
        Assert.assertEquals("http://jabber.org/protocol/pubsub#owner", request.getChildElementNamespace());
        Assert.assertEquals("pubsub", request.getChildElementName());
        XmlPullParser parser = TestUtils.getIQParser(request.toXML().toString());
        PubSub pubsubResult = ((PubSub) (PacketParserUtils.parseIQ(parser)));
        SubscriptionsExtension subElem = pubsubResult.getExtension(SUBSCRIPTIONS_OWNER);
        List<Subscription> subscriptions = subElem.getSubscriptions();
        Assert.assertEquals(2, subscriptions.size());
        Subscription sub1 = subscriptions.get(0);
        Assert.assertEquals("romeo@montague.org", sub1.getJid().toString());
        Assert.assertEquals(subscribed, sub1.getState());
        Subscription sub2 = subscriptions.get(1);
        Assert.assertEquals("juliet@capulet.org", sub2.getJid().toString());
        Assert.assertEquals(none, sub2.getState());
    }

    @Test
    public void getAffiliationsAsOwnerTest() throws IOException, Exception, InterruptedException, SmackException, XMPPException {
        Protocol protocol = new Protocol();
        XMPPConnection connection = ConnectionUtils.createMockedConnection(protocol, FULL_JID_1_RESOURCE_1);
        PubSubManager mgr = new PubSubManager(connection, JidTestUtil.PUBSUB_EXAMPLE_ORG);
        Node testNode = new LeafNode(mgr, "princely_musings");
        List<Affiliation> affiliations = Arrays.asList(new Affiliation(JidTestUtil.BARE_JID_1, Type.member), new Affiliation(JidTestUtil.BARE_JID_2, Type.publisher));
        AffiliationsExtension affiliationsExtension = new AffiliationsExtension(AffiliationNamespace.owner, affiliations);
        PubSub response = new PubSub(JidTestUtil.PUBSUB_EXAMPLE_ORG, Type.result, PubSubNamespace.owner);
        response.addExtension(affiliationsExtension);
        protocol.addResponse(response);
        List<Affiliation> returnedAffiliations = testNode.getAffiliationsAsOwner();
        PubSub request = ((PubSub) (protocol.getRequests().get(0)));
        Assert.assertEquals("http://jabber.org/protocol/pubsub#owner", request.getChildElementNamespace());
        Assert.assertEquals("pubsub", request.getChildElementName());
        Affiliation affiliationOne = returnedAffiliations.get(0);
        Assert.assertEquals(affiliationOne.getJid(), BARE_JID_1);
        Assert.assertEquals(affiliationOne.getAffiliation(), member);
        Affiliation affiliationTwo = returnedAffiliations.get(1);
        Assert.assertEquals(affiliationTwo.getJid(), BARE_JID_2);
        Assert.assertEquals(affiliationTwo.getAffiliation(), publisher);
    }
}

