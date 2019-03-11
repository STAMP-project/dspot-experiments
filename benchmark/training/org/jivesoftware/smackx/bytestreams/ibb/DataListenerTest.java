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
import StanzaError.Condition.item_not_found;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;
import org.jivesoftware.smackx.bytestreams.ibb.packet.DataPacketExtension;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.JidTestUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;


/**
 * Test for the CloseListener class.
 *
 * @author Henning Staib
 */
public class DataListenerTest extends InitExtensions {
    private static final Jid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final Jid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    /**
     * If a data stanza of an unknown session is received it should be replied
     * with an &lt;item-not-found/&gt; error.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldReplyErrorIfSessionIsUnknown() throws Exception {
        // mock connection
        XMPPConnection connection = Mockito.mock(XMPPConnection.class);
        // initialize InBandBytestreamManager to get the DataListener
        InBandBytestreamManager byteStreamManager = InBandBytestreamManager.getByteStreamManager(connection);
        // get the DataListener from InBandByteStreamManager
        DataListener dataListener = Whitebox.getInternalState(byteStreamManager, DataListener.class);
        DataPacketExtension dpe = new DataPacketExtension("unknownSessionID", 0, "Data");
        Data data = new Data(dpe);
        data.setFrom(DataListenerTest.initiatorJID);
        data.setTo(DataListenerTest.targetJID);
        dataListener.handleIQRequest(data);
        // wait because packet is processed in an extra thread
        Thread.sleep(200);
        // capture reply to the In-Band Bytestream close request
        ArgumentCaptor<IQ> argument = ArgumentCaptor.forClass(IQ.class);
        Mockito.verify(connection).sendStanza(argument.capture());
        // assert that reply is the correct error packet
        Assert.assertEquals(DataListenerTest.initiatorJID, argument.getValue().getTo());
        Assert.assertEquals(error, argument.getValue().getType());
        Assert.assertEquals(item_not_found, argument.getValue().getError().getCondition());
    }
}

