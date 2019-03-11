/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.session;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class SessionManagementFilterTests {
    @Test
    public void newSessionShouldNotBeCreatedIfSessionExistsAndUserIsNotAuthenticated() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo);
        HttpServletRequest request = new MockHttpServletRequest();
        String sessionId = request.getSession().getId();
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertThat(request.getSession().getId()).isEqualTo(sessionId);
    }

    @Test
    public void strategyIsNotInvokedIfSecurityContextAlreadyExistsForRequest() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        SessionAuthenticationStrategy strategy = Mockito.mock(SessionAuthenticationStrategy.class);
        // mock that repo contains a security context
        Mockito.when(repo.containsContext(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(true);
        SessionManagementFilter filter = new SessionManagementFilter(repo, strategy);
        HttpServletRequest request = new MockHttpServletRequest();
        authenticateUser();
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        Mockito.verifyZeroInteractions(strategy);
    }

    @Test
    public void strategyIsNotInvokedIfAuthenticationIsNull() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        SessionAuthenticationStrategy strategy = Mockito.mock(SessionAuthenticationStrategy.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo, strategy);
        HttpServletRequest request = new MockHttpServletRequest();
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        Mockito.verifyZeroInteractions(strategy);
    }

    @Test
    public void strategyIsInvokedIfUserIsNewlyAuthenticated() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        // repo will return false to containsContext()
        SessionAuthenticationStrategy strategy = Mockito.mock(SessionAuthenticationStrategy.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo, strategy);
        HttpServletRequest request = new MockHttpServletRequest();
        authenticateUser();
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        Mockito.verify(strategy).onAuthentication(ArgumentMatchers.any(Authentication.class), ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
        // Check that it is only applied once to the request
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        Mockito.verifyNoMoreInteractions(strategy);
    }

    @Test
    public void strategyFailureInvokesFailureHandler() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        // repo will return false to containsContext()
        SessionAuthenticationStrategy strategy = Mockito.mock(SessionAuthenticationStrategy.class);
        AuthenticationFailureHandler failureHandler = Mockito.mock(AuthenticationFailureHandler.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo, strategy);
        filter.setAuthenticationFailureHandler(failureHandler);
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain fc = Mockito.mock(FilterChain.class);
        authenticateUser();
        SessionAuthenticationException exception = new SessionAuthenticationException("Failure");
        Mockito.doThrow(exception).when(strategy).onAuthentication(SecurityContextHolder.getContext().getAuthentication(), request, response);
        filter.doFilter(request, response, fc);
        Mockito.verifyZeroInteractions(fc);
        Mockito.verify(failureHandler).onAuthenticationFailure(request, response, exception);
    }

    @Test
    public void responseIsRedirectedToTimeoutUrlIfSetAndSessionIsInvalid() throws Exception {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        // repo will return false to containsContext()
        SessionAuthenticationStrategy strategy = Mockito.mock(SessionAuthenticationStrategy.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo, strategy);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestedSessionId("xxx");
        request.setRequestedSessionIdValid(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertThat(response.getRedirectedUrl()).isNull();
        // Now set a redirect URL
        request = new MockHttpServletRequest();
        request.setRequestedSessionId("xxx");
        request.setRequestedSessionIdValid(false);
        SimpleRedirectInvalidSessionStrategy iss = new SimpleRedirectInvalidSessionStrategy("/timedOut");
        iss.setCreateNewSession(true);
        filter.setInvalidSessionStrategy(iss);
        FilterChain fc = Mockito.mock(FilterChain.class);
        filter.doFilter(request, response, fc);
        Mockito.verifyZeroInteractions(fc);
        assertThat(response.getRedirectedUrl()).isEqualTo("/timedOut");
    }

    @Test
    public void customAuthenticationTrustResolver() throws Exception {
        AuthenticationTrustResolver trustResolver = Mockito.mock(AuthenticationTrustResolver.class);
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo);
        filter.setTrustResolver(trustResolver);
        HttpServletRequest request = new MockHttpServletRequest();
        authenticateUser();
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        Mockito.verify(trustResolver).isAnonymous(ArgumentMatchers.any(Authentication.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTrustResolverNull() {
        SecurityContextRepository repo = Mockito.mock(SecurityContextRepository.class);
        SessionManagementFilter filter = new SessionManagementFilter(repo);
        filter.setTrustResolver(null);
    }
}

