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


import HttpStatus.FOUND;
import KeycloakAuthenticationEntryPoint.DEFAULT_LOGIN_URI;
import org.apache.http.HttpHeaders;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static KeycloakAuthenticationEntryPoint.DEFAULT_LOGIN_URI;


/**
 * Keycloak authentication entry point tests.
 */
public class KeycloakAuthenticationEntryPointTest {
    private KeycloakAuthenticationEntryPoint authenticationEntryPoint;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private AdapterDeploymentContext adapterDeploymentContext;

    @Mock
    private KeycloakDeployment keycloakDeployment;

    @Mock
    private RequestMatcher requestMatcher;

    @Test
    public void testCommenceWithRedirect() throws Exception {
        configureBrowserRequest();
        authenticationEntryPoint.commence(request, response, null);
        Assert.assertEquals(FOUND.value(), response.getStatus());
        Assert.assertEquals(DEFAULT_LOGIN_URI, response.getHeader("Location"));
    }

    @Test
    public void testCommenceWithRedirectNotRootContext() throws Exception {
        configureBrowserRequest();
        String contextPath = "/foo";
        request.setContextPath(contextPath);
        authenticationEntryPoint.commence(request, response, null);
        Assert.assertEquals(FOUND.value(), response.getStatus());
        Assert.assertEquals((contextPath + (DEFAULT_LOGIN_URI)), response.getHeader("Location"));
    }

    @Test
    public void testCommenceWithUnauthorizedWithAccept() throws Exception {
        request.addHeader(HttpHeaders.ACCEPT, "application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        authenticationEntryPoint.commence(request, response, null);
        Assert.assertEquals(FOUND.value(), response.getStatus());
        Assert.assertNull(response.getHeader(HttpHeaders.WWW_AUTHENTICATE));
    }

    @Test
    public void testSetLoginUri() throws Exception {
        configureBrowserRequest();
        final String logoutUri = "/foo";
        authenticationEntryPoint.setLoginUri(logoutUri);
        authenticationEntryPoint.commence(request, response, null);
        Assert.assertEquals(FOUND.value(), response.getStatus());
        Assert.assertEquals(logoutUri, response.getHeader("Location"));
    }

    @Test
    public void testCommenceWithCustomRequestMatcher() throws Exception {
        new KeycloakAuthenticationEntryPoint(adapterDeploymentContext, requestMatcher).commence(request, response, null);
        Mockito.verify(requestMatcher).matches(request);
    }
}

