/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.security;


import AuthenticationAuditListener.AUTHENTICATION_FAILURE;
import AuthorizationAuditListener.AUTHORIZATION_FAILURE;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


/**
 * Tests for {@link AuthorizationAuditListener}.
 */
public class AuthorizationAuditListenerTests {
    private final AuthorizationAuditListener listener = new AuthorizationAuditListener();

    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);

    @Test
    public void testAuthenticationCredentialsNotFound() {
        AuditApplicationEvent event = handleAuthorizationEvent(new org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent(this, Collections.singletonList(new SecurityConfig("USER")), new AuthenticationCredentialsNotFoundException("Bad user")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_FAILURE);
    }

    @Test
    public void testAuthorizationFailure() {
        AuditApplicationEvent event = handleAuthorizationEvent(new org.springframework.security.access.event.AuthorizationFailureEvent(this, Collections.singletonList(new SecurityConfig("USER")), new UsernamePasswordAuthenticationToken("user", "password"), new AccessDeniedException("Bad user")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHORIZATION_FAILURE);
    }

    @Test
    public void testDetailsAreIncludedInAuditEvent() {
        Object details = new Object();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", "password");
        authentication.setDetails(details);
        AuditApplicationEvent event = handleAuthorizationEvent(new org.springframework.security.access.event.AuthorizationFailureEvent(this, Collections.singletonList(new SecurityConfig("USER")), authentication, new AccessDeniedException("Bad user")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHORIZATION_FAILURE);
        assertThat(event.getAuditEvent().getData()).containsEntry("details", details);
    }
}

