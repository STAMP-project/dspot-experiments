/**
 * Copyright 2012-2019 the original author or authors.
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
import AuthenticationAuditListener.AUTHENTICATION_SUCCESS;
import AuthenticationAuditListener.AUTHENTICATION_SWITCH;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;


/**
 * Tests for {@link AuthenticationAuditListener}.
 */
public class AuthenticationAuditListenerTests {
    private final AuthenticationAuditListener listener = new AuthenticationAuditListener();

    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);

    @Test
    public void testAuthenticationSuccess() {
        AuditApplicationEvent event = handleAuthenticationEvent(new org.springframework.security.authentication.event.AuthenticationSuccessEvent(new UsernamePasswordAuthenticationToken("user", "password")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_SUCCESS);
    }

    @Test
    public void testOtherAuthenticationSuccess() {
        this.listener.onApplicationEvent(new org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent(new UsernamePasswordAuthenticationToken("user", "password"), getClass()));
        // No need to audit this one (it shadows a regular AuthenticationSuccessEvent)
        Mockito.verify(this.publisher, Mockito.never()).publishEvent(ArgumentMatchers.any(ApplicationEvent.class));
    }

    @Test
    public void testAuthenticationFailed() {
        AuditApplicationEvent event = handleAuthenticationEvent(new org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent(new UsernamePasswordAuthenticationToken("user", "password"), new BadCredentialsException("Bad user")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_FAILURE);
    }

    @Test
    public void testAuthenticationSwitch() {
        AuditApplicationEvent event = handleAuthenticationEvent(new org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent(new UsernamePasswordAuthenticationToken("user", "password"), new org.springframework.security.core.userdetails.User("user", "password", AuthorityUtils.commaSeparatedStringToAuthorityList("USER"))));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_SWITCH);
    }

    @Test
    public void testAuthenticationSwitchBackToAnonymous() {
        AuditApplicationEvent event = handleAuthenticationEvent(new org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent(new UsernamePasswordAuthenticationToken("user", "password"), null));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_SWITCH);
    }

    @Test
    public void testDetailsAreIncludedInAuditEvent() {
        Object details = new Object();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", "password");
        authentication.setDetails(details);
        AuditApplicationEvent event = handleAuthenticationEvent(new org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent(authentication, new BadCredentialsException("Bad user")));
        assertThat(event.getAuditEvent().getType()).isEqualTo(AUTHENTICATION_FAILURE);
        assertThat(event.getAuditEvent().getData()).containsEntry("details", details);
    }
}

