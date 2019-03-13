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
package io.helidon.security.examples.google;


import HttpHeaders.WWW_AUTHENTICATE;
import Response.Status.UNAUTHORIZED;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Google login common unit tests.
 */
public abstract class GoogleMainTest {
    private static Client client;

    @Test
    public void testEndpoint() {
        Response response = GoogleMainTest.client.target((("http://localhost:" + (port())) + "/rest/profile")).request().get();
        MatcherAssert.assertThat(response.getStatusInfo().toEnum(), CoreMatchers.is(UNAUTHORIZED));
        MatcherAssert.assertThat(response.getHeaders().getFirst(WWW_AUTHENTICATE), CoreMatchers.is("Bearer realm=\"helidon\",scope=\"openid profile email\""));
    }
}

