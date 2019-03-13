/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.web.undertow.session;


import SessionListener.SessionDestroyedReason.TIMEOUT;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import io.undertow.servlet.api.Deployment;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionAttributes;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;
import org.wildfly.clustering.web.session.SessionExpirationListener;
import org.wildfly.clustering.web.session.SessionManager;


public class UndertowSessionExpirationListenerTestCase {
    @Test
    public void sessionExpired() {
        Deployment deployment = Mockito.mock(Deployment.class);
        UndertowSessionManager manager = Mockito.mock(UndertowSessionManager.class);
        SessionManager<LocalSessionContext, Batch> delegateManager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        ImmutableSession session = Mockito.mock(ImmutableSession.class);
        ImmutableSessionAttributes attributes = Mockito.mock(ImmutableSessionAttributes.class);
        ImmutableSessionMetaData metaData = Mockito.mock(ImmutableSessionMetaData.class);
        ArgumentCaptor<Session> capturedSession = ArgumentCaptor.forClass(Session.class);
        String expectedSessionId = "session";
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        SessionExpirationListener expirationListener = new UndertowSessionExpirationListener(deployment, listeners);
        Mockito.when(deployment.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getSessionManager()).thenReturn(delegateManager);
        Mockito.when(delegateManager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.suspendBatch()).thenReturn(batch);
        Mockito.when(session.getId()).thenReturn(expectedSessionId);
        Mockito.when(session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttributeNames()).thenReturn(Collections.emptySet());
        Mockito.when(session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getCreationTime()).thenReturn(Instant.now());
        Mockito.when(metaData.getLastAccessedTime()).thenReturn(Instant.now());
        Mockito.when(metaData.getMaxInactiveInterval()).thenReturn(Duration.ZERO);
        expirationListener.sessionExpired(session);
        Mockito.verify(batcher).suspendBatch();
        Mockito.verify(listener).sessionDestroyed(capturedSession.capture(), ArgumentMatchers.isNull(), ArgumentMatchers.same(TIMEOUT));
        Mockito.verify(batcher).resumeBatch(batch);
        Assert.assertSame(expectedSessionId, capturedSession.getValue().getId());
        Assert.assertSame(manager, capturedSession.getValue().getSessionManager());
    }
}

