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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link BasicWebSocketMessageTransmitter}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicWebSocketMessageTransmitterTest {
    private static final String ENDPOINT_ID = "endpointId";

    private static final String URL = "url";

    private static final String MESSAGE = "message";

    @Mock
    private WebSocketConnectionManager connectionManager;

    @Mock
    private MessagesReSender reSender;

    @Mock
    private UrlResolver urlResolver;

    @InjectMocks
    private BasicWebSocketMessageTransmitter transmitter;

    @Test
    public void shouldResolveUrlOnTransmit() {
        transmitter.transmit(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(urlResolver).getUrl(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID);
    }

    @Test
    public void shouldCheckIfConnectionIsOpenOnTransmit() {
        transmitter.transmit(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(connectionManager).isConnectionOpen(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldSendMessageIfConnectionIsOpenOnTransmit() {
        Mockito.when(connectionManager.isConnectionOpen(ArgumentMatchers.anyString())).thenReturn(true);
        transmitter.transmit(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(connectionManager).sendMessage(BasicWebSocketMessageTransmitterTest.URL, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(reSender, Mockito.never()).add(BasicWebSocketMessageTransmitterTest.URL, BasicWebSocketMessageTransmitterTest.MESSAGE);
    }

    @Test
    public void shouldAddMessageToReSenderIfConnectionIsNotOpenOnTransmit() {
        Mockito.when(connectionManager.isConnectionOpen(ArgumentMatchers.anyString())).thenReturn(false);
        transmitter.transmit(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(connectionManager, Mockito.never()).sendMessage(BasicWebSocketMessageTransmitterTest.URL, BasicWebSocketMessageTransmitterTest.MESSAGE);
        Mockito.verify(reSender).add(BasicWebSocketMessageTransmitterTest.ENDPOINT_ID, BasicWebSocketMessageTransmitterTest.MESSAGE);
    }
}

