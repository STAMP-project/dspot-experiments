/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.wildfly.clustering.web.undertow.sso;


import HttpServletRequest.CLIENT_CERT_AUTH;
import io.undertow.security.api.AuthenticatedSessionManager.AuthenticatedSession;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.BatchContext;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.web.sso.SSO;
import org.wildfly.clustering.web.sso.Sessions;


/**
 * Unit test for {@link DistributableSingleSignOn}
 *
 * @author Paul Ferraro
 */
public class DistributableSingleSignOnTestCase {
    private final SSO<AuthenticatedSession, String, String, Void> sso = Mockito.mock(SSO.class);

    private final SessionManagerRegistry registry = Mockito.mock(SessionManagerRegistry.class);

    private final Batcher<Batch> batcher = Mockito.mock(Batcher.class);

    private final Batch batch = Mockito.mock(Batch.class);

    private final InvalidatableSingleSignOn subject = new DistributableSingleSignOn(this.sso, this.registry, this.batcher, this.batch);

    @Test
    public void getId() {
        String id = "sso";
        Mockito.when(this.sso.getId()).thenReturn(id);
        String result = this.subject.getId();
        Assert.assertSame(id, result);
        Mockito.verifyZeroInteractions(this.batch);
    }

    @Test
    public void getAccount() {
        BatchContext context = Mockito.mock(BatchContext.class);
        Account account = Mockito.mock(Account.class);
        String mechanism = HttpServletRequest.BASIC_AUTH;
        AuthenticatedSession authentication = new AuthenticatedSession(account, mechanism);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.sso.getAuthentication()).thenReturn(authentication);
        Account result = this.subject.getAccount();
        Assert.assertSame(account, result);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void getMechanismName() {
        BatchContext context = Mockito.mock(BatchContext.class);
        Account account = Mockito.mock(Account.class);
        String mechanism = HttpServletRequest.CLIENT_CERT_AUTH;
        AuthenticatedSession authentication = new AuthenticatedSession(account, mechanism);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.sso.getAuthentication()).thenReturn(authentication);
        String result = this.subject.getMechanismName();
        Assert.assertEquals(CLIENT_CERT_AUTH, result);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void iterator() {
        BatchContext context = Mockito.mock(BatchContext.class);
        Sessions<String, String> sessions = Mockito.mock(Sessions.class);
        SessionManager manager = Mockito.mock(SessionManager.class);
        Session session = Mockito.mock(Session.class);
        String deployment = "deployment";
        String sessionId = "session";
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(this.sso.getSessions()).thenReturn(sessions);
        Mockito.when(sessions.getDeployments()).thenReturn(Collections.singleton(deployment));
        Mockito.when(sessions.getSession(deployment)).thenReturn(sessionId);
        Mockito.when(this.registry.getSessionManager(deployment)).thenReturn(manager);
        Mockito.when(manager.getSession(sessionId)).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(sessionId);
        Iterator<Session> results = this.subject.iterator();
        Assert.assertTrue(results.hasNext());
        Session result = results.next();
        Assert.assertEquals(session.getId(), result.getId());
        Assert.assertFalse(results.hasNext());
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
        // Validate that returned sessions can be invalidated
        HttpServerExchange exchange = new HttpServerExchange(null);
        Session mutableSession = Mockito.mock(Session.class);
        Mockito.when(session.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getSession(ArgumentMatchers.same(exchange), ArgumentMatchers.any())).thenReturn(mutableSession);
        result.invalidate(exchange);
        Mockito.verify(mutableSession).invalidate(ArgumentMatchers.same(exchange));
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void contains() {
        String deployment = "deployment";
        BatchContext context = Mockito.mock(BatchContext.class);
        Session session = Mockito.mock(Session.class);
        SessionManager manager = Mockito.mock(SessionManager.class);
        Sessions<String, String> sessions = Mockito.mock(Sessions.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(session.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getDeploymentName()).thenReturn(deployment);
        Mockito.when(this.sso.getSessions()).thenReturn(sessions);
        Mockito.when(sessions.getDeployments()).thenReturn(Collections.<String>emptySet());
        boolean result = this.subject.contains(session);
        Assert.assertFalse(result);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
        Mockito.reset(context);
        Mockito.when(sessions.getDeployments()).thenReturn(Collections.singleton(deployment));
        result = this.subject.contains(session);
        Assert.assertTrue(result);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void add() {
        String deployment = "deployment";
        String sessionId = "session";
        BatchContext context = Mockito.mock(BatchContext.class);
        Session session = Mockito.mock(Session.class);
        SessionManager manager = Mockito.mock(SessionManager.class);
        Sessions<String, String> sessions = Mockito.mock(Sessions.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(session.getId()).thenReturn(sessionId);
        Mockito.when(session.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getDeploymentName()).thenReturn(deployment);
        Mockito.when(this.sso.getSessions()).thenReturn(sessions);
        this.subject.add(session);
        Mockito.verify(sessions).addSession(deployment, sessionId);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void remove() {
        String deployment = "deployment";
        BatchContext context = Mockito.mock(BatchContext.class);
        Session session = Mockito.mock(Session.class);
        SessionManager manager = Mockito.mock(SessionManager.class);
        Sessions<String, String> sessions = Mockito.mock(Sessions.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(session.getSessionManager()).thenReturn(manager);
        Mockito.when(manager.getDeploymentName()).thenReturn(deployment);
        Mockito.when(this.sso.getSessions()).thenReturn(sessions);
        this.subject.remove(session);
        Mockito.verify(sessions).removeSession(deployment);
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void getSession() {
        String deployment = "deployment";
        String sessionId = "session";
        BatchContext context = Mockito.mock(BatchContext.class);
        SessionManager manager = Mockito.mock(SessionManager.class);
        Sessions<String, String> sessions = Mockito.mock(Sessions.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        Mockito.when(manager.getDeploymentName()).thenReturn(deployment);
        Mockito.when(this.sso.getSessions()).thenReturn(sessions);
        Mockito.when(sessions.getSession(deployment)).thenReturn(sessionId);
        Session result = this.subject.getSession(manager);
        Assert.assertSame(sessionId, result.getId());
        Assert.assertSame(manager, result.getSessionManager());
        Mockito.verifyZeroInteractions(this.batch);
        Mockito.verify(context).close();
    }

    @Test
    public void close() {
        BatchContext context = Mockito.mock(BatchContext.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        this.subject.close();
        Mockito.verify(this.batch).close();
        Mockito.verify(context).close();
        Mockito.reset(this.batch);
        this.subject.close();
        Mockito.verify(this.batch, Mockito.never()).close();
    }

    @Test
    public void invalidate() {
        BatchContext context = Mockito.mock(BatchContext.class);
        Mockito.when(this.batcher.resumeBatch(this.batch)).thenReturn(context);
        this.subject.invalidate();
        Mockito.verify(context).close();
    }
}

