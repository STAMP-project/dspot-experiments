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
package org.keycloak.adapters.springsecurity.authentication;


import AccessToken.Access;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OAuthRequestAuthenticator;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Spring Security request authenticator tests.
 */
public class SpringSecurityRequestAuthenticatorTest {
    private SpringSecurityRequestAuthenticator authenticator;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Mock
    private KeycloakDeployment deployment;

    @Mock
    private AdapterTokenStore tokenStore;

    @Mock
    private KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal;

    @Mock
    private AccessToken accessToken;

    @Mock
    private Access access;

    @Mock
    private RefreshableKeycloakSecurityContext refreshableKeycloakSecurityContext;

    @Test
    public void testCreateOAuthAuthenticator() throws Exception {
        OAuthRequestAuthenticator oathAuthenticator = authenticator.createOAuthAuthenticator();
        Assert.assertNotNull(oathAuthenticator);
    }

    @Test
    public void testCompleteOAuthAuthentication() throws Exception {
        authenticator.completeOAuthAuthentication(principal);
        Mockito.verify(request).setAttribute(ArgumentMatchers.eq(KeycloakSecurityContext.class.getName()), ArgumentMatchers.eq(refreshableKeycloakSecurityContext));
        Mockito.verify(tokenStore).saveAccountInfo(ArgumentMatchers.any(OidcKeycloakAccount.class));// FIXME: should verify account

    }

    @Test
    public void testCompleteBearerAuthentication() throws Exception {
        authenticator.completeBearerAuthentication(principal, "foo");
        Mockito.verify(request).setAttribute(ArgumentMatchers.eq(KeycloakSecurityContext.class.getName()), ArgumentMatchers.eq(refreshableKeycloakSecurityContext));
        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        Assert.assertTrue(KeycloakAuthenticationToken.class.isAssignableFrom(SecurityContextHolder.getContext().getAuthentication().getClass()));
    }

    @Test
    public void testGetHttpSessionIdTrue() throws Exception {
        String sessionId = authenticator.changeHttpSessionId(true);
        Assert.assertNotNull(sessionId);
    }

    @Test
    public void testGetHttpSessionIdFalse() throws Exception {
        String sessionId = authenticator.changeHttpSessionId(false);
        Assert.assertNull(sessionId);
    }
}

