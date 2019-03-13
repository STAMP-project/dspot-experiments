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
package org.keycloak.testsuite.authz;


import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AuthorizationAPITest extends AbstractAuthzTest {
    private static final String RESOURCE_SERVER_TEST = "resource-server-test";

    private static final String TEST_CLIENT = "test-client";

    private static final String AUTHZ_CLIENT_CONFIG = "default-keycloak.json";

    private static final String PAIRWISE_RESOURCE_SERVER_TEST = "pairwise-resource-server-test";

    private static final String PAIRWISE_TEST_CLIENT = "test-client-pairwise";

    private static final String PAIRWISE_AUTHZ_CLIENT_CONFIG = "default-keycloak-pairwise.json";

    @Test
    public void testAccessTokenWithUmaAuthorization() {
        testAccessTokenWithUmaAuthorization(AuthorizationAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testAccessTokenWithUmaAuthorizationPairwise() {
        testAccessTokenWithUmaAuthorization(AuthorizationAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testResourceServerAsAudience() throws Exception {
        testResourceServerAsAudience(AuthorizationAPITest.TEST_CLIENT, AuthorizationAPITest.RESOURCE_SERVER_TEST, AuthorizationAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testResourceServerAsAudienceWithPairwiseClient() throws Exception {
        testResourceServerAsAudience(AuthorizationAPITest.PAIRWISE_TEST_CLIENT, AuthorizationAPITest.RESOURCE_SERVER_TEST, AuthorizationAPITest.AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testPairwiseResourceServerAsAudience() throws Exception {
        testResourceServerAsAudience(AuthorizationAPITest.TEST_CLIENT, AuthorizationAPITest.PAIRWISE_RESOURCE_SERVER_TEST, AuthorizationAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }

    @Test
    public void testPairwiseResourceServerAsAudienceWithPairwiseClient() throws Exception {
        testResourceServerAsAudience(AuthorizationAPITest.PAIRWISE_TEST_CLIENT, AuthorizationAPITest.PAIRWISE_RESOURCE_SERVER_TEST, AuthorizationAPITest.PAIRWISE_AUTHZ_CLIENT_CONFIG);
    }
}

