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


import org.eclipse.che.api.core.websocket.commons.WebSocketMessageReceiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link WebSocketDispatcher}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketDispatcherTest {
    @Mock
    private WebSocketMessageReceiver receiver;

    @Mock
    private UrlResolver urlResolver;

    @InjectMocks
    private WebSocketDispatcher dispatcher;

    @Test
    public void shouldResolveUrlOnDispatch() {
        dispatcher.dispatch("url", "message");
        Mockito.verify(urlResolver).resolve("url");
    }

    @Test
    public void shouldRunReceiveOnDispatch() {
        dispatcher.dispatch("url", "message");
        Mockito.verify(receiver).receive("id", "message");
    }
}

