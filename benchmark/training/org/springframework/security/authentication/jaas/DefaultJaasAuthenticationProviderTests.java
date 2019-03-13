/**
 * Copyright 2010-2016 the original author or authors.
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
package org.springframework.security.authentication.jaas;


import java.util.Arrays;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;


public class DefaultJaasAuthenticationProviderTests {
    private DefaultJaasAuthenticationProvider provider;

    private UsernamePasswordAuthenticationToken token;

    private ApplicationEventPublisher publisher;

    private Log log;

    @Test(expected = IllegalArgumentException.class)
    public void afterPropertiesSetNullConfiguration() throws Exception {
        provider.setConfiguration(null);
        provider.afterPropertiesSet();
    }

    @Test(expected = IllegalArgumentException.class)
    public void afterPropertiesSetNullAuthorityGranters() throws Exception {
        provider.setAuthorityGranters(null);
        provider.afterPropertiesSet();
    }

    @Test
    public void authenticateUnsupportedAuthentication() {
        assertThat(provider.authenticate(new TestingAuthenticationToken("user", "password"))).isNull();
    }

    @Test
    public void authenticateSuccess() throws Exception {
        Authentication auth = provider.authenticate(token);
        assertThat(auth.getPrincipal()).isEqualTo(token.getPrincipal());
        assertThat(auth.getCredentials()).isEqualTo(token.getCredentials());
        assertThat(auth.isAuthenticated()).isEqualTo(true);
        assertThat(auth.getAuthorities().isEmpty()).isEqualTo(false);
        Mockito.verify(publisher).publishEvent(ArgumentMatchers.isA(JaasAuthenticationSuccessEvent.class));
        Mockito.verifyNoMoreInteractions(publisher);
    }

    @Test
    public void authenticateBadPassword() {
        try {
            provider.authenticate(new UsernamePasswordAuthenticationToken("user", "asdf"));
            fail("LoginException should have been thrown for the bad password");
        } catch (AuthenticationException success) {
        }
        verifyFailedLogin();
    }

    @Test
    public void authenticateBadUser() {
        try {
            provider.authenticate(new UsernamePasswordAuthenticationToken("asdf", "password"));
            fail("LoginException should have been thrown for the bad user");
        } catch (AuthenticationException success) {
        }
        verifyFailedLogin();
    }

    @Test
    public void logout() throws Exception {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        JaasAuthenticationToken token = Mockito.mock(JaasAuthenticationToken.class);
        LoginContext context = Mockito.mock(LoginContext.class);
        Mockito.when(event.getSecurityContexts()).thenReturn(Arrays.asList(securityContext));
        Mockito.when(securityContext.getAuthentication()).thenReturn(token);
        Mockito.when(token.getLoginContext()).thenReturn(context);
        provider.onApplicationEvent(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(token).getLoginContext();
        Mockito.verify(context).logout();
        Mockito.verifyNoMoreInteractions(event, securityContext, token, context);
    }

    @Test
    public void logoutNullSession() {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        provider.handleLogout(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(log).debug(ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(event);
    }

    @Test
    public void logoutNullAuthentication() {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(event.getSecurityContexts()).thenReturn(Arrays.asList(securityContext));
        provider.handleLogout(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verifyNoMoreInteractions(event, securityContext);
    }

    @Test
    public void logoutNonJaasAuthentication() {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(event.getSecurityContexts()).thenReturn(Arrays.asList(securityContext));
        Mockito.when(securityContext.getAuthentication()).thenReturn(token);
        provider.handleLogout(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verifyNoMoreInteractions(event, securityContext);
    }

    @Test
    public void logoutNullLoginContext() throws Exception {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        JaasAuthenticationToken token = Mockito.mock(JaasAuthenticationToken.class);
        Mockito.when(event.getSecurityContexts()).thenReturn(Arrays.asList(securityContext));
        Mockito.when(securityContext.getAuthentication()).thenReturn(token);
        provider.onApplicationEvent(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(token).getLoginContext();
        Mockito.verifyNoMoreInteractions(event, securityContext, token);
    }

    @Test
    public void logoutLoginException() throws Exception {
        SessionDestroyedEvent event = Mockito.mock(SessionDestroyedEvent.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        JaasAuthenticationToken token = Mockito.mock(JaasAuthenticationToken.class);
        LoginContext context = Mockito.mock(LoginContext.class);
        LoginException loginException = new LoginException("Failed Login");
        Mockito.when(event.getSecurityContexts()).thenReturn(Arrays.asList(securityContext));
        Mockito.when(securityContext.getAuthentication()).thenReturn(token);
        Mockito.when(token.getLoginContext()).thenReturn(context);
        Mockito.doThrow(loginException).when(context).logout();
        provider.onApplicationEvent(event);
        Mockito.verify(event).getSecurityContexts();
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(token).getLoginContext();
        Mockito.verify(context).logout();
        Mockito.verify(log).warn(ArgumentMatchers.anyString(), ArgumentMatchers.eq(loginException));
        Mockito.verifyNoMoreInteractions(event, securityContext, token, context);
    }

    @Test
    public void publishNullPublisher() {
        provider.setApplicationEventPublisher(null);
        AuthenticationException ae = new BadCredentialsException("Failed to login");
        provider.publishFailureEvent(token, ae);
        provider.publishSuccessEvent(token);
    }

    @Test
    public void javadocExample() {
        String resName = ("/" + (getClass().getName().replace('.', '/'))) + ".xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(resName);
        context.registerShutdownHook();
        try {
            provider = context.getBean(DefaultJaasAuthenticationProvider.class);
            Authentication auth = provider.authenticate(token);
            assertThat(auth.isAuthenticated()).isEqualTo(true);
            assertThat(auth.getPrincipal()).isEqualTo(token.getPrincipal());
        } finally {
            context.close();
        }
    }
}

