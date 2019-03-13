/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Http.Status.MOVED_PERMANENTLY_301;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;


/**
 * The MultiPortTest.
 */
public class MultiPortTest {
    private static final Logger LOGGER = Logger.getLogger(MultiPortTest.class.getName());

    private static Client client;

    private Handler commonHandler;

    private SSLContext ssl;

    private WebServer webServer;

    @Test
    public void programmaticNoCompound() throws Exception {
        WebServer webServer8080 = WebServer.create(ServerConfiguration.builder().build(), Routing.builder().get("/", commonHandler).get("/variable", ( req, res) -> res.send("Variable 8080")));
        WebServer webServer8443 = WebServer.create(ServerConfiguration.builder().ssl(ssl), Routing.builder().get("/", commonHandler).get("/variable", ( req, res) -> res.send("Variable 8443")));
        try {
            webServer8080.start().toCompletableFuture().join();
            webServer8443.start().toCompletableFuture().join();
            System.out.println(("Webserver started on port: " + (webServer8080.port())));
            System.out.println(("Webserver started on port: " + (webServer8443.port())));
            assertResponse("https", webServer8443.port(), "/", Is.is("Root! 1"));
            assertResponse("http", webServer8080.port(), "/", Is.is("Root! 2"));
            assertResponse("https", webServer8443.port(), "/", Is.is("Root! 3"));
            assertResponse("http", webServer8080.port(), "/", Is.is("Root! 4"));
            assertResponse("https", webServer8443.port(), "/variable", Is.is("Variable 8443"));
            assertResponse("http", webServer8080.port(), "/variable", Is.is("Variable 8080"));
        } finally {
            try {
                webServer8080.shutdown().toCompletableFuture().join();
            } finally {
                webServer8443.shutdown().toCompletableFuture().join();
            }
        }
    }

    @Test
    public void compositeInlinedWebServer() throws Exception {
        // start all of the servers
        webServer = WebServer.builder(Routing.builder().get("/overridden", ( req, res) -> res.send("Overridden 8443")).get("/", commonHandler).get("/variable", ( req, res) -> res.send("Variable 8443")).build()).config(ServerConfiguration.builder().ssl(ssl).addSocket("plain", SocketConfiguration.builder())).addNamedRouting("plain", Routing.builder().get("/overridden", ( req, res) -> res.send("Overridden 8080")).get("/", commonHandler).get("/variable", ( req, res) -> res.send("Variable 8080"))).build();
        webServer.start().toCompletableFuture().join();
        assertResponse("https", webServer.port(), "/", Is.is("Root! 1"));
        assertResponse("http", webServer.port("plain"), "/", Is.is("Root! 2"));
        assertResponse("https", webServer.port(), "/", Is.is("Root! 3"));
        assertResponse("http", webServer.port("plain"), "/", Is.is("Root! 4"));
        assertResponse("https", webServer.port(), "/variable", Is.is("Variable 8443"));
        assertResponse("http", webServer.port("plain"), "/variable", Is.is("Variable 8080"));
    }

    @Test
    public void compositeSingleRoutingWebServer() throws Exception {
        // start all of the servers
        webServer = WebServer.create(ServerConfiguration.builder().addSocket("secured", SocketConfiguration.builder().ssl(ssl)), Routing.builder().get("/overridden", ( req, res) -> res.send("Overridden BOTH")).get("/", commonHandler).get("/variable", ( req, res) -> res.send("Variable BOTH")));
        webServer.start().toCompletableFuture().join();
        assertResponse("https", webServer.port("secured"), "/", Is.is("Root! 1"));
        assertResponse("http", webServer.port(), "/", Is.is("Root! 2"));
        assertResponse("https", webServer.port("secured"), "/", Is.is("Root! 3"));
        assertResponse("http", webServer.port(), "/", Is.is("Root! 4"));
        assertResponse("https", webServer.port("secured"), "/variable", Is.is("Variable BOTH"));
        assertResponse("http", webServer.port(), "/variable", Is.is("Variable BOTH"));
    }

    @Test
    public void compositeRedirectWebServer() throws Exception {
        // start all of the servers
        webServer = WebServer.builder(Routing.builder().get("/foo", commonHandler)).config(ServerConfiguration.builder().ssl(ssl).addSocket("redirect", SocketConfiguration.builder())).addNamedRouting("redirect", Routing.builder().any(( req, res) -> {
            res.status(Http.Status.MOVED_PERMANENTLY_301).headers().add(Http.Header.LOCATION, String.format("https://%s:%d%s", req.headers().first(Http.Header.HOST).map(( s) -> s.contains(":") ? s.subSequence(0, s.indexOf(":")) : s).orElseThrow(() -> new IllegalStateException("Header 'Host' not found!")), req.webServer().port(), req.uri()));
            res.send();
        })).build();
        webServer.start().toCompletableFuture().join();
        Response response = MultiPortTest.client.target(("http://localhost:" + (webServer.port("redirect")))).path("/foo").request().get();
        MatcherAssert.assertThat(("Unexpected response: " + response), response.getHeaderString("Location"), AllOf.allOf(StringContains.containsString("https://localhost:"), StringContains.containsString("/foo")));
        MatcherAssert.assertThat(("Unexpected response: " + response), response.getStatus(), Is.is(MOVED_PERMANENTLY_301.code()));
        assertResponse("https", webServer.port(), "/foo", Is.is("Root! 1"));
        Response responseRedirected = MultiPortTest.client.target(response.getHeaderString("Location")).request().get();
        MatcherAssert.assertThat(("Unexpected response: " + responseRedirected), responseRedirected.readEntity(String.class), Is.is("Root! 2"));
    }

    @Test
    public void compositeFromConfig() throws Exception {
        Config config = Config.create(ConfigSources.classpath("multiport/application.yaml"));
        webServer = WebServer.builder(Routing.builder().get("/", ( req, res) -> res.send("Plain!"))).config(ServerConfiguration.create(config.get("webserver"))).addNamedRouting("secured", Routing.builder().get("/", ( req, res) -> res.send("Secured!"))).build();
        webServer.start().toCompletableFuture().join();
        assertResponse("http", webServer.port(), "/", Is.is("Plain!"));
        assertResponse("https", webServer.port("secured"), "/", Is.is("Secured!"));
    }
}

