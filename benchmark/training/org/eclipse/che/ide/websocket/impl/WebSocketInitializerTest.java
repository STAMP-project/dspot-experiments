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
 * Tests for {@link WebSocketInitializer}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketInitializerTest {
    @Mock
    private WebSocketConnectionManager connectionManager;

    @Mock
    private WebSocketPropertyManager propertyManager;

    @Mock
    private UrlResolver urlResolver;

    @Mock
    private WebSocketActionManager webSocketActionManager;

    @InjectMocks
    private WebSocketInitializer initializer;

    @Test
    public void shouldSetUrlMappingOnInitialize() {
        initializer.initialize("id", "url");
        Mockito.verify(urlResolver).setMapping("id", "url");
    }

    @Test
    public void shouldRunConnectionManagerInitializeConnectionOnInitialize() {
        initializer.initialize("id", "url");
        Mockito.verify(connectionManager).initializeConnection("url");
    }

    @Test
    public void shouldRunPropertyManagerInitializeConnectionOnInitialize() {
        initializer.initialize("id", "url");
        Mockito.verify(propertyManager).initializeConnection("url");
    }

    @Test
    public void shouldRunEstablishConnectionOnInitialize() {
        initializer.initialize("id", "url");
        Mockito.verify(connectionManager).establishConnection("url");
    }

    @Test
    public void shouldGetUrlOnTerminate() {
        Mockito.when(urlResolver.removeMapping("id")).thenReturn("url");
        initializer.terminate("id");
        Mockito.verify(urlResolver).removeMapping("id");
    }

    @Test
    public void shouldDisableSustainerOnTerminate() {
        Mockito.when(urlResolver.removeMapping("id")).thenReturn("url");
        initializer.terminate("id");
        Mockito.verify(propertyManager).disableSustainer("url");
    }

    @Test
    public void shouldCloseConnectionOnTerminate() {
        Mockito.when(urlResolver.removeMapping("id")).thenReturn("url");
        initializer.terminate("id");
        Mockito.verify(connectionManager).closeConnection("url");
    }
}

