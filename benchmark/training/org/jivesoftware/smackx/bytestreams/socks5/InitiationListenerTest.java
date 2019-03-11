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
package org.jivesoftware.smackx.bytestreams.socks5;


import IQ.Type.error;
import StanzaError.Condition.not_acceptable;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.JidTestUtil;
import org.jxmpp.jid.impl.JidCreate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Test for the InitiationListener class.
 *
 * @author Henning Staib
 */
public class InitiationListenerTest {
    private static final int TIMEOUT = 10000;

    private static final EntityFullJid initiatorJID = JidTestUtil.DUMMY_AT_EXAMPLE_ORG_SLASH_DUMMYRESOURCE;

    private static final EntityFullJid targetJID = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private static final DomainBareJid xmppServer = JidTestUtil.DOMAIN_BARE_JID_1;

    private static final DomainBareJid proxyJID = JidTestUtil.MUC_EXAMPLE_ORG;

    private static final String proxyAddress = "127.0.0.1";

    private static final String sessionID = "session_id";

    private XMPPConnection connection;

    private Socks5BytestreamManager byteStreamManager;

    private InitiationListener initiationListener;

    private Bytestream initBytestream;

    /**
     * If no listeners are registered for incoming SOCKS5 Bytestream requests, all request should be
     * rejected with an error.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldRespondWithError() throws Exception {
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // capture reply to the SOCKS5 Bytestream initiation
        ArgumentCaptor<IQ> argument = ArgumentCaptor.forClass(IQ.class);
        Mockito.verify(connection, Mockito.timeout(InitiationListenerTest.TIMEOUT)).sendStanza(argument.capture());
        // assert that reply is the correct error packet
        Assert.assertEquals(InitiationListenerTest.initiatorJID, argument.getValue().getTo());
        Assert.assertEquals(error, argument.getValue().getType());
        Assert.assertEquals(not_acceptable, argument.getValue().getError().getCondition());
    }

    /**
     * If a listener for all requests is registered it should be notified on incoming requests.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldInvokeListenerForAllRequests() throws Exception {
        // add listener
        Socks5BytestreamListener listener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(listener);
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // assert listener is called once
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(listener, Mockito.timeout(InitiationListenerTest.TIMEOUT)).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert that listener is called for the correct request
        Assert.assertEquals(InitiationListenerTest.initiatorJID, byteStreamRequest.getValue().getFrom());
    }

    /**
     * If a listener for a specific user in registered it should be notified on incoming requests
     * for that user.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldInvokeListenerForUser() throws Exception {
        // add listener
        Socks5BytestreamListener listener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(listener, InitiationListenerTest.initiatorJID);
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // assert listener is called once
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(listener, Mockito.timeout(InitiationListenerTest.TIMEOUT)).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert that reply is the correct error packet
        Assert.assertEquals(InitiationListenerTest.initiatorJID, byteStreamRequest.getValue().getFrom());
    }

    /**
     * If listener for a specific user is registered it should not be notified on incoming requests
     * from other users.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotInvokeListenerForUser() throws Exception {
        // add listener for request of user "other_initiator"
        Socks5BytestreamListener listener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(listener, JidCreate.from(("other_" + (InitiationListenerTest.initiatorJID))));
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // assert listener is not called
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(listener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
        // capture reply to the SOCKS5 Bytestream initiation
        ArgumentCaptor<IQ> argument = ArgumentCaptor.forClass(IQ.class);
        Mockito.verify(connection, Mockito.timeout(InitiationListenerTest.TIMEOUT)).sendStanza(argument.capture());
        // assert that reply is the correct error packet
        Assert.assertEquals(InitiationListenerTest.initiatorJID, argument.getValue().getTo());
        Assert.assertEquals(error, argument.getValue().getType());
        Assert.assertEquals(not_acceptable, argument.getValue().getError().getCondition());
    }

    /**
     * If a user specific listener and an all requests listener is registered only the user specific
     * listener should be notified.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldNotInvokeAllRequestsListenerIfUserListenerExists() throws Exception {
        // add listener for all request
        Socks5BytestreamListener allRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(allRequestsListener);
        // add listener for request of user "initiator"
        Socks5BytestreamListener userRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(userRequestsListener, InitiationListenerTest.initiatorJID);
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // assert user request listener is called once
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(userRequestsListener, Mockito.timeout(InitiationListenerTest.TIMEOUT)).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert all requests listener is not called
        byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(allRequestsListener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
    }

    /**
     * If a user specific listener and an all requests listener is registered only the all requests
     * listener should be notified on an incoming request for another user.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldInvokeAllRequestsListenerIfUserListenerExists() throws Exception {
        // add listener for all request
        Socks5BytestreamListener allRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(allRequestsListener);
        // add listener for request of user "other_initiator"
        Socks5BytestreamListener userRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(userRequestsListener, JidCreate.from(("other_" + (InitiationListenerTest.initiatorJID))));
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        // assert all requests listener is called
        byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(allRequestsListener, Mockito.timeout(InitiationListenerTest.TIMEOUT)).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert user request listener is not called
        Mockito.verify(userRequestsListener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
    }

    /**
     * If a request with a specific session ID should be ignored no listeners should be notified.
     *
     * @throws Exception
     * 		should not happen
     */
    @Test
    public void shouldIgnoreSocks5BytestreamRequestOnce() throws Exception {
        // add listener for all request
        Socks5BytestreamListener allRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(allRequestsListener);
        // add listener for request of user "initiator"
        Socks5BytestreamListener userRequestsListener = Mockito.mock(Socks5BytestreamListener.class);
        byteStreamManager.addIncomingBytestreamListener(userRequestsListener, InitiationListenerTest.initiatorJID);
        // ignore session ID
        byteStreamManager.ignoreBytestreamRequestOnce(InitiationListenerTest.sessionID);
        // run the listener with the initiation packet
        initiationListener.handleIQRequest(initBytestream);
        // assert user request listener is not called
        ArgumentCaptor<BytestreamRequest> byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(userRequestsListener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert all requests listener is not called
        byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(allRequestsListener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
        // run the listener with the initiation packet again
        initiationListener.handleIQRequest(initBytestream);
        // assert user request listener is called on the second request with the same session ID
        Mockito.verify(userRequestsListener, Mockito.timeout(InitiationListenerTest.TIMEOUT)).incomingBytestreamRequest(byteStreamRequest.capture());
        // assert all requests listener is not called
        byteStreamRequest = ArgumentCaptor.forClass(BytestreamRequest.class);
        Mockito.verify(allRequestsListener, Mockito.never()).incomingBytestreamRequest(byteStreamRequest.capture());
    }
}

