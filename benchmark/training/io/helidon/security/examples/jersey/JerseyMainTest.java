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
package io.helidon.security.examples.jersey;


import io.helidon.common.CollectionsHelper;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Common unit tests for builder, config and programmatic security.
 */
public abstract class JerseyMainTest {
    private static Client client;

    private static Client authFeatureClient;

    @Test
    public void testUnprotected() {
        Response response = JerseyMainTest.client.target(baseUri()).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.containsString("<ANONYMOUS>"));
    }

    @Test
    public void testProtectedOk() {
        testProtected(((baseUri()) + "/protected"), "jack", "password", CollectionsHelper.setOf("user", "admin"), CollectionsHelper.setOf());
        testProtected(((baseUri()) + "/protected"), "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
    }

    @Test
    public void testWrongPwd() {
        // here we call the endpoint
        Response response = callProtected(((baseUri()) + "/protected"), "jack", "somePassword");
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
    }

    @Test
    public void testDenied() {
        testProtectedDenied(((baseUri()) + "/protected"), "john", "password");
    }

    @Test
    public void testOutboundOk() {
        testProtected(((baseUri()) + "/outbound"), "jill", "password", CollectionsHelper.setOf("user"), CollectionsHelper.setOf("admin"));
    }
}

