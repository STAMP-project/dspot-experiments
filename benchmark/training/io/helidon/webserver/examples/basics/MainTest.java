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
package io.helidon.webserver.examples.basics;


import Http.Header.CONTENT_TYPE;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.Routing;
import io.helidon.webserver.testsupport.MediaPublisher;
import io.helidon.webserver.testsupport.TestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MainTest {
    @Test
    public void firstRouting() throws Exception {
        // POST
        TestResponse response = createClient(Main::firstRouting).path("/post-endpoint").post();
        Assertions.assertEquals(201, response.status().code());
        // GET
        response = createClient(Main::firstRouting).path("/get-endpoint").get();
        Assertions.assertEquals(204, response.status().code());
        Assertions.assertEquals("Hello World!", response.asString().get());
    }

    @Test
    public void routingAsFilter() throws Exception {
        // POST
        TestResponse response = createClient(Main::routingAsFilter).path("/post-endpoint").post();
        Assertions.assertEquals(201, response.status().code());
        // GET
        response = createClient(Main::routingAsFilter).path("/get-endpoint").get();
        Assertions.assertEquals(204, response.status().code());
    }

    @Test
    public void parametersAndHeaders() throws Exception {
        TestResponse response = createClient(Main::parametersAndHeaders).path("/context/aaa").queryParameter("bar", "bbb").header("foo", "ccc").get();
        Assertions.assertEquals(200, response.status().code());
        String s = response.asString().get();
        Assertions.assertTrue(s.contains("id: aaa"));
        Assertions.assertTrue(s.contains("bar: bbb"));
        Assertions.assertTrue(s.contains("foo: ccc"));
    }

    @Test
    public void organiseCode() throws Exception {
        // List
        TestResponse response = createClient(Main::organiseCode).path("/catalog-context-path").get();
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("1, 2, 3, 4, 5", response.asString().get());
        // Get by id
        response = createClient(Main::organiseCode).path("/catalog-context-path/aaa").get();
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("Item: aaa", response.asString().get());
    }

    @Test
    public void readContentEntity() throws Exception {
        // foo
        TestResponse response = createClient(Main::readContentEntity).path("/foo").post(MediaPublisher.create(TEXT_PLAIN, "aaa"));
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("aaa", response.asString().get());
        // bar
        response = createClient(Main::readContentEntity).path("/bar").post(MediaPublisher.create(TEXT_PLAIN, "aaa"));
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("aaa", response.asString().get());
    }

    @Test
    public void filterAndProcessEntity() throws Exception {
        TestResponse response = createClient(Main::filterAndProcessEntity).path("/create-record").post(MediaPublisher.create(MediaType.parse("application/name"), "John Smith"));
        Assertions.assertEquals(201, response.status().code());
        Assertions.assertEquals("John Smith", response.asString().get());
        // Unsupported Content-Type
        response = createClient(Main::filterAndProcessEntity).path("/create-record").post(MediaPublisher.create(TEXT_PLAIN, "John Smith"));
        Assertions.assertEquals(500, response.status().code());
    }

    @Test
    public void supports() throws Exception {
        // Jersey
        TestResponse response = createClient(Main::supports).path("/api/hw").get();
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("Hello world!", response.asString().get());
        // Static content
        response = createClient(Main::supports).path("/index.html").get();
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals(TEXT_HTML.toString(), response.headers().first(CONTENT_TYPE).orElse(null));
        // JSON
        response = createClient(Main::supports).path("/hello/Europe").get();
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("{\"message\":\"Hello Europe\"}", response.asString().get());
    }

    @Test
    public void errorHandling() throws Exception {
        // Valid
        TestResponse response = createClient(Main::errorHandling).path("/compute").post(MediaPublisher.create(TEXT_PLAIN, "2"));
        Assertions.assertEquals(200, response.status().code());
        Assertions.assertEquals("100 / 2 = 50", response.asString().get());
        // Zero
        response = createClient(Main::errorHandling).path("/compute").post(MediaPublisher.create(TEXT_PLAIN, "0"));
        Assertions.assertEquals(412, response.status().code());
        // NaN
        response = createClient(Main::errorHandling).path("/compute").post(MediaPublisher.create(TEXT_PLAIN, "aaa"));
        Assertions.assertEquals(400, response.status().code());
    }

    static class TMain extends Main {
        private Routing routing;

        @Override
        protected void startServer(Routing routing) {
            this.routing = routing;
        }
    }
}

