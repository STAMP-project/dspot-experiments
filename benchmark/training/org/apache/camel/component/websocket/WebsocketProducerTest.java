/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.websocket;


import WebsocketConstants.CONNECTION_KEY;
import WebsocketConstants.SEND_TO_ALL;
import java.util.Collection;
import java.util.concurrent.Future;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WebsocketProducerTest {
    private static final String MESSAGE = "MESSAGE";

    private static final String SESSION_KEY = "random-session-key";

    @Mock
    private WebsocketEndpoint endpoint;

    @Mock
    private WebsocketStore store;

    @Mock
    private Session session;

    @Mock
    private DefaultWebsocket defaultWebsocket1;

    @Mock
    private DefaultWebsocket defaultWebsocket2;

    @Mock
    private Exchange exchange;

    @Mock
    private Message inMessage;

    @Mock
    private RemoteEndpoint remoteEndpoint;

    @Mock
    private Future<Void> future;

    private WebsocketProducer websocketProducer;

    private Collection<DefaultWebsocket> sockets;

    @Test
    public void testProcessSingleMessage() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(inMessage);
        Mockito.when(inMessage.getMandatoryBody()).thenReturn(WebsocketProducerTest.MESSAGE);
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(false);
        Mockito.when(inMessage.getHeader(CONNECTION_KEY, String.class)).thenReturn(WebsocketProducerTest.SESSION_KEY);
        Mockito.when(store.get(WebsocketProducerTest.SESSION_KEY)).thenReturn(defaultWebsocket1);
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        websocketProducer.process(exchange);
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage, remoteEndpoint);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(inMessage, Mockito.times(1)).getMandatoryBody();
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(CONNECTION_KEY, String.class);
        inOrder.verify(store, Mockito.times(1)).get(WebsocketProducerTest.SESSION_KEY);
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessSingleMessageWithException() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(inMessage);
        Mockito.when(inMessage.getMandatoryBody()).thenReturn(WebsocketProducerTest.MESSAGE);
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(false);
        Mockito.when(inMessage.getHeader(CONNECTION_KEY, String.class)).thenReturn(WebsocketProducerTest.SESSION_KEY);
        Mockito.when(store.get(WebsocketProducerTest.SESSION_KEY)).thenReturn(defaultWebsocket1);
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        Mockito.when(remoteEndpoint.sendStringByFuture(WebsocketProducerTest.MESSAGE)).thenReturn(future);
        try {
            websocketProducer.process(exchange);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // expected
        }
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage, remoteEndpoint);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(inMessage, Mockito.times(1)).getMandatoryBody();
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(CONNECTION_KEY, String.class);
        inOrder.verify(store, Mockito.times(1)).get(WebsocketProducerTest.SESSION_KEY);
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verify(endpoint, Mockito.times(1)).getSendTimeout();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessMultipleMessages() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(inMessage);
        Mockito.when(inMessage.getMandatoryBody()).thenReturn(WebsocketProducerTest.MESSAGE);
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(true);
        Mockito.when(store.getAll()).thenReturn(sockets);
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(defaultWebsocket2.getSession()).thenReturn(session);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        websocketProducer.process(exchange);
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage, remoteEndpoint);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(inMessage, Mockito.times(1)).getMandatoryBody();
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verify(store, Mockito.times(1)).getAll();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verify(endpoint, Mockito.times(1)).getSendTimeout();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessSingleMessageNoConnectionKey() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(inMessage);
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(false);
        Mockito.when(inMessage.getHeader(CONNECTION_KEY, String.class)).thenReturn(null);
        try {
            websocketProducer.process(exchange);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(WebsocketSendException.class, e.getClass());
            Assert.assertNotNull(e.getMessage());
            Assert.assertNull(e.getCause());
        }
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage);
        inOrder.verify(exchange, Mockito.times(1)).getIn();
        inOrder.verify(inMessage, Mockito.times(1)).getMandatoryBody();
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(CONNECTION_KEY, String.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendMessage() throws Exception {
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        websocketProducer.sendMessage(defaultWebsocket1, WebsocketProducerTest.MESSAGE);
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage, remoteEndpoint);
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendMessageConnetionIsClosed() throws Exception {
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(session.isOpen()).thenReturn(false);
        websocketProducer.sendMessage(defaultWebsocket1, WebsocketProducerTest.MESSAGE);
        InOrder inOrder = Mockito.inOrder(endpoint, store, session, defaultWebsocket1, defaultWebsocket2, exchange, inMessage, remoteEndpoint);
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testIsSendToAllSet() {
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(true, false);
        Assert.assertTrue(websocketProducer.isSendToAllSet(inMessage));
        Assert.assertFalse(websocketProducer.isSendToAllSet(inMessage));
        InOrder inOrder = Mockito.inOrder(inMessage);
        inOrder.verify(inMessage, Mockito.times(2)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testIsSendToAllSetHeaderNull() {
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(null);
        Assert.assertFalse(websocketProducer.isSendToAllSet(inMessage));
        InOrder inOrder = Mockito.inOrder(inMessage);
        inOrder.verify(inMessage, Mockito.times(1)).getHeader(SEND_TO_ALL, false, Boolean.class);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendToAll() throws Exception {
        Mockito.when(store.getAll()).thenReturn(sockets);
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(defaultWebsocket2.getSession()).thenReturn(session);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        Mockito.when(session.isOpen()).thenReturn(true);
        websocketProducer.sendToAll(store, WebsocketProducerTest.MESSAGE, exchange);
        InOrder inOrder = Mockito.inOrder(store, session, defaultWebsocket1, defaultWebsocket2, remoteEndpoint);
        inOrder.verify(store, Mockito.times(1)).getAll();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSendToAllWithException() throws Exception {
        Mockito.when(exchange.getIn()).thenReturn(inMessage);
        Mockito.when(inMessage.getMandatoryBody()).thenReturn(WebsocketProducerTest.MESSAGE);
        Mockito.when(inMessage.getHeader(SEND_TO_ALL, false, Boolean.class)).thenReturn(true);
        Mockito.when(store.getAll()).thenReturn(sockets);
        Mockito.when(defaultWebsocket1.getSession()).thenReturn(session);
        Mockito.when(defaultWebsocket2.getSession()).thenReturn(session);
        Mockito.when(session.getRemote()).thenReturn(remoteEndpoint);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(remoteEndpoint.sendStringByFuture(WebsocketProducerTest.MESSAGE)).thenReturn(future);
        try {
            websocketProducer.process(exchange);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // expected
        }
        InOrder inOrder = Mockito.inOrder(store, session, defaultWebsocket1, defaultWebsocket2, remoteEndpoint);
        inOrder.verify(store, Mockito.times(1)).getAll();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket1, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(session, Mockito.times(1)).isOpen();
        inOrder.verify(defaultWebsocket2, Mockito.times(1)).getSession();
        inOrder.verify(remoteEndpoint, Mockito.times(1)).sendStringByFuture(WebsocketProducerTest.MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }
}

