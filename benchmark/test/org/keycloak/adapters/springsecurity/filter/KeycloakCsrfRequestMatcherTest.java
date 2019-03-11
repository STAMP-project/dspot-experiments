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


import AdapterConstants.K_LOGOUT;
import AdapterConstants.K_PUSH_NOT_BEFORE;
import AdapterConstants.K_QUERY_BEARER_TOKEN;
import AdapterConstants.K_TEST_AVAILABLE;
import AdapterConstants.K_VERSION;
import HttpMethod.GET;
import HttpMethod.POST;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Keycloak CSRF request matcher tests.
 */
public class KeycloakCsrfRequestMatcherTest {
    private static final String ROOT_CONTEXT_PATH = "";

    private static final String SUB_CONTEXT_PATH = "/foo";

    private KeycloakCsrfRequestMatcher matcher = new KeycloakCsrfRequestMatcher();

    private MockHttpServletRequest request;

    @Test
    public void testMatchesMethodGet() throws Exception {
        request.setMethod(GET.name());
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesMethodPost() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, "some/random/uri");
        Assert.assertTrue(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, "some/random/uri");
        Assert.assertTrue(matcher.matches(request));
    }

    @Test
    public void testMatchesKeycloakLogout() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, K_LOGOUT);
        Assert.assertFalse(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, K_LOGOUT);
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesKeycloakPushNotBefore() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, K_PUSH_NOT_BEFORE);
        Assert.assertFalse(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, K_PUSH_NOT_BEFORE);
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesKeycloakQueryBearerToken() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, K_QUERY_BEARER_TOKEN);
        Assert.assertFalse(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, K_QUERY_BEARER_TOKEN);
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesKeycloakTestAvailable() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, K_TEST_AVAILABLE);
        Assert.assertFalse(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, K_TEST_AVAILABLE);
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesKeycloakVersion() throws Exception {
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.ROOT_CONTEXT_PATH, K_VERSION);
        Assert.assertFalse(matcher.matches(request));
        prepareRequest(POST, KeycloakCsrfRequestMatcherTest.SUB_CONTEXT_PATH, K_VERSION);
        Assert.assertFalse(matcher.matches(request));
    }
}

