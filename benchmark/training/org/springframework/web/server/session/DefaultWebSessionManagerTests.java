/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.server.session;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link DefaultWebSessionManager}.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultWebSessionManagerTests {
    private DefaultWebSessionManager sessionManager;

    private ServerWebExchange exchange;

    @Mock
    private WebSessionIdResolver sessionIdResolver;

    @Mock
    private WebSessionStore sessionStore;

    @Mock
    private WebSession createSession;

    @Mock
    private WebSession updateSession;

    @Test
    public void getSessionSaveWhenCreatedAndNotStartedThenNotSaved() {
        Mockito.when(this.sessionIdResolver.resolveSessionIds(this.exchange)).thenReturn(Collections.emptyList());
        WebSession session = this.sessionManager.getSession(this.exchange).block();
        this.exchange.getResponse().setComplete().block();
        Assert.assertSame(this.createSession, session);
        Assert.assertFalse(session.isStarted());
        Assert.assertFalse(session.isExpired());
        Mockito.verify(this.createSession, Mockito.never()).save();
        Mockito.verify(this.sessionIdResolver, Mockito.never()).setSessionId(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void getSessionSaveWhenCreatedAndStartedThenSavesAndSetsId() {
        Mockito.when(this.sessionIdResolver.resolveSessionIds(this.exchange)).thenReturn(Collections.emptyList());
        WebSession session = this.sessionManager.getSession(this.exchange).block();
        Assert.assertSame(this.createSession, session);
        String sessionId = this.createSession.getId();
        Mockito.when(this.createSession.isStarted()).thenReturn(true);
        this.exchange.getResponse().setComplete().block();
        Mockito.verify(this.sessionStore).createWebSession();
        Mockito.verify(this.sessionIdResolver).setSessionId(ArgumentMatchers.any(), ArgumentMatchers.eq(sessionId));
        Mockito.verify(this.createSession).save();
    }

    @Test
    public void existingSession() {
        String sessionId = this.updateSession.getId();
        Mockito.when(this.sessionIdResolver.resolveSessionIds(this.exchange)).thenReturn(Collections.singletonList(sessionId));
        WebSession actual = this.sessionManager.getSession(this.exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertEquals(sessionId, actual.getId());
    }

    @Test
    public void multipleSessionIds() {
        List<String> ids = Arrays.asList("not-this", "not-that", this.updateSession.getId());
        Mockito.when(this.sessionStore.retrieveSession("not-this")).thenReturn(Mono.empty());
        Mockito.when(this.sessionStore.retrieveSession("not-that")).thenReturn(Mono.empty());
        Mockito.when(this.sessionIdResolver.resolveSessionIds(this.exchange)).thenReturn(ids);
        WebSession actual = this.sessionManager.getSession(this.exchange).block();
        Assert.assertNotNull(actual);
        Assert.assertEquals(this.updateSession.getId(), actual.getId());
    }
}

