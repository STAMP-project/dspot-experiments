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


import io.helidon.security.SecurityContext;
import io.helidon.webserver.WebServer;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test that Jersey binding works.
 */
public class PreMatchingBindingTest {
    private static Client client;

    private static WebServer server;

    private static WebTarget baseTarget;

    @Test
    public void testPublic() {
        Response response = PreMatchingBindingTest.baseTarget.path("/").request().get();
        // this must be forbidden, as we use a pre-matching filter
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(401));
    }

    @Test
    public void testAuthenticated() {
        Response response = PreMatchingBindingTest.baseTarget.path("/").request().header("x-user", "jack").get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat(entity, CoreMatchers.is("hello jack"));
    }

    @Test
    public void testDeny() {
        // this should fail
        try {
            // we must authenticate, as we use a pre-matching filter
            PreMatchingBindingTest.baseTarget.path("/deny").request().header("x-user", "jack").get(String.class);
            Assertions.fail("The deny path should have been forbidden by authorization provider");
        } catch (ForbiddenException ignored) {
            // this is expected
        }
    }

    @Path("/")
    public static class MyResource {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String getIt(@Context
        SecurityContext context) {
            return "hello" + (context.user().map(Subject::principal).map(Principal::getName).map(( name) -> " " + name).orElse(""));
        }

        @GET
        @Path("deny")
        public String denyIt() {
            return "shouldNotGet";
        }
    }
}

