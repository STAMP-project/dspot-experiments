/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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


import HttpMethod.GET;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class QueryParamPresenceRequestMatcherTest {
    private static final String ROOT_CONTEXT_PATH = "";

    private static final String VALID_PARAMETER = "access_token";

    private QueryParamPresenceRequestMatcher matcher = new QueryParamPresenceRequestMatcher(QueryParamPresenceRequestMatcherTest.VALID_PARAMETER);

    private MockHttpServletRequest request;

    @Test
    public void testDoesNotMatchWithoutQueryParameter() throws Exception {
        prepareRequest(GET, QueryParamPresenceRequestMatcherTest.ROOT_CONTEXT_PATH, "some/random/uri", Collections.EMPTY_MAP);
        Assert.assertFalse(matcher.matches(request));
    }

    @Test
    public void testMatchesWithValidParameter() throws Exception {
        prepareRequest(GET, QueryParamPresenceRequestMatcherTest.ROOT_CONTEXT_PATH, "some/random/uri", Collections.singletonMap(QueryParamPresenceRequestMatcherTest.VALID_PARAMETER, ((Object) ("123"))));
        Assert.assertTrue(matcher.matches(request));
    }

    @Test
    public void testDoesNotMatchWithInvalidParameter() throws Exception {
        prepareRequest(GET, QueryParamPresenceRequestMatcherTest.ROOT_CONTEXT_PATH, "some/random/uri", Collections.singletonMap("some_parameter", ((Object) ("123"))));
        Assert.assertFalse(matcher.matches(request));
    }
}

