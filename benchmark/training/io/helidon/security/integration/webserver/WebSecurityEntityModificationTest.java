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
package io.helidon.security.integration.webserver;


import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link SecurityHandler} with message modification.
 */
public class WebSecurityEntityModificationTest {
    private static WebServer server;

    private static Client client;

    private static int port;

    @Test
    public void testReplaceEntity() {
        Response response = WebSecurityEntityModificationTest.client.target(("http://localhost:" + (WebSecurityEntityModificationTest.port))).path("/").request().post(Entity.text("Hello"));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat(entity, CoreMatchers.is("Hello Worldie Suffix"));
    }
}

