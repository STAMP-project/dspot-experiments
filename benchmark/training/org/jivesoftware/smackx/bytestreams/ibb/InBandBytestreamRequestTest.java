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


import IQ.Type.error;
import IQ.Type.result;
import StanzaError.Condition.not_acceptable;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.JidTestUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Test for InBandBytestreamRequest.
 *
 * @author Henning Staib
 */
public class InBandBytestreamRequestTest extends InitExtensions {
    private static final Jid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final Jid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private String sessionID = "session_id";

    private XMPPConnection connection;

    private InBandBytestreamManager byteStreamManager;

    private Open initBytestream;

    /**
     * Test reject() method.
     *
     * @throws NotConnectedException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void shouldReplyWithErrorIfRequestIsRejected() throws InterruptedException, NotConnectedException {
        InBandBytestreamRequest ibbRequest = new InBandBytestreamRequest(byteStreamManager, initBytestream);
        // reject request
        ibbRequest.reject();
        // capture reply to the In-Band Bytestream open request
        ArgumentCaptor<IQ> argument = ArgumentCaptor.forClass(IQ.class);
        Mockito.verify(connection).sendStanza(argument.capture());
        // assert that reply is the correct error packet
        Assert.assertEquals(InBandBytestreamRequestTest.initiatorJID, argument.getValue().getTo());
        Assert.assertEquals(error, argument.getValue().getType());
        Assert.assertEquals(not_acceptable, argument.getValue().getError().getCondition());
    }

    /**
     * Test accept() method.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReturnSessionIfRequestIsAccepted() throws Exception {
        InBandBytestreamRequest ibbRequest = new InBandBytestreamRequest(byteStreamManager, initBytestream);
        // accept request
        InBandBytestreamSession session = ibbRequest.accept();
        // capture reply to the In-Band Bytestream open request
        ArgumentCaptor<IQ> argument = ArgumentCaptor.forClass(IQ.class);
        Mockito.verify(connection).sendStanza(argument.capture());
        // assert that reply is the correct acknowledgment packet
        Assert.assertEquals(InBandBytestreamRequestTest.initiatorJID, argument.getValue().getTo());
        Assert.assertEquals(result, argument.getValue().getType());
        Assert.assertNotNull(session);
        Assert.assertNotNull(session.getInputStream());
        Assert.assertNotNull(session.getOutputStream());
    }
}

