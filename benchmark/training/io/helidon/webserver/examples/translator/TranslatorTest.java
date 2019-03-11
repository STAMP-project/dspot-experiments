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
package io.helidon.webserver.examples.translator;


import io.helidon.webserver.WebServer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;


/**
 * The TranslatorTest.
 */
public class TranslatorTest {
    private static WebServer webServerFrontend;

    private static WebServer webServerBackend;

    private static Client client;

    private static WebTarget target;

    @Test
    public void e2e() throws Exception {
        Response response = TranslatorTest.target.path("translator").queryParam("q", "cloud").request().get();
        MatcherAssert.assertThat(("Unexpected response! Status code: " + (response.getStatus())), response.readEntity(String.class), AllOf.allOf(StringContains.containsString("oblak"), StringContains.containsString("nube")));
    }
}

