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


import MediaType.TEXT_PLAIN;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Tests specific header expectation from 204 NO CONTENT status code together with {@link HttpURLConnection} based JAX-RS client.
 */
public class Status204Test {
    private WebServer server;

    @Test
    public void callPutAndGet() {
        WebTarget target = javax.ws.rs.client.ClientBuilder.newClient().target(("http://localhost:" + (server.port())));
        Response response = target.request().put(Entity.entity("test call", TEXT_PLAIN));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.is(204));
        String s = target.request().get(String.class);
        MatcherAssert.assertThat(s, CoreMatchers.is("test"));
    }
}

