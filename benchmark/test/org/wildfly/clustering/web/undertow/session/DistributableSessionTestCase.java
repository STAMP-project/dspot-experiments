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


import HttpServletRequest.FORM_AUTH;
import SessionDestroyedReason.INVALIDATED;
import io.undertow.security.api.AuthenticatedSessionManager.AuthenticatedSession;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionListeners;
import io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.BatchContext;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.web.session.Session;
import org.wildfly.clustering.web.session.SessionAttributes;
import org.wildfly.clustering.web.session.SessionManager;
import org.wildfly.clustering.web.session.SessionMetaData;


/**
 * Unit test for {@link DistributableSession}.
 *
 * @author Paul Ferraro
 */
public class DistributableSessionTestCase {
    private final UndertowSessionManager manager = Mockito.mock(UndertowSessionManager.class);

    private final SessionConfig config = Mockito.mock(SessionConfig.class);

    private final Session<LocalSessionContext> session = Mockito.mock(Session.class);

    private final Batch batch = Mockito.mock(Batch.class);

    private final Consumer<HttpServerExchange> closeTask = Mockito.mock(Consumer.class);

    private final Session adapter = new DistributableSession(this.manager, this.session, this.config, this.batch, this.closeTask);

    @Test
    public void getId() {
        String id = "id";
        Mockito.when(this.session.getId()).thenReturn(id);
        String result = this.adapter.getId();
        Assert.assertSame(id, result);
    }

    @Test
    public void requestDone() {
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        HttpServerExchange exchange = new HttpServerExchange(null);
        Mockito.when(this.session.isValid()).thenReturn(true);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        this.adapter.requestDone(exchange);
        Mockito.verify(this.session).close();
        Mockito.verify(this.batch).close();
        Mockito.verify(context).close();
        Mockito.verify(this.closeTask).accept(exchange);
        Mockito.reset(this.batch, this.session, context, this.closeTask);
        Mockito.when(this.session.isValid()).thenReturn(false);
        this.adapter.requestDone(exchange);
        Mockito.verify(this.session, Mockito.never()).close();
        Mockito.verify(this.batch, Mockito.never()).close();
        Mockito.verify(context, Mockito.never()).close();
        Mockito.verify(this.closeTask).accept(exchange);
    }

    @Test
    public void getCreationTime() {
        this.validate(( session) -> session.getCreationTime());
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Instant now = Instant.now();
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getCreationTime()).thenReturn(now);
        long result = this.adapter.getCreationTime();
        Assert.assertEquals(now.toEpochMilli(), result);
        Mockito.verify(context).close();
    }

    @Test
    public void getLastAccessedTime() {
        this.validate(( session) -> session.getLastAccessedTime());
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Instant now = Instant.now();
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getLastAccessedTime()).thenReturn(now);
        long result = this.adapter.getLastAccessedTime();
        Assert.assertEquals(now.toEpochMilli(), result);
        Mockito.verify(context).close();
    }

    @Test
    public void getMaxInactiveInterval() {
        this.validate(( session) -> session.getMaxInactiveInterval());
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        long expected = 3600L;
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getMaxInactiveInterval()).thenReturn(Duration.ofSeconds(expected));
        long result = this.adapter.getMaxInactiveInterval();
        Assert.assertEquals(expected, result);
        Mockito.verify(context).close();
    }

    @Test
    public void setMaxInactiveInterval() {
        int interval = 3600;
        this.validate(( session) -> session.setMaxInactiveInterval(interval));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        this.adapter.setMaxInactiveInterval(interval);
        Mockito.verify(metaData).setMaxInactiveInterval(Duration.ofSeconds(interval));
        Mockito.verify(context).close();
    }

    @Test
    public void getAttributeNames() {
        this.validate(( session) -> session.getAttributeNames());
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        Set<String> expected = Collections.singleton("name");
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttributeNames()).thenReturn(expected);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.getAttributeNames();
        Assert.assertSame(expected, result);
        Mockito.verify(context).close();
    }

    @Test
    public void getAttribute() {
        String name = "name";
        this.validate(( session) -> session.getAttribute(name));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        Object expected = new Object();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttribute(name)).thenReturn(expected);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.getAttribute(name);
        Assert.assertSame(expected, result);
        Mockito.verify(context).close();
    }

    @Test
    public void getAuthenticatedSessionAttribute() {
        String name = (CachedAuthenticatedSessionHandler.class.getName()) + ".AuthenticatedSession";
        this.validate(( session) -> session.getAttribute(name));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        Account account = Mockito.mock(Account.class);
        AuthenticatedSession auth = new AuthenticatedSession(account, HttpServletRequest.FORM_AUTH);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttribute(name)).thenReturn(auth);
        AuthenticatedSession result = ((AuthenticatedSession) (this.adapter.getAttribute(name)));
        Assert.assertSame(account, result.getAccount());
        Assert.assertSame(FORM_AUTH, result.getMechanism());
        Mockito.verify(context).close();
        Mockito.reset(context);
        LocalSessionContext localContext = Mockito.mock(LocalSessionContext.class);
        AuthenticatedSession expected = new AuthenticatedSession(account, HttpServletRequest.BASIC_AUTH);
        Mockito.when(attributes.getAttribute(name)).thenReturn(null);
        Mockito.when(this.session.getLocalContext()).thenReturn(localContext);
        Mockito.when(localContext.getAuthenticatedSession()).thenReturn(expected);
        result = ((AuthenticatedSession) (this.adapter.getAttribute(name)));
        Assert.assertSame(expected, result);
        Mockito.verify(context).close();
    }

    @Test
    public void setAttribute() {
        String name = "name";
        Integer value = Integer.valueOf(1);
        this.validate(( session) -> session.setAttribute(name, value));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Object expected = new Object();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.setAttribute(name, value)).thenReturn(expected);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.setAttribute(name, value);
        Assert.assertSame(expected, result);
        Mockito.verify(listener, Mockito.never()).attributeAdded(this.adapter, name, value);
        Mockito.verify(listener).attributeUpdated(this.adapter, name, value, expected);
        Mockito.verify(listener, Mockito.never()).attributeRemoved(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.any());
        Mockito.verify(context).close();
    }

    @Test
    public void setNewAttribute() {
        String name = "name";
        Integer value = Integer.valueOf(1);
        this.validate(( session) -> session.setAttribute(name, value));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Object expected = null;
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.setAttribute(name, value)).thenReturn(expected);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.setAttribute(name, value);
        Assert.assertSame(expected, result);
        Mockito.verify(listener).attributeAdded(this.adapter, name, value);
        Mockito.verify(listener, Mockito.never()).attributeUpdated(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.same(value), ArgumentMatchers.any());
        Mockito.verify(listener, Mockito.never()).attributeRemoved(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.any());
        Mockito.verify(context).close();
    }

    @Test
    public void setNullAttribute() {
        String name = "name";
        Object value = null;
        this.validate(( session) -> session.setAttribute(name, value));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Object expected = new Object();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.removeAttribute(name)).thenReturn(expected);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.setAttribute(name, value);
        Assert.assertSame(expected, result);
        Mockito.verify(listener, Mockito.never()).attributeAdded(this.adapter, name, value);
        Mockito.verify(listener, Mockito.never()).attributeUpdated(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.same(value), ArgumentMatchers.any());
        Mockito.verify(listener).attributeRemoved(this.adapter, name, expected);
        Mockito.verify(context).close();
    }

    @Test
    public void setSameAttribute() {
        String name = "name";
        Integer value = Integer.valueOf(1);
        this.validate(( session) -> session.setAttribute(name, value));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Object expected = value;
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.setAttribute(name, value)).thenReturn(expected);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Object result = this.adapter.setAttribute(name, value);
        Assert.assertSame(expected, result);
        Mockito.verify(listener, Mockito.never()).attributeAdded(this.adapter, name, value);
        Mockito.verify(listener, Mockito.never()).attributeUpdated(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.same(value), ArgumentMatchers.any());
        Mockito.verify(listener, Mockito.never()).attributeRemoved(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.any());
        Mockito.verify(context).close();
    }

    @Test
    public void setAuthenticatedSessionAttribute() {
        String name = (CachedAuthenticatedSessionHandler.class.getName()) + ".AuthenticatedSession";
        Account account = Mockito.mock(Account.class);
        AuthenticatedSession auth = new AuthenticatedSession(account, HttpServletRequest.FORM_AUTH);
        this.validate(( session) -> session.setAttribute(name, "bar"));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        Account oldAccount = Mockito.mock(Account.class);
        AuthenticatedSession oldAuth = new AuthenticatedSession(oldAccount, HttpServletRequest.FORM_AUTH);
        ArgumentCaptor<AuthenticatedSession> capturedAuth = ArgumentCaptor.forClass(AuthenticatedSession.class);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.setAttribute(ArgumentMatchers.same(name), capturedAuth.capture())).thenReturn(oldAuth);
        AuthenticatedSession result = ((AuthenticatedSession) (this.adapter.setAttribute(name, auth)));
        Assert.assertSame(auth.getAccount(), capturedAuth.getValue().getAccount());
        Assert.assertSame(auth.getMechanism(), capturedAuth.getValue().getMechanism());
        Assert.assertSame(oldAccount, result.getAccount());
        Assert.assertSame(FORM_AUTH, result.getMechanism());
        Mockito.verify(context).close();
        Mockito.reset(context, attributes);
        capturedAuth = ArgumentCaptor.forClass(AuthenticatedSession.class);
        Mockito.when(attributes.setAttribute(ArgumentMatchers.same(name), capturedAuth.capture())).thenReturn(null);
        result = ((AuthenticatedSession) (this.adapter.setAttribute(name, auth)));
        Assert.assertSame(auth.getAccount(), capturedAuth.getValue().getAccount());
        Assert.assertSame(auth.getMechanism(), capturedAuth.getValue().getMechanism());
        Assert.assertNull(result);
        Mockito.verify(context).close();
        Mockito.reset(context, attributes);
        auth = new AuthenticatedSession(account, HttpServletRequest.BASIC_AUTH);
        AuthenticatedSession oldSession = new AuthenticatedSession(oldAccount, HttpServletRequest.BASIC_AUTH);
        LocalSessionContext localContext = Mockito.mock(LocalSessionContext.class);
        Mockito.when(this.session.getLocalContext()).thenReturn(localContext);
        Mockito.when(localContext.getAuthenticatedSession()).thenReturn(oldSession);
        result = ((AuthenticatedSession) (this.adapter.setAttribute(name, auth)));
        Mockito.verify(localContext).setAuthenticatedSession(ArgumentMatchers.same(auth));
        Mockito.verify(context).close();
    }

    @Test
    public void removeAttribute() {
        String name = "name";
        this.validate(( session) -> session.removeAttribute(name));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Object expected = new Object();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.removeAttribute(name)).thenReturn(expected);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.removeAttribute(name);
        Assert.assertSame(expected, result);
        Mockito.verify(listener).attributeRemoved(this.adapter, name, expected);
        Mockito.verify(context).close();
    }

    @Test
    public void removeNonExistingAttribute() {
        String name = "name";
        this.validate(( session) -> session.removeAttribute(name));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.removeAttribute(name)).thenReturn(null);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Object result = this.adapter.removeAttribute(name);
        Assert.assertNull(result);
        Mockito.verify(listener, Mockito.never()).attributeRemoved(ArgumentMatchers.same(this.adapter), ArgumentMatchers.same(name), ArgumentMatchers.any());
        Mockito.verify(context).close();
    }

    @Test
    public void invalidate() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        this.validate(exchange, ( session) -> session.invalidate(exchange));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        String sessionId = "session";
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        Mockito.when(this.session.getId()).thenReturn(sessionId);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        this.adapter.invalidate(exchange);
        Mockito.verify(this.session).invalidate();
        Mockito.verify(this.config).clearSession(exchange, sessionId);
        Mockito.verify(listener).sessionDestroyed(this.adapter, exchange, INVALIDATED);
        Mockito.verify(this.batch).close();
        Mockito.verify(context).close();
        Mockito.verify(this.closeTask).accept(exchange);
    }

    @Test
    public void getSessionManager() {
        Assert.assertSame(this.manager, this.adapter.getSessionManager());
    }

    @Test
    public void changeSessionId() {
        HttpServerExchange exchange = new HttpServerExchange(null);
        SessionConfig config = Mockito.mock(SessionConfig.class);
        this.validate(exchange, ( session) -> session.changeSessionId(exchange, config));
        SessionManager<LocalSessionContext, Batch> manager = Mockito.mock(SessionManager.class);
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        BatchContext context = Mockito.mock(BatchContext.class);
        Session<LocalSessionContext> session = Mockito.mock(Session.class);
        SessionAttributes oldAttributes = Mockito.mock(SessionAttributes.class);
        SessionAttributes newAttributes = Mockito.mock(SessionAttributes.class);
        SessionMetaData oldMetaData = Mockito.mock(SessionMetaData.class);
        SessionMetaData newMetaData = Mockito.mock(SessionMetaData.class);
        LocalSessionContext oldContext = Mockito.mock(LocalSessionContext.class);
        LocalSessionContext newContext = Mockito.mock(LocalSessionContext.class);
        SessionListener listener = Mockito.mock(SessionListener.class);
        SessionListeners listeners = new SessionListeners();
        listeners.addSessionListener(listener);
        String oldSessionId = "old";
        String newSessionId = "new";
        String name = "name";
        Object value = new Object();
        Instant now = Instant.now();
        Duration interval = Duration.ofSeconds(10L);
        AuthenticatedSession authenticatedSession = new AuthenticatedSession(null, null);
        Mockito.when(this.manager.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(manager.createIdentifier()).thenReturn(newSessionId);
        Mockito.when(manager.createSession(newSessionId)).thenReturn(session);
        Mockito.when(this.session.getAttributes()).thenReturn(oldAttributes);
        Mockito.when(this.session.getMetaData()).thenReturn(oldMetaData);
        Mockito.when(session.getAttributes()).thenReturn(newAttributes);
        Mockito.when(session.getMetaData()).thenReturn(newMetaData);
        Mockito.when(oldAttributes.getAttributeNames()).thenReturn(Collections.singleton(name));
        Mockito.when(oldAttributes.getAttribute(name)).thenReturn(value);
        Mockito.when(newAttributes.setAttribute(name, value)).thenReturn(null);
        Mockito.when(oldMetaData.getLastAccessedTime()).thenReturn(now);
        Mockito.when(oldMetaData.getMaxInactiveInterval()).thenReturn(interval);
        Mockito.when(this.session.getId()).thenReturn(oldSessionId);
        Mockito.when(session.getId()).thenReturn(newSessionId);
        Mockito.when(this.session.getLocalContext()).thenReturn(oldContext);
        Mockito.when(session.getLocalContext()).thenReturn(newContext);
        Mockito.when(oldContext.getAuthenticatedSession()).thenReturn(authenticatedSession);
        Mockito.when(this.manager.getSessionListeners()).thenReturn(listeners);
        String result = this.adapter.changeSessionId(exchange, config);
        Assert.assertSame(newSessionId, result);
        Mockito.verify(newMetaData).setLastAccessedTime(now);
        Mockito.verify(newMetaData).setMaxInactiveInterval(interval);
        Mockito.verify(config).setSessionId(exchange, newSessionId);
        Mockito.verify(newContext).setAuthenticatedSession(ArgumentMatchers.same(authenticatedSession));
        Mockito.verify(listener).sessionIdChanged(this.adapter, oldSessionId);
        Mockito.verify(context).close();
    }
}

