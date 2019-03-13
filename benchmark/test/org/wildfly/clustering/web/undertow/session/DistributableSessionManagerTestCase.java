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


import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionAttributes;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;
import org.wildfly.clustering.web.session.Session;
import org.wildfly.clustering.web.session.SessionManager;


public class DistributableSessionManagerTestCase {
    private final String deploymentName = "mydeployment.war";

    private final SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);

    private final SessionListener listener = Mockito.mock(SessionListener.class);

    private final SessionListeners listeners = new SessionListeners();

    private final RecordableSessionManagerStatistics statistics = Mockito.mock(RecordableSessionManagerStatistics.class);

    private final DistributableSessionManager adapter = new DistributableSessionManager(this.deploymentName, this.manager, this.listeners, this.statistics);

    @Test
    public void getDeploymentName() {
        Assert.assertSame(this.deploymentName, this.adapter.getDeploymentName());
    }

    @Test
    public void start() {
        this.adapter.start();
        Mockito.verify(this.manager).start();
        Mockito.verify(this.statistics).reset();
    }

    @Test
    public void stop() {
        Mockito.when(this.manager.getStopTimeout()).thenReturn(Duration.ZERO);
        this.adapter.stop();
        Mockito.verify(this.manager).stop();
    }

    @Test
    public void setDefaultSessionTimeout() {
        this.adapter.setDefaultSessionTimeout(10);
        Mockito.verify(this.manager).setDefaultMaxInactiveInterval(Duration.ofSeconds(10L));
    }

    @Test
    public void createSessionNoSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        Session<LocalSessionContext> session = Mockito.mock(Session.class);
        String sessionId = "session";
        Mockito.when(this.manager.createIdentifier()).thenReturn(sessionId);
        Mockito.when(this.manager.createSession(sessionId)).thenReturn(session);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(session.getId()).thenReturn(sessionId);
        io.undertow.server.session.Session sessionAdapter = this.adapter.createSession(exchange, config);
        Assert.assertNotNull(sessionAdapter);
        Mockito.verify(this.listener).sessionCreated(sessionAdapter, exchange);
        Mockito.verify(config).setSessionId(exchange, sessionId);
        Mockito.verify(batcher).suspendBatch();
        Mockito.verify(this.statistics).record(sessionAdapter);
        String expected = "expected";
        Mockito.when(session.getId()).thenReturn(expected);
        String result = sessionAdapter.getId();
        Assert.assertSame(expected, result);
    }

    @Test
    public void createSessionSpecifiedSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        Session<LocalSessionContext> session = Mockito.mock(Session.class);
        String sessionId = "session";
        Mockito.when(config.findSessionId(exchange)).thenReturn(sessionId);
        Mockito.when(this.manager.createSession(sessionId)).thenReturn(session);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(session.getId()).thenReturn(sessionId);
        io.undertow.server.session.Session sessionAdapter = this.adapter.createSession(exchange, config);
        Assert.assertNotNull(sessionAdapter);
        Mockito.verify(this.listener).sessionCreated(sessionAdapter, exchange);
        Mockito.verify(batcher).suspendBatch();
        Mockito.verify(this.statistics).record(sessionAdapter);
        String expected = "expected";
        Mockito.when(session.getId()).thenReturn(expected);
        String result = sessionAdapter.getId();
        Assert.assertSame(expected, result);
    }

    @Test
    public void createSessionAlreadyExists() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        String sessionId = "session";
        Mockito.when(config.findSessionId(exchange)).thenReturn(sessionId);
        Mockito.when(this.manager.createSession(sessionId)).thenReturn(null);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        IllegalStateException exception = null;
        try {
            this.adapter.createSession(exchange, config);
        } catch (IllegalStateException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Mockito.verify(batch).discard();
        Mockito.verify(batch).close();
    }

    @Test
    public void getSession() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        Session<LocalSessionContext> session = Mockito.mock(Session.class);
        String sessionId = "session";
        Mockito.when(config.findSessionId(exchange)).thenReturn(sessionId);
        Mockito.when(this.manager.findSession(sessionId)).thenReturn(session);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(session.getId()).thenReturn(sessionId);
        io.undertow.server.session.Session sessionAdapter = this.adapter.getSession(exchange, config);
        Assert.assertNotNull(sessionAdapter);
        Mockito.verifyZeroInteractions(this.statistics);
        Mockito.verify(batcher).suspendBatch();
        String expected = "expected";
        Mockito.when(session.getId()).thenReturn(expected);
        String result = sessionAdapter.getId();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getSessionNoSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        Mockito.when(config.findSessionId(exchange)).thenReturn(null);
        io.undertow.server.session.Session sessionAdapter = this.adapter.getSession(exchange, config);
        Assert.assertNull(sessionAdapter);
    }

    @Test
    public void getSessionInvalidCharacters() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        String sessionId = "session+";
        Mockito.when(config.findSessionId(exchange)).thenReturn(sessionId);
        io.undertow.server.session.Session sessionAdapter = this.adapter.getSession(exchange, config);
        Assert.assertNull(sessionAdapter);
        sessionAdapter = this.adapter.getSession(sessionId);
        Assert.assertNull(sessionAdapter);
        Mockito.verify(this.manager, Mockito.never()).findSession(sessionId);
    }

    @Test
    public void getSessionNotExists() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        String sessionId = "session";
        Mockito.when(config.findSessionId(exchange)).thenReturn(sessionId);
        Mockito.when(this.manager.findSession(sessionId)).thenReturn(null);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        io.undertow.server.session.Session sessionAdapter = this.adapter.getSession(exchange, config);
        Assert.assertNull(sessionAdapter);
        Mockito.verify(batch).close();
        Mockito.verify(batcher, Mockito.never()).suspendBatch();
    }

    @Test
    public void activeSessions() {
        Mockito.when(this.manager.getActiveSessions()).thenReturn(Collections.singleton("expected"));
        int result = this.adapter.getActiveSessions().size();
        Assert.assertEquals(1, result);
    }

    @Test
    public void getTransientSessions() {
        Set<String> result = this.adapter.getTransientSessions();
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getActiveSessions() {
        String expected = "expected";
        Mockito.when(this.manager.getActiveSessions()).thenReturn(Collections.singleton(expected));
        Set<String> result = this.adapter.getActiveSessions();
        Assert.assertEquals(1, result.size());
        Assert.assertSame(expected, result.iterator().next());
    }

    @Test
    public void getAllSessions() {
        String expected = "expected";
        Mockito.when(this.manager.getLocalSessions()).thenReturn(Collections.singleton(expected));
        Set<String> result = this.adapter.getAllSessions();
        Assert.assertEquals(1, result.size());
        Assert.assertSame(expected, result.iterator().next());
    }

    @Test
    public void getSessionByIdentifier() {
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        ImmutableSession session = Mockito.mock(ImmutableSession.class);
        ImmutableSessionAttributes attributes = Mockito.mock(ImmutableSessionAttributes.class);
        ImmutableSessionMetaData metaData = Mockito.mock(ImmutableSessionMetaData.class);
        String id = "session";
        String name = "name";
        Object value = new Object();
        Set<String> names = Collections.singleton(name);
        Instant creationTime = Instant.now();
        Instant lastAccessedTime = Instant.now();
        Duration maxInactiveInterval = Duration.ofMinutes(30L);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(this.manager.viewSession(id)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(id);
        Mockito.when(session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttributeNames()).thenReturn(names);
        Mockito.when(attributes.getAttribute(name)).thenReturn(value);
        Mockito.when(session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getCreationTime()).thenReturn(creationTime);
        Mockito.when(metaData.getLastAccessedTime()).thenReturn(lastAccessedTime);
        Mockito.when(metaData.getMaxInactiveInterval()).thenReturn(maxInactiveInterval);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        io.undertow.server.session.Session result = this.adapter.getSession(id);
        Assert.assertSame(this.adapter, result.getSessionManager());
        Assert.assertSame(id, result.getId());
        Assert.assertEquals(creationTime.toEpochMilli(), result.getCreationTime());
        Assert.assertEquals(lastAccessedTime.toEpochMilli(), result.getLastAccessedTime());
        Assert.assertEquals(maxInactiveInterval.getSeconds(), result.getMaxInactiveInterval());
        Assert.assertEquals(names, result.getAttributeNames());
        Assert.assertSame(value, result.getAttribute(name));
        Mockito.verify(batch).close();
    }

    @Test
    public void getSessionByIdentifierNotExists() {
        String id = "session";
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(this.manager.viewSession(id)).thenReturn(null);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        io.undertow.server.session.Session result = this.adapter.getSession(id);
        Assert.assertNull(result);
        Mockito.verify(batch).close();
    }

    @Test
    public void getStatistics() {
        Assert.assertSame(this.statistics, this.adapter.getStatistics());
    }
}

