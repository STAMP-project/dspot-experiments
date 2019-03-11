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
package io.helidon.security.providers.httpauth;


import HttpBasicAuthProvider.HEADER_AUTHENTICATION_REQUIRED;
import io.helidon.common.CollectionsHelper;
import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link HttpBasicAuthProvider} and {@link HttpDigestAuthProvider}.
 */
public class HttpAuthProviderConfigTest {
    private static final Client authFeatureClient = ClientBuilder.newClient().register(HttpAuthenticationFeature.universalBuilder().build());

    private static final Client client = ClientBuilder.newClient();

    private static String serverBase;

    private static String digestUri;

    private static String digestOldUri;

    private static WebServer server;

    @Test
    public void basicTestJack() {
        testProtected(HttpAuthProviderConfigTest.serverBase, "jack", "jackIsGreat", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
    }

    @Test
    public void basicTestJill() {
        testProtected(HttpAuthProviderConfigTest.serverBase, "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
    }

    @Test
    public void digestTestJack() {
        testProtected(HttpAuthProviderConfigTest.digestUri, "jack", "jackIsGreat", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
    }

    @Test
    public void digestTestJill() {
        testProtected(HttpAuthProviderConfigTest.digestUri, "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
    }

    @Test
    public void digestOldTestJack() {
        testProtected(HttpAuthProviderConfigTest.digestOldUri, "jack", "jackIsGreat", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
    }

    @Test
    public void digestOldTestJill() {
        testProtected(HttpAuthProviderConfigTest.digestOldUri, "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
    }

    @Test
    public void basicTest401() {
        // here we call the endpoint
        Response response = HttpAuthProviderConfigTest.client.target(HttpAuthProviderConfigTest.serverBase).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
        String authHeader = response.getHeaderString(HEADER_AUTHENTICATION_REQUIRED);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.is("basic realm=\"mic\""));
    }

    @Test
    public void digestTest401() {
        // here we call the endpoint
        Response response = HttpAuthProviderConfigTest.client.target(HttpAuthProviderConfigTest.digestUri).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
        String authHeader = response.getHeaderString(HEADER_AUTHENTICATION_REQUIRED);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.startsWith("digest realm=\"mic\""));
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.containsString("qop="));
    }

    @Test
    public void digestOldTest401() {
        // here we call the endpoint
        Response response = HttpAuthProviderConfigTest.client.target(HttpAuthProviderConfigTest.digestOldUri).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
        String authHeader = response.getHeaderString(HEADER_AUTHENTICATION_REQUIRED);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.startsWith("digest realm=\"mic\""));
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.not(CoreMatchers.containsString("qop=")));
    }
}

