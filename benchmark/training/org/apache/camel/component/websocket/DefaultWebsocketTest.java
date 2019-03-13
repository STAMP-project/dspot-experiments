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


import java.net.InetSocketAddress;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultWebsocketTest {
    private static final int CLOSE_CODE = -1;

    private static final String MESSAGE = "message";

    private static final String CONNECTION_KEY = "random-connection-key";

    private static final InetSocketAddress ADDRESS = InetSocketAddress.createUnresolved("127.0.0.1", 12345);

    @Mock
    private Session session;

    @Mock
    private WebsocketConsumer consumer;

    @Mock
    private NodeSynchronization sync;

    private DefaultWebsocket defaultWebsocket;

    @Test
    public void testOnClose() {
        defaultWebsocket.onClose(DefaultWebsocketTest.CLOSE_CODE, DefaultWebsocketTest.MESSAGE);
        InOrder inOrder = Mockito.inOrder(session, consumer, sync);
        inOrder.verify(sync, Mockito.times(1)).removeSocket(defaultWebsocket);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnConnect() {
        defaultWebsocket.onConnect(session);
        InOrder inOrder = Mockito.inOrder(session, consumer, sync);
        inOrder.verify(sync, Mockito.times(1)).addSocket(defaultWebsocket);
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(session, defaultWebsocket.getSession());
    }

    @Test
    public void testOnMessage() {
        defaultWebsocket.setConnectionKey(DefaultWebsocketTest.CONNECTION_KEY);
        defaultWebsocket.setSession(session);
        defaultWebsocket.onMessage(DefaultWebsocketTest.MESSAGE);
        InOrder inOrder = Mockito.inOrder(session, consumer, sync);
        inOrder.verify(consumer, Mockito.times(1)).sendMessage(DefaultWebsocketTest.CONNECTION_KEY, DefaultWebsocketTest.MESSAGE, DefaultWebsocketTest.ADDRESS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnMessageWithNullConsumer() {
        defaultWebsocket = new DefaultWebsocket(sync, null, null);
        defaultWebsocket.setConnectionKey(DefaultWebsocketTest.CONNECTION_KEY);
        defaultWebsocket.onMessage(DefaultWebsocketTest.MESSAGE);
        InOrder inOrder = Mockito.inOrder(session, consumer, sync);
        inOrder.verify(consumer, Mockito.times(0)).sendMessage(DefaultWebsocketTest.CONNECTION_KEY, DefaultWebsocketTest.MESSAGE, DefaultWebsocketTest.ADDRESS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testGetConnection() {
        Assert.assertNull(defaultWebsocket.getSession());
        defaultWebsocket.onConnect(session);
        Assert.assertEquals(session, defaultWebsocket.getSession());
        defaultWebsocket.setSession(null);
        Assert.assertNull(defaultWebsocket.getSession());
        defaultWebsocket.setSession(session);
        Assert.assertEquals(session, defaultWebsocket.getSession());
    }

    @Test
    public void testSetConnection() {
        testGetConnection();
    }

    @Test
    public void testGetConnectionKey() {
        defaultWebsocket.setConnectionKey(null);
        Assert.assertNull(defaultWebsocket.getConnectionKey());
        defaultWebsocket.onConnect(session);
        Assert.assertNotNull(defaultWebsocket.getConnectionKey());
        defaultWebsocket.setConnectionKey(DefaultWebsocketTest.CONNECTION_KEY);
        Assert.assertEquals(DefaultWebsocketTest.CONNECTION_KEY, defaultWebsocket.getConnectionKey());
        defaultWebsocket.setConnectionKey(null);
        Assert.assertNull(defaultWebsocket.getConnectionKey());
    }

    @Test
    public void testSetConnectionKey() {
        testGetConnectionKey();
    }
}

