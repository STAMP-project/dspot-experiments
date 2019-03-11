/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.adapters.springsecurity.filter;


import HttpServletResponse.SC_UNAUTHORIZED;
import SslRequired.NONE;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.KeycloakAuthenticationException;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationFailureHandler;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * Keycloak authentication process filter test cases.
 */
public class KeycloakAuthenticationProcessingFilterTest {
    private KeycloakAuthenticationProcessingFilter filter;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AdapterDeploymentContext adapterDeploymentContext;

    @Mock
    private FilterChain chain;

    private MockHttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private AuthenticationSuccessHandler successHandler;

    @Mock
    private AuthenticationFailureHandler failureHandler;

    private KeycloakAuthenticationFailureHandler keycloakFailureHandler;

    @Mock
    private OidcKeycloakAccount keycloakAccount;

    @Mock
    private KeycloakDeployment keycloakDeployment;

    @Mock
    private KeycloakSecurityContext keycloakSecurityContext;

    private final List<? extends GrantedAuthority> authorities = Collections.singletonList(new KeycloakRole("ROLE_USER"));

    @Test
    public void testAttemptAuthenticationExpectRedirect() throws Exception {
        Mockito.when(keycloakDeployment.getAuthUrl()).thenReturn(KeycloakUriBuilder.fromUri("http://localhost:8080/auth"));
        Mockito.when(keycloakDeployment.getResourceName()).thenReturn("resource-name");
        Mockito.when(keycloakDeployment.getStateCookieName()).thenReturn("kc-cookie");
        Mockito.when(keycloakDeployment.getSslRequired()).thenReturn(NONE);
        Mockito.when(keycloakDeployment.isBearerOnly()).thenReturn(Boolean.FALSE);
        filter.attemptAuthentication(request, response);
        Mockito.verify(response).setStatus(302);
        Mockito.verify(response).setHeader(ArgumentMatchers.eq("Location"), ArgumentMatchers.startsWith("http://localhost:8080/auth"));
    }

    @Test(expected = KeycloakAuthenticationException.class)
    public void testAttemptAuthenticationWithInvalidToken() throws Exception {
        request.addHeader("Authorization", "Bearer xxx");
        filter.attemptAuthentication(request, response);
    }

    @Test(expected = KeycloakAuthenticationException.class)
    public void testAttemptAuthenticationWithInvalidTokenBearerOnly() throws Exception {
        Mockito.when(keycloakDeployment.isBearerOnly()).thenReturn(Boolean.TRUE);
        request.addHeader("Authorization", "Bearer xxx");
        filter.attemptAuthentication(request, response);
    }

    @Test
    public void testSuccessfulAuthenticationInteractive() throws Exception {
        Authentication authentication = new org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken(keycloakAccount, true, authorities);
        filter.successfulAuthentication(request, response, chain, authentication);
        Mockito.verify(successHandler).onAuthenticationSuccess(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response), ArgumentMatchers.eq(authentication));
        Mockito.verify(chain, Mockito.never()).doFilter(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class));
    }

    @Test
    public void testSuccessfulAuthenticationBearer() throws Exception {
        Authentication authentication = new org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken(keycloakAccount, false, authorities);
        this.setBearerAuthHeader(request);
        filter.successfulAuthentication(request, response, chain, authentication);
        Mockito.verify(chain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        Mockito.verify(successHandler, Mockito.never()).onAuthenticationSuccess(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
    }

    @Test
    public void testSuccessfulAuthenticationBasicAuth() throws Exception {
        Authentication authentication = new org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken(keycloakAccount, false, authorities);
        this.setBasicAuthHeader(request);
        filter.successfulAuthentication(request, response, chain, authentication);
        Mockito.verify(chain).doFilter(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response));
        Mockito.verify(successHandler, Mockito.never()).onAuthenticationSuccess(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
    }

    @Test
    public void testUnsuccessfulAuthenticationInteractive() throws Exception {
        AuthenticationException exception = new BadCredentialsException("OOPS");
        filter.unsuccessfulAuthentication(request, response, exception);
        Mockito.verify(failureHandler).onAuthenticationFailure(ArgumentMatchers.eq(request), ArgumentMatchers.eq(response), ArgumentMatchers.eq(exception));
    }

    @Test
    public void testUnsuccessfulAuthenticatioBearer() throws Exception {
        AuthenticationException exception = new BadCredentialsException("OOPS");
        this.setBearerAuthHeader(request);
        filter.unsuccessfulAuthentication(request, response, exception);
        Mockito.verify(failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void testUnsuccessfulAuthenticatioBasicAuth() throws Exception {
        AuthenticationException exception = new BadCredentialsException("OOPS");
        this.setBasicAuthHeader(request);
        filter.unsuccessfulAuthentication(request, response, exception);
        Mockito.verify(failureHandler).onAuthenticationFailure(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(AuthenticationException.class));
    }

    @Test
    public void testDefaultFailureHanlder() throws Exception {
        AuthenticationException exception = new BadCredentialsException("OOPS");
        filter.setAuthenticationFailureHandler(keycloakFailureHandler);
        filter.unsuccessfulAuthentication(request, response, exception);
        Mockito.verify(response).sendError(ArgumentMatchers.eq(SC_UNAUTHORIZED), ArgumentMatchers.any(String.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetAllowSessionCreation() throws Exception {
        filter.setAllowSessionCreation(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetContinueChainBeforeSuccessfulAuthentication() throws Exception {
        filter.setContinueChainBeforeSuccessfulAuthentication(true);
    }
}

