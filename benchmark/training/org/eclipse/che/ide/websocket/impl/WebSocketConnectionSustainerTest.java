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
 * Tests for {@link WebSocketConnectionSustainer}
 *
 * @author Dmitry Kuleshov
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketConnectionSustainerTest {
    @Mock
    private WebSocketConnectionManager connectionManager;

    @Mock
    private WebSocketPropertyManager propertyManager;

    @InjectMocks
    private WebSocketConnectionSustainer sustainer;

    @Test
    public void shouldGetReConnectionAttemptsOnReset() {
        sustainer.reset("url");
        Mockito.verify(propertyManager).getReConnectionAttempts("url");
    }

    @Test
    public void shouldSetReConnectionAttemptsOnReset() {
        sustainer.reset("url");
        Mockito.verify(propertyManager).setReConnectionAttempts("url", 0);
    }

    @Test
    public void shouldGetReConnectionAttemptsOnSustain() {
        sustainer.sustain("url");
        Mockito.verify(propertyManager).getReConnectionAttempts("url");
    }

    @Test
    public void shouldDisableSustainerOnExceedingTheLimitOfAttepts() {
        Mockito.when(propertyManager.getReConnectionAttempts("url")).thenReturn(10);
        sustainer.sustain("url");
        Mockito.verify(propertyManager).disableSustainer("url");
    }

    @Test
    public void shouldNotDisableSustainerIfNotExceededTheLimitOfAttepts() {
        Mockito.when(propertyManager.getReConnectionAttempts("url")).thenReturn(0);
        sustainer.sustain("url");
        Mockito.verify(propertyManager, Mockito.never()).disableSustainer("url");
    }

    @Test
    public void shouldCheckIfSustainerIsEnabled() {
        sustainer.sustain("url");
        Mockito.verify(propertyManager).sustainerEnabled("url");
    }

    @Test
    public void shouldProperlySetReconnectionAttemptsWhenSustainerIsEnabled() {
        Mockito.when(propertyManager.getReConnectionAttempts("url")).thenReturn(0);
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(true);
        sustainer.sustain("url");
        Mockito.verify(propertyManager).setReConnectionAttempts("url", 1);
    }

    @Test
    public void shouldNotSetReconnectionAttemptsWhenSustainerIsDisabled() {
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(false);
        sustainer.sustain("url");
        Mockito.verify(propertyManager, Mockito.never()).setReConnectionAttempts("url", 1);
    }

    @Test
    public void shouldProperlySetConnectionDelayWhenSustainerIsEnabled() {
        Mockito.when(propertyManager.getReConnectionAttempts("url")).thenReturn(0);
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(true);
        sustainer.sustain("url");
        Mockito.verify(propertyManager).setConnectionDelay("url", 1000);
    }

    @Test
    public void shouldNotSetConnectionDelayWhenSustainerIsDisabled() {
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(false);
        sustainer.sustain("url");
        Mockito.verify(propertyManager, Mockito.never()).setConnectionDelay("url", 1000);
    }

    @Test
    public void shouldRunEstablishConnectionWhenSustainerIsEnabled() {
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(true);
        sustainer.sustain("url");
        Mockito.verify(connectionManager).establishConnection("url");
    }

    @Test
    public void shouldNotRunConnectionWhenSustainerIsDisabled() {
        Mockito.when(propertyManager.sustainerEnabled("url")).thenReturn(false);
        sustainer.sustain("url");
        Mockito.verify(connectionManager, Mockito.never()).establishConnection("url");
    }
}

