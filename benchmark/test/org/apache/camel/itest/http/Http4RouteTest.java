/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.itest.http;


import Exchange.HTTP_QUERY;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class Http4RouteTest extends CamelTestSupport {
    private int port1;

    private int port2;

    @Test
    public void sendHttpGetRequestTest() {
        String response = template.requestBody(((("http4://localhost:" + (port1)) + "/test?aa=bb&httpClient.socketTimeout=10000&httpClient.connectTimeout=10000") + "&bridgeEndpoint=true&throwExceptionOnFailure=false"), null, String.class);
        assertEquals("Get a wrong response", "aa=bb", response);
        response = template.requestBodyAndHeader("direct:start1", null, HTTP_QUERY, "aa=bb", String.class);
        assertEquals("Get a wrong response", "aa=bb", response);
        response = template.requestBodyAndHeader("direct:start2", null, HTTP_QUERY, "aa=bb", String.class);
        assertEquals("Get a wrong response", "aa=bb&2", response);
    }
}

