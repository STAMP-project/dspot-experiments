/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver.examples.jersey;


import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * The Jersey Client based example that tests the {@link HelloWorld} resource
 * that gets served by running {@link WebServerJerseyMain#startServer(ServerConfiguration)}
 *
 * @see HelloWorld
 * @see WebServerJerseyMain
 */
public class HelloWorldTest {
    private static WebServer webServer;

    @Test
    public void testHelloWorld() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(("http://localhost:" + (HelloWorldTest.webServer.port()))).path("jersey/hello").request().get();
            Assertions.assertEquals("Hello World!", response.readEntity(String.class), ("Unexpected response; status: " + (response.getStatus())));
        } finally {
            client.close();
        }
    }
}

