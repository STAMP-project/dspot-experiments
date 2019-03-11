/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.logging;


import LoggingSession.State.CREATED;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Unit tests for {@link LoggingSession}.
 */
@Category(LoggingTest.class)
public class LoggingSessionTest {
    private LoggingSessionListeners loggingSessionListeners;

    private LogConfigSupplier logConfigSupplier;

    private Configuration configuration;

    private LoggingSession loggingSession;

    @Test
    public void createUsesLoggingSessionListenersGetByDefault() {
        loggingSession = LoggingSession.create();
        assertThat(loggingSession.getLoggingSessionListeners()).isEqualTo(LoggingSessionListeners.get());
    }

    @Test
    public void createSessionInitializesConfiguration() {
        loggingSession.createSession(logConfigSupplier);
        Mockito.verify(configuration).initialize(ArgumentMatchers.eq(logConfigSupplier));
    }

    @Test
    public void createSessionInvokesConfigChangedOnConfiguration() {
        loggingSession.createSession(logConfigSupplier);
        Mockito.verify(configuration).configChanged();
    }

    @Test
    public void createSessionPublishesConfiguration() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        Mockito.verify(configuration).initialize(ArgumentMatchers.eq(logConfigSupplier));
        Mockito.verify(configuration).configChanged();
    }

    @Test
    public void createSessionPublishesConfigBeforeCreatingLoggingSession() {
        loggingSession.createSession(logConfigSupplier);
        InOrder inOrder = Mockito.inOrder(configuration, loggingSessionListeners);
        inOrder.verify(configuration).initialize(ArgumentMatchers.eq(logConfigSupplier));
        inOrder.verify(configuration).configChanged();
        inOrder.verify(loggingSessionListeners).createSession(ArgumentMatchers.eq(loggingSession));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void createSessionChangesStateToCREATED() {
        loggingSession.createSession(logConfigSupplier);
        assertThat(loggingSession.getState()).isSameAs(CREATED);
    }

    @Test
    public void createSessionNotifiesLoggingSessionListeners() {
        loggingSession.createSession(logConfigSupplier);
        Mockito.verify(loggingSessionListeners).createSession(ArgumentMatchers.eq(loggingSession));
    }

    @Test
    public void createSessionThrowsIfSessionAlreadyCreated() {
        loggingSession.createSession(logConfigSupplier);
        assertThatThrownBy(() -> loggingSession.createSession(logConfigSupplier)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void createSessionThrowsIfSessionAlreadyStarted() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        assertThatThrownBy(() -> loggingSession.createSession(logConfigSupplier)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void startSessionNotifiesListeners() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        Mockito.verify(loggingSessionListeners).startSession();
    }

    @Test
    public void startSessionThrowsIfSessionNotCreated() {
        assertThatThrownBy(() -> loggingSession.startSession()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void startSessionThrowsIfSessionStopped() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        loggingSession.stopSession();
        assertThatThrownBy(() -> loggingSession.startSession()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void startSessionThrowsIfSessionAlreadyStarted() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        assertThatThrownBy(() -> loggingSession.startSession()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void stopSessionNotifiesListeners() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        loggingSession.stopSession();
        Mockito.verify(loggingSessionListeners).stopSession();
    }

    @Test
    public void stopSessionThrowsIfSessionNotCreated() {
        assertThatThrownBy(() -> loggingSession.stopSession()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void stopSessionThrowsIfSessionNotStarted() {
        loggingSession.createSession(logConfigSupplier);
        assertThatThrownBy(() -> loggingSession.stopSession()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void stopSessionThrowsIfSessionAlreadyStopped() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.startSession();
        loggingSession.stopSession();
        assertThatThrownBy(() -> loggingSession.stopSession()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shutdownInvokesConfigurationShutdown() {
        loggingSession.shutdown();
        Mockito.verify(configuration).shutdown();
    }

    @Test
    public void shutdownCleansUpConfiguration() {
        loggingSession.createSession(logConfigSupplier);
        loggingSession.shutdown();
        Mockito.verify(configuration).shutdown();
    }
}

