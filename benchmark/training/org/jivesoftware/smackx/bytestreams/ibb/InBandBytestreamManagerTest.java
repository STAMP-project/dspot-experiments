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
package org.jivesoftware.smackx.bytestreams.ibb;


import StanzaError.Condition.feature_not_implemented;
import StanzaType.MESSAGE;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.jivesoftware.util.Protocol;
import org.jivesoftware.util.Verification;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;
import org.mockito.Mockito;


/**
 * Test for InBandBytestreamManager.
 *
 * @author Henning Staib
 */
public class InBandBytestreamManagerTest extends InitExtensions {
    // settings
    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    String sessionID = "session_id";

    // protocol verifier
    private Protocol protocol;

    // mocked XMPP connection
    private XMPPConnection connection;

    /**
     * Test that
     * {@link InBandBytestreamManager#getByteStreamManager(XMPPConnection)} returns
     * one bytestream manager for every connection.
     */
    @Test
    public void shouldHaveOneManagerForEveryConnection() {
        // mock two connections
        XMPPConnection connection1 = Mockito.mock(XMPPConnection.class);
        XMPPConnection connection2 = Mockito.mock(XMPPConnection.class);
        // get bytestream manager for the first connection twice
        InBandBytestreamManager conn1ByteStreamManager1 = InBandBytestreamManager.getByteStreamManager(connection1);
        InBandBytestreamManager conn1ByteStreamManager2 = InBandBytestreamManager.getByteStreamManager(connection1);
        // get bytestream manager for second connection
        InBandBytestreamManager conn2ByteStreamManager1 = InBandBytestreamManager.getByteStreamManager(connection2);
        // assertions
        Assert.assertEquals(conn1ByteStreamManager1, conn1ByteStreamManager2);
        Assert.assertNotSame(conn1ByteStreamManager1, conn2ByteStreamManager1);
    }

    /**
     * Invoking {@link InBandBytestreamManager#establishSession(org.jxmpp.jid.Jid)} should
     * throw an exception if the given target does not support in-band
     * bytestream.
     *
     * @throws SmackException
     * 		
     * @throws XMPPException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void shouldFailIfTargetDoesNotSupportIBB() throws InterruptedException, SmackException, XMPPException {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        try {
            IQ errorIQ = IBBPacketUtils.createErrorIQ(InBandBytestreamManagerTest.targetJID, InBandBytestreamManagerTest.initiatorJID, feature_not_implemented);
            protocol.addResponse(errorIQ);
            // start In-Band Bytestream
            byteStreamManager.establishSession(InBandBytestreamManagerTest.targetJID);
            Assert.fail("exception should be thrown");
        } catch (XMPPErrorException e) {
            Assert.assertEquals(feature_not_implemented, e.getStanzaError().getCondition());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowTooBigDefaultBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setDefaultBlockSize(1000000);
    }

    @Test
    public void shouldCorrectlySetDefaultBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setDefaultBlockSize(1024);
        Assert.assertEquals(1024, byteStreamManager.getDefaultBlockSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowTooBigMaximumBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setMaximumBlockSize(1000000);
    }

    @Test
    public void shouldCorrectlySetMaximumBlockSize() {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setMaximumBlockSize(1024);
        Assert.assertEquals(1024, byteStreamManager.getMaximumBlockSize());
    }

    @Test
    public void shouldUseConfiguredStanzaType() throws InterruptedException, SmackException, XMPPException {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        byteStreamManager.setStanza(MESSAGE);
        protocol.addResponse(null, new Verification<Open, IQ>() {
            @Override
            public void verify(Open request, IQ response) {
                Assert.assertEquals(MESSAGE, request.getStanza());
            }
        });
        // start In-Band Bytestream
        byteStreamManager.establishSession(InBandBytestreamManagerTest.targetJID);
        protocol.verifyAll();
    }

    @Test
    public void shouldReturnSession() throws Exception {
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        IQ success = IBBPacketUtils.createResultIQ(InBandBytestreamManagerTest.targetJID, InBandBytestreamManagerTest.initiatorJID);
        protocol.addResponse(success, Verification.correspondingSenderReceiver, Verification.requestTypeSET);
        // start In-Band Bytestream
        InBandBytestreamSession session = byteStreamManager.establishSession(InBandBytestreamManagerTest.targetJID);
        Assert.assertNotNull(session);
        Assert.assertNotNull(session.getInputStream());
        Assert.assertNotNull(session.getOutputStream());
        protocol.verifyAll();
    }
}

