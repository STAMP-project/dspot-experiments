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


import io.undertow.security.api.AuthenticatedSessionManager.AuthenticatedSession;
import io.undertow.security.idm.Account;
import io.undertow.security.impl.SingleSignOn;
import io.undertow.security.impl.SingleSignOnManager;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batch;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.web.sso.SSO;
import org.wildfly.clustering.web.sso.SSOManager;


/**
 * Unit test for {@link DistributableSingleSignOnManager}
 *
 * @author Paul Ferraro
 */
public class DistributableSingleSignOnManagerTestCase {
    private final SSOManager<AuthenticatedSession, String, String, Void, Batch> manager = Mockito.mock(SSOManager.class);

    private final SessionManagerRegistry registry = Mockito.mock(SessionManagerRegistry.class);

    private final SingleSignOnManager subject = new DistributableSingleSignOnManager(this.manager, this.registry);

    @Test
    public void createSingleSignOn() {
        String id = "sso";
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        Account account = Mockito.mock(Account.class);
        String mechanism = HttpServletRequest.BASIC_AUTH;
        SSO<AuthenticatedSession, String, String, Void> sso = Mockito.mock(SSO.class);
        ArgumentCaptor<AuthenticatedSession> authenticationCaptor = ArgumentCaptor.forClass(AuthenticatedSession.class);
        Mockito.when(this.manager.createIdentifier()).thenReturn(id);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(this.manager.createSSO(ArgumentMatchers.same(id), authenticationCaptor.capture())).thenReturn(sso);
        SingleSignOn result = this.subject.createSingleSignOn(account, mechanism);
        Mockito.verify(batcher).suspendBatch();
        Assert.assertNotNull(result);
        AuthenticatedSession capturedAuthentication = authenticationCaptor.getValue();
        Assert.assertNotNull(capturedAuthentication);
        Assert.assertSame(capturedAuthentication.getAccount(), account);
        Assert.assertSame(capturedAuthentication.getMechanism(), mechanism);
    }

    @Test
    public void findSingleSignOnNotExists() {
        String id = "sso";
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(this.manager.findSSO(id)).thenReturn(null);
        SingleSignOn result = this.subject.findSingleSignOn(id);
        Assert.assertNull(result);
        Mockito.verify(batch).close();
        Mockito.verify(batcher, Mockito.never()).suspendBatch();
    }

    @Test
    public void findSingleSignOnInvalidCharacters() {
        String id = "sso+";
        SingleSignOn result = this.subject.findSingleSignOn(id);
        Assert.assertNull(result);
        Mockito.verifyZeroInteractions(this.manager);
    }

    @Test
    public void findSingleSignOn() {
        String id = "sso";
        Batcher<Batch> batcher = Mockito.mock(Batcher.class);
        Batch batch = Mockito.mock(Batch.class);
        SSO<AuthenticatedSession, String, String, Void> sso = Mockito.mock(SSO.class);
        Mockito.when(this.manager.getBatcher()).thenReturn(batcher);
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(this.manager.findSSO(id)).thenReturn(sso);
        SingleSignOn result = this.subject.findSingleSignOn(id);
        Assert.assertNotNull(result);
        Mockito.verify(batcher).suspendBatch();
    }

    @Test
    public void removeSingleSignOn() {
        InvalidatableSingleSignOn sso = Mockito.mock(InvalidatableSingleSignOn.class);
        this.subject.removeSingleSignOn(sso);
        Mockito.verify(sso).invalidate();
    }
}

