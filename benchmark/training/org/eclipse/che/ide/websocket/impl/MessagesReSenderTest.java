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
 * Tests for {@link MessagesReSender}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class MessagesReSenderTest {
    @Mock
    private WebSocketConnectionManager connectionManager;

    @Mock
    private UrlResolver urlResolver;

    @InjectMocks
    private MessagesReSender reSender;

    @Test
    public void shouldResendAllMessages() {
        reSender.add("endpointId", "1");
        reSender.add("endpointId", "2");
        reSender.add("endpointId", "3");
        Mockito.when(connectionManager.isConnectionOpen("url")).thenReturn(true);
        reSender.reSend("url");
        Mockito.verify(connectionManager, Mockito.times(3)).sendMessage(ArgumentMatchers.eq("url"), ArgumentMatchers.anyString());
    }

    @Test
    public void shouldStopSendingIfSessionIsClosed() {
        reSender.add("endpointId", "1");
        reSender.add("endpointId", "2");
        reSender.add("endpointId", "3");
        final int[] i = new int[]{ 0 };
        Mockito.when(connectionManager.isConnectionOpen("url")).thenAnswer(( invocation) -> ((i[0])++) <= 1);
        reSender.reSend("url");
        Mockito.verify(connectionManager, Mockito.times(2)).sendMessage(ArgumentMatchers.eq("url"), ArgumentMatchers.anyString());
        Mockito.when(connectionManager.isConnectionOpen("url")).thenReturn(true);
        reSender.reSend("url");
        Mockito.verify(connectionManager, Mockito.times(3)).sendMessage(ArgumentMatchers.eq("url"), ArgumentMatchers.anyString());
    }
}

