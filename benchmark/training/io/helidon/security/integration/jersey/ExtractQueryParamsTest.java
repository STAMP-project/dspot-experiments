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
package io.helidon.security.integration.jersey;


import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test that query params can be sent and resolved as headers for security.
 */
public class ExtractQueryParamsTest {
    private static final String USERNAME = "assdlakdfknkasdfvsadfasf";

    private static Client client;

    private static WebServer server;

    private static WebTarget baseTarget;

    @Test
    public void testBasicHeader() {
        Response response = ExtractQueryParamsTest.baseTarget.path("/test2").request().header("x-user", ExtractQueryParamsTest.USERNAME).get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.containsString(ExtractQueryParamsTest.USERNAME));
    }

    @Test
    public void testBasicQuery() {
        Response response = ExtractQueryParamsTest.baseTarget.path("/test2").queryParam("basicAuth", ExtractQueryParamsTest.USERNAME).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.containsString(ExtractQueryParamsTest.USERNAME));
    }

    @Test
    public void testBasicFails() {
        Response response = ExtractQueryParamsTest.baseTarget.path("/test2").queryParam("wrong", ExtractQueryParamsTest.USERNAME).request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
    }
}

