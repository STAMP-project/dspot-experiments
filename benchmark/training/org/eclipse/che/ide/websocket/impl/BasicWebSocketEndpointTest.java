/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.websocket.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link BasicWebSocketEndpoint}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicWebSocketEndpointTest {
    @Mock
    private WebSocketConnectionSustainer sustainer;

    @Mock
    private MessagesReSender reSender;

    @Mock
    private WebSocketDispatcher dispatcher;

    @Mock
    private WebSocketActionManager actionManager;

    @InjectMocks
    private BasicWebSocketEndpoint endpoint;

    @Test
    public void shouldResetSustainerOnOpen() {
        endpoint.onOpen("url");
        Mockito.verify(sustainer).reset("url");
    }

    @Test
    public void shouldReSendMessagesOnOpen() {
        endpoint.onOpen("url");
        Mockito.verify(reSender).reSend("url");
    }

    @Test
    public void shouldOnOpenActionsOnOpen() {
        endpoint.onOpen("url");
        Mockito.verify(actionManager).getOnOpenActions("url");
    }

    @Test
    public void shouldOnCloseActionsOnClose() {
        endpoint.onClose("url");
        Mockito.verify(actionManager).getOnCloseActions("url");
    }

    @Test
    public void shouldSustainOnClose() {
        endpoint.onClose("url");
        Mockito.verify(sustainer).sustain("url");
    }

    @Test
    public void shouldDispatchOnMessage() {
        endpoint.onMessage("url", "message");
        Mockito.verify(dispatcher).dispatch("url", "message");
    }
}

