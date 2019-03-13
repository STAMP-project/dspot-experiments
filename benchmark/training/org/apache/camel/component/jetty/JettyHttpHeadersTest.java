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
package org.apache.camel.component.jetty;


import Exchange.HTTP_METHOD;
import Exchange.HTTP_PATH;
import Exchange.HTTP_QUERY;
import Exchange.HTTP_URI;
import Exchange.HTTP_URL;
import org.junit.Test;


public class JettyHttpHeadersTest extends BaseJettyTest {
    @Test
    public void testHttpHeaders() throws Exception {
        getMockEndpoint("mock:input").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:input").expectedHeaderReceived("beer", "yes");
        getMockEndpoint("mock:input").expectedHeaderReceived(HTTP_METHOD, "POST");
        getMockEndpoint("mock:input").expectedHeaderReceived(HTTP_URL, (("http://localhost:" + (BaseJettyTest.getPort())) + "/foo"));
        getMockEndpoint("mock:input").expectedHeaderReceived(HTTP_URI, "/foo");
        getMockEndpoint("mock:input").expectedHeaderReceived(HTTP_QUERY, "beer=yes");
        getMockEndpoint("mock:input").expectedHeaderReceived(HTTP_PATH, "");
        String out = template.requestBodyAndHeader("http://localhost:{{port}}/foo?beer=yes", "Hello World", HTTP_METHOD, "POST", String.class);
        assertEquals("Bye World", out);
        assertMockEndpointsSatisfied();
    }
}

