/**
 * Copyright 2012-2014 Florian Schmaus
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
package org.jivesoftware.smackx.ping;


import IQ.Type.result;
import Ping.NAMESPACE;
import java.io.IOException;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.ThreadedDummyConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.JidTestUtil;


public class PingTest extends InitExtensions {
    @Test
    public void checkProvider() throws Exception {
        // @formatter:off
        String control = "<iq from='capulet.lit' to='juliet@capulet.lit/balcony' id='s2c1' type='get'>" + ("<ping xmlns='urn:xmpp:ping'/>" + "</iq>");
        // @formatter:on
        DummyConnection con = new DummyConnection();
        con.connect();
        // Enable ping for this connection
        PingManager.getInstanceFor(con);
        IQ pingRequest = PacketParserUtils.parseStanza(control);
        Assert.assertTrue((pingRequest instanceof Ping));
        con.processStanza(pingRequest);
        Stanza pongPacket = con.getSentPacket();
        Assert.assertTrue((pongPacket instanceof IQ));
        IQ pong = ((IQ) (pongPacket));
        Assert.assertThat("capulet.lit", equalsCharSequence(pong.getTo()));
        Assert.assertEquals("s2c1", pong.getStanzaId());
        Assert.assertEquals(result, pong.getType());
    }

    @Test
    public void checkSendingPing() throws IOException, InterruptedException, SmackException, XMPPException {
        DummyConnection dummyCon = PingTest.getAuthenticatedDummyConnection();
        PingManager pinger = PingManager.getInstanceFor(dummyCon);
        try {
            pinger.ping(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        } catch (SmackException e) {
            // Ignore the fact the server won't answer for this unit test.
        }
        Stanza sentPacket = dummyCon.getSentPacket();
        Assert.assertTrue((sentPacket instanceof Ping));
    }

    @Test
    public void checkSuccessfulPing() throws Exception {
        ThreadedDummyConnection threadedCon = PingTest.getAuthenticatedDummyConnection();
        PingManager pinger = PingManager.getInstanceFor(threadedCon);
        boolean pingSuccess = pinger.ping(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        Assert.assertTrue(pingSuccess);
    }

    /**
     * DummyConnection will not reply so it will timeout.
     *
     * @throws SmackException
     * 		
     * @throws XMPPException
     * 		
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void checkFailedPingOnTimeout() throws IOException, InterruptedException, SmackException, XMPPException {
        DummyConnection dummyCon = PingTest.getAuthenticatedDummyConnectionWithoutIqReplies();
        PingManager pinger = PingManager.getInstanceFor(dummyCon);
        try {
            pinger.ping(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        } catch (NoResponseException e) {
            return;
        }
        Assert.fail();
    }

    /**
     * Server returns an exception for entity.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void checkFailedPingToEntityError() throws Exception {
        ThreadedDummyConnection threadedCon = PingTest.getAuthenticatedDummyConnection();
        // @formatter:off
        String reply = "<iq type='error' id='qrzSp-16' to='test@myserver.com'>" + (((("<ping xmlns='urn:xmpp:ping'/>" + "<error type='cancel'>") + "<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>") + "</error>") + "</iq>");
        // @formatter:on
        IQ serviceUnavailable = PacketParserUtils.parseStanza(reply);
        threadedCon.addIQReply(serviceUnavailable);
        PingManager pinger = PingManager.getInstanceFor(threadedCon);
        boolean pingSuccess = pinger.ping(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        Assert.assertFalse(pingSuccess);
    }

    @Test
    public void checkPingToServerSuccess() throws Exception {
        ThreadedDummyConnection con = PingTest.getAuthenticatedDummyConnection();
        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSuccess = pinger.pingMyServer();
        Assert.assertTrue(pingSuccess);
    }

    /**
     * Server returns an exception.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void checkPingToServerError() throws Exception {
        ThreadedDummyConnection con = PingTest.getAuthenticatedDummyConnection();
        // @formatter:off
        String reply = (((((("<iq type='error' id='qrzSp-16' to='test@myserver.com' from='" + (con.getXMPPServiceDomain())) + "'>") + "<ping xmlns='urn:xmpp:ping'/>") + "<error type='cancel'>") + "<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>") + "</error>") + "</iq>";
        // @formatter:on
        IQ serviceUnavailable = PacketParserUtils.parseStanza(reply);
        con.addIQReply(serviceUnavailable);
        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSuccess = pinger.pingMyServer();
        Assert.assertTrue(pingSuccess);
    }

    @Test
    public void checkPingToServerTimeout() throws IOException, InterruptedException, SmackException, XMPPException {
        DummyConnection con = PingTest.getAuthenticatedDummyConnectionWithoutIqReplies();
        PingManager pinger = PingManager.getInstanceFor(con);
        boolean res = pinger.pingMyServer();
        Assert.assertFalse(res);
    }

    @Test
    public void checkSuccessfulDiscoRequest() throws Exception {
        ThreadedDummyConnection con = PingTest.getAuthenticatedDummyConnection();
        DiscoverInfo info = new DiscoverInfo();
        info.addFeature(NAMESPACE);
        // @formatter:off
        String reply = "<iq type='result' id='qrzSp-16' to='test@myserver.com'>" + (("<query xmlns='http://jabber.org/protocol/disco#info'><identity category='client' type='pc' name='Pidgin'/>" + "<feature var='urn:xmpp:ping'/>") + "</query></iq>");
        // @formatter:on
        IQ discoReply = PacketParserUtils.parseStanza(reply);
        con.addIQReply(discoReply);
        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSupported = pinger.isPingSupported(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        Assert.assertTrue(pingSupported);
    }

    @Test
    public void checkUnsuccessfulDiscoRequest() throws Exception {
        ThreadedDummyConnection con = PingTest.getAuthenticatedDummyConnection();
        DiscoverInfo info = new DiscoverInfo();
        info.addFeature(NAMESPACE);
        // @formatter:off
        String reply = "<iq type='result' id='qrzSp-16' to='test@myserver.com'>" + (("<query xmlns='http://jabber.org/protocol/disco#info'><identity category='client' type='pc' name='Pidgin'/>" + "<feature var='urn:xmpp:noping'/>") + "</query></iq>");
        // @formatter:on
        IQ discoReply = PacketParserUtils.parseStanza(reply);
        con.addIQReply(discoReply);
        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSupported = pinger.isPingSupported(JidTestUtil.DUMMY_AT_EXAMPLE_ORG);
        Assert.assertFalse(pingSupported);
    }
}

