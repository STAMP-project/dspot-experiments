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


import MediaType.TEXT_PLAIN_TYPE;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


/**
 * The test of SSL Netty layer.
 */
public class SslTest {
    private static final Logger LOGGER = Logger.getLogger(SslTest.class.getName());

    private static WebServer webServer;

    private static Client client;

    @Test
    public void testSecuredServerWithJerseyClient() throws Exception {
        Response response = SslTest.client.target(("https://localhost:" + (SslTest.webServer.port()))).request().get();
        doAssert(response);
    }

    @Test
    public void multipleSslRequestsKeepAlive() throws Exception {
        WebTarget target = SslTest.client.target(("https://localhost:" + (SslTest.webServer.port())));
        doAssert(target.request().get());
        doAssert(target.request().get());
    }

    @Test
    public void multipleSslRequestsNonKeepAlive() throws Exception {
        WebTarget target = SslTest.client.target(("https://localhost:" + (SslTest.webServer.port())));
        // send an entity that won't be consumed, as such a new connection will be created by the server
        doAssert(target.request().post(Entity.entity("", TEXT_PLAIN_TYPE)));
        doAssert(target.request().post(Entity.entity("", TEXT_PLAIN_TYPE)));
    }
}

