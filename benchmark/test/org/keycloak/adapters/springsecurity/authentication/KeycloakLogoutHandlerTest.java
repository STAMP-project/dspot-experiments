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


import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.junit.Test;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;


/**
 * Keycloak logout handler tests.
 */
public class KeycloakLogoutHandlerTest {
    private KeycloakAuthenticationToken keycloakAuthenticationToken;

    private KeycloakLogoutHandler keycloakLogoutHandler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Mock
    private AdapterDeploymentContext adapterDeploymentContext;

    @Mock
    private OidcKeycloakAccount keycloakAccount;

    @Mock
    private KeycloakDeployment keycloakDeployment;

    @Mock
    private RefreshableKeycloakSecurityContext session;

    private Collection<KeycloakRole> authorities = Collections.singleton(new KeycloakRole(UUID.randomUUID().toString()));

    @Test
    public void testLogout() throws Exception {
        keycloakLogoutHandler.logout(request, response, keycloakAuthenticationToken);
        Mockito.verify(session).logout(ArgumentMatchers.eq(keycloakDeployment));
    }

    @Test
    public void testLogoutAnonymousAuthentication() throws Exception {
        Authentication authentication = new org.springframework.security.authentication.AnonymousAuthenticationToken(UUID.randomUUID().toString(), UUID.randomUUID().toString(), authorities);
        keycloakLogoutHandler.logout(request, response, authentication);
        Mockito.verifyZeroInteractions(session);
    }

    @Test
    public void testLogoutUsernamePasswordAuthentication() throws Exception {
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(UUID.randomUUID().toString(), UUID.randomUUID().toString(), authorities);
        keycloakLogoutHandler.logout(request, response, authentication);
        Mockito.verifyZeroInteractions(session);
    }

    @Test
    public void testLogoutRememberMeAuthentication() throws Exception {
        Authentication authentication = new org.springframework.security.authentication.RememberMeAuthenticationToken(UUID.randomUUID().toString(), UUID.randomUUID().toString(), authorities);
        keycloakLogoutHandler.logout(request, response, authentication);
        Mockito.verifyZeroInteractions(session);
    }

    @Test
    public void testLogoutNullAuthentication() throws Exception {
        keycloakLogoutHandler.logout(request, response, null);
        Mockito.verifyZeroInteractions(session);
    }

    @Test
    public void testHandleSingleSignOut() throws Exception {
        keycloakLogoutHandler.handleSingleSignOut(request, response, keycloakAuthenticationToken);
        Mockito.verify(session).logout(ArgumentMatchers.eq(keycloakDeployment));
    }
}

