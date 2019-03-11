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
import java.io.IOException;
import java.lang.reflect.Proxy;
import javax.inject.Inject;
import javax.inject.Provider;
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
public class BindingTest {
    private static Client client;

    private static WebServer server;

    private static WebTarget baseTarget;

    @Test
    public void testBindToJersey() throws IOException {
        Response response = BindingTest.baseTarget.path("/").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat(entity, CoreMatchers.is("hello"));
        // this should fail
        try {
            BindingTest.client.target(("http://localhost:" + (BindingTest.server.port()))).path("/deny").request().get(String.class);
            Assertions.fail("The deny path should have been forbidden by authorization provider");
        } catch (ForbiddenException ignored) {
            // this is expected
        }
    }

    @Test
    public void testContextProvider() {
        Response response = BindingTest.baseTarget.path("/scProvider").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat("Injected Provider<SecurityContext> was null", entity, CoreMatchers.containsString("null=false"));
        MatcherAssert.assertThat("Injected Provider<SecurityContext> returned null", entity, CoreMatchers.containsString("contentNull=false"));
        // this is still a proxy, not sure why
        // assertThat("Injected Provider<SecurityContext> is a proxy", entity, containsString("proxy=false"));
    }

    @Test
    public void testContextInstance() {
        Response response = BindingTest.baseTarget.path("/scInstance").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat("Injected SecurityContext was null", entity, CoreMatchers.containsString("null=false"));
        MatcherAssert.assertThat("Injected SecurityContext was a proxy", entity, CoreMatchers.containsString("proxy=false"));
    }

    @Test
    public void testContextParameter() {
        Response response = BindingTest.baseTarget.path("/scParam").request().get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(200));
        String entity = response.readEntity(String.class);
        MatcherAssert.assertThat("Injected SecurityContext was null", entity, CoreMatchers.containsString("null=false"));
        MatcherAssert.assertThat("Injected SecurityContext was a proxy", entity, CoreMatchers.containsString("proxy=false"));
    }

    @Path("/")
    public static class MyResource {
        @Inject
        private Provider<SecurityContext> scProvider;

        @Context
        private SecurityContext scInstance;

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("scParam")
        public String checkScParam(@Context
        SecurityContext context) {
            if (null == context) {
                return "null=true";
            }
            return "null=false,proxy=" + (Proxy.isProxyClass(context.getClass()));
        }

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("scProvider")
        public String checkScProvider() {
            if (null == (scProvider)) {
                return "null=true";
            }
            SecurityContext context = scProvider.get();
            if (null == context) {
                return "null=false,contentNull=true";
            }
            return "null=false,contentNull=false,proxy=" + (Proxy.isProxyClass(context.getClass()));
        }

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("scInstance")
        public String checkScInstance() {
            if (null == (scInstance)) {
                return "null=true";
            }
            return "null=false,proxy=" + (Proxy.isProxyClass(scInstance.getClass()));
        }

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String getIt() {
            scProvider.get().isUserInRole("test");
            return "hello";
        }

        @GET
        @Path("deny")
        public String denyIt() {
            return "shouldNotGet";
        }
    }
}

