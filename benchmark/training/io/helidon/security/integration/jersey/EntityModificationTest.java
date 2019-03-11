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


import io.helidon.security.annotations.Authenticated;
import io.helidon.webserver.WebServer;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link SecurityFilter} with message modification.
 */
public class EntityModificationTest {
    private static WebServer server;

    private static Client client;

    private static int port;

    @Test
    public void testReplaceEntity() {
        Response response = EntityModificationTest.client.target(("http://localhost:" + (EntityModificationTest.port))).path("/").request().post(Entity.text("Hello"));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat(entity, CoreMatchers.is("Hello Worldie Suffix"));
    }

    @Path("/")
    public static class MyResource {
        @POST
        @Authenticated
        @Produces(MediaType.TEXT_PLAIN)
        @Consumes(MediaType.TEXT_PLAIN)
        public String getIt(String request) {
            return request;
        }
    }
}

