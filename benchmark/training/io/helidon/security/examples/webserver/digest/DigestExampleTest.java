/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security.examples.webserver.digest;


import io.helidon.common.CollectionsHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Abstract class with tests for this example (used by programmatic and config based tests).
 */
public abstract class DigestExampleTest {
    private static Client client;

    private static Client authFeatureClient;

    // now for the tests
    @Test
    public void testPublic() {
        // Must be accessible without authentication
        Response response = DigestExampleTest.client.target(((getServerBase()) + "/public")).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat(entity, CoreMatchers.containsString("<ANONYMOUS>"));
    }

    @Test
    public void testNoRoles() {
        String url = (getServerBase()) + "/noRoles";
        testNotAuthorized(DigestExampleTest.client, url);
        // Must be accessible with authentication - to everybody
        testProtected(url, "jack", "password", CollectionsHelper.setOf("admin", "user"), CollectionsHelper.setOf());
        testProtected(url, "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
        testProtected(url, "john", "password", CollectionsHelper.setOf(), CollectionsHelper.setOf("admin", "user"));
    }

    @Test
    public void testUserRole() {
        String url = (getServerBase()) + "/user";
        testNotAuthorized(DigestExampleTest.client, url);
        // Jack and Jill allowed (user role)
        testProtected(url, "jack", "password", CollectionsHelper.setOf("admin", "user"), CollectionsHelper.setOf());
        testProtected(url, "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
        testProtectedDenied(url, "john", "password");
    }

    @Test
    public void testAdminRole() {
        String url = (getServerBase()) + "/admin";
        testNotAuthorized(DigestExampleTest.client, url);
        // Only jack is allowed - admin role...
        testProtected(url, "jack", "password", CollectionsHelper.setOf("admin", "user"), CollectionsHelper.setOf());
        testProtectedDenied(url, "jill", "password");
        testProtectedDenied(url, "john", "password");
    }

    @Test
    public void testDenyRole() {
        String url = (getServerBase()) + "/deny";
        testNotAuthorized(DigestExampleTest.client, url);
        // nobody has the correct role
        testProtectedDenied(url, "jack", "password");
        testProtectedDenied(url, "jill", "password");
        testProtectedDenied(url, "john", "password");
    }

    @Test
    public void getNoAuthn() {
        String url = (getServerBase()) + "/noAuthn";
        // Must NOT be accessible without authentication
        Response response = DigestExampleTest.client.target(url).request().get();
        // authentication is optional, so we are not challenged, only forbidden, as the role can never be there...
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(403));
        // doesn't matter, we are never challenged
        testProtectedDenied(url, "jack", "password");
        testProtectedDenied(url, "jill", "password");
        testProtectedDenied(url, "john", "password");
    }
}

