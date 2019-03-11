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
package org.keycloak.adapters.springsecurity.token;


import java.security.Principal;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.RequestAuthenticator;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Spring Security token store tests.
 */
public class SpringSecurityTokenStoreTest {
    private SpringSecurityTokenStore store;

    @Mock
    private KeycloakDeployment deployment;

    @Mock
    private Principal principal;

    @Mock
    private RequestAuthenticator requestAuthenticator;

    @Mock
    private RefreshableKeycloakSecurityContext keycloakSecurityContext;

    private MockHttpServletRequest request;

    @Test
    public void testIsCached() throws Exception {
        Authentication authentication = new org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken("foo", "bar", Collections.singleton(new KeycloakRole("ROLE_FOO")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assert.assertFalse(store.isCached(requestAuthenticator));
    }

    @Test
    public void testSaveAccountInfo() throws Exception {
        OidcKeycloakAccount account = new org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount(principal, Collections.singleton("FOO"), keycloakSecurityContext);
        Authentication authentication;
        store.saveAccountInfo(account);
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull(authentication);
        Assert.assertTrue((authentication instanceof KeycloakAuthenticationToken));
    }

    @Test(expected = IllegalStateException.class)
    public void testSaveAccountInfoInvalidAuthenticationType() throws Exception {
        OidcKeycloakAccount account = new org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount(principal, Collections.singleton("FOO"), keycloakSecurityContext);
        Authentication authentication = new org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken("foo", "bar", Collections.singleton(new KeycloakRole("ROLE_FOO")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        store.saveAccountInfo(account);
    }

    @Test
    public void testLogout() throws Exception {
        MockHttpSession session = ((MockHttpSession) (request.getSession(true)));
        Assert.assertFalse(session.isInvalid());
        store.logout();
        Assert.assertTrue(session.isInvalid());
    }
}

